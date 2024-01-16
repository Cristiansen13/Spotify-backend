package app.Command.UserCommands;
import app.Admin;
import app.Command.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

public class PrintCurrentPage implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public PrintCurrentPage(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Print current page object node.
     * @return the object node
     */
    public ObjectNode execute() {
        String message = admin.printCurrentPage(commandInput);
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }
}
