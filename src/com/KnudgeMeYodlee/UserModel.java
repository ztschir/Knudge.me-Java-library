package com.KnudgeMeYodlee;
import com.yodlee.soap.common.UserContext;

public class UserModel {
	Integer id;
	String firstName;
	String middleName;
	String lastName;
	String username;
	String email;
	String encryptedPassword;
	String unencryptedPassword;
	String salt;
	UserContext yodleeUserContext;
}
