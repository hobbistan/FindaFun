package com.findafun.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.adapter.CategorySpinnerAdapter;
import com.findafun.adapter.CitySpinnerAdapter;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.FindAFunValidator;

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
import java.util.Calendar;
import java.util.List;

public class AddEventActivity extends AppCompatActivity implements DialogClickListener {

    ImageView imageView_back;
    EditText nameValue;
    EditText eventType;
    EditText eventCategory;
    EditText eventCity;
    EditText venueValue;
    EditText descripValue;
    LinearLayout layout_date;
    TextView startDate;
    EditText startDateValue;
    TextView endDate;
    EditText endDateValue;
    LinearLayout layout_time;
    TextView startTime;
    EditText startTimeValue;
    TextView endTime;
    EditText endTimeValue;
    EditText contactValue;
    EditText latValue;
    EditText lngValue;
    TextView txtLogo;
    Button btnLogo;
    TextView txtBanner;
    Button btnBanner;
    TextView txtPhotos;
    Button btnPhotos;
    Button btnSave;
    Button btnDiscard;

    DialogFragment timepicker, datepicker;
    private static final String TAG = SelectCityActivity.class.getName();

    private CitySpinnerAdapter citySpinnerAdapter;
    private CategorySpinnerAdapter categorySpinnerAdapter;

    String selectedCategory, selectedType, selectedCity;
    private ArrayList<String> cityList = new ArrayList<String>();
    private ArrayList<String> categoryList = new ArrayList<String>();
    private static int RESULT_LOAD_IMG_BANNER = 1;
    String imgBannerDecodableString;
    private ListPopupWindow listPopupCategory, listPopupCity, listPopupType;

    private ArrayAdapter<String> mCityAdapter = null;
    private List<String> mCityList = new ArrayList<String>();
    private ArrayAdapter<String> mTypeAdapter = null;
    private List<String> mTypeList = new ArrayList<String>();
    private ArrayAdapter<String> mCategoryAdapter = null;
    private List<String> mCategoryList = new ArrayList<String>();
    static int datePickerInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        new FetchCity().execute();
        new FetchCategory().execute();

