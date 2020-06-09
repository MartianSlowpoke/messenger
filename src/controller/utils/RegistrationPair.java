package controller.utils;

import model.user.Authentication;
import model.user.User;

public class RegistrationPair {

	private User user;
	private Authentication auth;

	public RegistrationPair(User user, Authentication auth) {
		this.user = user;
		this.auth = auth;
	}

	public User getUser() {
		return user;
	}

	public Authentication getAuth() {
		return auth;
	}

}
