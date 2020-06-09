package controller.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserGetRequestMatcher {

	// users/{id} - GET_BY_ID

	// users/{id}/photo - GET_USER_PHOTO

	// users?login=Slc - SEARCH

	private final static String userIdRegex = "^/[0-9]*$";
	private final static String userPhotoRegex = "^/[0-9]*/photo$";
	private final static String userAuthRegex = "^/[0-9]*/auth$";
	private final static String idRegex = "\\d+";

	public static UserGetCase match(String pathInfo) {
		if (pathInfo == null)
			return UserGetCase.SEARCH;
		if (Pattern.matches(userIdRegex, pathInfo))
			return UserGetCase.ID;
		if (Pattern.matches(userPhotoRegex, pathInfo))
			return UserGetCase.PHOTO;
		if (Pattern.matches(userAuthRegex, pathInfo))
			return UserGetCase.AUTH;
		throw new IllegalArgumentException("no matched");
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
