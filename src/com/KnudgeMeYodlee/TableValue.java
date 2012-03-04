package com.KnudgeMeYodlee;
import java.io.ByteArrayInputStream;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.util.List;

public class TableValue {
	public enum type {
		StringType, DecimalType, BoolType, TinyIntType, SmallIntType, IntType, BigIntType, RealType, DoubleType, BinaryType, ByteArrayType, DateType, TimeType, TimestampType, ClobType, BlobType, ArrayType, ObjectType;
	};

	public type domainType;
	public String columnName;
	public String stringType; // CHAR, VARCHAR, LONGVARCHAR
	public java.math.BigDecimal decimalType; // NUMERIC, DECIMAL
	public Boolean boolType; // BIT, BOOLEAN
	public Byte tinyIntType; // TINYINT
	public Short smallIntType; // SMALLINT
	public Integer intType; // INTEGER
	public Long bigIntType; // BIGINT
	public Float realType; // REAL
	public Double doubleType; // FLOAT, DOUBLE
	public List<Byte> binaryType; // BINARY, VARBINARY, LONGVARBINARY
	public ByteArrayInputStream byteArrayType; // BINARYSTREAM
	public java.sql.Date dateType; // DATE
	public java.sql.Time timeType; // TIME
	public java.sql.Timestamp timestampType; // TIMESTAMP
	public Clob clobType; // CLOB
	public Blob blobType; // BLOB
	public Array arrayType; // Array
	public Object objectType; // Object

	public TableValue(String tableColumnName, type typeOfTableValue,
			Object value) {
		domainType = typeOfTableValue;
		columnName = tableColumnName;
		switch (typeOfTableValue) {

		case StringType: {
			stringType = (String) value;
			break;
		}
		case DecimalType: {
			decimalType = (java.math.BigDecimal) value;
			break;
		}
		case BoolType: {
			boolType = (Boolean) value;
			break;
		}
		case TinyIntType: {
			tinyIntType = (Byte) value;
			break;
		}
		case SmallIntType: {
			smallIntType = (Short) value;
			break;
		}
		case IntType: {
			intType = (Integer) value;
			break;
		}
		case BigIntType: {
			bigIntType = (Long) value;
			break;
		}
		case RealType: {
			realType = (Float) value;
			break;
		}
		case DoubleType: {
			doubleType = (Double) value;
			break;
		}
		case BinaryType: {
			binaryType = (List<Byte>) value;
			break;
		}
		case ByteArrayType: {
			byteArrayType = (ByteArrayInputStream) value;
			break;
		}
		case DateType: {
			dateType = (java.sql.Date) value;
			break;
		}
		case TimeType: {
			timeType = (java.sql.Time) value;
			break;
		}
		case TimestampType: {
			timestampType = (java.sql.Timestamp) value;
			break;
		}
		case ClobType: {
			clobType = (Clob) value;
			break;
		}
		case BlobType: {
			blobType = (Blob) value;
			break;
		}
		case ArrayType: {
			arrayType = (Array) value;
			break;
		}
		case ObjectType: {
			objectType = (Object) value;
			break;
		}
		}
	}

	public boolean isValueSet() {
		switch (domainType) {

		case StringType: {
			return stringType != null;

		}
		case DecimalType: {
			return decimalType != null;

		}
		case BoolType: {
			return boolType != null;

		}
		case TinyIntType: {
			return tinyIntType != null;

		}
		case SmallIntType: {
			return smallIntType != null;

		}
		case IntType: {
			return intType != null;

		}
		case BigIntType: {
			return bigIntType != null;

		}
		case RealType: {
			return realType != null;

		}
		case DoubleType: {
			return doubleType != null;

		}
		case BinaryType: {
			return binaryType != null;
		}
		case ByteArrayType: {
			return byteArrayType != null;
		}
		case DateType: {
			return dateType != null;
		}
		case TimeType: {
			return timeType != null;

		}
		case TimestampType: {
			return timestampType != null;

		}
		case ClobType: {
			return clobType != null;

		}
		case BlobType: {
			return blobType != null;

		}
		case ArrayType: {
			return arrayType != null;

		}
		case ObjectType: {
			return objectType != null;
		}
		default:
			return false;
		}
	}
}
