package de.paulomart.gpex.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils {

	/**
	 * WARING THIS STUFF CAN CAUSE BAAAAD STUFF HAPPEN :)
	 * @param clazz
	 * @return
	 */
	public static String classToString(Object object, Object... ignore){
		List<String> ignoreObj = new ArrayList<String>();
		for (Object ignore1 : ignore){
			ignoreObj.add(ignore1.getClass().getSimpleName());
		}
		
		ignoreObj.add(object.getClass().getSimpleName());
		
		String toString = object.getClass().getSimpleName()+": [";
		try {
			for (Field field : object.getClass().getDeclaredFields()) {
				if (!ignoreObj.contains(field.getType().getSimpleName())){
					field.setAccessible(true);
					toString+= "\n"+field.getName() + "=" + field.get(object) + ", ";
				}	            	
			}
		} catch (Exception ex) {
			return ex.getMessage();
		}
		toString = toString.substring(0, toString.length() -2);
		toString+= "]";
		return toString;
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
}
