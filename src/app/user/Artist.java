package app.user;

import app.Admin;
import app.player.ListenRecord;
import app.player.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import app.audio.Collections.Album;
import app.audio.Collections.AlbumOutput;
import app.audio.Files.Song;
import app.pages.ArtistPage;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Artist.
 */
public final class Artist extends ContentCreator {
    private ArrayList<Album> albums;
    private ArrayList<Merchandise> merch;
    private ArrayList<Event> events;
    @Getter
    private ArtistStats stats = new ArtistStats();
    @Getter
    @Setter
    boolean isVerified = false;
    /**
     * Instantiates a new Artist.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public Artist(final String username, final int age, final String city) {
        super(username, age, city);
        albums = new ArrayList<>();
        merch = new ArrayList<>();
        events = new ArrayList<>();

        super.setPage(new ArtistPage(this));
    }

    /**
     * Gets albums.
     *
     * @return the albums
     */
    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /**
     * Gets merch.
     *
     * @return the merch
     */
    public ArrayList<Merchandise> getMerch() {
        return merch;
    }

    /**
     * Gets events.
     *
     * @return the events
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Gets event.
     *
     * @param eventName the event name
     * @return the event
     */
    public Event getEvent(final String eventName) {
        for (Event event : events) {
            if (event.getName().equals(eventName)) {
                return event;
            }
        }

        return null;
    }

    /**
     * Gets album.
     *
     * @param albumName the album name
     * @return the album
     */
    public Album getAlbum(final String albumName) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }

        return null;
    }

    /**
     * Gets all songs.
     *
     * @return the all songs
     */
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        albums.forEach(album -> songs.addAll(album.getSongs()));

        return songs;
    }

    /**
     * Show albums array list.
     *
     * @return the array list
     */
    public ArrayList<AlbumOutput> showAlbums() {
        ArrayList<AlbumOutput> albumOutput = new ArrayList<>();
        for (Album album : albums) {
            albumOutput.add(new AlbumOutput(album));
        }

        return albumOutput;
    }

    /**
     * Get user type
     *
     * @return user type string
     */
    public String userType() {
        return "artist";
    }

    public List<String> topFans() {
        Map<String, Long> fanOccurrences = new HashMap<>();
        Admin admin = Admin.getInstance();
        for (User user : admin.getUsers()) {
            ListenRecord listenRecord = user.getPlayer().getListenRecord();
            for (String artist : listenRecord.getListenedArtists()) {
                if (artist.equals(this.getUsername())) {
                    fanOccurrences.merge(user.getUsername(), 1L, Long::sum);
                }
            }
        }
        List<String> topFans = fanOccurrences.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()))
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        return topFans;
    }


    public Integer getNumberOfListeners() {
        Set<String> uniqueFans = new HashSet<>();
        Admin admin = Admin.getInstance();

        for (User user : admin.getUsers()) {
            ListenRecord listenRecord = user.getPlayer().getListenRecord();
            for (String artist : listenRecord.getListenedArtists()) {
                if (artist.equals(this.getUsername())) {
                    uniqueFans.add(user.getUsername());
                }
            }
        }
        return uniqueFans.size();
    }

}