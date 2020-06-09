package controller.user.handler.get;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.user.handler.UserRequestHandler;
import controller.user.response.UserResponseSender;
import dao.user.UserDAO;
import exception.UserDaoException;
import model.user.User;

public class GETMeRequestHandler implements UserRequestHandler {

	private UserDAO userDAO;

	public GETMeRequestHandler(UserDAO userDAO) {
		super();
		this.userDAO = userDAO;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (request.getMethod().equals("GET")) {
			if (request.getPathInfo() != null) {
				if (request.getPathInfo().equals("/me")) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		UserResponseSender sender = (UserResponseSender) request.getAttribute(UserResponseSender.class.getName());
		try {
			User user = (User) request.getSession(false).getAttribute("user");
			User target = userDAO.get(user.getId());
			sender.send(target, response);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_NOT_FOUND, e.getMessage(), response);
		} catch (UserDaoException e) {
			e.printStackTrace();
		}
	}

}
