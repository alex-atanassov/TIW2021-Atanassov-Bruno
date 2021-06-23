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
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.PlaylistDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreatePlaylist")
@MultipartConfig
public class CreatePlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreatePlaylist() {
		super();
	}
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		HttpSession session = request.getSession();
				
		String title = request.getParameter("playlistTitle");
		int newPlaylistId;
		try {
			if(title==null || title.isEmpty()) {
				response.setStatus(400);
				response.getWriter().println("Missing playlist title, creation failed");
				return;
			} else {
				PlaylistDAO pDAO = new PlaylistDAO(connection);
	
				int userid = ((User) session.getAttribute("user")).getId();
				newPlaylistId = pDAO.createPlaylist(title,userid);
			} 
		} catch (SQLException e) {
			// check if duplicate playlist name for same user (unique constraint)
			if(e.getMessage().contains("Duplicate")) {
				response.setStatus(400);
				response.getWriter().println("Duplicate name in your playlists");
			} else {
				response.setStatus(500);
				response.getWriter().println("Issue with DB, creation failed");
			}
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(newPlaylistId);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
	
