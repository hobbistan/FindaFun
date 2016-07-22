package com.findafun.fragment;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.findafun.R;
import com.findafun.activity.EventDetailActivity;
import com.findafun.adapter.EventsListAdapter;
import com.findafun.adapter.StaticEventListAdapter;
import com.findafun.bean.events.Event;
import com.findafun.bean.events.EventList;
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
 * A simple {@link Fragment} subclass.
 */
public class LandingPagerFragment extends Fragment implements  IEventServiceListener, AdapterView.OnItemClickListener {
    private static final String TAG = LandingPagerFragment.class.getName();


    private boolean isFirstRun = true;
    private SharedPreferences TransPrefs;


    protected ListView loadMoreListView;
    protected View view;
    protected EventsListAdapter eventsListAdapter;
    protected StaticEventListAdapter staticEventListAdapter;
    protected EventServiceHelper eventServiceHelper;
    //protected StaticEventServiceHelper staticEventServiceHelper;
    protected ArrayList<Event> eventsArrayList;
    int pageNumber = 0, totalCount = 0;
    protected ProgressDialogHelper progressDialogHelper;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    private TextView mNoEventsFound = null;


    public static LandingPagerFragment newInstance(int position) {
        LandingPagerFragment frag = new LandingPagerFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_landing_pager, container, false);

        initializeViews();
        initializeEventHelpers();

        return view;
    }



    protected void initializeViews() {
        Log.d(TAG, "initialize pull to refresh view");
        loadMoreListView = (ListView) view.findViewById(R.id.listView_events);
        mNoEventsFound = (TextView) view.findViewById(R.id.no_home_events);
        if (mNoEventsFound != null)
            mNoEventsFound.setVisibility(View.GONE);
       // loadMoreListView.setOnLoadMoreListener(this);
        loadMoreListView.setOnItemClickListener(this);
        eventsArrayList = new ArrayList<>();
    }

    protected void initializeEventHelpers() {
        eventServiceHelper = new EventServiceHelper(getActivity());
        eventServiceHelper.setEventServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getActivity());

    }

    public void callGetEventService(int position) {
        Log.d(TAG, "fetch event list" + position);
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/

        if (isLoadingForFirstTime) {
            Log.d(TAG, "Loading for the first time");
            if (eventsArrayList != null)
                eventsArrayList.clear();

            if (CommonUtils.isNetworkAvailable(getActivity())) {
                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                makeEventListServiceCall(1);
            } else {
                AlertDialogHelper.showSimpleAlertDialog(getActivity(), getString(R.string.no_connectivity));
            }
        } else {
            Log.d(TAG, "Do nothing");
        }
    }

    public void callGetFilterService() {
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/

        if (eventsArrayList != null)
            eventsArrayList.clear();

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            //    eventServiceHelper.makeRawRequest(FindAFunConstants.GET_ADVANCE_SINGLE_SEARCH);
            new HttpAsyncTask().execute("");
        } else {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), getString(R.string.no_connectivity));
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            eventServiceHelper.makeRawRequest(FindAFunConstants.GET_ADVANCE_SINGLE_SEARCH);
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void result) {
            progressDialogHelper.cancelProgressDialog();
        }
    }

    protected void updateListAdapter(ArrayList<Event> eventsArrayList) {
        this.eventsArrayList.addAll(eventsArrayList);
        if (mNoEventsFound != null)
            mNoEventsFound.setVisibility(View.GONE);

        if (eventsListAdapter == null) {
            eventsListAdapter = new EventsListAdapter(getActivity(), this.eventsArrayList);
            loadMoreListView.setAdapter(eventsListAdapter);
        } else {
            eventsListAdapter.notifyDataSetChanged();
        }
    }

    public void searchForEvent(String eventname) {
        Log.d(TAG, "searchevent called");
        if (eventsListAdapter != null) {
            eventsListAdapter.startSearch(eventname);
            eventsListAdapter.notifyDataSetChanged();
                        //loadMoreListView.invalidateViews();
        }

    }

    public void exitSearch() {
        Log.d(TAG, "exit event called");
        if (eventsListAdapter != null) {
            eventsListAdapter.exitSearch();
            eventsListAdapter.notifyDataSetChanged();
        }

        if(staticEventListAdapter!=null){
            staticEventListAdapter.exitSearch();
            staticEventListAdapter.notifyDataSetChanged();
        }

    }

   /* @Override
    public void onLoadMore() {

        int pageNumber = -1;
        int size = eventsArrayList.size();
        Log.d(TAG, "onLoad more called" + size + "totalcount" + totalCount);
        if (size < totalCount) {
            pageNumber = (size / 10) + 1;
        }
        Log.d(TAG, "Page number" + pageNumber);
        makeEventListServiceCall(pageNumber);
    }*/

    public void makeEventListServiceCall(int pageNumber) {
        if ((pageNumber != -1) && (pageNumber >= 0 && pageNumber <= 6)) {
            switch (getArguments().getInt("position")) {
                case 0:
                    Log.d(TAG, "fetch favourites");
                    eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_FAVOURITES, Integer.parseInt(PreferenceStorage.getUserId(getActivity())), pageNumber, PreferenceStorage.getUserCity(getActivity())));
//                    eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_FAVOURITES, pageNumber));

                    TransPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    isFirstRun = TransPrefs.getBoolean("isFirstRun", true);

                   /* if(isFirstRun){*/
                     //   getTransparentFavour();
                /*    }*/

                    isFirstRun = false;

                    TransPrefs.edit().putBoolean("isFirstRun", isFirstRun).commit();








                    break;
                case 1:
                    Log.d(TAG, "fetch ALL events");
                    eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_ALL_URL, pageNumber, PreferenceStorage.getUserCity(getActivity())));
                    break;
                case 2:
                    Log.d(TAG, "fetch Hotspot events");
