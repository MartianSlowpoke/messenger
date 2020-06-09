package controller.user.handler.update;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.user.handler.UserRequestHandler;
import controller.user.response.UserResponseSender;
import controller.utils.UserUpdateExtractor;
import controller.utils.UserUpdateRequestMatcher;
import dao.user.UserDAO;
import exception.UserDaoException;
import model.user.Authentication;
import model.user.User;

public class UpdateUserAuthRequestHandler implements UserRequestHandler {

	private final String userAuthRegex = "^/[0-9]*/auth$";
	private final String idRegex = "\\d+";
	private UserDAO userDAO;

	public UpdateUserAuthRequestHandler(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (request.getMethod().equals("PUT")) {
			if (request.getContentType().startsWith("multipart/form-data")) {
				if (request.getPathInfo() != null) {
					if (Pattern.matches(userAuthRegex, request.getPathInfo())) {
						return true;
					}
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
				Authentication auth = UserUpdateExtractor.extractAuth(request.getParts());
				auth.setUserId(UserUpdateRequestMatcher.extractId(request.getPathInfo()));
				userDAO.update(auth);
				sender.sendAuth(userDAO.getAuth(auth.getUserId()), response);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UserDaoException e) {
			e.printStackTrace();
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
