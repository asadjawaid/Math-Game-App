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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_OPTION = "com.example.a3anda4game.EXTRA_OPTION";
    public static final String EXTRA_USER_INFO = "com.example.a3anda4game.EXTRA_USER_INFO";
    private EditText userInputName;
    private long timeSelected = 25000; // the default value is 25 seconds
    private final DatabaseHelper myDb = new DatabaseHelper(this);
    private String userName;
    private String selectedUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#181818")));
        }

        TextView goButton = (TextView) findViewById(R.id.go_button);
        userInputName = (EditText) findViewById(R.id.user_name);
        Button selectUserNameBtn = (Button) findViewById(R.id.select_a_username);

        // add click listener to go button
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedUsername.equals("")) {
                    userName = userInputName.getText().toString(); // Set the input text to userName iff the user has not selected a name from previous stored names
                }
                else {
                    userName = selectedUsername; // set userName to selectedUsername iff the user has selected a name from the database.
                }
                // check if the user name length has been inputted and that it is at least greater than or equal to four characters.
                if(userName.length() != 0 && userName.length() >= 4) {
                    //addUserName(userName); // add the username to the database
                    Intent intent = new Intent(MainActivity.this, Game.class); // Game Activity (explicit intent)
                    intent.putExtra(EXTRA_OPTION, timeSelected);
                    intent.putExtra(EXTRA_USER_INFO, userName);
                    startActivity(intent); // start Game Activity
                }
                else if(userName.length() >= 1 && userName.length() < 4){
                    Toast.makeText(getApplicationContext(), "Username is too short. Must contain at least 4 characters!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Must enter or select username!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // add click listener to select username button.
        selectUserNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPrevUsernames();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.ten_seconds) {
            Toast.makeText(this, "You selected 10 seconds", Toast.LENGTH_SHORT).show();
            timeSelected = 10000;
            return true;
        }
        else if(itemId == R.id.fifteen_seconds) {
            Toast.makeText(this, "You selected 15 seconds", Toast.LENGTH_SHORT).show();
            timeSelected = 15000;
            return true;
        }
        else if(itemId == R.id.twenty_seconds) {
            Toast.makeText(this, "You selected 20 seconds", Toast.LENGTH_SHORT).show();
            timeSelected = 20000;
            return true;
        }
        else if(itemId == R.id.user_prev_scores) {
            Toast.makeText(this, "Showing all previous scores!", Toast.LENGTH_SHORT).show();
            displayAllScores();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void displayPrevUsernames() {
        ArrayList<String> listOfUsername = myDb.getAllUsername(); // get the scores from array list and store it into new list
        CharSequence[] cs = listOfUsername.toArray(new CharSequence[listOfUsername.size()]); // convert array list to CharSequence
        AlertDialog.Builder alert = new AlertDialog.Builder(this); // create an alert dialog

        alert.setTitle("Select a username:").setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedUsername = cs[which].toString(); // if the user selects a username from the dialog then save it into selectedUsername
                Toast.makeText(getApplicationContext(), "You have selected " + selectedUsername, Toast.LENGTH_SHORT).show();
            }
        }).create();
        alert.show();
        Log.d("All usernames: ", Arrays.toString(cs));
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
}