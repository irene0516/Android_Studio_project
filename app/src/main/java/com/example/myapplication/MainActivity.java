package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    EditText et1, et2;
    static Handler handler;
    String result;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et1 = (EditText) findViewById(R.id.editText1);
        et2 = (EditText) findViewById(R.id.editText2);
        judge();            //系統寫入權限判斷

        handler = new Handler() {                                        //接收登入是否成功訊息
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 123:
                        String ss = (String) msg.obj;
                        Toast.makeText(MainActivity.this, "" + ss, Toast.LENGTH_SHORT).show();
                        if (ss.equals("登入成功")) {
                            Intent it = new Intent(MainActivity.this, Page2.class);
                            startActivity(it);
                        }
                        break;
                }
            }
        };
    }


    //登入
    public void clickOK(View v) {
        String[] columns = {};
        int count = 0;

        //判斷帳號、密碼輸入框是否為空值
        if (et1.getText().toString().equals("") || et2.getText().toString().equals("")) {
            Toast.makeText(this, "請輸入帳密!", Toast.LENGTH_SHORT).show();
        } else {
            //連接伺服端的登入php查詢是否資料庫中有此帳號密碼
            final String url = "http://120.108.111.85/~foodie/class/getuser.php?" + "&username=" + et1.getText().toString() + "&password=" + et2.getText().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpClient httpClinet = new DefaultHttpClient();
                    HttpGet get = new HttpGet(url);
                    try {
                        HttpResponse response = httpClinet.execute(get);
                        HttpEntity resEntity = response.getEntity();
                        result = EntityUtils.toString(resEntity);
                        if (resEntity != null) {
                            Log.d("qqqqqqqqq", result);
                        }
                        Message msg = Message.obtain();
                        //設定Message的內容
                        msg.what = 123;
                        msg.obj = result;
                        //使用MainActivity的static handler來丟Message
                        MainActivity.handler.sendMessage(msg);
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

    //註冊
    public void register(View v) {
        Intent it = new Intent(this, register.class);
        startActivity(it);
    }

    //排名
    public void page3(View v) {
        Intent it = new Intent(this, Page3.class);
        startActivity(it);
    }


    //系統寫入權限判斷
    private void judge() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);

            }
        }
    }


}