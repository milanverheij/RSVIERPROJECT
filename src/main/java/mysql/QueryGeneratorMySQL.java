package mysql;

import annotations.Entity;
import exceptions.GeneriekeFoutmelding;
import interfaces.QueryGenerator;
import logger.DeLogger;
import model.Klant;

import java.lang.reflect.Field;

/**
 * Created by Milan_Verheij on 01-07-16.
 *
 * Query Generator voor MySQL
 *
 */
public class QueryGeneratorMySQL extends QueryGenerator {

    @Override
    public String buildInsertStatement(Object object) throws GeneriekeFoutmelding {
        int variableToInsert = 0;
        Class className = object.getClass();

        // Table name via annotations
        if (!object.getClass().isAnnotationPresent(Entity.class)) {
            DeLogger.getLogger().error("Entity annotation niet aanwezig in: " + className);
            throw new GeneriekeFoutmelding("QueryGenerator: Entity annotation niet aanwezig in: " + className);
        }
        String sqlTableName = object.getClass().getAnnotation(Entity.class).value();

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        Field[] declaredFields = className.getDeclaredFields();

        for (Field dcField : declaredFields) {
            try {
                dcField.setAccessible(true);
                if (dcField.get(object) != null) {
                    if (!isPrimitiveZero(dcField.get(object))) {
                        variableToInsert++;

                        if (variableToInsert > 1) {
                            columns.append(", ");
                            values.append(", ");
                            columns.append(dcField.getName());
                        } else
                            columns.append(dcField.getName());

                        if (dcField.get(object) instanceof String) {
                            values.append("\'");
                            values.append(dcField.get(object));
                            values.append("\'");
                        } else
                            values.append(dcField.get(object));
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
                DeLogger.getLogger().error("Fout bij maken insert statement " + "[" + className + "]: " + e.getMessage());
                throw new GeneriekeFoutmelding("Fout bij maken insert statement " + "[" + className + "]: " + e.getMessage());
            }
        }

        if (variableToInsert == 0) {
            DeLogger.getLogger().warn("Geen declared fields, niets te inserten");
            throw new GeneriekeFoutmelding("QueryGeneratorMySQL: Geen declared fields, niets te inserten");
        }

        return "INSERT INTO " + sqlTableName + "(" + columns  + ") " + "values (" + values + ");";
    }

    @Override
    public String buildUpdateStatement(Object object) throws GeneriekeFoutmelding {
        int variableToUpdate = 0;
        Class className = object.getClass();

        // Table name via annotations
        if (!object.getClass().isAnnotationPresent(Entity.class)) {
            DeLogger.getLogger().error("Entity annotation niet aanwezig in: " + className);
            throw new GeneriekeFoutmelding("QueryGenerator: Entity annotation niet aanwezig in: " + className);
        }
        String sqlTableName = object.getClass().getAnnotation(Entity.class).value();

        StringBuilder columnsValues = new StringBuilder();
        Field[] declaredFields = className.getDeclaredFields();


        for (Field dcField : declaredFields) {
            try {
                dcField.setAccessible(true);
                if (dcField.get(object) != null) {
                    if (!isPrimitiveZero(dcField.get(object)) && !isExcluded(dcField)) {
                        variableToUpdate++;

                        if (variableToUpdate > 1) {
                            columnsValues.append(", " + dcField.getName());
                        } else
                            columnsValues.append(dcField.getName());

                        if (dcField.get(object) instanceof String) {
                            columnsValues.append(" = \'");
                            columnsValues.append(dcField.get(object));
                            columnsValues.append("\'");
                        } else
                            columnsValues.append(" = " + dcField.get(object));
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
                DeLogger.getLogger().error("Fout bij maken update(MYSQL) statement " + "[" + className + "]: " + e.getMessage());
                throw new GeneriekeFoutmelding("Fout bij maken update(MYSQL) statement " + "[" + className + "]: " + e.getMessage());
            }
        }

        if (variableToUpdate == 0) {
            DeLogger.getLogger().warn("Geen declared fields, niets up te daten");
            throw new GeneriekeFoutmelding("QueryGeneratorMySQL: Geen declared fields, niets up te daten");
        }

        return "UPDATE " + sqlTableName + " SET " + columnsValues + " WHERE";
    }

    @Override
    public String buildSelectStatement(Object object) throws GeneriekeFoutmelding {
        Class className = object.getClass();

        if (!object.getClass().isAnnotationPresent(Entity.class)) {
            DeLogger.getLogger().error("Entity annotation niet aanwezig in: " + className);
            throw new GeneriekeFoutmelding("QueryGenerator: Entity annotation niet aanwezig in: " + className);
        }

        String sqlTableName = object.getClass().getAnnotation(Entity.class).value();

        // Als er een klant-object wordt meegegeven met een ingevuld Adres-object wordt geacht
        // dat er gezocht wordt op een klant op basis van bepaalde adresgegevens. Dit vereist
        // een dermate ander SQL-statement dat er een afzonderlijk stuk code nodig is.
        if (object instanceof Klant && ((Klant)object).getAdresGegevens() != null ) {

            // Haal de adreskolommen op waar naar gezocht moet worden
            StringBuilder columnsValues = buildInsertColumnValues(((Klant) object).getAdresGegevens());

            // Als er een leeg adres wordt meegegeven, dan worden alle klanten geselecteerd
            if (columnsValues.length() == 0)
                return "SELECT * FROM klant;";

            return "SELECT klant.* FROM klantHeeftAdres, adres, klant WHERE " +
                    columnsValues +
                    " AND " +
                    "klantHeeftAdres.adresIdAdres = adres.adresId AND " +
                    "klantHeeftAdres.klantIdKlant = klant.klantId " +
                    "GROUP BY klantId " +
                    "ORDER BY klantId;";
        }

        // Haal de kolommen op waar naar gezocht moet worden
        StringBuilder columnsValues = buildInsertColumnValues(object);

        if (columnsValues.length() == 0)
            return "SELECT * FROM " + sqlTableName + ";";
        else
            return "SELECT * FROM " + sqlTableName + " WHERE " + columnsValues + ";";
    }

    @Override
    public String buildDeleteStatement(Object object) throws GeneriekeFoutmelding {
        return null;
    }

    /**
     * Bouwt de columnvalue string op voor de insert-statement. Er zijn diverse soorten insert
     * statements (afhankelijk van het ingegeven object) welke deze methode delen.
     *
     * @param object Het object waar de statement van gemaakt moet worden.
     * @return SQL-string
     * @throws GeneriekeFoutmelding foutmelding met gegevens.
     */
    private StringBuilder buildInsertColumnValues(Object object) throws GeneriekeFoutmelding {
        int variableToUpdate = 0;
        Class className = object.getClass();
        StringBuilder columnsValues = new StringBuilder();
        Field[] declaredFields = className.getDeclaredFields();

        for (Field dcField : declaredFields) {
            try {
                dcField.setAccessible(true);
                if (dcField.get(object) != null) {
                    if (!isPrimitiveZero(dcField.get(object))) {
                        variableToUpdate++;

                        if (variableToUpdate > 1) {
                            columnsValues.append(" AND " + dcField.getName());
                        } else
                            columnsValues.append(dcField.getName());

                        if (dcField.get(object) instanceof String) {
                            columnsValues.append(" LIKE \'");
                            columnsValues.append(dcField.get(object));
                            columnsValues.append("\'");
                        } else
                            columnsValues.append(" LIKE " + dcField.get(object));
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
                DeLogger.getLogger().error("Fout bij maken select(MYSQL) statement " + "[" + className + "]: " + e.getMessage());
                throw new GeneriekeFoutmelding("Fout bij maken select(MYSQL) statement " + "[" + className + "]: " + e.getMessage());
            }
        }
    return columnsValues;
    }
}
