package com.KnudgeMeYodlee;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yodlee.soap.collections.common.ArrayOfContentServiceInfo;
import com.yodlee.soap.common.ContentServiceInfo;
import com.yodlee.soap.common.UserContext;

public class GeneralDatabaseAccessor {

	public Response addUser(UserModel user, String password) {
		StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
		RandomSaltGenerator saltGen = new RandomSaltGenerator();
		String salt = saltGen.generateSalt(16).toString();
		
		String encryptedPassword = passwordEncryptor.encryptPassword(password);
		Response response = new Response();

		List<TableValue> columnValuePairs = new ArrayList<TableValue>();
		String tableName = "users";
		if (itemExists(tableName, new TableValue("username",
				TableValue.type.StringType, user.username))) {
			return new Response("Username already exists");
		}
		if (itemExists(tableName, new TableValue("email",
				TableValue.type.StringType, user.email))) {
			return new Response("Email address already exists");
		}

		// TODO add email validator

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(user.yodleeUserContext);
			byte[] asBytes = baos.toByteArray();
			columnValuePairs.add(new TableValue("firstName",
					TableValue.type.StringType, user.firstName));
			columnValuePairs.add(new TableValue("middleName",
					TableValue.type.StringType, user.middleName));
			columnValuePairs.add(new TableValue("lastName",
					TableValue.type.StringType, user.lastName));
			columnValuePairs.add(new TableValue("username",
					TableValue.type.StringType, user.username));
			columnValuePairs.add(new TableValue("email",
					TableValue.type.StringType, user.email));
			columnValuePairs.add(new TableValue("encryptedPassword",
					TableValue.type.StringType, encryptedPassword));
			columnValuePairs.add(new TableValue("unencryptedPassword",
					TableValue.type.StringType, password));
			columnValuePairs.add(new TableValue("salt",
					TableValue.type.StringType, salt));
			columnValuePairs.add(new TableValue("yodleeUserContext",
					TableValue.type.ByteArrayType, new ByteArrayInputStream(
							asBytes)));

			DatabaseConnection dbConnection = new DatabaseConnection();
			dbConnection.insertSingleRow(tableName, columnValuePairs);

		} catch (IOException e) {
			e.printStackTrace();
			return new Response("Internal Error");
		}
		return response;
	}

	public boolean itemExists(String tableName, TableValue columnValue) {
		DatabaseConnection dbConnection = new DatabaseConnection();
		List<TableValue> column = new ArrayList<TableValue>();
		column.add(columnValue);
		if (dbConnection.getSingleRow(tableName, column, column) == null)
			return false;
		else
			return true;
	}

	public Response loginUser(String email, String password) {
		Response response = new Response();
		List<TableValue> columnValuePairs = new ArrayList<TableValue>();
		List<TableValue> whereAndClauses = new ArrayList<TableValue>();
		String tableName = "users";

		columnValuePairs.add(new TableValue("id",
				TableValue.type.IntType, null));
		columnValuePairs.add(new TableValue("username",
				TableValue.type.StringType, null));
		columnValuePairs.add(new TableValue("encryptedPassword",
				TableValue.type.StringType, null));
		columnValuePairs.add(new TableValue("yodleeUserContext",
				TableValue.type.ObjectType, null));

		whereAndClauses.add(new TableValue("email", TableValue.type.StringType,
				email));

		DatabaseConnection dbConnection = new DatabaseConnection();
		List<TableValue> results = dbConnection.getSingleRow(tableName,
				columnValuePairs, whereAndClauses);
		UserModel user = new UserModel();

		if (results != null && !results.isEmpty()) {
			for (TableValue result : results) {
				if (result.columnName.toLowerCase().equals("username")) {
					user.username = result.stringType;
				}else if (result.columnName.toLowerCase().equals("id")) {
					user.id = result.intType;
				}else if (result.columnName.toLowerCase().equals("encryptedpassword")) {
					user.encryptedPassword = result.stringType;
				}else if (result.columnName.toLowerCase().equals("yodleeusercontext")) {
					try {
						byte[] st = (byte[]) result.objectType;
						ByteArrayInputStream baip = new ByteArrayInputStream(st);
						ObjectInputStream ois;
						ois = new ObjectInputStream(baip);
						UserContext userCont = (UserContext) ois.readObject();
						user.yodleeUserContext = userCont;
					} catch (IOException e) {
						e.printStackTrace();
						return new Response("Internal error");
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						return new Response("Internal error");
					}
				}
			}
			StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
			if (passwordEncryptor.checkPassword(password,
					user.encryptedPassword)) {
				response.user = user;
				return response;
			}
		}
		return new Response("Email or Password incorrect.");
	}

	public void importContentServiceInfos(
			ArrayOfContentServiceInfo contentServices) {

		List<TableValue> columnValuePairs = new ArrayList<TableValue>();
		String tableName = "yodleeContentServiceInfo";
		
		

		for (ContentServiceInfo serviceInfo : contentServices.getElements()) {
			columnValuePairs.clear(); // = new ArrayList<TableValue>();
			columnValuePairs.add(new TableValue("contactEmailAddress",
					TableValue.type.StringType, serviceInfo
							.getContactEmailAddress()));
			columnValuePairs.add(new TableValue("contactPhoneNumber",
					TableValue.type.StringType, serviceInfo
							.getContactPhoneNumber()));
			columnValuePairs.add(new TableValue("contactUrl",
					TableValue.type.StringType, serviceInfo.getContactUrl()));
			columnValuePairs.add(new TableValue("contentServiceDisplayName",
					TableValue.type.StringType, serviceInfo
							.getContentServiceDisplayName()));
			columnValuePairs.add(new TableValue("contentServiceId",
					TableValue.type.BigIntType, serviceInfo
							.getContentServiceId()));
			columnValuePairs.add(new TableValue("homeUrl",
					TableValue.type.StringType, serviceInfo.getHomeUrl()));
			columnValuePairs.add(new TableValue("loginUrl",
					TableValue.type.StringType, serviceInfo.getLoginUrl()));
			columnValuePairs.add(new TableValue("organizationDisplayName",
					TableValue.type.StringType, serviceInfo
							.getOrganizationDisplayName()));
			columnValuePairs
					.add(new TableValue("organizationId",
							TableValue.type.BigIntType, serviceInfo
									.getOrganizationId()));
			columnValuePairs.add(new TableValue("passwordHelpUrl",
					TableValue.type.StringType, serviceInfo
							.getPasswordHelpUrl()));
			columnValuePairs.add(new TableValue("registrationUrl",
					TableValue.type.StringType, serviceInfo
							.getRegistrationUrl()));
			columnValuePairs.add(new TableValue("serviceId",
					TableValue.type.BigIntType, serviceInfo.getServiceId()));
			columnValuePairs.add(new TableValue("siteDisplayName",
					TableValue.type.StringType, serviceInfo
							.getSiteDisplayName()));
			columnValuePairs.add(new TableValue("siteId",
					TableValue.type.BigIntType, serviceInfo.getSiteId()));

			DatabaseConnection dbConnection = new DatabaseConnection();
			dbConnection.deleteAllRows(tableName);
			dbConnection.insertSingleRow(tableName, columnValuePairs);
		}
	}

	public void importLoginForms(com.yodlee.soap.collections.Map mapLoginForms) {
		mapLoginForms.getTable();
	}

	public Response getUserModel(long userID) {
		Response response = new Response();
		List<TableValue> columnValuePairs = new ArrayList<TableValue>();
		List<TableValue> whereAndClauses = new ArrayList<TableValue>();
		String tableName = "users";

		columnValuePairs.add(new TableValue("username",
				TableValue.type.StringType, null));
		columnValuePairs.add(new TableValue("unencryptedPassword",
				TableValue.type.StringType, null));
		columnValuePairs.add(new TableValue("yodleeUserContext",
				TableValue.type.ObjectType, null));

		whereAndClauses.add(new TableValue("id", TableValue.type.BigIntType,
				userID));

		DatabaseConnection dbConnection = new DatabaseConnection();
		List<TableValue> results = dbConnection.getSingleRow(tableName,
				columnValuePairs, whereAndClauses);
		UserModel user = new UserModel();

		if (results != null && !results.isEmpty()) {
			for (TableValue result : results) {
				if (result.columnName.toLowerCase().equals("username")) {
					user.username = result.stringType;
				} else if (result.columnName.toLowerCase().equals("unencryptedpassword")) {
						user.unencryptedPassword = result.stringType;
				} else if (result.columnName.toLowerCase().equals("yodleeusercontext")) {

					try {
						byte[] st = (byte[]) result.objectType;
						ByteArrayInputStream baip = new ByteArrayInputStream(st);
						ObjectInputStream ois;
						ois = new ObjectInputStream(baip);
						UserContext userCont = (UserContext) ois.readObject();
						user.yodleeUserContext = userCont;
					} catch (IOException e) {
						e.printStackTrace();
						return new Response("Internal error");
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						return new Response("Internal error");
					}
				}
			}
			response.user = user;
			return response;
		}
		return new Response("Email or Password incorrect.");
	}

}
