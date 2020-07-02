package com.piotrek.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlUtil {

	private static final String PASSWORD = "piotrek96k";

	private static final String USER = "postgres";

	private static final String SQL_URL = "jdbc:postgresql://localhost:5432/JEDI_ORDERS";

	private Connection connection;

	private Statement statement;

	public SqlUtil() throws SQLException {
		connection = DriverManager.getConnection(SQL_URL, USER, PASSWORD);
		statement = connection.createStatement();
	}

	public ResultSet executeQuery(String query) throws SQLException {
		statement.execute(query);
		ResultSet data = statement.executeQuery(query);
		return data;
	}

	public boolean execute(String query) throws SQLException {
		boolean result = statement.execute(query);
		return result;
	}

	public void close() throws SQLException {
		statement.close();
		connection.close();
	}

}
