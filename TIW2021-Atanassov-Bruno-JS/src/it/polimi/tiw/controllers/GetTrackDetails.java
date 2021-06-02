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
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.DAO.TrackDAO;
import it.polimi.tiw.beans.Track;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GetTrackDetails")
public class GetTrackDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GetTrackDetails() {
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException  {
		//get and check parameters
		Integer trackId = null;
		try {
			trackId = Integer.parseInt(request.getParameter("trackid"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		TrackDAO trackDAO = new TrackDAO(connection);
		Track track = null;
		
		try {
				track = trackDAO.findTrackById(trackId);
				if (track == null) {						
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					response.getWriter().println("Resource not found");
					return;
				}
				if (track.getUser() != user.getId()) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.getWriter().println("User not allowed");
					return;
				}
			}catch(SQLException e) {
				response.sendError(500, "Database access failed");
			}
			
		//mi sembra che track details si vedano dentro player view
		String path = "/WEB-INF/Player.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("Track", track);
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
	
