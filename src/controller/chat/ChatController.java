package controller.chat;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.chat.Chat;
import model.user.User;
import service.chat.ChatService;
import service.chat.ChatServiceException;

/*
 * controller to manage a chat resource
 * 
 * It is used to perform posting , getting , updating and deleting a chat
 */
@MultipartConfig
public class ChatController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ChatService chat;
	private ChatPathInfoMatcher matcher;
	private ChatResponseSender sender;
	private BodyChatRequestExtractor extractor;

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.chat = (ChatService) config.getServletContext().getAttribute("chatService");
		this.matcher = new ChatPathInfoMatcherImpl();
		this.sender = new JSONChatResponseSenderImpl();
		this.extractor = new MultipartExtractorImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			ChatPathInfo path = matcher.match(req);
			switch (path) {
			case GET_MY_CHATS:
				User user = (User) req.getSession(false).getAttribute("user");
				Collection<Chat> chats = chat.getChats(user.getId());
				sender.sendChats(chats, resp);
				break;
			default:
				sender.sendException(HttpServletResponse.SC_BAD_REQUEST, "no valid request URL  ", resp);
				break;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), resp);
		} catch (ChatServiceException e) {
			e.printStackTrace();
			sender.sendException(e.getHttpCode(), e.getMessage(), resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			ChatPathInfo path = matcher.match(req);
			switch (path) {
			case POST_CHAT:
				Chat inChat = extractor.extractChat(req);
				chat.add(inChat);
				sender.send(inChat, resp);
				break;
			default:
				sender.sendException(HttpServletResponse.SC_BAD_REQUEST, "no valid request URL  ", resp);
				break;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			sender.sendException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), resp);
		} catch (ChatServiceException e) {
			e.printStackTrace();
			sender.sendException(e.getHttpCode(), e.getMessage(), resp);
		}
	}

}
