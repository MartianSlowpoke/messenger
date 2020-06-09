package controller.user.handler.delete;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.user.handler.UserRequestHandler;
import controller.user.response.UserResponseSender;
import controller.utils.ClientAuthorizationService;
import dao.user.UserDAO;
import exception.UserDaoException;
import model.user.User;

public class DeleteUserRequestHandler implements UserRequestHandler {

	private final String matchPathInfo = "^/[0-9]*$";
	private final String regexGetId = "\\d+";
	private UserDAO userDAO;

	public DeleteUserRequestHandler(UserDAO userDAO) {
		super();
		this.userDAO = userDAO;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (request.getMethod().equals("DELETE")) {
			if (request.getPathInfo() != null) {
				if (Pattern.matches(matchPathInfo, request.getPathInfo())) {
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
			Long candidatUserId = getId(request.getPathInfo());
			if (isMethodAuthorized(request, candidatUserId)) {
				User deletedUser = userDAO.get(candidatUserId);
				userDAO.delete(deletedUser);
				ClientAuthorizationService.expire(request, response);
			} else {
				sender.sendException(HttpServletResponse.SC_FORBIDDEN,
						"you are not a user with such id " + candidatUserId, response);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), response);
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
		Pattern pattern = Pattern.compile(regexGetId);
		Matcher matcher = pattern.matcher(pathInfo);
		if (matcher.find()) {
			String group = matcher.group();
			return Long.parseLong(group);
		}
		return null;
	}

}
