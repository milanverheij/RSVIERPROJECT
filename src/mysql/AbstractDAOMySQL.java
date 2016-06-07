package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AbstractDAOMySQL {
	Connection connection;
	PreparedStatement statement;
	ResultSet resultSet;
}
