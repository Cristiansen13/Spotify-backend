package app.Command.UserCommands;
import app.Admin;
import app.Command.Command;
import app.pages.ArtistPageStrategy;
import app.user.Artist;
import app.user.Merchandise;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

public class BuyMerchCommand implements Command {
    private final Admin admin;
    private final CommandInput commandInput;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public BuyMerchCommand(final Admin admin, final CommandInput commandInput) {
        this.admin = admin;
        this.commandInput = commandInput;
    }
    /**
     * Buy merch object node.
     *
     * @return the object node
     */
    public ObjectNode execute() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            if (user.getCurrentPageStrategy().printCurrentPage().startsWith("Album")) {
                ArtistPageStrategy artistPage = (ArtistPageStrategy) user.getCurrentPageStrategy();
                String purchasedMerch = null;
                Integer price = null;
                for (Merchandise merchandise : artistPage.getMerch()) {
                    if (merchandise.getName().equals(commandInput.getName())) {
                        purchasedMerch = merchandise.getName();
                        price = merchandise.getPrice();
                    }
                }
                if (purchasedMerch != null) {
                    user.getBoughtMerchandise().add(purchasedMerch);
                    for (Artist artist : admin.getArtists()) {
                        for (Merchandise merchandise : artist.getMerch()) {
                            if (merchandise.getName().equals(purchasedMerch)) {
                                artist.getStats().addMerchRevenue(price);
                            }
                        }
                    }
                    objectNode.put("message", commandInput.getUsername()
                        + " has added new merch successfully.");
                } else {
                    objectNode.put("message", "The merch " + commandInput.getName()
                        + " doesn't exist.");
                }
            } else {
                objectNode.put("message", "Cannot buy merch from this page.");
            }
        } else {
            objectNode.put("message", "The username " + commandInput.getUsername()
                + " doesn't exist.");
        }
        return objectNode;
    }
}
