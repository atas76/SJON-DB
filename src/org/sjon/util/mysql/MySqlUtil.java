package org.sjon.util.mysql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.sjon.db.SjonRecord;
import org.sjon.db.SjonTable;
import org.sjon.parser.SjonParsingException;
import org.sjon.parser.SjonScanningException;

public class MySqlUtil {
	
	private final static String DEFAULT_DATA_SOURCE = "./resources/mysql/pscms"; 

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		File dsDirectory = new File(DEFAULT_DATA_SOURCE);
		
		Properties configProperties = new Properties();
		
		/**
		 * VALIDATION
		 */
		if (dsDirectory.isDirectory()) {
			System.out.println("Data source directory found");
		} else {
			System.out.println("Data source directory does not exist");
			System.exit(1);
		}
		
		File [] dsFiles = dsDirectory.listFiles();
		
		/**
		 * VALIDATION
		 */
		File configFile = null;
		for (File file:dsFiles) {
			if (file.getName().equals("config.properties")) {
				System.out.println("Config properties file found");
				configFile = file;
				break;
			} 
		}
		
		String dbAddress = null;
		String sjonAddress = null;
		// String dbUser = null;
		// String dbPassword = null;
		
		try {
			
			Reader propertiesReader = new BufferedReader(new FileReader(configFile));
			
			configProperties.load(propertiesReader);
			
			dbAddress = configProperties.getProperty("dbAddress");
			sjonAddress = configProperties.getProperty("sjonAddress");
			// dbUser = configProperties.getProperty("dbUser");
			// dbPassword = configProperties.getProperty("dbPassword");
			
		} catch (FileNotFoundException fnfex) {
			System.out.println("Config properties file not found");
			System.exit(2);
		} catch (IOException ioex) {
			System.out.println("Error config loading properties");
			System.exit(3);
		}
		
		/**
		 * VALIDATION
		 */
		System.out.println("Database address: " + dbAddress);
		System.out.println("Sjon address: " + sjonAddress);
		// System.out.println("Database user: " + dbUser);
		// System.out.println("Database password: " + dbPassword);
		
		Connection conn = null;
		
		try {
		
			conn = DriverManager.getConnection(dbAddress);
			
		} catch (SQLException sqlEx) {
			System.out.println("Connection failed");
			sqlEx.printStackTrace();
			System.exit(4);
		}
		
		/**
		 * VALIDATION
		 */
		System.out.println("Connected to database");
		
		// For each sjon file in the sjonAddress folder
		
		File sjonDirectory = new File(sjonAddress);
		File [] sjonFiles = sjonDirectory.listFiles();
		
		Map<String, SjonTable> sjonTables = new HashMap<String, SjonTable>();
		
		try {
			for (File sjonFile:sjonFiles) {
				if (!sjonFile.isDirectory() && sjonFile.getName().endsWith(".sjon")) { // We exclude nested directories
					
					String tableName = sjonFile.getName().substring(0, sjonFile.getName().length() - 5);
					
					sjonTables.put(tableName, new SjonTable(sjonFile.getPath()));
				}
			}
		} catch (IOException ioex) {
			System.out.println("Error reading SJON files");
			System.exit(5);
		}
		
		/**
		 * VALIDATION
		 */
		System.out.println("Number of SJON tables retrieved: " + sjonTables.size());
		
