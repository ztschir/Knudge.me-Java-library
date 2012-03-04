/*
 * Copyright 2009 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.util.Calendar;
import java.util.Date;

/**
 * contains Helper methods for making calendar calculations
 */
public class CalendarUtil {

	//private static final Logger logger = Logger.getLogger(CalendarUtil.class.getName());

	public static final int FIRST_DAY_OF_MONTH = 1;

	public static Calendar createCalendar(long timeInMillis) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);
		calendar
				.setMinimalDaysInFirstWeek(calculateMinimalDaysInFirstWeek(calendar));
		return calendar;
	}

	public static int calculateMinimalDaysInFirstWeek(Calendar calendar) {

		Calendar temp = (Calendar) calendar.clone();
		temp.set(Calendar.DAY_OF_YEAR, 1);
		int firstDayOfJan = temp.get(Calendar.DAY_OF_WEEK);
		return 8 - firstDayOfJan;
	}

	// normalises the calendar object by clearing time related fields
	public static void normalise(Calendar calendar) {

		Calendar tempCalendar = (Calendar) calendar.clone();
		calendar.clear();
		calendar.set(Calendar.YEAR, tempCalendar.get(Calendar.YEAR));
		calendar.set(Calendar.DAY_OF_YEAR, tempCalendar
				.get(Calendar.DAY_OF_YEAR));
	}

	public static int getYear(Calendar calendar) {
		return calendar.get(Calendar.YEAR);
	}

	public static Long getTimeForFirstDayOfYear(Calendar calendar) {

		Calendar tempCalendar = (Calendar) calendar.clone();
		setTimeAsFirstDayOfYear(tempCalendar);

		return new Long(tempCalendar.getTimeInMillis());
	}

	public static void setTimeAsFirstDayOfYear(Calendar calendar) {

		calendar.set(Calendar.DAY_OF_YEAR, 1);
		normalise(calendar);
	}

	/*
	 * Helper methods for Month
	 */
	public static int getMonth(Calendar calendar) {
		return calendar.get(Calendar.MONTH);
	}

	public static boolean isSameMonthOfYear(Calendar calendar1,
			Calendar calendar2) {

		return (getYear(calendar1) == getYear(calendar2))
				&& (getMonth(calendar1) == getMonth(calendar2));
	}

	public static void decrementOneMonth(Calendar calendar) {

		int monthBeforeDecrement = calendar.get(Calendar.MONTH);
		calendar.add(Calendar.MONTH, -1);
		int monthAfterDecrement = calendar.get(Calendar.MONTH);

		// if year changes setMinimalDaysInFirstWeek for New Year
		if (monthBeforeDecrement == Calendar.JANUARY
				&& monthAfterDecrement == Calendar.DECEMBER) {
			calendar
					.setMinimalDaysInFirstWeek(calculateMinimalDaysInFirstWeek(calendar));
		}
	}

	public static void incrementOneMonth(Calendar calendar) {

		int monthBeforeIncrement = calendar.get(Calendar.MONTH);
		calendar.add(Calendar.MONTH, 1);
		int monthAfterIncrement = calendar.get(Calendar.MONTH);

		// if year changes setMinimalDaysInFirstWeek for New Year
		if (monthBeforeIncrement == Calendar.DECEMBER
				&& monthAfterIncrement == Calendar.JANUARY) {
			calendar
					.setMinimalDaysInFirstWeek(calculateMinimalDaysInFirstWeek(calendar));
		}
	}

	public static Long getTimeForFirstDayOfMonth(Calendar calendar) {

		Calendar tempCalendar = (Calendar) calendar.clone();
		setTimeAsFirstDayOfMonth(tempCalendar);

		return new Long(tempCalendar.getTimeInMillis());
	}

	public static void setTimeAsFirstDayOfMonth(Calendar calendar) {

		calendar.set(Calendar.DAY_OF_MONTH, FIRST_DAY_OF_MONTH);
		normalise(calendar);
	}

	/**
	 * returns number of months from date of startPoint till date of endPoint
	 *
	 * <pre>
	 *     1. returns 0 if startPoint and endPoint are in the same month
	 *     2. returns a positive no. if month of startPoint is before month of endPoint
	 *     3. returns a negative no. if month of startPoint is after month of endPoint
	 * </pre>
	 *
	 * @param startPoint
	 * @param endPoint
	 *
	 * @return returns number of months from date of startPoint till date of
	 *         endPoint
	 */
	public static int calculateMonthsInBetween(Calendar startPoint,
			Calendar endPoint) {

		int numMonths = 0;

		Calendar tempCalendar = (Calendar) startPoint.clone();
		int compare = startPoint.getTime().compareTo(endPoint.getTime());
		if (compare <= 0) {

			while (!isSameMonthOfYear(tempCalendar, endPoint)) {
				incrementOneMonth(tempCalendar);
				numMonths++;
			}
		} else {

			while (!isSameMonthOfYear(tempCalendar, endPoint)) {
				decrementOneMonth(tempCalendar);
				numMonths--;
			}
		}

		return numMonths;
	}

	/**
	 * returns true if there are missing months between first and second points
	 *
	 * <br>
	 * Note: The first point is before second point
	 */
	public static boolean areMonthsMissing(Calendar firstPoint,
			Calendar secondPoint) {

		boolean missing = true;

		int yearOfFirstPoint = getYear(firstPoint);
		int yearOfCurrentPoint = getYear(secondPoint);
		int monthOfFirstPoint = getMonth(firstPoint);
		int monthOfSecondPoint = getMonth(secondPoint);

		// if both dates on the same month
		if (isSameMonthOfYear(firstPoint, secondPoint))
			missing = false;
		// else if same year check if consecutive months of the year
		if ((yearOfFirstPoint == yearOfCurrentPoint)
				&& (monthOfFirstPoint + 1 == monthOfSecondPoint)) {
			missing = false;
		}
		// if different years check if consecutive months of the two years
		else if (yearOfFirstPoint + 1 == yearOfCurrentPoint) {

			Calendar tempCalendar = (Calendar) firstPoint.clone();
			incrementOneMonth(tempCalendar);
			if (isSameMonthOfYear(tempCalendar, secondPoint))
				missing = false;
		}

		return missing;
	}

	/*
	 * Helper methods for Week
	 */

	public static int getWeek(Calendar calendar) {
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	public static boolean isSameWeekOfYear(Calendar calendar1,
			Calendar calendar2) {

		boolean sameWeek = false;

		// if same year check if same week
		if (getYear(calendar1) == getYear(calendar2)) {
			if (getWeek(calendar1) == getWeek(calendar2))
				sameWeek = true;
		}
		/*
		 * else if different years check if the two points fall in the same
		 * week. e.g : 29th December 2003 and 2nd January 2004 fall in different
		 * years but same week
		 */
		else {
			Calendar calendar1Temp = (Calendar) calendar1.clone();
			Calendar calendar2Temp = (Calendar) calendar2.clone();
			setTimeAsFirstDayOfWeek(calendar1Temp);
			setTimeAsFirstDayOfWeek(calendar2Temp);
			if (isSameDayOfYear(calendar1Temp, calendar2Temp))
				sameWeek = true;
		}

		return sameWeek;
	}

	public static void decrementOneWeek(Calendar calendar) {

		int monthBeforeDecrement = calendar.get(Calendar.MONTH);
		calendar.add(Calendar.WEEK_OF_YEAR, -1);
		int monthAfterDecrement = calendar.get(Calendar.MONTH);

		// if year changes setMinimalDaysInFirstWeek for New Year
		if (monthBeforeDecrement == Calendar.JANUARY
				&& monthAfterDecrement == Calendar.DECEMBER) {
			calendar
					.setMinimalDaysInFirstWeek(calculateMinimalDaysInFirstWeek(calendar));
		}
	}

	public static void incrementOneWeek(Calendar calendar) {

		int monthBeforeIncrement = calendar.get(Calendar.MONTH);
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		int monthAfterIncrement = calendar.get(Calendar.MONTH);

		// if year changes setMinimalDaysInFirstWeek for New Year
		if (monthBeforeIncrement == Calendar.DECEMBER
				&& monthAfterIncrement == Calendar.JANUARY) {
			calendar
					.setMinimalDaysInFirstWeek(calculateMinimalDaysInFirstWeek(calendar));
		}
	}

	public static Long getTimeForFirstDayOfWeek(Calendar calendar) {

		Calendar tempCalendar = (Calendar) calendar.clone();
		setTimeAsFirstDayOfWeek(tempCalendar);

		return new Long(tempCalendar.getTimeInMillis());
	}

	public static void setTimeAsFirstDayOfWeek(Calendar calendar) {

		int monthBeforeSet = calendar.get(Calendar.MONTH);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
		int monthAfterSet = calendar.get(Calendar.MONTH);

		// if year changes setMinimalDaysInFirstWeek for New Year
		if (monthBeforeSet == Calendar.JANUARY
				&& monthAfterSet == Calendar.DECEMBER) {
			calendar
					.setMinimalDaysInFirstWeek(calculateMinimalDaysInFirstWeek(calendar));
		}
		normalise(calendar);
	}

	/**
	 * returns number of weeks from date of startPoint till date of endPoint
	 *
	 * <pre>
	 *     1. returns 0 if startPoint and endPoint are in the same week
	 *     2. returns a positive no. if week of startPoint is before week of endPoint
	 *     3. returns a negative no. if week of startPoint is after week of endPoint
	 * </pre>
	 *
	 * @param startPoint
	 * @param endPoint
	 *
	 * @return returns number of weeks from date of startPoint till date of
	 *         endPoint
	 */
	public static int calculateWeeksInBetween(Calendar startPoint,
			Calendar endPoint) {

		int numWeeks = 0;

		Calendar tempCalendar = (Calendar) startPoint.clone();
		int compare = startPoint.getTime().compareTo(endPoint.getTime());
		if (compare <= 0) {

			while (!isSameWeekOfYear(tempCalendar, endPoint)) {
				incrementOneWeek(tempCalendar);
				numWeeks++;
			}
		} else {

			while (!isSameWeekOfYear(tempCalendar, endPoint)) {
				decrementOneWeek(tempCalendar);
				numWeeks--;
			}
		}

		return numWeeks;
	}

	/**
	 * returns true if there are missing weeks between first and second points
	 *
	 * <br>
	 * Note: The first point is before the second point
	 */
	public static boolean areWeeksMissing(Calendar firstPoint,
			Calendar secondPoint) {

		boolean missing = true;

		int yearOfFirstPoint = getYear(firstPoint);
		int yearOfCurrentPoint = getYear(secondPoint);
		int weekOfFirstPoint = getWeek(firstPoint);
		int weekOfSecondPoint = getWeek(secondPoint);

		// if both dates on the same week
		if (isSameWeekOfYear(firstPoint, secondPoint))
			missing = false;
		// else if same year check if consecutive weeks of the year
		else if ((yearOfFirstPoint == yearOfCurrentPoint)
				&& (weekOfFirstPoint + 1 == weekOfSecondPoint)) {
			missing = false;
		}
		// if different years check if consecutive weeks of the two years
		else if (yearOfFirstPoint + 1 == yearOfCurrentPoint) {

			Calendar tempCalendar = (Calendar) firstPoint.clone();
			incrementOneWeek(tempCalendar);
			if (isSameWeekOfYear(tempCalendar, secondPoint))
				missing = false;
		}

		return missing;
	}

	/*
	 * Helper methods for Day
	 */

	public static int getDay(Calendar calendar) {
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	public static boolean isSameDayOfYear(Calendar calendar1, Calendar calendar2) {

		return (getYear(calendar1) == getYear(calendar2))
				&& (getDay(calendar1) == getDay(calendar2));
	}

	public static void decrementOneDay(Calendar calendar) {

		int monthBeforeDecrement = calendar.get(Calendar.MONTH);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		int monthAfterDecrement = calendar.get(Calendar.MONTH);

		// if year changes setMinimalDaysInFirstWeek for New Year
		if (monthBeforeDecrement == Calendar.JANUARY
				&& monthAfterDecrement == Calendar.DECEMBER) {
			calendar
					.setMinimalDaysInFirstWeek(calculateMinimalDaysInFirstWeek(calendar));
		}
	}

	public static void incrementOneDay(Calendar calendar) {

		int monthBeforeIncrement = calendar.get(Calendar.MONTH);
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		int monthAfterIncrement = calendar.get(Calendar.MONTH);

		// if year changes setMinimalDaysInFirstWeek for New Year
		if (monthBeforeIncrement == Calendar.DECEMBER
				&& monthAfterIncrement == Calendar.JANUARY) {
			calendar
					.setMinimalDaysInFirstWeek(calculateMinimalDaysInFirstWeek(calendar));
		}
	}

	public static boolean afterCurrentDay(Calendar time) {
		boolean afterCurrent = false;

		Calendar currentTime = createCalendar(System.currentTimeMillis());
		int yearOfTime = getYear(time);
		int yearOfCurrentTime = getYear(currentTime);

		if (yearOfTime > yearOfCurrentTime)
			afterCurrent = true;
		else if (yearOfTime == yearOfCurrentTime
				&& getDay(time) > getDay(currentTime))
			afterCurrent = true;

		return afterCurrent;
	}

	/**
	 * This method takes time in millisecs ,and returs a Calendar object with
	 * only year and month fields set to the given time. Date is set to the
	 * first day of the month.Rest of the fields in caledar are cleared.
	 *
	 * @param timeMillis
	 * @return
	 */
	public static Calendar getNormalizedMonth(long timeMillis) {
		Calendar transCalendar = Calendar.getInstance();
		transCalendar.setTimeInMillis(timeMillis);

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, transCalendar.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, transCalendar.get(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar;
	}

	public static Date getPreceedingDate(Date date) {

		Calendar calendar = createCalendar(date.getTime());
		decrementOneDay(calendar);

		return calendar.getTime();
	}
	
	/*public static Calendar getCurrentCalendar() {
		return PaymentRequestCalendarUtil.getCurrentCalendar();
	}*/

}
