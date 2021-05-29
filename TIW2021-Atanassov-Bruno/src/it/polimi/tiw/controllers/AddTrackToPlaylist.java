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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.DAO.TrackDAO;
import it.polimi.tiw.beans.Track;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/AddTrackToPlaylist")
public class AddTrackToPlaylist extends HttpServlet {
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
			if (track == null) {
				errorMsg = "Invalid track input";
			} else {
				tDAO.addTrackToPlaylist(trackid, playlistid);
			}
		} catch (NumberFormatException e) {
			errorMsg = "Invalid parameters";
		} catch (SQLException e) {
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
