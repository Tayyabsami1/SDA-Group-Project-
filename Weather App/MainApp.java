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


//for File
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.io.LineNumberReader;
import java.io.FileNotFoundException;

interface UserInterface {
    void displayWeatherData(WeatherData data);

    void displayLocationOptions(List<Location> locations);

    Location getLocationInput();

    int getMenuChoice();


    // List<Location> getLocationsByLatLngInput();

    // List<Location> getLocationsByCityInput();

    void showCurrentWeather(WeatherService weatherService, UserInterface ui);

    void showBasicInfo(WeatherService weatherService, Location location,Storage storage);

    void showSunriseSunset(WeatherService weatherService,Location location,Storage storage);

    void showWeatherForecast(WeatherService weatherService, UserInterface ui);

    void showAirPollution(WeatherService weatherService, UserInterface ui);

    void showPollutingGases(WeatherService weatherService, UserInterface ui);

}

interface Storage {
    void saveLocation(Location location);

    List<Location> getLocations();

    void saveWeatherData(Location location, WeatherData data);

    WeatherData getWeatherData(Location location);
    void saveBasicInfo(Location location, double fl,double Tmin, double Tmax);
    boolean checkBasicInfo(Location location);
}

// * API Logic starts here
// https://api.openweathermap.org/data/2.5/weather?lat=33.44&lon=94.04&appid={yourownapikey}
// http://api.openweathermap.org/data/2.5/air_pollution?lat={lat}&lon={lon}&appid={APIkey}
// https://api.openweathermap.org/data/2.5/forecast?lat=33.44&lon=94.04&appid={APIKEY}
//https://api.openweathermap.org/data/2.5/weather?q=Pakistan&APPID={APIKEY}

interface WeatherService {
    // Get Normal Weather Data
    WeatherData getWeatherData(Coord location);

    // Get Forecast for 5 next days
    Forecast getForecastData(Coord location);

    // Air polution for certain longitude and latitide
    AirPollution getPollutionData(Coord locattion);
}

class WeatherServiceImpl implements WeatherService {

    private static String api;
    WeatherData myweatherData;

    public WeatherServiceImpl(String api) {
        this.api = api;
        myweatherData = new WeatherData();
    }

    // utility function to call Api
    public String responseReturner(String api) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api))
                .header("X", "api.openweathermap.org")
                .header("X-RapidAPI-Key", "yourapikey")
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

        System.out.println(response.body());

        return response.body();
    }

    @Override
    public WeatherData getWeatherData(Coord location) {
        WeatherData myweatherData = new WeatherData();
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());

        api = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon
                + "&date=2020-03-04&appid=yourapikey";

        String res = responseReturner(api);

        ObjectMapper mapper = new ObjectMapper();

        try {
            myweatherData = mapper.readValue(res, WeatherData.class);
        } catch (IOException e) {
            System.out.println(e);
        }

        return myweatherData;
    }

    @Override
    public Forecast getForecastData(Coord location) {

        Forecast myforecastData = new Forecast();

        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());

        api = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon
                + "&date=2020-03-04&appid=yourapikey";

        String res = responseReturner(api);

        ObjectMapper mapper = new ObjectMapper();

        try {
            myforecastData = mapper.readValue(res, Forecast.class);
        } catch (IOException e) {
            System.out.println(e);
        }

        return myforecastData;

    }

    @Override
    public AirPollution getPollutionData(Coord location) {

        AirPollution myPollutionData = new AirPollution();
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());

        api = "https://api.openweathermap.org/data/2.5/air_pollution?lat=" + lat + "&lon=" + lon
                + "&date=2020-03-04&appid=yourapikey";

        String res = responseReturner(api);

        ObjectMapper mapper = new ObjectMapper();

        try {
            myPollutionData = mapper.readValue(res, AirPollution.class);
        }
         catch (IOException e) {
            System.out.println(e);
        }

        return myPollutionData;
    }

    // Sample main to test out the APi
    public static void main(String[] args) {
        WeatherServiceImpl myobj = new WeatherServiceImpl(
                "https://api.openweathermap.org/data/2.5/weather?lat=33.44&lon=94.04&date=2020-03-04&appid=yourapikey");

        System.out.println("Working fine ");

        Coord myloc = new Coord(25.5, 20.5);

        WeatherData MyApiData = myobj.getWeatherData(myloc);
        Forecast ForecastData = myobj.getForecastData(myloc);
        AirPollution MyPollutionData=myobj.getPollutionData(myloc);
        // ! This returns a list of 40 weather forecase for the next 5 days each list
        // contains forecast of 3hrs
        System.out.println(MyApiData.getVisibility());
        System.out.println(ForecastData.getList().size());
        System.out.println("No2 in Air is "+MyPollutionData.getList().get(0).getComponents().no2);
    }
}

