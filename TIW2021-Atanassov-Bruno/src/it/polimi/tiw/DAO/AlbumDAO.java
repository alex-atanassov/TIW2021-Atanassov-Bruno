package it.polimi.tiw.DAO;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.Part;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.User;

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
				album.setYear(result.getInt("year"));
				album.setArtist(result.getString("artist"));
				
				byte[] imgData = result.getBytes("image");
				String encodedImg = Base64.getEncoder().encodeToString(imgData);
				album.setImage(encodedImg);
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
	
	public List<Album> findAlbumsByUser(User user) throws SQLException {
		List<Album> albums = new ArrayList<>();
		String query = "SELECT * FROM album where userid = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, user.getId());
			result = pstatement.executeQuery();
			while (result.next()) {
				Album album = new Album();
				album.setId(result.getInt("id"));
				album.setName(result.getString("name"));
				album.setYear(result.getInt("year"));
				album.setArtist(result.getString("artist"));
				//set img
				albums.add(album);
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
		return albums;
	}
	
	public int createAlbum(String name, String artist, int year, Part image, int user) throws SQLException, IOException {
		String query = "INSERT into album (name, artist, year, image, userid) VALUES(?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
			pstatement.setString(1, name);
			pstatement.setString(2, artist);
			pstatement.setInt(3, year);
			pstatement.setBlob(4, image.getInputStream());
			pstatement.setInt(5,  user);
			pstatement.executeUpdate();
			ResultSet generatedKeys = pstatement.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getInt(1);
			} else {
				throw new SQLException("Error while creating album has occurred. No IDs Returned");
			}		
		}
	}
	
}
