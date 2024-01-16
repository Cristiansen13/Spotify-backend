# Project Title

For this project, I have implemented an application similar in functionality to Spotify, simulating various user actions. Now, with added features related to monetization and user engagement details on our platform. Additionally, the project involves the ability to create playlists based on user preferences or preferences of fans of a specific artist. Users will also receive notifications from the platform about new additions.

## Table of Contents

1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Design Patterns](#design-patterns)

## Introduction

GlobalWaves isn't just about music; it's a comprehensive ecosystem where users can engage with their favorite artists, discover new content, and contribute to the success of musicians. From personalized statistics that reflect individual listening habits to a monetization system that ensures artists are fairly compensated for their work, GlobalWaves aims to redefine the music streaming experience.

## Project Structure

### Documentation for Main class
The main class is the entry point of the application. It is responsible for creating the objects of the other classes and calling their methods. It is also responsible for the user interface.
### Documentation for Command interface
The Command interface represents a generic command in the application.
All commands that can be executed within the app should implement this interface.
Each implementing class should provide its specific behavior through the execute method.
### Documentation for Page interface
The PageStrategy interface represents a strategy for printing the current page.
Classes implementing this interface provide specific implementations for printing information
based on the current page, following the Strategy design pattern.
### Documentation for Player class
The Player class represents a multimedia player in the application,
offering functionalities for playing audio content such as songs, playlists, albums, and podcasts.
It incorporates various supporting classes to handle different aspects of audio playback,
including sources, statistics, and bookmarks.

- `PlayerSource`: Manages the source of audio content, including handling playlists, albums, and podcasts.
It facilitates setting the next or previous audio file, shuffling, and generating shuffle orders.

- `ListenRecord`: Records the user's listening history, including information about listened artists, albums, genres, songs, and episodes.
It also maintains a list of premium listened songs for premium users.

- `PodcastBookmark`: Represents a bookmark for a specific podcast episode within the {@code Player}.
Used for tracking the user's progress in a podcast and resuming playback from the last bookmarked position.

- `PlayerStats`: Provides statistics about the current playback status, including the remaining time,
repeat mode, shuffle status, and whether the player is paused.
### Documentation for Searchbar class
The SearchBar class provides functionality for searching and filtering library entries and content creators.
It uses various filter criteria to narrow down search results.
The class includes methods for searching songs, playlists, podcasts, and albums, as well as content creators such as artists and hosts.
It also supports selecting and clearing selections from the search results.
- `Filters`: A data class that holds filter criteria for search operations.
- `FilterUtils`: A utility class with static methods for filtering library entries based on different criteria.
### Documentation for UserAbstract class
The `UserAbstract` class serves as an abstract base class for various user-related entities within the application.
* It encapsulates common attributes and behaviors shared among different user types, allowing for a flexible and
* extensible user hierarchy.
### Documentation for Admin class
The Admin class serves as a central hub for managing users, artists, hosts, audio files,
and various audio collections within the application. It follows the Singleton design pattern
to ensure that there is a single instance of the class throughout the application.
This class facilitates the creation, modification, and deletion of users, artists, hosts, songs,
podcasts, albums, events, merchandise, and announcements. Additionally, it handles user navigation
between different pages in the application and maintains timestamps to simulate the passage of time
for user activities.

The class interacts with the following important entities and classes:
- User, Artist, Host: Represents different types of users in the application.
- Song, Podcast, Episode, Album: Represents audio files and collections.
- Player: Manages audio playback for users.
- Playlist: Represents a collection of songs created by a user.
- Event: Represents events created by artists.
- Merchandise: Represents merchandise created by artists.
- Announcement: Represents announcements created by hosts.
- PageStrategy: Provides strategies for printing information about the current page.

The class utilizes several helper methods for setting up users, songs, podcasts, and handling user actions
such as adding/deleting users, albums, podcasts, events, merchandise, and announcements. It also manages
user navigation between different pages and simulates time passage for user activities.

This class follows the Singleton pattern to ensure a single point of access and control over the application's
core functionalities. Its static methods allow for the retrieval of the instance and resetting the instance,
ensuring that only one instance of the class exists at any given time.

## Design Patterns

1. **Singleton Pattern:**
  - The Singleton design pattern is employed in the Admin class to ensure there is only one instance, offering centralized control and global access to manage various aspects of the application. This approach prevents unintended instantiation, simplifies resource management, and ensures thread safety in a multithreaded environment. By maintaining a single point of control for user, artist, and host management, the Singleton pattern enhances code organization and efficiency.

2. **Strategy Pattern:**
  - The PageStrategy interface embodies the Strategy design pattern to efficiently handle diverse page display requirements in the application. This design choice is crucial due to varying page logic and the need for adaptable, maintainable code. By employing the Strategy pattern, the interface promotes code reusability, flexibility in accommodating new page types, and a clear separation of concerns, resulting in an organized and scalable solution. The design facilitates independent testing of each page strategy and ensures easy extensibility for future enhancements without impacting existing code.

3. **Observer Pattern:**
  - The UserAbstract abstract class employs the Observer design pattern to facilitate real-time notifications in the application. When an artist posts something, the artist, acting as the subject, triggers a state change. Subscribed normal users, functioning as observers, receive notifications through the update method, ensuring they are promptly informed of the artist's activity. This design pattern enhances communication between users, enabling efficient dissemination of updates without directly coupling the artist and normal user classes. It promotes a scalable and maintainable solution for notifying users about relevant events, contributing to a responsive and dynamic user interaction system.

4. **Command Pattern:**
  - The Command design pattern, implemented through the Command interface, enhances code organization and promotes separation of concerns. By encapsulating requests as objects, it allows clients to remain unaware of command execution details, fostering maintainability and readability. The pattern supports extensibility, enabling the easy addition of new commands without modifying existing code. Additionally, it facilitates features like command queues or histories, providing a modular approach for implementing advanced functionalities. In essence, the Command pattern is beneficial for scenarios requiring decoupling of request senders and processors, offering flexibility and maintainability.