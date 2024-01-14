package app.Command.StatsCommands;
import app.Admin;
import app.Command.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import java.util.List;

public class GetTop5ArtistListCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public GetTop5ArtistListCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Gets top 5 artists.
     * @return the top 5 artists
     */
    public ObjectNode execute() {
        List<String> artists = admin.getTop5ArtistList();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(artists));

        return objectNode;
    }
}
