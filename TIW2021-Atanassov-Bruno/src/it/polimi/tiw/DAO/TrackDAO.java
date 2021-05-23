package it.polimi.tiw.DAO;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Part;

import it.polimi.tiw.beans.Track;
import it.polimi.tiw.beans.TrackCover;
import it.polimi.tiw.beans.User;

public class TrackDAO {
	private Connection connection;

	public TrackDAO(Connection connection) {
		this.connection = connection;
	}

	public List<TrackCover> findTracksByPlaylist(int playlistId) throws SQLException {
		List<TrackCover> tracks = new ArrayList<TrackCover>();
		String query = "SELECT id, title, userid FROM track WHERE id in "
				+ "( SELECT trackid FROM playlist_containment WHERE playlistid = ? )";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, playlistId);
			result = pstatement.executeQuery();
			while (result.next()) {
				TrackCover track = new TrackCover();
				track.setTitle(result.getString("title"));
				track.setId(result.getInt("id"));
				track.setUserid(result.getInt("userid"));
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
	
	public List<TrackCover> findTracksByUser(User user) throws SQLException {
		List<TrackCover> tracks = new ArrayList<TrackCover>();
		String query = "SELECT id, title FROM track WHERE userid = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, user.getId());
			result = pstatement.executeQuery();
			while (result.next()) {
				TrackCover track = new TrackCover();
				track.setTitle(result.getString("title"));
				track.setId(result.getInt("id"));
//				track.setUserid(result.getInt("userid"));
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
				track.setAlbum(result.getInt("albumid"));
				track.setGenre(result.getString("genre"));
				track.setUser(result.getInt("userid"));
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
	
	public int uploadTrack(String title, int albumid, String genre, Part file, int user) throws SQLException {
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
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		String query = "INSERT into playlist_containment (playlistid, trackid)   VALUES(?, ?)";

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
