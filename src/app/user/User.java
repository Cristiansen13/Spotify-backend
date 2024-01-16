package app.user;

import app.Admin;
import app.audio.Collections.Album;
import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.PlaylistOutput;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.pages.HomePageStrategy;
import app.pages.LikedContentPageStrategy;
import app.pages.PageStrategy;
import app.player.Player;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.searchBar.SearchBar;
import app.utils.Enums;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The type User.
 */
public final class User extends UserAbstract {
    @Getter
    private ArrayList<Playlist> playlists;
    @Getter
    private ArrayList<Song> likedSongs;
    @Getter
    private ArrayList<Playlist> followedPlaylists;
    @Getter
    private final Player player;
    @Getter
    private boolean status;
    private final SearchBar searchBar;
    private boolean lastSearched;
    @Getter
    @Setter
    private PageStrategy currentPageStrategy;
    @Getter
    @Setter
    private HomePageStrategy homePage;
    @Getter
    @Setter
    private LikedContentPageStrategy likedContentPage;
    @Getter
    private ArrayList<String> boughtMerchandise = new ArrayList<>();
    @Getter@Setter
    private ArrayList<PageStrategy> pageStrategies = new ArrayList<>();
    @Getter@Setter
    private Integer pageIndex = -1;
    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public User(final String username, final int age, final String city) {
        super(username, age, city);
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        player = new Player();
        searchBar = new SearchBar(username);
        lastSearched = false;
        status = true;

        homePage = new HomePageStrategy(this);
        currentPageStrategy = homePage;
        likedContentPage = new LikedContentPageStrategy(this);
    }

    @Override
    public String userType() {
        return "user";
    }

    /**
     * Search array list.
     *
     * @param filters the filters
     * @param type    the type
     * @return the array list
     */
    public ArrayList<String> search(final Filters filters, final String type) {
        searchBar.clearSelection();
        if (player.getSource() != null) {
            Admin admin = Admin.getInstance();
            boolean isSong = false;
            for (Song song : admin.getSongs()) {
                if (song.getName().equals(player.getSource().getAudioFile()
                    .getName()) && song.getAlbum().equals(((Song) player
                    .getSource().getAudioFile()).getAlbum())) {
                    isSong = true;
                    break;
                }
            }
            if (isSong && (player.getListenRecord().getListenedSongs().isEmpty()
                || !player.getSource().getAudioFile().getName().equals(player
                .getListenRecord().getListenedSongs().get(player.getListenRecord()
                    .getListenedSongs().size() - 1)))) {
                player.getListenRecord().getListenedSongs().add(player.getSource()
                    .getAudioFile().getName());
                player.getListenRecord().getListenedArtists().add(((Song) player
                    .getSource().getAudioFile()).getArtist());
                player.getListenRecord().getListenedGenres().add(((Song) player
                    .getSource().getAudioFile()).getGenre());
                player.getListenRecord().getListenedAlbums().add(((Song) player
                    .getSource().getAudioFile()).getAlbum());
                for (Artist artist : admin.getArtists()) {
                    if (artist.getUsername().equals(((Song) player.getSource()
                        .getAudioFile()).getArtist())) {
                        for (Album album : artist.getAlbums()) {
                            if (album.getName().equals(((Song) player.getSource()
                                .getAudioFile()).getAlbum())) {
                                artist.getStats().getListenedAlbums()
                                    .add(((Song) player.getSource().getAudioFile())
                                        .getAlbum());
                                artist.getStats().getListenedSongs().add(player
                                    .getSource().getAudioFile().getName());
                            }
                        }
                    }
                }
            }
        }
        player.stop();

        lastSearched = true;
        ArrayList<String> results = new ArrayList<>();

        if (type.equals("artist") || type.equals("host")) {
            List<ContentCreator> contentCreatorsEntries =
            searchBar.searchContentCreator(filters, type);

            for (ContentCreator contentCreator : contentCreatorsEntries) {
                results.add(contentCreator.getUsername());
            }
        } else {
            List<LibraryEntry> libraryEntries = searchBar.search(filters, type);

            for (LibraryEntry libraryEntry : libraryEntries) {
                results.add(libraryEntry.getName());
            }
        }
        return results;
    }

    /**
     * Select string.
     *
     * @param itemNumber the item number
     * @return the string
     */
    public String select(final int itemNumber) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (!lastSearched) {
            return "Please conduct a search before making a selection.";
        }

        lastSearched = false;

