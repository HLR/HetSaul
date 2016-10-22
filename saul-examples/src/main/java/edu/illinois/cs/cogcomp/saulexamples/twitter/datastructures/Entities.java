/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the structure of a tweet's `entities` field
 *
 * More info here:
 * https://dev.twitter.com/overview/api/entities
 */
public class Entities {
    private List<Hashtag> hashtags = null;
    private List<URL> urls = null;
    private List<Medium> media = null;
    private List<Mention> mentions = null;

    public Entities(JSONObject obj) {
        if (contains("hashtags", obj)) {
            JSONArray hts = obj.getJSONArray("hashtags");
            hashtags = new ArrayList<>(hts.length());
            for (int i = 0; i < hts.length(); i++) {
                JSONObject ht = hts.getJSONObject(i);
                hashtags.add(new Hashtag(ht.getJSONArray("indices"), ht.getString("text")));
            }
        }
        if (contains("urls", obj)) {
            JSONArray hts = obj.getJSONArray("urls");
            urls = new ArrayList<>(hts.length());
            for (int i = 0; i < hts.length(); i++) {
                JSONObject ht = hts.getJSONObject(i);
                urls.add(new URL(ht.getJSONArray("indices"), ht.getString("url")));
            }
        }
        if (contains("media", obj)) {
            JSONArray hts = obj.getJSONArray("media");
            media = new ArrayList<>(hts.length());
            for (int i = 0; i < hts.length(); i++) {
                JSONObject ht = hts.getJSONObject(i);
                media.add(new Medium(ht.getJSONArray("indices"), ht.getString("url"), ht.getString("type")));
            }
        }
        if (contains("mentions", obj)) {
            JSONArray hts = obj.getJSONArray("mentions");
            mentions = new ArrayList<>(hts.length());
            for (int i = 0; i < hts.length(); i++) {
                JSONObject ht = hts.getJSONObject(i);
                mentions.add(new Mention(ht.getJSONArray("indices"), ht.getString("name"), ht.getString("screen_name")));
            }
        }
    }

    private boolean contains(String key, JSONObject obj) {
        return obj.has(key) && obj.getJSONArray(key).length() != 0;
    }

    public boolean hasHashtags() {
        return hashtags != null;
    }

    public boolean hasURLs() {
        return urls != null;
    }

    public boolean hasMedia() {
        return media != null;
    }

    public boolean hasMentions() {
        return mentions != null;
    }

    public List<Hashtag> getHashtags() {
        return hashtags;
    }

    public List<URL> getUrls() {
        return urls;
    }

    public List<Medium> getMedia() {
        return media;
    }

    public List<Mention> getMentions() {
        return mentions;
    }
}
