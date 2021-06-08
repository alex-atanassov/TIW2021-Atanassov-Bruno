package it.polimi.tiw.beans;

public class Track {
	private int id;
	private int userid;
	private String title;
	private Album album;
	private int albumid;
	private String genre;
	private String audio;
	
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

	public int getUser() {
		return userid;
	}

	public void setUser(int user) {
		this.userid = user;
	}

	public String getFile() {
		return audio;
	}

	public void setFile(String audio) {
		this.audio = audio;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public Album getAlbum() {
		return album;
	}

	public int getAlbumid() {
		return albumid;
	}

	public void setAlbumid(int albumid) {
		this.albumid = albumid;
	}
	
}
