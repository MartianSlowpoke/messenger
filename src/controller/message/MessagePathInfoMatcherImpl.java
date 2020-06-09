package controller.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class MessagePathInfoMatcherImpl implements MessagePathInfoMatcher {

	private final String messageIdRegex = "^/[0-9]*$";
	private final String messageFileRegex = "^/[0-9]*/file$";
	private final String messageIdSearchRegex = "\\d+";

	@Override
	public MessagePathInfo match(HttpServletRequest request) {
		switch (request.getMethod()) {
		case "GET":
			if (request.getPathInfo() == null && request.getParameter("chatId") != null)
				return MessagePathInfo.GET_CHAT_MESSAGES;
			if (Pattern.matches(messageIdRegex, request.getPathInfo()))
				return MessagePathInfo.GET_MESSAGE;
			if (Pattern.matches(messageFileRegex, request.getPathInfo()))
				return MessagePathInfo.GET_MESSAGE_FILE;
			break;
		case "POST":
			if (request.getPathInfo() == null)
				return MessagePathInfo.POST_MESSAGE;
			break;
		}
		throw new IllegalArgumentException("couldn't match pathInfo[" + request.getPathInfo() + "],method["
				+ request.getMethod() + "] to any PathInfoMatch case");
	}

	@Override
	public Long getMessageIdFromPathInfo(String pathInfo) {
		Pattern pattern = Pattern.compile(messageIdSearchRegex);
		Matcher matcher = pattern.matcher(pathInfo);
		if (matcher.find()) {
			String str = matcher.group();
			return Long.valueOf(str);
		}
		throw new IllegalArgumentException("no id specified in the pathInfo");
	}

}
