package com.cc.cloud5409tourismapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cc.cloud5409tourismapp.LandmarkInfo.LandmarkInfoActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    private RequestQueue queue;
    public static final int TRIGGER_AUTO_SEARCH_COMPLETE = 100;
    public static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoCompleteTextViewAdapter autoCompleteTextViewAdapter;
    // Search Microservice
    String url = "http://192.168.0.49:5000/search/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoCompleteTextView = findViewById(R.id.search_auto_complete_textView);
        autoCompleteTextViewAdapter = new AutoCompleteTextViewAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(autoCompleteTextViewAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Passing data to Landmark info activity
                String[] word = autoCompleteTextViewAdapter.getObject(i).split(",");
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url + word[0], null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0; i< response.length();i++){
                                String[] info = response.getString(i).split("--__--");
                                String id = info[1];
                                Intent intent = new Intent(getApplicationContext(), LandmarkInfoActivity.class);
                                intent.putExtra("place_id", id);
                                startActivity(intent);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });
                RequestQueueApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeMessages(TRIGGER_AUTO_SEARCH_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_SEARCH_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what == TRIGGER_AUTO_SEARCH_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        FetchSearchResults(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });
    }

    private void FetchSearchResults(String query) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url + query, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<String> places = new ArrayList<>();
                try {
                    for(int i=0; i< response.length();i++){
                        String[] info = response.getString(i).split("--__--");
                        String location = info[0];
                        places.add(location);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                autoCompleteTextViewAdapter.setData(places);
                autoCompleteTextViewAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        RequestQueueApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }


}
