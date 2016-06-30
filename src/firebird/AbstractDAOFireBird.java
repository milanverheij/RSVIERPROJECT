package firebird;

import interfaces.VerkrijgConnectie;
import java.sql.PreparedStatement;

public abstract class AbstractDAOFireBird {
	PreparedStatement statement;
	static VerkrijgConnectie connPool;

	public static void setConnPool(VerkrijgConnectie connPool) {
		AbstractDAOFireBird.connPool = connPool;
	}
}
