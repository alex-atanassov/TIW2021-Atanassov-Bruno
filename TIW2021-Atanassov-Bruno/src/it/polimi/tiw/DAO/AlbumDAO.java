package it.polimi.tiw.DAO;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	public int createAlbum(String name, String artist, int year, Part image, int user) throws SQLException {
		String query = "INSERT into album (name, artist, year, image, userid) VALUES(?, ?, ?, ?, ?)";
		int code = 0;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, name);
			pstatement.setString(2, artist);
			pstatement.setInt(3, year);
			pstatement.setBlob(4, image.getInputStream());
			pstatement.setInt(5,  user);
			code = pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public int findNewAlbum(int userid) throws SQLException {
		int id = -1;
		String query = "SELECT max(id) as maxid FROM album where userid = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, userid);
			result = pstatement.executeQuery();
			while (result.next()) {
				id = result.getInt("maxid");
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
		return id;
	}
}
