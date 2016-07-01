package interfaces;

import exceptions.GeneriekeFoutmelding;

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
}
