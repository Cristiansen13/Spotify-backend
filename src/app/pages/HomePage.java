package app.pages;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.user.User;

import java.util.Comparator;
import java.util.List;
import lombok.Setter;

/**
 * The type Home page.
 */
public final class HomePage implements Page {
    private List<Song> likedSongs;
    private List<Playlist> followedPlaylists;
    @Setter
    private String recommendedSong;
    private final int limit = 5;

    /**
     * Instantiates a new Home page.
     *
     * @param user the user
     */
    public HomePage(final User user) {
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
        if (recommendedSong != null)
            result += "\n\nRecommended song:\n\t%s".formatted(recommendedSong);
        return result;
    }
}
