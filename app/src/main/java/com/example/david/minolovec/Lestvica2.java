package com.example.david.minolovec;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;



public class Lestvica2 extends AsyncTask<Void, Void, String> {

    private String urlStoritve="http://www.genics.eu/androidApp/php/request_scores.php";
    private Lestvica mActivity;

    public Lestvica2(Lestvica activity) {
        mActivity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {
        ConnectivityManager connMgr = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                return connect();
            } catch (IOException e) {
                return mActivity.getResources().getString(R.string.napaka_storitev);
            }
        }
        else{
            return mActivity.getResources().getString(R.string.napaka_omrezje);
        }
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        mActivity.odgovorStoritve(result);
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
// the content as a InputStream, which it returns as a string.
    private String connect() throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved content.
        int len = 1350;

        try {
            URL url = new URL(urlStoritve);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000 /* milliseconds */);
            conn.setConnectTimeout(10000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Accept", "application/json");

            // Starts the query
            conn.connect();

            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            //String contentAsString = readIt(is, len);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            StringBuilder out = new StringBuilder();
            String contentAsString = null;
            String line;

            while((line = reader.readLine()) != null){
                out.append(line);
            }
            System.out.println(out.toString());
            contentAsString = out.toString();
            Log.d("comments:", "JSON: " +contentAsString);

            reader.close();

            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}