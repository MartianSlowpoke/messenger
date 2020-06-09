package controller.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.user.User;

public class ClientAuthorizationService {

	public static void auth(HttpServletRequest request, HttpServletResponse response, User user, int cookieMaxAge) {
		HttpSession session = request.getSession(true);
		session.setAttribute("user", user);
		Cookie cookie = new Cookie("JSESSIONID", session.getId());
		cookie.setMaxAge(60 * 15);
		response.addCookie(cookie);
	}

	public static void expire(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("user") != null) {
			session.invalidate();
		}
		eraseCookie(request, response);
	}

	private static void eraseCookie(HttpServletRequest req, HttpServletResponse resp) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies) {
				cookie.setValue("");
				cookie.setPath("/");
				cookie.setMaxAge(0);
				resp.addCookie(cookie);
			}
	}

}
