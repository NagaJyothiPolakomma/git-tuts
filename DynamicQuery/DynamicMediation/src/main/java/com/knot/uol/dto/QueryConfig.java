package com.knot.uol.dto;

//import lombok.Data;
//@Data
public class QueryConfig {
	private String systemName;
	private String schemaName;
	private String parameters;
	private String propertiesFile;

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getParameters() {
		return parameters;
	}

	public void addParameter(String paramName) {
		this.parameters = paramName;
	}

	public String getPropertiesFile() {
		return propertiesFile;
	}

	public void setPropertiesFile(String propertiesFile) {
		this.propertiesFile = propertiesFile;
	}
}
