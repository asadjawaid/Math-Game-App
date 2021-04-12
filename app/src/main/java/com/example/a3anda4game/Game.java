/*
* Author: Asad Jawaid
* Student Number: 104969683
* Due Date: 2021-03-12
* */
package com.example.a3anda4game;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class Game extends AppCompatActivity {
    // reference to all the text views and button from the ui
    private Button playButton;
    private TextView timerText;
    private TextView firstOption;
    private TextView secondOption;
    private TextView thirdOption;
    private TextView forthOption;
    private TextView firstNumber;
    private TextView secondNumber;
    private TextView wrongOrRight;
    private TextView scoreText;
    // variables that will be used throughout the application
    private int score = 0;
    private int numberOfQuestions = 0;
    private long selectedTime;
    private int remainingTime;
    private String passedUserName; // username passed from main activity
    private long selectedTimeDuringGameplay = 0;
    private int remainingTimeDuringGameplay = 0;
    private boolean isGameRunning = false;
    private boolean optionItemClicked = false;
    private final DatabaseHelper myDb = new DatabaseHelper(Game.this); // create database helper and the context is this class (Game)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#181818")));
        }

        timerText = (TextView) findViewById(R.id.countdown_timer_text); // find timer text
        firstOption = (TextView) findViewById(R.id.first_option); // find first option
        secondOption = (TextView) findViewById(R.id.second_option); // find second option
        thirdOption = (TextView) findViewById(R.id.third_option); // find third option
        forthOption = (TextView) findViewById(R.id.forth_option); // find forth option
        firstNumber = (TextView) findViewById(R.id.first_number); // find first number
        secondNumber = (TextView) findViewById(R.id.second_number); // find second number
        wrongOrRight = (TextView) findViewById(R.id.wrong_right); // find text
        scoreText = (TextView) findViewById(R.id.score); // find score text
        playButton = (Button) findViewById(R.id.play_button); // find the play button

        setButtonState(false); // disable all option buttons until play button is clicked
        handleIntentFromMain(); // handle intent from MainActivity
        generateQuestion(); // generate new question

        // click listener for play button start game
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String originalScore = "0/0";
                scoreText.setText(originalScore); // update the text
                playButton.setVisibility(View.INVISIBLE); // make the play button invisible
                wrongOrRight.setVisibility(View.INVISIBLE); // make the text invisible when pressing play
                setButtonState(true); // enable all options to be clickable so the user can select their answer.
                startCountdown(); // start the count down timer
            }
        });

        firstOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("First", "Option clicked");
                start(firstOption.getId());
            }
        });

        secondOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Second", "Option clicked");
                start(secondOption.getId());
            }
        });

        thirdOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Third", "Option clicked");
                start(thirdOption.getId());
            }
        });

        forthOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Forth", "Option clicked");
                start(forthOption.getId());
            }
        });
    }

    /*
    * This method gets called whenever the users click the three dots on the top right corner of the game which display an option menu.
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu); // inflate menu
        return true;
    }

    /*
    * This method is called whenever an item in the options menu is selected.
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        optionItemClicked = true;

        if(itemId == R.id.ten_seconds) {
            // if the game is running then save into new variable
            if(isGameRunning) {
                selectedTimeDuringGameplay = 10000;
                remainingTimeDuringGameplay = 10;
                Toast.makeText(this, "Next game starts with 10 seconds", Toast.LENGTH_SHORT).show();
            }
            else {
                selectedTime = 10000;
                remainingTime = 10;
                Toast.makeText(this, "Timer set to 10 seconds", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else if(itemId == R.id.fifteen_seconds) {
            if(isGameRunning) {
                selectedTimeDuringGameplay = 15000;
                remainingTimeDuringGameplay = 15;
                Toast.makeText(this, "Next game starts with 15 seconds", Toast.LENGTH_SHORT).show();
            }
            else {
                selectedTime = 15000;
                remainingTime = 15;
                Toast.makeText(this, "Timer set to 15 seconds", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else if(itemId == R.id.twenty_seconds) {
            if(isGameRunning) {
                selectedTimeDuringGameplay = 20000;
                remainingTimeDuringGameplay = 20;
                Toast.makeText(this, "Next game starts with 20 seconds", Toast.LENGTH_SHORT).show();
            }
            else {
                selectedTime = 20000;
                remainingTime = 20;
                Toast.makeText(this, "Timer set to 20 seconds", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else if(itemId == R.id.user_prev_scores) {
            // if the game is not running then display the scores
            if(!isGameRunning) {
                displayAllScores();
            }
            else {
                Toast.makeText(this, "Gaming is running. Wait till the game is over!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*
    * This method is responsible for starting the countdown timer
    * */
    private void startCountdown() {
        isGameRunning = true;
        generateQuestion(); // generate new question

        CountDownTimer timer = new CountDownTimer(selectedTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = (int) millisUntilFinished / 1000;
                String updateText = String.valueOf(remainingTime) + "s";
                timerText.setText(updateText);
            }

            @Override
            public void onFinish() {
                isGameRunning = false;
                String playAgain = "Play Again";
                Log.d("Timer", "Ended");
                setButtonState(false); // disable all option buttons until play button is clicked
                playButton.setText(playAgain);
                playButton.setVisibility(View.VISIBLE);

                // the user selected a new timer while the game was running. So the next time they click play it will start with the time they selected during the game play.
                if(selectedTimeDuringGameplay != 0 && remainingTimeDuringGameplay != 0 && optionItemClicked) {
                    selectedTime = selectedTimeDuringGameplay;
                    remainingTime = remainingTimeDuringGameplay;
                    optionItemClicked = false;
                }
                else {
                    selectedTimeDuringGameplay = 0;
                    remainingTimeDuringGameplay = 0;
                    selectedTime = 25000;
                    remainingTime = 25;
                }
                displayScore();
                addInfoToDatabase(); // add the username and score to the database

                // tutorial: https://www.youtube.com/watch?v=SK98ayjhk1E
            }
        }.start();
    }

    /*
    * This method gets all data from the intent from the MainActivity.java class. For instance, the selected time etc.
    * */
    private void handleIntentFromMain() {
        Intent intent = getIntent();
        selectedTime = intent.getLongExtra(MainActivity.EXTRA_OPTION, 25000); // get time selected or if not sent set the default time to 25s.
        passedUserName = intent.getStringExtra(MainActivity.EXTRA_USER_INFO); // get username from main activity.

        if(selectedTime == 25000) {
            remainingTime = 25;
            String setTimeText = "25s";
            timerText.setText(setTimeText);
        }
        else if(selectedTime == 10000) {
            remainingTime = 10;
            String setTimeText = "10s";
            timerText.setText(setTimeText);
        }
        else if(selectedTime == 15000) {
            remainingTime = 15;
            String setTimeText = "15s";
            timerText.setText(setTimeText);
        }
        else if(selectedTime == 20000) {
            remainingTime = 20;
            String setTimeText = "20s";
            timerText.setText(setTimeText);
        }
    }

    /*
    * This method is used to start the game with parameter of integer (val) which indicates the option or answer selected from the ui.
    * */
    private void start(int val) {
        int firstOptionNum, secondOptionNum, thirdOptionNum, forthOptionNum;

        if(val == R.id.first_option) {
            firstOptionNum = Integer.parseInt(firstOption.getText().toString());
            checkAnswer(firstOptionNum);
        }
        else if(val == R.id.second_option) {
            secondOptionNum = Integer.parseInt(secondOption.getText().toString());
            checkAnswer(secondOptionNum);
        }
        else if(val == R.id.third_option) {
            thirdOptionNum = Integer.parseInt(thirdOption.getText().toString());
            checkAnswer(thirdOptionNum);
        }
        else if(val == R.id.forth_option) {
            forthOptionNum = Integer.parseInt(forthOption.getText().toString());
            checkAnswer(forthOptionNum);
        }
    }

    /* This method is used to disable or enable the four options from the ui. */
    private void setButtonState(boolean value) {
        firstOption.setEnabled(value);
        secondOption.setEnabled(value);
        thirdOption.setEnabled(value);
        forthOption.setEnabled(value);
    }

    /* This method is used to check if the user selected the correct answer */
    private void checkAnswer(int userAnswer) {
        int num1 = Integer.parseInt(firstNumber.getText().toString()); // parse text view to int
        int num2 = Integer.parseInt(secondNumber.getText().toString()); // parse text view to int
        int solution = num1 + num2; // store the result

        // answer is correct
        if(userAnswer == solution) {
            ++score; // increment score
            ++numberOfQuestions;
            String correctAnswer = "Correct!";
            displayWrongOrRight(correctAnswer);
            String userScore = score + "/" + numberOfQuestions;
            scoreText.setText(userScore); // update the text
        }
        // answer selected is wrong
        else {
            ++numberOfQuestions;
            String userScore = score + "/" + numberOfQuestions;
            scoreText.setText(userScore); // update the text
            String wrongAnswer = "Wrong!";
            displayWrongOrRight(wrongAnswer);
        }
        // generate a new question
        generateQuestion();
    }

    /*
     * @Description: This method is used to generate random numbers and get the solution.
     * */
    private void generateQuestion() {
        Random rnd = new Random();
        // generate random integers between 1 and 20 which will be used for the solution
        int genFirstNum = rnd.nextInt(20);
        int genSecondNum = rnd.nextInt(20);

        int genFirstSolution = rnd.nextInt(40); // first possible solution
        int genThirdSolution = rnd.nextInt(40); // second possible solution
        int genForthSolution = rnd.nextInt(40); // third possible solution
        int solutionGen = genFirstNum + genSecondNum; // solution

        // convert first and second number to string
        String first_number_gen = String.valueOf(genFirstNum);
        String second_number_gen = String.valueOf(genSecondNum);

        // convert options to string
        String generatedFirst = String.valueOf(genFirstSolution);
        String generatedSecond = String.valueOf(solutionGen);
        String generatedThird = String.valueOf(genThirdSolution);
        String generatedForth = String.valueOf(genForthSolution);

        // set question text
        firstNumber.setText(first_number_gen);
        secondNumber.setText(second_number_gen);

        int positionToPut = rnd.nextInt(7); // generate a random number between 0 and 3 to put solution in different quadrants

        if(positionToPut == 0 || positionToPut == 7) {
            firstOption.setText(generatedFirst);
            secondOption.setText(generatedThird);
            thirdOption.setText(generatedSecond);
            forthOption.setText(generatedForth);
        }
        else if(positionToPut == 1 || positionToPut == 6) {
            firstOption.setText(generatedSecond);
            secondOption.setText(generatedFirst);
            thirdOption.setText(generatedThird);
            forthOption.setText(generatedForth);
        }
        else if(positionToPut == 2 || positionToPut == 5) {
            firstOption.setText(generatedFirst);
            secondOption.setText(generatedForth);
            thirdOption.setText(generatedThird);
            forthOption.setText(generatedSecond);
        }
        else if(positionToPut == 3 || positionToPut == 4) {
            firstOption.setText(generatedFirst);
            secondOption.setText(generatedSecond);
            thirdOption.setText(generatedThird);
            forthOption.setText(generatedForth);
        }
    }

    private void displayWrongOrRight(String toDisplay) {
        wrongOrRight.setText(toDisplay);
        wrongOrRight.setVisibility(View.VISIBLE); // make the text visible
    }

    /* This method displays the score after the game is finished. */
    private void displayScore() {
        String scoreToDisplay = passedUserName + "'s Score: " + score + "/" + numberOfQuestions;
        wrongOrRight.setText(scoreToDisplay);
        wrongOrRight.setVisibility(View.VISIBLE);
    }

    /* This method gets all the scores from the database and stores it into an arraylist. We also convert the arraylist to a char sequence to build an alert dialog so the user
    * can see the previous scores. */
    private void displayAllScores() {
        ArrayList<String> allListOfScores = myDb.getScores(); // get the scores from array list and store it into new list
        CharSequence[] cs = allListOfScores.toArray(new CharSequence[allListOfScores.size()]); // convert array list to CharSequence
        AlertDialog.Builder alert = new AlertDialog.Builder(this); // create an alert dialog

        alert.setTitle("All Previous Scores").setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Score "+(which + 1)+": " + cs[which].toString(), Toast.LENGTH_SHORT).show(); // display a toast of which score they selected
            }
        }).create();
        alert.show();
        Log.d("In Game Class Scores: ", allListOfScores.toString());
    }

    /*
    * This method inserts the username and the score into the database. It also resets the score and number of questions back to zero.
    * */
    private void addInfoToDatabase() {
        myDb.insertValues(passedUserName, score + "/" + numberOfQuestions); // insert the record into the database
        //myDb.deleteAll(); This is only used when all the data in the database wants to be deleted
        score = 0; // reset the score back to zero
        numberOfQuestions = 0; // reset the number of questions back to zero
    }
}