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
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.DAO.PlaylistDAO;
import it.polimi.tiw.DAO.TrackDAO;
import it.polimi.tiw.beans.Playlist;
import it.polimi.tiw.beans.Track;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/AddTrackToPlaylist")
public class AddTrackToPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	//public AddTrackToPlaylist() {
	//	super();
	//}
	
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
		Boolean isBadRequest = false;
		try {
			playlistid = Integer.parseInt(request.getParameter("playlistid"));
			trackid = Integer.parseInt(request.getParameter("trackid"));
			
			TrackDAO tDAO = new TrackDAO(connection);
			PlaylistDAO pDAO = new PlaylistDAO(connection);
			Track track = null;
			Playlist playlist = null;

			track = tDAO.findTrackById(trackid);
			playlist = pDAO.findPlaylistById(playlistid);
			if (track == null || playlist == null) {
				isBadRequest = true;
			} else {
				tDAO.addTrackToPlaylist(trackid, playlistid);
			}
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid parameters");
			return;
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
			return;
		}
	
		ServletContext servletContext = getServletContext();
		String ctxpath = servletContext.getContextPath();
		String path;
		if(!isBadRequest) {
			path = ctxpath + "/GetPlaylistTracks?playlistid=" + playlistid;
			response.sendRedirect(path);
		} else {
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Invalid track or playlist.");
			path = ctxpath + "/Playlist.html";
			templateEngine.process(path, ctx, response.getWriter());
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
