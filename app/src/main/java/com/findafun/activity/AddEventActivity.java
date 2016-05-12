package com.findafun.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

public class AddEventActivity extends AppCompatActivity implements DialogClickListener {
    TextView startDate, endDate, Time;
    EditText nameVal, venueVal, desciptVal, startVal, endVal, TimeVal, contactVal, latVal, lngVal;
    DialogFragment timepicker, datepicker;
    private static final String TAG = SelectCityActivity.class.getName();
    Spinner eventcity, eventType, eventCategory;
    private ArrayList<String> cityList;
    private CitySpinnerAdapter citySpinnerAdapter;
    ArrayAdapter<CharSequence> cityAdapt, typeAdapt, categAdapt;
    Button btnSave,btnDiscard;
    String selectedCategory,selectedType,selectedCity;
    private static int RESULT_LOAD_IMG_BANNER = 1;
    String imgBannerDecodableString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        eventCategory = (Spinner) findViewById(R.id.addEvtCatgryVal);
        eventType = (Spinner) findViewById(R.id.addEvtTypeVal);
        eventcity = (Spinner) findViewById(R.id.addEvtCityVal);
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


        getcityList();

        getTypeList();


        getCategoryList();


        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameVal.setText("");
                eventCategory.setSelection(0);
                eventType.setSelection(0);
                eventcity.setSelection(0);
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
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_lng));
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

        categAdapt = ArrayAdapter.createFromResource(this,
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
        });


    }

    private void getTypeList() {

        typeAdapt = ArrayAdapter.createFromResource(this,
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
        });

    }

    private void getcityList() {

        cityAdapt = ArrayAdapter.createFromResource(this,
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