package app.Command.StatsCommands;
import app.Admin;
import app.Command.Command;
import app.audio.Collections.PodcastOutput;
import app.user.Host;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import java.util.List;

public class ShowPodcastsCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public ShowPodcastsCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Show podcasts object node.
     *
     * @return the object node
     */
    public ObjectNode execute() {
        Host host = admin.getHost(commandInput.getUsername());
        List<PodcastOutput> podcasts = host.getPodcasts().stream().map(PodcastOutput::new).toList();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(podcasts));

        return objectNode;
    }
}
