package com.example.james_wills.nytsearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.james_wills.nytsearch.R;
import com.example.james_wills.nytsearch.models.NYTArticle;
import com.example.james_wills.nytsearch.utils.NYTSearchBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by james_wills on 5/26/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

  private List<NYTArticle> articles;

  public ArticleAdapter(List<NYTArticle> articles) {
    this.articles = articles;
  }

  @Override
  public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    // Inflate the custom layout
    View contactView = inflater.inflate(R.layout.item_article, parent, false);

    // Return a new holder instance
    ViewHolder viewHolder = new ViewHolder(contactView);
    return viewHolder;
  }

  // Involves populating data into the item through holder
  @Override
  public void onBindViewHolder(ArticleAdapter.ViewHolder viewHolder, int position) {
    // Get the data model based on position
    NYTArticle article = articles.get(position);

    // Set item views based on the data model
    TextView textView = viewHolder.tvHeadline;
    textView.setText(article.getHeadline());

    Context c = viewHolder.ivImage.getContext();

    ViewGroup.LayoutParams params = viewHolder.ivImage.getLayoutParams();

    if (article.getImageURL() == null) {
      viewHolder.ivImage.setImageResource(0);
      return;
    }

    viewHolder.ivImage.setLayoutParams(params);
    Picasso.with(c)
        .load(NYTSearchBuilder.getFullImageURL(article.getImageURL()))
        .tag(c)
        .resize(article.getImageWidth(), article.getImageHeight())
        .placeholder(R.drawable.placeholder)
        .into(viewHolder.ivImage);
  }

  // Return the total count of items
  @Override
  public int getItemCount() {
    return articles.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView tvHeadline;
    public ImageView ivImage;

    public ViewHolder(View itemView) {
      super(itemView);

      tvHeadline = (TextView) itemView.findViewById(R.id.tvItemHeadline);
      ivImage = (ImageView) itemView.findViewById(R.id.ivArticleImage);
    }
  }

  public void addItems(List<NYTArticle> items) {
    int cursor = getItemCount();
    articles.addAll(items);

    notifyItemRangeInserted(cursor, items.size() - 1);
  }

  public void replaceItems(List<NYTArticle> items) {
    articles.clear();
    articles.addAll(items);
    notifyDataSetChanged();
  }

  public void clearArticles() {
    articles.clear();
    notifyDataSetChanged();
  }
}
