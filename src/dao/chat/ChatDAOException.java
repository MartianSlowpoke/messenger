package dao.chat;

public class ChatDAOException extends Exception {

	private static final long serialVersionUID = 1L;

	private String message;

	public ChatDAOException(String message) {
		this.message = message;
	}

	public String getMessage(ChatDAOException this) {
		return this.message;
	}

}
