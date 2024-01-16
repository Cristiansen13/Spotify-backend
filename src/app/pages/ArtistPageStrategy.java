package app.pages;

import app.audio.Collections.Album;
import app.user.Artist;
import app.user.Event;
import app.user.Merchandise;

import java.util.List;
import lombok.Getter;

/**
 * The type Artist page.
 */
public final class ArtistPageStrategy implements PageStrategy {
    private List<Album> albums;
    @Getter
    private List<Merchandise> merch;
    private List<Event> events;
    @Getter
    private String artistName;

    /**
     * Instantiates a new Artist page.
     *
     * @param artist the artist
     */
    public ArtistPageStrategy(final Artist artist) {
        albums = artist.getAlbums();
        merch = artist.getMerch();
        events = artist.getEvents();
        artistName = artist.getUsername();
    }

    @Override
    public String printCurrentPage() {
        return "Albums:\n\t%s\n\nMerch:\n\t%s\n\nEvents:\n\t%s"
                .formatted(albums.stream().map(Album::getName).toList(),
                           merch.stream().map(merchItem -> "%s - %d:\n\t%s"
                                .formatted(merchItem.getName(),
                                           merchItem.getPrice(),
                                           merchItem.getDescription()))
                                .toList(),
                           events.stream().map(event -> "%s - %s:\n\t%s"
                                 .formatted(event.getName(),
                                            event.getDate(),
                                            event.getDescription()))
                                 .toList());
    }
}
