package it.polimi.tiw.DAO;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Track;

public class TrackDAO {
	private Connection connection;

	public TrackDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Track> findTracksByPlaylist(int playlistId) throws SQLException {
		List<Track> tracks = new ArrayList<Track>();
		String query = "SELECT title, album, genre, player FROM track WHERE id in "
				+ "( SELECT trackid FROM aggregation WHERE playlistid = ? )";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, playlistId);
			result = pstatement.executeQuery();
			while (result.next()) {
				Track track = new Track();
				track.setTitle(result.getString("title"));
				track.setAlbum(result.getInt("album"));
				track.setGenre(result.getString("genre"));
				//set file
				tracks.add(track);
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
		return tracks;
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
				track.setAlbum(result.getInt("album"));
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
	
	public int uploadTrack(String title, int albumid, String genre, Blob file, int user) throws SQLException {
		String query = "INSERT into track (title, album, genre, file)   VALUES(?, ?, ?, ?, ?)";

		int code = 0;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, title);
			pstatement.setInt(2, albumid);
			pstatement.setString(3, genre);
			pstatement.setBlob(4, file);
			pstatement.setInt(5,  user);
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
	
	public int addTrackToPlaylist(int trackid, int playlistid) throws SQLException {
		String query = "INSERT into aggregation (playlistid, trackid)   VALUES(?, ?)";

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
