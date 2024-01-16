package app.user;

import app.pages.PageStrategy;
import lombok.Getter;

/**
 * The type Content creator.
 */
public abstract class ContentCreator extends UserAbstract {
    private String description;
    @Getter
    private PageStrategy pageStrategy;

    /**
     * Instantiates a new Content creator.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public ContentCreator(final String username, final int age, final String city) {
        super(username, age, city);
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets page.
     *
     * @return the page
     */
    public PageStrategy getPage() {
        return pageStrategy;
    }

    /**
     * Sets page.
     *
     * @param pageStrategy the page
     */
    public void setPage(final PageStrategy pageStrategy) {
        this.pageStrategy = pageStrategy;
    }
}
