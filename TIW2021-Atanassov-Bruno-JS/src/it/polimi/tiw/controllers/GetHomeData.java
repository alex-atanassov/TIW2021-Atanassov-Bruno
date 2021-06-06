package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Playlist;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beansform.TrackForm;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.PlaylistDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/Home")
public class GetHomeData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public GetHomeData() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String playlistErrorMsg = request.getParameter("playlistErrorMsg");

		User user = (User) session.getAttribute("user");
		PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		AlbumDAO albumDAO = new AlbumDAO(connection);
		List<Playlist> playlists = new ArrayList<Playlist>();
		List<Album> albums = new ArrayList<Album>();

		try {
			playlists = playlistDAO.findPlaylistsByUser(user);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover playlists");
			return;
		}
		
		try {
			albums = albumDAO.findAlbumsByUser(user);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover albums");
			return;
		}
		
		TrackForm trackForm = (TrackForm) session.getAttribute("trackForm");
		if(trackForm == null) trackForm = new TrackForm();

	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
