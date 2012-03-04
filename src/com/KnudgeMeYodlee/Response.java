package com.KnudgeMeYodlee;
public class Response {
	public UserModel user;
	public String errorMessage;
	public boolean isSuccessful;

	public Response() {
		isSuccessful = true;
	}

	public Response(String error) {
		errorMessage = error;
		isSuccessful = false;
	}
}
