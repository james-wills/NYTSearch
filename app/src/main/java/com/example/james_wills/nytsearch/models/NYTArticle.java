package com.example.james_wills.nytsearch.models;

import android.text.TextUtils;
import android.util.Log;

import com.example.james_wills.nytsearch.utils.DateFormatUtils;

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
  private String pubDate;
  private String id;
  private String typeOfMaterial;

  private int imageWidth;
  private int imageHeight;

  private ArrayList<String> authors;

  public static String BYLINE_KEY = "byline";

  public NYTArticle(JSONObject doc) throws JSONException {
    url = doc.getString("web_url");
    snippet = doc.getString("snippet");
    leadParagraph = doc.getString("lead_paragraph");
    headline = doc.getJSONObject("headline").getString("main");
    id = doc.getString("_id");
    typeOfMaterial = doc.getString("type_of_material");

    JSONObject imageData = getLargestImageData(doc.getJSONArray("multimedia"));
    if (imageData != null) {
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
      pubDate = DateFormatUtils.convert(
          doc.getString("pub_date"), DateFormatUtils.RESPONSE_FORMAT, DateFormatUtils.USER_FORMAT) ;
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
    int max = 0;
    JSONObject maxObject = null;

    for (int i = 0; i < images.length(); i++) {
      JSONObject image = images.getJSONObject(i);
      if (image.has("width") && image.has("height") && image.has("url")) {
        int width = image.getInt("width");
        if (width > max) {
          max = width;
          maxObject = image;
        }
      }
    }

    return maxObject;
  }
}
