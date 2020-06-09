package context;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import controller.chat.ChatResponseSender;
import controller.chat.JSONChatResponseSenderImpl;
import dao.chat.ChatDAO;
import dao.chat.ChatDAOImpl;
import dao.user.DatabaseUserDaoImpl;
import dao.user.UserDAO;
import service.chat.ChatEventListener;
import service.chat.ChatEventListenerImpl;
import service.chat.ChatService;
import service.chat.ChatServiceImpl;
import service.chat.MessageEventListener;
import service.chat.MessageEventListenerImpl;
import service.user.ConnectionPermissionImpl;
import service.user.UserEventListener;
import service.user.UserEventListenerImpl;
import service.user.UserServiceImpl;
import wsserver.MessengerUpdateServer;

public class ServletContextListenerImpl implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			UserDAO userDAO = new DatabaseUserDaoImpl();
			ChatDAO chatDAO = new ChatDAOImpl(userDAO);
			ChatService chatService = new ChatServiceImpl(chatDAO);
			UserServiceImpl userService = new UserServiceImpl(userDAO);
			MessengerUpdateServer wsServer = new MessengerUpdateServer(9090, userService,
					new ConnectionPermissionImpl());
			UserEventListener userEventListener = new UserEventListenerImpl(wsServer);
			userService.setUserEventListener(userEventListener);
			MessageEventListener messageHandler = new MessageEventListenerImpl(wsServer);
			ChatEventListener chatHandler = new ChatEventListenerImpl(wsServer);
			chatService.setMessageEventListener(messageHandler);
			chatService.setChatEventListener(chatHandler);

			ChatResponseSender chatSender = new JSONChatResponseSenderImpl();

			sce.getServletContext().setAttribute("chatResponseSender", chatSender);
			sce.getServletContext().setAttribute("chatDAO", chatDAO);
			sce.getServletContext().setAttribute("userDAO", userDAO);
			sce.getServletContext().setAttribute("wsServer", wsServer);
			sce.getServletContext().setAttribute("chatService", chatService);
			wsServer.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		MessengerUpdateServer wsServer;
		Object attributeObject = context.getAttribute("wsServer");
		wsServer = (MessengerUpdateServer) attributeObject;
		try {
			wsServer.stop();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
