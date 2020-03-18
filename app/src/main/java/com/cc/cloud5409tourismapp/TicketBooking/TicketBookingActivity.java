package com.cc.cloud5409tourismapp.TicketBooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.cc.cloud5409tourismapp.LandmarkInfo.LandmarkInfoActivity;
import com.cc.cloud5409tourismapp.R;

public class TicketBookingActivity extends AppCompatActivity {

    Button signOutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_booking);

        Intent intent = getIntent();
        String access_token = intent.getStringExtra("app_access_token");
        String app_id_token = intent.getStringExtra("app_id_token");
        String user_name = intent.getStringExtra("user_name");
        System.out.println("ACCESS_TOKEN: " + access_token);
        System.out.println("APP_ID_TOKEN: " + app_id_token);
        System.out.println("USER_NAME " + user_name);

        signOutButton = findViewById(R.id.sign_out);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LandmarkInfoActivity.class);
                intent.putExtra("sign_out_message", "SignOut");
                startActivity(intent);
            }
        });
    }
}
