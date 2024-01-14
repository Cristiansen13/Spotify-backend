package app.Command.UserCommands;
import app.Admin;
import app.Command.Command;
import app.pages.ArtistPage;
import app.pages.HostPage;
import app.user.Artist;
import app.user.Host;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

public class SubscribeCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private static final ObjectMapper ObjectMapper = new ObjectMapper();
    public SubscribeCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Subscribe object node.
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
            if (user.getCurrentPage().printCurrentPage().startsWith("Album")) {
                ArtistPage artistPage = (ArtistPage) user.getCurrentPage();
                for (Artist artist : admin.getArtists()) {
                    if (artist.getUsername().equals(artistPage.getArtistName())) {
                        if (artist.getObservers().contains(user)) {
                            artist.removeObserver(user);
                            objectNode.put("message", commandInput
                                .getUsername() + " unsubscribed from "
                                + artist.getUsername() + " successfully.");
                        } else {
                            artist.addObserver(user);
                            objectNode.put("message", commandInput
                                .getUsername() + " subscribed to "
                                + artist.getUsername() + " successfully.");
                        }
                        break;
                    }
                }
            } else if (user.getCurrentPage().printCurrentPage().startsWith("Podcast")) {
                HostPage hostPage = (HostPage) user.getCurrentPage();
                for (Host host : admin.getHosts()) {
                    if (host.getUsername().equals(hostPage.getHostName())) {
                        if (host.getObservers().contains(user)) {
                            host.removeObserver(user);
                            objectNode.put("message", commandInput
                                .getUsername() + " unsubscribed from "
                                + host.getUsername() + " successfully.");
                        } else {
                            host.addObserver(user);
                            objectNode.put("message", commandInput
                                .getUsername() + " subscribed to " + host.getUsername()
                                + " successfully.");
                        }
                        break;
                    }
                }
            } else {
                objectNode.put("message", "To subscribe you need to be on "
                    + "the page of an artist or host.");
            }
        } else {
            objectNode.put("message", "The username " + commandInput.getUsername()
                + " doesn't exist.");
        }
        return objectNode;
    }
}
