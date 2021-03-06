package com.example.james_wills.nytsearch.models;

import android.os.Parcelable;

import com.example.james_wills.nytsearch.utils.DateFormatUtils;
import com.example.james_wills.nytsearch.utils.NYTSearchBuilder;
import com.loopj.android.http.RequestParams;

import org.parceler.Parcel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by james_wills on 5/26/16.
 */
@Parcel
public class NYTSearchQueryParams implements Parcelable {
  public enum NewsDeskCategory {
    ARTS("Arts"),
    FASHION_AND_STYLE("Fashion & Style"),
    SPORTS("Sports");

    private String name;

    NewsDeskCategory(String name) {
      this.name = name;
    }
  }

  public enum SortOption {
    NEWEST,
    OLDEST;

    public String getString() {
      return this.toString().toLowerCase();
    }

    public static SortOption getSortOption(String s) {
      return SortOption.valueOf(s.toUpperCase());
    }

    public static String[] getValues() {
      String[] result = { NEWEST.toString().toLowerCase(), OLDEST.toString().toLowerCase() };
      return result;
    }
  }

  boolean shouldHighlight;
  String query;
  String beginDate;
  String endDate;
  SortOption sortOrder;
  Set<NewsDeskCategory> newsDeskCategories;

  public NYTSearchQueryParams() {
    query = null;
    beginDate = null;
    endDate = null;
    shouldHighlight = true;
    sortOrder = SortOption.NEWEST;
    newsDeskCategories = new HashSet<>();
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getBeginDate() {
    return beginDate;
  }

  public void setBeginDate(String beginDate) {
    this.beginDate = beginDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public SortOption getSortOrder() {
    return sortOrder;
  }

  public String getSortOrderValue() {
    return sortOrder.getString();
  }

  public void setSortOrder(SortOption sortOrder) {
    this.sortOrder = sortOrder;
  }

  public void setSortOrder(String sortOrderString) {
    this.sortOrder = SortOption.getSortOption(sortOrderString);
  }

  public void updateNewsDeskParam(NewsDeskCategory category, boolean enableFilter) {
    if (enableFilter) {
      newsDeskCategories.add(category);
    } else {
      newsDeskCategories.remove(category);
    }
  }

  public boolean hasNewsDeskParam(NewsDeskCategory category) {
    return newsDeskCategories.contains(category);
  }

  private String getNewsDeskParam() {
    String categories = "";
    for (NewsDeskCategory category : newsDeskCategories) {
      categories += String.format("\"%s\" ", category);
    }

    return String.format("news_desk:(%s)", categories);
  }

  public RequestParams getRequestParams(int page, String apiKey) throws ParseException {
    RequestParams params = new RequestParams();
    params.put("api-key", apiKey);
    params.put("page", page);

    if (query != null) {
      params.put("q", query);
    }

    if (beginDate != null) {
      String formattedDate = DateFormatUtils.convert(beginDate, DateFormatUtils.USER_FORMAT, DateFormatUtils.PARAM_FORMAT);
      params.put("begin_date", formattedDate);
    }

    if (endDate != null) {
      String formattedDate = DateFormatUtils.convert(endDate, DateFormatUtils.USER_FORMAT, DateFormatUtils.PARAM_FORMAT);
      params.put("end_date", formattedDate);
    }

    params.put("hl", shouldHighlight);
    params.put("sort", sortOrder);

    if (newsDeskCategories.size() > 0) {
      params.put("fq", getNewsDeskParam());
    }

    return params;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(android.os.Parcel dest, int flags) {
    dest.writeByte(this.shouldHighlight ? (byte) 1 : (byte) 0);
    dest.writeString(this.query);
    dest.writeString(this.beginDate);
    dest.writeString(this.endDate);
    dest.writeString(this.sortOrder.getString());
    List<Integer> categoryIndices = new ArrayList<>();
    for (NewsDeskCategory category : this.newsDeskCategories) {
      categoryIndices.add(category.ordinal());
    }
    dest.writeList(categoryIndices);
  }

  protected NYTSearchQueryParams(android.os.Parcel in) {
    this.shouldHighlight = in.readByte() != 0;
    this.query = in.readString();
    this.beginDate = in.readString();
    this.endDate = in.readString();
    this.sortOrder = SortOption.getSortOption(in.readString());
    List<Integer> categoryIndices = new ArrayList<>();
    in.readList(categoryIndices, null);

    for (Integer i : categoryIndices) {
      this.newsDeskCategories.add(NewsDeskCategory.values()[i]);
    }
  }

  public static final Creator<NYTSearchQueryParams> CREATOR = new Creator<NYTSearchQueryParams>() {
    @Override
    public NYTSearchQueryParams createFromParcel(android.os.Parcel source) {
      return new NYTSearchQueryParams(source);
    }

    @Override
    public NYTSearchQueryParams[] newArray(int size) {
      return new NYTSearchQueryParams[size];
    }
  };

  @Override
  public String toString() {
    return String.format("query: %s\n" +
                            "beginDate: %s\n" +
                            "endDate: %s\n" +
                            "sortOrder: %s\n" +
                            "showArts: %b\n" +
                            "showFashion: %b\n" +
                            "showSports: %b\n",
        query, beginDate, endDate, sortOrder,
        newsDeskCategories.contains(NewsDeskCategory.ARTS),
        newsDeskCategories.contains(NewsDeskCategory.FASHION_AND_STYLE),
        newsDeskCategories.contains(NewsDeskCategory.SPORTS));
  }
}
