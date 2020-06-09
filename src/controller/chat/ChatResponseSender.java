package controller.chat;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import model.chat.Chat;

public interface ChatResponseSender {

	public void send(Chat chat, HttpServletResponse response) throws IOException;

	public void sendChats(Collection<Chat> chats, HttpServletResponse response) throws IOException;;

	public void sendException(int code, String message, HttpServletResponse response) throws IOException;

}
