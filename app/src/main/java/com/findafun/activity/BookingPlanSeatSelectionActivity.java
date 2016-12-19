package com.findafun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.bean.events.BookPlan;
import com.findafun.ccavactivity.InitialScreenActivity;

/**
 * Created by Nandha on 11-12-2016.
 */

public class BookingPlanSeatSelectionActivity extends AppCompatActivity {

    private String eventName, eventVenue;
    private static final String TAG = "BookingPlanSeatSelectionActivity";
    private TextView txtEventName, txtEvnetVenue, txtEventPay, txtCountTicket;
    private ImageView imgPlus, imgMinus;
    private Button btnPay;
    private BookPlan bookPlan;
    String count;
    int _count = 0;
    double rate = 0.0;
    int pay = 0;
    double _pay=0.00;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_plan_seat_selection);
        txtEventName = (TextView) findViewById(R.id.event_name);
        txtEvnetVenue = (TextView) findViewById(R.id.event_venue);
        txtEventPay = (TextView) findViewById(R.id.event_pay_amount);
        txtCountTicket = (TextView) findViewById(R.id.no_tickets);
        imgPlus = (ImageView) findViewById(R.id.count_increase);
        imgMinus = (ImageView) findViewById(R.id.count_decrease);
        btnPay = (Button) findViewById(R.id.pay);

        bookPlan = (BookPlan) getIntent().getSerializableExtra("planObj");
        eventName = getIntent().getStringExtra("eventName");
        eventVenue = getIntent().getStringExtra("eventVenue");
        String _rate = bookPlan.getSeatRate();
        rate = Double.parseDouble(_rate);


        findViewById(R.id.back_res).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtEventName.setText(eventName);
        txtEvnetVenue.setText(eventVenue);
        txtEventPay.setText(bookPlan.getSeatPlan() + " - Rs : " + bookPlan.getSeatRate());

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = txtCountTicket.getText().toString();
                _count = Integer.parseInt(count);
                _count = _count + 1;
                txtCountTicket.setText(""+_count);

                int i = (int) rate;
//                int pay = 0;
                pay = (i * _count);
                _pay = (double)pay;

                if (_count >= 1) {
                    imgMinus.setEnabled(true);
                    btnPay.setVisibility(View.VISIBLE);
                }
                btnPay.setText("Pay - " + pay);
            }
        });

        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = txtCountTicket.getText().toString();
                _count = Integer.parseInt(count);
                _count = _count - 1;
                txtCountTicket.setText(""+_count);

                int i = (int) rate;
//                int pay = 0;
                pay = (i * _count);
                _pay = (double)pay;

                if (_count <= 0) {
                    imgMinus.setEnabled(false);
                    btnPay.setVisibility(View.GONE);
                }

                btnPay.setText("Pay - " + pay);
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InitialScreenActivity.class);
                intent.putExtra("planObj", bookPlan);
                intent.putExtra("eventName", eventName);
                intent.putExtra("eventVenue", eventVenue);
                Bundle b = new Bundle();
                b.putDouble("eventRate", _pay);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

    }
}
