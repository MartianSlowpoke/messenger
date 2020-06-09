package dao.chat;

import java.util.Collection;

import model.chat.Chat;
import model.chat.Message;
import model.user.User;

public class MessageListenerImpl implements MessageListener {

	@Override
	public void onNewMessage(Chat chat, Message message, Collection<User> recipients) {
		System.out.println("chat = " + chat.toString());
		System.out.println("message = " + message.toString());
		System.out.println("----");
		for (User user : recipients) {
			System.out.println(user.toString());
		}
		System.out.println("----");
	}

	@Override
	public void onUpdatedMessage(Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeletedMessage(Chat chat, Message message, Collection<User> recipients) {
		// TODO Auto-generated method stub

	}

}
