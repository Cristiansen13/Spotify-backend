package app.user;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistStats {
    private ArrayList<String> listenedAlbums = new ArrayList<>();
    private ArrayList<String> listenedSongs = new ArrayList<>();
    private Integer merchRevenue = 0;
    private double songRevenue = 0;
    private String mostProfitableSong = null;
    private double mostProfitableSongRevenue = 0;
    private HashMap<String, Double> winningsPerSong = new HashMap<>();
    /**
     * Add merch revenue.
     *
     * @param revenue earned from merch.
     */
    public void addMerchRevenue(final Integer revenue) {
        merchRevenue += revenue;
    }
}
