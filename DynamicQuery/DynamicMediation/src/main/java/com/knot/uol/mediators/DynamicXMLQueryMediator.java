package com.knot.uol.mediators;

import org.apache.synapse.MessageContext;

import org.apache.synapse.Mediator;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.aspectj.lang.reflect.CatchClauseSignature;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.knot.uol.dto.QueryConfig;
import com.knot.uol.utils.CommonUtils;
import com.knot.uol.utils.JDBCConnectionUtil;
import com.knot.uol.utils.PropertiesUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DynamicXMLQueryMediator extends AbstractMediator {
	// private static final Logger logger =
	// Logger.getLogger(DynamicXMLQueryMediator.class);
	//private static String apiRegistryConfigs= "E:\\Suresh_code_for_dynamic_Query\\api-registry-configs.properties";

	@Override
	public boolean mediate(MessageContext messageContext) {

		try {
			org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();

			//apiRegistryConfigs = (String) messageContext.getProperty("apiregistry.config.path"); 
		        String apiRegistryConfigs = (String) messageContext.getProperty("apiregistry"); 
		//	String apiRegistryConfigs = (String) messageContext.getProperty("query"); 
			String inputPayload = (String) messageContext.getProperty("payload");//payload is a property name we are using this to store the postman data
			Properties properties = PropertiesUtil.propertiesFileRead(apiRegistryConfigs);

			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(inputPayload);
			String queryName = jsonElement.getAsJsonObject().get("queryName").getAsString();
			// Fetch dynamic query configuration from the database based on query name
			QueryConfig queryConfig = JDBCConnectionUtil.getQueryConfigFromDatabase(queryName, properties);
			
			if (queryConfig == null) {
				// Handle error: Query not found in the database
				System.out.println("queryconfig null");

			}

			// Load the properties file containing dynamic queries
			Properties readQueryProperties = PropertiesUtil.propertiesFileRead(queryConfig.getPropertiesFile());

			// Get the dynamic query from the properties file based on query name
			String dynamicQuery = readQueryProperties.getProperty(queryName);

			if (dynamicQuery == null) {
				// Handle error: Query not found in the properties file
				System.out.println("dynamicQuery null");
			}

			if (queryConfig.getParameters() != null) {
				List<String> inputParams = Arrays.asList(queryConfig.getParameters().split(","));

				// Replace placeholders in the query with request values

				// Check if the parsed element is an object
				if (jsonElement.isJsonObject()) {
					//Changed
					if (jsonElement.getAsJsonObject().get("parameters").equals(null)) {
						
							System.out.println("There are No Parameters");
						}
					else {
						JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject("parameters");
						for (String keys : inputParams) {
							// Access key-value pairs
							System.out.println(jsonObject + "/" + jsonObject.get(keys));
							if (jsonObject.get(keys) != null) {
								String dynamicVal = jsonObject.get(keys).getAsString();
								System.out.println(keys + "dynamicVal : " + dynamicVal);
								dynamicQuery = dynamicQuery.replace("{" + keys + "}", dynamicVal);
							}
						}
					}
				} else {
					System.out.println("Invalid JSON: Not an object.");
				}

				System.out.println("dynamic query data::" + dynamicQuery);
			}
			// Use the constructed dynamic query as needed
			// For example, you can set it as a property in the message context
			messageContext.setProperty("dynamicQuery", dynamicQuery);

			// Connect to the database based on system name and schema name
			String systemName = queryConfig.getSystemName();
			String schemaName = queryConfig.getSchemaName();

			// Execute the dynamic query using the database connection

			Connection conn = JDBCConnectionUtil.connectToDatabase(systemName, schemaName, properties);
			System.out.println("==============" + conn);
			if (conn == null) {
				// Handle error: Unable to connect to the database
				System.out.println("is connection null");
			}
			System.out.println("===>Dynmc query::" + dynamicQuery);
			PreparedStatement preparedStatement = conn.prepareStatement(dynamicQuery);
			ResultSet resultSet = preparedStatement.executeQuery();

			JsonArray jsonarray = CommonUtils.prepareResultSetJSONObject(resultSet);
			System.out.println("jsonarray==>" + jsonarray);
			messageContext.setProperty("response", jsonarray);
			// Close the database resources
			resultSet.close();
			preparedStatement.close();
			conn.close();

		} catch (Exception e) {
			// Handle database-related exceptions
			System.out.println("Exception in "+ e);
			e.printStackTrace();
			// Handle error: Database query execution error
			System.out.println("connection was null");
		}
		return true;

	}

}
