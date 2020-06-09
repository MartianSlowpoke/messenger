package controller.user.handler.update;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.user.handler.UserRequestHandler;
import controller.user.response.UserResponseSender;
import controller.utils.UserUpdateExtractor;
import dao.user.UserDAO;
import exception.UserDaoException;
import model.user.User;

public class UpdateUserInfoRequestHandler implements UserRequestHandler {

	private final String matchIdRegex = "^/[0-9]*$";
	private final String idRegex = "\\d+";

	private UserDAO userDAO;

	public UpdateUserInfoRequestHandler(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (request.getMethod().equals("PUT")) {
			if (request.getContentType().startsWith("multipart/form-data")) {
				if (request.getPathInfo() != null) {
					if (Pattern.matches(matchIdRegex, request.getPathInfo())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserResponseSender sender = (UserResponseSender) request.getAttribute(UserResponseSender.class.getName());
		Long candidatUserId = getId(request.getPathInfo());
		try {
			if (isRequestAuthorized(request, candidatUserId)) {
				User extractedUser = UserUpdateExtractor.extractUser(request.getParts());
				extractedUser.setId(candidatUserId);
				userDAO.update(extractedUser);
				sender.send(userDAO.get(extractedUser.getId()), response);
			} else {
				sender.sendException(HttpServletResponse.SC_FORBIDDEN,
						"you are not a user with such id " + candidatUserId, response);
			}
		} catch (IOException | SQLException | ServletException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), response);
		} catch (UserDaoException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), response);
		}
	}

	private boolean isRequestAuthorized(HttpServletRequest request, Long requestedUserId) {
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
