package app.Command.UserCommands;

import app.Admin;
import app.Command.Command;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

public class UpdateRecommendationsCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UpdateRecommendationsCommand(Admin admin, CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    public ObjectNode execute() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        User user = admin.getUser(commandInput.getUsername());
        if (commandInput.getRecommendationType().equals("random_song")){
            user.updateRecommendationsSong();
        }
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", "The recommendations for user " + user.getUsername() + " have been updated successfully.");
        return objectNode;
    }
}
