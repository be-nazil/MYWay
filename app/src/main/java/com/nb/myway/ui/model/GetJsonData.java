package com.nb.myway.ui.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class GetJsonData {
    public static void getData(Context context){
        try {
            InputStream inputStream = context.getAssets().open("response.json");

            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(i);
                JSONArray jsonArray1 = jsonObject.getJSONArray("routes");
                for (int j=0; j< jsonArray1.length(); j++) {
                    Log.i("GetJsonData", "getData: " + jsonArray1.getJSONObject(i).get("sourceLat").toString());
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
