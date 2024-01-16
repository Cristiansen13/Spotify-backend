package app.user;

import app.Admin;
import app.audio.Collections.Podcast;
import app.pages.HostPageStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

/**
 * The type Host.
 */
public final class Host extends ContentCreator {
    private ArrayList<Podcast> podcasts;
    private ArrayList<Announcement> announcements;
    @Getter
    private ArrayList<String> listenedEpisodes = new ArrayList<>();
    /**
     * Instantiates a new Host.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public Host(final String username, final int age, final String city) {
        super(username, age, city);
        podcasts = new ArrayList<>();
        announcements = new ArrayList<>();

        super.setPage(new HostPageStrategy(this));
    }

    /**
     * Gets podcasts.
     *
     * @return the podcasts
     */
    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    /**
     * Add podcast.
     *
     * @param podcast the podcast
     */
    public void addPodcast(final Podcast podcast) {
        podcasts.add(podcast);
        notifyObservers("podcast");
    }

    /**
     * Sets podcasts.
     *
     * @param podcasts the podcasts
     */
    public void setPodcasts(final ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    /**
     * Gets announcements.
     *
     * @return the announcements
     */
    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    /**
     * Add announcement.
     *
     * @param announcement the announcement
     */
    public void addAnnouncement(final Announcement announcement) {
        announcements.add(announcement);
        notifyObservers("announcement");
    }
    /**
     * Sets announcements.
     *
     * @param announcements the announcements
     */
    public void setAnnouncements(final ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }

    /**
     * Gets podcast.
     *
     * @param podcastName the podcast name
     * @return the podcast
     */
    public Podcast getPodcast(final String podcastName) {
        for (Podcast podcast: podcasts) {
            if (podcast.getName().equals(podcastName)) {
                return podcast;
            }
        }

        return null;
    }

    /**
     * Gets announcement.
     *
     * @param announcementName the announcement name
     * @return the announcement
     */
    public Announcement getAnnouncement(final String announcementName) {
        for (Announcement announcement: announcements) {
            if (announcement.getName().equals(announcementName)) {
                return announcement;
            }
        }

        return null;
    }

    /**
     * Gets user type.
     *
     * @return the user type
     */
    @Override
    public String userType() {
        return "host";
    }

    /**
     * Gets numberof listeners.
     *
     * @return the numberof listeners
     */
    public Integer getNumberofListeners() {
        Set<String> uniqueFans = new HashSet<>();
        Admin admin = Admin.getInstance();
        for (User user : admin.getUsers()) {
            for (String listenedHost : user.getPlayer().getListenRecord().getListenedHosts()) {
                if (this.getUsername().equals(listenedHost)) {
                    uniqueFans.add(user.getUsername());
                }
            }
        }
        return uniqueFans.size();
    }
    /**
     * Handle the update when something new is added.
     *
     * @param updatedUser the updated user
     */
    @Override
    protected void update(final UserAbstract updatedUser, final String type) {
        if (type.equals("podcast")) {
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.put("name", "New Podcast");
            objectNode.put("description", "New Podcast from " + this.getUsername());
            updatedUser.getNotifications().add(objectNode);
        } else if (type.equals("announcement")) {
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.put("name", "New Announcement");
            objectNode.put("description", "New Announcement from " + this.getUsername());
            updatedUser.getNotifications().add(objectNode);
        }
    }
}
