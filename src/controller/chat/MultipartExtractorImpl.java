package controller.chat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.io.IOUtils;

import model.chat.Chat;
import model.chat.Message;
import model.chat.Message.Property;
import model.chat.MessageFile;
import model.user.User;
import model.user.UserBuilder;

public class MultipartExtractorImpl implements BodyChatRequestExtractor {

//	@SuppressWarnings("preview")
//	@Override
//	public Map<Property, Object> extractMessage(HttpServletRequest request)
//			throws IOException, ServletException, SerialException, SQLException {
//		Map<Property, Object> map = new HashMap<>();
//		Collection<Part> parts = request.getParts();
//		for (Part part : parts) {
//			Property property = Message.Property.fromPartName(part.getName());
//			switch (property) {
//			case CONTENT -> {
//				map.put(property, IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8));
//			}
//			case FILE -> {
//				String fileName = part.getSubmittedFileName();
//				Blob fileData = new SerialBlob(IOUtils.toByteArray(part.getInputStream()));
//				MessageFile file = new MessageFile(fileName, fileData);
//				map.put(property, file);
//			}
//			default -> {
//			}
//			}
//		}
//		return map;
//	}
	@Override
	public Message extractMessage(HttpServletRequest request)
			throws IOException, ServletException, SerialException, SQLException {
		Collection<Part> parts = request.getParts();
		Map<Property, Object> map = new HashMap<>();
		for (Part part : parts) {
			Property property = Message.Property.fromPartName(part.getName());
			switch (property) {
			case CHAT_ID:
				map.put(property, Long.parseLong(toStr(part.getInputStream())));
				break;
			case CONTENT:
				map.put(property, toStr(part.getInputStream()));
				break;
			case FILE:
				String fileName = part.getSubmittedFileName();
				Blob fileData = new SerialBlob(IOUtils.toByteArray(part.getInputStream()));
				MessageFile file = new MessageFile(fileName, fileData);
				map.put(property, file);
				break;
			}
		}
		return new Message(map);
	}

	@Override
	public Chat extractChat(HttpServletRequest request) throws IOException, ServletException {
		Chat chat = new Chat();
		Collection<User> participiants = new ArrayList<>();
		chat.setParticipiants(participiants);
		Collection<Part> parts = request.getParts();
		for (Part part : parts) {
			String partName = part.getName();
			switch (partName) {
			case "type":
				chat.setType(IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8));
				break;
			case "participiant":
				UserBuilder builder = new UserBuilder();
				participiants.add(builder
						.id(Long.parseLong(IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8))).build());
				break;
			default:
				break;
			}
		}
		chat.setCreatedAt(Instant.now());
		return chat;
	}

	private String toStr(InputStream in) throws IOException {
		return IOUtils.toString(in, StandardCharsets.UTF_8);
	}

}