        if (searchBar.getLastSearchType().equals("artist")
            || searchBar.getLastSearchType().equals("host")) {
            ContentCreator selected = searchBar.selectContentCreator(itemNumber);

            if (selected == null) {
                return "The selected ID is too high.";
            }

            currentPageStrategy = selected.getPage();
            return "Successfully selected %s's page.".formatted(selected.getUsername());
        } else {
            LibraryEntry selected = searchBar.select(itemNumber);

            if (selected == null) {
                return "The selected ID is too high.";
            }

            return "Successfully selected %s.".formatted(selected.getName());
        }
    }

    /**
     * Load string.
     *
     * @return the string
     */
    public String load() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (searchBar.getLastSelected() == null) {
            return "Please select a source before attempting to load.";
        }

        if (!searchBar.getLastSearchType().equals("song")
            && ((AudioCollection) searchBar.getLastSelected()).getNumberOfTracks() == 0) {
            return "You can't load an empty audio collection!";
        }
        player.setSource(searchBar.getLastSelected(), searchBar.getLastSearchType());
        searchBar.clearSelection();

        player.pause();

        return "Playback loaded successfully.";
    }

    /**
     * Play pause string.
     *
     * @return the string
     */
    public String playPause() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to pause or resume playback.";
        }

        player.pause();

        if (player.getPaused()) {
            return "Playback paused successfully.";
        } else {
            return "Playback resumed successfully.";
        }
    }

    /**
     * Repeat string.
     *
     * @return the string
     */
    public String repeat() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before setting the repeat status.";
        }

        Enums.RepeatMode repeatMode = player.repeat();
        String repeatStatus = "";

        switch (repeatMode) {
            case NO_REPEAT -> {
                repeatStatus = "no repeat";
            }
            case REPEAT_ONCE -> {
                repeatStatus = "repeat once";
            }
            case REPEAT_ALL -> {
                repeatStatus = "repeat all";
            }
            case REPEAT_INFINITE -> {
                repeatStatus = "repeat infinite";
            }
            case REPEAT_CURRENT_SONG -> {
                repeatStatus = "repeat current song";
            }
            default -> {
                repeatStatus = "";
            }
        }

        return "Repeat mode changed to %s.".formatted(repeatStatus);
    }

    /**
     * Shuffle string.
     *
     * @param seed the seed
     * @return the string
     */
    public String shuffle(final Integer seed) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before using the shuffle function.";
        }

        if (!player.getType().equals("playlist")
            && !player.getType().equals("album")) {
            return "The loaded source is not a playlist or an album.";
        }

        player.shuffle(seed);

        if (player.getShuffle()) {
            return "Shuffle function activated successfully.";
        }
        return "Shuffle function deactivated successfully.";
    }

    /**
     * Forward string.
     *
     * @return the string
     */
    public String forward() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to forward.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipNext();

        return "Skipped forward successfully.";
    }

    /**
     * Backward string.
     *
     * @return the string
     */
    public String backward() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please select a source before rewinding.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipPrev();

        return "Rewound successfully.";
    }

    /**
     * Like string.
     *
     * @return the string
     */
    public String like() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before liking or unliking.";
        }

        if (!player.getType().equals("song") && !player.getType().equals("playlist")
            && !player.getType().equals("album")) {
            return "Loaded source is not a song.";
        }

        Song song = (Song) player.getCurrentAudioFile();

        if (likedSongs.contains(song)) {
            likedSongs.remove(song);
            song.dislike();

            return "Unlike registered successfully.";
        }

        likedSongs.add(song);
        song.like();
        return "Like registered successfully.";
    }

    /**
     * Next string.
     *
     * @return the string
     */
    public String next() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        player.next();

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        return "Skipped to next track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Prev string.
     *
     * @return the string
     */
    public String prev() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before returning to the previous track.";
        }

        player.prev();

        return "Returned to previous track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Create playlist string.
     *
     * @param name      the name
     * @param timestamp the timestamp
     * @return the string
     */
    public String createPlaylist(final String name, final int timestamp) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (playlists.stream().anyMatch(playlist -> playlist.getName().equals(name))) {
            return "A playlist with the same name already exists.";
        }

        playlists.add(new Playlist(name, getUsername(), timestamp));
        notifyObservers("playlist");
        return "Playlist created successfully.";
    }

    /**
     * Add remove in playlist string.
     *
     * @param id the id
     * @return the string
     */
    public String addRemoveInPlaylist(final int id) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before adding to or removing from the playlist.";
        }

        if (player.getType().equals("podcast")) {
            return "The loaded source is not a song.";
        }

        if (id > playlists.size()) {
            return "The specified playlist does not exist.";
        }

        Playlist playlist = playlists.get(id - 1);

        if (playlist.containsSong((Song) player.getCurrentAudioFile())) {
            playlist.removeSong((Song) player.getCurrentAudioFile());
            return "Successfully removed from playlist.";
        }

        playlist.addSong((Song) player.getCurrentAudioFile());
        return "Successfully added to playlist.";
    }

    /**
     * Switch playlist visibility string.
     *
     * @param playlistId the playlist id
     * @return the string
     */
    public String switchPlaylistVisibility(final Integer playlistId) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (playlistId > playlists.size()) {
            return "The specified playlist ID is too high.";
        }

        Playlist playlist = playlists.get(playlistId - 1);
        playlist.switchVisibility();

        if (playlist.getVisibility() == Enums.Visibility.PUBLIC) {
            return "Visibility status updated successfully to public.";
        }

        return "Visibility status updated successfully to private.";
    }

    /**
     * Show playlists array list.
     *
     * @return the array list
     */
    public ArrayList<PlaylistOutput> showPlaylists() {
        ArrayList<PlaylistOutput> playlistOutputs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistOutputs.add(new PlaylistOutput(playlist));
        }

        return playlistOutputs;
    }

    /**
     * Follow string.
     *
     * @return the string
     */
    public String follow() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        LibraryEntry selection = searchBar.getLastSelected();
        String type = searchBar.getLastSearchType();

        if (selection == null) {
            return "Please select a source before following or unfollowing.";
        }

        if (!type.equals("playlist")) {
            return "The selected source is not a playlist.";
        }

        Playlist playlist = (Playlist) selection;

        if (playlist.getOwner().equals(getUsername())) {
            return "You cannot follow or unfollow your own playlist.";
        }

        if (followedPlaylists.contains(playlist)) {
            followedPlaylists.remove(playlist);
            playlist.decreaseFollowers();

            return "Playlist unfollowed successfully.";
        }

        followedPlaylists.add(playlist);
        playlist.increaseFollowers();


        return "Playlist followed successfully.";
    }

    /**
     * Gets player stats.
     *
     * @return the player stats
     */
    public PlayerStats getPlayerStats() {
        return player.getStats();
    }

    /**
     * Show preferred songs array list.
     *
     * @return the array list
     */
    public ArrayList<String> showPreferredSongs() {
        ArrayList<String> results = new ArrayList<>();
        for (AudioFile audioFile : likedSongs) {
            results.add(audioFile.getName());
        }

        return results;
    }

    /**
     * Gets preferred genre.
     *
     * @return the preferred genre
     */
    public String getPreferredGenre() {
        String[] genres = {"pop", "rock", "rap"};
        int[] counts = new int[genres.length];
        int mostLikedIndex = -1;
        int mostLikedCount = 0;

        for (Song song : likedSongs) {
            for (int i = 0; i < genres.length; i++) {
                if (song.getGenre().equals(genres[i])) {
                    counts[i]++;
                    if (counts[i] > mostLikedCount) {
                        mostLikedCount = counts[i];
                        mostLikedIndex = i;
                    }
                    break;
                }
            }
        }

        String preferredGenre = mostLikedIndex != -1 ? genres[mostLikedIndex] : "unknown";
        return "This user's preferred genre is %s.".formatted(preferredGenre);
    }

    /**
     * Switch status.
     */
    public void switchStatus() {
        status = !status;
    }

    /**
     * Simulate time.
     *
     * @param time the time
     */
    public void simulateTime(final int time) {
        if (!status) {
            return;
        }

        player.simulatePlayer(time);
    }

    /**
     * Handle the update when a new playlist is created.
     *
     * @param updatedUser the updated user
     */
    @Override
    protected void update(final UserAbstract updatedUser, final String type) {
        if (type.equals("playlist")) {
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.put("name", "New Playlist");
            objectNode.put("description", "New Playlist from " + this.getUsername());
            updatedUser.getNotifications().add(objectNode);
        }
    }
    /**
     * Update recommendations song.
     */
    public void updateRecommendationsSong() {
        Song currentSong = (Song) this.player.getCurrentAudioFile();
        String currentgenre = currentSong.getGenre();
        ArrayList<Song> songs = (ArrayList<Song>) Admin.getInstance().getSongs();
        ArrayList<Song> songsByGenre = new ArrayList<>();
        for (Song song : songs) {
            if (song.getGenre().equals(currentgenre)) {
                songsByGenre.add(song);
            }
        }
        int seed = this.player.getSource().getAudioFile().getDuration() - this.player.getSource().getDuration();
        Random random = new Random(seed);
        int randomIndex = random.nextInt(songsByGenre.size());
        Song randomSong = songsByGenre.get(randomIndex);
        this.homePage.setRecommendedSong(randomSong);
        this.homePage.setLastRecommendation("song");
    }
    /**
     * Update recommendations playlist.
     *
     * @param timestamp the timestamp
     */
    public void updateRecommendationsPlaylist(final int timestamp) {
        ArrayList<String> genres = new ArrayList<>();
        for (Song song : this.likedSongs) {
           genres.add(song.getGenre());
        }
        for (Playlist playlist : this.followedPlaylists) {
            for (Song song : playlist.getSongs()) {
                genres.add(song.getGenre());
            }
        }
        for (Playlist playlist : this.playlists) {
            for (Song song : playlist.getSongs()) {
                genres.add(song.getGenre());
            }
        }
        Map<String, Integer> genreCountMap = new HashMap<>();
        for (String genre : genres) {
            genreCountMap.put(genre, genreCountMap.getOrDefault(genre, 0) + 1);
        }
        List<String> top3Genres = genreCountMap.entrySet()
            .stream()
            .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
            .limit(3)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        ArrayList<Song> top1GenreSongs = new ArrayList<>();
        ArrayList<Song> top2GenreSongs = new ArrayList<>();
        ArrayList<Song> top3GenreSongs = new ArrayList<>();
        Admin admin = Admin.getInstance();
        for (Song song : admin.getSongs()) {
            if (!top3Genres.isEmpty() && song.getGenre().equals(top3Genres.get(0))) {
                top1GenreSongs.add(song);
            } else if (top3Genres.size() > 1 && song.getGenre().equals(top3Genres.get(1))) {
                top2GenreSongs.add(song);
            } else if (top3Genres.size() > 2 && song.getGenre().equals(top3Genres.get(2))) {
                top3GenreSongs.add(song);
            }
        }
        top1GenreSongs.sort(Song.getLikesComparator());
        top2GenreSongs.sort(Song.getLikesComparator());
        top3GenreSongs.sort(Song.getLikesComparator());
        for (int i = top1GenreSongs.size() - 1; i >= 5; i--) {
            top1GenreSongs.remove(i);
        }
        for (int i = top2GenreSongs.size() - 1; i >= 3; i--) {
            top2GenreSongs.remove(i);
        }
        for (int i = top3GenreSongs.size() - 1; i >= 2; i--) {
            top3GenreSongs.remove(i);
        }
        Playlist playlist = new Playlist(this.getUsername() + "'s recommendations", this.getUsername(), timestamp);
        playlist.addSongs(top1GenreSongs);
        playlist.addSongs(top2GenreSongs);
        playlist.addSongs(top3GenreSongs);
        this.homePage.setRecommendedPlaylist(playlist);
        this.homePage.setLastRecommendation("playlist");
    }
    /**
     * Update recommendations fans playlist.
     *
     * @param timestamp the timestamp
     */
    public void updateRecommendationsFansPlaylist(final Integer timestamp) {
        Admin admin = Admin.getInstance();
        Song currentSong = (Song) this.player.getCurrentAudioFile();
        Artist artist = admin.getArtist(currentSong.getArtist());
        List<String> fans = artist.topFans();
        Playlist playlist = new Playlist(artist.getUsername() + " Fan Club recommendations", this.getUsername(), timestamp);
        for (String fan : fans) {
            User user = admin.getUser(fan);
            ArrayList<Song> songs = user.getLikedSongs();
            songs.sort(Song.getLikesComparator());
            for (int i = songs.size() - 1; i >= 5; i--) {
                songs.remove(i);
            }
            playlist.addSongs(songs);
        }
        this.homePage.setRecommendedPlaylist(playlist);
        this.homePage.setLastRecommendation("playlist");
    }

    /**
     * Load recommendations.
     */
    public String loadRecommendations() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }
        if (this.homePage.getLastRecommendation() == null) {
            return "Please select a source before loading recommendations.";
        }
        if (this.homePage.getLastRecommendation().equals("song")) {
            player.setSource(this.homePage.getRecommendedSong(), this.homePage.getLastRecommendation());
        } else if (this.homePage.getLastRecommendation().equals("playlist")){
            player.setSource(this.homePage.getRecommendedPlaylist(), this.homePage.getLastRecommendation());
        }
        player.pause();

        return "Playback loaded successfully.";
    }

    /**
     * Sets premium.
     */
    public void setPremium(final boolean premium) {
        this.player.setPremium(premium);
    }
    /**
     * Gets premium.
     *
     * @return the premium
     */
    public boolean getPremium() {
        return this.player.isPremium();
    }
}
