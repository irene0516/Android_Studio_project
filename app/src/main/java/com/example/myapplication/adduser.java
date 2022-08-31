package com.example.myapplication;

import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class adduser {
    private String result;
    private String geturl;
    private String gettype;
    public void Get(String url, String type) {
        this.geturl=url;
        this.gettype=type;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClinet=new DefaultHttpClient();
                HttpGet get=new HttpGet(geturl);
                try{
                    HttpResponse response=httpClinet.execute(get);
                    HttpEntity resEntity=response.getEntity();
                    result= EntityUtils.toString(resEntity);
                    if (resEntity != null){
                        Log.d("qqqqqqqqq",result);
                    }
                    Message msg = Message.obtain();
                    //設定Message的內容
                    msg.what = 123;
                    msg.obj=result;
                    //使用MainActivity的static handler來丟Message
                    if(gettype=="1"){
                       MainActivity.handler.sendMessage(msg);
                    }else{
                        register.handler.sendMessage(msg);
                    }

                    // Log.d("Response of GET request", EntityUtils.toString(resEntity));


                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
