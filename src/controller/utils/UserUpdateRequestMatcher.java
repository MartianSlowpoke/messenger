package controller.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUpdateRequestMatcher {

	/*
	 * update authentication credentials /messenger/users/{id}/auth
	 * 
	 * update user information /messenger/users/{id}
	 */

	private final static String userIdRegex = "^/[0-9]*$";
	private final static String userAuthRegex = "^/[0-9]*/auth$";
	private final static String idRegex = "\\d+";

	public static UserUpdateCase match(String pathInfo) throws IllegalArgumentException {
		if (Pattern.matches(userIdRegex, pathInfo))
			return UserUpdateCase.UPDATE_INFO;
		if (Pattern.matches(userAuthRegex, pathInfo))
			return UserUpdateCase.UPDATE_AUTH;
		throw new IllegalArgumentException("bad pathInfo");
	}

	public static Long extractId(String pathInfo) {
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(pathInfo);
		matcher.find();
		String str = matcher.group();
		return Long.valueOf(str);
	}

}
