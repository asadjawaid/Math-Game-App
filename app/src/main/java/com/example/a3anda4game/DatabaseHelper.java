/*
 * Author: Asad Jawaid
 * Student Number: 104969683
 * Due Date: 2021-03-12
 * */
package com.example.a3anda4game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "DatabaseHelper";
    private static final String TABLE_NAME = "USER_SCORE";
    private static final String COL1_ID = "ID";
    private static final String COL2_USERNAME = "Username"; // primary key and stores all usernames
    private static final String COL3_SCORE = "Score"; // stores all scores
    private static final int DB_VER = 1; // database version

    // DatabaseHelper requires a context which will be used for locating the database.
    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, DB_VER);
    }

    // this is called the first time a database is accessed. There should be code in here to create a new database.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // auto increment will automatically increment id when a user is added
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL1_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2_USERNAME + " TEXT, " + COL3_SCORE + " TEXT);";
        db.execSQL(createTable); // execute the sql create table line.
        Log.i("Database", "Table Created");
    }

    // this is called if the database version number changes. It prevents previous users apps from breaking when you change the database design.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*
    * This method handles inserting a username into the database
    * */
    public boolean insertValues(String userName, String userScore) {
        SQLiteDatabase db = this.getWritableDatabase(); // open database for inserting username
        ContentValues contentValues = new ContentValues(); // create a content value to put the column name and the username
        contentValues.put(COL2_USERNAME, userName); // Key: COL2_USERNAME, Value: userName
        contentValues.put(COL3_SCORE, userScore); // Key: COL3_SCORE, Values: userScore

        long result = db.insert(TABLE_NAME, null, contentValues); // insert the content values or username/score into the database

        // if userResult is -1 then we have an error otherwise we return true denoting the username was successfully inserted into the database
        if(result == -1) {
            return false;
        }

        return true;

    }

    /*
    * This method selects all the data from COL2_USERNAME column
    * */
    public ArrayList<String> getScores() {
        ArrayList<String> listOfScores = new ArrayList<>(); // create a list of string which will store all the scores
        String queryScore = "SELECT * FROM " + TABLE_NAME; // select everything from the table

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery(queryScore, null); // result set from the query

        // move to the first result in the result test and if it exists then continue
        if(cr.moveToFirst()) {
            // now we will loop through the cursor (result set) and get the score from the third column (score) and add it to our array list
            do {
                String retrievedScore = cr.getString(cr.getColumnIndex(COL3_SCORE)); // score retrieved
                listOfScores.add(retrievedScore); // add the score to the list
            }while(cr.moveToNext()); // move to next item if it exists
        }

        cr.close(); // close the cursor
        db.close(); // close the database
        return listOfScores; // return the array list
    }

    /*
     * This method selects all the data from COL3_USERNAME column
     * */
    public ArrayList<String> getAllUsername() {
        ArrayList<String> listOfNames = new ArrayList<>(); // create a list of string which will store all the scores
        String queryScore = "SELECT * FROM " + TABLE_NAME; // select everything from the table

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery(queryScore, null); // result set from the query

        // move to the first result in the result test and if it exists then continue
        if(cr.moveToFirst()) {
            // now we will loop through the cursor (result set) and get the username from the second column (username) and add it to our array list
            do {
                String retrievedName = cr.getString(cr.getColumnIndex(COL2_USERNAME)); // score retrieved
                listOfNames.add(retrievedName); // add the score to the list
            }while(cr.moveToNext()); // move to next item if it exists
        }

        cr.close(); // close the cursor
        db.close(); // close the database
        return listOfNames; // return the array list
    }

    /*
    * This method deletes all the records in the database. Will not be used in the application but if the user decides to remove all the records this can be called in the application.
    * */
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

}
