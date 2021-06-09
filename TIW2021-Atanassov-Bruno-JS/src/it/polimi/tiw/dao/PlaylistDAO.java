package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Playlist;
import it.polimi.tiw.beans.User;

public class PlaylistDAO {
	private Connection connection;

	public PlaylistDAO(Connection connection) {
		this.connection = connection;
	}
	
	public int createPlaylist(String title, int user) throws SQLException{
		String query = "INSERT into playlist (name, userid)   VALUES(?, ?)";

		try (PreparedStatement pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
			pstatement.setString(1, title);
			pstatement.setInt(2, user);
			pstatement.executeUpdate();
			ResultSet generatedKeys = pstatement.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getInt(1);
			} else {
				throw new SQLException("Error while creating playlist has occurred. No IDs Returned");
			}		
		}
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
				playlist.setId(result.getInt("id"));
				playlist.setTitle(result.getString("name"));
				playlist.setUser(result.getInt("userid"));
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
		String query = "SELECT * FROM playlist where userid = ? order by date desc";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, user.getId());
			result = pstatement.executeQuery();
			while (result.next()) {
				Playlist playlist= new Playlist();
				playlist.setId(result.getInt("id"));
				playlist.setTitle(result.getString("name"));
				//playlist.setUser(user);
				playlist.setDate(new SimpleDateFormat( "yyyy-MM-dd" ).format(result.getDate("date")));
				playlists.add(playlist);
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