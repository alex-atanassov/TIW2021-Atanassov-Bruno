package it.polimi.tiw.DAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.beans.Playlist;
import it.polimi.tiw.beans.Track;
import it.polimi.tiw.beans.User;

public class PlaylistDAO {
	private Connection connection;

	public PlaylistDAO(Connection connection) {
		this.connection = connection;
	}
	
	public int createPlaylist(String title, int user, Date date) throws SQLException{
		String query = "INSERT into Playlist (title, userid, date)   VALUES(?, ?, ?)";

		int code = 0;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, title);
			pstatement.setInt(2, user);
			pstatement.setDate(3, (java.sql.Date) date);
			code = pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e) {
				throw new SQLException("Cannot close statement");
			}
		}
		return code;
	}
	
	public Playlist findPlaylistByTitle(String Title) throws SQLException {
		
		Playlist playlist = null;
		String query = "SELECT * FROM Playlist where title = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			//TODO don't if setString is correct
			pstatement.setString(1, Title);
			result = pstatement.executeQuery();
			while (result.next()) {
				playlist= new Playlist();
				playlist.setTitle(result.getString("title"));
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
			} catch (Exception e) {
				throw new SQLException("Cannot close statement");
			}
		}
		return playlist;
	}
	
	public Playlist findPlaylistById(int id) throws SQLException {
		Playlist playlist = null;
		String query = "SELECT * FROM playlist where id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id);
			result = pstatement.executeQuery();
			while (result.next()) {
				playlist = new Playlist();
				playlist.setid(result.getInt("id"));
				playlist.setTitle(result.getString("title"));
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
			} catch (Exception e) {
				throw new SQLException("Cannot close statement");
			}
		}
		return playlist;
	}
	
	public List<Playlist> findPlaylistsByUser(User user) throws SQLException {
		
		List<Playlist> playlists = new ArrayList<Playlist>();
		//TODO check query
		String query = "SELECT * FROM Playlist where user = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, user.getId());
			result = pstatement.executeQuery();
			while (result.next()) {
				Playlist playlist= new Playlist();
				playlist.setTitle(result.getString("title"));
				playlist.setUser(user);
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
			} catch (Exception e) {
				throw new SQLException("Cannot close statement");
			}
		}
		return playlists;
	}
}