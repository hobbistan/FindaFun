package com.findafun.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.findafun.R;
import com.findafun.app.AppController;
import com.findafun.bean.events.Event;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.FindAFunHelper;
import com.findafun.servicehelpers.EventServiceHelper;
import com.findafun.servicehelpers.ShareServiceHelper;
import com.findafun.serviceinterfaces.IEventServiceListener;
import com.findafun.serviceinterfaces.IGamificationServiceListener;
import com.findafun.twitter.TwitterAuthenticateTask;
import com.findafun.twitter.TwitterUtil;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class EventDetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, IGamificationServiceListener, IEventServiceListener {
    private static final String TAG = EventDetailActivity.class.getName();
    private Event event;
    private TextView txtEventName, txtEventCategory, txtEventDesc, txtEventVenue, txtEventStartDate, txtEventEndDate, txtEventStartTime, txtEventEndTime;
    private TextView txtEventTime, txtEventDate, txtEventEntry, txtEventContact, txtEventEmail, txtWebSite;
    private ImageView imgEventBanner;
    ImageLoader uImageLoader = AppController.getInstance().getUniversalImageLoader();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double currentLatitude, currentLongitude;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    private boolean isPermissionGranted = false;
    private ImageView mFacebookBtn, mTwitterBtn, mWhatsAppBtn;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private EventServiceHelper mServiceHelper = null;
    private ArrayAdapter<String> mShareAdapter = null;
    private List<String> mShareLIst = new ArrayList<String>();
    private List<Integer> mShareResources = new ArrayList<Integer>();
    private List<Integer> mShareBagroundResources = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.activity_event_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("EVENT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();
        event = (Event) getIntent().getSerializableExtra("eventObj");
        populateData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if (mGoogleApiClient != null)
            mGoogleApiClient.connect();*/
    }

    @Override
    protected void onStop() {
        super.onStop();
       /* if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();*/
    }

    /* private void addEvent() {
         Intent intent = new Intent(Intent.ACTION_INSERT)
                 .setData(CalendarContract.Events.CONTENT_URI)
                 .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,(long) event.getStartDate())
                 .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, (long) event.getEndDate())
                 .putExtra(CalendarContract.Events.TITLE, "")
                 .putExtra(CalendarContract.Events.DESCRIPTION, "")
                 .putExtra(CalendarContract.Events.EVENT_LOCATION, "")
                 .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

         startActivity(intent);
     }*/
    public void facebook(View v) {
        try {


            Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
            if (intent != null) {
                // The application exists
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage("com.facebook.katana");

                shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, event.getEventName());
                shareIntent.putExtra(Intent.EXTRA_TEXT, event.getDescription());
                // Start the specific social application
                startActivity(shareIntent);
            } else {
                // The application does not exist
                // Open GooglePlay or use the default system picker
            }
        } catch (Exception ex) {
            return;
        }
    }

    public void twitter(View v) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.twitter.android");
            if (intent != null) {
                // The application exists
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage("com.twitter.android");
                SpannableString content = new SpannableString("http://www.hobbistan.com/");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

                String text = event.getEventName();

                shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, "www.Hobbistan.com");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                // Start the specific social application
                startActivity(shareIntent);
            } else {
                // The application does not exist
                // Open GooglePlay or use the default system picker
            }
        } catch (Exception ex) {
            return;
        }
    }

    public void whatsapp(View v) {


        PackageManager pm = getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");


            SpannableString content = new SpannableString("http://www.hobbistan.com/");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

            String text = event.getEventName() + "\n" + event.getDescription() + "\n" + content;


            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));
            sendShareStatus();

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void populateData() {
        txtEventName.setText(event.getEventName());
        // txtEventDesc.setText(event.getDescription());
        if(event.getDescription() != null) {
            txtEventCategory.setText(event.getDescription());
        }
        txtEventVenue.setText(event.getEventVenue());
        txtEventVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lat = Double.parseDouble(event.getEventLatitude());
                double longitude = Double.parseDouble(event.getEventLongitude());
                if ((lat > 0) | (longitude > 0)) {
                    Intent intent = new Intent(EventDetailActivity.this, MapsActivity.class);
                    intent.putExtra("eventObj", event);
                    startActivity(intent);
                }else{
                    Toast.makeText(EventDetailActivity.this,"Location information not available for this event", Toast.LENGTH_SHORT).show();
                }
            }
        });
        String timeDetails = null;
        String dateDetails = null;
        String start = FindAFunHelper.getDate(event.getStartDate());
        String end = FindAFunHelper.getDate(event.getEndDate());
        if (start != null) {
            txtEventStartDate.setText(start);
        }
        if ((end != null)) {
            txtEventEndDate.setText(end);
        }
        if(event.getEvent_cost() != null) {
            txtEventEntry.setText(event.getEvent_cost());
        }
        //fetch timer values
        start = FindAFunHelper.getTime(event.getStartDate());
        end = FindAFunHelper.getTime(event.getEndDate());
        if (start != null) {
            txtEventStartTime.setText(start);
        }

        if (end != null) {
            txtEventEndTime.setText(end);
        }

       if((event.getEventEmail() != null) && !(event.getEventEmail().isEmpty())){
           txtEventEmail.setText(event.getEventEmail());
       }else{
           txtEventEmail.setText("N/A");
       }
        Log.d(TAG, "Image uri is" + event.getEventBanner());
        uImageLoader.displayImage((event.getEventLogo()), imgEventBanner);
    }

    private void showShareList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Share Event Using");
        /*View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Gender");
        builderSingle.setCustomTitle(view);*/

        builderSingle.setAdapter(mShareAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mShareLIst.get(which);
                        if (which == 0) {
                            shareWithfacebook();

                        } else if (which == 1) {
                            postUsingTwitter();
                        } else if (which == 2) {
                            whatsapp(null);
                        }

                    }
                });
        builderSingle.show();
    }


    private void initializeViews() {
        txtEventName = (TextView) findViewById(R.id.txt_event_name);
        // txtEventDesc = (TextView) findViewById(R.id.txt_event_desc);
        txtEventVenue = (TextView) findViewById(R.id.txt_event_venue);
        txtEventCategory = (TextView) findViewById(R.id.txt_event_category);
        //txtEventTime = (TextView) findViewById(R.id.txt_calander);
        //txtEventDate = (TextView) findViewById(R.id.txt_time);
        txtEventEntry = (TextView) findViewById(R.id.txt_event_entry);

        txtEventEmail = (TextView) findViewById(R.id.txt_contact_mail);
        //txtWebSite = (TextView) findViewById(R.id.txt_website);

        Button whishListBtn = (Button) findViewById(R.id.whishlist_btn);
        Button shareBtn = (Button) findViewById(R.id.share_btn);
        Button contactBtn = (Button) findViewById(R.id.contact_btn);
        whishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Bookmark Button selected" + event.getId());
                if (mServiceHelper == null) {
                    mServiceHelper = new EventServiceHelper(EventDetailActivity.this);
                    mServiceHelper.setEventServiceListener(EventDetailActivity.this);

                }
                if(GamificationDataHolder.getInstance().isEventBookmarked(event.getId())){
                    Toast.makeText(EventDetailActivity.this,"Event already bookmarked", Toast.LENGTH_SHORT).show();

                }else {
                    try {
                        mServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.ADD_EVENT_BOOKMARK,
                                Integer.parseInt(PreferenceStorage.getUserId(EventDetailActivity.this)), Integer.parseInt((event.getId()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareList();

            }
        });
        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = event.getContact();
                if ((phoneNumber != null) && !(phoneNumber.isEmpty())) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));

                    startActivity(intent);
                }

            }
        });

        txtEventStartDate = (TextView) findViewById(R.id.txt_calander);
        txtEventEndDate = (TextView) findViewById(R.id.cal_to_val);
        txtEventStartTime = (TextView) findViewById(R.id.txt_clock_from_val);
        txtEventEndTime = (TextView) findViewById(R.id.clock_to_val);
        imgEventBanner = (ImageView) findViewById(R.id.img_banner);

        callbackManager = CallbackManager.Factory.create();

        mShareLIst.add("Facebook");
        mShareLIst.add("Twitter");
        mShareLIst.add("WhatsApp");
        mShareResources.add(R.drawable.facebook);
        mShareResources.add(R.drawable.twitter);
        mShareResources.add(R.drawable.whatsapp);
        mShareBagroundResources.add(R.drawable.social_network_statlist);
        mShareBagroundResources.add(R.drawable.twitter_share_bg);
        mShareBagroundResources.add(R.drawable.whatsapp_selector);
        mShareAdapter = new ArrayAdapter<String>(this, R.layout.share_dialog_layout, R.id.share_dialog_text, mShareLIst) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG,"getview called"+ position);
                View view = getLayoutInflater().inflate(R.layout.share_dialog_layout, parent, false);
                ImageView img = (ImageView) view.findViewById(R.id.share_image_src);
                TextView sharename = (TextView) view.findViewById(R.id.share_dialog_text);
                sharename.setText(mShareLIst.get(position));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    img.setBackground(getResources().getDrawable(mShareBagroundResources.get(position), getApplicationContext().getTheme()));
                    img.setImageDrawable(getResources().getDrawable(mShareResources.get(position),getApplicationContext().getTheme()));
                } else {
                    img.setBackgroundDrawable(getResources().getDrawable(mShareBagroundResources.get(position)));
                    img.setImageDrawable(getResources().getDrawable(mShareResources.get(position)));
                }



                // ... Fill in other views ...
                return view;
            }
        };

        mFacebookBtn = (ImageView) findViewById(R.id.facebook_btn);
        mFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWithfacebook();
            }
        });
        mTwitterBtn = (ImageView) findViewById(R.id.twitter_btn);
        mTwitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postUsingTwitter();

            }
        });
        mWhatsAppBtn = (ImageView) findViewById(R.id.whatsapp_btn);
        mWhatsAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whatsapp(v);
            }
        });
    }

    private void sendShareStatustoServer(){
        ShareServiceHelper serviceHelper = new ShareServiceHelper(this);
        int eventId = Integer.parseInt(event.getId());
        int ruleid = 1;
        int ticketcount = 0;
        String activitydetail = "You have shared photo"+ event.getEventName();
        serviceHelper.postShareDetails(String.format(FindAFunConstants.SHARE_EVENT_URL,eventId, Integer.parseInt(PreferenceStorage.getUserId(this)),
                ruleid,Uri.encode(activitydetail),event.getEventLogo(),ticketcount),this);

    }

    private void sendShareStatus(){

        //A user can only get points 3 times a day for photo sharing. So restrict beyond that
        long currentTime = System.currentTimeMillis();
        long lastsharedTime = PreferenceStorage.getEventSharedTime(this);
        int sharedCount = PreferenceStorage.getEventSharedcount(this);

        if( (currentTime - lastsharedTime)  > FindAFunConstants.TWENTY4HOURS ){
            Log.d(TAG,"event time elapsed more than 24hrs");
            PreferenceStorage.saveEventSharedtime(this, currentTime);
            PreferenceStorage.saveEventSharedcount(this, 1);
            sendShareStatustoServer();
        }else{
            if(sharedCount < 3){
                Log.d(TAG,"event shared cout is"+ sharedCount);
                sharedCount++;
                PreferenceStorage.saveEventSharedcount(this, sharedCount);
                sendShareStatustoServer();
            }
        }

    }

    private void shareWithfacebook(){
        shareDialog = new ShareDialog(EventDetailActivity.this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(TAG, "Share success");
                Toast.makeText(EventDetailActivity.this,"Shared event using facebook", Toast.LENGTH_SHORT).show();
                sendShareStatus();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Share cancelled");

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Share failed" + error.getLocalizedMessage());
                Toast.makeText(EventDetailActivity.this, "Error posting message. Please try again", Toast.LENGTH_SHORT);

            }
        });
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            Log.d(TAG,"Share can be done");
            String title = event.getEventName();
            String description = event.getDescription();
            Uri uri = Uri.parse("android.resource://http://www.hobbistan.com/wp-content/uploads/2015/12/logo-ho.png");
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription(description)
                    .setContentUrl(Uri.parse("http://www.hobbistan.com/"))
                    .setImageUrl(Uri.parse("http://www.hobbistan.com/wp-content/uploads/2015/12/logo-ho.png"))
                    .build();

            shareDialog.show(linkContent);
        }else{
            Log.d(TAG,"cant share content using facebook");
        }

    }
    private boolean twitterLoggedIn(){
        boolean loggedin = PreferenceStorage.getTwitterLoggedIn(this);
        return loggedin;
    }

    private void postUsingTwitter(){
        if( !CommonUtils.isNetworkAvailable(getApplicationContext())){
            Log.d(TAG,"No Network connection");
            Toast.makeText(this, "No Networkconnection", Toast.LENGTH_SHORT).show();
            return;

        }

        if((FindAFunConstants.TWITTER_CONSUMER_KEY.trim().length() == 0) || (FindAFunConstants.TWITTER_CONSUMER_SECRET.trim().length() == 0)){
            Log.d(TAG,"Consumer key or secret key is bot set");
            Toast.makeText(this,"Please set Twitter Consumer key and Consumer secret", Toast.LENGTH_SHORT).show();
            return;
        }


        if(twitterLoggedIn()){
            Log.d(TAG, "Already Logged In to twitter");
            String text = event.getEventName();

          //  shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, "www.Hobbistan.com");
           // shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            String message = "http://www.Hobbistan.com "+event.getEventName()+ "\n"+ event.getDescription();
            String shortText = "";
            if(message.length() >= 140){
                Log.d(TAG,"length greater than 140"+ message.length());
                shortText = message.substring(0,139);
                message = shortText;
            }
            Log.d(TAG,"length greater than 140"+ message.length());
            if ((message != null) && !message.isEmpty()) {

                new TwitterUpdateStatusTask().execute(message);
            }
           /* Intent intent = new Intent(this, TwitterActivity.class);
            startActivity(intent);*/

        }else{
            Log.d(TAG, "Start twitter oAuth");
            //Set the current event
            GamificationDataHolder.getInstance().setmCurrentEvent(event);
            new TwitterAuthenticateTask(this).execute();
        }

    }

    @Override
    public void onSuccess(int resultCode, Object result) {
        Log.d(TAG,"Succesfully posted share status");

    }

    @Override
    public void onError(String erorr) {
        Log.d(TAG,"error while sending status update"+ erorr);

    }

    @Override
    public void onEventResponse(JSONObject response) {
        Toast.makeText(this,"Event boookmarked succesfully", Toast.LENGTH_SHORT).show();
        GamificationDataHolder.getInstance().addBookmarkedEvent(event.getId());

    }

    @Override
    public void onEventError(String error) {
        Log.e(TAG,"Error while bookmarking event");
        Toast.makeText(this,"Error bookmarking event", Toast.LENGTH_SHORT).show();

    }

    class TwitterUpdateStatusTask extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(getApplicationContext(), "Event shared successfully using twitter", Toast.LENGTH_SHORT).show();
                sendShareStatus();
                // finish();
            }
            else
                Toast.makeText(getApplicationContext(), "Tweet failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessTokenString = sharedPreferences.getString(FindAFunConstants.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(FindAFunConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");

                if ( ((accessTokenString != null) && (accessTokenString.length() > 0))
                        && ( (accessTokenSecret != null) && (accessTokenSecret.length() > 0))) {
                    AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                    twitter4j.Status status = TwitterUtil.getInstance().getTwitterFactory().getInstance(accessToken).updateStatus(params[0]);
                    return true;
                }

            } catch (TwitterException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return false;  //To change body of implemented methods use File | Settings | File Templates.

        }
    }



    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Received FacebookActivity share result");
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //LinkedIN call
        // mLinkedinSessionManager.onActivityResult(this,requestCode,resultCode,data);
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
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + "&daddr=" + event.getEventLatitude() + "," + event.getEventLongitude()));
            startActivity(intent);
            return true;
        }else if(id == android.R.id.home) {
            Log.d(TAG,"home up button selected");
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    isPermissionGranted = true;
                    getLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
