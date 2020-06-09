package controller.user.handler.get;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.user.handler.UserRequestHandler;
import controller.user.response.UserResponseSender;
import dao.user.UserDAO;
import exception.UserDaoException;
import model.user.User;

public class GETUserByIdRequestHandler implements UserRequestHandler {

	private final String matchPathInfo = "^/[0-9]*$";
	private final String regexGetId = "\\d+";

	private UserDAO userDAO;

	public GETUserByIdRequestHandler(UserDAO userDAO) {
		super();
		this.userDAO = userDAO;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (request.getMethod().equals("GET")) {
			if (request.getPathInfo() != null) {
				if (Pattern.matches(matchPathInfo, request.getPathInfo())) {
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
			Long id = getId(request.getPathInfo());
			User user = userDAO.get(id);
			sender.send(user, response);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_NOT_FOUND, e.getMessage(), response);
		} catch (UserDaoException e) {
			e.printStackTrace();
		}
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
