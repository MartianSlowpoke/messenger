package controller.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import model.user.Authentication;
import model.user.AuthenticationBuilder;

public class AuthorizationHeaderExtractor {

	public static Authentication extract(String authHeader) {
		// Authorization: Basic base64credentials
		String base64Credentials = authHeader.substring("Basic".length()).trim();
		byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
		String credentials = new String(credDecoded, StandardCharsets.UTF_8);
		// credentials = username:password
		final String[] values = credentials.split(":", 2);
		return new AuthenticationBuilder().email(values[0]).password(values[1]).build();
	}
}
