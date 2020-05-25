package com.example.booklisting;

import android.net.UrlQuerySanitizer;
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

public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private QueryUtils(){}
    public static ArrayList<word> fetchData(String requestUrl){
        URL url=createUrl(requestUrl);
        String jsonResponse=null;
        jsonResponse = makeHttpRequest(url);
        return extractFromJson(jsonResponse);
    }

    private static ArrayList<word> extractFromJson(String jsonResponse) {
        ArrayList<word> books=new ArrayList<word>();
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(jsonResponse);
            JSONArray items=jsonObject.getJSONArray("items");
            for(int i=0;i<items.length();i++){
                JSONObject currentBook=items.getJSONObject(i);
                JSONObject volumeInfo=currentBook.getJSONObject("volumeInfo");
                    String title=volumeInfo.getString("title");
                    String authors;
                    try {
                        authors=volumeInfo.getJSONArray("authors").toString();
                    }catch (JSONException e){
                        authors="N/A";
                    }
                    String publisher=volumeInfo.getString("publisher");
                    String publishedDate="N/A";
                    try {
                        publishedDate = volumeInfo.getString("publishedDate");
                    }catch (JSONException e){
                        publishedDate="N/A";
                    }
                    String description;
                    try {
                        description = volumeInfo.getString("description");
                    }catch (JSONException e){
                        description="N/A";
                    }
                    String previewLink=volumeInfo.getString("previewLink");
                    JSONObject imageLinks=volumeInfo.getJSONObject("imageLinks");
                        String thumbnail=imageLinks.getString("thumbnail").replace("http://","https://");
                JSONObject saleInfo=currentBook.getJSONObject("saleInfo");
                    String amount;
                    try{
                        JSONObject listPrice=saleInfo.getJSONObject("listPrice");
                        amount=listPrice.get("amount").toString();
                    }catch (JSONException e){
                        amount="N/A";
                    }
                books.add(new word(title,authors,publisher,publishedDate,description,amount,thumbnail,previewLink));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }

    private static String makeHttpRequest(URL url) {
        String jsonResponse="";
        if(url==null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection=null;
        InputStream inputStream=null;
        try{
            urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode()==200){
                inputStream=urlConnection.getInputStream();
                jsonResponse=readFromStream(inputStream);
            }else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing input stream", e);
                }
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String line=null;
            line=bufferedReader.readLine();
            while(line!=null){
                stringBuilder.append(line);
                line=bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    private static URL createUrl(String requestUrl) {
        URL url=null;
        try{
            url=new URL(requestUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
