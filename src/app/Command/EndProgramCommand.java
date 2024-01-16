package app.Command;
import app.Admin;
import app.audio.Files.Song;
import app.user.Artist;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;

public class EndProgramCommand implements Command {
    private final Admin admin;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public EndProgramCommand(final Admin admin) {
        this.admin = admin;
    }
    private final Integer userMoney = 1000000;
    private final double roundHelper = 100.0;
    /**
     * Runs at the end of the program to give a general stat about the artits
     * @return an object node with the stat
     */
    public ObjectNode execute() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "endProgram");
        ObjectNode result = objectMapper.createObjectNode();
        Admin admin = Admin.getInstance();

        for (User user : admin.getUsers()) {
            if (user.getPremium()) {
                ArrayList<String> updatedSongs = new ArrayList<>();
                for (Song song : user.getPlayer().getListenRecord().getPremiumListenedSongs()) {
                    if (!updatedSongs.contains(song.getName())) {
                        double songwinnings = 0;
                        for (Artist artist : admin.getArtists()) {
                            if (song.getArtist().equals(artist.getUsername())) {
                                songwinnings += (double) userMoney / user.getPlayer()
                                    .getListenRecord().getPremiumListenedSongs().size();
                                int nroccurencies = 0;
                                for (Song samesong : user.getPlayer().getListenRecord()
                                    .getPremiumListenedSongs()) {
                                    if (samesong.getName().equals(song.getName())) {
                                        nroccurencies++;
                                    }
                                }
                                songwinnings *= nroccurencies;
                                artist.getStats().setSongRevenue(artist.getStats()
                                    .getSongRevenue() + songwinnings);
                                if (artist.getStats().getWinningsPerSong()
                                    .get(song.getName()) == null) {
                                    artist.getStats().getWinningsPerSong()
                                        .put(song.getName(), songwinnings);
                                } else {
                                    artist.getStats().getWinningsPerSong().put(song.getName(),
                                        artist.getStats().getWinningsPerSong().get(song.getName())
                                            + songwinnings);
                                }
                                if (artist.getStats().getMostProfitableSongRevenue()
                                    < artist.getStats().getWinningsPerSong().get(song.getName())
                                    || (artist.getStats().getMostProfitableSongRevenue()
                                    == artist.getStats().getWinningsPerSong().get(song.getName())
                                    && artist.getStats().getMostProfitableSong()
                                    .compareTo(song.getName()) > 0)) {
                                    artist.getStats().setMostProfitableSong(song.getName());
                                    artist.getStats().setMostProfitableSongRevenue(artist.getStats()
                                        .getWinningsPerSong().get(song.getName()));
                                }
                                break;
                            }
                        }
                        updatedSongs.add(song.getName());
                    }
                }
            }
        }

        ArrayList<Artist> listenedArtists = new ArrayList<>();
        for (Artist artist : admin.getArtists()) {
            if (!artist.getStats().getListenedAlbums().isEmpty()
                || artist.getStats().getMerchRevenue() != 0) {
                listenedArtists.add(artist);
            }
        }
        int verifiedArtists = 0;
        while (verifiedArtists < listenedArtists.size()) {
            double maxMoney = 0;
            double songRevenue = 0;
            Artist currentArtist = null;
            for (Artist artist : listenedArtists) {
                if (!artist.isVerified()) {
                    double money = artist.getStats().getMerchRevenue();
                    songRevenue = artist.getStats().getSongRevenue();
                    money += songRevenue;
                    if (money > maxMoney) {
                        maxMoney = money;
                        currentArtist = artist;
                    }
                    if (currentArtist == null) {
                        currentArtist = artist;
                    } else if (money == maxMoney
                        && artist.getUsername()
                        .compareTo(currentArtist.getUsername()) < 0) {
                        currentArtist = artist;
                    }
                }
            }
            currentArtist.setVerified(true);
            ObjectNode artistNode = objectMapper.createObjectNode();
            artistNode.put("merchRevenue", maxMoney - songRevenue);
            artistNode.put("songRevenue", Math
                .round(songRevenue * roundHelper) / roundHelper);
            artistNode.put("ranking", verifiedArtists + 1);
            if (currentArtist.getStats().getMostProfitableSongRevenue() == 0) {
                artistNode.put("mostProfitableSong", "N/A");
            } else {
                artistNode.put("mostProfitableSong", currentArtist
                    .getStats().getMostProfitableSong());
            }
            result.put(currentArtist.getUsername(), artistNode);
            verifiedArtists++;
        }
        objectNode.put("result", result);
        return objectNode;
    }
}
