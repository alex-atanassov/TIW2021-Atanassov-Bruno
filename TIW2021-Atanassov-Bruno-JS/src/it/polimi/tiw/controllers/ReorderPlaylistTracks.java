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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import it.polimi.tiw.dao.PlaylistDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/ReorderPlaylistTracks")
@MultipartConfig
public class ReorderPlaylistTracks extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
				
		int playlistid = (Integer) request.getSession().getAttribute("playlistid");
		
		Integer[] orderedTracksIds = new Gson().fromJson(request.getParameter("orderedTracks"), Integer[].class);
		
		try {
			
			// checks for inexistent or unauthorized tracks
//			List<Integer> tracks = Arrays.asList(orderedTracksIds);

//			TrackDAO tDAO = new TrackDAO(connection);
//			for(int t : tracks) {
//				Track track = tDAO.findTrackById(t);
//				if(track == null || track.getUserid() != ((User) request.getSession().getAttribute("user")).getId()) {
//					response.setStatus(400);
//					response.getWriter().println("Found an inexistent or unauthorized track");
//					return;
//				}
//			}
		
			new PlaylistDAO(connection).reorderPlaylistTracks(orderedTracksIds, playlistid);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Issue with DB, reorder failed");
			return;
		} catch (JsonSyntaxException e) {
			response.setStatus(400);
			response.getWriter().println("Invalid JSON");
			return;			
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(playlistid);
		
	}
}
