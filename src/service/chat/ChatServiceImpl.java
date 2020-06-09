package service.chat;

import java.time.Instant;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import dao.chat.ChatDAO;
import dao.chat.ChatDAOException;
import model.chat.Chat;
import model.chat.Message;

public class ChatServiceImpl implements ChatService {

	private ChatDAO chatDAO;
	private ChatEventListener chatHandler;
	private MessageEventListener messageHandler;

	public ChatServiceImpl(ChatDAO chatDAO) {
		this.chatDAO = chatDAO;
	}

	@Override
	public void add(Message message) throws ChatServiceException {
		try {
			chatDAO.addMessage(message);
			Chat chat = chatDAO.getChat(message.getChatId());
			MessageEvent event = new MessageEvent("NEW_MESSAGE", message, chat, Instant.now(),
					chatDAO.getParticipiants(message.getChatId()));
			notify(event);
		} catch (ChatDAOException e) {
			e.printStackTrace();
			throw new ChatServiceException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add(Chat chat) throws ChatServiceException {
		try {
			chatDAO.addChat(chat);
			ChatEvent event = new ChatEvent("NEW_CHAT", chat, Instant.now(),
					chatDAO.getParticipiants(chat.getChatId()));
			notify(event);
		} catch (ChatDAOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Collection<Chat> getChats(Long userId) throws ChatServiceException {
		try {
			Collection<Chat> chats = chatDAO.getChats(userId);
			return chats;
		} catch (ChatDAOException e) {
			e.printStackTrace();
			throw new ChatServiceException(500, e.getMessage());
		}
	}

	@Override
	public Collection<Message> getMessages(Long chatId) throws ChatServiceException {
		try {
			Collection<Message> messages = chatDAO.getChatMessages(chatId);
			return messages;
		} catch (ChatDAOException e) {
			e.printStackTrace();
			throw new ChatServiceException(500, e.getMessage());
		}
	}

	@Override
	public Message getMessage(Long messageId) throws ChatServiceException {
		try {
			Message message = chatDAO.getMessage(messageId);
			return message;
		} catch (ChatDAOException e) {
			e.printStackTrace();
			throw new ChatServiceException(500, e.getMessage());
		}
	}

	@Override
	public void deleteMessage(Long messageId) throws ChatServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteChat(Long chatId) throws ChatServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setChatEventListener(ChatEventListener listener) {
		chatHandler = listener;
	}

	@Override
	public void setMessageEventListener(MessageEventListener listener) {
		messageHandler = listener;
	}

	private void notify(MessageEvent messageEvent) {
		messageHandler.onEvent(messageEvent);
	}

	private void notify(ChatEvent chatEvent) {
		chatHandler.onEvent(chatEvent);
	}

}
