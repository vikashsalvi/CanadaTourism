package com.cc.cloud5409tourismapp.TicketBooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.cc.cloud5409tourismapp.LandmarkInfo.LandmarkInfoActivity;
import com.cc.cloud5409tourismapp.R;

public class TicketBookingActivity extends AppCompatActivity {

    Button signOutButton;
    Auth cognitoAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_booking);

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