// TODO : Update class diagram accordingly
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
    public int pop;
    public String dt_txt;

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

// *Modified Forecast class to use it as my Api data store to store forecast data 

class Forecast {
    public int cod;
    public int message;
    public int cnt;
    public List<WeatherData> list;
    public City city;

    public int getCod() {
        return cod;
    }

    public int getMessage() {
        return message;
    }

    public int getCnt() {
        return cnt;
    }

    public List<WeatherData> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }

}

// *My Api class New for Forecasting
class City {

    public int id;
    public String name;
    public Coord coord;
    public String country;
    public int population;
    public int timezone;
    public long sunrise;
    public long sunset;

    // Getters and setters (optional)

    public City(int id, String name, Coord coord, String country, int population, int timezone, long sunrise,
            long sunset) {
        this.id = id;
        this.name = name;
        this.coord = coord;
        this.country = country;
        this.population = population;
        this.timezone = timezone;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public City() {
        this.id = 0;
        this.name = "";
        this.country = "";
        this.population = 0;
        this.timezone = 0;
        this.sunrise = 0;
        this.sunset = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coord getCoord() {
        return coord;
    }

    public String getCountry() {
        return country;
    }

    public int getPopulation() {
        return population;
    }

    public int getTimezone() {
        return timezone;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }
}

// * Modified class to store Air pollution data
class AirPollution {
    public Coord coord;
    public List<AirQuality> list;

    public Coord getCoord() {
        return coord;
    }

    public List<AirQuality> getList() {
        return list;
    }

}

class AirQuality {

    public AirIndex main;
    public Components components;
    public long dt;

    public AirQuality(AirIndex main, Components components, long dt) {
        this.main = main;
        this.components = components;
        this.dt = dt;
    }

    public AirQuality() {

    }

    public AirIndex getMain() {
        return main;
    }

    public Components getComponents() {
        return components;
    }

    public long getDt() {
        return dt;
    }
}

class AirIndex {

    public int aqi;

    public AirIndex()
    {

    }

    public AirIndex(int aqi) {
        this.aqi = aqi;
    }

    int getAqi() {
        return this.aqi;
    }
}

// TODO : Update class diagram and add these 7 classes

// * My Api related classes

// Air polution data store 
class Components {

    public double co;
    public double no;
    public double no2;
    public double o3;
    public double so2;
    public double pm2_5;
    public double pm10;
    public double nh3;

    public Components(double co, double no, double no2, double o3, double so2, double pm2_5, double pm10, double nh3) {
        this.co = co;
        this.no = no;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
        this.pm2_5 = pm2_5;
        this.pm10 = pm10;
        this.nh3 = nh3;
    }

    public Components() {
        // Default constructor
    }

    public double getCo() {
        return co;
    }

    public double getNo() {
        return no;
    }

    public double getNo2() {
        return no2;
    }

    public double getO3() {
        return o3;
    }

    public double getSo2() {
        return so2;
    }

    public double getPm2_5() {
        return pm2_5;
    }

    public double getPm10() {
        return pm10;
    }

    public double getNh3() {
        return nh3;
    }
}

class Coord {
    public double lon;
    public double lat;

    public Coord() {
        this.lon = 0.0; // Default longitude
        this.lat = 0.0; // Default latitude
    }

    public Coord(double latitude, double longitude) {
        this.lat = latitude;
        this.lon = longitude;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lon;
    }

    @Override
    public String toString() {
        return "Location{" +
                ", latitude=" + lat +
                ", longitude=" + lon +
                "}";
    }
}

class Weather {
    public int id;
    public String main;
    public String description;
    public String icon;

    public Weather() {
        this.id = 0; // Default weather ID
        this.main = ""; // Default main weather description
        this.description = ""; // Default weather description
        this.icon = ""; // Default weather icon code
    }

    public Weather(int id, String main, String description, String icon) {
        this.id = id;
        this.main = main;
        this.description = description;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

}

class Main {
    public double temp;
    public double feels_like;
    public double temp_min;
    public double temp_max;
    public int pressure;
    public int humidity;
    public int sea_level;
    public int grnd_level;
    public int temp_kf;
    public String location;
    public String date;

    public Main() {
        this.temp = 0.0; // Default temperature
        this.feels_like = 0.0; // Default feels_like temperature
        this.temp_min = 0.0; // Default minimum temperature
        this.temp_max = 0.0; // Default maximum temperature
        this.pressure = 0; // Default pressure
        this.humidity = 0; // Default humidity
        this.sea_level = 0;
        this.grnd_level = 0;
        this.temp_kf = 0;
    }

    public Main(double temp, double feels_like, double temp_min, double temp_max, int pressure, int humidity,
            int sea_level, int grnd_level, int temp_kf) {
        this.temp = temp;
        this.feels_like = feels_like;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.pressure = pressure;
        this.humidity = humidity;
        this.sea_level = sea_level;
        this.grnd_level = grnd_level;
        this.temp_kf = temp_kf;
    }

    public double getTemp() {
        return temp;
    }
    public  void setFeelsike(double fl)
    {
        feels_like=fl;
    }
    public  void setTemp_Min(double fl)
    {
        temp_min=fl;
    }
    public  void setTemp_Max(double fl)
    {
        temp_max=fl;
    }
    public  void setLocation(String l)
    {
        location=l;
    }
    public  void setDate(String d)
    {
        date=d;
    }

    public String getLocation()
    {
        return location;
    }
    public String getDate()
    {
        return date;
    }
    public double getFeelsLike() {
        return feels_like;
    }

    public double getTempMin() {
        return temp_min;
    }

    public double getTempMax() {
        return temp_max;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }
}

class Wind {
    public double speed;
    public int deg;
    public double gust;

    public Wind() {
        this.speed = 0.0; // Default wind speed
        this.deg = 0; // Default wind direction (degrees)
        this.gust = 0.0; // Default wind gust speed
    }

    public Wind(double speed, int deg, double gust) {
        this.speed = speed;
        this.deg = deg;
        this.gust = gust;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDeg() {
        return deg;
    }

    public double getGust() {
        return gust;
    }

}

class Clouds {
    public int all;

    public Clouds() {
        this.all = 0; // Default cloud cover percentage
    }

    public Clouds(int all) {
        this.all = all;
    }

    public int getAll() {
        return all;
    }
}

class Sys {
    public int sunrise;
    public int sunset;
    public String pod;

    public Sys() {
        this.sunrise = 0; // Default sunrise time (seconds since epoch)
        this.sunset = 0; // Default sunset time (seconds since epoch)
    }

    public Sys(int sunrise, int sunset) {
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public int getSunrise() {
        return sunrise;
    }

    public int getSunset() {
        return sunset;
    }

}

// * API logic ends here

class TerminalUI implements UserInterface {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void displayWeatherData(WeatherData data) {
        // System.out.println("Current Weather for " + data.getCurrentWeather().getTimestamp() + ":");
        // System.out.println("  Temperature: " + data.getCurrentWeather().getTemperature() + "°C");
        // System.out.println("  Feels Like: " + data.getCurrentWeather().getFeelsLike() + "°C");
        // System.out.println("  Min Temp: " + data.getBasicInfo().getMinTemp() + "°C");
        // System.out.println("  Max Temp: " + data.getBasicInfo().getMaxTemp() + "°C");
        // System.out.println("  Sunrise: " + data.getCurrentWeather().getSunrise());
        // System.out.println("  Sunset: " + data.getCurrentWeather().getSunset());
        // System.out.println("  Forecast:");
        // for (Forecast forecast : data.getForecast()) {
        //     System.out.println("    - " + forecast.getDate() + ": " + forecast.getTemperature() + "°C");
        // }
        // System.out.println("  Air Quality Index (AQI): " + data.getAirPollution().getAqi());
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

    @Override
    public int getMenuChoice() {
        System.out.println("\nWeather App Menu:");
        System.out.println("  1. Show current weather conditions");
        System.out.println("  2. Show basic information like 'Feels like, minimum and maximum temperature'");
        System.out.println("  3. Show sunrise and sunset time");
        System.out.println("  4. Show weather forecast for 5 days");
        System.out.println("  5. Show Air Pollution data");
        System.out.println("  6. Show data about polluting gases");
        System.out.println("  7. Exit");
        System.out.print("Enter your choice: ");
        return scanner.nextInt();
    }
    // @Override
    // public List<Location> getLocationsByLatLngInput() {
    //     // Implement as required
    //     return null;
    // }

    // @Override
    // public List<Location> getLocationsByCityInput() {
    //     // Implement as required
    //     return null;
    // }


    @Override
    public void showCurrentWeather(WeatherService weatherService, UserInterface ui) {
        // Implement as required
    }

    @Override
    public void showBasicInfo(WeatherService weatherService, Location location,Storage storage) {
        // Implement as required
        double feels_like;
        double temp_min;
        double temp_max;
        Coord myloc = new Coord(location.getLatitude(), location.getLongitude());
        WeatherData Data = weatherService.getWeatherData(myloc);
        feels_like=Data.getMain().getFeelsLike();
        temp_min=Data.getMain().getTempMin();
        temp_max=Data.getMain().getTempMax();
        System.out.println("Feels Like: "+feels_like+"\n Minimum Temperature: "+temp_min+"Maximum Temperature: "+temp_max);
        storage.saveBasicInfo(location, feels_like, temp_min, temp_max);
    }

    @Override
    public void showSunriseSunset(WeatherService weatherService,Location location,Storage storage) {
        // Implement as required
    }

    @Override
    public void showWeatherForecast(WeatherService weatherService, UserInterface ui) {
        // Implement as required
    }

    @Override
    public void showAirPollution(WeatherService weatherService, UserInterface ui) {
        // Implement as required
    }

    @Override
    public void showPollutingGases(WeatherService weatherService, UserInterface ui) {
        // Implement as required
    }
}

class Location {
    private final String name;
    private final double latitude;
    private final double longitude;

    public Location(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

class FileStorage implements Storage {

    
    @Override
    public void saveBasicInfo(Location location, double fl,double Tmin, double Tmax)
    {
        try {

            File myFile = new File("BasicInfo.txt");
            FileWriter writer = new FileWriter(myFile,true);
            LocalDate today = LocalDate.now();
            writer.write(location.getName());
            writer.write(",");
            writer.write(String.valueOf(fl));
            writer.write(",");
            writer.write(String.valueOf(Tmin));
            writer.write(",");
            writer.write(String.valueOf(Tmax));
            writer.write(",");
            writer.write(String.valueOf(today));
            writer.write("\n");
            writer.close();
        } 
          catch (IOException e) 
          {
            System.err.println("Error writing to file: " + e.getMessage());
          }
    }

    @Override
    public boolean checkBasicInfo(Location location)
    {
        String delimiter = ",";  // Word to stop reading
        int numberOfLines = 0;
        LocalDate today = LocalDate.now();
        boolean check=false;
       try{
            LineNumberReader reader = new LineNumberReader(new FileReader("BasicInfo.txt"));
            while (reader.readLine() != null) {
                numberOfLines++;
            }
            reader.close();
            
        }
            catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
            Main[] arr= new Main[numberOfLines];
       try{
          Scanner scanner = new Scanner(new File("BasicInfo.txt"));
          scanner.useDelimiter(delimiter);  // Set delimiter for splitting
    
          for( int i=0;i<arr.length;i++)
          {
            arr[i] = new Main();
              arr[i].setLocation(scanner.next());
              arr[i].setFeelsike(Double.parseDouble(scanner.next()));
              arr[i].setTemp_Min(Double.parseDouble(scanner.next()));
              arr[i].setTemp_Max(Double.parseDouble(scanner.next()));
              String firstLine = scanner.nextLine();
              arr[i].setDate(firstLine.substring(1));

          }
          scanner.close();
        }
          catch (FileNotFoundException e) {
            System.err.println("Error opening file:  " + e.getMessage());
        }
        int index=0;
        for(int i=0;i<arr.length;i++)
        {
            
            if(arr[i].getLocation().equalsIgnoreCase(location.getName()))
            {
                if(arr[i].getDate().equals(String.valueOf(today)))
                {
                    check=true;
                    index=i;

                }
            }
        }
        if(check==false)
        {
            return false;
        }
        System.out.println("Feels Like: "+arr[index].getFeelsLike()+"\n Minimum Temperature: "+arr[index].getTempMin()+"Maximum Temperature: "+arr[index].getTempMax());
        return true;
          
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

/ Changing the Name of the start point
// ! Also changed the name of the file so that it runs
class MainApp {

    public static void main(String[] args) {
        UserInterface ui = new TerminalUI();
        Storage storage = new FileStorage();
        WeatherService weatherService = new WeatherServiceImpl(
        "https://api.openweathermap.org/data/2.5/weather?lat=33.44&lon=94.04&date=2020-03-04&appid=yourapikey"); // Replace with actual API
                                                                                         // implementation

        // List<Location> locationsByLatLng = ui.getLocationsByLatLngInput();

        // // Get multiple locations by city/country name
        // List<Location> locationsByCity = ui.getLocationsByCityInput();

        Location location=ui.getLocationInput();
        while (true) {
            int choice = ui.getMenuChoice();
            switch (choice) {
                case 1:
                    ui.showCurrentWeather(weatherService, ui);
                    break;
                case 2:
                {
                    boolean check=storage.checkBasicInfo(location);
                    if(check==false)
                    {
                    ui.showBasicInfo(weatherService,location,storage );
                    }
                    break;
                }
                case 3:
                {
                    boolean check=storage.checkBasicInfo(location);
                    if(check==false)
                    {
                    ui.showSunriseSunset(weatherService,location,storage);
                    }
                    break;
               }
                case 4:
                    ui.showWeatherForecast(weatherService, ui);
                    break;
                case 5:
                    ui.showAirPollution(weatherService, ui);
                    break;
                case 6:
                    ui.showPollutingGases(weatherService, ui);
                    break;
                case 7:
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
