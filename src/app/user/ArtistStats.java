package app.user;

import java.util.ArrayList;
import lombok.Getter;

@Getter
public class ArtistStats {
    private ArrayList<String> listenedAlbums = new ArrayList<>();
    private ArrayList<String> listenedSongs = new ArrayList<>();
    private Integer merchRevenue = 0;

    /**
     * Add merch revenue.
     *
     * @param revenue earned from merch.
     */
    public void addMerchRevenue(final Integer revenue) {
        merchRevenue += revenue;
    }
}