        setUI();
    }

    private void setUI() {

        citySpinnerAdapter = new CitySpinnerAdapter(this, R.layout.city_dropdown_item, cityList);
        categorySpinnerAdapter = new CategorySpinnerAdapter(this, R.layout.category_dropdown_item, categoryList);

        mTypeList.add("Normal Event");
        mTypeList.add("Hotspot Event");
        mTypeList.add("Ad Event");

        mTypeAdapter = new ArrayAdapter<String>(this, R.layout.type_layout, R.id.type_name, mTypeList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.type_layout, parent, false);
                TextView typename = (TextView) view.findViewById(R.id.type_name);
                typename.setText(mTypeList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        imageView_back = (ImageView) findViewById(R.id.cst_back_btn);

        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nameValue = (EditText) findViewById(R.id.addEvtNameVal);
        eventType = (EditText) findViewById(R.id.addEvtTypeVal);
        eventCategory = (EditText) findViewById(R.id.addEvtCatgryVal);
        eventCity = (EditText) findViewById(R.id.addEvtCityVal);
        venueValue = (EditText) findViewById(R.id.addEvtVenueVal);
        descripValue = (EditText) findViewById(R.id.addEvtDescVal);
        layout_date = (LinearLayout) findViewById(R.id.layout_date);
        startDate = (TextView) findViewById(R.id.addEvtStartDate);
        startDateValue = (EditText) findViewById(R.id.addEvtStartDateVal);
        endDate = (TextView) findViewById(R.id.addEvtEndDate);
        endDateValue = (EditText) findViewById(R.id.addEvtEndDateVal);
        layout_time = (LinearLayout) findViewById(R.id.layout_time);
        startTime = (TextView) findViewById(R.id.addEvtStrTime);
        startTimeValue = (EditText) findViewById(R.id.addEvtStrTimeVal);
        endTime = (TextView) findViewById(R.id.addEvtEndTime);
        endTimeValue = (EditText) findViewById(R.id.addEvtEndTimeVal);
        contactValue = (EditText) findViewById(R.id.addEvtContactVal);
        latValue = (EditText) findViewById(R.id.addEvtLatVal);
        lngValue = (EditText) findViewById(R.id.addEvtLngVal);
        txtLogo = (TextView) findViewById(R.id.addEvtLogoNoFile);
        btnLogo = (Button) findViewById(R.id.buttonAddLogoPic);
        txtBanner = (TextView) findViewById(R.id.addEvtBannerNoFile);
        btnBanner = (Button) findViewById(R.id.buttonAddBannerPic);
        txtPhotos = (TextView) findViewById(R.id.addEvtPhotoNoFile);
        btnPhotos = (Button) findViewById(R.id.buttonAddEventPic);
        btnSave = (Button) findViewById(R.id.addEvtSave);
        btnDiscard = (Button) findViewById(R.id.addEvtDiscard);

        eventType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeList();
            }
        });

        eventCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryList();
            }
        });

        eventCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCityList();
            }
        });

        startDateValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DATE", 1);

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);

                newFragment.show(getSupportFragmentManager(), "datePicker");

                endDate.setVisibility(View.VISIBLE);
                endDateValue.setVisibility(View.VISIBLE);
            }
        });


        endDateValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DATE", 2);

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);

                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        startTimeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("TIME", 1);

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);

                newFragment.show(getSupportFragmentManager(), "TimePicker");
            }
        });

        endTimeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("TIME", 2);

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);

                newFragment.show(getSupportFragmentManager(), "TimePicker");
            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameValue.setText("");
                eventCategory.setSelection(0);
                eventType.setSelection(0);
                eventCity.setSelection(0);
                venueValue.setText("");
                descripValue.setText("");
                startDateValue.setText("");
                endDateValue.setText("");
                startTimeValue.setText("");
                endTimeValue.setText("");
                contactValue.setText("");
                latValue.setText("");
                lngValue.setText("");

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {
                    Toast.makeText(AddEventActivity.this, "validated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showTypeList() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Type");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);

        header.setText("Select Type");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mTypeAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mTypeList.get(which);
                        eventType.setText(strName);

                        if ((strName.trim().contentEquals("Normal Event")) || (strName.trim().contentEquals("Ad Event"))) {

                            //Date Enable
                            layout_date.setVisibility(View.VISIBLE);
                            startDate.setVisibility(View.VISIBLE);
                            startDateValue.setVisibility(View.VISIBLE);
                            endDate.setVisibility(View.VISIBLE);
                            endDateValue.setVisibility(View.VISIBLE);

                            //Time Enable
                            layout_time.setVisibility(View.VISIBLE);
                            startTime.setVisibility(View.VISIBLE);
                            startTimeValue.setVisibility(View.VISIBLE);
                            endTime.setVisibility(View.VISIBLE);
                            endTimeValue.setVisibility(View.VISIBLE);

                        } else if (strName.trim().contentEquals("Hotspot Event")) {

                            //Date Enable
                            layout_date.setVisibility(View.GONE);
                            startDate.setVisibility(View.GONE);
                            startDateValue.setVisibility(View.GONE);
                            endDate.setVisibility(View.GONE);
                            endDateValue.setVisibility(View.GONE);

                            //Time Enable
                            layout_time.setVisibility(View.VISIBLE);
                            startTime.setVisibility(View.VISIBLE);
                            startTimeValue.setVisibility(View.VISIBLE);
                            endTime.setVisibility(View.VISIBLE);
                            endTimeValue.setVisibility(View.VISIBLE);
                        }
                    }
                });
        builderSingle.show();
    }

    private void showCategoryList() {
        Log.d(TAG, "show the category list");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Category");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(categorySpinnerAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventCategory.setText(categorySpinnerAdapter.getItem(which).toString());
                dialog.dismiss();
            }
        }).create().show();
    }

    private void showCityList() {
        Log.d(TAG, "show the city list");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select City");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(citySpinnerAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventCity.setText(citySpinnerAdapter.getItem(which).toString());
                dialog.dismiss();
            }
        }).create().show();
    }

    private class FetchCity extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            final String url = FindAFunConstants.GET_CITY_URL;
            Log.d(TAG, "fetch city list URL");

            new Thread() {
                public void run() {
                    String in = null;
                    try {
                        in = openHttpConnection(url);
                        JSONArray jsonArray = new JSONArray(in);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            cityList.add(jsonObject.getString("city_name"));
                        }
                        Log.d(TAG, "Received city list" + jsonArray.length());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }
            }.start();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            citySpinnerAdapter = new CitySpinnerAdapter(AddEventActivity.this, android.R.layout.simple_spinner_dropdown_item, cityList);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class FetchCategory extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            final String url = FindAFunConstants.GET_CATEGORY_LIST_URL;
            Log.d(TAG, "fetch category list URL");

            new Thread() {
                public void run() {
                    String in = null;
                    try {
                        in = openHttpConnection(url);
                        JSONArray jsonArray = new JSONArray(in);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            categoryList.add(jsonObject.getString("category_name"));
                        }
                        Log.d(TAG, "Received category list" + jsonArray.length());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }.start();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            categorySpinnerAdapter = new CategorySpinnerAdapter(AddEventActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryList);
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
        StringBuilder sb = new StringBuilder();
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

                while ((read = br.readLine()) != null) {
                    //System.out.println(read);
                    sb.append(read);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void showTimePickerDialog(View v) {

        timepicker = new TimePickerFragment();
        timepicker.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        static final int START_TIME = 1;
        static final int END_TIME = 2;
        private int mChosenTime;
        int cur = 0;
        String am_pm = "";

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            if (c.get(Calendar.AM_PM) == Calendar.AM) {
                am_pm = "AM";
            } else if (c.get(Calendar.AM_PM) == Calendar.PM) {
                am_pm = "PM";
            }

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                mChosenTime = bundle.getInt("TIME", 1);
            }

            switch (mChosenTime) {

                case START_TIME:
                    cur = START_TIME;
                    return new TimePickerDialog(getActivity(), this, hour, minute, true);

                case END_TIME:
                    cur = END_TIME;
                    return new TimePickerDialog(getActivity(), this, hour, minute, true);

            }
            return null;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            if (cur == START_TIME) {
                // set selected date into textview
                Log.v("Date Début", "Date1 : " + new StringBuilder().append(hourOfDay)
                        .append(":").append(minute)
                        .append(" "));

                ((EditText) getActivity().findViewById(R.id.addEvtTimeVal)).setText(new StringBuilder().append(hourOfDay)
                        .append(":").append(minute).append(" ").append(am_pm)
                        .append(" "));
            } else {
                Log.v("Date fin", "Date2 : " + new StringBuilder().append(hourOfDay)
                        .append(":").append(minute)
                        .append(" "));

                ((EditText) getActivity().findViewById(R.id.addEvtEndTimeVal)).setText(new StringBuilder().append(hourOfDay)
                        .append(":").append(minute).append(" ").append(am_pm)
                        .append(" "));
            }

        }

        private void onTimeSet(String timeString) {
            ((EditText) getActivity().findViewById(R.id.addEvtTimeVal)).setText(timeString);
        }
    }

    public void showDatePickerDialog(View v) {
        datepicker = new DatePickerFragment();
        datepicker.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        static final int START_DATE = 1;
        static final int END_DATE = 2;

        private int mChosenDate;

        int cur = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                mChosenDate = bundle.getInt("DATE", 1);
            }

            switch (mChosenDate) {

                case START_DATE:
                    cur = START_DATE;
                    return new DatePickerDialog(getActivity(), this, year, month, day);

                case END_DATE:
                    cur = END_DATE;
                    return new DatePickerDialog(getActivity(), this, year, month, day);

            }
            return null;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            if (cur == START_DATE) {
                // set selected date into textview
                Log.v("Date Début", "Date1 : " + new StringBuilder().append(month + 1)
                        .append("-").append(day).append("-").append(year)
                        .append(" "));

                ((EditText) getActivity().findViewById(R.id.addEvtStartDateVal)).setText(new StringBuilder().append(month + 1)
                        .append("-").append(day).append("-").append(year)
                        .append(" "));
            } else {
                Log.v("Date fin", "Date2 : " + new StringBuilder().append(month + 1)
                        .append("-").append(day).append("-").append(year)
                        .append(" "));

                ((EditText) getActivity().findViewById(R.id.addEvtEndDateVal)).setText(new StringBuilder().append(month + 1)
                        .append("-").append(day).append("-").append(year)
                        .append(" "));

            }
        }
    }

    private boolean validateFields() {
        if (!FindAFunValidator.checkNullString(this.nameValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_name));
            return false;
        } else if (!FindAFunValidator.checkStartWith(selectedCategory)) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_category));
            return false;
        } else if (!FindAFunValidator.checkStartWith(selectedType)) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_type));
            return false;
        } else if (!FindAFunValidator.checkStartWith(selectedCity)) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_city));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.venueValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_venue));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.descripValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_desc));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.startTimeValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_time));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.endTimeValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_time));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.contactValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_contact));
            return false;
        } else if (!FindAFunValidator.checkValidContact(this.contactValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_contact_valid));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.latValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_lat));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.lngValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_lng));
            return false;
        } else {
            return true;
        }
    }
}