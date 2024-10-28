package com.example.brickbreaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {
    private TextView loggedInUserTextView;
    private Button btnLogout;
    private SharedPreferences sharedPreferences;

    private void updateLoggedInUserIndicator() {
        String userEmail = sharedPreferences.getString("user_email", null);
        if (userEmail != null) {
            loggedInUserTextView.setText("Logged in as: " + userEmail);
        } else {
            loggedInUserTextView.setText("Not logged in");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        loggedInUserTextView = findViewById(R.id.loggedInUserTextView);
        btnLogout = findViewById(R.id.btnLogout);

        updateLoggedInUserIndicator();  // Initialize the indicator

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the user session
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Update indicator
                updateLoggedInUserIndicator();

                // Redirect to login activity
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        Button buttonLogin = findViewById(R.id.button_login);
        Button buttonStart = findViewById(R.id.button_start);
        Button buttonLeaderboards = findViewById(R.id.button_leaderboards);
        Button buttonExit = findViewById(R.id.button_exit);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonLeaderboards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch LeaderboardActivity
                Intent intent = new Intent(MainMenuActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
