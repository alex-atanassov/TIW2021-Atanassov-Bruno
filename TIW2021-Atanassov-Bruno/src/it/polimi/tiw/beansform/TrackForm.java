package it.polimi.tiw.beansform;

import javax.servlet.http.Part;

public class TrackForm {
	private String title;
	private String genre;
	private String albumid;
	private String albumName;
	private String artist;
	private String year;
	private Part albumimg;
	private Part file;
	
	private String titleError;
	private String genreError;
	private String albumidError;
	private String albumNameError;
	private String artistError;
	private String yearError;
	private String albumimgError;
	private String fileError;

	public TrackForm(String title, String genre, String albumid, String albumName, String artist, String year,
			Part albumimg, Part file) {
//		this.title = title;
//		this.genre = genre;
//		this.albumid = albumid;
//		this.albumName = albumName;
//		this.artist = artist;
//		this.year = year;
//		this.albumimg = albumimg;
//		this.file = file;
		setTitle(title);
		setGenre(genre);
		setAlbumid(albumid);
		setAlbumName(albumName);
		setArtist(artist);
		setYear(year);
		setAlbumimg(albumimg);
		setFile(file);
	}


	public boolean isValid() {
		if (title == null || genre == null || file == null)
			return false;
		if(albumid != null)
			return true;
		if(albumName == null || artist == null || year == null || albumimg == null)
			return false;
		return true;
	}
	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
		if (title == null || title.isEmpty()) {
			this.titleError = "Title field is required";
		} else {
			this.titleError = null;
		}
	}


	public String getGenre() {
		return genre;
	}


	public void setGenre(String genre) {
		this.genre = genre;
		if (genre == null || genre.isEmpty()) {
			this.setGenreError("Genre field is required");
		} else {
			this.setGenreError(null);
		}
	}


	public String getAlbumid() {
		return albumid;
	}


	public void setAlbumid(String albumid) {
		this.albumid = albumid;
	}


	public String getAlbumName() {
		return albumName;
	}


	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}


	public String getArtist() {
		return artist;
	}


	public void setArtist(String artist) {
		this.artist = artist;
	}


	public String getYear() {
		return year;
	}


	public void setYear(String year) {
		this.year = year;
	}


	public Part getAlbumimg() {
		return albumimg;
	}


	public void setAlbumimg(Part albumimg) {
		this.albumimg = albumimg;
	}


	public Part getFile() {
		return file;
	}


	public void setFile(Part file) {
		this.file = file;
	}


	public void setGenreError(String genreError) {
		this.genreError = genreError;
	}


	public void setAlbumidError(String albumidError) {
		this.albumidError = albumidError;
	}
	
	public String getTitleError() {
		return titleError;
	}


	public String getGenreError() {
		return genreError;
	}


	public String getAlbumidError() {
		return albumidError;
	}


	public String getAlbumNameError() {
		return albumNameError;
	}


	public String getArtistError() {
		return artistError;
	}


	public String getYearError() {
		return yearError;
	}


	public String getAlbumimgError() {
		return albumimgError;
	}


	public String getFileError() {
		return fileError;
	}

}
