package com.example.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class register extends AppCompatActivity {
    EditText et1,et2,et3;
    static Handler handler;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et1=(EditText)findViewById(R.id.editText1);
        et2=(EditText)findViewById(R.id.editText2);
        et3=(EditText)findViewById(R.id.editText3);
        handler = new Handler(){
            public void handleMessage(Message msg){             //接收註冊是否成功的訊息
                switch (msg.what){
                    case 123:
                        String ss = (String)msg.obj;
                        Toast.makeText(register.this, ss,Toast.LENGTH_LONG).show();
                        if(ss.equals("註冊成功")){
                            Intent it=new Intent(register.this, MainActivity.class);
                            startActivity(it);
                        }
                        break;
                }
            }
        };
    }

    //註冊功能
    public void Click(View v){
        //判斷註冊之帳號框、密碼框、再次輸入密碼框是否為空值
        if(et1.getText().toString().equals("")||et2.getText().toString().equals("")||et3.getText().toString().equals("")){
            Toast.makeText(register.this, "請輸入帳密",Toast.LENGTH_LONG).show();
        }else{
            if(et2.getText().toString().equals(et3.getText().toString())){

                //將註冊之帳號密碼連接到伺服端
                final String url="http://120.108.111.85/~foodie/class/insertuser.php?"+"&username="+et1.getText().toString()+"&password="+et2.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpClient httpClinet=new DefaultHttpClient();
                        HttpGet get=new HttpGet(url);
                        try{
                            HttpResponse response=httpClinet.execute(get);
                            HttpEntity resEntity=response.getEntity();
                            result=EntityUtils.toString(resEntity);
                            if (resEntity != null){
                                Log.d("qqqqqqqqq",result);
                            }
                            Message msg = Message.obtain();
                            //設定Message的內容
                            msg.what = 123;
                            msg.obj=result;
                            //使用register的static handler來丟Message
                            register.handler.sendMessage(msg);
                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }else{
                Toast.makeText(register.this, "請輸入兩次相同密碼",Toast.LENGTH_LONG).show();
            }
        }


    }

    //返回首頁
    public void Goback(View v){
        finish();
    }
}
