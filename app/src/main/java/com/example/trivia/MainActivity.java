package com.example.trivia;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.util.Prefs;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // initialize variables
    private TextView questionTextView;
    private TextView questionCounterTextView;
    private TextView scoreText;
    private TextView highScoreText;
    private Button trueButton;
    private Button falseButton;
    private Button resetHiScoreBtn;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String highScoreKeeper = "";
    private String questionCounter = "";
    private String scoreCounter = "";
    private List<Question> questionList;

    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new Prefs(MainActivity.this);

        // combine variables with views from the layout
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        resetHiScoreBtn = findViewById(R.id.reset_hi_score_btn);
        scoreText = findViewById(R.id.score_text);
        highScoreText = findViewById(R.id.high_score_text);
        questionCounterTextView = findViewById(R.id.counter_text);
        questionTextView = findViewById(R.id.question_text);

        // setting on-click listeners for the buttons
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        resetHiScoreBtn.setOnClickListener(this);

        // Sets the question index to the saved index, if there is one.
        // If there isn't, the default is 0.
        currentQuestionIndex = prefs.getState();

        // Sets visible text based on the currentQuestionIndex
        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounter = "Question: " + (currentQuestionIndex + 1) + " / " + questionArrayList.size();
                questionCounterTextView.setText(questionCounter);

                scoreCounter = "Current Score: " + score + " pts";
                scoreText.setText(scoreCounter);
            }
        });

        // Get data back from Shared Preferences for High Score
        setHighScoreText();



    }

    private void setHighScoreText() {
        highScoreKeeper = "High Score: " + prefs.getHighScore() + " pts";
        highScoreText.setText(highScoreKeeper);
    }

    @Override
    public void onClick(View view) {

        // Performs different functions based on what button is pressed
        switch (view.getId()) {
            case R.id.prev_button:
                // Decrements the current question index
                currentQuestionIndex = (currentQuestionIndex + (questionList.size() - 1)) % questionList.size();
                // Updates the question using the new current question index
                updateQuestion();
                break;
            case R.id.next_button:
                goNext();
                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;
            case R.id.reset_hi_score_btn:
                showMessage();
        }
    }

    // Shows an alert dialog if the user presses the "Reset High Score" button.
    // User must confirm that they want to reset the high score.
    private void showMessage() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Are you sure you want to reset the high score? This action cannot be undone!");

        // Pushing "YES" will reset the high score in memory and display the reset high score
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                prefs.resetHighScore(score);
                prefs.getHighScore();
                setHighScoreText();
                dialogInterface.dismiss();
            }
        });

        // Pushing "NO" will just dismiss the alert dialog
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }


    private void goNext() {
        // Increments the currentQuestionIndex
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        // Updates the current question using the new currentQuestionIndex
        updateQuestion();
    }

    private void checkAnswer(boolean userChoice) {
        // Checks whether the answer to the question is "true" or "false"
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();

        if (userChoice == answerIsTrue) {
            // Adds 10 points to the current score
            score += 10;

            // Saves the new high score if the current score is higher than the saved high score
            prefs.saveHighScore(score);

            // Applies fading animation
            fadeView();
        } else {
            // Subtracts 10 points only if the current score does NOT equal 0
            if (score > 0) {
                score -= 10;
            }

            // Saves the new high score if the current score is higher than the saved high score
            prefs.saveHighScore(score);

            // Applies shake animation
            shakeAnimation();
        }
        updateScore();
    }

    private void updateScore() {
        // Sets the new score text
        scoreCounter = "Current Score: " + score + " pts";
        scoreText.setText(scoreCounter);

        // Sets the new high score text, if necessary
        setHighScoreText();

    }

    private void updateQuestion() {
        questionTextView.setText(questionList.get(currentQuestionIndex).getAnswer());
        questionCounter = "Question: " + (currentQuestionIndex + 1) + " / " + questionList.size();
        questionCounterTextView.setText(questionCounter);
    }

    // Fading animation
    private void fadeView() {
        // Combines variable with view from the layout
        final CardView cardView = findViewById(R.id.cardView);

        // Creates the fading animation
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(200);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        // Applies the fading animation to the card view
        cardView.setAnimation(alphaAnimation);

        // Applies an animation listener to the created animation
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // At the start of the animation, the card's background is set to green
                // and all buttons in view are disabled (ensures that the animation is
                // finished before moving to the next question)
                cardView.setCardBackgroundColor(Color.GREEN);
                disableButtons();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // At the end of the animation, the card's background is reset to white,
                // the next question is shown, and all buttons are enabled again
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
                enableButtons();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    // Shake animation
    private void shakeAnimation() {
        // Retrieves the shake animation that was created in the "anim" folder
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);

        // Combines variable with view from layout
        final CardView cardView = findViewById(R.id.cardView);

        // Applies the shake animation to the card view
        cardView.setAnimation(shake);

        // Applies an animation listener to the shake animation
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // At the start of the animation, the card's background is set to red
                // and all buttons in view are disabled (ensures that the animation is
                // finished before moving to the next question)
                cardView.setCardBackgroundColor(Color.RED);
                disableButtons();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // At the end of the animation, the card's background is reset to white,
                // the next question is shown, and all buttons are enabled again
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
                enableButtons();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void enableButtons() {
        // makes the buttons clickable
        nextButton.setClickable(true);
        prevButton.setClickable(true);
        trueButton.setClickable(true);
        falseButton.setClickable(true);
        resetHiScoreBtn.setClickable(true);
    }

    private void disableButtons() {
        // prevents user from clicking the buttons
        nextButton.setClickable(false);
        prevButton.setClickable(false);
        trueButton.setClickable(false);
        falseButton.setClickable(false);
        resetHiScoreBtn.setClickable(false);

    }

    @Override
    protected void onPause() {
        // saves the state whenever the application is paused
        prefs.setState(currentQuestionIndex);
        super.onPause();

    }
}