package it.polimi.tiw.beans;

import java.util.Date;

public class Playlist {
	private String Title;
	private User user;
	private int id;
	private Date date;
	
	public void setTitle(String Title) {
		this.Title = Title;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setid(int id) {
		this.id = id; 
	}
	
	public void setdate(Date date) {
		this.date = date;
	}
	
	public String getTitle() {
		return Title;
	}
	
	public User getOwner() {
		return user;
	}
	
	public int getid() {
		return id;
	}
	
	public Date getdate() {
		return date;
	}
}
