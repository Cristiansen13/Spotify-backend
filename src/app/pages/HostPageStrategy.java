package app.pages;

import app.audio.Collections.Podcast;
import app.user.Announcement;
import app.user.Host;

import java.util.List;
import lombok.Getter;

/**
 * The type Host page.
 */
public final class HostPageStrategy implements PageStrategy {
    private List<Podcast> podcasts;
    private List<Announcement> announcements;
    @Getter
    private String hostName;
    /**
     * Instantiates a new Host page.
     *
     * @param host the host
     */
    public HostPageStrategy(final Host host) {
        podcasts = host.getPodcasts();
        announcements = host.getAnnouncements();
        hostName = host.getUsername();
    }

    @Override
    public String printCurrentPage() {
        return "Podcasts:\n\t%s\n\nAnnouncements:\n\t%s"
               .formatted(podcasts.stream().map(podcast -> "%s:\n\t%s\n"
                          .formatted(podcast.getName(),
                                     podcast.getEpisodes().stream().map(episode -> "%s - %s"
                          .formatted(episode.getName(), episode.getDescription())).toList()))
                          .toList(),
                          announcements.stream().map(announcement -> "%s:\n\t%s\n"
                          .formatted(announcement.getName(), announcement.getDescription()))
                          .toList());
    }
}
