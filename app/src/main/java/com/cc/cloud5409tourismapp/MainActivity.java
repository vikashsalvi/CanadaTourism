package com.cc.cloud5409tourismapp;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageButton;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cc.cloud5409tourismapp.Cards.Card;
import com.cc.cloud5409tourismapp.Cards.CardAdapter;
import com.cc.cloud5409tourismapp.LandmarkInfo.LandmarkInfoActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    private RequestQueue queue;
    public static final int TRIGGER_AUTO_SEARCH_COMPLETE = 100;
    public static final long AUTO_COMPLETE_DELAY = 300;
    private Auth auth;
    private Handler handler;
    private AutoCompleteTextViewAdapter autoCompleteTextViewAdapter;
    private ImageButton cancel_button;
    public String encrypt_text;
    public String decrypt_text;
    public String decoded_text;
    ArrayList<HashMap<String,String>> searchPlacelist;

    RecyclerView mRecyclerView;
    List<Card> cardList;
    Card cardData;

    // Search Microservice
    String url = "http://192.168.0.72:5000/search/";
    String encrypt_url = "http://192.168.0.72:5005/encrypt";
    String decrypt_url = "http://192.168.0.72:5005/decrypt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cancel_button = findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteTextView.setText("");
            }
        });
        autoCompleteTextView = findViewById(R.id.search_auto_complete_textView);
        autoCompleteTextViewAdapter = new AutoCompleteTextViewAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(autoCompleteTextViewAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Passing data to Landmark info activity
                String id = autoCompleteTextViewAdapter.getObject(i).get("id");
                String location = autoCompleteTextViewAdapter.getObject(i).get("location");
                Intent intent = new Intent(getApplicationContext(), LandmarkInfoActivity.class);
                intent.putExtra("place_id", id);
                intent.putExtra("location", location);
                startActivity(intent);
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
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("text", autoCompleteTextView.getText().toString());
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, encrypt_url, new JSONObject(params), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    encrypt_text = response.getString("output");
                                    HashMap<String, String> decryptParams = new HashMap<String, String>();
                                    decryptParams.put("text", encrypt_text);
                                    JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, decrypt_url, new JSONObject(decryptParams), new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                decrypt_text = response.getString("output");
                                                FetchSearchResults(decrypt_text);
                                            } catch (JSONException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                        }
                                    });
                                    RequestQueueApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                        RequestQueueApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
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
                searchPlacelist = new ArrayList<HashMap<String, String>>();
                try {
                    for(int i=0; i< response.length();i++){
                        String[] info = response.getString(i).split("--__--");
                        String location = info[0];
                        String id = info[1];
                        HashMap<String,String> searchList = new HashMap<String, String>();
                        searchList.put("location", location);
                        searchList.put("id", id);
                        searchPlacelist.add(searchList);
                        places.add(location);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                autoCompleteTextViewAdapter.setData(searchPlacelist);
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
    @Override
    protected void onStart() {
        super.onStart();
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url + "ga", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                cardList = new ArrayList<>();
                try {
                    for(int i=0; i< response.length();i++){
                        String[] info = response.getString(i).split("--__--");
                        String location = info[0];
                        String id = info[1];
                        cardData = new Card(location, id);
                        cardList.add(cardData);
                    }
                    CardAdapter myAdapter = new CardAdapter(MainActivity.this, cardList);
                    mRecyclerView.setAdapter(myAdapter);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print(error);
            }
        });
        RequestQueueApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

}
