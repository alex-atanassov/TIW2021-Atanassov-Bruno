package it.polimi.tiw.beans;

public class Track {
	private int id;
	private int userid;
	private String title;
	private int albumid;
	private String genre;
	private String file;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getAlbum() {
		return albumid;
	}
	
	public void setAlbum(int album) {
		this.albumid = album;
	}

	public int getUser() {
		return userid;
	}

	public void setUser(int user) {
		this.userid = user;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}
	
}
