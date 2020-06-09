package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthorizationFilter implements Filter {

	/*
	 * the array of accessible URLs without authorization
	 * 
	 */
	private String[] authURIs;

	public AuthorizationFilter() {
		authURIs = new String[] { "/messenger/auth/logIn", "/messenger/auth/logOut" };
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse rep, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;
		final String requestURI = request.getRequestURI();
		if (isForAuth(requestURI)) {
			chain.doFilter(request, response);
		} else {
			if (isRegistration(request)) {
				chain.doFilter(req, rep);
			} else {
				HttpSession session = request.getSession(false);
				boolean isLoggedIn = (session != null) && (session.getAttribute("user") != null);
				if (isLoggedIn) {
					chain.doFilter(request, response);
				} else {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
							"you are not authorizated!to perform authorization, ping auth/logIn with the bearer authorization");
				}
			}
		}
	}

	private boolean isForAuth(String pathInfo) {
		for (String res : authURIs)
			if (res.equals(pathInfo))
				return true;
		return false;
	}

	private boolean isRegistration(HttpServletRequest req) {
		if (req.getMethod().equals("POST") && req.getRequestURI().toString().equals("/messenger/users"))
			return true;
		// check if an email of a login is busy
		if (req.getMethod().equals("GET") && req.getRequestURI().toString().equals("/messenger/users")) {
			boolean registration = Boolean.valueOf(req.getParameter("registration"));
			if (registration) {
				String login = req.getParameter("login");
				String email = req.getParameter("email");
				if (login != null || email != null)
					return true;
			}
		}
		return false;
	}

}
