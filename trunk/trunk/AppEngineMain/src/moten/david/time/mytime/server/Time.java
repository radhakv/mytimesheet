package moten.david.time.mytime.server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Utility class to help with time-based calculations in Astronomy
 * 
 * @author smr
 * 
 */
public class Time {

	public static final double MILLISEC_PER_DAY = 86400000.0;
	public static final double BASE_JD = 2440587.5;

	/**
	 * Calculate the Julian day number corresponding to the time provided
	 * 
	 * @param -
	 *            time, the time for which the Julian Day number is required. If
	 *            null, the current time will be used.
	 * 
	 * @return - the Julian day number corresponding to the supplied time
	 */
	public static double getJulianDayNumber(Calendar time) {

		double jd = BASE_JD + time.getTimeInMillis() / MILLISEC_PER_DAY;

		return jd;
	}

	public static Calendar getCalendarFromJulianDay(double jd) {
		long time = Math.round(Math.floor((jd - BASE_JD) * MILLISEC_PER_DAY));
		GregorianCalendar cal = new GregorianCalendar(TimeZone
				.getTimeZone("GMT"));
		cal.setTimeInMillis(time);
		return cal;
	}

	public static void main(String[] args) {

		Date d = new Date(0);
		System.out.println(d);
	}

}
