package com.example.canlasd.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBhelper extends MainActivity {

    private static final String KEY_ROW_ID = "_id";
    private static final String MAIN_QUESTION = "main_question";
    private static final String CORRECT_ANSWER = "correct_answer";
    private static final String FIRST_ANSWER = "first_answer";
    private static final String SECOND_ANSWER = "second_answer";
    private static final String THIRD_ANSWER = "third_answer";
    private static final String FOURTH_ANSWER = "fourth_answer";
    private static final String FIFTH_ANSWER = "fifth_answer";
    private static final String SIXTH_ANSWER = "sixth_answer";

    private final DatabaseHelper db_helper;
    private SQLiteDatabase main_db;
    private static final String DATABASE_NAME = "quiz.db";
    private static final int DATABASE_VERSION = 1;
    private static final String QUIZ_TABLE = "quiz_body";

    public interface Constants {
        String LOG = "quiz_app";
    }

    private static final String CREATE_QUIZ_TABLE = "CREATE TABLE "
            + QUIZ_TABLE + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + MAIN_QUESTION + " TEXT NOT NULL," + CORRECT_ANSWER + " TEXT NOT NULL,"
            + FIRST_ANSWER + " TEXT," + SECOND_ANSWER + " TEXT,"
            + THIRD_ANSWER + " TEXT," + FOURTH_ANSWER + " TEXT,"
            + FIFTH_ANSWER + " TEXT," + SIXTH_ANSWER + " TEXT)";


    static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_QUIZ_TABLE);
            Log.d(Constants.LOG, "Quiz Database Enabled");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + QUIZ_TABLE);
            onCreate(db);
        }
    }


    public DBhelper(Context ctx) {
        Context current_context = ctx;
        db_helper = new DatabaseHelper(current_context);
    }

    public void open() throws SQLException {
        main_db = db_helper.getWritableDatabase();


    }


    public void close() {
        db_helper.close();
    }


    public void insertStdDetails(String main_question, String correct_answer, String first_answer,
                                 String second_answer, String third_answer, String fourth_answer,
                                 String fifth_answer, String sixth_answer) {

        ContentValues cv = new ContentValues();

        cv.put(MAIN_QUESTION, main_question);
        cv.put(CORRECT_ANSWER, correct_answer);
        cv.put(FIRST_ANSWER, first_answer);
        cv.put(SECOND_ANSWER, second_answer);
        cv.put(THIRD_ANSWER, third_answer);
        cv.put(FOURTH_ANSWER, fourth_answer);
        cv.put(FIFTH_ANSWER, fifth_answer);
        cv.put(SIXTH_ANSWER, sixth_answer);

        long check = main_db.insert(QUIZ_TABLE, null, cv);

        if (check != 0) {
            Log.d(Constants.LOG, "Record Added Successfully " + check);
        } else {
            Log.d(Constants.LOG, "ERROR inserting row " + check);

        }

    }

    public void deleteData() {
        // reset id number and clear table
        String reset = "DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + QUIZ_TABLE + "'";
        main_db.delete(QUIZ_TABLE, null, null);
        main_db.execSQL(reset);


    }

    public List<String> getRowData(int key) {
        List<String> row_list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + QUIZ_TABLE + " WHERE "
                + KEY_ROW_ID + " = " + key;

        Cursor c = main_db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            for (int i = 0; i < c.getColumnCount(); i++) {
                if ((!c.isNull(i))) {
                    row_list.add(c.getString(i));
                }
            }
            c.close();
        }
        return row_list;
        
    }

    public long getNumberRows() {
        return DatabaseUtils.queryNumEntries(main_db, QUIZ_TABLE);
    }
}
