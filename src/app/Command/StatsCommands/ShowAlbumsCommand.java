package app.Command.StatsCommands;
import app.Admin;
import app.Command.Command;
import app.audio.Collections.AlbumOutput;
import app.user.Artist;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import java.util.ArrayList;

public class ShowAlbumsCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public ShowAlbumsCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Show Albums object node.
     *
     * @return the object node
     */
    public ObjectNode execute() {
        Artist artist = admin.getArtist(commandInput.getUsername());
        ArrayList<AlbumOutput> albums = artist.showAlbums();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(albums));

        return objectNode;
    }
}