//                    eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_STATIC_EVENTS, Integer.parseInt(PreferenceStorage.getUserId(getActivity())), pageNumber, PreferenceStorage.getUserCity(getActivity())));
//                    //eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_NEARBY_URL, pageNumber));
//                    break;

             /*   case 3:
                    Log.d(TAG, "fetch Filter events");
                    eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_ADVANCE_SINGLE_SEARCH, PreferenceStorage.getFilterSingleDate(getActivity())));
                    break;*/
            }
        } else {
            Log.d(TAG, "ignoring this page");
          //  loadMoreListView.onLoadMoreComplete();
            progressDialogHelper.hideProgressDialog();
        }
    }

    private void getTransparentFavour() {

        final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Translucent);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.transparent_favourite);
        dialog.show();
        final ImageView navPointIcon = (ImageView)dialog.findViewById(R.id.navi_pnt_icon);
        final ImageView srchPointIcon = (ImageView)dialog.findViewById(R.id.adv_screen_pt);
        final ImageView gamiPointIcon = (ImageView)dialog.findViewById(R.id.gami_pt);
        final ImageView addEvntPointIcon = (ImageView)dialog.findViewById(R.id.add_event_pt);
        final ImageView favorPointIcon = (ImageView)dialog.findViewById(R.id.favorite_pnt_icon);
        final ImageView popularPointIcon = (ImageView)dialog.findViewById(R.id.popular_pnt_icon);
        final ImageView hotspotPointIcon = (ImageView)dialog.findViewById(R.id.hotspot_pnt_icon);
        final ImageView listViewPointIcon = (ImageView)dialog.findViewById(R.id.eventlst_pnt_icon);
        final ImageView mapViewPointIcon  = (ImageView)dialog.findViewById(R.id.map_pnt_icon);
        final ImageView nearByPointIcon = (ImageView)dialog.findViewById(R.id.nearby_pnt_icon);


        final TextView txtNavigate = (TextView) dialog.findViewById(R.id.trans_navigation_drawer);
        final TextView txtSearch = (TextView) dialog.findViewById(R.id.trans_search);
        final TextView txtGami = (TextView) dialog.findViewById(R.id.trans_gami);
        final TextView txtAddEvnt = (TextView) dialog.findViewById(R.id.trans_add_event);


        final TextView txtFavorite = (TextView) dialog.findViewById(R.id.trans_favorite);
        final TextView txtPopular = (TextView) dialog.findViewById(R.id.trans_popular);
        final TextView txtHotspot = (TextView) dialog.findViewById(R.id.trans_hotspot);
        final TextView txtlistView = (TextView) dialog.findViewById(R.id.trans_evntlist);
        final TextView txtMap = (TextView) dialog.findViewById(R.id.trans_map);
        final TextView txtNearby = (TextView) dialog.findViewById(R.id.trans_nearby_events);





        navPointIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navPointIcon.setVisibility(View.GONE);
                favorPointIcon.setVisibility(View.VISIBLE);
                popularPointIcon.setVisibility(View.GONE);
                hotspotPointIcon.setVisibility(View.GONE);
                listViewPointIcon.setVisibility(View.GONE);
                nearByPointIcon.setVisibility(View.GONE);
                mapViewPointIcon.setVisibility(View.GONE);
                srchPointIcon.setVisibility(View.GONE);
                gamiPointIcon.setVisibility(View.GONE);
                addEvntPointIcon.setVisibility(View.GONE);
                txtNavigate.setVisibility(View.GONE);
                txtFavorite.setVisibility(View.VISIBLE);
                txtPopular.setVisibility(View.GONE);
                txtHotspot.setVisibility(View.GONE);
                txtlistView.setVisibility(View.GONE);
                txtMap.setVisibility(View.GONE);
                txtNearby.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
                txtGami.setVisibility(View.GONE);
                txtAddEvnt.setVisibility(View.GONE);


            }
        });




        favorPointIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navPointIcon.setVisibility(View.GONE);
                favorPointIcon.setVisibility(View.GONE);
                popularPointIcon.setVisibility(View.VISIBLE);
                hotspotPointIcon.setVisibility(View.GONE);
                listViewPointIcon.setVisibility(View.GONE);
                nearByPointIcon.setVisibility(View.GONE);
                mapViewPointIcon.setVisibility(View.GONE);
                srchPointIcon.setVisibility(View.GONE);
                gamiPointIcon.setVisibility(View.GONE);
                addEvntPointIcon.setVisibility(View.GONE);

                txtNavigate.setVisibility(View.GONE);
                txtFavorite.setVisibility(View.GONE);
                txtPopular.setVisibility(View.VISIBLE);
                txtHotspot.setVisibility(View.GONE);
                txtlistView.setVisibility(View.GONE);
                txtMap.setVisibility(View.GONE);
                txtNearby.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
                txtGami.setVisibility(View.GONE);
                txtAddEvnt.setVisibility(View.GONE);

            }
        });


        popularPointIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navPointIcon.setVisibility(View.GONE);
                favorPointIcon.setVisibility(View.GONE);
                popularPointIcon.setVisibility(View.GONE);
                hotspotPointIcon.setVisibility(View.VISIBLE);
                listViewPointIcon.setVisibility(View.GONE);
                nearByPointIcon.setVisibility(View.GONE);
                mapViewPointIcon.setVisibility(View.GONE);
                srchPointIcon.setVisibility(View.GONE);
                gamiPointIcon.setVisibility(View.GONE);
                addEvntPointIcon.setVisibility(View.GONE);

                txtNavigate.setVisibility(View.GONE);
                txtFavorite.setVisibility(View.GONE);
                txtPopular.setVisibility(View.GONE);
                txtHotspot.setVisibility(View.VISIBLE);
                txtlistView.setVisibility(View.GONE);
                txtMap.setVisibility(View.GONE);
                txtNearby.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
                txtGami.setVisibility(View.GONE);
                txtAddEvnt.setVisibility(View.GONE);

            }
        });



        hotspotPointIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navPointIcon.setVisibility(View.GONE);
                favorPointIcon.setVisibility(View.GONE);
                popularPointIcon.setVisibility(View.GONE);
                hotspotPointIcon.setVisibility(View.GONE);
                listViewPointIcon.setVisibility(View.VISIBLE);
                nearByPointIcon.setVisibility(View.GONE);
                mapViewPointIcon.setVisibility(View.GONE);
                srchPointIcon.setVisibility(View.GONE);
                gamiPointIcon.setVisibility(View.GONE);
                addEvntPointIcon.setVisibility(View.GONE);

                txtNavigate.setVisibility(View.GONE);
                txtFavorite.setVisibility(View.GONE);
                txtPopular.setVisibility(View.GONE);
                txtHotspot.setVisibility(View.GONE);
                txtlistView.setVisibility(View.VISIBLE);
                txtMap.setVisibility(View.GONE);
                txtNearby.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
                txtGami.setVisibility(View.GONE);
                txtAddEvnt.setVisibility(View.GONE);

            }
        });


        listViewPointIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navPointIcon.setVisibility(View.GONE);
                favorPointIcon.setVisibility(View.GONE);
                popularPointIcon.setVisibility(View.GONE);
                hotspotPointIcon.setVisibility(View.GONE);
                listViewPointIcon.setVisibility(View.GONE);
                mapViewPointIcon.setVisibility(View.VISIBLE);
                nearByPointIcon.setVisibility(View.GONE);

                srchPointIcon.setVisibility(View.GONE);
                gamiPointIcon.setVisibility(View.GONE);
                addEvntPointIcon.setVisibility(View.GONE);

                txtNavigate.setVisibility(View.GONE);
                txtFavorite.setVisibility(View.GONE);
                txtPopular.setVisibility(View.GONE);
                txtHotspot.setVisibility(View.GONE);
                txtlistView.setVisibility(View.GONE);
                txtMap.setVisibility(View.VISIBLE);
                txtNearby.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
                txtGami.setVisibility(View.GONE);
                txtAddEvnt.setVisibility(View.GONE);

            }
        });


        mapViewPointIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navPointIcon.setVisibility(View.GONE);
                favorPointIcon.setVisibility(View.GONE);
                popularPointIcon.setVisibility(View.GONE);
                hotspotPointIcon.setVisibility(View.GONE);
                listViewPointIcon.setVisibility(View.GONE);
                mapViewPointIcon.setVisibility(View.GONE);
                nearByPointIcon.setVisibility(View.VISIBLE);
                srchPointIcon.setVisibility(View.GONE);
                gamiPointIcon.setVisibility(View.GONE);
                addEvntPointIcon.setVisibility(View.GONE);

                txtNavigate.setVisibility(View.GONE);
                txtFavorite.setVisibility(View.GONE);
                txtPopular.setVisibility(View.GONE);
                txtHotspot.setVisibility(View.GONE);
                txtlistView.setVisibility(View.GONE);
                txtMap.setVisibility(View.GONE);
                txtNearby.setVisibility(View.VISIBLE);
                txtSearch.setVisibility(View.GONE);
                txtGami.setVisibility(View.GONE);
                txtAddEvnt.setVisibility(View.GONE);

            }
        });



        nearByPointIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navPointIcon.setVisibility(View.GONE);
                favorPointIcon.setVisibility(View.GONE);
                popularPointIcon.setVisibility(View.GONE);
                hotspotPointIcon.setVisibility(View.GONE);
                listViewPointIcon.setVisibility(View.GONE);
                nearByPointIcon.setVisibility(View.GONE);
                mapViewPointIcon.setVisibility(View.GONE);
                srchPointIcon.setVisibility(View.VISIBLE);
                gamiPointIcon.setVisibility(View.GONE);
                addEvntPointIcon.setVisibility(View.GONE);

                txtNavigate.setVisibility(View.GONE);
                txtFavorite.setVisibility(View.GONE);
                txtPopular.setVisibility(View.GONE);
                txtHotspot.setVisibility(View.GONE);
                txtlistView.setVisibility(View.GONE);
                txtMap.setVisibility(View.GONE);
                txtNearby.setVisibility(View.GONE);
                txtSearch.setVisibility(View.VISIBLE);
                txtGami.setVisibility(View.GONE);
                txtAddEvnt.setVisibility(View.GONE);

            }
        });

        srchPointIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navPointIcon.setVisibility(View.GONE);
                favorPointIcon.setVisibility(View.GONE);
                popularPointIcon.setVisibility(View.GONE);
                hotspotPointIcon.setVisibility(View.GONE);
                listViewPointIcon.setVisibility(View.GONE);
                nearByPointIcon.setVisibility(View.GONE);
                mapViewPointIcon.setVisibility(View.GONE);
                srchPointIcon.setVisibility(View.GONE);
                gamiPointIcon.setVisibility(View.GONE);
                addEvntPointIcon.setVisibility(View.VISIBLE);

                txtNavigate.setVisibility(View.GONE);
                txtFavorite.setVisibility(View.GONE);
                txtPopular.setVisibility(View.GONE);
                txtHotspot.setVisibility(View.GONE);
                txtlistView.setVisibility(View.GONE);
                txtMap.setVisibility(View.GONE);
                txtNearby.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
                txtGami.setVisibility(View.GONE);
                txtAddEvnt.setVisibility(View.VISIBLE);
            }
        });


        addEvntPointIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navPointIcon.setVisibility(View.GONE);
                favorPointIcon.setVisibility(View.GONE);
                popularPointIcon.setVisibility(View.GONE);
                hotspotPointIcon.setVisibility(View.GONE);
                listViewPointIcon.setVisibility(View.GONE);
                nearByPointIcon.setVisibility(View.GONE);
                mapViewPointIcon.setVisibility(View.GONE);
                srchPointIcon.setVisibility(View.GONE);
                gamiPointIcon.setVisibility(View.GONE);
                addEvntPointIcon.setVisibility(View.VISIBLE);

                txtNavigate.setVisibility(View.GONE);
                txtFavorite.setVisibility(View.GONE);
                txtPopular.setVisibility(View.GONE);
                txtHotspot.setVisibility(View.GONE);
                txtlistView.setVisibility(View.GONE);
                txtMap.setVisibility(View.GONE);
                txtNearby.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
                txtGami.setVisibility(View.GONE);
                txtAddEvnt.setVisibility(View.VISIBLE);

                dialog.dismiss();
            }
        });



        gamiPointIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navPointIcon.setVisibility(View.GONE);
                favorPointIcon.setVisibility(View.GONE);
                popularPointIcon.setVisibility(View.GONE);
                hotspotPointIcon.setVisibility(View.GONE);
                listViewPointIcon.setVisibility(View.GONE);
                nearByPointIcon.setVisibility(View.GONE);
                mapViewPointIcon.setVisibility(View.GONE);
                srchPointIcon.setVisibility(View.GONE);
                gamiPointIcon.setVisibility(View.VISIBLE);
                addEvntPointIcon.setVisibility(View.GONE);

                txtNavigate.setVisibility(View.GONE);
                txtFavorite.setVisibility(View.GONE);
                txtPopular.setVisibility(View.GONE);
                txtHotspot.setVisibility(View.GONE);
                txtlistView.setVisibility(View.GONE);
                txtMap.setVisibility(View.GONE);
                txtNearby.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
                txtGami.setVisibility(View.VISIBLE);
                txtAddEvnt.setVisibility(View.GONE);
            }
        });






    }

    @Override
    public void onEventResponse(final JSONObject response) {

        Log.d("ajazFilterresponse : ", response.toString());

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogHelper.hideProgressDialog();
              //  loadMoreListView.onLoadMoreComplete();

                Gson gson = new Gson();
                EventList eventsList = gson.fromJson(response.toString(), EventList.class);
                if (eventsList.getEvents() != null && eventsList.getEvents().size() > 0) {
                    if (mNoEventsFound != null)
                        mNoEventsFound.setVisibility(View.GONE);
                    totalCount = eventsList.getCount();
                    isLoadingForFirstTime = false;
                    updateListAdapter(eventsList.getEvents());
                } else {
                    if (mNoEventsFound != null)
                        mNoEventsFound.setVisibility(View.VISIBLE);
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
               // loadMoreListView.onLoadMoreComplete();
                AlertDialogHelper.showSimpleAlertDialog(getActivity(), error);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onEvent list item clicked" + i);
        Event event = null;
        if ((eventsListAdapter != null) && (eventsListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = eventsListAdapter.getActualEventPos(i);
            Log.d(TAG, "actual index" + actualindex);
            event = eventsArrayList.get(actualindex);
        } else {
            event = eventsArrayList.get(i);
        }

        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
        intent.putExtra("eventObj", event);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
//        // getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }

    public void onWindowFocusChanged() {

    }
}
