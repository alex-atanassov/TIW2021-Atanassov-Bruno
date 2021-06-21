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
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.PlaylistDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreatePlaylist")
public class CreatePlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		HttpSession session = request.getSession();
				
		String title = request.getParameter("playlistTitle");
		String errorMsg = null;
		try {
			if(title==null || title.isEmpty()) {
				errorMsg = "Invalid playlist name";
			} else {
				PlaylistDAO pDAO = new PlaylistDAO(connection);
	
				// check if duplicate playlist name for same user
				// Note: DB is already protected from duplicates, but this is to get the correct error message
//				if(pDAO.findPlaylistsByUser((User) session.getAttribute("user")).stream()
//						.anyMatch(p -> p.getTitle().equals(title)))
//					errorMsg = "Duplicate playlist name";
//				else {
					int userid = ((User) session.getAttribute("user")).getId();
					pDAO.createPlaylist(title,userid);
//				}
			}
		} catch (SQLException e) {
			if(e.getMessage().contains("Duplicate"))
				errorMsg = "Duplicate name in your playlists";
			else errorMsg = "Issue with database, creation failed";
		}
		
		ServletContext servletContext = getServletContext();
		String ctxpath = servletContext.getContextPath();
		String path = ctxpath + "/Home" ;
		if(errorMsg != null) {
			// show error to client
			errorMsg.replaceAll(" ", "+");
			path += "?playlistErrorMsg=" + errorMsg;
		}
		// redirect in both cases, to avoid retransmission on refresh
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
	
