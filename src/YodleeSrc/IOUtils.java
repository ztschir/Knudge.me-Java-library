/*
 * Copyright 2008 Yodlee, Inc.  All Rights Reserved.  Your use of this code
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


/**
 * This class handles input from the user.
 */
public final class IOUtils {
    /** Cannot create. **/
    private IOUtils() {
    }

    /**
     * Prompted the user for a string with a default.
     *
     * @param defaultStr the default string if the user doesn't enter anything.
     * @return what the user entered or default.
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
     * Read String.
     *
     * @return string entered by user or null for nothing
     */
    public static String readStr() {
        BufferedReader br =
            new BufferedReader(new InputStreamReader(System.in));

        String readStr = null;

        try {
            readStr = br.readLine();

            if (readStr.equalsIgnoreCase("")) {
                return null;
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            throw new RuntimeException("Error reading line!");
        }

        return readStr;
    }

    /**
     * Read Integer.
     *
     * @return the integer entered or -1 if the user messes up
     */
    public static int readInt() {
        BufferedReader br =
            new BufferedReader(new InputStreamReader(System.in));

        String readStr = null;

        try {
            readStr = br.readLine();
            readStr = readStr.trim();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            throw new RuntimeException("Error reading line!");
        }

        try {
            return new Integer(readStr).intValue();
        } catch (NumberFormatException nfEx) {
            // throw new RuntimeException ("Invalid entry: " + readStr + ". You
            // must enter a number.");
            System.out.println(
                "Invalid entry: " + readStr + ". You must enter a number.");

            return -1;
        }
    }

    /**
     * Read Long.
     *
     * @return the long entered or -1 if the user messes up
     */
    public static long readLong() {
        BufferedReader br =
            new BufferedReader(new InputStreamReader(System.in));

        String readStr = null;

        try {
            readStr = br.readLine();
            readStr = readStr.trim();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            throw new RuntimeException("Error reading line!");
        }

        try {
            return new Long(readStr).longValue();
        } catch (NumberFormatException nfEx) {
            // throw new RuntimeException ("Invalid entry: " + readStr + ". You
            // must enter a number.");
            System.out.println(
                "Invalid entry: " + readStr + ". You must enter a number.");

            return -1;
        }
    }

    /**
     * Get current timestamp.
     *
     * @return timestamp
     */
    public static String getTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        return formatter.format(calendar.getTime());
    }

    /**
     * Write to File.
     *
     * @param file output file
     * @param text output text
     * @return boolean success of writing to file
     */
    public static boolean writeFile(File file, String text) {
        return writeFile(file.toString(), text);
    }

    /**
     * Write to File.
     *
     * @param filename the filename
     * @param text text to write
     * @return success.
     */
    public static boolean writeFile(String filename, String text) {
        try {
            FileOutputStream os = new FileOutputStream(filename);
            byte[] textBytes = text.getBytes();
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
    public static String promptInput(
        String displayInputStr, String reenterDisplayInputStr) {
        String input = null;
        System.out.print(displayInputStr);

        boolean validInputFlag = false;

        do {
            validInputFlag = true;

            input = (IOUtils.readStr()).trim();

            if (null != input) {
                validInputFlag = false;
            }

            if (validInputFlag && (null != reenterDisplayInputStr)) {
                System.out.print(reenterDisplayInputStr);
            }
        } while (validInputFlag);

        return input;
    }
}
