package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.DAO.AlbumDAO;
import it.polimi.tiw.DAO.GenreDAO;
import it.polimi.tiw.DAO.TrackDAO;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beansform.TrackForm;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/UploadTrack")
@MultipartConfig
public class UploadTrack extends HttpServlet {
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
		
		HttpSession session = request.getSession();
		
		boolean isBadRequest = true;
		
		String title = request.getParameter("trackTitle");
		String genre = request.getParameter("genre");
		Part file = request.getPart("audio");
		String albumchoice = request.getParameter("album");
		String albumid = request.getParameter("albumId");
		String albumName = request.getParameter("albumName");
		String artist = request.getParameter("albumArtist");
		String year = request.getParameter("albumYear");
		Part albumimg = request.getPart("albumImage");
		
		TrackForm trackForm = new TrackForm(title, genre, albumchoice, albumid, albumName, artist, year, albumimg, file);
		
		if (trackForm.isValid()) {
			AlbumDAO aDAO = new AlbumDAO(connection);
			GenreDAO gDAO = new GenreDAO(connection);
			TrackDAO tDAO = new TrackDAO(connection);
			try {
				if(gDAO.findGenreByName(genre) == null) {
					trackForm.setGenreError("Invalid genre.");	//TODO set genre to null?
				}
				else if(Integer.parseInt(albumchoice) == 1 && aDAO.findAlbumById(Integer.parseInt(albumid) /*Ocio al formato*/) == null) {
					trackForm.setAlbumIdError("Invalid existing album choice.");
				} else {
					int album;
					int userid = ((User) session.getAttribute("user")).getId();
					if(Integer.parseInt(albumchoice) == 2) {
						album = aDAO.createAlbum(albumName, artist, Integer.parseInt(year), albumimg, userid);
						// TODO duplicate albumName+artist
					}
					else album = Integer.parseInt(albumid);	//TODO else if - else
					
					tDAO.uploadTrack(title, album, genre, file, userid);
					
					isBadRequest = false;
				}
			} catch (NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid parameters");
				return;
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
				return;
			} catch (IOException e) {
				// TODO handle
			}
		}
		
		ServletContext servletContext = getServletContext();
		String ctxpath = servletContext.getContextPath();
		String path;
		
		if (isBadRequest) {
			session.setAttribute("trackForm", trackForm);			
		}
		path = ctxpath + "/Home";
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