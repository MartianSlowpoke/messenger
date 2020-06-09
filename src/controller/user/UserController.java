package controller.user;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.user.response.JSONUserResponseSenderImpl;
import controller.user.response.UserResponseSender;
import controller.utils.ClientAuthorizationService;
import controller.utils.RegistrationPair;
import controller.utils.UserDeleteCase;
import controller.utils.UserDeleteRequestMatcher;
import controller.utils.UserGetRequestMatcher;
import controller.utils.UserPostExtractor;
import controller.utils.UserUpdateCase;
import controller.utils.UserUpdateExtractor;
import controller.utils.UserUpdateRequestMatcher;
import dao.user.UserDAO;
import exception.DaoIllegalException;
import exception.UserDaoException;
import model.user.Authentication;
import model.user.User;
import model.user.UserPhoto;

@MultipartConfig
@Deprecated
public class UserController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private UserDAO userDAO;
	private UserResponseSender userSender;

	public UserController() {
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.userDAO = (UserDAO) config.getServletContext().getAttribute("userDAO");
		this.userSender = new JSONUserResponseSenderImpl();
	}

	// users/*
	/*
	 * get user authentication credentials /messenger/users/{id}/auth
	 * 
	 * get user information /messenger/users/{id}
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			switch (UserGetRequestMatcher.match(req.getPathInfo())) {
			// users/{id}
			case ID:
				processID(req, resp);
				break;
			// users?login=Sck
			case SEARCH:
				processSearch(req, resp);
				break;
			// users/{id}/photo
			case PHOTO:
				processPhoto(req, resp);
				break;
			// users/{id}/authentication
			case AUTH:
				processAuth(req, resp);
				break;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			userSender.sendException(HttpServletResponse.SC_BAD_REQUEST, "path info = " + req.getPathInfo(), resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			RegistrationPair registrPair = UserPostExtractor.obtain(req.getParts());
			userDAO.add(registrPair.getUser(), registrPair.getAuth());
			// create the http session
			// put the user as the session attribute
			// prepare the cookie for further session identification
			ClientAuthorizationService.auth(req, resp, registrPair.getUser(), 60 * 15);
			userSender.send(registrPair.getUser(), resp);
		} catch (IllegalArgumentException | SQLException e) {
			userSender.sendException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), resp);
			e.printStackTrace();
		} catch (ServletException | UserDaoException e) {
			userSender.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), resp);
			e.printStackTrace();
		}
	}

	/*
	 * update authentication credentials /messenger/users/{id}/auth
	 * 
	 * update user information /messenger/users/{id}
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			UserUpdateCase type = UserUpdateRequestMatcher.match(req.getPathInfo());
			switch (type) {
			case UPDATE_INFO:
				// a user id to be modified
				// a user represented by a client
				if (isMethodAuthorized(req, UserUpdateRequestMatcher.extractId(req.getPathInfo()))) {
					User extractedUser = UserUpdateExtractor.extractUser(req.getParts());
					extractedUser.setId(UserUpdateRequestMatcher.extractId(req.getPathInfo()));
					userDAO.update(extractedUser);
					userSender.send(userDAO.get(extractedUser.getId()), resp);
				} else {
					userSender.sendException(HttpServletResponse.SC_FORBIDDEN,
							"you are not a user with such id " + UserUpdateRequestMatcher.extractId(req.getPathInfo()),
							resp);
				}
				break;
			case UPDATE_AUTH:
				if (isMethodAuthorized(req, UserUpdateRequestMatcher.extractId(req.getPathInfo()))) {
					Authentication auth = UserUpdateExtractor.extractAuth(req.getParts());
					auth.setUserId(UserUpdateRequestMatcher.extractId(req.getPathInfo()));
					userDAO.update(auth);
				} else {
					userSender.sendException(HttpServletResponse.SC_FORBIDDEN,
							"you are not a user with such id " + UserUpdateRequestMatcher.extractId(req.getPathInfo()),
							resp);
				}
				break;
			}
			// pathInfo is null
		} catch (IllegalArgumentException e) {
			userSender.sendException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), resp);
			e.printStackTrace();
		} catch (UserDaoException e) {
			userSender.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), resp);
			e.printStackTrace();
		} catch (SQLException e) {
			userSender.sendException(HttpServletResponse.SC_BAD_REQUEST, "bad file : " + e.getMessage(), resp);
			e.printStackTrace();
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			UserDeleteCase type = UserDeleteRequestMatcher.match(req.getPathInfo());
			switch (type) {
			case DELETE:
				try {
					Long userId = UserDeleteRequestMatcher.extractId(req.getPathInfo());
					if (isMethodAuthorized(req, userId)) {
						User deletedUser = userDAO.get(userId);
						userDAO.delete(deletedUser);
						ClientAuthorizationService.expire(req, resp);
					} else {
						userSender.sendException(HttpServletResponse.SC_FORBIDDEN,
								"you are not a user with such id " + userId, resp);
					}
				} catch (UserDaoException e) {
					e.printStackTrace();
					userSender.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), resp);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					userSender.sendException(HttpServletResponse.SC_NOT_FOUND, e.getMessage(), resp);
				}
				break;
			}
			// users/
			// users/{id}/sda
			// users
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			userSender.sendException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), resp);

		}
	}

	private void processID(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			Long userId = UserGetRequestMatcher.extractId(req.getPathInfo());
			User user = userDAO.get(userId);
			userSender.send(user, resp);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			DaoIllegalException exception = DaoIllegalException.fromMessage(e.getMessage());
			switch (exception) {
			case NOT_FOUND_BY_USER_ID:
				userSender.sendException(HttpServletResponse.SC_NOT_FOUND, e.getMessage(), resp);
				break;
			default:
				break;
			}
		} catch (UserDaoException e) {
			e.printStackTrace();
			userSender.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), resp);
		}
	}

	private void processPhoto(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			Long userId = UserGetRequestMatcher.extractId(req.getPathInfo());
			UserPhoto userPhoto = userDAO.getPhoto(userId);
			userSender.sendPhoto(userPhoto, resp, req.getServletContext());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			userSender.sendException(HttpServletResponse.SC_NOT_FOUND, e.getMessage(), resp);
		} catch (UserDaoException e) {
			e.printStackTrace();
			userSender.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), resp);
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

	// users?login=Mylogin&registration=true
	// users?email=myEmail&registration=true
	private void processSearch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			boolean registration = Boolean.valueOf(req.getParameter("registration"));
			if (registration) {
				String login = req.getParameter("login");
				if (login != null) {
					boolean isBusy = userDAO.existLogin(login);
					userSender.sendExist(obtainCode(isBusy), "login", isBusy, resp);
					return;
				}
				String email = req.getParameter("email");
				if (email != null) {
					boolean isBusy = userDAO.existEmail(email);
					userSender.sendExist(obtainCode(isBusy), "email", isBusy, resp);
					return;
				}
			} else {
				// return all users
				Collection<User> users = userDAO.getAll();
				userSender.send(users, resp);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			DaoIllegalException exception = DaoIllegalException.fromMessage(e.getMessage());
			switch (exception) {
			case NO_USERS:
				userSender.sendException(HttpServletResponse.SC_NOT_FOUND, exception.toString(), resp);
				break;
			default:
				break;
			}
		} catch (UserDaoException e) {
			userSender.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), resp);
			e.printStackTrace();
		}
	}

	private void processAuth(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			Long userId = UserGetRequestMatcher.extractId(req.getPathInfo());
			if (this.isMethodAuthorized(req, userId)) {
				Authentication auth = this.userDAO.getAuth(userId);
				userSender.sendAuth(auth, resp);
			} else {
				userSender.sendException(HttpServletResponse.SC_FORBIDDEN, "you are not a user with such id " + userId,
						resp);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UserDaoException e) {
			e.printStackTrace();
		}
	}

	private int obtainCode(boolean isBusy) {
		if (isBusy) {
			return HttpServletResponse.SC_OK;
		} else {
			return HttpServletResponse.SC_NOT_FOUND;
		}
	}

}
