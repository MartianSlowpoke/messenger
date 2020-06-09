package controller.user.response;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.simple.JSONObject;

import exception.UserDaoException;
import json.UserJsonMapper;
import model.user.Authentication;
import model.user.User;
import model.user.UserPhoto;

public class JSONUserResponseSenderImpl implements UserResponseSender {

	@Override
	public void send(User user, HttpServletResponse response) throws IOException {
		sendJson(200, response, UserJsonMapper.toJSON(user));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendException(int code, String message, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		json.put("code", Integer.valueOf(code));
		json.put("message", message);
		sendJson(code, response, json.toJSONString());
	}

	@Override
	public void sendPhoto(UserPhoto user, HttpServletResponse response, ServletContext context)
			throws IOException, UserDaoException {
		try {
			InputStream in = user.getFileData().getBinaryStream();
			int fileLength = in.available();
			String mimeType = context.getMimeType(user.getFileName());
			response.setContentType(mimeType);
			response.setContentLength(fileLength);
			String headerKey = "Content-Disposition";
			String headerValue = String.format("filename=\"%s\"", user.getFileName());
			response.setHeader(headerKey, headerValue);
			IOUtils.copy(in, response.getOutputStream());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserDaoException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendExist(int code, String checkedObject, boolean isBusy, HttpServletResponse response)
			throws IOException {
		JSONObject json = new JSONObject();
		json.put("code", Integer.valueOf(code));
		json.put("checked object", checkedObject);
		json.put("exist", isBusy);
		sendJson(code, response, json.toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void send(Collection<User> users, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		json.put("users", UserJsonMapper.toJSONArray(users));
		sendJson(200, response, json.toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendAuth(Authentication auth, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		json.put("email", auth.getEmail());
		json.put("password", auth.getPassword());
		sendJson(200, response, json.toJSONString());
	}

	private void sendJson(int statusCode, HttpServletResponse response, String json) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(statusCode);
		response.getWriter().write(json);
		response.getWriter().flush();
	}

}
