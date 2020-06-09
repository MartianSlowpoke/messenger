package controller.user.handler.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.user.handler.UserRequestHandler;
import controller.user.response.UserResponseSender;
import controller.utils.ClientAuthorizationService;

public class UserLogOutRequestHandler implements UserRequestHandler {

	@Override
	public boolean matches(HttpServletRequest request) {
		if (request.getMethod().equals("GET")) {
			if (request.getPathInfo().equals("/logOut")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		@SuppressWarnings("unused")
		UserResponseSender sender = (UserResponseSender) request.getAttribute(UserResponseSender.class.getName());
		ClientAuthorizationService.expire(request, response);
	}

}
