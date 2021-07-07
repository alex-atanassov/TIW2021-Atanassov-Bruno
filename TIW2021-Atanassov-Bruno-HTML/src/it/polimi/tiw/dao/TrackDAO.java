package it.polimi.tiw.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import javax.servlet.http.Part;

import it.polimi.tiw.beans.Track;

public class TrackDAO {
	private Connection connection;

	public TrackDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Track findTrackById(int id) throws SQLException {
		Track track = null;
		String query = "SELECT * FROM track where id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id);
			result = pstatement.executeQuery();
			while (result.next()) {
				track = new Track();
				track.setId(result.getInt("id"));
				track.setTitle(result.getString("title"));
				track.setAlbum(result.getInt("albumid"));
				track.setGenre(result.getString("genre"));
				track.setUser(result.getInt("userid"));
				
				byte[] data = result.getBytes("audio");
				String encoded = Base64.getEncoder().encodeToString(data);
				track.setAudio(encoded);
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
		return track;
	}
	
	public int uploadTrack(String title, int albumid, String genre, Part file, int user) throws SQLException, IOException {
		String query = "INSERT into track (title, albumid, genre, audio, userid) VALUES(?, ?, ?, ?, ?)";

		int code = 0;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, title);
			pstatement.setInt(2, albumid);
			pstatement.setString(3, genre);
			pstatement.setBlob(4, file.getInputStream());
			pstatement.setInt(5, user);
			code = pstatement.executeUpdate();

			// required if autocommit is false
			if(!connection.getAutoCommit())
				connection.commit();

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new SQLException(e);
		} finally {
			// re-enable autocommit
			connection.setAutoCommit(true);
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
	
	public int addTrackToPlaylist(int trackid, int playlistid) throws SQLException {
		String query = "INSERT into playlist_containment (playlistid, trackid) VALUES(?, ?)";

		int code = 0;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, playlistid);
			pstatement.setInt(2, trackid);
			code = pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e) {

			}
		}
		return code;
	}
}
