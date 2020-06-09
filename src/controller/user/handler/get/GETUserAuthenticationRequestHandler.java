package controller.user.handler.get;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.user.handler.UserRequestHandler;
import controller.user.response.UserResponseSender;
import dao.user.UserDAO;
import exception.UserDaoException;
import model.user.Authentication;
import model.user.User;

public class GETUserAuthenticationRequestHandler implements UserRequestHandler {

	private final String userAuthRegex = "^/[0-9]*/auth$";
	private final String idRegex = "\\d+";

	private UserDAO userDAO;

	public GETUserAuthenticationRequestHandler(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (request.getMethod().equals("GET")) {
			if (request.getPathInfo() != null) {
				if (Pattern.matches(userAuthRegex, request.getPathInfo())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserResponseSender sender = (UserResponseSender) request.getAttribute(UserResponseSender.class.getName());
		try {
			Long candidatUserId = getId(request.getPathInfo());
			if (isMethodAuthorized(request, candidatUserId)) {
				Authentication auth;
				auth = userDAO.getAuth(candidatUserId);
				sender.sendAuth(auth, response);
			} else {
				sender.sendException(HttpServletResponse.SC_FORBIDDEN,
						"you are not a user with such id " + candidatUserId, response);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_NOT_FOUND, e.getMessage(), response);
		} catch (UserDaoException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), response);
		}
	}

	private boolean isMethodAuthorized(HttpServletRequest request, Long requestedUserId) {
		HttpSession session = request.getSession(false);
		User sessionUser = (User) session.getAttribute("user");
		if (requestedUserId.equals(sessionUser.getId())) {
			return true;
		}
		return false;
	}

	private Long getId(String pathInfo) {
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(pathInfo);
		if (matcher.find()) {
			String group = matcher.group();
			return Long.parseLong(group);
		}
		return null;
	}

}
