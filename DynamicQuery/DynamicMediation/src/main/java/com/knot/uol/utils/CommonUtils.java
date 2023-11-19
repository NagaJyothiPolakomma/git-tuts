package com.knot.uol.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CommonUtils {
	
	
	public static JsonArray prepareResultSetJSONObject(ResultSet resultSet)
	{
		JsonArray jsonArray = new JsonArray();
		try {
		// Create a JSON array to hold the result rows
        // Get metadata about the ResultSet to obtain column names
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
		

        while (resultSet.next()) {
            JsonObject jsonObject = new JsonObject();

            // Iterate through columns and add them to the JSON object
            for (int i = 1; i <= columnCount; i++) {
            	
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(i);
                if (columnValue == null) {
                    jsonObject.add(columnName, null);
                } else {
                    jsonObject.addProperty(columnName, columnValue.toString());
                }
//                Optional<Object> optionalColumnValue = Optional.ofNullable(resultSet.getObject(i));
//                optionalColumnValue.ifPresent(columnValue2 ->
//                        jsonObject.addProperty(columnName, columnValue2.toString()));
            
            }

            // Add the JSON object to the array
            jsonArray.add(jsonObject);
        }
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonArray;

	}

}
