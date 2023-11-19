package com.knot.uol.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.knot.uol.dto.QueryConfig;


public class JDBCConnectionUtil {

	// Method to connect to the appropriate database based on system name and schema
	// name
	public static Connection connectToDatabase(String systemName, String schemaName, Properties properties) {
		// Implement database connection logic based on the system name
		// For example, use different JDBC drivers and connection parameters for MySQL
		// and Oracle
		Connection connectionObj = null;
		try {
			String dbUrl = null, dbUser = null, dbPassword = null;
			if (properties != null) {
				dbUrl = properties.getProperty(systemName + ".db.url"); // Change this to your database URL
				dbUser = properties.getProperty(systemName + ".db.username");
				dbPassword = properties.getProperty(systemName + ".db.password");

			}
			String mysqlUrl = dbUrl + schemaName + "?user=" + dbUser + "&password=" + dbPassword;

			System.out.println("db url name::" + mysqlUrl);
			// connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			connectionObj = DriverManager.getConnection(mysqlUrl);

		} catch (Exception e) {
			// Handle database connection errors
			e.printStackTrace();
			System.out.println(e);
		}

		System.out.println("connection ==>" + connectionObj);
		return connectionObj;
	}
	
	// Method to fetch query configuration from the database based on query name
	public static QueryConfig getQueryConfigFromDatabase(String queryName, Properties properties) throws SQLException, ClassNotFoundException {
		// Load the properties file containing dynamic queries
		Connection connection=null;
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		String dbUrl = null, dbUser = null, dbPassword = null, queryConfigPath = null;// Change this to your database
																						// URL
		if (properties != null) {
			dbUrl = properties.getProperty("apiregistry.db.url"); // Change this to your database URL
			dbUser = properties.getProperty("apiregistry.db.username");
			dbPassword = properties.getProperty("apiregistry.db.password");
			queryConfigPath = properties.getProperty("apiregistry.db.queries.basic.path");
			}

		QueryConfig queryConfig = null;
		try {
			// Establish a database connection 
			Class.forName("com.mysql.jdbc.Driver");
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			 connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			System.out.println("query name::" + queryName);
			// Prepare and execute the SQL query to retrieve query configuration based on
			String sql = "SELECT * FROM vyshu.dynamic_queries WHERE query_name = ?";
		    preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, queryName);
		    resultSet = preparedStatement.executeQuery();
			// Populate the query configuration
			queryConfig = new QueryConfig();
			while (resultSet.next()) {

				queryConfig.setSystemName(resultSet.getString("db_system"));
				queryConfig.setSchemaName(resultSet.getString("schema_name"));
				queryConfig.addParameter(resultSet.getString("parameters"));
				queryConfig.setPropertiesFile(queryConfigPath + resultSet.getString("propertiesFile"));

			}
			System.out.println("queryConfig obj" + queryConfig.getPropertiesFile());
			
		} catch (SQLException e) {
			// Handle database-related exceptions
			e.printStackTrace();			
		}finally {
			// Close the database resources
			resultSet.close();
			preparedStatement.close();
			connection.close();
		}
		return queryConfig;
	}

	
		}


