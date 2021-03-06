package com.alperencan.news.android.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.alperencan.news.android.model.Content;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from Guardian API.
 */

public class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google Books API dataset and return a list of {@link Content} objects.
     */
    public static List<Content> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response, create a list of {@link Volume}s and return.
        return extractVolumesFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        Uri builtUri = Uri.parse(stringUrl)
                .buildUpon()
                .appendQueryParameter("page-size", "20")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Content} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Content> extractVolumesFromJson(String responseJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(responseJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news items to
        List<Content> newsItems = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(responseJSON).getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of news items.
            JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

            // For each news item in the resultsArray, create an {@link Content} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single news item at position i within the list
                JSONObject currentNewsItem = resultsArray.getJSONObject(i);

                // Extract the value for title and author (if provided).
                String title = "";
                String author = "";
                String webTitle = currentNewsItem.getString("webTitle");
                if (webTitle.contains("|")) {
                    String[] parts = webTitle.split("[|]");
                    title += parts[0].trim();
                    author += parts[1].trim();
                } else {
                    title += webTitle;
                }

                // Extract the value for section.
                String section = currentNewsItem.getString("sectionName");

                //Extract the value for url.
                String url = currentNewsItem.getString("webUrl");

                // Extract the value for the key called "thumbnail" if there are any dates.
                String dateString = "";
                if (currentNewsItem.has("webPublicationDate")) {
                    String timestampString = currentNewsItem.getString("webPublicationDate");
                    SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    try {
                        Date date = dateFormatIn.parse(timestampString);
                        SimpleDateFormat dateFormatOut = new SimpleDateFormat("MMM dd, yyyy");
                        dateString = dateFormatOut.format(date);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Problem in formatting date.", e);
                    }
                }


                // Create a new {@link Volume} object with the title, authors, and thumbnail,
                // from the JSON response.
                Content newsItem = new Content(title, section, url, author, dateString);

                // Add the new {@link Volume} to the list of volumes.
                newsItems.add(newsItem);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the volume JSON results", e);
        }

        // Return the list of volumes
        return newsItems;
    }
}