(function() {

    //page components
    var pageOrchestrator = new PageOrchestrator();

    window.addEventListener("load", () => {
        if (sessionStorage.getItem("user") == null) {
            window.location.href = "index.html";
        } else {
            pageOrchestrator.start(); // initialize the components
            pageOrchestrator.refresh();
        }
    }, false);


    // Constructors of view components

    function PersonalMessage(_user, messagecontainer) {
        var user = JSON.parse(_user);
        this.show = function() {
            messagecontainer.textContent = user.name + " " + user.surname;
        }
    }

    function Playlists(_alert, _formalert, _zeroplaylistsMsg, _playlistscontainer, _playlistsbody, _form, _modal) {
        this.alert = _alert;
        this.formalert = _formalert;
        this.zeroplaylistsMsg = _zeroplaylistsMsg;
        this.playlistscontainer = _playlistscontainer;
        this.playlistsbody = _playlistsbody;
        this.createplaylistform = _form;
        this.modalObject = _modal;

        this.reset = function () {
            this.playlistscontainer.style.display = "none";
			this.alert.textContent = "";
			this.formalert.textContent = "";
			this.zeroplaylistsMsg.textContent = "You have no playlists yet, create one below!";
			this.zeroplaylistsMsg.style.textAlign = "center";
        }
        
        this.registerEvents = function (orchestrator) {
            this.createplaylistform.querySelector("input[type='button']").addEventListener('click', (e) => {
                var form = e.target.closest("form");
                if (form.checkValidity()) {
                    var self = this;
                    makeCall("POST", "CreatePlaylist", form,
                        function (req) {
                            if (req.readyState == 4) {
                                var message = req.responseText; // error or new id
                                if (req.status == 200) {                                
                                    orchestrator.refresh(message);
                                } else {
                                    self.formalert.textContent = message;
                                }
                            } else {
		                        self.formalert.textContent = message;
		                    }
                        }
                    );
                } else {
                    form.reportValidity();
                }
            });
        }

        this.show = function(next) {
            // self visible by closure
            var self = this;
            makeCall("GET", "GetPlaylistsData", null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var playlistsToShow = JSON.parse(req.responseText);
                            if (playlistsToShow.length > 0) {
	                            self.update(playlistsToShow); 
	                            if (next) next(); // show the default element of the list if present
                            }
                        }
                    } else {
                        self.alert.textContent = message;
                    }
                }
            );
        };


        this.update = function(arrayPlaylists) {
            var row, namecell, datecell, linkcell, reordercell, linkanchor, reorderanchor;
            this.playlistsbody.innerHTML = ""; // empty the table body
            // build updated list
            var self = this;
            self.zeroplaylistsMsg.textContent = "";
            arrayPlaylists.forEach(function(playlist) { // self visible here, not this
                row = document.createElement("tr");

                namecell = document.createElement("td");
                namecell.textContent = playlist.title;
                row.appendChild(namecell);

                datecell = document.createElement("td");
                datecell.textContent = playlist.date;
                row.appendChild(datecell);

                linkcell = document.createElement("td");
                linkanchor = document.createElement("a");
                linkcell.appendChild(linkanchor);
                linkText = document.createTextNode("Show");
                linkanchor.appendChild(linkText);

                linkanchor.setAttribute('playlistid', playlist.id); // set a custom HTML attribute
                linkanchor.addEventListener("click", (e) => {
                    // dependency via module parameter
                    playlistTracks.show(e.target.getAttribute("playlistid"), playlist.title); // the list must know the details container
                }, false);
                linkanchor.href = "#";
                row.appendChild(linkcell);

                reordercell = document.createElement("td");
                reorderanchor = document.createElement("a");
                reordercell.appendChild(reorderanchor);
                linkText = document.createTextNode("Reorder");
                reorderanchor.appendChild(linkText);

                reorderanchor.setAttribute('playlist', playlist.id); // set a custom HTML attribute
                reorderanchor.addEventListener("click", (e) => {
                
                  self.modalObject.modalDiv.style.display = "block";
                  self.modalObject.show(playlist);
                  
                }, false);
                reorderanchor.href = "#";
                row.appendChild(reordercell);

                self.playlistsbody.appendChild(row);
            });
            this.playlistscontainer.style.display = "block";

        }

        this.autoclick = function (playlistId) {
            var e = new Event("click");
            var selector = "a[playlistid='" + playlistId + "']";
            var anchorToClick =
                (playlistId) ? document.querySelector(selector) : this.playlistsbody.querySelectorAll("a")[0];
            if (anchorToClick) anchorToClick.dispatchEvent(e);
        }

    }

    function PlaylistTracks(options) {
        this.alert = options['alert'];
        this.formalert = options['formalert'];
        this.trackscontainer = options['trackscontainer'];
        this.addform = options['form'];
		this.playlistname = options['playlistname'];

        this.registerEvents = function (orchestrator) {
            this.addform.querySelector("input[type='button']").addEventListener('click', (e) => {
                var form = e.target.closest("form");
                if (form.checkValidity()) {
                    var self = this,
                     playlistToUpdate = self.playlistid;
                    makeCall("POST", "AddTrackToPlaylist", form,
                        function (req) {
                            if (req.readyState == 4) {
                                var message = req.responseText;
                                if (req.status == 200) {
                                    orchestrator.refresh(playlistToUpdate);
                                } else {
                                    self.formalert.textContent = message;
                                }
                            }
                        }
                    );
                } else {
                    form.reportValidity();
                }
            });
        }


        //shows Playlist Tracks
        this.show = function(playlistid, name) {
        	this.playlistid = playlistid;
            var self = this;
            
            this.playlistname.textContent = name;
            makeCall("GET", "GetPlaylistTracks?playlistid=" + playlistid, null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var tracksToShow = JSON.parse(req.responseText);
                            if (tracksToShow.length == 0) {
                                self.trackscontainer.innerHTML = "This playlist is empty.";
                                return;
                            }
                            self.update(tracksToShow); // self visible by closure
                            if (playlistid) self.autoclick();
                        }
                    } else {
                        self.alert.textContent = message;
                    }
                }
            );
        };


        this.reset = function () {
            this.trackscontainer.innerHTML = "No playlists available.";
            this.alert.textContent = "";
			this.formalert.textContent = "";
                        
            //reset add track form
            trackselect = this.addform.getElementsByTagName("select")[0];
            
            var self = this;

            makeCall("GET", "GetUserTracks", null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var tracksToShow = JSON.parse(req.responseText);
                            trackselect.innerHTML = "";
                            tracksToShow.forEach(function(track) {
                            	var row = document.createElement("option");
                            	row.value = track.id;
                            	row.textContent = track.title /*+ " (by " + track.album.artist + ")"*/;
                            	
                            	trackselect.appendChild(row);
                            });
                        }
                    } else {
                        self.formalert.textContent = message;
                    }
                }
            );
            
        }

        this.update = function (arrayTracks) {
            var row, titlecell, imagecell, linkcell, linkanchor, previous, next;
            this.trackscontainer.innerHTML = "";
			
            var self = this;
            for(var i = 0; i <= parseInt((arrayTracks.length - 1) / 5); i += 1) {
                trackGroup = arrayTracks.slice(5 * i, 5 * i + 5);
                groupdiv = document.createElement("div");
                table = document.createElement("table");
                table.className = "boxed";
                // table.style.padding = "5px";

                row = document.createElement("tr");
                trackGroup.forEach(function (track) { // self visible here, not this
                    cell = document.createElement("td");

                    titlecell = document.createElement("span");
                    titlecell.textContent = track.title;
                    cell.appendChild(titlecell);
                    cell.appendChild(document.createElement("br"));

                    imagecell = document.createElement("img");
                    imagecell.style.width = '140px';
					imagecell.style.height = '200px';
					
                    imagecell.style.objectFit = "cover";

                    imagecell.src = "data:image/jpeg;base64," + track.album.image;
                    cell.appendChild(imagecell);
                    cell.appendChild(document.createElement("br"));

                    linkcell = document.createElement("a");
                    linkText = document.createTextNode("Play");
                    linkcell.appendChild(linkText);
                    cell.appendChild(document.createElement("br"));

                    linkcell.setAttribute('trackid', track.id); // set a custom HTML attribute
                    linkcell.addEventListener("click", (e) => {
                        trackDetails.show(e.target.getAttribute("trackid")); // the list must know the details container
                    }, false);
                    linkcell.href = "#td_botright";
                    cell.appendChild(linkcell);

                    row.appendChild(cell);
                });
                table.appendChild(row);
                groupdiv.appendChild(table);
                groupdiv.appendChild(document.createElement("br"));
				
				if (i > 0) {
	                previous = document.createElement("button");
	                previous.innerHTML = "Previous";
	                previous.addEventListener("click", (e) => {
	                	self.changeStep(e.target.closest("div"), e.target.closest("div").previousElementSibling);
	                });
	                previous.style.marginRight = "5px";
	                groupdiv.appendChild(previous);
	                
	                groupdiv.hidden = true; // hide next elements at the beginning
                }

				if (i < parseInt((arrayTracks.length - 1) / 5)) {
	                next = document.createElement("button");
	                next.innerHTML = "Next";
	                next.addEventListener("click", (e) => {
	                	self.changeStep(e.target.closest("div"), e.target.closest("div").nextElementSibling);
	                });
	                next.style.marginLeft = "5px";
	                groupdiv.appendChild(next);
	            }

                self.trackscontainer.appendChild(groupdiv);
            }
            this.trackscontainer.style.visibility = "visible";

        }
        
        this.autoclick = function (trackid) {
            var e = new Event("click");
            var selector = "#track_groups.a[trackid='" + trackid + "']";
            var anchorToClick =
                (trackid) ? document.querySelector(selector) : this.trackscontainer.querySelectorAll("a")[0];
            if (anchorToClick) anchorToClick.dispatchEvent(e);
        }
        
		this.changeStep = function(origin, destination) {
	      origin.hidden = true;
	      destination.hidden = false;
	    }
    }

    function TrackDetails(options) {
        this.alertmessage = options['alert'];
        this.title = options['title'];
        this.genre = options['genre'];
        this.albumname = options['albumname'];
        this.albumartist = options['albumartist'];
        this.albumyear = options['albumyear'];
        this.albumimage = options['albumimage'];
        this.player = options['player'];
        this.track_player = options['container'];

        this.show = function (trackid) {
            var self = this;
            makeCall("GET", "GetTrackDetails?trackid=" + trackid, null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var track = JSON.parse(req.responseText);
                            self.update(track); // self is the object on which the function is applied

                            self.track_player.style.visibility = "visible";
                        } else {
                            self.alertmessage.textContent = message;
                        }
                    }
                }
            );
        };

        this.reset = function () {
        	this.alertmessage.textContent = "No tracks available.";
            this.track_player.style.visibility = "hidden";
        }

        this.update = function (track) {
        	this.alertmessage.hidden = true;
            this.title.textContent = "Title: " + track.title;
            this.genre.textContent = "Genre: " + track.genre;
            this.albumname.textContent = "Album: " + track.album.name;
            this.albumartist.textContent = "Artist: " + track.album.artist;
            this.albumyear.textContent = "Year: " + track.album.year;
            
            // reset background color
            // document.getElementsByTagName("body")[0].style.background = "white";
            
            // sets both image and background
            for(img in this.albumimage)
            	this.albumimage[img].src = "data:image/jpeg;base64," + track.album.image;
            this.player.src = "data:audio/mp3;base64," + track.audio;
        }
    }
    
    function Modal(_modal, _playlistname) {
    	this.modalDiv = _modal;
    	this.alert = document.getElementById("reorderErrorMsg");
    	this.table = this.modalDiv.getElementsByTagName("tbody")[0];
    	this.playlistname = _playlistname;
    	this.submitbutton = this.modalDiv.getElementsByTagName("input")[1];
    	    	
    	var self = this,
    	 span = document.getElementsByClassName("close")[0];
    	
    	this.registerEvents = function (orchestrator) {
    		this.submitbutton.addEventListener('click', (e) => this.submit(orchestrator));
    	
	    	// When the user clicks on the cross, close it
	    	span.addEventListener('click', () => {
	    		self.reset();
	    	});
	
			// When the user clicks anywhere outside of the modal, close it
			window.addEventListener('click', (e) => {
			    if (e.target == self.modalDiv) {
			        self.reset();
			    }
			});
		}
		
		this.reset = function() {
			self.modalDiv.style.display = "none";
			self.modalDiv.getElementsByTagName("table")[0].hidden = true;
			document.getElementById("emptyplaylistmessage").hidden = true;
    		self.submitbutton.disabled = true;
		}
		
		this.show = function (playlist) {
			makeCall("GET", "GetPlaylistTracks?playlistid=" + playlist.id, null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var tracksToShow = JSON.parse(req.responseText);
                            if (tracksToShow.length == 0) {
                            	document.getElementById("emptyplaylistmessage").hidden = false;
                                document.getElementById("emptyplaylistmessage").innerHTML = "This playlist is empty.";
                                return;
                            }
                            self.playlistname.textContent = playlist.title;
                            self.update(tracksToShow); // self visible by closure
                        }
                    } else {
                        self.alert.textContent = message;
                    }
                }
            );
		}
		
		this.update = function (arrayTracks) {
			var titlecell, albumnamecell, albumartistcell, hiddencell;
            self.table.innerHTML = "";

            arrayTracks.forEach(function (track) { // self visible here, not this
                row = document.createElement("tr");
                
                //source of drag & drop
                var source;
                
                //enable drag and drop
                row.setAttribute('draggable', true);
                row.addEventListener('dragstart', (e) => { dragStart(e); source = e.target.closest("tr"); });
                row.addEventListener('dragleave', dragLeave);
                row.addEventListener('dragover', dragOver);
                row.addEventListener('drop', (e) => {
                	drop(e);
                	if(e.target.closest("tr") != source)
                		// enable Reorder button after the first order changing drop has been done
                		self.submitbutton.disabled = false;
                });

                titlecell = document.createElement("td");
                titlecell.textContent = track.title;
                row.appendChild(titlecell);

                albumnamecell = document.createElement("td");
                albumnamecell.textContent = track.album.name;
                row.appendChild(albumnamecell);

                albumartistcell = document.createElement("td");
                albumartistcell.textContent = track.album.artist;
                row.appendChild(albumartistcell);
                
                hiddencell = document.createElement("td");
				hiddencell.textContent = track.id;
				hiddencell.style="display:none;";
                row.appendChild(hiddencell);
                
				self.table.appendChild(row);
            });
            self.modalDiv.getElementsByTagName("table")[0].hidden = false;
        }
        
	
	   this.submit = function(orchestrator) {
        
	        // Create array with IDs of sorted tracks
	        var column = [];
	
	        for(var i = 0; i < self.table.rows.length; i++)
	        	column.push(self.table.rows[i].children[3].textContent);
	        	
	        self.modalDiv.getElementsByTagName("input")[0].value = JSON.stringify(column);

        	makeCall("POST", "ReorderPlaylistTracks", self.modalDiv.getElementsByTagName("form")[0],
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;	// error or id of ordered playlist
                        if (req.status == 200) {                                
                            orchestrator.refresh(message);
                        } else {
                            self.alert.innerHTML = message;
                        }
                    } else {
                        self.alert.innerHTML = message;
                    }
                }
            );
        }		
    }

    function TrackForm(wizardId, alert) {
	    this.wizard = wizardId;
	    this.alert = alert;

		var self = this;
		
		this.show = function() {
			makeCall("GET", "GetUserAlbums", null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var albumsToShow = JSON.parse(req.responseText);
                            if (albumsToShow.length == 0) {
                                self.alert.textContent = "You have no albums uploaded";
                                document.getElementById('1').disabled = "true"; // disables first radio button
                                return;
                            } else console.log("AAA"); document.getElementById('1').removeAttribute("disabled");
                            
                            // append available user albums to select in fieldset number 2
							albumselect = self.wizard.getElementsByTagName("select")[1];
							albumselect.innerHTML = "";
                            albumsToShow.forEach(function(album) {
                            	var row = document.createElement("option");
                            	row.value = album.id;
                            	row.textContent = album.name + " (by " + album.artist + ")";
                            	
                            	albumselect.appendChild(row);
                            });
                            
	                    } else {
	                        self.alert.textContent = message;
	                    }
	                }
	            }
	        );
		}
				
	    this.registerEvents = function(orchestrator) {
	      // Manage previous and next buttons
	      var nextButtons = Array.prototype.slice.call(this.wizard.querySelectorAll("input[type='button'].next"));
	      var prevButtons = Array.prototype.slice.call(this.wizard.querySelectorAll("input[type='button'].prev"));
	      
	      // prev/next buttons in the two middle fieldsets
	      Array.from(nextButtons.slice(1, 3).concat(prevButtons.slice(0,2))).forEach(b => {
	        b.addEventListener("click", (e) => { 
	          var eventfieldset = e.target.closest("fieldset"),
	            valid = true;
	          if (e.target.className == "next") {
	            for (i = 0; i < eventfieldset.elements.length; i++) {
	              if (!eventfieldset.elements[i].checkValidity()) {
	                eventfieldset.elements[i].reportValidity();
	                valid = false;
	                break;
	              }
	            }
	          }
	          if (valid) {
	            this.changeStep(e.target.parentNode, (e.target.className === "next") ? e.target.closest("form").children[3] : e.target.closest("form").children[0]);
	          }
	        }, false);
	      });
	      
	      // for the two remaining buttons, that require to check an extra condition, based on user input
		  Array.from(nextButtons.slice(0, 1).concat(prevButtons.slice(2,3))).forEach(b => {
	        b.addEventListener("click", (e) => { 
	          var eventfieldset = e.target.closest("fieldset"),
	            valid = true;
	          if (e.target.className == "next") {
	            for (i = 0; i < eventfieldset.elements.length; i++) {
	              if (!eventfieldset.elements[i].checkValidity()) {
	                eventfieldset.elements[i].reportValidity();
	                valid = false;
	                break;
	              }
	            }
		        var radioButtons = eventfieldset.querySelectorAll('input[name="album"]');
	            for (var radio of radioButtons) {
	                if (radio.checked) {
	                    self.selectedAlbumOption = radio.value;
	                    break;
	                }
	            }
	          }
	          
	          if (valid) {
				if ((self.selectedAlbumOption) && self.selectedAlbumOption == 1)
	            	this.changeStep(e.target.parentNode, e.target.closest("form").children[1]);
	            else this.changeStep(e.target.parentNode, e.target.closest("form").children[2]);
	          }
	        }, false);
	      });
	      
	      // Manage submit button
	      this.wizard.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
	        var eventfieldset = e.target.closest("fieldset"),
	          valid = true;
	        for (i = 0; i < eventfieldset.elements.length; i++) {
	          if (!eventfieldset.elements[i].checkValidity()) {
	            eventfieldset.elements[i].reportValidity();
	            valid = false;
	            break;
	          }
	        }

	        if (valid) {
	          var self = this;
	          makeCall("POST", 'UploadTrack', e.target.closest("form"),
	            function(req) {
	              if (req.readyState == XMLHttpRequest.DONE) {
	                var message = req.responseText; // error message, if present
	                self.alert.textContent = message;
	                if (req.status == 200) {
	                  orchestrator.refreshNoAutoclick();
	                } else {
	                  self.reset();
	                }
	              }
	            }
	          );
	        }
	      });
	      // Manage cancel button
	      this.wizard.querySelector("input[type='button'].cancel").addEventListener('click', (e) => {
	        e.target.closest('form').reset();
	        this.reset();
	      });
	    };

	    this.reset = function() {
	      // CSS-like selector: fieldsets inside #uploadtrackform
	      var fieldsets = document.querySelectorAll("#" + this.wizard.id + " fieldset");
	      fieldsets[0].hidden = false;
	      fieldsets[1].hidden = true;
	      fieldsets[2].hidden = true;
		  fieldsets[3].hidden = true;
		  
		  // set max year to new album
		  fieldsets[2].querySelectorAll("input[name='albumYear']")[0].max = new Date().getFullYear();
	    }

	    this.changeStep = function(origin, destination) {
	      origin.hidden = true;
	      destination.hidden = false;
	    }
	  }


    function PageOrchestrator() {
	    // var alertContainer = document.getElementById("id_alert");
        this.start = function() {
            personalMessage = new PersonalMessage(sessionStorage.getItem('user'),
                document.getElementById("id_username"));
            personalMessage.show();

            reorderModal = new Modal(
             	document.getElementById("reorder_modal"),
             	document.getElementById("modal_playlist_name")
            );
            reorderModal.registerEvents(this);

            playlists = new Playlists(
                document.getElementById("playlistsError"),
                document.getElementById("playlistCreationError"),
                document.getElementById("zeroPlaylists"),
                document.getElementById("playlistscontainer"),
                document.getElementById("playlistsbody"),
                document.getElementById("createplaylistform"),
                reorderModal
            );
            playlists.registerEvents(this);
            
            playlistTracks = new PlaylistTracks({
            	alert: document.getElementById("playlistTracksError"),
                formalert: document.getElementById("addToPlaylistError"),
                playlistname: document.getElementById("playlist_name"),
                trackscontainer: document.getElementById("track_groups"),
                form: document.getElementById("addtrackform")
            });
            playlistTracks.registerEvents(this);

            trackDetails = new TrackDetails({
                alert: document.getElementById("notrackselected"),
                container: document.getElementById("track_player"),
                title: document.getElementById("track_title"),
                genre: document.getElementById("track_genre"),
                albumname: document.getElementById("album_name"),
                albumartist: document.getElementById("album_artist"),
                albumyear: document.getElementById("album_year"),
                albumimage: document.getElementsByClassName("album_image"),
                player: document.getElementById("track_audioplayer")
            });

            trackForm = new TrackForm(document.getElementById("uploadtrackform"), document.getElementById("trackFormError"));
            trackForm.registerEvents(this);

            document.querySelector("a[href='Logout']").addEventListener('click', () => {
                window.sessionStorage.removeItem('user');
            })
        };

        this.refresh = function (currentPlaylist) {
            reorderModal.reset();
            playlists.reset();
            playlistTracks.reset();
            trackDetails.reset();
            playlists.show(function () {
                playlists.autoclick(currentPlaylist);
            }); // closure preserves visibility of this
            trackForm.reset();
            trackForm.show();
        };
        
        this.refreshNoAutoclick = function () {
            playlists.reset();
            playlistTracks.reset();
            playlists.show(function () {
                playlists.autoclick();
            });
            trackForm.reset();
            trackForm.show();
        };
    }
    
    function parallax()
	{
	    var background = document.getElementsByClassName('album_image')[0]; 
	    background.style.top = window.pageYOffset + 'px';
	}
	
	window.addEventListener("scroll", parallax, false);
	
})();
