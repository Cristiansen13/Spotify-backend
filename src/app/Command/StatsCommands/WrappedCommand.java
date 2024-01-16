package app.Command.StatsCommands;
import app.Admin;
import app.Command.Command;
import app.user.Artist;
import app.user.Host;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WrappedCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public WrappedCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Gets 5 most frequent elements from an array of strings
     * @param list the list to work with
     * @return an object node with the most frequent strings and their frequency
     */
    private ObjectNode getTop5(final ArrayList<String> list) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String element : list) {
            frequencyMap.put(element, frequencyMap.getOrDefault(element, 0) + 1);
        }
        Comparator<String> frequencyComparator = (element1, element2) -> {
            int frequencyCompare = frequencyMap.get(element2).compareTo(frequencyMap.get(element1));
            return frequencyCompare != 0 ? frequencyCompare : element1.compareTo(element2);
        };
        list.sort(frequencyComparator);
        List<String> firstDistinctElements = new ArrayList<>();
        List<String> seenElements = new ArrayList<>();
        for (String element : list) {
            if (!seenElements.contains(element)) {
                firstDistinctElements.add(element);
                seenElements.add(element);
                if (firstDistinctElements.size() == admin.getLimit()) {
                    break;
                }
            }
        }
        for (String word : firstDistinctElements) {
            objectNode.put(word, frequencyMap.get(word));
        }
        return objectNode;
    }
    /**
     * Creates wrap for a user
     * @return the object node
     */
    public ObjectNode execute() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        LinkedHashMap<String, Object> results = new LinkedHashMap<>();
        User user = admin.getUser(commandInput.getUsername());
        Artist artist = admin.getArtist(commandInput.getUsername());
        Host host = admin.getHost(commandInput.getUsername());
        if (user != null) {
            if (getTop5(
                user.getPlayer().getListenRecord().getListenedArtists()).isEmpty()
                && getTop5(
                user.getPlayer().getListenRecord().getListenedSongs()).isEmpty()
                && getTop5(
                user.getPlayer().getListenRecord().getListenedGenres()).isEmpty()
                && getTop5(
                user.getPlayer().getListenRecord().getListenedAlbums()).isEmpty()
                && getTop5(
                user.getPlayer().getListenRecord().getListenedEpisodes()).isEmpty()) {
                objectNode.put("message", "No data to show for user " + user.getUsername() + ".");
            } else {
                ObjectNode topArtists = getTop5(
                    user.getPlayer().getListenRecord().getListenedArtists());
                results.put("topArtists", objectMapper.valueToTree(topArtists));
                ObjectNode topGenres = getTop5(
                    user.getPlayer().getListenRecord().getListenedGenres());
                results.put("topGenres", objectMapper.valueToTree(topGenres));
                ObjectNode topSongs = getTop5(
                    user.getPlayer().getListenRecord().getListenedSongs());
                results.put("topSongs", objectMapper.valueToTree(topSongs));
                ObjectNode topAlbums = getTop5(
                    user.getPlayer().getListenRecord().getListenedAlbums());
                results.put("topAlbums", objectMapper.valueToTree(topAlbums));
                ObjectNode topEpisodes = getTop5(
                    user.getPlayer().getListenRecord().getListenedEpisodes());
                results.put("topEpisodes", objectMapper.valueToTree(topEpisodes));
                objectNode.put("result", objectMapper.valueToTree(results));
            }
        } else if (artist != null) {
            if (getTop5(artist.getStats().getListenedAlbums()).isEmpty()
                && getTop5(artist.getStats().getListenedSongs()).isEmpty()
                && artist.topFans().isEmpty() && artist.getNumberOfListeners() == 0) {
                objectNode.put("message", "No data to show for artist "
                    + artist.getUsername() + ".");
            } else {
                ObjectNode topAlbums = getTop5(artist.getStats().getListenedAlbums());
                results.put("topAlbums", objectMapper.valueToTree(topAlbums));
                ObjectNode topSongs = getTop5(artist.getStats().getListenedSongs());
                results.put("topSongs", objectMapper.valueToTree(topSongs));
                List<String> topFans = artist.topFans();
                results.put("topFans", objectMapper.valueToTree(topFans));
                Integer listeners = artist.getNumberOfListeners();
                results.put("listeners", objectMapper.valueToTree(listeners));
                objectNode.put("result", objectMapper.valueToTree(results));
            }
        } else if (host != null) {
            ObjectNode topEpisodes = getTop5(host.getListenedEpisodes());
            results.put("topEpisodes", objectMapper.valueToTree(topEpisodes));
            Integer listeners = host.getNumberofListeners();
            results.put("listeners", objectMapper.valueToTree(listeners));
            objectNode.put("result", objectMapper.valueToTree(results));
        }
        return objectNode;
    }
}
