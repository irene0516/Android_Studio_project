package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PhoneUnlockedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
            Log.d("where","screenOn");
            //Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            //vb.vibrate(new long[]{0,100,1000,100}, 2);      //每秒震動0.1秒,不斷重複
            //Intent mStartActivity = new Intent(context, MainActivity.class);
            Intent mStartActivity = new Intent(context, Page2.class);
            mStartActivity.putExtra("data", "hello_1");
            context.startActivity(mStartActivity);
            //Timer CountdownTimer = new Timer(true);
            //CountdownTimer.schedule(new CountdownTimerTask(), 1000, 3000);
            //wakeMyApp(context);
        }else if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
            Log.d("where","screenOff");
            //Intent mStartActivity = new Intent(context, MainActivity.class);
            Intent mStartActivity = new Intent(context, Page2.class);
            mStartActivity.putExtra("data", "hello_2");
            context.startActivity(mStartActivity);
            //wakeMyApp(context);
        }

    }



}
