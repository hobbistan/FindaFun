package com.findafun.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;


import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.adapter.CitySpinnerAdapter;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.SimpleGestureFilter;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.SignUpServiceHelper;
import com.findafun.serviceinterfaces.IServiceListener;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;

import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.util.concurrent.ExecutionException;

public class SelectCityActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , DialogClickListener,IServiceListener {
    private static final String TAG = SelectCityActivity.class.getName();

    private SimpleGestureFilter detector;
    private EditText txtCityDropDown;
    private CitySpinnerAdapter citySpinnerAdapter;
    private ArrayList<String> cityList;
    private View mDecorView;
    private Activity activity;
    GoogleApiClient mGoogleApiClient = null;
    Location mLastLocation = null;
    private TextView txtTaptoView;
    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        txtTaptoView = (TextView)findViewById(R.id.txt_swipe_up);

        mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        buildGoogleApiClient();

        cityList = new ArrayList<>();
        //cityList.add("Coimbatore");
        new FetchCity().execute();
        activity=this;
        //detector = new SimpleGestureFilter(this, this);
        txtCityDropDown = (EditText) findViewById(R.id.btn_city_drop_down);
        citySpinnerAdapter = new CitySpinnerAdapter(this, R.layout.city_dropdown_item, cityList);
        txtCityDropDown.setOnClickListener(this);
        txtTaptoView.setOnClickListener(this);
        //check if user had previously selected city
        String cityName = PreferenceStorage.getUserCity(this);
        if( (cityName != null) && !cityName.isEmpty()){
            txtCityDropDown.setText(cityName);
        }

        TextView autoselectCity = (TextView) findViewById(R.id.auto_select_location);

        autoselectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"fetching the current city based on current location");
                if((mLastLocation == null) && (mGoogleApiClient != null)){
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                }

                if(mLastLocation != null) {
                    CityAsyncTask cst = new CityAsyncTask(SelectCityActivity.this,
                            mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    cst.execute();

                    String lo = null;
                    try {
                        if(cst.get() != null) {
                            lo = cst.get().toString();
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }else{
                    Log.e(TAG, "fetched location is Null");
                    AlertDialogHelper.showSimpleAlertDialog(SelectCityActivity.this,"Current Location Not Available. Please check if Location services are turned ON");
                }

            }
        });

    }

    protected  void buildGoogleApiClient() {
        Log.d(TAG, "Initiate GoogleApi connection");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

  /*  @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onSwipe(int direction) {
        if (direction == SimpleGestureFilter.SWIPE_UP) {
            Log.d(TAG,"Swipe up detected");
            if (!txtCityDropDown.getText().toString().equalsIgnoreCase("Select your City")) {
                Intent intent = new Intent(this, SelectPreferenceActivity.class);
                intent.putExtra("selectedCity", txtCityDropDown.getText().toString());
                PreferenceStorage.saveUserCity(this,txtCityDropDown.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            } else {
                Toast.makeText(this, "Please select your city", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    @Override
    public void onClick(View v) {
        if (v == txtCityDropDown) {
            Log.d(TAG, "Available cities count" + citySpinnerAdapter.getCount());
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
            TextView header = (TextView) view.findViewById(R.id.gender_header);
            header.setText("Select City");
            builderSingle.setCustomTitle(view);

            builderSingle.setAdapter(citySpinnerAdapter, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            txtCityDropDown.setText(citySpinnerAdapter.getItem(which).toString());
                            txtCityDropDown.clearComposingText();
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (v == txtTaptoView) {
            Log.d(TAG,"Swipe up detected");
            if (!txtCityDropDown.getText().toString().equalsIgnoreCase("Select your City")) {
                updateUserCity();
            } else {
                Toast.makeText(this, "Please select your city", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUserCity() {
        String userCity = txtCityDropDown.getText().toString();
        if ((userCity == null) && !(userCity.isEmpty())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Please select your city");
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Updating City");
            mProgressDialog.show();
                saveCity();
        }
    }

    private void saveCity() {

        String cityVal = txtCityDropDown.getText().toString();


        String url = String.format(FindAFunConstants.UPDATE_CITY,
                Integer.parseInt(PreferenceStorage.getUserId(this)), cityVal);

        SignUpServiceHelper mServiceHelper = new SignUpServiceHelper(this);
        mServiceHelper.updateUserProfile(url, this);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");


        if((mGoogleApiClient != null)  && !mGoogleApiClient.isConnected()){
            Log.d(TAG, "make api connect");
            mGoogleApiClient.connect();

        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if((mGoogleApiClient != null) && (mGoogleApiClient.isConnected())) {
            Log.d(TAG,"make api disconnect");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            // Log.e(TAG, "Current location is" + "Lat" + String.valueOf(mLastLocation.getLatitude()) + "Long" + String.valueOf(mLastLocation.getLongitude()));


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onSuccess(int resultCode, Object result) {

        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        Log.d(TAG, "received on success");

        if (result instanceof JSONObject) {
            Log.d(TAG, "City was saved successfully");

            //AlertDialogHelper.showSimpleAlertDialog(this, "City updated succesfully");

            Intent intent = new Intent(this, SelectPreferenceActivity.class);
            intent.putExtra("selectedCity", txtCityDropDown.getText().toString());
            PreferenceStorage.saveUserCity(this,txtCityDropDown.getText().toString());
            startActivity(intent);
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

        }
    }

    @Override
    public void onError(String erorr) {

        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        AlertDialogHelper.showSimpleAlertDialog(this, "Error saving your city. Try again");

    }

    public class CityAsyncTask extends AsyncTask<String, String, String> {
        Activity act;
        double latitude;
        double longitude;

        public CityAsyncTask(Activity act, double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            this.act = act;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            Geocoder geocoder = new Geocoder(act, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                Log.e("Addresses", "-->" + addresses);
                Address address = addresses.get(0);
                if(address != null){
                    result = address.getLocality();

                }else {
                    result = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.d(TAG,"received city is"+ result);

            String citytext = txtCityDropDown.getText().toString();
            Log.d(TAG,"current city text is"+ citytext);

            if( (result != null) && !(result.isEmpty()) ) {
                txtCityDropDown.setText(result.toLowerCase());
            }else{
                Toast.makeText(SelectCityActivity.this, "Unable to retrive current location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class FetchCity extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            final String url = FindAFunConstants.GET_CITY_URL;
            Log.d(TAG,"fetch city list URL");

            new Thread() {
                public void run() {
                    String in = null;
                  try {
                        in = openHttpConnection(url);
                      JSONArray jsonArray=new JSONArray(in);

                      for(int i=0;i<jsonArray.length();i++) {
                          JSONObject jsonObject =jsonArray.getJSONObject(i);
                        cityList.add(jsonObject.getString("city_name"));
                      }
                      Log.d(TAG,"Received city list"+ jsonArray.length());
                    }

                    catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }
            }.start();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            citySpinnerAdapter = new CitySpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, cityList);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private String openHttpConnection(String urlStr) {
        InputStream in = null;
        StringBuilder sb=new StringBuilder();
        int resCode = -1;

        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();

            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String read;

                while((read=br.readLine()) != null) {
                    //System.out.println(read);
                    sb.append(read);
                }

            }
        }

        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    }
