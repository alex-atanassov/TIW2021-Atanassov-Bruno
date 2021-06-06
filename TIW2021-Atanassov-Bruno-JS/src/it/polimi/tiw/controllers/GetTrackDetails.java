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

import com.google.gson.Gson;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Track;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.TrackDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GetTrackDetails")
public class GetTrackDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetTrackDetails() {
		super();
	}
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		//get and check parameters
		Integer trackId = null;
		try {
			trackId = Integer.parseInt(request.getParameter("trackid"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect parameter values");
			return;
		}
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		TrackDAO trackDAO = new TrackDAO(connection);
		Track track = null;
		AlbumDAO albumDAO = new AlbumDAO(connection);
		Album album = null;
		
		try {
			track = trackDAO.findTrackById(trackId);
			if (track == null) {						
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().println("Track not found");
				return;
			}
			if (track.getUser() != user.getId()) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("User not allowed");
				return;
			}
			// TODO maybe put the following part (till setAlbum) in trackDAO?
			album = albumDAO.findAlbumById(track.getAlbum());
			if (album == null) {						
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().println("Album not found");
				return;
			}
			track.setAlbum(album);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Database access failed");
			return;
		}
		
		String json = new Gson().toJson(track);
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
	

