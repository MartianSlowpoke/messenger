package controller.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.user.handler.UserRequestHandler;
import controller.user.handler.delete.DeleteUserRequestHandler;
import controller.user.handler.get.GETMeRequestHandler;
import controller.user.handler.get.GETUserAuthenticationRequestHandler;
import controller.user.handler.get.GETUserByIdRequestHandler;
import controller.user.handler.get.GETUserPhotoRequestHandler;
import controller.user.handler.get.GETUsersRequestHandler;
import controller.user.handler.post.UserRegistrationRequestHandler;
import controller.user.handler.update.UpdateUserAuthRequestHandler;
import controller.user.handler.update.UpdateUserInfoRequestHandler;
import controller.user.response.JSONUserResponseSenderImpl;
import controller.user.response.UserResponseSender;
import dao.user.UserDAO;

@SuppressWarnings("serial")
@MultipartConfig
public class AdvancedUserController extends HttpServlet {

	private List<UserRequestHandler> handlers;

	@Override
	public void init(ServletConfig config) throws ServletException {
		handlers = new ArrayList<>();
		UserDAO userDAO = (UserDAO) config.getServletContext().getAttribute("userDAO");
		UserRegistrationRequestHandler handler1 = new UserRegistrationRequestHandler(userDAO);
		GETUserByIdRequestHandler handler2 = new GETUserByIdRequestHandler(userDAO);
		GETUserPhotoRequestHandler handler3 = new GETUserPhotoRequestHandler(userDAO);
		GETUserAuthenticationRequestHandler handler4 = new GETUserAuthenticationRequestHandler(userDAO);
		UpdateUserInfoRequestHandler handler5 = new UpdateUserInfoRequestHandler(userDAO);
		UpdateUserAuthRequestHandler handler6 = new UpdateUserAuthRequestHandler(userDAO);
		DeleteUserRequestHandler handler7 = new DeleteUserRequestHandler(userDAO);
		GETUsersRequestHandler handler8 = new GETUsersRequestHandler(userDAO);
		GETMeRequestHandler handler9 = new GETMeRequestHandler(userDAO);
		handlers.add(handler1);
		handlers.add(handler2);
		handlers.add(handler3);
		handlers.add(handler4);
		handlers.add(handler5);
		handlers.add(handler6);
		handlers.add(handler7);
		handlers.add(handler8);
		handlers.add(handler9);
//		<filter>
//		<description>authorization filter</description>
//		<filter-name>auth-filter</filter-name>
//		<filter-class>filters.AuthorizationFilter</filter-class>
//	</filter>
//	<filter-mapping>
//		<filter-name>auth-filter</filter-name>
//		<url-pattern>/*</url-pattern>
//	</filter-mapping>
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		String accept = req.getHeader("Accept");
//		if (accept.startsWith("application/json")) {
//			req.setAttribute(UserResponseSender.class.getName(), new JSONUserResponseSenderImpl());
//		} else {
//			resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
//			return;
//		}
		req.setAttribute(UserResponseSender.class.getName(), new JSONUserResponseSenderImpl());

		for (UserRequestHandler handler : handlers) {
			if (handler.matches(req)) {
				handler.handle(req, resp);
			}
		}
	}

}
