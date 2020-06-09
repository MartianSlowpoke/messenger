package service.chat;

import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import json.ChatJsonMapper;
import model.user.User;

public class ChatEventListenerImpl implements ChatEventListener {

	private WebSocketServer ws;

	public ChatEventListenerImpl(WebSocketServer ws) {
		this.ws = ws;
	}

	@Override
	public void onEvent(ChatEvent event) {
		String json = ChatJsonMapper.eventToJson(event).toJSONString();
		for (User recipient : event.getRecipients()) {
			send(json, recipient);
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
