package com.example.canlasd.quizapp;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canlasd.quizapp.AlertDialogButton.AlertPositiveListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements AlertPositiveListener {


    private static final String URL =
            "https://docs.google.com/document/u/0/export?format=txt&id=1MV7GHAvv4tgj98Hj6B_WZdeeEu7CRf1GwOfISjP4GT0";

    private DBhelper DelHelper;
    private long number_entries;
    private long current_time;
    private boolean clicked;
    private TextView timer_view;
    private int correct_answer;
    private int current_key;
    private int correct_position;
    public static MyCounter timer;
    private String PREFS_COUNT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button = (Button) findViewById(R.id.button_start);
        timer_view = (TextView) findViewById(R.id.timer_view);


        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        clicked = true;
                        // start timer
                        timer = new MyCounter(60000, 1000);
                        timer.start();


                        // initial number of correct answers
                        correct_answer = 0;

                        // clear contents of sqlite db
                        DelHelper = new DBhelper(MainActivity.this);
                        DelHelper.open();
                        DelHelper.deleteData();
                        DelHelper.close();

                        // download data from url
                        DownloadData db = new DownloadData();
                        String raw_data = db.startDownload(URL);

                        // save data into sqlite database
                        getData(raw_data);

                        // get data from first sqlite row and display in dialog
                        displayDialog(1);
                    }
                }
        );
    }

    private void displayDialog(int key) {

        // get information from sqlite db
        DBhelper rowHelper = new DBhelper(this);
        rowHelper.open();
        // get sqlite row based on key.  initial key is 1
        List<String> result = rowHelper.getRowData(key);
        number_entries = rowHelper.getNumberRows();
        rowHelper.close();

        // get info from retrieved row
        String main_question = result.get(1);
        current_key = Integer.parseInt(result.get(0));

        // convert the correct answer to its equivalent number
        String correct_string = (result.get(2)).toLowerCase();
        correct_position = getEquivalentNumber(correct_string);

        List<String> question_items = result.subList(3, result.size());
        String[] array = question_items.toArray(new String[question_items.size()]);

        FragmentManager manager = getFragmentManager();
        AlertDialogButton alert = new AlertDialogButton();


        //Creating a bundle object to store sqlite info to alert dialog
        Bundle b = new Bundle();
        b.putString("main_question", main_question);
        b.putStringArray("array", array);
        alert.setArguments(b);

        alert.show(manager, "alert_dialog_radio");


    }

    // return the position from dialog fragment
    @Override
    public void onNextClick(int position) {

        int position1 = position;

        // check if returned answer is correct
        if (correct_position == position) {
            correct_answer++;
        }
        // move to next row
        int next_key = current_key + 1;

        // check if there are any rows with data left in the sqlite db
        if (next_key > number_entries) {
            timer.cancel();
            timer_view.setText(R.string.timer_cancelled);
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Finished");
            alertDialog.setMessage("# Corrrect Answers: " + correct_answer + "\n" +
                    "# Questions: " + number_entries);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            clicked = false;


        } else {
            displayDialog(next_key);
        }

    }

    // save instance state to prevent loss of data during orientation changes
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save the user's current game state
        savedInstanceState.putInt("correct_answer", correct_answer);
        savedInstanceState.putInt("correct_position", correct_position);
        savedInstanceState.putInt("current_key", current_key);
        savedInstanceState.putLong("number_entries", number_entries);
        savedInstanceState.putLong("current_time", current_time);
        savedInstanceState.putBoolean("clicked", clicked);

        super.onSaveInstanceState(savedInstanceState);


    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        correct_answer = savedInstanceState.getInt("correct_answer");
        correct_position = savedInstanceState.getInt("correct_position");
        current_key = savedInstanceState.getInt("current_key");
        number_entries = savedInstanceState.getLong("number_entries");
        current_time = savedInstanceState.getLong("current_time");
        clicked = savedInstanceState.getBoolean("clicked");

    }

    // convert letter answer to equivalent number
    private int getEquivalentNumber(String correct_string) {
        Map<String, Integer> correct_answer = new HashMap<>();
        correct_answer.put("a", 1);
        correct_answer.put("b", 2);
        correct_answer.put("c", 3);
        correct_answer.put("d", 4);
        correct_answer.put("e", 5);
        correct_answer.put("f", 6);

        return correct_answer.get(correct_string);
    }

    // method to insert data to sqlite db
    private void insertData(int length, List<String> map, List<String> questions) {

        DBhelper dbHelper = new DBhelper(this);
        dbHelper.open();

        if (length == 5) {

            String main = map.get(0);
            String correct = map.get(1);
            String first = questions.get(0);
            String second = questions.get(1);
            String third = questions.get(2);

            dbHelper.insertStdDetails(main, correct, first, second, third, null, null, null);

        } else if (length == 6) {

            String two_main = map.get(0);
            String two_correct = map.get(1);
            String two_first = questions.get(0);
            String two_second = questions.get(1);
            String two_third = questions.get(2);
            String two_fourth = questions.get(3);

            dbHelper.insertStdDetails(two_main, two_correct, two_first, two_second,
                    two_third, two_fourth, null, null);

        } else if (length == 7) {

            String three_main = map.get(0);
            String three_correct = map.get(1);
            String three_first = questions.get(0);
            String three_second = questions.get(1);
            String three_third = questions.get(2);
            String three_fourth = questions.get(3);
            String three_fifth = questions.get(4);

            dbHelper.insertStdDetails(three_main, three_correct, three_first,
                    three_second, three_third, three_fourth, three_fifth, null);

        } else if (length == 8) {

            String four_main = map.get(0);
            String four_correct = map.get(1);
            String four_first = questions.get(0);
            String four_second = questions.get(1);
            String four_third = questions.get(2);
            String four_fourth = questions.get(3);
            String four_fifth = questions.get(4);
            String four_sixth = questions.get(5);

            dbHelper.insertStdDetails(four_main, four_correct, four_first,
                    four_second, four_third, four_fourth, four_fifth, four_sixth);

        } else {
            Toast.makeText(this, "Error Inserting Data", Toast.LENGTH_LONG).show();

        }

        dbHelper.close();

    }

    // extract data from downloaded api
    private void getData(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("questions");
            int array_length = jsonArray.length();

            for (int i = 0; i < array_length; i++) {

                List<String> list = new ArrayList<>();
                List<String> questions = new ArrayList<>();

                String question_object = jsonArray.getJSONObject(i).getString("question");
                String answer_object = jsonArray.getJSONObject(i).getString("answer");
                JSONArray multiple_choice = jsonArray.getJSONObject(i).getJSONArray("multiple_choice");

                // put data into list
                list.add(question_object);
                list.add(answer_object);

                for (int j = 0; j < multiple_choice.length(); j++) {
                    String answer = multiple_choice.getJSONObject(j).getString("answer");
                    questions.add(answer);
                }

                // inserting one row at a time to sqlite db
                int total_length = (list.size() + questions.size());
                insertData(total_length, list, questions);

                list.clear();
                questions.clear();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // generate timer during quiz
    public class MyCounter extends CountDownTimer {

        public MyCounter(long millis_params, long count_params) {
            super(millis_params, count_params);
        }

        @Override
        public void onFinish() {

            Toast.makeText(MainActivity.this, "Time is up. You Got " + correct_answer + " out of "
                    + number_entries, Toast.LENGTH_LONG).show();
            MainActivity.this.finish();
        }

        @Override
        public void onTick(long finish_params) {
            current_time = finish_params;
            timer_view.setText(("Time Remaining in seconds: " + finish_params / 1000) + "");

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // store current time
        SharedPreferences settings = getSharedPreferences(PREFS_COUNT, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("current_time", current_time);


        editor.commit();

        if (timer != null) {
            timer.cancel();
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences settings = getSharedPreferences(PREFS_COUNT, 0);
        current_time = settings.getLong("current_time", current_time);


        if (clicked) {
            timer = new MyCounter(current_time, 1000);
            timer.start();
        }
    }


}



