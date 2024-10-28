package com.example.brickbreaker;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Field;

public interface ApiService {
    @FormUrlEncoded
    @POST("register.php")
    Call<Void> registerUser(
            @Field("user_name") String userName,
            @Field("user_email") String userEmail,
            @Field("user_password") String userPassword
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<String> loginUser(
            @Field("user_email") String userEmail,
            @Field("user_password") String userPassword
    );

    @FormUrlEncoded
    @POST("save_score.php")
    Call<Void> saveScore(
            @Field("user_email") String userEmail,
            @Field("score") int score
    );

    @GET("get_leaderboard.php")
    Call<List<Score>> getLeaderboard();
}
