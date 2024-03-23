import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*ObjectMapper will only work in Intelije after you install 4
dependencies in your PC and included in your project structure 
the dependencies are mentioned in Readme file.*/
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.io.IOException;

//for databsase
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//for graphics (GUI)
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

interface UserInterface {
    void displayWeatherData(WeatherData data);

    void displayLocationOptions(List<Location> locations);

    Location getLocationInput();
}

interface Storage {
    void saveLocation(Location location);

    List<Location> getLocations();

    void saveWeatherData(Location location, WeatherData data);

    WeatherData getWeatherData(Location location);
}

// * API Logic starts here
// https://api.openweathermap.org/data/2.5/weather?lat=33.44&lon=94.04&appid={yourownapikey}
interface WeatherService {
    String getWeatherData(Coord location);
}

class WeatherServiceImpl implements WeatherService {

    private static String api;
    WeatherData myweatherData;

    public WeatherServiceImpl(String api) {
        this.api = api;
        myweatherData = new WeatherData();
    }

    @Override
    public WeatherData getWeatherData(Coord location) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api))
                .header("X", "api.openweathermap.org")
                .header("X-RapidAPI-Key", "yourApikey")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;

        try {

            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

        String res = response.body();
        ObjectMapper mapper = new ObjectMapper();

        try {
            myweatherData = mapper.readValue(res, WeatherData.class);
        }

        catch (IOException e) {
            System.out.println("fault is there");
        }

        return myweatherData;
    }

    // Sample main to test out the APi
    public static void main(String[] args) {
        WeatherServiceImpl myobj = new WeatherServiceImpl(
                "https://api.openweathermap.org/data/2.5/weather?lat=33.44&lon=94.04&date=2020-03-04&appid=2a8a0b0b17f5b1d25f15762b2fa658f4");

        // Print statement to test
        System.out.println("Working fine ");

        Coord myloc = new Coord(0, 0);

        WeatherData MyApiData = myobj.getWeatherData(myloc);

        // ! If this gives the same output according to which the API was called your
        // data is successfully stored
        System.out.println(MyApiData.getCoord().getLatitude());

    }
}

// * My API main class
class WeatherData {

    public Coord coord;
    public List<Weather> weather;
    public String base;
    public Main main;
    public int visibility;
    public Wind wind;
    public Clouds clouds;
    public long dt;
    public Sys sys;
    public int timezone;
    public int id;
    public String name;
    public int cod;

    public WeatherData(Coord coord, List<Weather> weather, String base, Main main, int visibility, Wind wind,
            Clouds clouds, long dt, Sys sys, int timezone, int id, String name, int cod) {
        this.coord = coord;
        this.weather = weather;
        this.base = base;
        this.main = main;
        this.visibility = visibility;
        this.wind = wind;
        this.clouds = clouds;
        this.dt = dt;
        this.sys = sys;
        this.timezone = timezone;
        this.id = id;
        this.name = name;
        this.cod = cod;
    }

    public WeatherData() {
        this.coord = new Coord();
        this.visibility = 0;
        this.dt = 0;
        this.timezone = 0;
        this.id = 0;
        this.name = "";
        this.cod = 0;
    }

