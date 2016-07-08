package mysql;

import interfaces.QueryGenerator;
import interfaces.VerkrijgConnectie;

import java.sql.PreparedStatement;

public abstract class AbstractDAOMySQL {
	PreparedStatement statement;
	static VerkrijgConnectie connPool;
	static QueryGenerator queryGenerator = new QueryGeneratorMySQL();

	public static void setConnPool(VerkrijgConnectie connPool) {
		AbstractDAOMySQL.connPool = connPool;
	}
}
