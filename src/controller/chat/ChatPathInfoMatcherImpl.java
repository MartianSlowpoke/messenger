package controller.chat;

import javax.servlet.http.HttpServletRequest;

public class ChatPathInfoMatcherImpl implements ChatPathInfoMatcher {

	@Override
	public ChatPathInfo match(HttpServletRequest request) {
		switch (request.getMethod()) {
		case "GET":
			if (request.getPathInfo() == null)
				return ChatPathInfo.GET_MY_CHATS;
			throw new IllegalArgumentException("couldn't match method = " + request.getMethod() + " and pathInfo = "
					+ request.getPathInfo() + " to any ChatPathInfo object");
		case "POST":
			if (request.getPathInfo() == null)
				return ChatPathInfo.POST_CHAT;
			throw new IllegalArgumentException("couldn't match method = " + request.getMethod() + " and pathInfo = "
					+ request.getPathInfo() + " to any ChatPathInfo object");
		}
		throw new IllegalArgumentException("couldn't match method = " + request.getMethod() + " and pathInfo = "
				+ request.getPathInfo() + " to any ChatPathInfo object");
	}

}
