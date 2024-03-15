import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password; // New field for password

    private List<Location> savedLocations;
    private List<Location> favoriteLocations;
    private String userId;
    private NotificationSettings notificationSettings;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.userId = ""; // Provide a default userId
        this.savedLocations = new ArrayList<>();
        this.favoriteLocations = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    // Getter method for password
    public String getPassword() {
        return password;
    }



    public List<Location> getSavedLocations() {
        return savedLocations;
    }

    public void addSavedLocation(Location location) {
        savedLocations.add(location);
    }

    public void removeSavedLocation(Location location) {
        savedLocations.remove(location);
    }

    public List<Location> getFavoriteLocations() {
        return favoriteLocations;
    }

    public void addFavoriteLocation(Location location) {
        favoriteLocations.add(location);
    }

    public void removeFavoriteLocation(Location location) {
        favoriteLocations.remove(location);
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
