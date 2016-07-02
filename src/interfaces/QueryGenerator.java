package interfaces;

import exceptions.GeneriekeFoutmelding;

import java.lang.reflect.Field;

/**
 * Created by Milan_Verheij on 01-07-16.
 *
 * Algemene interface voor querygenerators van de diverse databasevormen
 */

public abstract class QueryGenerator {
    public abstract String buildInsertStatement(Object object) throws GeneriekeFoutmelding;
    public abstract String buildUpdateStatement(Object object) throws GeneriekeFoutmelding;
    public abstract String buildSelectStatement(Object object) throws GeneriekeFoutmelding;
    public abstract String buildDeleteStatement(Object object) throws GeneriekeFoutmelding;

    public boolean isPrimitiveZero(Object object) {
        boolean isPrimitiveZero = false;

        if (object instanceof Long) {
            if ((Long) object == 0) {
                isPrimitiveZero = true;
            }
        } else if (object instanceof Integer) {
            if ((Integer) object == 0) {
                isPrimitiveZero = true;
            }
        } else if (object instanceof Float) {
            if ((Float) object == 0.0) {
                isPrimitiveZero = true;
            }
        } else if (object instanceof Double) {
            if ((Double) object == 0.0) {
                isPrimitiveZero = true;
            }
        }
        return isPrimitiveZero;
    }

    public boolean isExcluded(Field declaredField) {
        boolean isExcluded = false;
        String[] excludedColumns = {"klant_id", "adres_id", "artikel_id", "bestelling_id", "prijs_id"};

        for (String excludedColumn : excludedColumns) {
            if (declaredField.getType().equals(long.class) && declaredField.getName().equals(excludedColumn))
                isExcluded = true;
        }
        return isExcluded;
    }
}
