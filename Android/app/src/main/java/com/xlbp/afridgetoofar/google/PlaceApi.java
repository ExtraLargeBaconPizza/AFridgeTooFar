package com.xlbp.afridgetoofar.google;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlaceApi
{
    public ArrayList<String[]> autoComplete(String input)
    {
        ArrayList<String[]> _resultArrayList = new ArrayList<>();
        HttpURLConnection _connection = null;
        StringBuilder _jsonResult = new StringBuilder();

        try
        {
            StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
            stringBuilder.append("input=").append(input);
            // TODO - remove country restrictions
            stringBuilder.append("&components=country:us|country:ca");
            stringBuilder.append("&key=AIzaSyCCsgmb-3SsAt7eQxSucG1vktfKBKeW9sM");

            URL url = new URL(stringBuilder.toString());
            _connection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(_connection.getInputStream());

            int read;
            char[] buff = new char[1024];

            while ((read = inputStreamReader.read(buff)) != -1)
            {
                _jsonResult.append(buff, 0, read);
            }

//            Log.e("PlaceApi", "JSon" + _jsonResult.toString());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (_connection != null)
            {
                _connection.disconnect();
            }
        }

        try
        {
            JSONObject jsonObject = new JSONObject(_jsonResult.toString());
            JSONArray prediction = jsonObject.getJSONArray("predictions");

            for (int i = 0; i < prediction.length(); i++)
            {
                String fullDescription = prediction.getJSONObject(i).getString("description");

                JSONObject structuredFormatting = prediction.getJSONObject(i).getJSONObject("structured_formatting");
                String mainText = structuredFormatting.getString("main_text");
                String secondaryText = structuredFormatting.getString("secondary_text");

                String[] address = new String[3];

                address[0] = fullDescription;
                address[1] = mainText;
                address[2] = secondaryText;

                _resultArrayList.add(address);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return _resultArrayList;
    }
}
