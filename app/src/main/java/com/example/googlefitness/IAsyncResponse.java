package com.example.googlefitness;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Carl Berglund (churlz) on 05/07/16.
 */
public interface IAsyncResponse {
    public void result(int responseCode, JSONObject json) throws JSONException;
}
