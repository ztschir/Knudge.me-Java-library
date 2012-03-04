/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you.
 */
package com.yodlee.sampleapps.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Validation Util Class The different validations supporting this class are 1.
 * Converts Date String to Date object. 2. Validates Start Date and End Date. 3.
 * Validate Amount Entered.
 */
public class ValidationUtil {

	/**
	 * Converts a Date String to a Date Object with the user's chosen Date
	 * Format.
	 * 
	 * @param dateStr -
	 *            Date as String.
	 * @return - date as java.util.Date Object
	 * @throws ParseException
	 */
	public static Date getDate(String dateStr) throws ParseException {
		Date date = null;
		// need this null validation because end could be null
		if (dateStr != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					IFileTransferConstants.dateFormat);
			date = formatter.parse(dateStr);
		}

		return date;
	}

	/**
	 * Validates the start date and the end date.
	 * 
	 * @param startDt
	 *            start date
	 * @param endDt
	 *            end date
	 * @return true if the validation is successful, false otherwise.
	 */
	public static boolean isValidStartAndEndDates(String startDt, String endDt) {
		if (startDt != null && endDt != null) {
			// start date must be before end date
			// if (start.before(end)){
			// End date must be no more than [X days] in the future.
			// Assume 1 year unless Archie/Greg say otherwise.

			Date startDate = null;
			Date endDate = null;

			try {
				startDate = getDate(startDt);
				endDate = getDate(endDt);
			} catch (ParseException pe) {
				System.out.println("Invalid format for Date");
				return false;
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			cal.add(Calendar.YEAR, 1);
			if (!endDate.after(cal.getTime()) && !endDate.equals(cal.getTime()))
				return true;

		}
		return false;
	}

	/**
	 * Validates if the input amount is a valid Amount.
	 * 
	 * @param amount -
	 *            amount to be validated.
	 * @return - true if amount validated, false otherwise.
	 */
	public static double getValidAmount(String amount) {
		// Converting the amounts to the real double values.
		if (amount.length() < 2) {
			amount += "0";
		}
		double responseAmount = 0.00;
		try {
			responseAmount = Double.valueOf(amount).doubleValue();
		} catch (NumberFormatException e) {
			System.out.println("The verify Amount entered is not a number");
			System.out
					.println("The deposit amounts you entered is invalid. "
							+ "Please note that you can only enter digits for the deposit amounts.");
		}
		return responseAmount;
	}
}
