package dao.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

import model.user.UserPhoto;
import model.user.UserPhotoBuilder;

public class UserPhotoHandler implements ResultSetHandler<UserPhoto> {

	@Override
	public UserPhoto handle(ResultSet resultSet) throws SQLException {
		if (resultSet.next()) {
			UserPhotoBuilder builder = new UserPhotoBuilder();
			return builder.fileName(resultSet.getString("fileName")).fileData(resultSet.getBlob("fileData")).build();
		}
		return null;
	}

}
