package com.findafun.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.costum.android.widget.LoadMoreListView;
import com.findafun.R;
import com.findafun.adapter.BookingPlanAdapter;
import com.findafun.bean.events.BookPlan;
import com.findafun.bean.events.BookPlanList;
import com.findafun.bean.events.Event;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.servicehelpers.EventServiceHelper;
import com.findafun.serviceinterfaces.IEventServiceListener;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nandha on 11-12-2016.
 */

public class BookingPlansActivity extends AppCompatActivity implements LoadMoreListView.OnLoadMoreListener, IEventServiceListener, AdapterView.OnItemClickListener {

    private static final String TAG = "BookingPlansActivity";
    LoadMoreListView loadMoreListView;
    View view;
    String eventId, eventName, eventVenue, eventDate;
    BookingPlanAdapter bookingPlanAdapter;
    EventServiceHelper eventServiceHelper;
    ArrayList<BookPlan> bookPlanArrayList;
    int pageNumber = 0, totalCount = 0;
    protected ProgressDialogHelper progressDialogHelper;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    private SearchView mSearchView = null;
    TextView txtEventName, txtEvnetVenue, txtEventDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_plans);
        //getSupportActionBar().hide();
        loadMoreListView = (LoadMoreListView) findViewById(R.id.listView_plans);
        txtEventName = (TextView) findViewById(R.id.event_name);
        txtEvnetVenue = (TextView) findViewById(R.id.event_venue);
        txtEventDate = (TextView) findViewById(R.id.event_when);
        loadMoreListView.setOnLoadMoreListener(this);
        loadMoreListView.setOnItemClickListener(this);
        bookPlanArrayList = new ArrayList<>();
        eventServiceHelper = new EventServiceHelper(this);
        eventServiceHelper.setEventServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");
        eventVenue = getIntent().getStringExtra("eventVenue");
        eventDate = getIntent().getStringExtra("eventStartEndDate");

        txtEventName.setText(eventName);
        txtEvnetVenue.setText(eventVenue);
        txtEventDate.setText("When : " + eventDate);

        findViewById(R.id.back_res).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //  if (PreferenceStorage.getFilterApply(this)) {
        //    PreferenceStorage.IsFilterApply(this, false);
        callGetFilterService();
        //}
    }

    public void callGetFilterService() {
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/
        if (bookPlanArrayList != null)
            bookPlanArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            //    eventServiceHelper.makeRawRequest(FindAFunConstants.GET_ADVANCE_SINGLE_SEARCH);
            new BookingPlansActivity.HttpAsyncTask().execute("");
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.no_connectivity));
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_BOOKING_PLAN, eventId));
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void result) {
            progressDialogHelper.cancelProgressDialog();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
        Log.d(TAG, "onEvent list item clicked" + i);
        BookPlan bookPlan = null;
        if ((bookingPlanAdapter != null) && (bookingPlanAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = bookingPlanAdapter.getActualEventPos(i);
            Log.d(TAG, "actual index" + actualindex);
            bookPlan = bookPlanArrayList.get(actualindex);
        } else {
            bookPlan = bookPlanArrayList.get(i);
        }

        Intent intent = new Intent(getApplicationContext(), BookingPlanSeatSelectionActivity.class);
        intent.putExtra("planObj", bookPlan);
        intent.putExtra("eventName", eventName);
        intent.putExtra("eventVenue", eventVenue);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
//        // getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }

    @Override
    public void onEventResponse(final JSONObject response) {
        Log.d("ajazFilterresponse : ", response.toString());

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogHelper.hideProgressDialog();
                loadMoreListView.onLoadMoreComplete();

                Gson gson = new Gson();
                BookPlanList planList = gson.fromJson(response.toString(), BookPlanList.class);
                if (planList.getPlans() != null && planList.getPlans().size() > 0) {
                    totalCount = planList.getCount();
                    isLoadingForFirstTime = false;
                    updateListAdapter(planList.getPlans());
                }
            }
        });
    }

    @Override
    public void onEventError(final String error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogHelper.hideProgressDialog();
                loadMoreListView.onLoadMoreComplete();
                AlertDialogHelper.showSimpleAlertDialog(BookingPlansActivity.this, error);
            }
        });
    }

    @Override
    public void onLoadMore() {

    }

    protected void updateListAdapter(ArrayList<BookPlan> bookPlanArrayList) {
        this.bookPlanArrayList.addAll(bookPlanArrayList);
        if (bookingPlanAdapter == null) {
            bookingPlanAdapter = new BookingPlanAdapter(this, this.bookPlanArrayList);
            loadMoreListView.setAdapter(bookingPlanAdapter);
        } else {
            bookingPlanAdapter.notifyDataSetChanged();
        }
    }

    public void searchForEvent(String eventname) {
        Log.d(TAG, "searchevent called");
        if (bookingPlanAdapter != null) {
            bookingPlanAdapter.startSearch(eventname);
            bookingPlanAdapter.notifyDataSetChanged();
            //loadMoreListView.invalidateViews();
        }
    }

    public void exitSearch() {
        Log.d(TAG, "exit event called");
        if (bookingPlanAdapter != null) {
            bookingPlanAdapter.exitSearch();
            bookingPlanAdapter.notifyDataSetChanged();
        }

    }
}
