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

import it.polimi.tiw.DAO.AlbumDAO;
import it.polimi.tiw.DAO.PlaylistDAO;
import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Playlist;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beansform.TrackForm;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/Home")
public class GoToHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
	public GoToHome() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
//		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String playlistErrorMsg = (String) request.getAttribute("playlistErrorMsg");

		User user = (User) session.getAttribute("user");
		PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		AlbumDAO albumDAO = new AlbumDAO(connection);
		List<Playlist> playlists = new ArrayList<Playlist>();
		List<Album> albums = new ArrayList<Album>();

		try {
			playlists = playlistDAO.findPlaylistsByUser(user);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover playlists");
			return;
		}
		
		try {
			albums = albumDAO.findAlbumsByUser(user);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover albums");
			return;
		}
		
		TrackForm trackForm = (TrackForm) session.getAttribute("trackForm");
		if(trackForm == null) trackForm = new TrackForm();

		// Redirect to the Home page
		String path = "/WEB-INF/Home.html";
//		if(session.getAttribute("trackForm") != null)
//			path += "#form2";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("playlists", playlists);
		ctx.setVariable("albums", albums);
		ctx.setVariable("trackForm", trackForm);
		ctx.setVariable("playlistErrorMsg", playlistErrorMsg);
		
		session.removeAttribute("trackForm");
		
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