    public Coord getCoord() {
        return coord;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public String getBase() {
        return base;
    }

    public Main getMain() {
        return main;
    }

    public int getVisibility() {
        return visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public long getDt() {
        return dt;
    }

    public Sys getSys() {
        return sys;
    }

    public int getTimezone() {
        return timezone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCod() {
        return cod;
    }

}

// * API logic ends here

class TerminalUI implements UserInterface {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void displayWeatherData(WeatherData data) {
        System.out.println("Current Weather for " + data.getCurrentWeather().getTimestamp() + ":");
        System.out.println("  Temperature: " + data.getCurrentWeather().getTemperature() + "°C");
        System.out.println("  Feels Like: " + data.getCurrentWeather().getFeelsLike() + "°C");
        System.out.println("  Min Temp: " + data.getBasicInfo().getMinTemp() + "°C");
        System.out.println("  Max Temp: " + data.getBasicInfo().getMaxTemp() + "°C");
        System.out.println("  Sunrise: " + data.getCurrentWeather().getSunrise());
        System.out.println("  Sunset: " + data.getCurrentWeather().getSunset());
        System.out.println("  Forecast:");
        for (Forecast forecast : data.getForecast()) {
            System.out.println("    - " + forecast.getDate() + ": " + forecast.getTemperature() + "°C");
        }
        System.out.println("  Air Quality Index (AQI): " + data.getAirPollution().getAqi());
    }

    @Override
    public void displayLocationOptions(List<Location> locations) {
        if (locations.isEmpty()) {
            System.out.println("No saved locations found.");
            return;
        }
        System.out.println("Saved Locations:");
        int index = 1;
        for (Location location : locations) {
            System.out.println("  " + index + ". " + location.getName());
            index++;
        }
    }

    @Override
    public Location getLocationInput() {
        System.out.println("Enter location details:");
        System.out.print("  Name: ");
        String name = scanner.nextLine();
        System.out.print("  Latitude: ");
        double latitude = scanner.nextDouble();
        scanner.nextLine(); // Consume newline character
        System.out.print("  Longitude: ");
        double longitude = scanner.nextDouble();
        scanner.nextLine(); // Consume newline character
        return new Location(name, latitude, longitude);
    }

    public int getMenuChoice() {
        System.out.println("\nWeather App Menu:");
        System.out.println("  1. View Weather by Location");
        System.out.println("  2. Manage Saved Locations");
        System.out.println("  3. Exit");
        System.out.print("Enter your choice: ");
        return scanner.nextInt();
    }
}

class FileStorage implements Storage {

    private final String storageFile;
    private final ObjectMapper objectMapper;

    public FileStorage(String storageFile) {
        this.storageFile = storageFile;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void saveLocation(Location location) {
        List<Location> locations = getLocations();
        locations.add(location);
        writeLocationsToFile(locations);
    }

    @Override
    public List<Location> getLocations() {
        File file = new File(storageFile);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, List.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveWeatherData(Location location, WeatherData data) {
        // Not implemented in this example, could store weather data per location
    }

    @Override
    public WeatherData getWeatherData(Location location) {
        // Not implemented in this example, could retrieve cached weather data
        return null;
    }

    private void writeLocationsToFile(List<Location> locations) {
        try {
            objectMapper.writeValue(new File(storageFile), locations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class DatabaseStorage implements Storage {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public DatabaseStorage(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    @Override
    public void saveLocation(Location location) {
        String sql = "INSERT INTO locations (name, latitude, longitude) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, location.getName());
            statement.setDouble(2, location.getLatitude());
            statement.setDouble(3, location.getLongitude());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT name, latitude, longitude FROM locations";
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                double latitude = resultSet.getDouble(2);
                double longitude = resultSet.getDouble(3);
                locations.add(new Location(name, latitude, longitude));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    }

    // Not implemented in this example, could store weather data per location
    @Override
    public void saveWeatherData(Location location, WeatherData data) {
        // ... (implement saving weather data to the database)
    }

    // Not implemented in this example, could retrieve cached weather data
    @Override
    public WeatherData getWeatherData(Location location) {
        // ... (implement retrieving weather data from the database)
        return null;
    }
}

class Notification {
    private final String message;
    private final String timestamp;
    private final Severity severity; // Optional

    public Notification(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        this.severity = Severity.INFO; // Default severity
    }

    public Notification(String message, String timestamp, Severity severity) {
        this.message = message;
        this.timestamp = timestamp;
        this.severity = severity;
    }

    // Getter methods for message, timestamp, and severity (if applicable)

    public enum Severity {
        INFO,
        WARNING,
        ERROR
    }
}

// Changing the Name of the start point
// ! Also changed the name of the file so that it runs
public class MainApp {

    public static void main(String[] args) {
        UserInterface ui = new TerminalUI();
        Storage storage = new FileStorage("locations.json");
        WeatherService weatherService = new WeatherServiceImpl(new OpenWeatherMapAPI()); // Replace with actual API
                                                                                         // implementation

        while (true) {
            int choice = ui.getMenuChoice();
            switch (choice) {
                case 1:
                    Location location = ui.getLocationInput() /* or display saved locations and get user choice */;
                    try {
                        WeatherData weatherData = weatherService.getWeatherData(location);
                        ui.displayWeatherData(weatherData);
                    } catch (Exception e) {
                        System.err.println("Error fetching weather data: " + e.getMessage());
                    }
                    break;
                case 2:
                    List<Location> locations = storage.getLocations();
                    ui.displayLocationOptions(locations);
                    // Add options to manage saved locations (add, remove, edit)
                    break;
                case 3:
                    System.out.println("Exiting weather app.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}

class JavaFXUI extends Application implements UserInterface {

    private WeatherUIController controller; // Reference to the FXML controller

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("weather_ui.fxml")); // Replace with your FXML file
                                                                                       // path
        Parent root = loader.load();
        controller = loader.getController(); // Assuming your FXML controller extends UserInterface
        controller.setUserInterface(this); // Pass reference to this object

        primaryStage.setTitle("Weather App");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void displayWeatherData(WeatherData data) {
        // Access FXML elements from the controller
        controller.setCurrentTemperatureLabel(String.format("%.2f°C", data.getCurrentWeather().getTemperature()));
        controller.setFeelsLikeLabel(String.format("%.2f°C", data.getCurrentWeather().getFeelsLike()));
        controller.setMinTempLabel(String.format("%.2f°C", data.getBasicInfo().getMinTemp()));
        controller.setMaxTempLabel(String.format("%.2f°C", data.getBasicInfo().getMaxTemp()));
        controller.setSunriseLabel(data.getCurrentWeather().getSunrise());
        controller.setSunsetLabel(data.getCurrentWeather().getSunset());

        // Example for displaying a limited number of forecasts (modify as needed)
        List<String> forecastList = new ArrayList<>();
        for (int i = 0; i < 3 && i < data.getForecast().size(); i++) {
            Forecast forecast = data.getForecast().get(i);
            forecastList.add(String.format("%s: %.2f°C", forecast.getDate(), forecast.getTemperature()));
        }
        controller.getForecastListView().setItems(forecastList);

        controller.setAirPollutionLabel(String.valueOf(data.getAirPollution().getAqi()));
    }

    @Override
    public void displayLocationOptions(List<Location> locations) {
        controller.getLocationListView().getItems().clear(); // Clear existing entries
        for (Location location : locations) {
            controller.getLocationListView().getItems().add(location.getName());
        }
    }

    @Override
    public Location getLocationInput() {
        String name = controller.getLocationNameField().getText().trim();
        if (name.isEmpty()) {
            return null; // Handle empty input
        }
        // Assuming latitude and longitude are not user-editable in this example
        // You might need to add additional input fields or handle them differently
        double latitude = 0.0; // Replace with default or actual latitude value
        double longitude = 0.0; // Replace with default or actual longitude value
        return new Location(name, latitude, longitude);
    }
}
