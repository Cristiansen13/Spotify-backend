package main;

import app.Admin;
import app.Command.ArtistCommands.AddAlbumCommand;
import app.Command.HostCommands.AddAnnouncementCommand;
import app.Command.ArtistCommands.AddEventCommand;
import app.Command.ArtistCommands.AddMerchCommand;
import app.Command.HostCommands.AddPodcastCommand;
import app.Command.UserCommands.UpdateRecommendationsCommand;
import app.Command.UserCommands.AddRemoveInPlaylistCommand;
import app.Command.UserCommands.AddUserCommand;
import app.Command.PlayerCommands.BackwardCommand;
import app.Command.UserCommands.BuyMerchCommand;
import app.Command.UserCommands.ChangePageCommand;
import app.Command.Command;
import app.Command.UserCommands.CreatePlaylistCommand;
import app.Command.UserCommands.DeleteUserCommand;
import app.Command.EndProgramCommand;
import app.Command.UserCommands.FollowCommand;
import app.Command.PlayerCommands.ForwardCommand;
import app.Command.UserCommands.GetAllUsersCommand;
import app.Command.UserCommands.GetNotificationsCommand;
import app.Command.UserCommands.GetOnlineUsersCommand;
import app.Command.UserCommands.GetPreferredGenreCommand;
import app.Command.StatsCommands.GetTop5AlbumListCommand;
import app.Command.StatsCommands.GetTop5ArtistListCommand;
import app.Command.StatsCommands.GetTop5PlaylistCommand;
import app.Command.StatsCommands.GetTop5SongsCommand;
import app.Command.UserCommands.LikeCommand;
import app.Command.PlayerCommands.LoadCommand;
import app.Command.PlayerCommands.NextCommand;
import app.Command.PlayerCommands.PlayPauseCommand;
import app.Command.PlayerCommands.PrevCommand;
import app.Command.UserCommands.PrintCurrentPage;
import app.Command.ArtistCommands.RemoveAlbumCommand;
import app.Command.HostCommands.RemoveAnnouncementCommand;
import app.Command.ArtistCommands.RemoveEventCommand;
import app.Command.HostCommands.RemovePodcastCommand;
import app.Command.PlayerCommands.RepeatCommand;
import app.Command.PlayerCommands.SearchCommand;
import app.Command.UserCommands.SeeMerchCommand;
import app.Command.PlayerCommands.SelectCommand;
import app.Command.StatsCommands.ShowAlbumsCommand;
import app.Command.StatsCommands.ShowLikedSongsCommand;
import app.Command.StatsCommands.ShowPlaylistsCommand;
import app.Command.StatsCommands.ShowPodcastsCommand;
import app.Command.PlayerCommands.ShuffleCommand;
import app.Command.UserCommands.StatusCommand;
import app.Command.UserCommands.SubscribeCommand;
import app.Command.UserCommands.SwitchConnectionStatusCommand;
import app.Command.UserCommands.SwitchVisibilityCommand;
import app.Command.StatsCommands.WrappedCommand;
import app.searchBar.SearchBar;
import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.CommandInput;
import fileio.input.LibraryInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
        final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH
                + "library/library.json"),
            LibraryInput.class);
        CommandInput[] commands = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH
                + filePath1),
            CommandInput[].class);
        ArrayNode outputs = objectMapper.createArrayNode();

        Admin admin = Admin.getInstance();
        SearchBar.updateAdmin();
        admin.setUsers(library.getUsers());
        admin.setSongs(library.getSongs());
        admin.setPodcasts(library.getPodcasts());
        System.out.println("----------------------------------------");
        for (CommandInput command : commands) {
            admin.updateTimestamp(command.getTimestamp());

            String commandName = command.getCommand();
            Command commandObject = null;
            switch (commandName) {
                case "search" -> {
                    commandObject = new SearchCommand(admin, command); }
                case "select" -> {
                    commandObject = new SelectCommand(admin, command);
                }
                case "load" -> {
                    commandObject = new LoadCommand(admin, command);
                }
                case "playPause" -> {
                    commandObject = new PlayPauseCommand(admin, command);
                }
                case "repeat" -> {
                    commandObject = new RepeatCommand(admin, command);
                }
                case "shuffle" -> {
                    commandObject = new ShuffleCommand(admin, command);
                }
                case "forward" -> {
                    commandObject = new ForwardCommand(admin, command);
                }
                case "backward" -> {
                    commandObject = new BackwardCommand(admin, command);
                }
                case "like" -> {
                    commandObject = new LikeCommand(admin, command);
                }
                case "next" -> {
                    commandObject = new NextCommand(admin, command);
                }
                case "prev" -> {
                    commandObject = new PrevCommand(admin, command);
                }
                case "createPlaylist" -> {
                    commandObject = new CreatePlaylistCommand(admin,
                        command);
                }
                case "addRemoveInPlaylist" -> {
                    commandObject = new AddRemoveInPlaylistCommand(admin,
                        command);
                }
                case "switchVisibility" -> {
                    commandObject = new SwitchVisibilityCommand(admin,
                        command);
                }
                case "showPlaylists" -> {
                    commandObject = new ShowPlaylistsCommand(admin, command);
                }
                case "follow" -> {
                    commandObject = new FollowCommand(admin, command);
                }
                case "status" -> {
                    commandObject = new StatusCommand(admin, command);
                }
                case "showPreferredSongs" -> {
                    commandObject = new ShowLikedSongsCommand(admin,
                        command);
                }
                case "getPreferredGenre" -> {
                    commandObject = new GetPreferredGenreCommand(admin,
                        command);
                }
                case "getTop5Songs" -> {
                    commandObject = new GetTop5SongsCommand(admin,
                        command);
                }
                case "getTop5Playlists" -> {
                    commandObject = new GetTop5PlaylistCommand(admin,
                        command);
                }
                case "switchConnectionStatus" -> {
                    commandObject = new SwitchConnectionStatusCommand(admin,
                        command);
                }
                case "addUser" -> {
                    commandObject = new AddUserCommand(admin, command);
                }
                case "deleteUser" -> {
                    commandObject = new DeleteUserCommand(admin, command);
                }
                case "addPodcast" -> {
                    commandObject = new AddPodcastCommand(admin, command);
                }
                case "removePodcast" -> {
                    commandObject = new RemovePodcastCommand(admin, command);
                }
                case "addAnnouncement" -> {
                    commandObject = new AddAnnouncementCommand(admin, command);
                }
                case "removeAnnouncement" -> {
                    commandObject = new RemoveAnnouncementCommand(admin, command);
                }
                case "addAlbum" -> {
                    commandObject = new AddAlbumCommand(admin, command);
                }
                case "removeAlbum" -> {
                    commandObject = new RemoveAlbumCommand(admin, command);
                }
                case "addEvent" -> {
                    commandObject = new AddEventCommand(admin, command);
                }
                case "removeEvent" -> {
                    commandObject = new RemoveEventCommand(admin, command);
                }
                case "addMerch" -> {
                    commandObject = new AddMerchCommand(admin, command);
                }
                case "changePage" -> {
                    commandObject = new ChangePageCommand(admin, command);
                }
                case "printCurrentPage" -> {
                    commandObject = new PrintCurrentPage(admin, command);
                }
                case "getTop5Albums" -> {
                    commandObject = new GetTop5AlbumListCommand(admin, command);
                }
                case "getTop5Artists" -> {
                    commandObject = new GetTop5ArtistListCommand(admin, command);
                }
                case "getAllUsers" -> {
                    commandObject = new GetAllUsersCommand(admin, command);
                }
                case "getOnlineUsers" -> {
                    commandObject = new GetOnlineUsersCommand(admin, command);
                }
                case "showAlbums" -> {
                    commandObject = new ShowAlbumsCommand(admin, command);
                }
                case "showPodcasts" -> {
                    commandObject = new ShowPodcastsCommand(admin, command);
                }
                case "wrapped" -> {
                    commandObject = new WrappedCommand(admin, command);
                }
                case "buyMerch" -> {
                    commandObject = new BuyMerchCommand(admin, command);
                }
                case "seeMerch" -> {
                    commandObject = new SeeMerchCommand(admin, command);
                }
                case "subscribe" -> {
                    commandObject = new SubscribeCommand(admin, command);
                }
                case "getNotifications" ->
                    commandObject = new GetNotificationsCommand(admin, command);
                case "updateRecommendations" -> {
                    commandObject = new UpdateRecommendationsCommand(admin, command);
                }
                default -> System.out.println("Invalid command " + commandName);
            }
            if (commandObject != null) {
                outputs.add(commandObject.execute());
            }
        }
        Command EndProgram = new EndProgramCommand(admin);
        outputs.add(EndProgram.execute());
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), outputs);

        Admin.resetInstance();
    }
}
