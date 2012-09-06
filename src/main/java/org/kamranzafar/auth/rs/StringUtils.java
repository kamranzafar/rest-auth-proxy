package org.kamranzafar.auth.rs;

import java.util.regex.Pattern;

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
		} catch (NumberFormatException nfe) {
			return defval;
		}
	}

	public static String trim(String s, char c) {
		StringBuffer tmp = new StringBuffer(s);
		for (int i = 0; i < tmp.length(); i++) {
			if (tmp.charAt(i) != c) {
				break;
			} else {
				tmp.deleteCharAt(i);
			}
		}

		for (int i = tmp.length() - 1; i >= 0; i--) {
			if (tmp.charAt(i) != c) {
				break;
			} else {
				tmp.deleteCharAt(i);
			}
		}

		return tmp.toString();
	}

	public static boolean matches(String file, String pattern) {
		Pattern p = null;
		try {
			p = Pattern.compile(pattern);
		} catch (Exception e) {
			return true;
		}

		return p.matcher(file).matches();
	}
}
