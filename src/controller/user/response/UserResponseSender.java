package controller.user.response;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import exception.UserDaoException;
import model.user.Authentication;
import model.user.User;
import model.user.UserPhoto;

public interface UserResponseSender {

	public void send(User user, HttpServletResponse response) throws IOException;

	public void send(Collection<User> users, HttpServletResponse response) throws IOException;

	public void sendException(int code, String message, HttpServletResponse response) throws IOException;

	public void sendAuth(Authentication auth, HttpServletResponse response) throws IOException;

	public void sendPhoto(UserPhoto user, HttpServletResponse response, ServletContext context)
			throws IOException, UserDaoException;

	public void sendExist(int code, String checkedObject, boolean isBusy, HttpServletResponse response)
			throws IOException;

}
