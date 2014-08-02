package testproject.ambal.literssreader.service;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ambal on 18.07.14.
 */
public class Downloader {
    private static final String LOG_TAG = "mylogs";

    public static final String CREATED = "created";
    public static final String UPDATED = "updated";
    public static final String STATUS_CODE = "status";
    public static final String STATUS_SUCCESS = "OK";
    public static final String STATUS_MSG = "msg";
    public static final String RESULT = "result";

    Map<String, String> download(String url) {

        Map<String, String> resultMap = new HashMap<String, String>(5);
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            resultMap.put(STATUS_CODE, String.valueOf(statusLine.getStatusCode()));
            resultMap.put(STATUS_MSG, statusLine.getReasonPhrase());
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                resultMap.put(RESULT, stringBuilder.toString());
                return resultMap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultMap.put(STATUS_MSG, e.getMessage());
            //Log.e(LOG_TAG, String.valueOf(e.getMessage()));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultMap;
    }

}
