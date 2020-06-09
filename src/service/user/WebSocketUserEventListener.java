package service.user;

import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import json.UserJsonMapper;
import model.user.User;

public class WebSocketUserEventListener implements UserEventListener {

	private WebSocketServer ws;

	public WebSocketUserEventListener(WebSocketServer ws) {
		this.ws = ws;
	}

	@Override
	public void onEvent(UserEvent event) {
		String payload = UserJsonMapper.eventToJson(event);
		for (User recipient : event.getRecipients()) {
			send(payload, recipient);
		}
	}

	private void send(String payload, User receiver) {
		Collection<WebSocket> clients = ws.getConnections();
		for (WebSocket w : clients) {
			User attached = w.getAttachment();
			if (attached.equals(receiver)) {
				w.send(payload);
			}
		}
	}

}
