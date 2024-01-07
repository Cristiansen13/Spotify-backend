package app.player;

import java.util.ArrayList;
import lombok.Getter;

public class ListenRecord {
    @Getter
    private ArrayList<String> listenedArtists = new ArrayList<>();
    @Getter
    private ArrayList<String> listenedAlbums = new ArrayList<>();
    @Getter
    private ArrayList<String> listenedGenres = new ArrayList<>();
    @Getter
    private ArrayList<String> listenedSongs = new ArrayList<>();
    @Getter
    private ArrayList<String> listenedEpisodes = new ArrayList<>();
    @Getter
    private ArrayList<String> listenedHosts = new ArrayList<>();
}
