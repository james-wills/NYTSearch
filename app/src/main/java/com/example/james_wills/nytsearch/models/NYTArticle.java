package com.example.james_wills.nytsearch.models;

import android.text.TextUtils;

import com.example.james_wills.nytsearch.utils.DateFormatUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by james_wills on 5/21/16.
 */
public class NYTArticle {

  public static final String BYLINE_KEY = "byline";
  public static final String FIRST_NAME_KEY = "firstname";
  public static final String HEADLINE_KEY = "headline";
  public static final String HEIGHT_KEY = "height";
  public static final String ID_KEY = "_id";
  public static final String LAST_NAME_KEY = "lastname";
  public static final String LEAD_PARAGRAPH_KEY = "lead_paragraph";
  public static final String MAIN_KEY = "main";
  public static final String MIDDLE_NAME_KEY = "middlename";
  public static final String MULTIMEDIA_KEY = "multimedia";
  public static final String ORGANIZATION_KEY = "organization";
  public static final String ORIGINAL_KEY = "original";
  public static final String PERSON_KEY = "person";
  public static final String PUB_DATE_KEY = "pub_date";
  public static final String SNIPPET_KEY = "snippet";
  public static final String TYPE_OF_MATERIAL_KEY = "type_of_material";
  public static final String URL_KEY = "url";
  public static final String WEB_URL_KEY = "web_url";
  public static final String WIDTH_KEY = "width";

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


  public NYTArticle(JSONObject doc) throws JSONException {
    url = doc.getString(WEB_URL_KEY);
    snippet = doc.getString(SNIPPET_KEY);
    leadParagraph = doc.getString(LEAD_PARAGRAPH_KEY);
    headline = doc.getJSONObject(HEADLINE_KEY).getString(MAIN_KEY);
    id = doc.getString(ID_KEY);
    typeOfMaterial = doc.getString(TYPE_OF_MATERIAL_KEY);

    JSONObject imageData = getLargestImageData(doc.getJSONArray(MULTIMEDIA_KEY));
    if (imageData != null) {
      imageURL = imageData.getString(URL_KEY);
      imageHeight = imageData.getInt(HEIGHT_KEY);
      imageWidth = imageData.getInt(WIDTH_KEY);
    } else {
      imageURL = null;
      imageHeight = 0;
      imageWidth = 0;
    }

    Object byline = doc.get(BYLINE_KEY);
    if (byline instanceof  JSONObject) {
      authors = parseAuthors((JSONObject) byline);
    } else {
      authors = new ArrayList<>();
    }

    try {
      pubDate = DateFormatUtils.convert(
          doc.getString(PUB_DATE_KEY), DateFormatUtils.RESPONSE_FORMAT, DateFormatUtils.USER_FORMAT);
    } catch (ParseException e) {
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
    JSONArray people = o.getJSONArray(PERSON_KEY);
    if (people.length() != 0) {
      return authorsFromJSONArray(people);
    }

    ArrayList<String> result = new ArrayList<>();
    if (o.has(ORGANIZATION_KEY)) {
      result.add(o.getString(ORGANIZATION_KEY));
    } else if (o.has(ORIGINAL_KEY)) {
      result.add(o.getString(ORIGINAL_KEY));
    }

    return result;
  }

  private static ArrayList<String> authorsFromJSONArray(JSONArray arr) throws JSONException {
    ArrayList<String> authors = new ArrayList<>();
    String[] nameKeys = { FIRST_NAME_KEY, MIDDLE_NAME_KEY, LAST_NAME_KEY };
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
      if (image.has(WIDTH_KEY) && image.has(HEIGHT_KEY) && image.has(URL_KEY)) {
        int width = image.getInt(WIDTH_KEY);
        if (width > max) {
          max = width;
          maxObject = image;
        }
      }
    }

    return maxObject;
  }
}
