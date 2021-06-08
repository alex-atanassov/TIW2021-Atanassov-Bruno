	(function() {  
	
	 //page components
	  var Playlist, PlaylistDetails, //maybe also wizard for previous and next
	    pageOrchestrator = new PageOrchestrator();

	  window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	      pageOrchestrator.refresh();
	    }
	  }, false);


	  // Constructors of view components

	  function PersonalMessage(_username, messagecontainer) {
	    this.username = _username;
	    this.show = function() {
	      messagecontainer.textContent = this.username;
	    }
	  }

	  function Playlist(_alert, _listcontainer, _listcontainerbody) {
	    this.alert = _alert;
	    this.listcontainer = _listcontainer;
	    this.listcontainerbody = _listcontainerbody;

	    this.reset = function() {
	      this.listcontainer.style.visibility = "hidden";
	    }

	    this.show = function(next) {
	    //self per avere riferimento a playlist tramite closure
	      var self = this;
	      makeCall("GET", "GetPlaylistData", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var TracksToShow = JSON.parse(req.responseText);
	              if (TracksToShow.length == 0) {
	                self.alert.textContent = "No Tracks yet!";
	                return;
	              }
	              self.update(TracksToShow); // self visible by closure
	              if (next) next(); // show the default element of the list if present
	            }
	          } else {
	            self.alert.textContent = message;
	          }
	        }
	      );
	    };


	    this.update = function(arrayTracks) {
	      var elem, i, row, destcell, datecell, linkcell, anchor;
	      this.listcontainerbody.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
	      arrayTracks.forEach(function(mission) { // self visible here, not this
	        row = document.createElement("tr");
	        destcell = document.createElement("td");
	        destcell.textContent = Playlist.title;
	        
	        row.appendChild(destcell);
	        datecell = document.createElement("td");
	        datecell.textContent = Playlist.date;
	        
	        row.appendChild(datecell);
	        linkcell = document.createElement("td");
	        anchor = document.createElement("a");
	        linkcell.appendChild(anchor);
	        linkText = document.createTextNode("Show");
	        anchor.appendChild(linkText);
	        
	        //anchor.Playlistid = Playlist.id; // make list item clickable
	        anchor.setAttribute('PlaylistId', Playlist.id); // set a custom HTML attribute
	        anchor.addEventListener("click", (e) => {
	          // dependency via module parameter
	          PlaylistDetails.show(e.target.getAttribute("Playlistid")); // the list must know the details container
	        }, false);
	        
	        anchor.href = "#";
	        row.appendChild(linkcell);
	        self.listcontainerbody.appendChild(row);
	      });
	      this.listcontainer.style.visibility = "visible";

	    }

	    this.autoclick = function(TrackId) {
	      var e = new Event("click");
	      var selector = "a[Trackid='" + TrackId + "']";
	      
	      //prendo il primo della tabella
	      var anchorToClick = (TrackId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
	      if (anchorToClick) anchorToClick.dispatchEvent(e);
	    }

	  }

	  function PlaylistDetails(options) {
	    this.alert = options['alert'];
	    this.detailcontainer = options['detailcontainer'];
	    this.expensecontainer = options['expensecontainer'];
	    this.expenseform = options['expenseform'];
	    this.closeform = options['closeform'];
	    this.date = options['date'];
	    this.track_title = options['track_title'];
	    this.artist = options['artist'];
	    this.genre = options['genre'];
	    this.album = options['album'];
	    this.image = options['image'];
	    
	    this.registerEvents = function(orchestrator) {
	      this.expenseform.querySelector("input[type='button']").addEventListener('click', (e) => {
	        var form = e.target.closest("form");
	        if (form.checkValidity()) {
	          var self = this,
	            Playlist= form.querySelector("input[type = 'hidden']").value;
	          makeCall("POST", 'CreatePlaylist', form,
	            function(req) {
	              if (req.readyState == 4) {
	                var message = req.responseText;
	                if (req.status == 200) {
	                  orchestrator.refresh(Playlist);
	                } else {
	                  self.alert.textContent = message;
	                }
	              }
	            }
	          );
	        } else {
	          form.reportValidity();
	        }
	      });

	      this.closeform.querySelector("input[type='button']").addEventListener('click', (event) => {
	        var self = this,
	          form = event.target.closest("form"),
	          PlaylistToClose = form.querySelector("input[type = 'hidden']").value;
	        makeCall("POST", 'AdTrackToPlaylist', form,
	          function(req) {
	            if (req.readyState == 4) {
	              var message = req.responseText;
	              if (req.status == 200) {
	                orchestrator.refresh(missionToClose);
	              } else {
	                self.alert.textContent = message;
	              }
	            }
	          }
	        );
	      });
	    }


		//shows Playlist Tracks
	    this.show = function(Playlistid) {
	      var self = this;
	      makeCall("GET", "GetPlaylistTracksData?Playlistid=" + Playlistid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var Track = JSON.parse(req.responseText);
	              self.update(Track); // self is the object on which the function
	              // is applied
	              self.detailcontainer.style.visibility = "visible";
	              switch (mission.status) {
	                case "OPEN":
	                  self.expensecontainer.style.visibility = "hidden";
	                  self.expenseform.style.visibility = "visible";
	                  self.expenseform.missionid.value = mission.id;
	                  self.closeform.style.visibility = "hidden";
	                  break;
	                case "REPORTED":
	                  self.expensecontainer.style.visibility = "visible";
	                  self.expenseform.style.visibility = "hidden";
	                  self.closeform.missionid.value = mission.id;
	                  self.closeform.style.visibility = "visible";
	                  break;
	                case "CLOSED":
	                  self.expensecontainer.style.visibility = "visible";
	                  self.expenseform.style.visibility = "hidden";
	                  self.closeform.style.visibility = "hidden";
	                  break;
	              }
	            } else {
	              self.alert.textContent = message;

	            }
	          }
	        }
	      );
	    };


	    this.reset = function() {
	      this.detailcontainer.style.visibility = "hidden";
	      this.expensecontainer.style.visibility = "hidden";
	      this.expenseform.style.visibility = "hidden";
	      this.closeform.style.visibility = "hidden";
	    }

	    this.update = function(t) {
	      this.date.textContent = t.date;
	      this.track_title.textContent = t.track_title;
	      this.artist.textContent = t.artist;
	      this.genre.textContent = t.genre;
	      this.album.textContent = t.album;
	      this.image.textContent = t.image;
	    }
	  }
	  
	  //here wizard code.js


	
	  

	  function PageOrchestrator() {
	  
	    var alertContainer = document.getElementById("id_alert"); //reference in HomeCS
	    
	    this.start = function() {
	      personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
	        document.getElementById("username"));
	      personalMessage.show();

	      Playlist = new Playlist(
	        alertContainer, 	//if user login doesn't have playlist
	        document.getElementById("id_listcontainer"),
	        document.getElementById("id_listcontainerbody"));

	      PlaylistDetails = new PlaylistDetails({
	      
	        alert: alertContainer,
	        detailcontainer: document.getElementById("id_detailcontainer"),
	        expensecontainer: document.getElementById("id_expensecontainer"),
	        expenseform: document.getElementById("id_expenseform"),
	        closeform: document.getElementById("id_closeform"),
	        date: document.getElementById("id_date"),
	        artist: document.getElementById("id_artist"),
	        genre: document.getElementById("id_genre"),
	        album: document.getElementById("id_album"),
	        track_title: document.getElementById("id_track_title"),
	        album_image: document.getElementById("id_album_image"),
	        
	      });
	      PlaylistDetails.registerEvents(this);
	      
	      //wizard here
	      
	      document.querySelector("a[href='Logout']").addEventListener('click', () => {
	        window.sessionStorage.removeItem('username');
	      })
	    };


	    this.refresh = function(currentTrack) {
	      alertContainer.textContent = "";
	      Playlist.reset();
	      PlaylistDetails.reset();
	      Playlist.show(function() {
	        Playlist.autoclick(currentTrack);
	      });
	      //wizard.reset()
	    };
	  }
	})();
