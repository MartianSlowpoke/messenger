package dao.chat;

import java.util.Collection;

import model.chat.Chat;
import model.chat.Message;
import model.user.User;

public interface MessageListener {

	public void onNewMessage(Chat chat,Message message, Collection<User> recipients);

	public void onUpdatedMessage(Message message);

	public void onDeletedMessage(Chat chat,Message message,Collection<User> recipients);

}
