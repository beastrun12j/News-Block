package com.example.newsblock;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    public static final String LOG_TAG = NewsActivity.class.getName();

    private QueryUtils() {
    }

    public static List<News> extractFeatureFromJSON(String newsJSON) {

        if(TextUtils.isEmpty(newsJSON))
        {
            return null;
        }

        List<News> newsInfo = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for(int i=0;i<results.length();i++)
            {
                JSONObject currentNews = results.getJSONObject(i);
                String headline = currentNews.getString("webTitle");
                String sectionName = currentNews.getString("sectionName");
                String type = currentNews.getString("type");
                String webPublicationDate = currentNews.getString("webPublicationDate");
                String webUrl = currentNews.getString("webUrl");

                News news = new News(headline,sectionName,type,webPublicationDate,webUrl);
                newsInfo.add(news);
            }

        }

        catch (JSONException e) {

            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return newsInfo;
    }

    public static List<News> fetchNewsData(String requestUrl)
    {

        Log.e(LOG_TAG,"Fetching News data");

        URL url = createURL(requestUrl);
        String jsonResponse = null;

        try
        {
            jsonResponse = makeHTTPRequest(url);
        }
        catch(IOException e)
        {
            Log.e(LOG_TAG, "Problem making the HTTP request",e);
        }

        List<News> news = extractFeatureFromJSON(jsonResponse);

        return news;

    }


    private static URL createURL(String stringURL)
    {
        URL url = null;
        try
        {
            url = new URL(stringURL);
        }
        catch (MalformedURLException e)
        {
            Log.e(LOG_TAG, "Problem building the URL",e);
        }
        return url;
    }

    private static String makeHTTPRequest(URL url) throws IOException
    {
        String jsonResponse = null;

        if(url == null)
        {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try
        {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200)
            {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else
            {
                Log.e(LOG_TAG,"Error Response Code : "+ urlConnection.getResponseCode());
            }

        }
        catch(IOException e)
        {
            Log.e(LOG_TAG,"Problem retrieving the News JSON results", e);
        }
        finally
        {
            if(urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if(inputStream != null)
            {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream)
    {
        StringBuilder output = new StringBuilder();

        try {

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG,"Problem retrieving the News JSON results", e);
        }

        return output.toString();
    }

}

