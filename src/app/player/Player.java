package app.player;

import app.Admin;
import app.audio.Collections.Album;
import app.audio.Collections.AudioCollection;
import app.audio.Collections.Podcast;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.user.Artist;
import app.user.Host;
import app.utils.Enums.PlayerSourceType;
import app.utils.Enums.RepeatMode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Player.
 */
public final class Player {
    private RepeatMode repeatMode;
    private boolean shuffle;
    private boolean paused;
    @Getter
    private PlayerSource source;
    @Getter
    private String type;
    private final int skipTime = 90;

    private ArrayList<PodcastBookmark> bookmarks = new ArrayList<>();
    @Getter
    private ListenRecord listenRecord = new ListenRecord();

    /**
     * Instantiates a new Player.
     */
    public Player() {
        this.repeatMode = RepeatMode.NO_REPEAT;
        this.paused = true;
    }

    /**
     * Stop.
     */
    public void stop() {
        if ("podcast".equals(this.type)) {
            bookmarkPodcast();
        }

        repeatMode = RepeatMode.NO_REPEAT;
        paused = true;
        source = null;
        shuffle = false;
    }

    private void bookmarkPodcast() {
        if (source != null && source.getAudioFile() != null) {
            PodcastBookmark currentBookmark =
                    new PodcastBookmark(source.getAudioCollection().getName(),
                                        source.getIndex(),
                                        source.getDuration());
            bookmarks.removeIf(bookmark -> bookmark.getName().equals(currentBookmark.getName()));
            bookmarks.add(currentBookmark);
        }
    }

    /**
     * Create source player source.
     *
     * @param type      the type
     * @param entry     the entry
     * @param bookmarks the bookmarks
     * @return the player source
     */
    public static PlayerSource createSource(final String type,
                                            final LibraryEntry entry,
                                            final List<PodcastBookmark> bookmarks) {
        if ("song".equals(type)) {
            return new PlayerSource(PlayerSourceType.LIBRARY, (AudioFile) entry);
        } else if ("playlist".equals(type)) {
            return new PlayerSource(PlayerSourceType.PLAYLIST, (AudioCollection) entry);
        } else if ("podcast".equals(type)) {
            return createPodcastSource((AudioCollection) entry, bookmarks);
        } else if ("album".equals(type)) {
            return new PlayerSource(PlayerSourceType.ALBUM, (AudioCollection) entry);
        }

        return null;
    }

    private static PlayerSource createPodcastSource(final AudioCollection collection,
                                                    final List<PodcastBookmark> bookmarks) {
        for (PodcastBookmark bookmark : bookmarks) {
            if (bookmark.getName().equals(collection.getName())) {
                return new PlayerSource(PlayerSourceType.PODCAST, collection, bookmark);
            }
        }
        return new PlayerSource(PlayerSourceType.PODCAST, collection);
    }

    /**
     * Sets source.
     *
     * @param entry      the entry
     * @param sourceType the sourceType
     */
    public void setSource(final LibraryEntry entry, final String sourceType) {
        if ("podcast".equals(this.type)) {
            bookmarkPodcast();
        }

        this.type = sourceType;
        this.source = createSource(sourceType, entry, bookmarks);
        this.repeatMode = RepeatMode.NO_REPEAT;
        this.shuffle = false;
        this.paused = true;
    }

    /**
     * Pause.
     */
    public void pause() {
        paused = !paused;
    }

    /**
     * Shuffle.
     *
     * @param seed the seed
     */
    public void shuffle(final Integer seed) {
        if (seed != null) {
            source.generateShuffleOrder(seed);
        }

        if (source.getType() == PlayerSourceType.PLAYLIST
            || source.getType() == PlayerSourceType.ALBUM) {
            shuffle = !shuffle;
            if (shuffle) {
                source.updateShuffleIndex();
            }
        }
    }

    /**
     * Repeat enums . repeat mode.
     *
     * @return the enums . repeat mode
     */
    public RepeatMode repeat() {
        if (repeatMode == RepeatMode.NO_REPEAT) {
            if (source.getType() == PlayerSourceType.LIBRARY) {
                repeatMode = RepeatMode.REPEAT_ONCE;
            } else {
                repeatMode = RepeatMode.REPEAT_ALL;
            }
        } else {
            if (repeatMode == RepeatMode.REPEAT_ONCE) {
                repeatMode = RepeatMode.REPEAT_INFINITE;
            } else {
                if (repeatMode == RepeatMode.REPEAT_ALL) {
                    repeatMode = RepeatMode.REPEAT_CURRENT_SONG;
                } else {
                    repeatMode = RepeatMode.NO_REPEAT;
                }
            }
        }

        return repeatMode;
    }

