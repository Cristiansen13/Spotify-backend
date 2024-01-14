package app.Command.ArtistCommands;
import app.Admin;
import app.Command.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

public class AddAlbumCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public AddAlbumCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Add album object node.
     *
     * @return the object node
     */
    public ObjectNode execute() {
        String message = admin.addAlbum(commandInput);
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }
}
