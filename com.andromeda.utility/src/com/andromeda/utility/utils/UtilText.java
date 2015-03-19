package com.andromeda.utility.utils;

/**
 * Utility that holds everything that relates to String operation
 * 
 * @author tsaravana
 *
 */
public class UtilText {

	public static String getFirstLowerName(String input) {
		return input.substring(0, 1).toLowerCase() + input.substring(1);
	}

	public static String getFirstUpperName(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
}
