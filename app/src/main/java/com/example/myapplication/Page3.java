package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Page3 extends AppCompatActivity {
    int [] resId = {R.drawable.icon_01, R.drawable.icon_02, R.drawable.icon_03,
            R.drawable.icon_04, R.drawable.icon_05, R.drawable.icon_06, R.drawable.icon_07,
            R.drawable.icon_08, R.drawable.icon_09, R.drawable.dog};
    String [] data = {"陳一", "林二", "張三", "李四", "王五",
            "小六", "川七", "老八", "馬九", "全十"};
    String [] email = {"chen1@gmail.com", "lin2@hotmail.com", "chang3@hinet.net",
            "lee4@yahoo.com", "wang5@seednet.net", "little6@school.edu",
            "river7@church.org", "elder8@center.net", "ma9@roc.gov", "all10@all10.idv"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page3);
        View v = getLayoutInflater().inflate(R.layout.activity_page3, null);
        LinearLayout layout1 = (LinearLayout) v.findViewById(R.id.linearLayout1);
        // 在自製 ListView 之前，擺放一個 TextView, 作為顯示電郵訊息之用
        final TextView emailContent = (TextView) v.findViewById(R.id.textView1);
        emailContent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                emailContent.setVisibility(View.GONE);
            }
        });
        for(int i=0; i<resId.length; i++) {
            // 以 LayoutInflater的方式，實體化LinearLayout
            LinearLayout layout2 = (LinearLayout) getLayoutInflater().inflate(
                    R.layout.my_image_and_text, null);
            //layout2.setGravity(Gravity.CENTER_VERTICAL);
            // 為 LinearLayout 註冊 OnClick,並設定Tag作為編號
            layout2.setTag(i);
            layout2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int index = (Integer) v.getTag();			// 取得所點擊的Tag編號
                    //前3個人可做排名
                    if(index>=0&&index<=2){
                        Intent intentService=new Intent(Page3.this,Page3_1.class);
                        intentService.putExtra("data","u"+index);
                        startActivity(intentService);
                    }else{
                        emailContent.setVisibility(View.VISIBLE);	// 將Email的TextView打開能見度
                        emailContent.setText(data[index] +"的電子郵件: "+ email[index]);
                    }

                }});
            /* 以 LayoutInflater的方式，實體化TextView, 並設定 TextView 的內容 */
            TextView tv = (TextView) layout2.findViewById(R.id.textView1);
            tv.setText(data[i]);
            tv.setTextSize(20);
            tv.setPadding(10, 10, 10, 10);
            /* 以 LayoutInflater的方式，實體化ImageView, 並設定 ImageView 的內容 */
            ImageView iv = (ImageView) layout2.findViewById(R.id.imageView1);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId[i]);
            bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
            iv.setImageBitmap(bitmap);
            iv.setPadding(10, 10, 10, 10);
            /* 將每一筆 layout2 都加進 LinearLayout1內*/
            layout1.addView(layout2);
            // 新增一條分隔線，加進 LinearLayout1內
            ImageView segment = new ImageView(this);
            segment.setImageResource(R.drawable.segment);
            segment.setScaleType(ImageView.ScaleType.FIT_XY);
            layout1.addView(segment, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        // 最後以 Root Layout 作為整個視窗的內容視圖
        setContentView(v);
    }

    //返回前一頁
    public void Goback(View v){
        finish();
    }

}
