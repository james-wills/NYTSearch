package com.example.james_wills.nytsearch.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * From http://blog.grafixartist.com/pinterest-masonry-layout-staggered-grid/
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
  private final int mSpace;
  public SpacesItemDecoration(int space) {
    this.mSpace = space;
  }
  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    outRect.left = mSpace;
    outRect.right = mSpace;
    outRect.bottom = mSpace;
    outRect.top = mSpace;
  }
}
