package it.polimi.tiw.beans;

public class Playlist {
	private String title;
	private int userid;
	private int id;
	private String date;
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setUser(int userid) {
		this.userid = userid;
	}
	
	public void setId(int id) {
		this.id = id; 
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getUser() {
		return userid;
	}
	
	public int getId() {
		return id;
	}
	
	public String getDate() {
		return date;
	}
}
