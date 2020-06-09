package controller.user.handler.get;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.user.handler.UserRequestHandler;
import controller.user.response.UserResponseSender;
import dao.user.UserDAO;
import exception.UserDaoException;
import model.user.User;

public class GETUsersRequestHandler implements UserRequestHandler {

	private UserDAO userDAO;

	public GETUsersRequestHandler(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (request.getMethod().equals("GET")) {
			if (request.getPathInfo() == null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		UserResponseSender sender = (UserResponseSender) request.getAttribute(UserResponseSender.class.getName());
		try {
			Collection<User> users = userDAO.getAll();
			sender.send(users, response);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_NO_CONTENT, e.getMessage(), response);
		} catch (UserDaoException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), response);
		}
	}

}
