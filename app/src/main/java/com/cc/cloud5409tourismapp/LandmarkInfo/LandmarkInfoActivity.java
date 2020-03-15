package com.cc.cloud5409tourismapp.LandmarkInfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.amazonaws.mobileconnectors.cognitoauth.AuthUserSession;
import com.amazonaws.mobileconnectors.cognitoauth.handlers.AuthHandler;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cc.cloud5409tourismapp.R;
import com.cc.cloud5409tourismapp.RequestQueueApiSingleton;
import com.cc.cloud5409tourismapp.TicketBooking.TicketBookingActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LandmarkInfoActivity extends AppCompatActivity {

    Button ticketBooking;
    private Auth auth;
    private Uri reDirectAWS;
    ImageView location_image;
    TextView name;
    TextView description;
    // Description Microservice
    String url = "http://192.168.0.53:5050/description/";
    private static final String TAG = "Cloud5409AuthCognito";

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

        Intent in = getIntent();
        String message = in.getStringExtra("sign_out_message");
        if(message != null) {
            this.auth.signOut();
        }


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String place_id = getIntent().getStringExtra("place_id");
                System.out.println(url + place_id);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + place_id, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        synchronized (this) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        name.setText(response.get("name").toString());
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
                        System.out.println(error);
                    }
                });

                RequestQueueApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
            }
        };
        Thread get_request = new Thread(null,runnable, "background");
        get_request.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent activityIntent = getIntent();
        if (activityIntent.getData() != null &&
                reDirectAWS.getHost().equals(activityIntent.getData().getHost())) {
            auth.getTokens(activityIntent.getData());
        }
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
            intent.putExtras(b);
            startActivity(intent);
        }

        @Override
        public void onSignout() {

        }

        @Override
        public void onFailure(Exception e) {
            Log.e(TAG, "Failed to auth", e);
        }
    }

}
