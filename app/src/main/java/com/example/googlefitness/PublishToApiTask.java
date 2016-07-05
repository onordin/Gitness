package com.example.googlefitness;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

/**
 * Created by CHURLZ on 05/07/16.
 */
public class PublishToApiTask extends AsyncTask<Void, Void, JSONObject> {
    String urlString = "https://api.stena-health.d4bb62f5.svc.dockerapp.io/healthData?apikeyid=pY_8_iW1HNiZxGvrGLpOZw&secretaccesskey=LJb8siHDrxXzD27p8KCUcw";
    int responseCode;
    IAsyncResponse delegate;

    public PublishToApiTask(IAsyncResponse delegate){
        this.delegate = delegate;
    }

    private HttpURLConnection setUpPostConnection(String params){
        HttpURLConnection conn = null;
        byte[] data = params.getBytes(Charset.forName("UTF-8"));
        int size = data.length;
        Log.d("POST", String.format("size: %s", size));
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15);
            conn.setConnectTimeout(15);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(size));
            conn.setInstanceFollowRedirects(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }


    @Override
    protected JSONObject doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        try {
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter(URLEncoder.encode("user", "UTF-8"), URLEncoder.encode("username", "UTF-8"))
                    .appendQueryParameter(URLEncoder.encode("steps", "UTF-8"  ), URLEncoder.encode("steps", "UTF-8"))
                    .appendQueryParameter(URLEncoder.encode("timestamp", "UTF-8"  ), URLEncoder.encode("tiemstamp", "UTF-8"))
                    .appendQueryParameter(URLEncoder.encode("team", "UTF-8"  ), URLEncoder.encode("team", "UTF-8"));
            URL url = new URL(urlString);

            String query = builder.build().getEncodedQuery();
            urlConnection = setUpPostConnection(builder.build().getQuery());

            OutputStream os = urlConnection.getOutputStream();
            InputStream in = null;
            responseCode = urlConnection.getResponseCode();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();
            responseCode = urlConnection.getResponseCode();
        } catch (UnknownHostException e) {
            Log.d("UNKNOWN HOST:", urlString + " " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }


    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            delegate.result(responseCode, result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
