package com.cc.cloud5409tourismapp.TicketBooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.braintreepayments.cardform.view.CardForm;
import com.cc.cloud5409tourismapp.LandmarkInfo.LandmarkInfoActivity;
import com.cc.cloud5409tourismapp.R;
import com.cc.cloud5409tourismapp.RequestQueueApiSingleton;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TicketBookingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,LocationListener{

    CardForm cardForm;
    Button buy;
    EditText source;
    EditText destination;
    TextView cost;
    AlertDialog.Builder alertBuilder;
    private LocationManager locationManager;
    private double longitude;
    private double latitude;
    private String provider;
    private Location location;
    public static String text;
    String url = "http://d7cd37f1.ngrok.io/download/pdf/Bus_ticket.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_booking);
        source = (EditText) findViewById(R.id.editText7);
        cost = (TextView) findViewById(R.id.textView6);
        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        cardForm = findViewById(R.id.card_form);
        buy = findViewById(R.id.btnBuy);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .setup(TicketBookingActivity.this);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria c = new Criteria();
        provider = locationManager.getBestProvider(c,false);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //location = locationManager.getLastKnownLocation(provider);
        //System.out.println("here"+location);
        //onLocationChanged(location);
        //loc_func(location);

        Intent intent = getIntent();
        String access_token = intent.getStringExtra("app_access_token");
        String app_id_token = intent.getStringExtra("app_id_token");
        final String user_name = intent.getStringExtra("user_name");
        String to = intent.getStringExtra("dest");
        System.out.println("ACCESS_TOKEN: " + access_token);
        System.out.println("APP_ID_TOKEN: " + app_id_token);
        System.out.println("USER_NAME " + user_name);
        destination = (EditText) findViewById(R.id.editText9);
        destination.setText(to);
        destination.setEnabled(false);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardForm.isValid()) {
                    alertBuilder = new AlertDialog.Builder(TicketBookingActivity.this);
                    alertBuilder.setTitle("Confirm before purchase");
                    alertBuilder.setMessage("Card number: " + cardForm.getCardNumber() + "\n" +
                            "Card expiry date: " + cardForm.getExpirationDateEditText().getText().toString() + "\n" +
                            "Card CVV: " + cardForm.getCvv());
                    alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            //call API and set flag
                                System.out.println("Button clicked");
                                //download pdf
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("book_date", "30-3-2020");
                                params.put("name", user_name);
                                params.put("from", source.getText().toString());
                                params.put("to", destination.getText().toString());
                                params.put("dep_date", "29-03-17");
                                params.put("nseats", text);
                                params.put("cost", cost.getText().toString());
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(TicketBookingActivity.this, "Thank you for purchase", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(TicketBookingActivity.this, "Ticket Details: " + response.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                RequestQueueApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                        }
                    });
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();

                } else {
                    Toast.makeText(TicketBookingActivity.this, "Please complete the form", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        text = parent.getItemAtPosition(position).toString();
        cost.setText(String.valueOf(Integer.parseInt(text)*10) + "$");
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onLocationChanged(Location location){
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        System.out.println(latitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle){

    }

    @Override
    public void onProviderEnabled(String s){


    }
    @Override
    public  void onProviderDisabled(String s){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_sign_out) {
            Intent intent = new Intent(getApplicationContext(), LandmarkInfoActivity.class);
            intent.putExtra("sign_out_message", "SignOut");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loc_func(Location location){
        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(latitude,longitude,1);
            String country = addresses.get(0).getCountryName();
            String city = addresses.get(0).getLocality();
            System.out.println(country+","+city);
            //source.setText(city+","+country);
            //source.setEnabled(false);
        }catch(IOException e){
            e.printStackTrace();

        }

    }
}
