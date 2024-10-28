package com.example.brickbreaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GameOverListener {
    private GameView gameView;
    private Button pauseButton;
    private Button resumeButton;
    private Button restartButton;
    private Button btnLogout;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", null);

        if (userEmail == null) {
            // User is not logged in, redirect to login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // User is logged in, proceed with main activity
            setContentView(R.layout.activity_main);

            gameView = findViewById(R.id.game_view);
            apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
            gameView.setApiService(apiService);
            gameView.setGameOverListener(this);

            pauseButton = findViewById(R.id.button_pause);
            resumeButton = findViewById(R.id.button_resume);
            restartButton = findViewById(R.id.button_restart);
            btnLogout = findViewById(R.id.btnLogout);

            // Hide the logout button
            btnLogout.setVisibility(View.GONE);

            pauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameView.pauseGame();
                }
            });

            resumeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameView.resumeGame();
                }
            });

            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameView.restartGame();
                    restartButton.setVisibility(View.GONE); // Hide the button after restarting
                }
            });
        }
    }

    @Override
    public void onGameOver() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                restartButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
