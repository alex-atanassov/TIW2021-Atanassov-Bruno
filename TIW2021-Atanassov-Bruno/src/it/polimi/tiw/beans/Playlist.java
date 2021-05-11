package it.polimi.tiw.beans;

public class Playlist {
	private String Title;
	private Date Data;
	private User user;
	private int id;
	
	public void setTitle(String Title) {
		this.Title = Title;
	}
	
	public void setDate(Date Data) {
		this.Data=Data;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setid(int id) {
		this.id = id; 
	}
	
	public String getTitle() {
		return Title;
	}
	
	public Date getData() {
		return Data;
	}
	
	public User getOwner() {
		return user;
	}
	
	public int getid() {
		return id;
	}
}
