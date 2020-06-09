package controller.message;

import javax.servlet.http.HttpServletRequest;

public interface MessagePathInfoMatcher {

	public MessagePathInfo match(HttpServletRequest request);
	
	public Long getMessageIdFromPathInfo(String pathInfo);
	
}
