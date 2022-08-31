package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class Page3_1 extends AppCompatActivity {
    WebView myWebView;
    String intentMsg;
    EditText et3,et4;
    public ImageView iv;
    public Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page3_1);
        et3=(EditText)findViewById(R.id.editText3);
        et4=(EditText)findViewById(R.id.editText4);
        //請求伺服器的cheer.jpg圖片顯示在imageView中
        new DownloadImageTask((ImageView)findViewById(R.id.imageView100)).execute("http://120.108.118.154/cheer.jpg");
        myWebView=(WebView)findViewById(R.id.webview);
        //myWebView.loadUrl("http://120.108.118.154/hello.php");
        //myWebView.loadUrl("http://www.youtube.com/watch?v=3kdb-KlqoLs");

        Intent intentScreen=getIntent();
        if(intentScreen.hasExtra("data")){
            intentMsg=intentScreen.getStringExtra("data");
            Log.d("where",""+intentMsg);
        }
    }

    //返回前一頁
    public void clickBack(View v){
        finish(); // 結束 Activity, 即可回到前一個 Activity
    }

    //查看排名
    public void clickSee(View v){
        //在webView中顯示請求結果
        myWebView.loadUrl("http:/120.108.111.85/~foodie/class/loaduser.php?userName="+intentMsg+"&startTime="+et3.getText().toString()+"&endTime="+et4.getText().toString());
    }

    //取得網路上的圖片
    private class DownloadImageTask extends AsyncTask<String,Void, Bitmap>{
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage){
            this.bmImage=bmImage;

        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urldisplay=strings[0];
            Bitmap mIcon11=null;
            try{
                InputStream in =new java.net.URL(urldisplay).openStream();
                mIcon11= BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("where",e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result){
            bmImage.setImageBitmap(result);
        }
    }


}
