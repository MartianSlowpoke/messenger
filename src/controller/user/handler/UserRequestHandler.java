package controller.user.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserRequestHandler {

	public boolean matches(HttpServletRequest request);

	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

}
