package controller.message;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import model.chat.Message;
import model.chat.MessageFile;

public interface MessageResponseSender {

	public void send(Message message, HttpServletResponse response) throws IOException;

	public void sendFile(MessageFile file, HttpServletResponse response, ServletContext context) throws IOException, SQLException;

	public void send(Collection<Message> messages, HttpServletResponse response) throws IOException;

	public void sendException(int code, String message, HttpServletResponse response) throws IOException;

}
