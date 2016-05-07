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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.adapter.CitySpinnerAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {
    TextView startDate,endDate,Time;
    EditText startVal,endVal,TimeVal;
    DialogFragment timepicker,datepicker;
    private static final String TAG = SelectCityActivity.class.getName();
    Spinner eventcity,eventType,eventCategory;
    private ArrayList<String> cityList;
    private CitySpinnerAdapter citySpinnerAdapter;
    ArrayAdapter<CharSequence> cityAdapt,typeAdapt,categAdapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        startDate = (TextView) findViewById(R.id.addEvtStartDate);
        endDate = (TextView) findViewById(R.id.addEvtEndDate);
        Time = (TextView) findViewById(R.id.addEvtTime);


        startVal = (EditText) findViewById(R.id.addEvtStartDateVal);
        endVal = (EditText) findViewById(R.id.addEvtEndDateVal);
        TimeVal = (EditText) findViewById(R.id.addEvtTimeVal);



        getcityList();
        eventcity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {

                String selectedCity=(String)parent.getItemAtPosition(pos);
                Toast.makeText(getApplicationContext(), "City :"+selectedCity, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        getTypeList();
        eventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedType=(String)parent.getItemAtPosition(position);
                Toast.makeText(AddEventActivity.this, "City :"+selectedType, Toast.LENGTH_LONG).show();

                if(selectedType.trim().toLowerCase().equals("regular")){

                    Time.setVisibility(View.VISIBLE);
                    TimeVal.setVisibility(View.VISIBLE);
                    startDate.setVisibility(View.GONE);
                    endDate.setVisibility(View.GONE);
                    startVal.setVisibility(View.GONE);
                    endVal.setVisibility(View.GONE);

                }else if(selectedType.trim().toLowerCase().equals("special")){

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

        getCategoryList();
        eventCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory=(String)parent.getItemAtPosition(position);
                Toast.makeText(AddEventActivity.this, "City :"+selectedCategory, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private void getCategoryList() {
        eventCategory = (Spinner) findViewById(R.id.addEvtCatgryVal);
        categAdapt = ArrayAdapter.createFromResource(this,
                R.array.event_category, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        categAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        eventCategory.setAdapter(categAdapt);
    }

    private void getTypeList() {
        eventType = (Spinner) findViewById(R.id.addEvtTypeVal);
        typeAdapt = ArrayAdapter.createFromResource(this,
                R.array.event_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        typeAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        eventType.setAdapter(typeAdapt);
    }

    private void getcityList() {
        eventcity = (Spinner) findViewById(R.id.addEvtCityVal);
         cityAdapt = ArrayAdapter.createFromResource(this,
                R.array.india_top_places, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        cityAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        eventcity.setAdapter(cityAdapt);





    }


    public void showTimePickerDialog(View v){

        timepicker = new TimePickerFragment();
        timepicker.show(getSupportFragmentManager(), "timePicker");



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
            String timeString = hourOfDay+":"+minute;

            onTimeSet(timeString);
            Toast.makeText(getActivity(),"date :"+hourOfDay+" "+minute, Toast.LENGTH_LONG).show();

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



            String selectedDate = Integer.toString(month)+"/"+Integer.toString(day)+"/"+Integer.toString(year);
            //date_text.setText(selectedDate);
            System.out.println("selected date is:"+selectedDate);
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


    }

}
