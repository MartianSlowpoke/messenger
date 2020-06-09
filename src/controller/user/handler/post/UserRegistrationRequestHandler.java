package controller.user.handler.post;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.user.handler.UserRequestHandler;
import controller.user.response.UserResponseSender;
import controller.utils.ClientAuthorizationService;
import controller.utils.RegistrationPair;
import controller.utils.UserPostExtractor;
import dao.user.UserDAO;
import exception.UserDaoException;

public class UserRegistrationRequestHandler implements UserRequestHandler {

	private UserDAO userDAO;

	public UserRegistrationRequestHandler(UserDAO userDAO) {
		super();
		this.userDAO = userDAO;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (request.getMethod().equals("POST")) {
			if (request.getContentType().startsWith("multipart/form-data")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		try {
			UserResponseSender sender = (UserResponseSender) request.getAttribute(UserResponseSender.class.getName());
			RegistrationPair registrPair = UserPostExtractor.obtain(request.getParts());
			userDAO.add(registrPair.getUser(), registrPair.getAuth());
			ClientAuthorizationService.auth(request, response, registrPair.getUser(), 60 * 15);
			sender.send(registrPair.getUser(), response);
		} catch (IllegalArgumentException | IOException | SQLException | ServletException e) {
			e.printStackTrace();
		} catch (UserDaoException e) {
			e.printStackTrace();
		}
	}

}
