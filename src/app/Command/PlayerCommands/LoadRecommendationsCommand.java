package app.Command.PlayerCommands;

import app.Admin;
import app.Command.Command;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

public class LoadRecommendationsCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public LoadRecommendationsCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Load recommendations object node.
     *
     * @return the object node
     */
    @Override
    public ObjectNode execute() {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.loadRecommendations();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }
}