/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.yodlee.sampleapps.CobrandContextSingleton;

/**
 * IO Utils
 */
public class IOUtils {
	private static final String BLANK_STR = "";
	private static final String SPACE_1 = " ";
	private static BufferedReader br = new BufferedReader(
			new InputStreamReader(CobrandContextSingleton.getSingletonObject()
					.getInputStream()));

	/**
	 * Read String
	 * 
	 * @param defaultStr
	 * @return
	 */
	public static String readStr(String defaultStr) {
		String str = readStr();
		if (str == null) {
			return defaultStr;
		} else {
			return str;
		}
	}

	/**
	 * Read String
	 * 
	 * @return string entered by user
	 */
	public static String readStr() {

		String readStr = null;

		try {
			do {
				readStr = br.readLine();
				if (readStr != null) {
					readStr = readStr.substring(0,
							readStr.indexOf('#') == -1 ? readStr.length()
									: readStr.indexOf('#'));
				}
				readStr = readStr.trim();
			} while (readStr == null || (readStr.equalsIgnoreCase("")));
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
			throw new RuntimeException("Error reading line!");
		}

		return readStr;

	}

	/**
	 * Read Integer
	 * 
	 * @return
	 */
	public static int readInt() {

		String readStr = readStr();

		try {
			return new Integer(readStr).intValue();
		} catch (NumberFormatException nfEx) {
			// throw new RuntimeException ("Invalid entry: " + readStr + ". You
			// must enter a number.");
			System.out.println("Invalid entry: " + readStr
					+ ". You must enter a number.");
			return -1;
		}
	}

	/**
	 * Read Long
	 * 
	 * @return
	 */
	public static long readLong() {

		String readStr = readStr();
		try {
			return new Long(readStr).longValue();
		} catch (NumberFormatException nfEx) {
			// throw new RuntimeException ("Invalid entry: " + readStr + ". You
			// must enter a number.");
			System.out.println("Invalid entry: " + readStr
					+ ". You must enter a number.");
			return -1;
		}
	}

	public static void clrScrn() {
		/*
		 * for(int i=0; i<80; i++){ System.out.println(); }
		 */
		System.out.println("\n\n\n");

	}

	/**
	 * Get current timestamp
	 * 
	 * @return timestamp
	 */
	public static String getTimeStamp() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(calendar.getTime());
	}

	/**
	 * Write to File
	 * 
	 * @param file
	 * @param text
	 * @return boolean sucess of writing to file
	 */
	public static boolean writeFile(File file, String text) {
		return writeFile(file.toString(), text);
	}

	/**
	 * Write to File
	 * 
	 * @param filename
	 *            the filename
	 * @param text
	 *            text to write
	 * @reutrn success.
	 */
	public static boolean writeFile(String filename, String text) {

		try {
			FileOutputStream os = new FileOutputStream(filename);
			byte textBytes[] = text.getBytes();
			os.write(textBytes, 0, textBytes.length);

		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Prompts input from the end user.
	 * 
	 * @param displayInputStr
	 *            Message to be displayed to the user
	 * @param reenterDisplayInputStr
	 *            Error Message to be display to re-enter
	 * @return Input entered by the User
	 */
	public static String promptInput(String displayInputStr,
			String reenterDisplayInputStr) {
		String input = null;
		System.out.print(displayInputStr);
		boolean validInputFlag = false;
		do {
			validInputFlag = true;

			input = (IOUtils.readStr()).trim();
			if (null != input) {
				if ((IFileTransferConstants.EXIT_VALUE).equals(input)) {
					return input;
				}
				validInputFlag = false;
			}
			if (validInputFlag && (null != reenterDisplayInputStr))
				System.out.print(reenterDisplayInputStr);
		} while (validInputFlag);

		return input;
	}

	/**
	 * Displays the string and the required number of spaces after the String
	 * 
	 * @param temp
	 * @param colSize
	 */
	public static void diplayRows(String temp, int colSize) {
		if (temp == null)
			temp = "-";
		System.out.print(temp);
		if (temp.length() < colSize) {
			System.out.print(generateSpaces(colSize - temp.length()));
		}
	}

	/**
	 * generates the required number of spaces
	 * 
	 * @param i
	 * @return
	 */
	public static String generateSpaces(int i) {
		String x = BLANK_STR;
		for (int j = 0; j < i; j++)
			x = x + SPACE_1;
		return x;
	}

	/**
	 * Validates the date
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @return
	 */
	public static boolean validateDate(int year, int month, int date) {
		Calendar cal = Calendar.getInstance();
		if (month < 1 || month > 12) {
			System.out.println("Please enter valid month [1-12]");
			return false;
		}
		if (date < 1 || date > 31) {
			System.out.println("Please enter valid date [1-31]");
			return false;
		}
		if (year < cal.get(Calendar.YEAR)) {
			System.out.println("Please enter valid year > "
					+ cal.get(Calendar.YEAR));
			return false;

		}
		return true;
	}
}
