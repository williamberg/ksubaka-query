package nu.mine.wberg.main;import com.fasterxml.jackson.core.JsonProcessingException;import com.mashape.unirest.http.ObjectMapper;import com.mashape.unirest.http.Unirest;import nu.mine.wberg.query.MovieData;import nu.mine.wberg.query.MovieSource;import nu.mine.wberg.query.MovieSourceFactory;import org.apache.http.impl.client.HttpClients;import java.io.IOException;import java.util.List;public class Main {    private static final int NAME_COLUMN_WIDTH = 60;    private static final int DIRECTOR_COLUMN_WIDTH = 30;    private static final int YEAR_COLUMN_WIDTH = 4;    public static void main(String... args) throws IOException {        configure();        MovieSourceFactory movieSourceFactory = new MovieSourceFactory();        List<MovieData> movieDatas;        try {            MovieSource movieSource = movieSourceFactory.getMovieSource(System.getProperty("api"));            movieDatas = movieSource.getMovieData(System.getProperty("movie"));        }        catch (RuntimeException e) {            System.err.println(e.getMessage());            return;        }        for (MovieData movieData : movieDatas) {            System.out.printf("%-" + NAME_COLUMN_WIDTH + "s " +                    "%-" + DIRECTOR_COLUMN_WIDTH + "s " +                    "%-" + YEAR_COLUMN_WIDTH + "s " +                    "%n",                    movieData.getName(),                    movieData.getDirector(),                    movieData.getYear());        }        Unirest.shutdown();    }    private static void configure() {        Unirest.setObjectMapper(new ObjectMapper() {            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper                    = new com.fasterxml.jackson.databind.ObjectMapper();            public <T> T readValue(String value, Class<T> valueType) {                try {                    return jacksonObjectMapper.readValue(value, valueType);                } catch (IOException e) {                    throw new RuntimeException(e);                }            }            public String writeValue(Object value) {                try {                    return jacksonObjectMapper.writeValueAsString(value);                } catch (JsonProcessingException e) {                    throw new RuntimeException(e);                }            }        });        // disable cookie management because IMDB cookies are invalid and it produces lots of error output        Unirest.setHttpClient(HttpClients.custom().disableCookieManagement().build());    }}