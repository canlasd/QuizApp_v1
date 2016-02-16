package com.example.canlasd.quizapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


class DownloadData {

    private final static String log_tag = "log_download_data";
    private String final_result;

    private class DownloadGeoJsonFile extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                URL url = new URL(params[0]);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                // Open a stream from the URL
                InputStream stream = new URL(params[0]).openStream();

                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {
                    // Read and save each line of the stream
                    result.append(line);
                }

                final_result = result.toString();

                reader.close();
                stream.close();


            } catch (IOException e) {
                e.printStackTrace();
                Log.e(log_tag, "GeoJSON file could not be read");

            }

            return null;
        }


        protected void onPostExecute(JSONObject jsonObject) {

        }
    }

    // method called to get api data
    public String startDownload(String link) {

        DownloadGeoJsonFile downloadGeoJsonFile = new DownloadGeoJsonFile();
        // Download the json file
        downloadGeoJsonFile.execute(link);

        // buffer time before cancelling task
        try {
            downloadGeoJsonFile.get(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        return final_result;

    }

}








