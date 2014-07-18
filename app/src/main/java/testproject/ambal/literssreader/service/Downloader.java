package testproject.ambal.literssreader.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

import testproject.ambal.literssreader.R;

/**
 * Created by Ambal on 18.07.14.
 */

public class Downloader extends AsyncTask<String, String, String> {
    private Context mContext;
    private ProgressDialog dialog;

    //данный конструктор нужен чтобы передать контекст в AsyncTask
    public Downloader(Context context) {
        mContext = context;
        dialog = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Please Wait...");
        this.dialog.setIndeterminate(true);
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(params[0]);
        InputStream inputStream = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } else {
                return mContext.getString(R.string.download_error_message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

        /*@Override
        protected void onProgressUpdate(String... result) {
            super.onPostExecute(result[0]);
            if (result[0] == null) {
                Toast.makeText(MainActivity.this, "Error loading...", Toast.LENGTH_LONG).show();
            }
        }*/

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
       /* menuItem.collapseActionView();
        menuItem.setActionView(null);*/
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();

        }


    }
}



