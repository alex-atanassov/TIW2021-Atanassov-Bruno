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

import com.google.gson.Gson;

import it.polimi.tiw.beans.TrackCover;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.TrackCoverDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GetPlaylistTracks")
public class GetUserTracks extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		TrackCoverDAO tDAO= new TrackCoverDAO(connection);
		List<TrackCover> userTracks = new ArrayList<TrackCover>();
		
		try {
			// All user tracks, selectable to be added to the playlist
			userTracks = tDAO.findTracksByUser(user);
		} catch(SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Database access failed");
			return;	
		}
				
		String json = new Gson().toJson(userTracks);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
	
