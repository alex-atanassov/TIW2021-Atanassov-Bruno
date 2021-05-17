package it.polimi.tiw.beans;

import java.util.Date;

public class Playlist {
	private String title;
	private User user;
	private int id;
	private Date date;
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setid(int id) {
		this.id = id; 
	}
	
	public void setDate(Date date) {
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
	
	public Date getDate() {
		return date;
	}
}