		try {
			
			for (Map.Entry<String, SjonTable> sjonTable:sjonTables.entrySet()) {
				
				String tableName = sjonTable.getKey();
				
				// For each sjon record in the file
				for (SjonRecord sjonRecord:sjonTable.getValue().getData()) {
					
					// construct an insert or update query for the specific record
					
					Collection<String> tableFields = sjonRecord.getFieldNames();
					Map<String, String> tuple = new HashMap<String, String>();
					
					List<String> orderedTableFields = new ArrayList<String>();
					
					for (String tableField:tableFields) {
						orderedTableFields.add(tableField.trim());
						tuple.put(tableField.trim(), sjonRecord.getValue(tableField));
					}
					
					// Execute a reconnaissance query based on the fields for error checking, but most importantly for getting the types of fields
					
					Statement typeStmt = conn.createStatement();
					
					String typeQuery = constructTypeQuery(tableName, orderedTableFields); 
					ResultSet typeRS = typeStmt.executeQuery(typeQuery);
					ResultSetMetaData rsmd = typeRS.getMetaData();
					
					Map<String, Integer> columnTypes = new HashMap<String, Integer>();
					
					for (int i = 0; i < rsmd.getColumnCount(); i++) {
						int columnType = rsmd.getColumnType(i + 1);
						columnTypes.put(orderedTableFields.get(i), columnType);
					}
					
					// We will insert rows one by one, as sjon != relational (we can't guarantee the same columns for each record).
					
					Statement insUpdStmt = conn.createStatement();
					
					String insUpdQuery = constructInsertUpdateQuery(tableName, tuple, columnTypes);
					
					insUpdStmt.execute(insUpdQuery);
					
					System.out.println("INSERT QUERY: " + insUpdQuery + ", Update count: " + insUpdStmt.getUpdateCount());
					
					insUpdStmt.close();
					
					// INSERT INTO table (a,b) VALUES (1,2), (2,3), (3,4);
					// INSERT INTO table (id, name, age) VALUES(1, "A", 19) ON DUPLICATE KEY UPDATE name=VALUES(name), age=VALUES(age)
				}
			}
		} catch (SjonScanningException sjonScanEx) {
			System.out.println("Syntax error in SJON record: " + sjonScanEx.getMessage());
			System.exit(6);
		} catch (SjonParsingException sjonParseEx) {
			System.out.println("Invalid SJON record: " + sjonParseEx.getMessage());
			System.exit(7);
		} catch (SQLException sqlEx) {
			System.out.println("Database error after connection");
			sqlEx.printStackTrace();
			System.exit(8);
		}
		
		try {
		
			conn.close();
			
		} catch (SQLException sqlEx) {
			conn = null;
		}
	}
	
	protected static String constructTypeQuery(String tableName, final List<String> tableFields) {
		
		StringBuilder columnsCommalist = new StringBuilder();
		
		for (String tableField:tableFields) {
			columnsCommalist.append(tableField);
			columnsCommalist.append(",");
		}
		
		columnsCommalist.deleteCharAt(columnsCommalist.length() - 1);
		
		return "SELECT " + columnsCommalist.toString() + " FROM " + tableName + " WHERE 1=2";
	}
	
	protected static String constructInsertUpdateQuery(String tableName, Map<String, String> tuple, Map<String, Integer> fieldTypes) {
		
		// We need to find the type of the corresponding field and convert it to that type for the query
		// INSERT INTO table (id, name, age) VALUES(1, "A", 19) ON DUPLICATE KEY UPDATE name=VALUES(name), age=VALUES(age)
		
		StringBuilder query = new StringBuilder();
		
		List<String> fieldNames = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		
		query.append("INSERT INTO ");
		query.append(tableName + "(");
		
		for (Map.Entry<String, String> column:tuple.entrySet()) {
			fieldNames.add(column.getKey());
			values.add(sqlify(column.getValue(), fieldTypes.get(column.getKey())));
		}
		
		StringBuilder fieldNamesCommalist = new StringBuilder();
		
		for (String fieldName:fieldNames) {
			fieldNamesCommalist.append(fieldName);
			fieldNamesCommalist.append(",");
		}
		
		query.append(fieldNamesCommalist.substring(0, fieldNamesCommalist.length() - 1));
		query.append(") ");
		
		query.append("VALUES(");
		
		StringBuilder valuesCommalist = new StringBuilder();
		
		for (String value:values) {
			valuesCommalist.append(value);
			valuesCommalist.append(",");
		}
		
		String valuesCommalistStr = valuesCommalist.toString();
		
		query.append(valuesCommalistStr.substring(0, valuesCommalist.length() - 1));
		query.append(") ");
		
		query.append("ON DUPLICATE KEY UPDATE ");
		
		StringBuilder insertUpdateBoilerplate = new StringBuilder();
		
		for (String fieldName:fieldNames) {
			insertUpdateBoilerplate.append(fieldName + "=VALUES(" + fieldName + "),");
		}
		
		query.append(insertUpdateBoilerplate.substring(0, insertUpdateBoilerplate.length() - 1));
		
		return query.toString();
	}
	
	private static String sqlify(String value, Integer sqlTypeCode) {
		
		// System.out.println("Value: " + value);
		// System.out.println("Type: " + sqlTypeCode);
		
		if (sqlTypeCode == java.sql.Types.VARCHAR) { // Excuse the magic number; that's for enumerations
			return "\"" + value + "\"";
		} else {
			return value;
		}
	}
}
