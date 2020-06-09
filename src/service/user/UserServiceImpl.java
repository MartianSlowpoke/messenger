package service.user;

import java.time.Instant;

import dao.user.UserDAO;
import exception.UserDaoException;
import model.user.User;

public class UserServiceImpl implements UserService, UserStateConnectionListener {

	private UserDAO userDAO;
	private UserEventListener userListener;

	public UserServiceImpl(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public void onOpen(User user) {
		try {
			user.setIsOnline(true);
			userDAO.updateOnlineStatus(user);
			UserEvent event = new UserEvent("ONLINE_USER", userDAO.get(user.getId()), Instant.now(),
					"the user has just connected to the server", userDAO.getAll());
			notify(event);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UserDaoException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(User user) {
		try {
			user.setLastSeen(Instant.now().toString());
			user.setIsOnline(false);
			userDAO.updateOnlineStatus(user);
			UserEvent event = new UserEvent("OFFLINE_USER", userDAO.get(user.getId()), Instant.now(),
					"the user has just closed the connection", userDAO.getAll());
			notify(event);
		} catch (IllegalArgumentException | UserDaoException e) {
			e.printStackTrace();
		}
	}

	private void notify(UserEvent event) {
		if (userListener != null) {
			userListener.onEvent(event);
		}
	}

	@Override
	public void setUserEventListener(UserEventListener userListener) {
		this.userListener = userListener;
	}

}
