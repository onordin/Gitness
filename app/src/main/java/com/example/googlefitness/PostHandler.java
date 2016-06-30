package com.example.googlefitness;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by oscarn on 2016-06-28.
 */
public class PostHandler {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    // json string to be send
    String stepJson(ArrayList<String> input) {

        return "{'Type':'" +input.get(0)+ "',"
                + "'Date':'" +input.get(1)+ "',"
                + "'Field':'" +input.get(2)+ "',"
                + "'Value':'" +input.get(3)+ "'}";

    }
}
