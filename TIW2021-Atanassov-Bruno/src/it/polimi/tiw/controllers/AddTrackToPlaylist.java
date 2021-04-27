package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.DAO.PlaylistDAO;
import it.polimi.tiw.DAO.TrackDAO;
import it.polimi.tiw.beans.Playlist;
import it.polimi.tiw.beans.Track;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/AddTrackToPlaylist")
public class AddTrackToPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	//public AddTrackToPlaylist() {
	//	super();
	//}
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		Integer playlistid = null;
		Integer trackid = null;
		Boolean isBadRequest = false;
		try {
			playlistid = Integer.parseInt(request.getParameter("playlistid"));
			trackid = Integer.parseInt(request.getParameter("trackid"));
			
			TrackDAO tDAO = new TrackDAO(connection);
			PlaylistDAO pDAO = new PlaylistDAO(connection);
			Track track = null;
			Playlist playlist = null;

			track = tDAO.findTrackById(trackid);
			playlist = pDAO.findPlaylistById(playlistid);
			if (track == null || playlist == null) {
				//response.sendError(HttpServletResponse.SC_NOT_FOUND, "Track does not exists");
				isBadRequest = true;
			} else {
				tDAO.addTrackToPlaylist(trackid, playlistid);
			}
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid parameters");
			return;
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
			return;
		}
	
		//...
		String ctxpath = getServletContext().getContextPath();
		if(!isBadRequest) {
			String path = ctxpath + "/GetPlaylistTracks?playlistid=" + playlistid;
			response.sendRedirect(path);
		} else {
			//forward with error flag
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
