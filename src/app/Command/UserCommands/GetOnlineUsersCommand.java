package app.Command.UserCommands;
import app.Admin;
import app.Command.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import java.util.List;

public class GetOnlineUsersCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private static final ObjectMapper ObjectMapper = new ObjectMapper();
    public GetOnlineUsersCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Gets online users.
     *
     * @return the online users
     */
    public ObjectNode execute() {
        List<String> onlineUsers = admin.getOnlineUsers();
        ObjectNode objectNode = ObjectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", ObjectMapper.valueToTree(onlineUsers));

        return objectNode;
    }
}