    /**
     * Simulate player.
     *
     * @param time the time
     */
    public void simulatePlayer(final int time) {
        int elapsedTime = time;
        if (!paused) {
            while (elapsedTime >= source.getDuration()) {
                addToRecord();
                elapsedTime -= source.getDuration();
                next();
                if (paused) {
                    break;
                }
            }
            if (!paused) {
                if (!(elapsedTime == 0 && !listenRecord.getListenedSongs().isEmpty()
                    && source.getAudioCollection() != null
                    && !listenRecord.getListenedSongs()
                    .get(listenRecord.getListenedSongs().size() - 1).equals(source.getAudioFile()
                        .getName()))) {
                    addToRecord();
                }
                source.skip(-elapsedTime);
            }
        }
    }

    private void addToRecord() {
        Integer total = null;
        Admin admin = Admin.getInstance();
        for (Song song : admin.getSongs()) {
            if (song.getName().equals(source.getAudioFile().getName()) && song.getAlbum().equals(((Song) source.getAudioFile()).getAlbum())) {
                total = song.getDuration();
                break;
            }
        }
        if (total == null) {
            if (listenRecord.getListenedEpisodes().isEmpty()
                || !listenRecord.getListenedEpisodes().get(listenRecord.getListenedEpisodes().size() - 1).equals(source.getAudioFile().getName())) {
                listenRecord.getListenedEpisodes().add(source.getAudioFile().getName());
                for (Host host : admin.getHosts()) {
                    Podcast podcast = (Podcast) source.getAudioCollection();
                    if (podcast.getOwner().equals(host.getUsername())) {
                        host.getListenedEpisodes().add(source.getAudioFile().getName());
                        listenRecord.getListenedHosts().add(source.getAudioCollection().getOwner());
                    }
                }
            }
        } else if (total == source.getDuration()) {
            listenRecord.getListenedSongs().add(source.getAudioFile().getName());
            listenRecord.getListenedArtists().add(((Song) source.getAudioFile()).getArtist());
            listenRecord.getListenedGenres().add(((Song) source.getAudioFile()).getGenre());
            listenRecord.getListenedAlbums().add(((Song) source.getAudioFile()).getAlbum());
            for (Artist artist : admin.getArtists()) {
                if (artist.getUsername().equals(((Song) source.getAudioFile()).getArtist())) {
                    for (Album album : artist.getAlbums()) {
                        if (album.getName().equals(((Song) source.getAudioFile()).getAlbum())) {
                            artist.getStats().getListenedAlbums()
                                .add(((Song) source.getAudioFile()).getAlbum());
                            artist.getStats().getListenedSongs().add(source.getAudioFile().getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Next.
     */
    public void next() {
        paused = source.setNextAudioFile(repeatMode, shuffle);
        if (repeatMode == RepeatMode.REPEAT_ONCE) {
            repeatMode = RepeatMode.NO_REPEAT;
        }

        if (source.getDuration() == 0 && paused) {
            stop();
        }
    }

    /**
     * Prev.
     */
    public void prev() {
        source.setPrevAudioFile(shuffle);
        paused = false;
    }

    private void skip(final int duration) {
        source.skip(duration);
        paused = false;
    }

    /**
     * Skip next.
     */
    public void skipNext() {
        if (source.getType() == PlayerSourceType.PODCAST) {
            skip(-skipTime);
        }
    }

    /**
     * Skip prev.
     */
    public void skipPrev() {
        if (source.getType() == PlayerSourceType.PODCAST) {
            skip(skipTime);
        }
    }

    /**
     * Gets current audio file.
     *
     * @return the current audio file
     */
    public AudioFile getCurrentAudioFile() {
        if (source == null) {
            return null;
        }
        return source.getAudioFile();
    }

    /**
     * Gets current audio collection.
     *
     * @return the current audio collection
     */
    public AudioCollection getCurrentAudioCollection() {
        if (source == null) {
            return null;
        }
        return source.getAudioCollection();
    }

    /**
     * Gets paused.
     *
     * @return the paused
     */
    public boolean getPaused() {
        return paused;
    }

    /**
     * Gets shuffle.
     *
     * @return the shuffle
     */
    public boolean getShuffle() {
        return shuffle;
    }

    /**
     * Gets stats.
     *
     * @return the stats
     */
    public PlayerStats getStats() {
        String filename = "";
        int duration = 0;
        if (source != null && source.getAudioFile() != null) {
            filename = source.getAudioFile().getName();
            duration = source.getDuration();
        } else {
            stop();
        }

        return new PlayerStats(filename, duration, repeatMode, shuffle, paused);
    }
}
