package it.polimi.tiw.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import it.polimi.tiw.beans.TrackCover;
import it.polimi.tiw.beans.User;

public class TrackCoverDAO {
	private Connection connection;

	public TrackCoverDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<TrackCover> findTracksByPlaylist(int playlistId) throws SQLException {
		List<TrackCover> tracks = new ArrayList<TrackCover>();
		String query = "SELECT track.id, title, track.userid, image "
				+ "FROM track join album on track.albumid = album.id "
				+ "WHERE track.id in ( SELECT trackid FROM playlist_containment WHERE playlistid = ? )";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, playlistId);
			result = pstatement.executeQuery();
			while (result.next()) {
				TrackCover track = new TrackCover();
				track.setTitle(result.getString("title"));
				track.setId(result.getInt("track.id"));
				track.setUserid(result.getInt("track.userid"));
				
				byte[] imgData = result.getBytes("image");
				String encodedImg = Base64.getEncoder().encodeToString(imgData);
				track.setImage(encodedImg);
				
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
				
//				byte[] imgData = result.getBytes("image");
//				String encodedImg=Base64.getEncoder().encodeToString(imgData);
//				album.setImage(encodedImg);
				
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
}
