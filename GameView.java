package com.example.brickbreaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class GameView extends View {
    private int healthPoints;
    private Paint paint;
    private RectF paddle;
    private RectF ball;
    private float ballXVelocity;
    private float ballYVelocity;
    private List<RectF> bricks;
    private int score;
    private boolean gameOver;
    private boolean paused;
    private List<Level> levels;
    private int currentLevel;
    private int remainingBricks;
    private GameOverListener gameOverListener;
    private ApiService apiService;
    private static final String TAG = "GameView";

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public GameView(Context context) {
        super(context);
        initialize(context);
    }

    public void initialize(Context context) {
        paint = new Paint();
        paddle = new RectF(400, 1000, 600, 1020);
        ball = new RectF(450, 780, 500, 830);
        ballXVelocity = 5;
        ballYVelocity = -5;
        score = 0;
        healthPoints = 3;
        gameOver = false;
        paused = false;
        initLevels();
        currentLevel = 0;
        loadLevel(currentLevel);
    }

    private class Level {
        private int numBricks;
        private int ballSpeed;
        public Level(int numBricks, int ballSpeed) {
            this.numBricks = numBricks;
            this.ballSpeed = ballSpeed;
        }
        public int getNumBricks() {
            return numBricks;
        }
        public int getBallSpeed() {
            return ballSpeed;
        }
    }

    private void initLevels() {
        levels = new ArrayList<>();
        levels.add(new Level(15, 5)); // Level 1
        levels.add(new Level(20, 6)); // Level 2
        levels.add(new Level(25, 7)); // Level 3
    }

    private void loadLevel(int levelIndex) {
        Level level = levels.get(levelIndex % levels.size()); // Loop levels
        ballXVelocity = level.getBallSpeed() + (levelIndex / levels.size()); // Increase difficulty
        ballYVelocity = -(level.getBallSpeed() + (levelIndex / levels.size())); // Increase difficulty
        remainingBricks = level.getNumBricks();
        bricks = new ArrayList<>();

        int brickWidth = 100; // Width of each brick
        int brickHeight = 50; // Height of each brick
        int numRows = 5; // Number of rows
        int numCols = (remainingBricks + numRows - 1) / numRows; // Calculate number of columns
        int brickPadding = 10; // Padding between bricks

        // Calculate offset to center the bricks
        int totalBrickWidth = (brickWidth + brickPadding) * numCols - brickPadding;
        int offsetX = (getWidth() - totalBrickWidth) / 2;
        int offsetY = 100; // Starting Y position for bricks

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (i * numCols + j < remainingBricks) {
                    float left = offsetX + j * (brickWidth + brickPadding);
                    float top = offsetY + i * (brickHeight + brickPadding);
                    float right = left + brickWidth;
                    float bottom = top + brickHeight;
                    bricks.add(new RectF(left, top, right, bottom));
                }
            }
        }
    }






    private void saveScore(String userEmail, int score) {
        Log.d(TAG, "Saving score for: " + userEmail + " Score: " + score);
        if (apiService != null) {
            apiService.saveScore(userEmail, score).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d(TAG, "Response: " + response.message());
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Score saved successfully");
                    } else {
                        Log.d(TAG, "Failed to save score");
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Error: " + t.getMessage());
                }
            });
        } else {
            Log.d(TAG, "ApiService is null");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        if (gameOver) {
            paint.setColor(Color.RED);
            paint.setTextSize(100);
            canvas.drawText("Game Over", 300, 400, paint);
            paint.setTextSize(50);
            canvas.drawText("Score: " + score, 350, 500, paint);
            if (gameOverListener != null) {
                gameOverListener.onGameOver();
            }
            saveScore("user@example.com", score); // Replace with actual user email
            return;
        }
        if (paused) {
            paint.setColor(Color.YELLOW);
            paint.setTextSize(100);
            canvas.drawText("Paused", 350, 400, paint);
            return;
        }
        paint.setColor(Color.WHITE);
        canvas.drawRect(paddle, paint);
        paint.setColor(Color.RED);
        canvas.drawOval(ball, paint);
        paint.setColor(Color.GREEN);
        for (RectF brick : bricks) {
            canvas.drawRect(brick, paint);
        }
        paint.setColor(Color.YELLOW);
        paint.setTextSize(50);
        canvas.drawText("BrickPoints: " + score, 40, 50, paint);
        canvas.drawText("Chances: " + healthPoints, 800, 50, paint);  // Display health points

        ball.left += ballXVelocity;
        ball.right += ballXVelocity;
        ball.top += ballYVelocity;
        ball.bottom += ballYVelocity;

        if ((ball.left <= 0) || (ball.right >= getWidth())) {
            ballXVelocity = -ballXVelocity;
        }
        if (ball.top <= 0) {
            ballYVelocity = -ballYVelocity;
        }
        if (ball.bottom >= getHeight()) {
            healthPoints--;
            if (healthPoints <= 0) {
                gameOver = true;
                invalidate();
                return;
            } else {
                // Reset ball position and continue
                ball.left = paddle.left + 50;
                ball.right = paddle.right - 50;
                ball.top = paddle.top - 50;
                ball.bottom = paddle.top;
                ballYVelocity = -ballYVelocity;
            }
        }
        if (RectF.intersects(paddle, ball)) {
            ballYVelocity = -ballYVelocity;
            ball.bottom = paddle.top;
            ball.top = ball.bottom - 50;
        }
        for (int i = 0; i < bricks.size(); i++) {
            if (RectF.intersects(bricks.get(i), ball)) {
                ballYVelocity = -ballYVelocity;
                bricks.remove(i);
                score += 10;
                Log.d(TAG, "Score after hitting brick: " + score);
                remainingBricks--;
                if (remainingBricks == 0) {
                    currentLevel++;
                    loadLevel(currentLevel); // Loop levels and increase difficulty
                }
                break;
            }
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE && !paused) {
            paddle.left = event.getX() - 100;
            paddle.right = event.getX() + 100;
            invalidate();
        }
        return true;
    }

    public void pauseGame() {
        paused = true;
        invalidate();
    }

    public void resumeGame() {
        paused = false;
        invalidate();
    }

    public void restartGame() {
        initialize(getContext());
        invalidate();
    }
}
