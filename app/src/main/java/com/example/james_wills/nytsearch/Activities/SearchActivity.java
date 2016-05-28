package com.example.james_wills.nytsearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.james_wills.nytsearch.adapters.ArticleAdapter;
import com.example.james_wills.nytsearch.fragments.FiltersDialogFragment;
import com.example.james_wills.nytsearch.R;
import com.example.james_wills.nytsearch.models.NYTArticle;
import com.example.james_wills.nytsearch.models.NYTSearchQueryParams;
import com.example.james_wills.nytsearch.utils.EndlessRecyclerViewScrollListener;
import com.example.james_wills.nytsearch.utils.NYTSearchBuilder;
import com.example.james_wills.nytsearch.utils.SpacesItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements FiltersDialogFragment.FilterDialogListener {
  @BindView(R.id.rvResults) RecyclerView rvResults;
  @BindView(R.id.searchToolbar) Toolbar toolbar;

  ArticleAdapter adapter;
  NYTSearchQueryParams queryParams = new NYTSearchQueryParams();
  boolean lastPage = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_search);

    ButterKnife.bind(this);
    setSupportActionBar(toolbar);

    initViews();
  }

  private void initViews() {
    adapter = new ArticleAdapter(new ArrayList<NYTArticle>());
    final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

    rvResults.setAdapter(adapter);
    rvResults.setLayoutManager(layoutManager);
    rvResults.addItemDecoration(new SpacesItemDecoration(10));
    rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
      @Override
      public void onLoadMore(int page, int totalItemsCount) {
        if (!lastPage) {
          loadArticles(page);
        }
      }
    });

    adapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(View itemView, int position) {
        Intent i = new Intent(SearchActivity.this, WebActivity.class);
        i.putExtra("url", adapter.getItem(position).getUrl());
        startActivity(i);
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_search, menu);

    MenuItem searchItem = menu.findItem(R.id.action_search);
    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        queryParams.setQuery(query);
        queryUpdated();
        searchView.clearFocus();
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_filters)
      showFiltersDialog();

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onFinishDialog(NYTSearchQueryParams result) {
    queryParams = result;
    queryUpdated();
  }

  private void showFiltersDialog() {
    FragmentManager fm = getSupportFragmentManager();
    FiltersDialogFragment filtersFragment = FiltersDialogFragment.newInstance(queryParams);
    filtersFragment.show(fm, "fragment_edit_name");
  }

  private void loadArticles(final int page) {
    if (queryParams.getQuery().length() == 0) {
      return;
    }

    NYTSearchBuilder.executeAndGetResults(this, queryParams, page, new NYTSearchBuilder.NYTArticleListener() {
      @Override
      public void onLoadArticlesSuccessful(NYTSearchBuilder.SearchResult result) {
        if (result.articles.size() == 0)
          showSnackbarMessage(getString(R.string.no_articles_found));

        if (page == 0) {
          adapter.replaceItems(result.articles);
          showSnackbarMessage(String.format(getString(R.string.found_x_results), result.hits));
        } else {
          adapter.addItems(result.articles);
        }

        if (result.isLastPage && !lastPage) {
          showSnackbarMessage(getString(R.string.no_more_results));
        }

        lastPage = result.isLastPage;
      }

      @Override
      public void onLoadArticlesFailed(int errorCode) {
        switch(errorCode) {
          case NYTSearchBuilder.CODE_PARSE_ERROR:
            showSnackbarMessage(getString(R.string.parse_error));
            break;
          case NYTSearchBuilder.CODE_NETWORK_ERROR:
            showSnackbarMessage(getString(R.string.network_error));
            break;
          case NYTSearchBuilder.CODE_GENERAL_ERROR:
            showSnackbarMessage(getString(R.string.general_request_error));
            break;
          case NYTSearchBuilder.CODE_DATE_PARSE_ERROR:
            showSnackbarMessage(getString(R.string.date_parse_error));
            break;
          default:
            showSnackbarMessage(getString(R.string.unknown_error));
        }
      }
    });
  }

  private void queryUpdated() {
    adapter.clearArticles();
    loadArticles(0);
  }

  private void showSnackbarMessage(String message) {
    Snackbar.make(rvResults, message, Snackbar.LENGTH_LONG).show();
  }
}
