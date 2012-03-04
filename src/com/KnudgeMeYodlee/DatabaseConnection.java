package com.KnudgeMeYodlee;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.mysql.jdbc.PreparedStatement;

public class DatabaseConnection {

	Connection con;

	public DatabaseConnection() {
		try {
			// TODO read from properties file
			Class.forName("com.mysql.jdbc.Driver");
			String DBHost = "jdbc:mysql://192.168.1.135/KnudgeMe";
			con = DriverManager.getConnection(DBHost, "mysql",
					"dxAT76SAC8CmNGKA");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// TODO Do logging instead of returning anything.
	public int insertSingleRow(String tableName,
			List<TableValue> columnValuePairs) {
		try {

			StringBuilder columnNames = new StringBuilder();
			StringBuilder valueNames = new StringBuilder();

			// Remove empties first
			Iterator<TableValue> it = columnValuePairs.iterator();
			while (it.hasNext()) {
				TableValue value = it.next();
				if (!value.isValueSet()) {
					it.remove();
				}
			}

			for (TableValue column : columnValuePairs) {
				if (columnNames.length() != 0) {
					columnNames.append(",");
					valueNames.append(",");
				}
				columnNames.append(column.columnName);
				valueNames.append("?");
			}

			String sql = "INSERT INTO " + tableName + " (" + columnNames
					+ ") VALUES (" + valueNames + ")";
			System.out.println(sql);
			PreparedStatement statement = (PreparedStatement) con
					.prepareStatement(sql);

			int index = 1;
			for (TableValue column : columnValuePairs) {
				setStatementHelper(index, statement, column);
				index++;
			}

			int resultingRowCount = statement.executeUpdate();
			if (resultingRowCount == 0) {
				System.out.println("Error: Row not inserted into " + tableName);
				return -1;
			}
			statement.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return -1;
		}
		return 0;
	}

	private void setStatementHelper(int index, PreparedStatement statement,
			TableValue type) {
		try {
			switch (type.domainType) {
			case StringType: {
				statement.setString(index, type.stringType);
				break;
			}
			case DecimalType: {
				statement.setBigDecimal(index, type.decimalType);
				break;
			}
			case BoolType: {
				statement.setBoolean(index, type.boolType);
				break;
			}
			case TinyIntType: {
				statement.setByte(index, type.tinyIntType);
				break;
			}
			case SmallIntType: {
				statement.setShort(index, type.smallIntType);
				break;
			}
			case IntType: {
				statement.setInt(index, type.intType);
				break;
			}
			case BigIntType: {
				statement.setLong(index, type.bigIntType);
				break;
			}
			case RealType: {
				statement.setFloat(index, type.realType);
				break;
			}
			case DoubleType: {
				statement.setDouble(index, type.doubleType);
				break;
			}
			case BinaryType: {
				statement.setBytes(index, ArrayUtils
						.toPrimitive(type.binaryType.toArray(new Byte[0])));
				break;
			}
			case ByteArrayType: {
				statement.setBinaryStream(index, type.byteArrayType);
				break;
			}
			case DateType: {
				statement.setDate(index, type.dateType);
				break;
			}
			case TimeType: {
				statement.setTime(index, type.timeType);
				break;
			}
			case TimestampType: {
				statement.setTimestamp(index, type.timestampType);
				break;
			}
			case ClobType: {
				statement.setClob(index, type.clobType);
				break;
			}
			case BlobType: {
				statement.setBlob(index, type.blobType);
				break;
			}
			case ArrayType: {
				statement.setArray(index, type.arrayType);
				break;
			}
			case ObjectType: {
				statement.setObject(index, type.objectType);
			}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private TableValue getStatementHelper(String columnName,
			ResultSet resultSet, TableValue type) {
		try {
			TableValue result;
			switch (type.domainType) {
			case StringType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getString(columnName));
				break;
			}
			case DecimalType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getBigDecimal(columnName));
				break;
			}
			case BoolType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getBoolean(columnName));
				break;
			}
			case TinyIntType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getByte(columnName));
				break;
			}
			case SmallIntType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getShort(columnName));
				break;
			}
			case IntType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getInt(columnName));
				break;
			}
			case BigIntType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getLong(columnName));
				break;
			}
			case RealType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getFloat(columnName));
				break;
			}
			case DoubleType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getDouble(columnName));
				break;
			}
			case BinaryType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getBytes(columnName));
				break;
			}
			case ByteArrayType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getObject(columnName));
				break;
			}
			case DateType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getDate(columnName));
				break;
			}
			case TimeType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getTime(columnName));
				break;
			}
			case TimestampType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getTimestamp(columnName));
				break;
			}
			case ClobType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getClob(columnName));
				break;
			}
			case BlobType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getBlob(columnName));
				break;
			}
			case ArrayType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getArray(columnName));
				break;
			}
			case ObjectType: {
				result = new TableValue(columnName, type.domainType,
						resultSet.getObject(columnName));
				break;
			}
			default:
				return null;
			}
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void deleteAllRows(String tableName){
		Statement st;
		try {
			st = con.createStatement();
			String sql = "DELETE FROM " + tableName;
			st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int updateSingleRow(String tableName,
			List<TableValue> columnValuePairs, List<TableValue> whereAndClauses) {
		try {
			StringBuilder insertValues = new StringBuilder();
			StringBuilder whereClauseValues = new StringBuilder();

			// Remove empties first
			Iterator<TableValue> it = columnValuePairs.iterator();
			while (it.hasNext()) {
				TableValue value = it.next();
				if (!value.isValueSet()) {
					it.remove();
				}
			}

			for (TableValue column : columnValuePairs) {
				if (insertValues.length() != 0) {
					insertValues.append(",");
				}
				insertValues.append(column.columnName);
				insertValues.append("=");
				insertValues.append("?");
			}
			if (whereAndClauses != null) {
				for (TableValue column : whereAndClauses) {
					if (whereClauseValues.length() != 0) {
						whereClauseValues.append(" AND ");
					}
					whereClauseValues.append(column);
					whereClauseValues.append("=");
					whereClauseValues.append("?");
				}
			}

			String sql = "UPDATE " + tableName + " SET " + insertValues
					+ " WHERE " + whereClauseValues;
			PreparedStatement statement = (PreparedStatement) con
					.prepareStatement(sql);

			int index = 1;
			for (TableValue column : whereAndClauses) {
				setStatementHelper(index, statement, column);
				index++;
			}

			int resultingRowCount = statement.executeUpdate();
			statement.close();

			if (resultingRowCount == 0) {
				System.out.println("Error: Row not inserted into " + tableName);
				con.close();
				return -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return -1;
		}
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public List<TableValue> getSingleRow(String tableName,
			List<TableValue> columnValuePairs, List<TableValue> whereAndClauses) {
		List<TableValue> result = new ArrayList<TableValue>();
		try {

			StringBuilder columnNames = new StringBuilder();
			StringBuilder whereClauseValues = new StringBuilder();

			for (TableValue column : columnValuePairs) {
				if (columnNames.length() != 0) {
					columnNames.append(",");
				}
				columnNames.append(column.columnName);
			}
			if (whereAndClauses != null) {
				for (TableValue column : whereAndClauses) {
					if (whereClauseValues.length() != 0) {
						whereClauseValues.append(" AND ");
					}
					whereClauseValues.append(column.columnName);
					whereClauseValues.append(" = ");
					whereClauseValues.append("?");
				}
			}

			String sql = "SELECT " + columnNames + " FROM " + tableName
					+ " WHERE " + whereClauseValues;
			System.out.println(sql);
			PreparedStatement statement = (PreparedStatement) con
					.prepareStatement(sql);

			int index = 1;
			for (TableValue column : whereAndClauses) {
				setStatementHelper(index, statement, column);
				index++;
			}

			ResultSet rs = statement.executeQuery();
			if (rs.first()) {
				for (TableValue column : columnValuePairs) {
					result.add(getStatementHelper(column.columnName, rs, column));
				}
				statement.close();
				con.close();
			} else {
				statement.close();
				con.close();
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
