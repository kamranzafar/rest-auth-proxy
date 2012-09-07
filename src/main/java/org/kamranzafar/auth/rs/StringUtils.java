package org.kamranzafar.auth.rs;


/**
 * @author Kamran Zafar
 * 
 */
public class StringUtils {
	public static boolean isEmpty(String str) {
		return str == null || str.trim().equals("");
	}

	public static boolean isBlank(String str) {
		return str != null && str.trim().equals("");
	}

	public static int toInt(String str, int defval) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return defval;
		}
	}
}
