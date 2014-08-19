package options;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {
	public static final Locale locale = new Locale("en", "GB");
	public static final TimeZone timezone = TimeZone.getTimeZone("Europe/London");
	private static final Calendar rightNow = Calendar.getInstance(timezone, locale);
	private static Calendar nextExpiryDate;
	
	public static final Calendar rightNow() {
		return (Calendar) rightNow.clone();
	}
	
	public static final boolean before(Calendar a, Calendar b) {
		 return a.get(Calendar.YEAR) < b.get(Calendar.YEAR) ||
				 ( a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.MONTH) < b.get(Calendar.MONTH) );
	}
	
	public static final boolean equals(Calendar a, Calendar b) {
		 return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.MONTH) == b.get(Calendar.MONTH);
	}
	
	public static final Calendar dateToCalendar(Date d) {
		final Calendar a = rightNow();
		a.setTime(d);
		return a;
	}
	
	static {
		nextExpiryDate = calculateNextExpiryDate();
		System.out.println("The next expiry date is " + new SimpleDateFormat("dd-MMM-yy", Utils.locale).format(nextExpiryDate.getTime()));
	}
	
	public static final Calendar nextExpiryDate() {
		return (Calendar) nextExpiryDate.clone();
	}
	
	private static final Calendar calculateNextExpiryDate() {
		Calendar rightNow = rightNow();
		Calendar cal = rightNow(); // just using this as an easy way to get hold of a Calendar object, with right locale
		for (int m=0; m<2; m++) {
			int fridays = 0;
			for (int d=1; d<=30; d++) {
				// increment date, day-by-day
				cal.set(rightNow.get(Calendar.YEAR), (rightNow.get(Calendar.MONTH) + m) % 12, d);
				if (Calendar.FRIDAY == cal.get(Calendar.DAY_OF_WEEK) && ++fridays == 3 && cal.after(rightNow())) {
					return cal;
				}
			}
		}
		return rightNow;
	}
}
