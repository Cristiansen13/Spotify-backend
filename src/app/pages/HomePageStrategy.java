package app.pages;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.user.User;

import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Home page.
 */
public final class HomePageStrategy implements PageStrategy {
    private List<Song> likedSongs;
    private List<Playlist> followedPlaylists;
    @Setter@Getter
    private Song recommendedSong;
    @Setter@Getter
    private Playlist recommendedPlaylist;
    @Setter@Getter
    private String lastRecommendation = null;
    private final int limit = 5;

    /**
     * Instantiates a new Home page.
     *
     * @param user the user
     */
    public HomePageStrategy(final User user) {
        likedSongs = user.getLikedSongs();
        followedPlaylists = user.getFollowedPlaylists();
    }

    @Override
    public String printCurrentPage() {
        String result = "Liked songs:\n\t%s\n\nFollowed playlists:\n\t%s"
               .formatted(likedSongs.stream()
                                    .sorted(Comparator.comparing(Song::getLikes)
                                    .reversed()).limit(limit).map(Song::getName)
                          .toList(),
                          followedPlaylists.stream().sorted((o1, o2) ->
                                  o2.getSongs().stream().map(Song::getLikes)
                                    .reduce(Integer::sum).orElse(0)
                                  - o1.getSongs().stream().map(Song::getLikes).reduce(Integer::sum)
                                  .orElse(0)).limit(limit).map(Playlist::getName)
                          .toList());
        if (recommendedSong != null) {
            result += "\n\nSong recommendations:\n\t[%s]".formatted(recommendedSong.getName());
        } else {
            result += "\n\nSong recommendations:\n\t[]";
        }
        if (recommendedPlaylist != null) {
            result += "\n\nPlaylists recommendations:\n\t[%s]".formatted(
                recommendedPlaylist.getName());
        } else {
            result += "\n\nPlaylists recommendations:\n\t[]";
        }
        return result;
    }
}
