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
import javax.servlet.http.Part;

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
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
				
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
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Invalid genre");
					return;
				}
				else if(Integer.parseInt(albumchoice) == 1 && (aDAO.findAlbumById(Integer.parseInt(albumid)) == null
						|| aDAO.findAlbumById(Integer.parseInt(albumid)).getUserid() != userid)) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Invalid existing album choice");
					return;
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
				}
			} catch (NumberFormatException e) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().println("Parsing error: invalid parameters");
				return;
			} catch (SQLException e) {
				// check if cause is duplicate value on unique constraint
				if(e.getMessage().contains("Duplicate")) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Duplicate album name for same artist");
				} else {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Issue with Database happened");
				}
				return;
			} catch (IOException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("I/O exception has occured");
				return;
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(trackForm.getErrors());
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println("Creation successful");

	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
