(function() {

    //page components
    var Playlist, PlaylistDetails, //maybe also wizard for previous and next
        pageOrchestrator = new PageOrchestrator();

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

    function Playlists(_alert, _listcontainer, _listcontainerbody, _form) {
        this.alert = _alert;
        this.playlistscontainer = _listcontainer;
        this.playlistsbody = _listcontainerbody;
        this.createplaylistform = _form;

        this.reset = function () {
            this.playlistscontainer.style.visibility = "hidden";
            //this.createplaylistform.style.visibility = "hidden";
        }
        
        this.registerEvents = function (orchestrator) {
            this.createplaylistform.querySelector("input[type='button']").addEventListener('click', (e) => {
                var form = e.target.closest("form");
                if (form.checkValidity()) {
                    var self = this;
                    makeCall("POST", "CreatePlaylist", form,
                        function (req) {
                            if (req.readyState == 4) {
                                var message = req.responseText;
                                if (req.status == 200) {                                
                                    orchestrator.refresh(message); // error or new id
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
        }

        this.show = function(next) {
            //self per avere riferimento a playlist tramite closure
            var self = this;
            makeCall("GET", "GetPlaylistsData", null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var playlistsToShow = JSON.parse(req.responseText);
                            if (playlistsToShow.length == 0) {
                                self.alert.textContent = "No playlists available to display.";
                                return;
                            }
                            self.update(playlistsToShow); // self visible by closure
                            if (next) next(); // show the default element of the list if present
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
                //anchor.missionid = mission.id; // make list item clickable
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

                //anchor.setAttribute('playlist', playlist.id); // set a custom HTML attribute
                //anchor.addEventListener("click", (e) => {
                //  playlistTracks.show(e.target.getAttribute("playlistid")); // the list must know the details container
                //}, false);
                reorderanchor.href = "#";
                row.appendChild(reordercell);

                self.playlistsbody.appendChild(row);
            });
            this.playlistscontainer.style.visibility = "visible";

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
                                    self.alert.textContent = message;
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
        	this.playlistid = playlistid; // TODO this line is to avoid session storage, but might have to be removed if JWT will be used
            var self = this;
            this.playlistname.textContent = name;
            makeCall("GET", "GetPlaylistTracks?playlistid=" + playlistid, null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var tracksToShow = JSON.parse(req.responseText);
                            if (tracksToShow.length == 0) {
                                self.alert.textContent = "This playlist is empty.";
                                return;
                            }
                            self.update(tracksToShow); // self visible by closure
                            if (playlistid) self.autoclick(); // TODO possibly refactor this line (if (next) next();)
                        }
                    } else {
                        self.alert.textContent = message;
                    }
                }
            );
        };


        this.reset = function () {
            this.trackscontainer.style.visibility = "hidden";
            this.trackscontainer.innerHTML = "";
                        
            //reset add track form
            trackselect = this.addform.getElementsByTagName("select")[0];
            
            //TODO maybe filter tracks not in the playlist, but this would require to move code out of reset()
            makeCall("GET", "GetUserTracks", null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var tracksToShow = JSON.parse(req.responseText);
                            tracksToShow.forEach(function(track) {
                            	var row = document.createElement("option");
                            	row.value = track.id;
                            	row.textContent = track.title /*+ " (by " + track.album.artist + ")"*/;
                            	
                            	trackselect.appendChild(row);
                            });
                        }
                    } else {
                        self.alert.textContent = message;
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
                row = document.createElement("tr");
                trackGroup.forEach(function (track) { // self visible here, not this
                    cell = document.createElement("td");
                    cell.className = "boxed";

                    titlecell = document.createElement("span");
                    titlecell.textContent = track.title;
                    cell.appendChild(titlecell);
                    cell.appendChild(document.createElement("br"));

                    imagecell = document.createElement("img");
                    imagecell.style.height = '200px';
                    imagecell.style.width = '160px';

                    imagecell.src = "data:image/*;base64," + track.image;
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
                    linkcell.href = "#";
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
	                	e.target.closest("div").hidden = true;
	                	e.target.closest("div").previousElementSibling.hidden = false;
	                });
	                groupdiv.appendChild(previous);
	                
	                groupdiv.hidden = true; // hide next elements at the beginning
                }

				if (i < parseInt((arrayTracks.length - 1) / 5)) {
	                next = document.createElement("button");
	                next.innerHTML = "Next";
	                next.addEventListener("click", (e) => {
	          			e.target.closest("div").hidden = true;
	                	e.target.closest("div").nextElementSibling.hidden = false;
	                });
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

        //here wizard code.js
    }

    function TrackDetails(options) {
        this.alert = options['alert'];
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
                            self.alert.textContent = message;
                        }
                    }
                }
            );
        };

        this.reset = function () {
            this.track_player.style.visibility = "hidden";
        }

        this.update = function (track) {
            this.title.textContent = track.title;
            this.genre.textContent = track.genre;
            this.albumname.textContent = track.album.name;
            this.albumartist.textContent = track.album.artist;
            this.albumyear.textContent = track.album.year;
            this.albumimage.src = "data:image/*;base64," + track.album.image;
            this.player.src = "data:audio/*;base64," + track.audio;
        }
    }

    //TODO TRACKFORM...


    function PageOrchestrator() {

        var alertContainer = document.getElementById("id_alert"); //reference in HomeCS

        this.start = function() {
            personalMessage = new PersonalMessage(sessionStorage.getItem('user'),
                document.getElementById("id_username"));
            personalMessage.show();

            playlists = new Playlists(
                alertContainer,
                document.getElementById("playlistscontainer"),
                document.getElementById("playlistsbody"),
                document.getElementById("createplaylistform")
            );
            playlists.registerEvents(this);

            playlistTracks = new PlaylistTracks({
                alert: alertContainer,
                playlistname: document.getElementById("playlist_name"),
                trackscontainer: document.getElementById("track_groups"),
                form: document.getElementById("addtrackform")
                //wizard here
            });
            playlistTracks.registerEvents(this);

            trackDetails = new TrackDetails({
                alert: alertContainer,
                container: document.getElementById("track_player"),
                title: document.getElementById("track_title"),
                genre: document.getElementById("track_genre"),
                albumname: document.getElementById("album_name"),
                albumartist: document.getElementById("album_artist"),
                albumyear: document.getElementById("album_year"),
                albumimage: document.getElementById("album_image"),
                player: document.getElementById("track_audioplayer")
            });

            //trackForm = new TrackForm(document.getElementById("uploadtrackform"), alertContainer);
            //trackForm.registerEvents(this);

            document.querySelector("a[href='Logout']").addEventListener('click', () => {
                window.sessionStorage.removeItem('user');
            })
        };

        this.refresh = function (currentPlaylist) {
            alertContainer.textContent = "";
            playlists.reset();
            playlistTracks.reset();
            trackDetails.reset();
            playlists.show(function () {
                playlists.autoclick(currentPlaylist);
            }); // closure preserves visibility of this
            //trackForm.reset();
        };
    }
})();
