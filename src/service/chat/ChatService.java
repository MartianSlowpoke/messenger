package service.chat;

import java.util.Collection;

import model.chat.Chat;
import model.chat.Message;

public interface ChatService {

	public void add(Message message) throws ChatServiceException;

	public void add(Chat chat) throws ChatServiceException;

	public Collection<Chat> getChats(Long userId) throws ChatServiceException;

	public Collection<Message> getMessages(Long chatId) throws ChatServiceException;

	public Message getMessage(Long messageId) throws ChatServiceException;

	public void deleteMessage(Long messageId) throws ChatServiceException;

	public void deleteChat(Long chatId) throws ChatServiceException;

	public void setChatEventListener(ChatEventListener listener);

	public void setMessageEventListener(MessageEventListener listener);

}
