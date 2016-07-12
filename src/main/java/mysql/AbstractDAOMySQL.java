package mysql;

import interfaces.QueryGenerator;
import interfaces.VerkrijgConnectie;

public abstract class AbstractDAOMySQL {
	static VerkrijgConnectie connPool;
	static QueryGenerator queryGenerator = new QueryGeneratorMySQL();

	public static void setConnPool(VerkrijgConnectie connPool) {
		AbstractDAOMySQL.connPool = connPool;
	}
}
