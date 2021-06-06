package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Playlist;
import it.polimi.tiw.beans.TrackCover;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.PlaylistDAO;
import it.polimi.tiw.dao.TrackCoverDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GetPlaylistTracks")
public class GetPlaylistTracks extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetPlaylistTracks() {
		super();
	}
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		TrackCoverDAO tDAO= new TrackCoverDAO(connection);
		List<TrackCover> playlistTracks = new ArrayList<TrackCover>();
		List<TrackCover> userTracks = new ArrayList<TrackCover>();
		Integer playlistid = null;
		Playlist playlist = null;
		
		try {
			// All user tracks, selectable to be added to the playlist
			userTracks = tDAO.findTracksByUser(user);
			playlistid = Integer.parseInt(request.getParameter("playlistid"));
			// The covers of the tracks in this playlist
			playlist = new PlaylistDAO(connection).findPlaylistById(playlistid);
			playlistTracks = tDAO.findTracksByPlaylist(playlistid);
			if (playlistTracks == null) {						
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().println("Resource not found");
				return;
			}
			if (playlist.getUser() != user.getId()) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("User not allowed");
				return;
			}
		} catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Invalid parameters");
			return;	
		} catch(SQLException e) {
			e.printStackTrace();
			response.sendError(500, "Database access failed");
		}
		
		session.setAttribute("playlistid", playlistid);
		
//		String json = new Gson().toJson(mission);
//		response.setContentType("application/json");
//		response.setCharacterEncoding("UTF-8");
//		response.getWriter().write(json);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
	

