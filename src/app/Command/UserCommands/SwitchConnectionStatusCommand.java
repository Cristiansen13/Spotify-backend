package app.Command.UserCommands;
import app.Admin;
import app.Command.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

public class SwitchConnectionStatusCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private static final ObjectMapper ObjectMapper = new ObjectMapper();
    public SwitchConnectionStatusCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Switch connection status object node.
     *
     * @return the object node
     */
    public ObjectNode execute() {
        String message = admin.switchStatus(commandInput.getUsername());
        ObjectNode objectNode = ObjectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }
}
