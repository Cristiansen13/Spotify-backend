package app.user;

import app.Admin;
import app.player.ListenRecord;
import java.util.ArrayList;
import lombok.Getter;

public class ArtistStats {
    @Getter
    private ArrayList<String> listenedAlbums = new ArrayList<>();
    @Getter
    private ArrayList<String> listenedSongs = new ArrayList<>();
    @Getter
    private Integer merchRevenue = 0;
}
