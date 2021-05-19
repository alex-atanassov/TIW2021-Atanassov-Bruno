package it.polimi.tiw.beans;

public class Playlist {
	private String title;
	private User user;
	private int id;
	private String date;
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setid(int id) {
		this.id = id; 
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getTitle() {
		return title;
	}
	
	public User getOwner() {
		return user;
	}
	
	public int getid() {
		return id;
	}
	
	public String getDate() {
		return date;
	}
}
