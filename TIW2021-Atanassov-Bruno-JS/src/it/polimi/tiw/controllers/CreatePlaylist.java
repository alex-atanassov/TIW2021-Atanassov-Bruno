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
		
		boolean isBadRequest = false;
		
		String title = request.getParameter("playlistTitle");	
		try {
			if(title==null || title.isEmpty()) {
				isBadRequest = true;
			}else {
			PlaylistDAO pDAO = new PlaylistDAO(connection);

//			Date startDate = Calendar.getInstance().getTime();
			int userid = ((User) session.getAttribute("user")).getId();
			pDAO.createPlaylist(title,userid);
				} 
		}catch (Exception e) {
			e.printStackTrace();
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
	
