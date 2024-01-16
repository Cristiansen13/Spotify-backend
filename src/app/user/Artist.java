package app.user;

import app.Admin;
import app.player.ListenRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import app.audio.Collections.Album;
import app.audio.Collections.AlbumOutput;
import app.audio.Files.Song;
import app.pages.ArtistPageStrategy;
import java.util.Map;
import java.util.Set;
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
    private boolean isVerified = false;
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

        super.setPage(new ArtistPageStrategy(this));
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
     * Add album.
     *
     * @param album the album to be added.
     */
    public void addAlbum(final Album album) {
        albums.add(album);
        notifyObservers("album");
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
     * Add merch.
     *
     * @param merchItem the merch item to be added.
     */
    public void addMerch(final Merchandise merchItem) {
        merch.add(merchItem);
        notifyObservers("merch");
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
     * Add event.
     *
     * @param event the event to be added.
     */
    public void addEvent(final Event event) {
        events.add(event);
        notifyObservers("event");
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

    /**
     * Gets top fans.
     *
     * @return the top fans
     */
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
            .limit(Admin.getInstance().getLimit())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        return topFans;
    }

    /**
     * Gets number of listeners.
     *
     * @return the number of listeners
     */
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

    /**
     * Handle the update when something new is added.
     *
     * @param updatedUser the updated user
     */
    @Override
    protected void update(final UserAbstract updatedUser, final String type) {
        if (type.equals("album")) {
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.put("name", "New Album");
            objectNode.put("description", "New Album from " + this.getUsername() + ".");
            updatedUser.getNotifications().add(objectNode);
        } else if (type.equals("merch")) {
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.put("name", "New Merchandise");
            objectNode.put("description", "New Merchandise from " + this.getUsername() + ".");
            updatedUser.getNotifications().add(objectNode);
        } else if (type.equals("event")) {
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.put("name", "New Event");
            objectNode.put("description", "New Event from " + this.getUsername() + ".");
            updatedUser.getNotifications().add(objectNode);
        }
    }
}
