package mysql;

import interfaces.VerkrijgConnectie;

import java.sql.PreparedStatement;

public abstract class AbstractDAOMySQL {
	PreparedStatement statement;
	static VerkrijgConnectie connPool;

	public static void setConnPool(VerkrijgConnectie connPool) {
		AbstractDAOMySQL.connPool = connPool;
	}
}
