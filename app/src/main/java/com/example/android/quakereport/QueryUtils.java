 package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
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

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**

     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<EarthQuake>fetchEarthquakeData(String requestUrl){
        // create Url object using the Createurl method.

        URL url=createUrl(requestUrl);

        //perform httpRequest by using the url object and receive jsonResponse.
        String json=null;
        try {
             json = makeHttpRequest(url);
        }catch ( IOException e){
            Log.e(LOG_TAG,"problem in making httpRequest,e");
        }
        // extract the list of earthquakes info from the returned json String and store it in list.
        List<EarthQuake> earthQuakeList = extractFeatureFromJson(json);
        return earthQuakeList;


    }
    //  Returns a new url of the given String.
    private static URL createUrl(String stringUrl){
        URL url=null;
        try{
            url =new URL(stringUrl);

        }
        catch (MalformedURLException e){
            Log.e(LOG_TAG,"problem building the url",e);
        }
        return url;
    }

    //make http request using the given url and return a string as the response.
    private static String  makeHttpRequest(URL url) throws IOException{
        String jsonResponse="";
        if(url== null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection=null;
        InputStream inputStream=null;
        try{
            urlConnection =(HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // if the request was successful and the response code was 200
            //then read the input Stream
            if(urlConnection.getResponseCode()==200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse= readFromStream(inputStream);
            }
            else{
                Log.e(LOG_TAG,"Error Response code"+urlConnection.getResponseCode());
            }

        }
        catch (IOException e){
            Log.e(LOG_TAG,"Problem Reterieving the EarthQuake Json results",e);

        }
        finally{
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(inputStream!=null){
                inputStream.close();
            }
        }
        return  jsonResponse;
    }
    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output=new StringBuilder();
        if(inputStream!=null){
            /*An InputStreamReader is a bridge from byte streams to character streams:
            It reads bytes and decodes them into characters using a specified charset.
            The charset that it uses may be specified by name or may be given explicitly,
             or the platform's default charset may be accepted.
             */
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream, Charset.forName("UTF-8"));
           /* Reads text from a character-input stream,
           buffering characters so as to provide for the efficient reading of characters, arrays, and lines.
           The buffer size may be specified, or the default size may be used. The default is large enough for most purposes.*/
            BufferedReader reader=new BufferedReader(inputStreamReader);
            String line=reader.readLine();
            while (line!=null){
                output.append(line);
                line=reader.readLine();
            }

        }
        return output.toString();
    }
    /*Return a list of {@link Earthquake} objects that has been built up from
      parsing the given JSON response.
       */
    private static  List<EarthQuake> extractFeatureFromJson(String earthQuakeJson){
        // check if the json String is empty or null.
        if(TextUtils.isEmpty(earthQuakeJson)){
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        List<EarthQuake> earthquakes = new ArrayList<>();
        // Try to parse the earthquakeJson String. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            
            // build up a list of Earthquake objects with the corresponding data.
            JSONObject baseJsonResponse=new JSONObject(earthQuakeJson);
            JSONArray earthQuakeArray=baseJsonResponse.getJSONArray("features");
            for(int i=0;i<earthQuakeArray.length();i++){
                JSONObject currentEarthquake=earthQuakeArray.getJSONObject(i);
                JSONObject properties=currentEarthquake.getJSONObject("properties");
                double magnitude=properties.getDouble("mag");
                String location=properties.getString("place");
                String primaryLocation=properties.getString("place");
                long time=properties.getLong("time");
                String url=properties.getString("url");
                EarthQuake earthquake =new EarthQuake(magnitude,location,primaryLocation,time,url);
                earthquakes.add(earthquake);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;

    }
}