package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.Genre;

public class GenreDAO {
	private Connection connection;

	public GenreDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Genre findGenreByName(String name) throws SQLException {
		Genre g = null;
		String query = "SELECT * FROM genre where name = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, name);
			result = pstatement.executeQuery();
			while (result.next()) {
				g = new Genre();
				g.setName(result.getString("name"));
			}
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement");
			}
		}
		return g;
	}
}
