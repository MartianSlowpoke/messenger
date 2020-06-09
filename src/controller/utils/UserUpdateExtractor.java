package controller.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.io.IOUtils;

import model.user.Authentication;
import model.user.AuthenticationBuilder;
import model.user.User;
import model.user.UserBuilder;
import model.user.UserPhoto;
import model.user.UserPhotoBuilder;

public class UserUpdateExtractor {

	public static Authentication extractAuth(Collection<Part> parts) throws IOException {
		AuthenticationBuilder authBuilder = new AuthenticationBuilder();
		for (Iterator<Part> iterator = parts.iterator(); iterator.hasNext();) {
			Part part = iterator.next();
			String partName = part.getName();
			switch (partName) {
			case "email":
				authBuilder.email(IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8));
				break;
			case "password":
				authBuilder.password(IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8));
				break;
			}
		}
		return authBuilder.build();
	}

	public static User extractUser(Collection<Part> parts) throws IOException, SerialException, SQLException {
		UserBuilder userBuilder = new UserBuilder();
		UserPhotoBuilder photoBuilder = new UserPhotoBuilder();
		for (Iterator<Part> iterator = parts.iterator(); iterator.hasNext();) {
			Part part = iterator.next();
			String partName = part.getName();
			switch (partName) {
			case "login":
				userBuilder.login(IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8));
				break;
			case "firstName":
				userBuilder.firstName(IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8));
				break;
			case "lastName":
				userBuilder.lastName(IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8));
				break;
			case "description":
				userBuilder.description(IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8));
				break;
			case "photo":
				photoBuilder.fileName(part.getSubmittedFileName());
				photoBuilder.fileData(new SerialBlob(IOUtils.toByteArray(part.getInputStream())));
				break;
			}
		}
		UserPhoto photo = photoBuilder.build();
		User user = userBuilder.build();
		user.setPhoto(photo);
		return user;
	}

}
