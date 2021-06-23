package it.polimi.tiw.beans;

public class Album {
	private int id;
	private String name;
	private String artist;
	private int year;
	private String image;
	private int userid;
	
	public Album() {}
	
	// Useful for the table shown in the modal window for reorder
	public Album(String name, String artist, String image) {
		this.name = name;
		this.artist = artist;
		this.image = image;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

}
