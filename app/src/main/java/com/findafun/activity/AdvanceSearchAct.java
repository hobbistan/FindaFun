package com.findafun.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.bean.categories.Category;
import com.findafun.servicehelpers.CategoryServiceHelper;
import com.findafun.serviceinterfaces.ICategoryServiceListener;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

public class AdvanceSearchAct extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener , ICategoryServiceListener {
    private static final String TAG = AdvanceSearchAct.class.getName();

    String singleDate = "";
    boolean todayPressed = false, tomorrowPressed = false, datePressed = false;
    Spinner spincity;
    private ArrayList<String> selecteditemIndexList;
    private Button spincat;
    AlertDialog.Builder builder;
    StringBuilder sb;
    private boolean isdoneclick = false;
    String[] categoryarray = {"Sports & Fitness", "Spirituality", "Lifestyle", "Government", "Travel & Adventure", "Charity", "Health", "Entertainment", "Training / Workshop"
            , "Entertainment", "Training / Workshop", "Others"
    };
    boolean[] isSelectedArray = {
            false, false, false, false, false, false, false, false, false, false, false, false
    };
    DatePickerDialog mFromDatePickerDialog = null;
    private ArrayList<String> categoryArrayList = new ArrayList<String>();
    private CategoryServiceHelper categoryServiceHelper;
    ArrayAdapter<String> categoryAdapter = null;
    HashSet<Integer> mSelectedCategoryList = new HashSet<Integer>();
    private String mFromDateVal = null;
    private String mTodateVal = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_search);
        fetchCategoryValues();
        iniView();
    }


    private void iniView() {
        getSupportActionBar().hide();

        findViewById(R.id.btnselectdate).setOnClickListener(this);
        findViewById(R.id.btntomorrow).setOnClickListener(this);
        findViewById(R.id.btntoday).setOnClickListener(this);
        spincat = (Button) findViewById(R.id.catgoryspinner);

        spincat.setOnClickListener(this);
        String storedCategory = PreferenceStorage.getFilterCatgry(this);
        if( (storedCategory != null) && !(storedCategory.isEmpty())){
            spincat.setText(storedCategory);
        }


        spincity = (Spinner) findViewById(R.id.cityspinner);
        spincity.setOnItemSelectedListener(this);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.india_top_places));
        spincity.setAdapter(dataAdapter1);
        int index = PreferenceStorage.getFilterCityIndex(this);
        if((index >=0) && index < (getResources().getStringArray(R.array.india_top_places).length)){
            spincity.setSelection(index);
        }
        DatePickerSelection();
        findViewById(R.id.btnapply).setOnClickListener(this);
        findViewById(R.id.btncancel).setOnClickListener(this);
        fetchCategoryValues();
        categoryAdapter = new ArrayAdapter<String>(this, R.layout.category_list_item, R.id.category_list_name, categoryArrayList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG,"getview called"+ position);
                View view = getLayoutInflater().inflate(R.layout.category_list_item, parent, false);
                TextView name = (TextView) view.findViewById(R.id.category_list_name);
                name.setText(categoryArrayList.get(position));

                CheckBox checkbox = (CheckBox) view.findViewById(R.id.item_selection);
                checkbox.setTag(Integer.toString(position));
                if(mSelectedCategoryList.contains(position)){
                    checkbox.setChecked(true);
                }else{
                    checkbox.setChecked(false);
                }
                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String tag = (String) buttonView.getTag();
                        if (tag != null) {
                            int index = Integer.parseInt(tag);
                            if(mSelectedCategoryList.contains(index)){
                                mSelectedCategoryList.remove(index);
                            }else{
                                mSelectedCategoryList.add(index);
                            }

                        }
                    }
                });

                // ... Fill in other views ...
                return view;
            }
        };

        PreferenceStorage.saveFilterCatgry(this, "");
        PreferenceStorage.IsFilterApply(this, false);
        PreferenceStorage.saveFilterCity(this, "");
        PreferenceStorage.saveFilterFromDate(this, "");
        PreferenceStorage.saveFilterToDate(this, "");
        PreferenceStorage.saveFilterSingleDate(this, "");
    }

    private void fetchCategoryValues(){
        categoryServiceHelper = new CategoryServiceHelper(this);
        categoryServiceHelper.setCategoryServiceListener(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "category_list");
            jsonObject.put(FindAFunConstants.PARAMS_USER_ID, PreferenceStorage.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        categoryServiceHelper.makeGetCategoryServiceCall(jsonObject);
    }


    private void DatePickerSelection() {
        final Calendar c = Calendar.getInstance();
        final int currentYear = c.get(Calendar.YEAR);
        final int currentMonth = c.get(Calendar.MONTH) ;
        final int currentDay = c.get(Calendar.DAY_OF_MONTH);

       /* String singleDateVal = PreferenceStorage.getFilterSingleDate(this);
        if( (singleDateVal != null) && !(singleDateVal.isEmpty())){
            ((Button)findViewById(R.id.btnselectdate)).setText(singleDateVal);
        }*/


        final DatePickerDialog.OnDateSetListener fromdate = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int month, int day) {
                Log.d(TAG,"From selected");
               // isdoneclick = true;

                if (isdoneclick) {
                    ((Button) findViewById(R.id.btnfrom)).setText(formatDate(year, month , day));
                    mFromDateVal = formatDateServer(year, month , day);
                } else {
                    Log.e("Clear", "Clear");
                    ((Button) findViewById(R.id.btnfrom)).setText("");
                    mFromDateVal = "";
                }
            }

        };

        findViewById(R.id.btnfrom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btnselectdate).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                findViewById(R.id.btntomorrow).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                findViewById(R.id.btntoday).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                ((Button) findViewById(R.id.btnselectdate)).setText("DD-MM-YYYY");

                singleDate = "";
                todayPressed = false;
                datePressed = false;
                tomorrowPressed = false;
                mFromDatePickerDialog = new DatePickerDialog(AdvanceSearchAct.this, fromdate, currentYear,
                        currentMonth, currentDay);


                mFromDatePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isdoneclick = true;
                        Log.d(TAG, "Done clicked");
                        DatePicker datePicker = mFromDatePickerDialog.getDatePicker();
                        fromdate.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        mFromDatePickerDialog.dismiss();
                    }
                });
                mFromDatePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isdoneclick = false;
                        ((Button) findViewById(R.id.btnfrom)).setText("");
                        mFromDatePickerDialog.dismiss();
                    }
                });
                mFromDatePickerDialog.show();
            }
        });
        final DatePickerDialog.OnDateSetListener todate = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int month, int day) {
               // isdoneclick = true;

                if (isdoneclick) {
                    ((Button) findViewById(R.id.btnto)).setText(formatDate(year, month , day));
                    mTodateVal = formatDateServer(year, month , day);

                } else {
                    // Log.e("Clear", "Clear");
                    ((Button) findViewById(R.id.btnto)).setText("");
                    mTodateVal = "";
                }

            }

        };
        findViewById(R.id.btnto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btnselectdate).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                findViewById(R.id.btntomorrow).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                findViewById(R.id.btntoday).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                ((Button) findViewById(R.id.btnselectdate)).setText("DD-MM-YYYY");
                singleDate = "";
                todayPressed = false;
                datePressed = false;
                tomorrowPressed = false;
                final DatePickerDialog dpd = new DatePickerDialog(AdvanceSearchAct.this, todate, currentYear,
                        currentMonth, currentDay);
                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isdoneclick = true;
                        DatePicker datePicker = dpd.getDatePicker();
                        todate.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        dpd.dismiss();
                    }
                });
                dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isdoneclick = false;
                        ((Button) findViewById(R.id.btnto)).setText("");
                        dpd.dismiss();
                    }
                });
                dpd.show();
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        PreferenceStorage.saveFilterCitySelection(this, position);
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private static String formatDateServer(int year, int month, int day) {

            /*Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, day);
            Date date = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-mmm-yyyy");

            return sdf.format(date);*/
        String formattedDay = "", formattedMonth = "";
        month = month +1;
        if (day < 10) {
            formattedDay = "0" + day;
        } else {
            formattedDay = "" + day;
        }

        if (month < 10) {
            formattedMonth = "0" + month;
        } else {
            formattedMonth = "" + month;
        }

        return  year +"-" + formattedMonth + "-" +  formattedDay;
    }

    private static String formatDate(int year, int month, int day) {

            /*Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, day);
            Date date = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-mmm-yyyy");

            return sdf.format(date);*/
        String formattedDay = "", formattedMonth = "";
        month = month +1;
        if (day < 10) {
            formattedDay = "0" + day;
        } else {
            formattedDay = "" + day;
        }

        if (month < 10) {
            formattedMonth = "0" + month;
        } else {
            formattedMonth = "" + month;
        }

        return formattedDay +"-" + formattedMonth + "-" + year;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnselectdate:
                ((Button) findViewById(R.id.btnfrom)).setText("");
                ((Button) findViewById(R.id.btnto)).setText("");
                if (!datePressed) {
                    findViewById(R.id.btnselectdate).setBackgroundColor(getResources().getColor(R.color.orange));
                    findViewById(R.id.btntomorrow).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    findViewById(R.id.btntoday).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    final DatePickerDialog.OnDateSetListener singledate = new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int month, int day) {


                            //Log.e("Singledate", "singleDate : " + singleDate);
                            if (isdoneclick) {
                                ((Button) findViewById(R.id.btnselectdate)).setText(formatDate(year, month , day));
                                singleDate = formatDateServer(year, month , day);
                            } else {
                                Log.e("Clear", "Clear");
                                ((Button) findViewById(R.id.btnselectdate)).setText("DD-MM-YYYY");
                            }

                        }

                    };
                    final Calendar c2 = Calendar.getInstance();
                    final int currentYear2 = c2.get(Calendar.YEAR);
                    final int currentMonth2 = c2.get(Calendar.MONTH) ;
                    final int currentDay2 = (c2.get(Calendar.DAY_OF_MONTH));
                    final DatePickerDialog dpd = new DatePickerDialog(AdvanceSearchAct.this, singledate, currentYear2,
                            currentMonth2, currentDay2);
                    dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isdoneclick = true;
                            DatePicker datePicker = dpd.getDatePicker();
                            singledate.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            dpd.dismiss();
                        }
                    });
                    dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Clear", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isdoneclick = false;
                            dpd.dismiss();
                        }
                    });
                    dpd.show();

                    datePressed = true;

                } else {
                    findViewById(R.id.btnselectdate).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    findViewById(R.id.btntomorrow).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    findViewById(R.id.btntoday).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    singleDate = "";
                    datePressed = false;
                    todayPressed = false;
                    tomorrowPressed = false;
                }

                break;
            case R.id.btntomorrow:
                ((Button) findViewById(R.id.btnfrom)).setText("");
                ((Button) findViewById(R.id.btnto)).setText("");
                if (!tomorrowPressed) {

                    findViewById(R.id.btnselectdate).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    findViewById(R.id.btntomorrow).setBackgroundColor(getResources().getColor(R.color.orange));
                    findViewById(R.id.btntoday).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    // singleDate=((Button)findViewById(R.id.btntomorrow)).getText().toString();

                    final Calendar c = Calendar.getInstance();
                    final int currentYear = c.get(Calendar.YEAR);
                    final int currentMonth = c.get(Calendar.MONTH) ;
                    final int currentDay = (c.get(Calendar.DAY_OF_MONTH) );
                    singleDate = formatDateServer(currentYear, currentMonth, currentDay);

                    //  ((Button) findViewById(R.id.btnselectdate)).setText(singleDate);
                    Log.e("Singledate", "singleDate : " + singleDate);
                    tomorrowPressed = true;
                } else {

                    findViewById(R.id.btnselectdate).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    findViewById(R.id.btntomorrow).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    findViewById(R.id.btntoday).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    singleDate = "";
                    tomorrowPressed = false;
                    datePressed = false;
                    todayPressed = false;
                }
                ((Button) findViewById(R.id.btnselectdate)).setText("DD-MM-YYYY");
                break;
            case R.id.btntoday:
                ((Button) findViewById(R.id.btnfrom)).setText("");
                ((Button) findViewById(R.id.btnto)).setText("");
                ((Button) findViewById(R.id.btnselectdate)).setText("DD-MM-YYYY");
                if (!todayPressed) {
                    findViewById(R.id.btnselectdate).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    findViewById(R.id.btntomorrow).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    findViewById(R.id.btntoday).setBackgroundColor(getResources().getColor(R.color.orange));
                    final Calendar c1 = Calendar.getInstance();
                    final int currentYear1 = c1.get(Calendar.YEAR);
                    final int currentMonth1 = c1.get(Calendar.MONTH) ;
                    final int currentDay1 = (c1.get(Calendar.DAY_OF_MONTH));
                    singleDate = formatDateServer(currentYear1, currentMonth1, currentDay1);
                    // ((Button) findViewById(R.id.btnselectdate)).setText(singleDate);

                    Log.e("Singledate", "singleDate : " + singleDate);
                    todayPressed = true;
                } else {
                    findViewById(R.id.btnselectdate).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    findViewById(R.id.btntomorrow).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    findViewById(R.id.btntoday).setBackgroundColor(getResources().getColor(R.color.btngreybg));
                    singleDate = "";
                    todayPressed = false;
                    datePressed = false;
                    tomorrowPressed = false;
                }
                break;
            case R.id.btnapply:
                String city = spincity.getSelectedItem().toString();
                String catgry = spincat.getText().toString();
                String fromdate = ((Button) findViewById(R.id.btnfrom)).getText().toString();
                String todate = ((Button) findViewById(R.id.btnto)).getText().toString();
                if (!singleDate.equalsIgnoreCase("") && singleDate != null) {
                    PreferenceStorage.IsFilterApply(this, true);
                    PreferenceStorage.saveFilterSingleDate(this, singleDate);
                    //  Toast.makeText(this, "Filter applied", Toast.LENGTH_SHORT).show();
                    PreferenceStorage.saveFilterFromDate(this, "");
                    PreferenceStorage.saveFilterToDate(this, "");
                    if (!city.equalsIgnoreCase("Select Your City")) {
                        PreferenceStorage.saveFilterCity(this, city);
                    }
                    if (!catgry.equalsIgnoreCase("Select Category")) {
                        PreferenceStorage.saveFilterCatgry(this, catgry);
                    }

                    startActivity(new Intent(AdvanceSearchAct.this, AdvaSearchResAct.class));
                    finish();
                } else if (fromdate.trim().length() > 0 || todate.trim().length() > 0) {
                    singleDate = "";
                    PreferenceStorage.saveFilterSingleDate(this, singleDate);
                    PreferenceStorage.IsFilterApply(this, true);


                    if (fromdate.equalsIgnoreCase("")) {
                        Toast.makeText(this, "Select From Date", Toast.LENGTH_SHORT).show();
                    } else if (todate.equalsIgnoreCase("")) {
                        Toast.makeText(this, "Select To Date", Toast.LENGTH_SHORT).show();
                    } else {

                        PreferenceStorage.saveFilterFromDate(this, mFromDateVal);
                        PreferenceStorage.saveFilterToDate(this, mTodateVal);
                        if (!city.equalsIgnoreCase("Select Your City")) {
                            PreferenceStorage.saveFilterCity(this, city);
                        }
                        if (!catgry.equalsIgnoreCase("Select Category")) {
                            PreferenceStorage.saveFilterCatgry(this, catgry);
                        }
                        startActivity(new Intent(AdvanceSearchAct.this, AdvaSearchResAct.class));
                        finish();
                    }

                } else if (!city.equalsIgnoreCase("Select Your City") || !catgry.equalsIgnoreCase("Select Category")) {
                    PreferenceStorage.IsFilterApply(this, true);
                    singleDate = "";
                    PreferenceStorage.saveFilterSingleDate(this, singleDate);
                    if (!city.equalsIgnoreCase("Select Your City")) {
                        PreferenceStorage.saveFilterCity(this, city);
                    }
                    if (!catgry.equalsIgnoreCase("Select Category")) {
                        PreferenceStorage.saveFilterCatgry(this, catgry);
                    }
                    startActivity(new Intent(AdvanceSearchAct.this, AdvaSearchResAct.class));
                    finish();
                } else {
                    Toast.makeText(AdvanceSearchAct.this, "select any criteria", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btncancel:
                finish();
                break;
            case R.id.catgoryspinner:
                AlertDialog.Builder builder = new AlertDialog.Builder(AdvanceSearchAct.this);
                sb = new StringBuilder();
                // String array for alert dialog multi choice items

                final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Select Category")
                    .setAdapter(categoryAdapter, null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //fetch all the selected category'
                            int ival =0;
                            for(Integer i: mSelectedCategoryList){
                                String name = categoryArrayList.get(i);
                                if(ival == 0) {
                                    sb = sb.append( name);
                                }else{
                                    sb = sb.append("," + name);
                                }
                                ival++;
                            }
                            spincat.setText(sb.toString());
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();

                dialog.getListView().setItemsCanFocus(false);
                dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Log.d(TAG, "Item clicked");
                        // Manage selected items here//*System.out.println("clicked" + position);
                        /*CheckedTextView textView = (CheckedTextView) view;
                        if(textView.isChecked()) {
                            Log.d(TAG,"checked state is"+ textView.isChecked());

                        } else {

                        }*/
                    }
                });

                dialog.show();


                /*// Convert the color array to list
                final List<String> colorsList = Arrays.asList(categoryarray);


                builder.setMultiChoiceItems(categoryarray, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        // Update the current focused item's checked status
                        isSelectedArray[which] = isChecked;

                        // Get the current focused item
                        String currentItem = colorsList.get(which);

                        // Notify the current action

                    }
                });*/

               /* // Specify the dialog is not cancelable
                builder.setCancelable(false);

                // Set a title for alert dialog
                builder.setTitle("Select Category");

                // Set the positive/yes button click listener
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click positive button
                        for (int i = 0; i < isSelectedArray.length; i++) {
                            boolean checked = isSelectedArray[i];
                            if (checked) {
                                sb = sb.append("," + categoryarray[i]);
                            }
                        }
                        sb = sb.deleteCharAt(0);
                        spincat.setText(sb.toString());
                    }
                });

                // Set the negative/no button click listener
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click the negative button
                        dialog.dismiss();
                    }
                });


                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();*/

                break;


        }

    }

    @Override
    public void onCategoryResponse(JSONArray response) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Category>>() {
        }.getType();
        ArrayList<Category> arrayList = gson.fromJson(response.toString(), listType);
        categoryArrayList.clear();
        mSelectedCategoryList.clear();
       // isSelectedArray.clear();
        for(Category category: arrayList){
            categoryArrayList.add(category.getCategory());
            //isSelectedArray.add(false);
        }
        //dataAdapter1.notifyDataSetChanged();
    }

    @Override
    public void onCategoryError(String error) {

    }

    @Override
    public void onSetCategoryResponse(JSONObject response) {

    }

    @Override
    public void onSetCategoryError(String error) {

    }
}
