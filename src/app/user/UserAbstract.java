package app.user;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import lombok.Getter;

/**
 * The type User abstract.
 */
public abstract class UserAbstract {
    private String username;
    private int age;
    private String city;
    @Getter
    private ArrayList<UserAbstract> observers = new ArrayList<>();
    @Getter
    private ArrayList<ObjectNode> notifications = new ArrayList<>();
    /**
     * Instantiates a new User abstract.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public UserAbstract(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Gets age.
     *
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets age.
     *
     * @param age the age
     */
    public void setAge(final int age) {
        this.age = age;
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * User type string.
     *
     * @return the string
     */
    public abstract String userType();

    /**
     * Method to subscribe another UserAbstract instance as an observer.
     *
     * @param observer the observer to be added
     */
    public void addObserver(final UserAbstract observer) {
        observers.add(observer);
    }

    /**
     * Method to unsubscribe an observer.
     *
     * @param observer the observer to be removed
     */
    public void removeObserver(final UserAbstract observer) {
        observers.remove(observer);
    }

    /**
     * Method to notify all observers of any state change.
     */
    void notifyObservers(final String type) {
        for (UserAbstract observer : observers) {
            this.update(observer, type);
        }
    }

    /**
     * Abstract method to be implemented by concrete subclasses for handling updates.
     *
     * @param updatedUser the updated user
     */
    protected abstract void update(UserAbstract updatedUser, String type);
}
