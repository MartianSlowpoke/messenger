package dao.chat;

import java.util.Collection;

import model.chat.Chat;
import model.chat.Message;
import model.user.User;

public interface ChatDAO {

	public void addChat(Chat chat) throws ChatDAOException;

	public void addMessage(Message message) throws ChatDAOException;

	public Chat getChat(Long chatId) throws ChatDAOException;

	public Message getMessage(Long messageId) throws ChatDAOException;

	public Collection<Chat> getChats(Long participiantId) throws ChatDAOException;

	public Collection<Message> getChatMessages(Long chatId) throws ChatDAOException;

	public Collection<User> getParticipiants(Long chatId) throws ChatDAOException;

}
