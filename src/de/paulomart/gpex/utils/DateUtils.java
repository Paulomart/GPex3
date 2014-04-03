package de.paulomart.gpex.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	public static String stringFromDate(Date date){
		return dateFormat.format(date);
	}
	
	public static Date dateFromString(String string){
		try {
			return dateFormat.parse(string);
		} catch (Exception e) {
		}
		return null;
	}
}
