package app.Command.UserCommands;

import app.Admin;
import app.Command.Command;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

public class NextPageCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper= new ObjectMapper();
    public NextPageCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Next page object node.
     *
     * @return the object node
     */
    public ObjectNode execute() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        User user = admin.getUser(commandInput.getUsername());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        if (user.getPageIndex() == user.getPageStrategies().size() - 3) {
            objectNode.put("message", "There are no pages left to go forward.");
            return objectNode;
        }
        user.setPageIndex(user.getPageIndex() + 1);
        user.setCurrentPageStrategy(user.getPageStrategies().get(user.getPageIndex()));
        objectNode.put("message", "The user " + commandInput.getUsername()
            + " has navigated successfully to the next page.");
        return objectNode;
    }
}
