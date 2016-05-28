package com.example.james_wills.nytsearch.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.james_wills.nytsearch.activities.SearchActivity;
import com.example.james_wills.nytsearch.R;
import com.example.james_wills.nytsearch.models.NYTSearchQueryParams;
import com.example.james_wills.nytsearch.utils.DateFormatUtils;

import java.text.ParseException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

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

  private DatePickerDialog beginDatePicker;
  private DatePickerDialog endDatePicker;

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

    if (params.getBeginDate() != null) {
      tvBeginDateLink.setText(params.getBeginDate());
    }
    if (params.getEndDate() != null) {
      tvEndDateLink.setText(params.getBeginDate());
    }

    cbArts.setChecked(
        params.hasNewsDeskParam(NYTSearchQueryParams.NewsDeskCategory.ARTS));
    cbFashion.setChecked(
        params.hasNewsDeskParam(NYTSearchQueryParams.NewsDeskCategory.FASHION_AND_STYLE));
    cbSports.setChecked(
        params.hasNewsDeskParam(NYTSearchQueryParams.NewsDeskCategory.SPORTS));

    beginDatePicker = createDatePicker(tvBeginDateLink, params.getBeginDate());
    endDatePicker = createDatePicker(tvEndDateLink, params.getEndDate());
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
    final String[] items = { getString(R.string.newest), getString(R.string.oldest) };
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle(R.string.sort_by);
    builder.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        params.setSortOrder(items[which]);
        tvSortByLink.setText(items[which]);
      }
    });
    builder.show();
  }

  @OnClick(R.id.tvBeginDateLink)
  public void onBeginDateClicked() {
    beginDatePicker.show();
  }

  @OnClick(R.id.tvEndDateLink)
  public void onEndDateClicked() {
    endDatePicker.show();
  }

  private DatePickerDialog createDatePicker(final TextView outputView, String startDate) {

    Calendar calendar;
    if (startDate != null) {
      try {
        calendar = DateFormatUtils.getCalendar(startDate, DateFormatUtils.USER_FORMAT);
      } catch (ParseException e) {
        calendar = Calendar.getInstance();
        e.printStackTrace();
      }
    } else {
      calendar = Calendar.getInstance();
    }

    return new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        outputView.setText(DateFormatUtils.USER_FORMAT.format(newDate.getTime()));
      }
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
  }
}
