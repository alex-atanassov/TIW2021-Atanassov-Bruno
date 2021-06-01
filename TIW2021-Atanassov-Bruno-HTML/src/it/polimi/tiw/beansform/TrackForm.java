package it.polimi.tiw.beansform;

import java.util.Calendar;

import javax.servlet.http.Part;

public class TrackForm {
	private String title;
	private String genre;
	private String albumChoice;
	private String albumId;
	private String albumName;
	private String albumArtist;
	private String albumYear;
//	private Part albumImage;
//	private Part audio;
	
	private int albumChoiceNumber = 0;
	
	private String titleError;
	private String genreError;
	private String albumChoiceError;
	private String albumIdError;
	private String albumNameError;
	private String albumArtistError;
	private String albumYearError;
	private String albumImageError;
	private String audioError;

	public TrackForm() {
		super();
	}
	
	public TrackForm(String title, String genre, String albumChoice, String albumId, String albumName, 
			String albumArtist, String albumYear, Part albumImage, Part audio) {
		setTitle(title);
		setGenre(genre);
		setAlbumChoice(albumChoice);
		setAlbumId(albumId);
		setAlbumName(albumName);
		setAlbumArtist(albumArtist);
		setAlbumYear(albumYear);
		setAlbumImage(albumImage);
		setAudio(audio);
	}


	public boolean isValid() {
		return !(titleError != null || genreError != null || audioError != null ||
				albumChoiceError != null || albumIdError != null || albumNameError != null ||
				albumArtistError != null || albumYearError != null || albumImageError != null);
	}


	public void setTitle(String title) {
		if (title == null || title.isEmpty()) {
			this.titleError = "Title field is required";
		} else {
			this.titleError = null;
			this.title = title;
		}
	}
	
	public void setGenre(String genre) {
		if (genre == null || genre.isEmpty()) {
			this.setGenreError("Genre field is required");
		// a servlet will check whether genre exists. If not, setGenreError will be invoked later by it	
		} else {
			this.setGenreError(null);
			this.genre = genre;
		}
	}
	
	public void setAlbumId(String albumId) {
		if (albumChoiceNumber == 1 && (albumId == null || albumId.isEmpty())) {
			// or non existing album...
			this.setAlbumIdError("An album is required");
		} else {
			this.setAlbumIdError(null);
			this.albumId = albumId;
		}
	}
	
	public void setAlbumName(String albumName) {
		if (albumChoiceNumber == 2 && (albumName == null || albumName.isEmpty())) {
			this.setAlbumNameError("Album name is required");
		} else {
			this.setAlbumNameError(null);
			this.albumName = albumName;
		}
	}
	
	public void setAlbumArtist(String artist) {
		if (albumChoiceNumber == 2 && (artist == null || artist.isEmpty())) {
			this.setAlbumArtistError("An artist name is required");
		} else {
			this.setAlbumArtistError(null);
			this.albumArtist = artist;
		}
	}

	public void setAlbumYear(String albumYear) {		
		if(albumChoiceNumber == 2) {
			int year;
			try {
				year = Integer.parseInt(albumYear);
			} catch (NumberFormatException e) {
				this.setAlbumYearError("A valid year is required");
				return;
			}
			
			if (year < 1800 && year > Calendar.getInstance().get(Calendar.YEAR))
				this.setAlbumYearError("A valid year is required");
			else {
				this.setAlbumYearError(null);
				this.albumYear = albumYear;
			}
		} else {
			this.setAlbumYearError(null);
			this.albumYear = albumYear;
		}
	}
	
	public void setAlbumImage(Part albumImage) {
//		this.albumImage = albumImage;
		
		if(albumChoiceNumber == 2) {
			if (albumImage == null || albumImage.getSize() <= 0) {
				this.setAlbumImageError("An image for the new album is required");
			} else {
				String contentType = albumImage.getContentType();
	
				if (!contentType.startsWith("image")) {
					this.setAlbumImageError("File must be an image");
				} else {
					this.setAlbumImageError(null);
				}
			}
		} else {
			this.setAlbumImageError(null);
		}
	}
	
	public void setAudio(Part audio) {
//		this.audio = audio;
		if (audio == null || audio.getSize() <= 0) {
			this.setAudioError("An audio file is required");
		} else {
			String contentType = audio.getContentType();

			if (!contentType.startsWith("audio")) {
				this.setAudioError("File must be an audio");
			} else {
				this.setAudioError(null);
			}
		}
	}
	
	public void setAlbumChoice(String albumChoice) {		
		int choice;
		try {
			choice = Integer.parseInt(albumChoice);
		} catch (NumberFormatException e) {
			this.setAlbumChoiceError("Please choose between an existing album or a new one");
			return;
		}
		
		if (choice != 1 && choice != 2)
			this.setAlbumChoiceError("Please choose between an existing album or a new one");
		else {
			albumChoiceNumber = choice;	// TODO ???
			this.setAlbumChoiceError(null);
			this.albumChoice = albumChoice;
		}
	}
	
	public String getTitle() {
		return title;
	}

	public String getGenre() {
		return genre;
	}

	public String getAlbumid() {
		return albumId;
	}

	public String getAlbumName() {
		return albumName;
	}

	private void setAlbumNameError(String albumNameError) {
		this.albumNameError = albumNameError;
	}


	public String getAlbumArtist() {
		return albumArtist;
	}

	private void setAlbumArtistError(String artistError) {
		this.albumArtistError = artistError;
	}

	public String getAlbumYear() {
		return albumYear;
	}


	private void setAlbumYearError(String yearError) {
		this.albumYearError = yearError;
	}

//	public Part getAlbumImage() {
//		return albumImage;
//	}



	private void setAlbumImageError(String albumImageError) {
		this.albumImageError = albumImageError;
	}

//	public Part getAudio() {
//		return audio;
//	}

	private void setAudioError(String audioError) {
		this.audioError = audioError;
	}


	public void setGenreError(String genreError) {
		this.genreError = genreError;
	}


	public void setAlbumIdError(String albumIdError) {
		this.albumIdError = albumIdError;
	}
	
	public String getTitleError() {
		return titleError;
	}


	public String getGenreError() {
		return genreError;
	}


	public String getAlbumidError() {
		return albumIdError;
	}


	public String getAlbumNameError() {
		return albumNameError;
	}


	public String getAlbumArtistError() {
		return albumArtistError;
	}


	public String getAlbumYearError() {
		return albumYearError;
	}


	public String getAlbumImageError() {
		return albumImageError;
	}


	public String getAudioError() {
		return audioError;
	}


	public String getAlbumChoiceError() {
		return albumChoiceError;
	}


	public void setAlbumChoiceError(String albumChoiceError) {
		this.albumChoiceError = albumChoiceError;
	}


	public String getAlbumChoice() {
		return albumChoice;
	}



}
