package firebird;

import interfaces.QueryGenerator;
import interfaces.VerkrijgConnectie;

public abstract class AbstractDAOFireBird {
	static VerkrijgConnectie connPool;
	static QueryGenerator queryGenerator = new QueryGeneratorFireBird();

	public static void setConnPool(VerkrijgConnectie connPool) {
		AbstractDAOFireBird.connPool = connPool;
	}
}
