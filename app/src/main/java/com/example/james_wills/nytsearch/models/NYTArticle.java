package com.example.james_wills.nytsearch.models;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by james_wills on 5/21/16.
 */
public class NYTArticle {
  private String url;
  private String imageURL;
  private String snippet;
  private String leadParagraph;
  private String headline;
  private ArrayList<Keyword> keywords;
  private String pubDate;
  private String id;
  private String typeOfMaterial;

  private int imageWidth;
  private int imageHeight;

  private ArrayList<String> authors;

  private SimpleDateFormat responseDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  private SimpleDateFormat appDF = new SimpleDateFormat("MM/dd/yy");

  public static String BYLINE_KEY = "byline";

  public NYTArticle(JSONObject doc) throws JSONException {
    url = doc.getString("web_url");
    snippet = doc.getString("snippet");
    leadParagraph = doc.getString("lead_paragraph");
    headline = doc.getJSONObject("headline").getString("main");
    keywords = Keyword.fromJSONArray(doc.getJSONArray("keywords"));
    id = doc.getString("_id");
    typeOfMaterial = doc.getString("type_of_material");

    JSONArray media = doc.getJSONArray("multimedia");
    Log.i("JB", media.length() + "");
    if (media.length() > 0 ) {
      JSONObject imageData = getLargestImageData(media);
      imageURL = imageData.getString("url");
      imageHeight = imageData.getInt("height");
      imageWidth = imageData.getInt("width");
    } else {
      imageURL = null;
      imageHeight = 0;
      imageWidth = 0;
    }

    Object byline = doc.get("byline");
    if (byline == null) {
      authors = new ArrayList<>();
    } else if (byline instanceof  JSONObject) {
      authors = parseAuthors((JSONObject) byline);
    } else if (byline instanceof  JSONArray) {
      Log.i("JSONARRAY", ((JSONArray) byline).toString(2));
    }

    try {
      pubDate = appDF.format(responseDF.parse(doc.getString("pub_date")));
    } catch (ParseException e) {
      Log.d("JAMES", "Error parsing date string: " + doc.getString("pub_date"));
      e.printStackTrace();
      pubDate = "";
    }
  }

  public String getUrl() {
    return url;
  }

  public String getSnippet() {
    return snippet;
  }

  public String getLeadParagraph() {
    return leadParagraph;
  }

  public String getHeadline() {
    return headline;
  }

  public String getPubDate() {
    return pubDate;
  }

  public String getId() {
    return id;
  }

  public String getTypeOfMaterial() {
    return typeOfMaterial;
  }

  public ArrayList<String> getAuthors() {
    return authors;
  }

  public String getImageURL() {
    return imageURL;
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public int getProportionalImageHeight(int widthPx) {
    if (imageHeight == 0 || imageWidth == 0) {
      return 0;
    }

    return (int) ((double) imageHeight / (double) imageWidth) * widthPx;
  }

  public ArrayList<String> getKeywords() {
    ArrayList<String> result = new ArrayList<>();
    for (Keyword k : keywords) {
      result.add(k.value);
    }

    return result;
  }

  public static ArrayList<NYTArticle> fromJSONArray(JSONArray arr) throws JSONException {
    ArrayList<NYTArticle> articles = new ArrayList<>();
    for (int i = 0; i < arr.length(); i++) {
      try {
        articles.add(new NYTArticle(arr.getJSONObject(i)));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return articles;
  }

  public String toString() {
    return headline;
  }

  private static ArrayList<String> parseAuthors(JSONObject o) throws JSONException {
    JSONArray people = o.getJSONArray("person");
    if (people.length() != 0) {
      return authorsFromJSONArray(people);
    }

    ArrayList<String> result = new ArrayList<>();
    if (o.has("organization")) {
      result.add(o.getString("organization"));
    } else if (o.has("original")) {
      result.add(o.getString("original"));
    }

    return result;
  }

  private static ArrayList<String> authorsFromJSONArray(JSONArray arr) throws JSONException {
    ArrayList<String> authors = new ArrayList<>();
    String[] nameKeys = { "firstname", "middlename", "lastname" };
    for (int i = 0; i < arr.length(); i++) {
      try {
        JSONObject o = arr.getJSONObject(i);
        ArrayList<String> names = new ArrayList<>();
        for (String nameKey : nameKeys) {
          if (o.has(nameKey)) {
            String name = o.getString(nameKey);
            if (name.length() > 0) {
              names.add(name.substring(0, 1).toUpperCase() + name.substring(1));
            }
          }
        }

        String fullName = TextUtils.join(" ", names.toArray());
        authors.add(fullName);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return authors;
  }

  private static JSONObject getLargestImageData(JSONArray images) throws JSONException {
    int maxWidth = 0;
    JSONObject maxObject = null;

    for (int i = 0; i < images.length(); i++) {
      JSONObject image = images.getJSONObject(i);
      int width = image.getInt("width");
      Log.i("JB", "width " +width + " maxwidth " + maxWidth);
      if (width > maxWidth) {
        maxWidth = width;
        maxObject = image;
      }
    }

    return maxObject;
  }
}

class Keyword {
  public int rank;
  public String name;
  public String value;

  public Keyword(JSONObject o) throws JSONException {
    rank = Integer.parseInt(o.getString("rank"));
    name = o.getString("name");
    value = o.getString("value");
  }

  public static ArrayList<Keyword> fromJSONArray(JSONArray arr) throws JSONException {
    ArrayList<Keyword> keywords = new ArrayList<>();
    for (int i = 0; i < arr.length(); i++) {
      keywords.add(new Keyword(arr.getJSONObject(i)));
    }

    return keywords;
  }
}
