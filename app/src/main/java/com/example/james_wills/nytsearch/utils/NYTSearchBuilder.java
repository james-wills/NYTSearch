package com.example.james_wills.nytsearch.utils;

import android.content.Context;

import com.example.james_wills.nytsearch.R;
import com.example.james_wills.nytsearch.models.NYTArticle;
import com.example.james_wills.nytsearch.models.NYTSearchQueryParams;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by james_wills on 5/21/16.
 */
public class NYTSearchBuilder {

  public static class SearchResult {
    public List<NYTArticle> articles;
    public int hits;
    public boolean isLastPage;

    public SearchResult(List<NYTArticle> articles, int hits, boolean isLastPage) {
      this.articles = articles;
      this.hits = hits;
      this.isLastPage = isLastPage;
    }
  }

  private static final String NYT_SEARCH_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
  private static final String NYT_BASE_URL = "http://www.nytimes.com/";

  // Query Parameters

  public static final int CODE_PARSE_ERROR = 0;
  public static final int CODE_NETWORK_ERROR = 1;
  public static final int CODE_GENERAL_ERROR = 2;
  public static final int CODE_DATE_PARSE_ERROR = 3;

  public static final int PAGE_LENGTH = 10;
  public static final int MAX_PAGES = 100; // limited by the api

  public static void executeAndGetResults(final Context c,
                                          NYTSearchQueryParams params,
                                          final int page,
                                          final NYTArticleListener resultListener) {
    AsyncHttpClient client = new AsyncHttpClient();
    RequestParams requestParams;
    try {
      requestParams =
          params.getRequestParams(page, c.getResources().getString(R.string.nyt_search_key));
    } catch (ParseException e) {
      resultListener.onLoadArticlesFailed(CODE_DATE_PARSE_ERROR);
      e.printStackTrace();
      return;
    }

    requestParams.toString();

    client.get(NYT_SEARCH_URL, requestParams, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        try {
          JSONObject responseJSON = response.getJSONObject("response");
          JSONArray docs = responseJSON.getJSONArray("docs");
          List<NYTArticle> articles = NYTArticle.fromJSONArray(docs);
          JSONObject meta = responseJSON.getJSONObject("meta");
          int hits = meta.getInt("hits");
          int offset = meta.getInt("offset");

          SearchResult result = new SearchResult(articles, hits, isOnLastPage(hits, offset, page));

          resultListener.onLoadArticlesSuccessful(result);
        } catch (JSONException e) {
          resultListener.onLoadArticlesFailed(CODE_PARSE_ERROR);
          e.printStackTrace();
        }
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        resultListener.onLoadArticlesFailed(CODE_NETWORK_ERROR);
      }
    });
  }

  private static boolean isOnLastPage(int hits, int offset, int page) throws JSONException {
    if (page >= MAX_PAGES) {
      return true;
    }

    return hits < offset + PAGE_LENGTH;
  }

  public static String getFullImageURL(String partialImageURL) {
    return NYT_BASE_URL + partialImageURL;
  }

  public interface NYTArticleListener {
    void onLoadArticlesSuccessful(SearchResult result);
    void onLoadArticlesFailed(int errorCode);
  }
}
