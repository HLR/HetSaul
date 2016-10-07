/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures;

import org.json.JSONObject;

/**
 * The data for a Twitter user.
 *
 * More fields/info here:
 * https://dev.twitter.com/overview/api/users
 */
public class User {
    /** The user-defined UTF-8 string describing their account. */
    private final String description;

    /**
     * The name of the user, as they’ve defined it. Not necessarily a person’s name.
     * Typically capped at 20 characters, but subject to change.
     */
    private final String name;

    /**
     * The screen name, handle, or alias that this user identifies themselves with.
     * Typically a maximum of 15 characters long, but some historical accounts may exist with longer names.
     */
    private final String screenName;

    /** The number of followers this account currently has. */
    private final int numFollowers;

    /** The number of users this account is following (AKA their “followings”). */
    private final int numFriends;

    public User(JSONObject obj) {
        this.description = (obj.has("description") && !obj.isNull("description")) ? obj.getString("description") : "n/a";
        this.name = obj.getString("name");
        this.screenName = obj.getString("screen_name");
        this.numFollowers = obj.getInt("followers_count");
        this.numFriends = obj.getInt("friends_count");
    }

    /**
     * @return The user-defined UTF-8 string describing their account.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The name of the user, as they’ve defined it. Not necessarily a person’s name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The screen name, handle, or alias that this user identifies themselves with.
     */
    public String getScreenName() {
        return screenName;
    }

    /**
     * @return The number of followers this account currently has.
     */
    public int getNumFollowers() {
        return numFollowers;
    }

    /**
     * @return The number of users this account is following (AKA their “followings”).
     */
    public int getNumFriends() {
        return numFriends;
    }
}
