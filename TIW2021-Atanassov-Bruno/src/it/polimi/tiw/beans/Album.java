package it.polimi.tiw.beans;

import java.time.Year;

public class Album {
	private int id;
	private String name;
	private Year year;
	//image
	
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
	public Year getYear() {
		return year;
	}
	public void setYear(Year year) {
		this.year = year;
	}

}
