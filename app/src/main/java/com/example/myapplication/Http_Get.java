package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Http_Get extends Service {
    private String getUrl;
    public void Get(String url) {
        this.getUrl=url;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClinet=new DefaultHttpClient();
                HttpGet get=new HttpGet(getUrl);
                try{
                    HttpResponse response=httpClinet.execute(get);
                    HttpEntity resEntity=response.getEntity();
                    Log.d("Response of GET request", EntityUtils.toString(resEntity));
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            public void get(View v){


            }
        }).start();


    }
    public static String ConvertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error=" + e.toString());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                System.out.println("Error=" + e.toString());
            }
        }
        return sb.toString();

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
