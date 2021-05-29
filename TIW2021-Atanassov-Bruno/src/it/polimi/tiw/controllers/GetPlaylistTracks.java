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

import it.polimi.tiw.DAO.TrackCoverDAO;
import it.polimi.tiw.beans.TrackCover;
import it.polimi.tiw.beans.User;
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
		
		try {
			// All user tracks, selectable to be added to the playlist
			userTracks = tDAO.findTracksByUser(user);
			playlistid = Integer.parseInt(request.getParameter("playlistid"));
			// The covers of the tracks in this playlist
			playlistTracks = tDAO.findTracksByPlaylist(playlistid);
			if (playlistTracks == null) {						
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().println("Resource not found");
				return;
			}
			if (playlistTracks.stream().anyMatch(t -> t.getUserid() != user.getId())) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("User not allowed");
				return;
			}
		} catch(NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid parameters");
			return;	
		} catch(SQLException e) {
			e.printStackTrace();
			response.sendError(500, "Database access failed");
		}
		
		session.setAttribute("playlistid", playlistid);
		//TODO replace w/ cookies?
		
		String path = "/WEB-INF/Playlist.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("tracks", playlistTracks);
		ctx.setVariable("userTracks", userTracks);
		ctx.setVariable("playlistid", playlistid);
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
	

