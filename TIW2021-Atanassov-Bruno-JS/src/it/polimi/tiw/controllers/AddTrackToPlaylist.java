package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Track;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.TrackDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/AddTrackToPlaylist")
public class AddTrackToPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Integer playlistid = null;
		Integer trackid = null;
		
		String errorMsg = null;
		try {
			playlistid = (Integer) request.getSession().getAttribute("playlistid");
			//TODO remove attribute after usage (not in this servlet)
			trackid = Integer.parseInt(request.getParameter("trackid"));
			
			TrackDAO tDAO = new TrackDAO(connection);
			Track track = null;

			track = tDAO.findTrackById(trackid);
			
			// track does not exist, or the owner is another user
			if (track == null || track.getId() != ((User) request.getSession().getAttribute("user")).getId()) {
				errorMsg = "Invalid track parameter";
			} else {
				tDAO.addTrackToPlaylist(trackid, playlistid);
			}
		} catch (NumberFormatException e) {
			errorMsg = "Invalid track or playlist";
		} catch (SQLException e) {
			e.printStackTrace();
			errorMsg = "Issue with DB";
		}
	
		ServletContext servletContext = getServletContext();
		String ctxpath = servletContext.getContextPath();
		String path = ctxpath + "/GetPlaylistTracks?playlistid=" + playlistid;
		
		if(errorMsg != null) {
			errorMsg.replaceAll(" ", "+");
			path += "&errorMsg=" + errorMsg;
		}
		response.sendRedirect(path);	
				
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
