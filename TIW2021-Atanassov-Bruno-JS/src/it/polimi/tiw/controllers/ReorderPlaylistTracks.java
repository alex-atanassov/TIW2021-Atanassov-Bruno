package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.dao.PlaylistDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/ReorderPlaylistTracks")
@MultipartConfig
public class ReorderPlaylistTracks extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
				
		int playlistid = (Integer) request.getSession().getAttribute("playlistid");	//TODO exception
		
		int[] orderedTracksIds = new Gson().fromJson(request.getParameter("orderedTracks"), int[].class);
		
		//TODO checks, errors, exceptions, etc.
		
		try {
			new PlaylistDAO(connection).reorderPlaylistTracks(orderedTracksIds, playlistid);
		} catch (SQLException e) {
			response.setStatus(500);
			response.getWriter().println("Issue with DB, reorder failed");
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(playlistid);
		
	}
}
