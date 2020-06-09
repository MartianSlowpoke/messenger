package controller.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDeleteRequestMatcher {

	private final static String userIdRegex = "^/[0-9]*$";
	private final static String idRegex = "\\d+";

	public static UserDeleteCase match(String pathInfo) throws IllegalArgumentException {
		if (pathInfo.matches(userIdRegex))
			return UserDeleteCase.DELETE;
		throw new IllegalArgumentException("no valid path info for removal process");
	}

	public static Long extractId(String pathInfo) throws IllegalArgumentException {
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(pathInfo);
		if (matcher.find()) {
			String str = matcher.group();
			return Long.valueOf(str);
		}
		throw new IllegalArgumentException("no id specified in the pathInfo");
	}

}
