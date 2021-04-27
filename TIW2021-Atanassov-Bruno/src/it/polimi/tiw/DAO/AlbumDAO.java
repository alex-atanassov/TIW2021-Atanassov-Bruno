package it.polimi.tiw.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.time.ZoneId;
import java.util.Date;

import it.polimi.tiw.beans.Album;

public class AlbumDAO {
	private Connection connection;

	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Album findAlbumById(int id) throws SQLException {
		Album album = null;
		String query = "SELECT * FROM album where id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id);
			result = pstatement.executeQuery();
			while (result.next()) {
				album = new Album();
				album.setId(result.getInt("id"));
				album.setName(result.getString("name"));
				//album.setYear(result.getDate("year"));
			}
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement");
			}
		}
		return album;
	}
	
	public int createAlbum(String name, Year year) throws SQLException {
		String query = "INSERT into album (name, year) VALUES(?, ?)";
		int code = 0;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, name);
			pstatement.setObject(2, year);
			code = pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {

			}
		}
		return code;
	}
}
