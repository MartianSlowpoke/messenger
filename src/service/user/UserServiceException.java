package service.user;

public class UserServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	private String message;
	private Integer httpCode;

	public UserServiceException(String message, Integer httpCode) {
		super();
		this.message = message;
		this.httpCode = httpCode;
	}

	public String getMessage() {
		return message;
	}

	public Integer getHttpCode() {
		return httpCode;
	}

}
