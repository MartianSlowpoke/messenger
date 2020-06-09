package controller.message;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import json.MessageJsonMapper;
import model.chat.Message;
import model.chat.MessageFile;

public class JSONMessageResponseSender implements MessageResponseSender {

	@Override
	public void send(Message message, HttpServletResponse response) throws IOException {
		sendJson(200, response, MessageJsonMapper.messageToJson(message).toJSONString());
	}

	@Override
	public void sendFile(MessageFile file, HttpServletResponse response, ServletContext context)
			throws IOException, SQLException {
		InputStream in = file.getData().getBinaryStream();
		int fileLength = in.available();
		String mimeType = context.getMimeType(file.getName());
		response.setContentType(mimeType);
		response.setContentLength(fileLength);
		String headerKey = "Content-Disposition";
		String headerValue = String.format("filename=\"%s\"", file.getName());
		response.setHeader(headerKey, headerValue);
		IOUtils.copy(in, response.getOutputStream());
	}

	@Override
	public void send(Collection<Message> messages, HttpServletResponse response) throws IOException {
		sendJson(200, response, MessageJsonMapper.messagesToJson(messages));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendException(int code, String message, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		json.put("code", Integer.valueOf(code));
		json.put("message", message);
		sendJson(code, response, json.toJSONString());
	}

	private void sendJson(int statusCode, HttpServletResponse response, String json) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(statusCode);
		response.getWriter().write(json);
		response.getWriter().flush();
	}

}
