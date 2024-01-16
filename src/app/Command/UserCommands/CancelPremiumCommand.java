package app.Command.UserCommands;

import app.Admin;
import app.Command.Command;
import app.audio.Files.Song;
import app.user.Artist;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import java.util.ArrayList;

public class CancelPremiumCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Integer userMoney = 1000000;
    public CancelPremiumCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Cancel premium page object node.
     *
     * @return the object node
     */
    public ObjectNode execute() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        User user = admin.getUser(commandInput.getUsername());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        if (!user.getPremium()) {
            objectNode.put("message", commandInput.getUsername()
                + " is not a premium user.");
            return objectNode;
        }
        objectNode.put("message",
            commandInput.getUsername() + " cancelled the subscription successfully.");
        user.setPremium(false);

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
                            artist.getStats().getWinningsPerSong().put(song
                                .getName(), songwinnings);
                        } else {
                            artist.getStats().getWinningsPerSong().put(song.getName(),
                                artist.getStats().getWinningsPerSong()
                                    .get(song.getName()) + songwinnings);
                        }
                        if (artist.getStats().getMostProfitableSongRevenue() < artist.getStats()
                            .getWinningsPerSong().get(song.getName())
                            || (artist.getStats().getMostProfitableSongRevenue() == artist
                            .getStats().getWinningsPerSong().get(song.getName())
                            && artist.getStats().getMostProfitableSong()
                            .compareTo(song.getName()) > 0)) {
                            artist.getStats().setMostProfitableSong(song.getName());
                            artist.getStats().setMostProfitableSongRevenue(artist
                                .getStats().getWinningsPerSong().get(song.getName()));
                        }
                        break;
                    }
                }
                updatedSongs.add(song.getName());
            }
        }
        return objectNode;
    }
}
