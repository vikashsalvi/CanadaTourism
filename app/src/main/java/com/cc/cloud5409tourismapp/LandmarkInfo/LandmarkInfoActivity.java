package com.cc.cloud5409tourismapp.LandmarkInfo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.amazonaws.mobileconnectors.cognitoauth.AuthUserSession;
import com.amazonaws.mobileconnectors.cognitoauth.handlers.AuthHandler;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cc.cloud5409tourismapp.MainActivity;
import com.cc.cloud5409tourismapp.R;
import com.cc.cloud5409tourismapp.RequestQueueApiSingleton;
import com.cc.cloud5409tourismapp.TicketBooking.TicketBookingActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

public class LandmarkInfoActivity extends AppCompatActivity {

    Button ticketBooking;
    private Auth auth;
    private Uri reDirectAWS;
    ImageView location_image;
    TextView name;
    TextView description;
    // Description Microservice
    String url = "http://192.168.0.72:5050/description/";
    String encrypt_url = "http://192.168.0.72:5005/encrypt";
    String decrypt_url = "http://192.168.0.72:5005/decrypt";
    public String encrypt_text;
    public String decrypt_text;
    // URL for Amazon S3 Bucket
    String s3bucketUrl = "https://cloud-5409-tourism-app-resources.s3.amazonaws.com/";
    private static final String TAG = "Cloud5409AuthCognito";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_info);
        initCognitoAuth();
        location_image = findViewById(R.id.place_image);
        name = findViewById(R.id.search_place_name);
        description = findViewById(R.id.place_description);
        ticketBooking = findViewById(R.id.proceed_booking);
        ticketBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getSession();
            }
        });

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                if(intent.getStringExtra("place_id") != null) {
                    final String place_id = intent.getStringExtra("place_id");
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("text", place_id);
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
                                            fetchLandMarkInfo(decrypt_text);
                                            loadImage(decrypt_text);
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
        };
        Thread get_request = new Thread(null,runnable, "background");
        get_request.start();
    }


    private void fetchLandMarkInfo(final String query) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + query, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                System.out.println("Successful Response: " + response);
                synchronized (this) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println(url + query);
                                String[] state_string = response.get("city").toString().split(" ");
                                if(state_string.length == 1){
                                    String place = response.get("name").toString() + ", " + state_string[0].substring(0,1).toUpperCase() + state_string[0].substring(1);
                                    name.setText(place);
                                } else {
                                    String place = response.get("name").toString() + ", " + state_string[0].substring(0,1).toUpperCase() + state_string[0].substring(1)
                                            + " " + state_string[1].substring(0,1).toUpperCase() + state_string[1].substring(1);
                                    name.setText(place);
                                }
                                description.setText(response.get("description").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("No Response from Description: "+ error);
            }
        });

        RequestQueueApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void loadImage(String query){
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(getApplicationContext())
                .load(s3bucketUrl + (query + ".jpeg"))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(location_image);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent in = getIntent();
        if(in.getStringExtra("sign_out_message") != null){
            String message = in.getStringExtra("sign_out_message");
            if(message != null) {
                this.auth.signOut();
            }
        }
        else {
            Intent activityIntent = getIntent();
            if (activityIntent.getData() != null &&
                    reDirectAWS.getHost().equals(activityIntent.getData().getHost())) {
                auth.getTokens(activityIntent.getData());
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("back_to_main_screen", true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    void initCognitoAuth() {
        Auth.Builder builder = new Auth.Builder().setAppClientId(getString(R.string.cognito_client_id))
                .setAppClientSecret(getString(R.string.cognito_client_secret))
                .setAppCognitoWebDomain(getString(R.string.cognito_web_domain))
                .setApplicationContext(getApplicationContext())
                .setAuthHandler(new callback())
                .setSignInRedirect(getString(R.string.app_redirect))
                .setSignOutRedirect(getString(R.string.app_redirect));
        this.auth = builder.build();
        reDirectAWS = Uri.parse(getString(R.string.app_redirect));
    }



    class callback implements AuthHandler {

        @Override
        public void onSuccess(AuthUserSession authUserSession) {
            Intent intent = new Intent(getApplicationContext(), TicketBookingActivity.class);
            Bundle b = new Bundle();
            b.putString("app_access_token", authUserSession.getAccessToken().getJWTToken());
            b.putString("app_id_token", authUserSession.getIdToken().getJWTToken());
            b.putString("user_name", authUserSession.getUsername());
            intent.putExtras(b);
            startActivity(intent);
        }

        @Override
        public void onSignout() {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        @Override
        public void onFailure(Exception e) {
            Log.e(TAG, "Failed to auth", e);
        }
    }

}
