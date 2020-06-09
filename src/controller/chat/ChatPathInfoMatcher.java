package controller.chat;

import javax.servlet.http.HttpServletRequest;

public interface ChatPathInfoMatcher {

	public ChatPathInfo match(HttpServletRequest request);

}
