package database.daos.firebird;

import database.factories.interfaces.VerkrijgConnectie;
import database.daos.queryGenerator.QueryGenerator;
import database.daos.queryGenerator.QueryGeneratorFireBird;


public abstract class AbstractDAOFireBird {
	static VerkrijgConnectie connPool;
	static QueryGenerator queryGenerator = new QueryGeneratorFireBird();

	public static void setConnPool(VerkrijgConnectie connPool) {
		AbstractDAOFireBird.connPool = connPool;
	}
}
