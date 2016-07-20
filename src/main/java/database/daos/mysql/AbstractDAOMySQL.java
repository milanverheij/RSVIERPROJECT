package database.daos.mysql;

import database.daos.queryGenerator.QueryGenerator;
import database.daos.queryGenerator.QueryGeneratorMySQL;
import database.factories.interfaces.VerkrijgConnectie;

public abstract class AbstractDAOMySQL {
	static VerkrijgConnectie connPool;
	static QueryGenerator queryGenerator = new QueryGeneratorMySQL();

	public static void setConnPool(VerkrijgConnectie connPool) {
		AbstractDAOMySQL.connPool = connPool;
	}
}
