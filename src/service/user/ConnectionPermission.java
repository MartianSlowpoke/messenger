package service.user;

import org.java_websocket.handshake.ClientHandshake;

import model.user.User;

public interface ConnectionPermission {
	
	public User permit(ClientHandshake request) throws IllegalArgumentException;
	
}
