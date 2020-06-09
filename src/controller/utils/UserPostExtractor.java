package controller.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.io.IOUtils;

import model.user.Authentication;
import model.user.AuthenticationBuilder;
import model.user.User;
import model.user.UserBuilder;

public class UserPostExtractor {

	public static RegistrationPair obtain(Collection<Part> parts)
			throws IOException, IllegalArgumentException, SerialException, SQLException {
		UserBuilder userBuilder = new UserBuilder();
		AuthenticationBuilder authBuilder = new AuthenticationBuilder();
		for (Iterator<Part> iterator = parts.iterator(); iterator.hasNext();) {
			Part part = iterator.next();
			String partName = part.getName();
			switch (partName) {
			case "login":
				userBuilder.login(IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8));
				break;
			case "email":
				authBuilder.email(IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8));
				break;
			case "password":
				authBuilder.password(IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8));
				break;
			}
		}
		userBuilder.createdAt(Instant.now());
		userBuilder.isDeleted(Boolean.valueOf("false"));
		userBuilder.isOnline(Boolean.valueOf("false"));
		User user = userBuilder.build();
		Authentication auth = authBuilder.build();
		return new RegistrationPair(user, auth);
	}

}
