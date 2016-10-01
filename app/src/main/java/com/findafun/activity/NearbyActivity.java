package com.findafun.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.costum.android.widget.LoadMoreListView;
import com.findafun.R;
import com.findafun.adapter.StaticEventListAdapter;
import com.findafun.bean.events.Event;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.servicehelpers.EventServiceHelper;
import com.findafun.serviceinterfaces.IEventServiceListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Cube Reach 06 on 30-09-2016.
 */

public class NearbyActivity extends AppCompatActivity implements IEventServiceListener, LoadMoreListView.OnLoadMoreListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Spinner spinnearby;
    private double currentLatitude, currentLongitude;
    private static final String TAG = NearbyActivity.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    protected StaticEventListAdapter staticEventsListAdapter;
    protected EventServiceHelper eventServiceHelper;
    protected ArrayList<Event> eventsArrayList;
    protected ProgressDialogHelper progressDialogHelper;
    Handler mHandler = new Handler();
    private TextView mNoEventsFound = null;
    protected boolean isLoadingForFirstTime = true;
    protected LoadMoreListView loadMoreListView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.activity_nearby_event);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("NEARBY");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iniView();
    }

    private void iniView() {
        //getSupportActionBar().hide();

        spinnearby = (Spinner) findViewById(R.id.nearbyspinner);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.nearby_distance));
        spinnearby.setAdapter(dataAdapter2);
        /*int index = PreferenceStorage.getFilterEventTypeIndex(this);
        if((index >=0) && index < (getResources().getStringArray(R.array.nearby_distance).length)){
            spinnearby.setSelection(index);
        }*/

        spinnearby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                //PreferenceStorage.saveFilterEventTypeSelection(getApplicationContext(), position);
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_directions) {
//            Intent intent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + "&daddr=" + event.getEventLatitude() + "," + event.getEventLongitude()));
//            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            Log.d(TAG, "home up button selected");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("Google connection", "" + connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private void getLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();
        } else {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void checkPermissions() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onEventResponse(JSONObject response) {

    }

    @Override
    public void onEventError(String error) {

    }
}
