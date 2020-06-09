package controller.user.handler.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.user.handler.UserRequestHandler;
import controller.user.response.UserResponseSender;
import controller.utils.AuthorizationHeaderExtractor;
import controller.utils.ClientAuthorizationService;
import dao.user.UserDAO;
import exception.UserDaoException;
import model.user.Authentication;
import model.user.User;

public class UserLogInRequestHandler implements UserRequestHandler {

	private UserDAO userDAO;

	public UserLogInRequestHandler(UserDAO userDAO) {
		super();
		this.userDAO = userDAO;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (request.getMethod().equals("GET")) {
			if (request.getPathInfo().equals("/logIn")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		UserResponseSender sender = (UserResponseSender) request.getAttribute(UserResponseSender.class.getName());
		try {
			Authentication credentials = AuthorizationHeaderExtractor.extract(request.getHeader("Authorization"));
			User user = userDAO.logIn(credentials);
			ClientAuthorizationService.auth(request, response, user, 60 * 15);
			sender.send(user, response);
		} catch (UserDaoException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), response);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_NOT_FOUND, e.getMessage(), response);
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), response);
		}
	}

}
