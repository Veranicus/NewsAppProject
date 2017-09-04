package com.example.android.newsappproject;

/**
 * Created by Polacek on 16.7.2017.
 */
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
    public static final String LOG_TAG = QueryUtils.class.getName();

    //this have a private constructor because no one should create an instance of this class.
    private QueryUtils() {
    }

    /**
     * Quering the Guardian database
     */
    public static List<ListOfNews> fetchData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        //HTTP url to get url json response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        // Extracting relevant informations from JSON response
        List<ListOfNews> articleNews = extractData(jsonResponse);
        return articleNews;
    }

    /**
     * New object from created URl
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return url;
    }

    /**
     * Returning a string from HttpRequest
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // IF there is an empty(0) url
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
            // Reading response codes
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Converting input stream...
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

    public static List<ListOfNews> extractData(String jsonResponse) {
        ArrayList<ListOfNews> ListOfNews = new ArrayList<>();
        // Trying to parse JSON resposne
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            if (response.has("results")) {
                JSONArray newsArray = response.getJSONArray("results");
                //the data are obtained for each news
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject singleNews = newsArray.getJSONObject(i);
                    String webTitle = "";//getting title of given news
                    if (singleNews.has("webTitle")) {
                        webTitle = singleNews.getString("webTitle");
                    }
                    String sectionName = ""; //getting section anme
                    if (singleNews.has("sectionName")) {
                        sectionName = singleNews.getString("sectionName");
                    }
                    String webPublicationDate = "";  //getting date of publication
                    if (singleNews.has("webPublicationDate")) {
                        webPublicationDate = singleNews.getString("webPublicationDate");
                    }
                    String webUrl = ""; //gettin web page url
                    if (singleNews.has("webUrl")) {
                        webUrl = singleNews.getString("webUrl");
                    }
                    JSONArray tagsArray;
                    String authorName = ""; //this gets the author name
                    if (singleNews.has("tags")) {
                        tagsArray = singleNews.getJSONArray("tags");
                        if (tagsArray.length() > 0) {
                            for (int author = 0; author < 1; author++) {
                                JSONObject tags = tagsArray.getJSONObject(author);
                                if (tags.has("webTitle")) {
                                    authorName = tags.getString("webTitle");
                                }
                            }
                        }
                    }
                    //New object from JSON's data
                    ListOfNews articleNews = new ListOfNews(webTitle, sectionName, webPublicationDate, webUrl, authorName);
                    //adding articleNews object to a List of News
                    ListOfNews.add(articleNews);
                }
            } else {
                Log.v(LOG_TAG, "No results found");
            }
        } catch (JSONException e) {
            //If there is a problem with JSON formation, catching an error//
            Log.e(LOG_TAG, e.getMessage());
        }
        return ListOfNews;
    }
}
