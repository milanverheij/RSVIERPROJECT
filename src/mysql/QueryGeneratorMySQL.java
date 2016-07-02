package mysql;


import exceptions.GeneriekeFoutmelding;
import interfaces.QueryGenerator;
import logger.DeLogger;

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
        String sqlTableName = className.getSimpleName().toUpperCase();
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
        String sqlTableName = className.getSimpleName().toUpperCase();
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
        return null;
    }

    @Override
    public String buildDeleteStatement(Object object) throws GeneriekeFoutmelding {
        return null;
    }
}
