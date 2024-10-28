package com.example.brickbreaker;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.Gravity;  // Import this
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardActivity extends AppCompatActivity {
    private TextView leaderboardTextView;
    private ApiService apiService;
    private static final String TAG = "LeaderboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Log.d(TAG, "onCreate called");

        leaderboardTextView = findViewById(R.id.leaderboardTextView);
        leaderboardTextView.setGravity(Gravity.CENTER);  // Center-align text
        Log.d(TAG, "Leaderboard TextView initialized");

        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        fetchLeaderboard();
    }

    private void fetchLeaderboard() {
        Log.d(TAG, "Fetching leaderboard");
        apiService.getLeaderboard().enqueue(new Callback<List<Score>>() {
            @Override
            public void onResponse(Call<List<Score>> call, Response<List<Score>> response) {
                Log.d(TAG, "API response received");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Successful response, displaying leaderboard");
                    displayLeaderboard(response.body());
                } else {
                    Log.d(TAG, "Failed to fetch leaderboard");
                    leaderboardTextView.setText("Failed to fetch leaderboard.");
                }
            }

            @Override
            public void onFailure(Call<List<Score>> call, Throwable t) {
                Log.d(TAG, "Error in API call: " + t.getMessage());
                leaderboardTextView.setText("Error: " + t.getMessage());
            }
        });
    }

    private void displayLeaderboard(List<Score> leaderboard) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%-5s  %-15s  %-5s\n\n", "POS", "NAME", "SCORE"));
        for (int i = 0; i < leaderboard.size(); i++) {
            Score score = leaderboard.get(i);
            builder.append(String.format("%-5d  %-15s  %-5d\n", i + 1, score.getUserEmail() != null ? score.getUserEmail().trim() : "No Email", score.getScore()));
        }
        leaderboardTextView.setText(builder.toString());
    }
}
