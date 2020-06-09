package dao.chat;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

import com.mysql.cj.jdbc.MysqlDataSource;

import dao.user.UserDAO;
import exception.UserDaoException;
import model.chat.Chat;
import model.chat.Message;
import model.chat.MessageFile;
import model.user.User;

public class ChatDAOImpl implements ChatDAO {

	private final String SQL_INSERT_PRIVATE_CHAT = "INSERT INTO chat (chat_type,created_at) VALUES (?,?);";
	private final String SQL_INSERT_PARTICIPIANT = "INSERT INTO participiant (fk_user, fk_chat, created_at) VALUES (?,?,?);";
	private final String SQL_INSERT_MESSAGE = "INSERT INTO message (fk_chat, fk_sender,content,created_at, fkFile) VALUES (?,?,?,?,?);";
	private final String SQL_INSERT_MESSAGE_FILE = "INSERT INTO messageFile (fileName,fileData) VALUES (?,?);";;

	private final String SQL_GET_PARTICIPIANTS = "SELECT fk_user,fk_chat FROM participiant WHERE fk_chat = ?;";
	private final String SQL_GET_CHATS_ID_OF_PARTICIPIANT = "SELECT fk_chat FROM participiant WHERE fk_user = ?;";
	private final String SQL_GET_CHAT_BY_ID = "SELECT chat_id, chat_type,fk_last_message,created_at FROM chat WHERE chat_id = ?;";
	private final String SQL_GET_CHAT_MESSAGES = "SELECT message_id FROM message WHERE fk_chat = ?;";
	private final String SQL_GET_MESSAGE_BY_ID = "SELECT message_id,fk_chat,fk_sender,content,fkFile,created_at FROM message WHERE message_id = ?";
	private final String SQL_GET_MESSAGE_FILE = "SELECT fileName,fileData FROM messageFile WHERE fileId = ?;";

	private MysqlDataSource source;
	private UserDAO userDAO;

	public ChatDAOImpl(UserDAO userDAO) {
		this.source = new MysqlDataSource();
		this.source.setUser("root");
		this.source.setPassword("1111");
		this.source.setDatabaseName("chat");
		this.source.setURL(source.getURL()
				+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true&useSSL=false");
		this.userDAO = userDAO;
	}

	@Override
	public void addChat(Chat chat) throws ChatDAOException {
		try {
			switch (chat.getType()) {
			case "private":
				Connection connection = source.getConnection();
				connection.setAutoCommit(false);
				try (PreparedStatement statement = getPreparedStatement(SQL_INSERT_PRIVATE_CHAT,
						Statement.RETURN_GENERATED_KEYS)) {
					setParams(statement, chat.getType(), Timestamp.from(chat.getCreatedAt()));
					statement.executeUpdate();
					chat.setChatId(getGeneratedKey(statement.getGeneratedKeys()));
				}
				try (PreparedStatement statement = getPreparedStatement(SQL_INSERT_PARTICIPIANT,
						Statement.NO_GENERATED_KEYS)) {
					for (User participiant : chat.getParticipiants()) {
						setParams(statement, new Object[] { participiant.getId(), chat.getChatId(),
								Timestamp.from(chat.getCreatedAt()) });
						statement.execute();
					}
				}
				chat.setParticipiants(getParticipiants(chat.getChatId()));
				connection.commit();
				break;
			default:
				throw new ChatDAOException("unsupported operation");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	@Override
	public void addMessage(Message message) throws ChatDAOException {
		try {
			Connection connection = source.getConnection();
			connection.setAutoCommit(false);
			Long fileId = null;
			if (message.hasFile()) {
				fileId = addMessageFile(message);
			}
			try (PreparedStatement statement = getPreparedStatement(SQL_INSERT_MESSAGE,
					Statement.RETURN_GENERATED_KEYS)) {
				setParams(statement, message.getChatId(), message.getSender().getId(), message.getContent(),
						Timestamp.from(message.getCreatedAt()), fileId);
				statement.execute();
				message.setMessageId(getGeneratedKey(statement.getGeneratedKeys()));
				connection.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}

	}

	private Long addMessageFile(Message message) throws SQLException {
		try (PreparedStatement statement = getPreparedStatement(SQL_INSERT_MESSAGE_FILE,
				Statement.RETURN_GENERATED_KEYS)) {
			setParams(statement, message.getFile().getName(), message.getFile().getData());
			statement.execute();
			Long fkFile = getGeneratedKey(statement.getGeneratedKeys());
			return fkFile;
		}

	}

//	@Override

	@Override
	public Collection<Chat> getChats(Long participiantId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_CHATS_ID_OF_PARTICIPIANT,
				Statement.NO_GENERATED_KEYS)) {
			Collection<Chat> chats = new ArrayList<>();
			setParams(statement, participiantId);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				chats.add(getChat(result.getLong("fk_chat")));
			}
			return chats;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	@Override
	public Collection<Message> getChatMessages(Long chatId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_CHAT_MESSAGES, Statement.NO_GENERATED_KEYS)) {
			Collection<Message> messages = new ArrayList<>();
			setParams(statement, chatId);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				messages.add(getMessage(result.getLong("message_id")));
			}
			return messages;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	private Long getGeneratedKey(ChatDAOImpl this, ResultSet resultSet) throws SQLException {
		resultSet.next();
		return resultSet.getLong(1);
	}

	private PreparedStatement getPreparedStatement(ChatDAOImpl this, String sql, int generatedKey) throws SQLException {
		return this.source.getConnection().prepareStatement(sql, generatedKey);
	}

	@Override
	public Chat getChat(Long chatId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_CHAT_BY_ID, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, chatId);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				String type = result.getString("chat_type");
				Instant createdAt = result.getTimestamp("created_at").toInstant();
				if (result.getLong("fk_last_message") != 0) {
					Message last = getMessage(result.getLong("fk_last_message"));
					Chat chat = new Chat(chatId, type, last, createdAt, getParticipiants(chatId));
					return chat;
				}
				return new Chat(chatId, type, null, createdAt, getParticipiants(chatId));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new ChatDAOException("unknown reason,bro");
	}

	@Override
	public Collection<User> getParticipiants(Long chatId) throws ChatDAOException {
		Collection<User> users = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_PARTICIPIANTS, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, chatId);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				users.add(userDAO.get(result.getLong("fk_user")));
			}
			return users;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UserDaoException e) {
			e.printStackTrace();
		}
		throw new ChatDAOException("unknown reason");
	}

	@Override
	public Message getMessage(Long messageId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_MESSAGE_BY_ID, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, messageId);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				User sender = userDAO.get(result.getLong("fk_sender"));
				String content = result.getString("content");
				Instant time = result.getTimestamp("created_at").toInstant();
				Long fkFile = result.getLong("fkFile");
				if (fkFile == 0) {
					return new Message(messageId, sender, content, time);
				}
				return new Message(messageId, sender, content, getMessageFile(fkFile), time);
			}
			throw new ChatDAOException("message by id " + messageId + " not found");
		} catch (SQLException | IllegalArgumentException | UserDaoException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	private MessageFile getMessageFile(Long fileId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_MESSAGE_FILE, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, fileId);
			ResultSet result = statement.executeQuery();
			result.next();
			String fileName = result.getString("fileName");
			Blob fileData = result.getBlob("fileData");
			return new MessageFile(fileName, fileData);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	private void setParams(PreparedStatement statement, Object... values) throws SQLException {
		for (int i = 0; i < values.length; i++) {
			statement.setObject(i + 1, values[i]);
		}
	}

}
