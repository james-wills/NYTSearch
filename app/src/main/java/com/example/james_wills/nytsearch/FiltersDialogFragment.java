package com.example.james_wills.nytsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.james_wills.nytsearch.models.NYTSearchQueryParams;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by james_wills on 5/26/16.
 */
public class FiltersDialogFragment extends DialogFragment {
  public interface FilterDialogListener {
    void onFinishDialog(NYTSearchQueryParams params);
  }

  @BindView(R.id.tvBeginDateLink) TextView tvBeginDateLink;
  @BindView(R.id.tvEndDateLink) TextView tvEndDateLink;
  @BindView(R.id.tvSortByLink) TextView tvSortByLink;

  @BindView(R.id.cbArts) CheckBox cbArts;
  @BindView(R.id.cbFashion) CheckBox cbFashion;
  @BindView(R.id.cbSports) CheckBox cbSports;

  private NYTSearchQueryParams params;

  public FiltersDialogFragment() {

  }

  public static FiltersDialogFragment newInstance(NYTSearchQueryParams queryParams) {
    FiltersDialogFragment frag = new FiltersDialogFragment();
    Bundle args = new Bundle();
    args.putParcelable("query", queryParams);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_filters, container);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    params = getArguments().getParcelable("query");

    tvSortByLink.setText(params.getSortOrder());

    cbArts.setChecked(
        params.hasNewsDeskParam(NYTSearchQueryParams.NewsDeskCategory.ARTS));
    cbFashion.setChecked(
        params.hasNewsDeskParam(NYTSearchQueryParams.NewsDeskCategory.FASHION_AND_STYLE));
    cbSports.setChecked(
        params.hasNewsDeskParam(NYTSearchQueryParams.NewsDeskCategory.SPORTS));
  }

  @OnCheckedChanged(R.id.cbArts)
  public void onArtsChecked(boolean checked) {
    params.updateNewsDeskParam(NYTSearchQueryParams.NewsDeskCategory.ARTS, checked);
  }

  @OnCheckedChanged(R.id.cbFashion)
  public void onFashionChecked(boolean checked) {
    params.updateNewsDeskParam(NYTSearchQueryParams.NewsDeskCategory.FASHION_AND_STYLE, checked);
  }

  @OnCheckedChanged(R.id.cbSports)
  public void onSportsChecked(boolean checked) {
    params.updateNewsDeskParam(NYTSearchQueryParams.NewsDeskCategory.SPORTS, checked);
  }

  @OnClick(R.id.btnFilterSave)
  public void onSaveClicked() {
    SearchActivity parent = (SearchActivity) getActivity();
    parent.onFinishDialog(params);
    dismiss();
  }

  @OnClick(R.id.btnFilterCancel)
  public void onCancelClicked() {
    dismiss();
  }

  @OnClick(R.id.tvSortByLink)
  public void onSortByClicked() {

  }
}
