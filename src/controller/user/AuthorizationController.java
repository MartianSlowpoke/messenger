package controller.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.user.handler.UserRequestHandler;
import controller.user.handler.auth.UserLogInRequestHandler;
import controller.user.handler.auth.UserLogOutRequestHandler;
import controller.user.response.JSONUserResponseSenderImpl;
import controller.user.response.UserResponseSender;
import dao.user.UserDAO;

@SuppressWarnings("serial")
public class AuthorizationController extends HttpServlet {

	private Collection<UserRequestHandler> handlers;

	@Override
	public void init(ServletConfig config) throws ServletException {
		UserDAO userDAO = (UserDAO) config.getServletContext().getAttribute("userDAO");
		handlers = new ArrayList<>();
		UserLogInRequestHandler handler1 = new UserLogInRequestHandler(userDAO);
		UserLogOutRequestHandler handler2 = new UserLogOutRequestHandler();
		handlers.add(handler1);
		handlers.add(handler2);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute(UserResponseSender.class.getName(), new JSONUserResponseSenderImpl());
		for (UserRequestHandler handler : handlers) {
			if (handler.matches(req)) {
				handler.handle(req, resp);
			}
		}
	}

}
