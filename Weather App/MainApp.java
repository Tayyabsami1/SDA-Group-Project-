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
import java.text.SimpleDateFormat;
import java.util.Date;

interface UserInterface {

    void displayLocationOptions(List<Location> locations);

    Location getLocationInput();

    int getMenuChoice();


    // List<Location> getLocationsByLatLngInput();

    // List<Location> getLocationsByCityInput();

    void showCurrentWeather(WeatherService weatherService,Location location,Storage storage );

    void showBasicInfo(WeatherService weatherService, Location location,Storage storage);

    void showSunriseSunset(WeatherService weatherService,Location location,Storage storage);

    void showWeatherForecast(WeatherService weatherService, Location location,Storage storage);

    void showAirPollution(WeatherService weatherService,Location location,Storage storage);

    void showPollutingGases(WeatherService weatherService,Location location,Storage storage);

}

interface Storage {
    void saveLocation(Location location);

    List<Location> getLocations();

    void saveBasicInfo(Location location, double fl,double Tmin, double Tmax);
    boolean checkBasicInfo(Location location);
    void saveSunInfo(Location location, String SunR, String SunS);
    boolean checkSunInfo(Location location);
    void saveCurrentInfo(Location location, String main,String  description,double temp, int pressure, int humidity,double speed);
    boolean checkCurrentInfo(Location location);
    void saveAirPollution(Location location,int aqi,AirQuality object);
    boolean checkAirPollution(Location location);
    boolean checkPollutingGases(Location location);
    void saveForecastInfo(Location location,Forecast ForecastData);
    boolean checkForecastInfo(Location location);

}
// * API Logic starts here
// https://api.openweathermap.org/data/2.5/weather?lat=33.44&lon=94.04&appid={yourownapikey}
// http://api.openweathermap.org/data/2.5/air_pollution?lat={lat}&lon={lon}&appid={APIkey}
// https://api.openweathermap.org/data/2.5/forecast?lat=33.44&lon=94.04&appid={APIKEY}
//https://api.openweathermap.org/data/2.5/weather?q=Pakistan&APPID={APIKEY}

