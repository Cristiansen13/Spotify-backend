package app.Command.PlayerCommands;
import app.Admin;
import app.Command.Command;
import app.searchBar.Filters;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import java.util.ArrayList;

public class SearchCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SearchCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Search object node.
     *
     * @return the object node
     */
    @Override
    public ObjectNode execute() {
        User user = admin.getUser(commandInput.getUsername());
        Filters filters = new Filters(commandInput.getFilters());
        String type = commandInput.getType();
        ArrayList<String> results = new ArrayList<>();
        String message = "%s is offline.".formatted(user.getUsername());

        if (user.isStatus()) {
            results = user.search(filters, type);
            message = "Search returned " + results.size() + " results";
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        objectNode.put("results", objectMapper.valueToTree(results));

        return objectNode;
    }
}
