package app.Command.UserCommands;

import app.Admin;
import app.Command.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import app.user.User;
public class BuyPremiumCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public BuyPremiumCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Buy premium page object node.
     *
     * @return the object node
     */
    public ObjectNode execute() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        User user = admin.getUser(commandInput.getUsername());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        if (user.getPremium()) {
            objectNode.put("message", commandInput.getUsername()
                + " is already a premium user.");
            return objectNode;
        }
        objectNode.put("message", commandInput.getUsername()
            + " bought the subscription successfully.");
        user.setPremium(true);
        return objectNode;
    }
}