interface WeatherService {
    // Get Normal Weather Data
    WeatherData getWeatherData(Coord location);
    WeatherData getWeatherData(String country);
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
                + "&date=2020-03-04&appid=109a96ae51ebbed7fa95540a48ba65b2";

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
    public WeatherData getWeatherData(String country) {
        WeatherData myweatherData = new WeatherData();
        api="https://api.openweathermap.org/data/2.5/weather?q="+country+"&APPID=109a96ae51ebbed7fa95540a48ba65b2";
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
                + "&date=2020-03-04&appid=109a96ae51ebbed7fa95540a48ba65b2";

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
                + "&date=2020-03-04&appid=109a96ae51ebbed7fa95540a48ba65b2";

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
//    public static void main(String[] args) {
//        WeatherServiceImpl myobj = new WeatherServiceImpl(
//                "https://api.openweathermap.org/data/2.5/weather?lat=33.44&lon=94.04&date=2020-03-04&appid=109a96ae51ebbed7fa95540a48ba65b2");
//
//        System.out.println("Working fine ");
//
//        Coord myloc = new Coord(25.5, 20.5);
//
//        WeatherData MyApiData = myobj.getWeatherData(myloc);
////        WeatherData MyApiData2 = myobj.getWeatherData("Pakistan");
//        Forecast ForecastData = myobj.getForecastData(myloc);
//        AirPollution MyPollutionData=myobj.getPollutionData(myloc);
//        // ! This returns a list of 40 weather forecase for the next 5 days each list
//        // contains forecast of 3hrs
//        System.out.println(MyApiData.getVisibility());
////        System.out.println(MyApiData2.getSys().country);
////        System.out.println(ForecastData.getList().size());
//        System.out.println("No2 in Air is "+MyPollutionData.getList().get(0).getComponents().no2);
//    }
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
        this.weather = new ArrayList<>();
        this.visibility = 0;
        this.dt = 0;
        this.timezone = 0;
        this.id = 0;
        this.name = "";
        this.cod = 0;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
    public void setMain(Main m)
    {
        this.main=m;
    }
    public void setWind(Wind w)
    {
        this.wind=w;
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
    public String getDtText() {
        return dt_txt;
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
    public void setDtText(String s)
    {
        dt_txt=s;
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

    public void setName(String n)
    {
        name=n;
    }
    public void setCName(String n)
    {
        country=n;
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
    public String location;
    public String date;

    public AirIndex()
    {

    }

    public AirIndex(int aqi) {
        this.aqi = aqi;
    }
    public void setaqi(int a)
    {
        aqi=a;
    }
    public int getAqi() {
        return this.aqi;
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
    public void setCo(double c) {
        co=c;
    }

    public void setNo(double c) {
        no=c;
    }

    public void setNo2(double c) {
        no2=c;
    }

    public void setO3(double c) {
        o3=c;
    }

    public void setSo2(double c) {
        so2=c;
    }

    public void setPm2_5(double c) {
        pm2_5=c;
    }

    public void setPm10(double c) {
        pm10=c;
    }

    public void setNh3(double c) {
        nh3=c;
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
    public void setMain(String s)
    {
        main=s;
    }
    public void setDescription(String s)
    {
        description=s;
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
    public  void setTemp(double d)
    {
        temp=d;
    }
    public  void setPressure(int d)
    {
        pressure=d;
    }
    public  void setHumidity(int d)
    {
        humidity=d;
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

    public  void setSpeed(double d)
    {
        speed=d;
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
    public long sunrise;
    public long sunset;
    public String pod;
    String location;
    String date;

    public Sys() {
        this.sunrise = 0; // Default sunrise time (seconds since epoch)
        this.sunset = 0; // Default sunset time (seconds since epoch)
    }

    public Sys(int sunrise, int sunset) {
        this.sunrise = sunrise;
        this.sunset = sunset;
    }
    public  void setLocation(String l)
    {
        location=l;
    }
    public  void setDate(String d)
    {
        date=d;
    }
    public void setSunrise(long s)
    {
        sunrise=s;
    }
    public void setSunset(long s)
    {
        sunset=s;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }
    public String getLocation()
    {
        return location;
    }
    public String getDate()
    {
        return date;
    }

}
// * API logic ends here

class TerminalUI implements UserInterface {
    private final Scanner scanner = new Scanner(System.in);

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
    public void showCurrentWeather(WeatherService weatherService,Location location,Storage storage )
    {
        // Implement as required
        String main;
        String description;
        double temp;
        int pressure;
        int humidity;
        double speed;
        Coord myloc = new Coord(location.getLatitude(), location.getLongitude());
        WeatherData Data = weatherService.getWeatherData(myloc);
        temp=Data.getMain().getTemp();
        pressure=Data.getMain().getPressure();
        humidity=Data.getMain().getHumidity();
        speed=Data.getWind().getSpeed();
        main =Data.getWeather().get(0).getMain();
        description=Data.getWeather().get(0).getDescription();
        System.out.println("Weather: "+main+"\nDescription: "+description+"\nTemperature: "+temp+
                "\nPressure: "+pressure+"\nHumidity: "+humidity+"\nWind Speed: "+speed);
        storage.saveCurrentInfo(location, main, description, temp,pressure,humidity,speed);

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
        System.out.println("\nFeels Like: "+feels_like+"\nMinimum Temperature: "+temp_min+"\nMaximum Temperature: "+temp_max);
        storage.saveBasicInfo(location, feels_like, temp_min, temp_max);
    }

    @Override
    public void showSunriseSunset(WeatherService weatherService,Location location,Storage storage) {
        // Implement as required
        long  sun_rise;
        long  sun_set;
        Coord myloc = new Coord(location.getLatitude(), location.getLongitude());
        WeatherData Data = weatherService.getWeatherData(myloc);
        sun_rise=Data.getSys().getSunrise();
        sun_set=Data.getSys().getSunset();

        // Convert timestamp to Date object (assuming seconds)
        Date date1 = new Date(sun_rise * 1000); // Multiply by 1000 if milliseconds
        Date date2 = new Date(sun_set * 1000);
        // Format the date to "hh:mm:ss" format
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); // Use "HH" for 24-hour format
        String SunR = formatter.format(date1);
        String SunS = formatter.format(date2);
        System.out.println("Sun Rise Time: "+SunR+"\nSun Set Time: "+SunS);
        storage.saveSunInfo(location, SunR, SunS);
    }

    @Override
    public void showWeatherForecast(WeatherService weatherService, Location location,Storage storage) {
        // Implement as required
        Coord myloc = new Coord(location.getLatitude(), location.getLongitude());

        Forecast ForecastData = weatherService.getForecastData(myloc);
        List<WeatherData> list=ForecastData.getList();
        // ! This returns a list of 40 weather forecase for the next 5 days each list
        // contains forecast of 3hrs
        System.out.println("Weather Forecast for 5 days : ");
        int i=0;
        for (WeatherData weather : list) {

            System.out.println(++i+":\n\tWeather: " + weather.getWeather().get(0).getMain()+
                    "\n\tDescription: " + weather.getWeather().get(0).getDescription()+
                    "\n\tTemperature: " + weather.getMain().getTemp()+
                    "\n\tPressure: " + weather.getMain().getPressure()+
                    "\n\tHumidity: " + weather.getMain().getHumidity()+
                    "\n\tFeels Like: " + weather.getMain().getFeelsLike()+
                    "\n\tMinimum Temperature: " + weather.getMain().getTempMin()+
                    "\n\tMaximum Temperature: " + weather.getMain().getTempMax()+
                    "\n\tWind Speed: " + weather.getWind().getSpeed()+
                    "\n\tTime of Data Forecasted: " + weather.getDtText()+"\n");
        }
        storage.saveForecastInfo(location,ForecastData);
    }

    @Override
    public void showAirPollution(WeatherService weatherService,Location location,Storage storage) {
        // Implement as required

        Coord myloc = new Coord(location.getLatitude(), location.getLongitude());
        AirPollution MyPollutionData=weatherService.getPollutionData(myloc);
        int aqi=MyPollutionData.getList().get(0).getMain().getAqi();
        System.out.println("Air pollution Data: "+"\n\nAir Quality Index: "+aqi);
        storage.saveAirPollution(location,aqi,MyPollutionData.getList().get(0));
    }

    @Override
    public void showPollutingGases(WeatherService weatherService,Location location,Storage storage) {
        // Implement as required
        Coord myloc = new Coord(location.getLatitude(), location.getLongitude());
        AirPollution MyPollutionData=weatherService.getPollutionData(myloc);
        AirQuality object=MyPollutionData.getList().get(0);
        int aqi=MyPollutionData.getList().get(0).getMain().getAqi();
        System.out.println("Deatails Of Polluting Gases: \nCO: "+object.getComponents().getCo()+"\nNO: "+object.getComponents().getNo()+"\nNO2: "+object.getComponents().getNo2()+
                "\nO3: "+object.getComponents().getO3()+"\nSO2: "+object.getComponents().getSo2()+"\nPM2_5: "+object.getComponents().getPm2_5()+
                "\nPM10: "+object.getComponents().getPm10()+"\nNH3: "+object.getComponents().getNh3());
        storage.saveAirPollution(location,aqi,MyPollutionData.getList().get(0));
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
    public void saveAirPollution(Location location,int aqi,AirQuality object)
    {
        try {

            File myFile = new File("AirPollution.txt");
            FileWriter writer = new FileWriter(myFile,true);
            LocalDate today = LocalDate.now();
            writer.write(location.getName());
            writer.write(",");
            writer.write(String.valueOf(aqi));
            writer.write(",");
            writer.write(String.valueOf(today));
            writer.write(",");
            writer.write(String.valueOf(object.getComponents().getCo()));
            writer.write(",");
            writer.write(String.valueOf(object.getComponents().getNo()));
            writer.write(",");
            writer.write(String.valueOf(object.getComponents().getNo2()));
            writer.write(",");
            writer.write(String.valueOf(object.getComponents().getO3()));
            writer.write(",");
            writer.write(String.valueOf(object.getComponents().getSo2()));
            writer.write(",");
            writer.write(String.valueOf(object.getComponents().getPm2_5()));
            writer.write(",");
            writer.write(String.valueOf(object.getComponents().getPm10()));
            writer.write(",");
            writer.write(String.valueOf(object.getComponents().getNh3()));
            writer.write("\n");
            writer.close();
        }
        catch (IOException e)
        {
            System.err.println("Error writing to file: " + e.getMessage());
        }


    }
    @Override
    public boolean checkAirPollution(Location location)
    {
        String delimiter = ",";  // Word to stop reading
        int numberOfLines = 0;
        LocalDate today = LocalDate.now();
        boolean check=false;
        try{
            LineNumberReader reader = new LineNumberReader(new FileReader("AirPollution.txt"));
            while (reader.readLine() != null) {
                numberOfLines++;
            }
            reader.close();

        }
        catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        AirIndex[] arr= new AirIndex[numberOfLines];
        try{
            Scanner scanner = new Scanner(new File("AirPollution.txt"));
            scanner.useDelimiter(delimiter);  // Set delimiter for splitting

            for( int i=0;i<arr.length;i++)
            {
                arr[i] = new AirIndex();
                arr[i].setLocation(scanner.next());
                arr[i].setaqi(Integer.parseInt(scanner.next()));
                arr[i].setDate(scanner.next());
                String firstLine = scanner.nextLine();

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
        System.out.println("Air Pollution Data: \nAir Quality Index: "+arr[index].getAqi());
        return true;

    }
    @Override
    public boolean checkPollutingGases(Location location)
    {
        String delimiter = ",";  // Word to stop reading
        int numberOfLines = 0;
        LocalDate today = LocalDate.now();
        boolean check=false;
        try{
            LineNumberReader reader = new LineNumberReader(new FileReader("AirPollution.txt"));
            while (reader.readLine() != null) {
                numberOfLines++;
            }
            reader.close();

        }
        catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        AirIndex[] arr= new AirIndex[numberOfLines];
        Components[] arr1= new Components[numberOfLines];
        try{
            Scanner scanner = new Scanner(new File("AirPollution.txt"));
            scanner.useDelimiter(delimiter);  // Set delimiter for splitting

            for( int i=0;i<arr.length;i++)
            {
                arr[i] = new AirIndex();
                arr1[i] = new Components();
                arr[i].setLocation(scanner.next());
                arr[i].setaqi(Integer.parseInt(scanner.next()));
                arr[i].setDate(scanner.next());
                arr1[i].setCo(Double.parseDouble(scanner.next()));
                arr1[i].setNo(Double.parseDouble(scanner.next()));
                arr1[i].setNo2(Double.parseDouble(scanner.next()));
                arr1[i].setO3(Double.parseDouble(scanner.next()));
                arr1[i].setSo2(Double.parseDouble(scanner.next()));
                arr1[i].setPm2_5(Double.parseDouble(scanner.next()));
                arr1[i].setPm10(Double.parseDouble(scanner.next()));
                String firstLine = scanner.nextLine();
                arr1[i].setNh3(Double.parseDouble(firstLine.substring(1)));
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
        System.out.println("Deatails Of Polluting Gases: \nCO: "+arr1[index].getCo()+"\nNO: "+arr1[index].getNo()+"\nNO2: "+arr1[index].getNo2()+
                "\nO3: "+arr1[index].getO3()+"\nSO2: "+arr1[index].getSo2()+"\nPM2_5: "+arr1[index].getPm2_5()+
                "\nPM10: "+arr1[index].getPm10()+"\nNH3: "+arr1[index].getNh3());
        return true;


    }
    @Override
    public void saveForecastInfo(Location location,Forecast ForecastData)
    {
        try {

            File myFile = new File("ForecastInfo.txt");
            FileWriter writer = new FileWriter(myFile,true);
            LocalDate today = LocalDate.now();
            writer.write(location.getName());
            writer.write(",");
            for (WeatherData weather : ForecastData.getList()) {

                writer.write(weather.getWeather().get(0).getMain());
                writer.write(",");
                writer.write(weather.getWeather().get(0).getDescription());
                writer.write(",");
                writer.write(String.valueOf( weather.getMain().getTemp()));
                writer.write(",");
                writer.write(String.valueOf(weather.getMain().getPressure()));
                writer.write(",");
                writer.write(String.valueOf(weather.getMain().getHumidity()));
                writer.write(",");
                writer.write(String.valueOf(weather.getMain().getFeelsLike()));
                writer.write(",");
                writer.write(String.valueOf(weather.getMain().getTempMin()));
                writer.write(",");
                writer.write(String.valueOf(weather.getMain().getTempMax()));
                writer.write(",");
                writer.write(String.valueOf(weather.getWind().getSpeed()));
                writer.write(",");
                writer.write(weather.getDtText());
                writer.write(",");
            }

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
    public boolean checkForecastInfo(Location location)
    {
        String delimiter = ",";  // Word to stop reading
        int numberOfLines = 0;
        LocalDate today = LocalDate.now();
        boolean check=false;
        try{
            LineNumberReader reader = new LineNumberReader(new FileReader("ForecastInfo.txt"));
            while (reader.readLine() != null) {
                numberOfLines++;
            }
            reader.close();

        }
        catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        WeatherData[][] array = new WeatherData[numberOfLines][40];
//        for(int i=0;i<numberOfLines;i++)
//        {
//            array[i]=new WeatherData[40];
//        }

        Main[] arr1= new Main[numberOfLines];
        try{
            Scanner scanner = new Scanner(new File("ForecastInfo.txt"));
            scanner.useDelimiter(delimiter);  // Set delimiter for splitting

            for( int i=0;i<arr1.length;i++)
            {

                arr1[i] = new Main();

                arr1[i].setLocation(scanner.next());

                for(int j=0;j<40;j++)
                {
                    array[i][j]=new WeatherData();
                    Weather weather1 = new Weather(0,"m","d","10");
                    Main obj =new Main();
                    Wind obj1=new Wind();
                    array[i][j].getWeather().add(weather1);
                    array[i][j].getWeather().get(0).setMain(scanner.next());
                    array[i][j].getWeather().get(0).setDescription(scanner.next());
                    array[i][j].setMain(obj);
                    array[i][j].getMain().setTemp(Double.parseDouble(scanner.next()));
                    array[i][j].getMain().setPressure(Integer.parseInt(scanner.next()));
                    array[i][j].getMain().setHumidity(Integer.parseInt(scanner.next()));
                    array[i][j].getMain().setFeelsike(Double.parseDouble(scanner.next()));
                    array[i][j].getMain().setTemp_Min(Double.parseDouble(scanner.next()));
                    array[i][j].getMain().setTemp_Max(Double.parseDouble(scanner.next()));
                    array[i][j].setWind(obj1);
                    array[i][j].getWind().setSpeed(Double.parseDouble(scanner.next()));

                    array[i][j].setDtText(scanner.next());
                }
                String firstLine = scanner.nextLine();
                arr1[i].setDate(firstLine.substring(1));

            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("Error opening file:  " + e.getMessage());
        }
        int index=0;
        for(int i=0;i<arr1.length;i++)
        {

            if(arr1[i].getLocation().equalsIgnoreCase(location.getName()))
            {
                if(arr1[i].getDate().equals(String.valueOf(today)))
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
        System.out.println("Weather Forecast for 5 days : ");
        int i=0;
        for (int j=0;j<40;j++) {

            System.out.println(++i+":\n\tWeather: " + array[index][j].getWeather().get(0).getMain()+
                    ":\n\tDescription: " + array[index][j].getWeather().get(0).getDescription()+
                    ":\n\tTemperature: " + array[index][j].getMain().getTemp()+
                    ":\n\tPressure: " + array[index][j].getMain().getPressure()+
                    ":\n\tHumidity: " + array[index][j].getMain().getHumidity()+
                    ":\n\tFeels Like: " + array[index][j].getMain().getFeelsLike()+
                    ":\n\tMinimum Temperature: " + array[index][j].getMain().getTempMin()+
                    ":\n\tMaximum Temperature: " + array[index][j].getMain().getTempMax()+
                    ":\n\tWind Speed: " + array[index][j].getWind().getSpeed()+
                    ":\n\tTime of Data Forecasted: " + array[index][j].getDtText()+"\n");
        }
        return true;

    }

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
        System.out.println("Feels Like: "+arr[index].getFeelsLike()+"\nMinimum Temperature: "+arr[index].getTempMin()+"\nMaximum Temperature: "+arr[index].getTempMax());
        return true;

    }

    @Override
    public void saveSunInfo(Location location, String SunR, String SunS)
    {
        try {

            File myFile = new File("SunInfo.txt");
            FileWriter writer = new FileWriter(myFile,true);
            LocalDate today = LocalDate.now();
            writer.write(location.getName());
            writer.write(",");
            writer.write(SunR);
            writer.write(",");
            writer.write(SunS);
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
    public boolean checkSunInfo(Location location)
    {
        String delimiter = ",";  // Word to stop reading
        int numberOfLines = 0;
        LocalDate today = LocalDate.now();
        boolean check=false;
        try{
            LineNumberReader reader = new LineNumberReader(new FileReader("SunInfo.txt"));
            while (reader.readLine() != null) {
                numberOfLines++;
            }
            reader.close();

        }
        catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        Sys[] arr= new Sys[numberOfLines];
        String[] Sunrise = new String[numberOfLines];
        String[] Sunset = new String[numberOfLines];
        try{
            Scanner scanner = new Scanner(new File("SunInfo.txt"));
            scanner.useDelimiter(delimiter);  // Set delimiter for splitting

            for( int i=0;i<arr.length;i++)
            {
                arr[i] = new Sys();
                arr[i].setLocation(scanner.next());
                Sunrise[i]=scanner.next();
                Sunset[i]=scanner.next();
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
        System.out.println("Sun Rise Time: "+ Sunrise[index]+"\nSun Set Time: "+Sunset[index]);
        return true;

    }
    @Override
    public void saveCurrentInfo(Location location, String main,String  description,double temp, int pressure, int humidity,double speed)
    {
        try {

            File myFile = new File("CurrentInfo.txt");
            FileWriter writer = new FileWriter(myFile,true);
            LocalDate today = LocalDate.now();
            writer.write(location.getName());
            writer.write(",");
            writer.write(main);
            writer.write(",");
            writer.write(description);
            writer.write(",");
            writer.write(String.valueOf(temp));
            writer.write(",");
            writer.write(String.valueOf(pressure));
            writer.write(",");
            writer.write(String.valueOf(humidity));
            writer.write(",");
            writer.write(String.valueOf(speed));
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
    public boolean checkCurrentInfo(Location location)
    {
        String delimiter = ",";  // Word to stop reading
        int numberOfLines = 0;
        LocalDate today = LocalDate.now();
        boolean check=false;
        try{
            LineNumberReader reader = new LineNumberReader(new FileReader("CurrentInfo.txt"));
            while (reader.readLine() != null) {
                numberOfLines++;
            }
            reader.close();

        }
        catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        Main[] arr= new Main[numberOfLines];
        Weather[] arr1= new Weather[numberOfLines];
        Wind[] arr2= new Wind[numberOfLines];
        try{
            Scanner scanner = new Scanner(new File("CurrentInfo.txt"));
            scanner.useDelimiter(delimiter);  // Set delimiter for splitting

            for( int i=0;i<arr.length;i++)
            {
                arr[i] = new Main();
                arr1[i] = new Weather();
                arr2[i] = new Wind();
                arr[i].setLocation(scanner.next());
                arr1[i].setMain(scanner.next());
                arr1[i].setDescription(scanner.next());
                arr[i].setTemp(Double.parseDouble(scanner.next()));
                arr[i].setPressure(Integer.parseInt(scanner.next()));
                arr[i].setHumidity(Integer.parseInt(scanner.next()));
                arr2[i].setSpeed(Double.parseDouble(scanner.next()));
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
        System.out.println("Weather:  "+arr1[index].getMain()+"\nDescription: "+arr1[index].getDescription()+"\nTemperature: "+arr[index].getTemp()+
                "\nPressure: "+arr[index].getPressure()+"\nHumidity: "+arr[index].getHumidity()+"\nSpeed: "+arr2[index].getSpeed());
        return true;
    }


    @Override
    public void saveLocation(Location location) {
//        List<Location> locations = getLocations();
//        locations.add(location);
//        writeLocationsToFile(locations);
    }

    @Override
    public List<Location> getLocations() {
//        File file = new File(storageFile);
//        if (!file.exists()) {
//            return new ArrayList<>();
//        }
//        try {
//            return objectMapper.readValue(file, List.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
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
class MainApp {

    public static void main(String[] args) {
        UserInterface ui = new TerminalUI();
        Storage storage = new FileStorage();
        WeatherService weatherService = new WeatherServiceImpl(
                "https://api.openweathermap.org/data/2.5/weather?lat=33.44&lon=94.04&date=2020-03-04&appid=109a96ae51ebbed7fa95540a48ba65b2"); // Replace with actual API
        // implementation

        // List<Location> locationsByLatLng = ui.getLocationsByLatLngInput();

        // // Get multiple locations by city/country name
        // List<Location> locationsByCity = ui.getLocationsByCityInput();

        Location location=ui.getLocationInput();
        while (true) {
            int choice = ui.getMenuChoice();
            switch (choice) {
                case 1:
                {
                    File file = new File("CurrentInfo.txt");
                    if(file.exists())
                    {
                        boolean check=storage.checkCurrentInfo(location);
                        if(check==false)
                        {
                            ui.showCurrentWeather(weatherService,location,storage );
                        }
                    }
                    else {
                        ui.showCurrentWeather(weatherService,location,storage );
                    }
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("\nEnter any key to continue: ");
                    char inputChar = scanner.next().charAt(0);
                    break;
                }
                case 2:
                {
                    File file = new File("BasicInfo.txt");
                    if(file.exists()) {
                        boolean check = storage.checkBasicInfo(location);
                        if (check == false) {
                            ui.showBasicInfo(weatherService, location, storage);
                        }
                    }else {
                        ui.showBasicInfo(weatherService, location, storage);
                    }
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("\nEnter any key to continue: ");
                    char inputChar = scanner.next().charAt(0);
                    break;
                }
                case 3:
                {
                    File file = new File("SunInfo.txt");
                    if(file.exists())
                    {
                        boolean check=storage.checkSunInfo(location);
                        if(check==false)
                        {
                            ui.showSunriseSunset(weatherService,location,storage);
                        }
                    }
                    else {
                        ui.showSunriseSunset(weatherService,location,storage);
                    }
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("\nEnter any key to continue: ");
                    char inputChar = scanner.next().charAt(0);

                    break;
                }
                case 4:
                {
                    File file = new File("ForecastInfo.txt");
                    if(file.exists()) {
                        boolean check = storage.checkForecastInfo(location);
                        if (check == false) {
                            ui.showWeatherForecast(weatherService, location, storage);
                        }
                    }
                    else {
                        ui.showWeatherForecast(weatherService, location, storage);
                    }
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("\nEnter any key to continue: ");
                    char inputChar = scanner.next().charAt(0);
                    break;
                }
                case 5:
                {
                    File file = new File("AirPollution.txt");
                    if(file.exists()) {
                        boolean check = storage.checkAirPollution(location);
                        if (check == false) {
                            ui.showAirPollution(weatherService, location, storage);
                        }
                    }
                    else {
                        ui.showAirPollution(weatherService, location, storage);
                    }
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("\nEnter any key to continue: ");
                    char inputChar = scanner.next().charAt(0);
                    break;
                }
                case 6: {
                    File file = new File("AirPollution.txt");
                    if(file.exists()) {
                        boolean check = storage.checkPollutingGases(location);
                        if (check == false) {
                            ui.showPollutingGases(weatherService, location, storage);
                        }
                    }
                    else
                    {
                        ui.showPollutingGases(weatherService, location, storage);
                    }
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("\nEnter any key to continue: ");
                    char inputChar = scanner.next().charAt(0);
                    break;
                }
                case 7:
                    System.out.println("Exiting weather app.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
