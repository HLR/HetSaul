/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.twitter.tweet;

import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.Location.Coordinate;
import edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A collection of utility methods for accessing a Tweet's data.
 * <p/>
 * More fields/info here:
 * https://dev.twitter.com/overview/api/tweets
 */
public class Utils {

    /**
     * Fetches the text of the tweet and removes any non-ASCII characters.
     *
     * @param tweet The tweet JSON object
     * @return The cleaned-up (ASCII) version of the text
     */
    public static String getCleanText(JSONObject tweet) {
        String cleanText = tweet.getString("text");
        cleanText = cleanText.replaceAll("[^\\x00-\\x7F]", "");
        return cleanText;
    }

    /**
     * Print some basic information for a given tweet.
     *
     * @param tweet The tweet JSON object
     */
    public static void printInfo(JSONObject tweet) {
        User user = Utils.getUser(tweet);
        Entities entities = Utils.getEntities(tweet);
        System.out.println(user.getName() + "(" + user.getNumFollowers() + ") " + tweet.getString("text"));
        System.out.println("\tASCII only: " + getCleanText(tweet));
        if (entities.hasHashtags()) {
            System.out.println("\tFound hashtags: " + entities.getHashtags());
        }
        if (Utils.getCoordinates(tweet) != null) {
            Coordinate location = Utils.getCoordinates(tweet);
            if (Utils.isInLocation(Locations.ATHENS, location))
                System.out.println("\tLocation: Athens");
            else if (Utils.isInLocation(Locations.URBANA_CHAMPAIGN, location))
                System.out.println("\tLocation: Urbana-Champaign");
            else System.out.println("\tLocation: " + location);
        }
        if (Utils.getPlaceName(tweet) != null) {
            System.out.println("\tPlace: " + Utils.getPlaceName(tweet));
        }
    }

    /**
     * Returns the {@link Location} of a bounding box defined by the parameters
     *
     * @param longSW South-West longitude
     * @param latSW  South-West latitude
     * @param longNE North-East longitude
     * @param latNE  North-East latitude
     * @return The {@link Location} of the bounding box
     */
    public static Location getCityLocation(double longSW, double latSW, double longNE, double latNE) {
        return new Location(new Coordinate(longSW, latSW), new Coordinate(longNE, latNE));
    }

    public static User getUser(JSONObject obj) {
        return new User(obj.getJSONObject("user"));
    }

    public static int getNumFavorites(JSONObject obj) {
        return obj.getInt("favorite_count");
    }

    public static int getNumRetweets(JSONObject obj) {
        return obj.getInt("retweet_count");
    }

    /**
     * Returns the location of the tweet (if available) in terms of {@link Coordinate}s.
     * <p/>
     * Use {@link #isInLocation(Location, Location.Coordinate)} to check if the tweet is within a given {@link Location}.
     *
     * @param obj The JSON object of the tweet
     * @return The {@link Coordinate}s of the tweet's location (if available) or {@code null}.
     */
    public static Coordinate getCoordinates(JSONObject obj) {
        if (!obj.has("coordinates") || obj.isNull("coordinates"))
            return null;
        JSONArray coordinates = obj.getJSONObject("coordinates").getJSONArray("coordinates");
        return new Coordinate(coordinates.getDouble(0), coordinates.getDouble(1));
    }

    /**
     * Returns the location of the <b>place</b> of tweet (if available).
     * <p/>
     * <b>NB:</b> The difference with {@link #getCoordinates(JSONObject)} is that tweets associated with places
     * are not necessarily issued from that location but could also potentially be about that location.
     *
     * @param obj The JSON object of the tweet
     * @return A String of the tweet's <b>place</b> (if available) or {@code null}.
     */
    public static String getPlaceName(JSONObject obj) {
        if (!obj.has("place") || obj.isNull("place"))
            return null;
        JSONObject place = obj.getJSONObject("place");
        String placeName = place.getString("full_name");
        String country = place.getString("country");
        return placeName + ", " + country;
    }

    /**
     * Returns an {@link Entities} object containing (potentially null) lists of
     * {@link Hashtag}s,  {@link Medium} (media), {@link URL}s and {@link Mention}s.
     *
     * @param obj The JSON object of the tweet
     * @return {@link Entities} data structure
     */
    public static Entities getEntities(JSONObject obj) {
        return new Entities(obj.getJSONObject("entities"));
    }

    /**
     * Get the time of the Tweet's creation
     *
     * @param obj The JSON object of the tweet
     * @return String representation of the UTC time when this Tweet was created.
     */
    public static String getCreationTime(JSONObject obj) {
        return obj.getString("created_at");
    }

    public static String getLang(JSONObject obj) {
        return obj.getString("lang");
    }

    /**
     * Checks if the tweet originated from a given {@link Location}.
     *
     * @param location    The bounding box coordinates of the candidate location
     * @param coordinates The coordinates of the tweet
     * @return {@code true} iff the coordinates of the tweet are contained within the bounding box of the {@link Location}.
     */
    public static boolean isInLocation(Location location, Coordinate coordinates) {
        Coordinate southwestCoordinate = location.southwestCoordinate();
        Coordinate northeastCoordinate = location.northeastCoordinate();

        return coordinates.latitude() > southwestCoordinate.latitude() &&
                coordinates.longitude() > southwestCoordinate.longitude() &&
                coordinates.latitude() < northeastCoordinate.latitude() &&
                coordinates.longitude() < northeastCoordinate.longitude();
    }
}
