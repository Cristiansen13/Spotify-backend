package app.Command;
import app.Admin;
import app.user.Artist;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;

public class EndProgramCommand implements Command {
    private final Admin admin;
    private static final ObjectMapper ObjectMapper = new ObjectMapper();
    public EndProgramCommand(final Admin admin) {
        this.admin = admin;
    }

    /**
     * Runs at the end of the program to give a general stat about the artits
     * @return an object node with the stat
     */
    public ObjectNode execute() {
        ObjectNode objectNode = ObjectMapper.createObjectNode();
        objectNode.put("command", "endProgram");
        ObjectNode result = ObjectMapper.createObjectNode();
        Admin admin = Admin.getInstance();
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
            ObjectNode artistNode = ObjectMapper.createObjectNode();
            artistNode.put("merchRevenue", maxMoney - songRevenue);
            artistNode.put("songRevenue", songRevenue);
            artistNode.put("ranking", verifiedArtists + 1);
            if (songRevenue == 0) {
                artistNode.put("mostProfitableSong", "N/A");
            }
            result.put(currentArtist.getUsername(), artistNode);
            verifiedArtists++;
        }
        objectNode.put("result", result);
        return objectNode;
    }
}
