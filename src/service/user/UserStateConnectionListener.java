package service.user;

import model.user.User;

public interface UserStateConnectionListener {

	public void onOpen(User user);

	public void onClose(User user);

}
