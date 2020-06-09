package controller.message;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.chat.BodyChatRequestExtractor;
import controller.chat.MultipartExtractorImpl;
import model.chat.Message;
import model.user.User;
import service.chat.ChatService;
import service.chat.ChatServiceException;

@MultipartConfig
public class MessageController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private ChatService chat;
	private MessagePathInfoMatcher matcher;
	private MessageResponseSender sender;
	private BodyChatRequestExtractor extractor;

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.matcher = new MessagePathInfoMatcherImpl();
		this.chat = (ChatService) config.getServletContext().getAttribute("chatService");
		this.sender = new JSONMessageResponseSender();
		this.extractor = new MultipartExtractorImpl();
	}

	@SuppressWarnings("incomplete-switch")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			MessagePathInfo path = matcher.match(request);
			switch (path) {
			case GET_CHAT_MESSAGES:
				Long chatId = Long.parseLong(request.getParameter("chatId"));
				Collection<Message> messages = chat.getMessages(chatId);
				sender.send(messages, response);
				break;
			case GET_MESSAGE_FILE:
				Long messageId = matcher.getMessageIdFromPathInfo(request.getPathInfo());
				Message message = chat.getMessage(messageId);
				if (message.hasFile())
					sender.sendFile(message.getFile(), response, request.getServletContext());
				else
					sender.sendException(HttpServletResponse.SC_NOT_FOUND,
							"message [" + messageId + "] doesn't have any file", response);
				break;
			case GET_MESSAGE:
				sender.send(chat.getMessage(matcher.getMessageIdFromPathInfo(request.getPathInfo())), response);
				break;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ChatServiceException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("incomplete-switch")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			MessagePathInfo path = matcher.match(request);
			switch (path) {
			case POST_MESSAGE:
				try {
					Message message = extractor.extractMessage(request);
					User sender = (User) request.getSession(false).getAttribute("user");
					message.setSender(sender);
					chat.add(message);
					this.sender.send(message, response);
				} catch (IOException | ServletException | SQLException e) {
					e.printStackTrace();
					sender.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), response);
				} catch (ChatServiceException e) {
					e.printStackTrace();
					sender.sendException(e.getHttpCode(), e.getMessage(), response);
				}
				break;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

}
