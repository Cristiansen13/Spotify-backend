package app.Command.UserCommands;
import app.Admin;
import app.Command.Command;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

public class GetPreferredGenreCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private static final ObjectMapper ObjectMapper = new ObjectMapper();
    public GetPreferredGenreCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Get preferred object node.
     *
     * @return the object node
     */
    public ObjectNode execute() {
        User user = admin.getUser(commandInput.getUsername());
        String preferredGenre = user.getPreferredGenre();

        ObjectNode objectNode = ObjectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", ObjectMapper.valueToTree(preferredGenre));

        return objectNode;
    }
}
