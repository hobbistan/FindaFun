package com.findafun.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.adapter.CitySpinnerAdapter;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.utils.FindAFunValidator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEventActivity extends AppCompatActivity implements DialogClickListener {
    TextView startDate, endDate, Time;
    EditText nameVal, venueVal, desciptVal, startVal, endVal, TimeVal, contactVal, latVal, lngVal,eventCity, eventType, eventCategory;
    DialogFragment timepicker, datepicker;
  //  private String[] categoryList,typeList,citysList;
    private static final String TAG = SelectCityActivity.class.getName();
    //Spinner eventcity, eventType, eventCategory;
    ImageView imageView_back;
    private CitySpinnerAdapter citySpinnerAdapter;
    ArrayAdapter<CharSequence> cityAdapt, typeAdapt, categAdapt;
    Button btnSave,btnDiscard;
    String selectedCategory,selectedType,selectedCity;
    private static int RESULT_LOAD_IMG_BANNER = 1;
    String imgBannerDecodableString;
    private ListPopupWindow listPopupCategory,listPopupCity,listPopupType;

    private  ArrayAdapter<String> mCityAdapter = null;
    private List<String> mCityList = new ArrayList<String>();
    private  ArrayAdapter<String> mTypeAdapter = null;
    private List<String> mTypeList = new ArrayList<String>();
    private  ArrayAdapter<String> mCategoryAdapter = null;
    private List<String> mCategoryList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        mCityList.add("Select Your City");
        mCityList.add("Chennai");
        mCityList.add("Coimbatore");
        mCityList.add("Mumbai");
        mCityList.add("Trivandrum");
        mCityList.add("Delhi");
        mCityList.add("Bangalore");

        mCityAdapter = new ArrayAdapter<String>(this, R.layout.city_layout, R.id.city_name, mCityList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG,"getview called"+ position);
                View view = getLayoutInflater().inflate(R.layout.city_layout, parent, false);
                TextView cityname = (TextView) view.findViewById(R.id.city_name);
                cityname.setText(mCityList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };


        mTypeList.add("Select Type");
        mTypeList.add("Regular");
        mTypeList.add("Special");



        mTypeAdapter = new ArrayAdapter<String>(this, R.layout.type_layout, R.id.type_name, mTypeList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG,"getview called"+ position);
                View view = getLayoutInflater().inflate(R.layout.type_layout, parent, false);
                TextView typename = (TextView) view.findViewById(R.id.type_name);
                typename.setText(mTypeList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

     //   categoryList = new String[] { "Select Category", "Sports and Fitness", "Travel and Adventures", "Entertainment","Training / Workshop","Exhibitions / Fairs","Theatre /Stage Shows","Entertainment","Meetouts / Business","Fest / Carnivals","Food / Dineout","Shopping","Bars / Pubs"};
        mCategoryList.add("Select Category");
        mCategoryList.add("Sports and Fitness");
        mCategoryList.add("Travel and Adventures");
        mCategoryList.add("Entertainment");
        mCategoryList.add("Training / Workshop");
        mCategoryList.add("Exhibitions / Fairs");
        mCategoryList.add("Theatre /Stage Shows");
        mCategoryList.add("Meetouts / Business");
        mCategoryList.add("Fest / Carnivals");
        mCategoryList.add("Food / Dineout");
        mCategoryList.add("Shopping");
        mCategoryList.add("Bars / Pubs");



        mCategoryAdapter = new ArrayAdapter<String>(this, R.layout.category_layout, R.id.category_name, mCategoryList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG,"getview called"+ position);
                View view = getLayoutInflater().inflate(R.layout.category_layout, parent, false);
                TextView typename = (TextView) view.findViewById(R.id.category_name);
                typename.setText(mCategoryList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };




       // typeList = new String[] { "Select Type", "Regular", "Special" };
      //  citysList = new String[] { "Select Your City", "Chennai", "Coimbatore", "Mumbai","Trivandrum","Delhi","Bangalore" };


        imageView_back = (ImageView) findViewById(R.id.cst_back_btn);
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        eventCategory = (EditText) findViewById(R.id.addEvtCatgryVal);



        eventType = (EditText) findViewById(R.id.addEvtTypeVal);
        eventCity = (EditText) findViewById(R.id.addEvtCityVal);
        btnSave = (Button) findViewById(R.id.addEvtSave);
        btnDiscard = (Button) findViewById(R.id.addEvtDiscard);
        nameVal = (EditText) findViewById(R.id.addEvtNameVal);
        venueVal = (EditText) findViewById(R.id.addEvtVenueVal);
        desciptVal = (EditText) findViewById(R.id.addEvtDescVal);
        contactVal = (EditText) findViewById(R.id.addEvtContactVal);
        latVal = (EditText) findViewById(R.id.addEvtLatVal);
        lngVal = (EditText) findViewById(R.id.addEvtLngVal);
        startDate = (TextView) findViewById(R.id.addEvtStartDate);
        endDate = (TextView) findViewById(R.id.addEvtEndDate);
        Time = (TextView) findViewById(R.id.addEvtTime);


        startVal = (EditText) findViewById(R.id.addEvtStartDateVal);
        endVal = (EditText) findViewById(R.id.addEvtEndDateVal);
        TimeVal = (EditText) findViewById(R.id.addEvtTimeVal);



        eventCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCityList();
            }
        });

        eventCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryList();
            }
        });

        eventType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeList();
            }
        });




      // getcityList();

     //  getTypeList();


      // getCategoryList();


        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameVal.setText("");
                eventCategory.setSelection(0);
                eventType.setSelection(0);
                eventCity.setSelection(0);
                venueVal.setText("");
                desciptVal.setText("");
                startVal.setText("");
                endVal.setText("");
                TimeVal.setText("");
                contactVal.setText("");
                latVal.setText("");
                lngVal.setText("");

            }
        });



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               if(validateFields()){
                   Toast.makeText(AddEventActivity.this, "validated", Toast.LENGTH_SHORT).show();
               }


               /* if (nameVal.getText().toString().length() == 0) {
                    nameVal.setError("First name is required!");
                } else if (venueVal.getText().toString().length() == 0) {
                    nameVal.setError("Venue is required!");
                } else if (desciptVal.getText().toString().length() == 0) {
                    nameVal.setError("Description is required!");
                } else if (contactVal.getText().toString().length() == 0) {
                    contactVal.setError("Contact is required");
                } else if (latVal.getText().toString().length() == 0) {
                    latVal.setError("Latitude is required");
                } else if (lngVal.getText().toString().length() == 0) {
                    lngVal.setError("Latitude is required");
                }*/
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

        builderSingle.setAdapter(mTypeAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mTypeList.get(which);
                        eventType.setText(strName);
                    }
                });
        builderSingle.show();


    }


    private void showCategoryList() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Gender");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Category");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mCategoryAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mCategoryList.get(which);
                        eventCategory.setText(strName);
                    }
                });
        builderSingle.show();


    }

    private void showCityList() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select City");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select City");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mCityAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mCityList.get(which);
                        eventCity.setText(strName);
                    }
                });
        builderSingle.show();

    }









    private boolean validateFields() {
        if (!FindAFunValidator.checkNullString(this.nameVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_name));
            return false;
        } else if (!FindAFunValidator.checkStartWith(selectedCategory)) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_category));
            return false;
        }  else if (!FindAFunValidator.checkStartWith(selectedType)) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_type));
            return false;
        }  else if (!FindAFunValidator.checkStartWith(selectedCity)) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_city));
            return false;
        } else  if (!FindAFunValidator.checkNullString(this.venueVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_venue));
            return false;
        } else  if (!FindAFunValidator.checkNullString(this.desciptVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_desc));
            return false;
        }/*else
            if (!FindAFunValidator.checkNullString(this.startVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_start_date));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.endVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_end_date));
            return false;
        }*/ else if (!FindAFunValidator.checkNullString(this.TimeVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_time));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.contactVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_contact));
            return false;
        } else if (!FindAFunValidator.checkValidContact(this.contactVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_contact_valid));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.latVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_lat));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.lngVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_lng));
            return false;
        }

        else {
            return true;
        }
    }


    private void getCategoryList() {


        /*categAdapt = ArrayAdapter.createFromResource(this,
                R.array.event_category, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        categAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner

        eventCategory.setAdapter(categAdapt);

        eventCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (String) parent.getItemAtPosition(position);
             //   Toast.makeText(AddEventActivity.this, "City :" + selectedCategory, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


    }

    private void getTypeList() {












      /*  typeAdapt = ArrayAdapter.createFromResource(this,
                R.array.event_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        typeAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        eventType.setAdapter(typeAdapt);


        eventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedType = (String) parent.getItemAtPosition(position);
               // Toast.makeText(AddEventActivity.this, "City :" + selectedType, Toast.LENGTH_LONG).show();

                if (selectedType.trim().toLowerCase().equals("regular")) {

                    Time.setVisibility(View.VISIBLE);
                    TimeVal.setVisibility(View.VISIBLE);
                    startDate.setVisibility(View.GONE);
                    endDate.setVisibility(View.GONE);
                    startVal.setVisibility(View.GONE);
                    endVal.setVisibility(View.GONE);

                } else if (selectedType.trim().toLowerCase().equals("special")) {

                    Time.setVisibility(View.VISIBLE);
                    TimeVal.setVisibility(View.VISIBLE);
                    startDate.setVisibility(View.VISIBLE);
                    endDate.setVisibility(View.VISIBLE);
                    startVal.setVisibility(View.VISIBLE);
                    endVal.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

    }

    private void getcityList() {





       /* cityAdapt = ArrayAdapter.createFromResource(this,
                R.array.india_top_places, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        cityAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        eventcity.setAdapter(cityAdapt);


        eventcity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {

                 selectedCity = (String) parent.getItemAtPosition(pos);
            //    Toast.makeText(getApplicationContext(), "City :" + selectedCity, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
*/

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

/*    @Override
    public boolean onTouch(View v, MotionEvent event) {


        final int DRAWABLE_RIGHT = 2;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() >= (v.getWidth() - ((EditText) v)
                    .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                Boolean result =true;
                switch(v.getId()) {
                    case R.id.addEvtCatgryVal:
                        listPopupCategory.show();
                       result = true;
                        break;
                    case R.id.addEvtTypeVal:
                        listPopupType.show();
                        result = true;
                        break;
                    case R.id.addEvtCityVal:
                        listPopupCity.show();
                        result = true;
                        break;


                }

                return result;

            }



        }
        return false;
    }*/

    @SuppressLint("ValidFragment")
    class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            String timeString = hourOfDay + ":" + minute;

            onTimeSet(timeString);
       //     Toast.makeText(getActivity(), "date :" + hourOfDay + " " + minute, Toast.LENGTH_LONG).show();

        }

        private void onTimeSet(String timeString) {
            ((EditText) getActivity().findViewById(R.id.addEvtTimeVal)).setText(timeString);
        }
    }


    public void showDatePickerDialog(View v) {
        datepicker = new DatePickerFragment();
        datepicker.show(getSupportFragmentManager(), "datePicker");


    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user


            String selectedDate = Integer.toString(month) + "/" + Integer.toString(day) + "/" + Integer.toString(year);
            //date_text.setText(selectedDate);
            System.out.println("selected date is:" + selectedDate);
            onDateSet(selectedDate);
            // startVal.setText(selectedDate);



           /* switch (view.getId()){
                case R.id.addEvtStartDateVal:
                    startVal.setText("");
                    break;
            }*/


            //   Toast.makeText(getActivity(),"date :"+year+" "+month, Toast.LENGTH_LONG).show();

        }

        private void onDateSet(String selectedDate) {
            ((EditText) getActivity().findViewById(R.id.addEvtStartDateVal)).setText(selectedDate);
            //    startVal.setText(selectedDate);
        }




        private void onLoadImageBanner(View view){


        }



    }
}