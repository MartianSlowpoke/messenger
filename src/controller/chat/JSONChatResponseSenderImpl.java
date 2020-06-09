package controller.chat;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import json.ChatJsonMapper;
import model.chat.Chat;

public class JSONChatResponseSenderImpl implements ChatResponseSender {

	@Override
	public void send(Chat chat, HttpServletResponse response) throws IOException {
		String json = ChatJsonMapper.chatToJson(chat).toJSONString();
		sendJson(HttpServletResponse.SC_OK, response, json);
	}

	@Override
	public void sendChats(Collection<Chat> chats, HttpServletResponse response) throws IOException {
		sendJson(200, response, ChatJsonMapper.chatsToJson(chats));
	}

//	@Override
//	public void send(Message message, HttpServletResponse response) throws IOException {
//		sendJson(200, response, MessageJsonMapper.messageToJson(message).toJSONString());
//	}
//
//	@Override
//	public void sendMessages(Collection<Message> messages, HttpServletResponse response) throws IOException {
//		sendJson(200, response, MessageJsonMapper.messagesToJson(messages));
//	}
//
//	@Override
//	public void send(MessageFile file, HttpServletResponse response, ServletContext context) throws IOException {
//		try {
//			InputStream in = file.getData().getBinaryStream();
//			int fileLength = in.available();
//			String mimeType = context.getMimeType(file.getName());
//			response.setContentType(mimeType);
//			response.setContentLength(fileLength);
//			String headerKey = "Content-Disposition";
//			String headerValue = String.format("filename=\"%s\"", file.getName());
//			response.setHeader(headerKey, headerValue);
//			IOUtils.copy(in, response.getOutputStream());
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

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
