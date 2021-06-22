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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

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
	private TemplateEngine templateEngine;

	public GetPlaylistTracks() {
		super();
	}
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
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
			if (playlist == null) {						
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Playlist not found");
				return;
			}
			if (playlist != null && playlist.getUser() != user.getId()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				return;
			}
			playlistTracks = tDAO.findTracksByPlaylist(playlistid);
			if (playlistTracks == null) {						
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Playlist not found");
				return;
			}
		} catch(NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid parameters");
			return;	
		} catch(SQLException e) {
			e.printStackTrace();
			response.sendError(500, "Database access failed");
		}
		
		// Important: this line avoids manipulation of AddTrack form, in which user might change the destination playlist.
		// This is to avoid any redirect that "jumps" from one playlist to another
		session.setAttribute("playlistid", playlistid);
		
		String path = "/WEB-INF/Playlist.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("tracks", playlistTracks);
		// UserTracks are used in the AddTrack form select
		ctx.setVariable("userTracks", userTracks);
		ctx.setVariable("playlist", playlist);
		// Note: a non null errorMsg comes from a redirect from POST
		ctx.setVariable("errorMsg", request.getParameter("errorMsg"));
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
	

