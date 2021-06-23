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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.beansform.TrackForm;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.GenreDAO;
import it.polimi.tiw.dao.TrackDAO;
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
		int userid = ((User) session.getAttribute("user")).getId();

		if (trackForm.isValid()) {
			AlbumDAO aDAO = new AlbumDAO(connection);
			GenreDAO gDAO = new GenreDAO(connection);
			TrackDAO tDAO = new TrackDAO(connection);
			try {
				if(gDAO.findGenreByName(genre) == null) {
					trackForm.setGenreError("Invalid genre");
				}
				else if(Integer.parseInt(albumchoice) == 1 && (aDAO.findAlbumById(Integer.parseInt(albumid)) == null
						|| aDAO.findAlbumById(Integer.parseInt(albumid)).getUserid() != userid)) {
					trackForm.setAlbumIdError("Invalid existing album choice.");
				} else {
					int album;
					
					// disable autocommit for the next two SQL updates
					connection.setAutoCommit(false);
					if(Integer.parseInt(albumchoice) == 2) {
						album = aDAO.createAlbum(albumName, artist, Integer.parseInt(year), albumimg, userid);
					}
					else album = Integer.parseInt(albumid);
					
					tDAO.uploadTrack(title, album, genre, file, userid);
					
					connection.commit();
					connection.setAutoCommit(true);
					isBadRequest = false;
				}
			} catch (NumberFormatException e) {
				trackForm.setGenericError("Invalid parameters");
			} catch (SQLException e) {
				// check if cause is duplicate value on unique constraint
				if(e.getMessage().contains("Duplicate")) {
					trackForm.setAlbumNameError("Duplicate album name for same artist");
				}
				else trackForm.setGenericError("Database error, operation failed");
			} catch (IOException e) {
				trackForm.setGenericError("I/O exception has occured");
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
