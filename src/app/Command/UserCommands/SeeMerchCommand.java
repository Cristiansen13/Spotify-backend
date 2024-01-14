package app.Command.UserCommands;
import app.Admin;
import app.Command.Command;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

public class SeeMerchCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private static final ObjectMapper ObjectMapper = new ObjectMapper();
    public SeeMerchCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * See merch object node.
     *
     * @return the object node
     */
    public ObjectNode execute() {
        ObjectNode objectNode = ObjectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            objectNode.put("result", ObjectMapper
                .valueToTree(user.getBoughtMerchandise()));
        }
        return objectNode;
    }
}
