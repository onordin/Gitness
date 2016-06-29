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
    String bowlingJson(ArrayList<String> input) {

        return "{'" +input.get(0)+ "':'" +input.get(1)+ "',"
                + "'" +input.get(2)+ "':'" +input.get(3)+ "',"
                + "'" +input.get(4)+ "':'" +input.get(5)+ "',"
                + "'" +input.get(6)+ "':'" +input.get(7)+ "'}";
        /*
        return "{'winCondition':'HIGH_SCORE',"
                + "'name':'Bowling',"
                + "'round':4,"
                + "'lastSaved':1367702411696,"
                + "'dateStarted':1367702378785,"
                + "'players':["
                + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
                + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
                + "]}";
        */
    }



}
