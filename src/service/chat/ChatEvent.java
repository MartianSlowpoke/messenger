package service.chat;

import java.time.Instant;
import java.util.Collection;

import model.chat.Chat;
import model.user.User;

public class ChatEvent {

	private String type;
	private Chat chat;
	private Instant creationTime;
	private Collection<User> recipients;

	public ChatEvent(String type, Chat chat, Instant creationTime, Collection<User> recipients) {
		super();
		this.type = type;
		this.chat = chat;
		this.creationTime = creationTime;
		this.recipients = recipients;
	}

	public String getType() {
		return type;
	}

	public Chat getChat() {
		return chat;
	}

	public Instant getCreationTime() {
		return creationTime;
	}

	public Collection<User> getRecipients() {
		return recipients;
	}

}
