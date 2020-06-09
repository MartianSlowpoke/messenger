package controller.chat;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialException;

import model.chat.Chat;
import model.chat.Message;

public interface BodyChatRequestExtractor {

	public Message extractMessage(HttpServletRequest request)
			throws IOException, ServletException, SerialException, SQLException;

	public Chat extractChat(HttpServletRequest request) throws IOException, ServletException;

}
