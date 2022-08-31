package com.example.myapplication;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import android.os.Build;
import android.os.Bundle;


import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class Page2 extends AppCompatActivity {
    int tocSec = 0;
    Spinner hour;
    Spinner minute;
    int flagNoti = 0;
    String homeName = "";
    static Handler handler;
    static final String db_name = "testDB";    // 資料庫名稱
    static final String tb_name = "test";        // 資料表名稱
    SQLiteDatabase db;    //資料庫
    TextView txv;
    ListView lv1;
    //Cursor cursor;
    List<String> list = new ArrayList<>();
    String intentMsg = "";
    private PhoneUnlockedReceiver phoneUnlockedReceiver;
    private SharedPreferences prefs;
    ArrayList<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter adapter2;
    int tic = (int) (System.currentTimeMillis() / 1000);
    int toc = (int) (System.currentTimeMillis() / 1000);
    int brightness;
    int cnt = 0;
    String sT0 = "0";
    String sTend = "0";
    int dt = 0;
    Http_Get HG;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        //取得目前螢幕亮度
        brightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);
        toc = (int) (System.currentTimeMillis() / 1000);
        Log.d("where", "" + toc);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String filename = prefs.getString(getString(R.string.filename_key), "yyyyMMddhhmmss");
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(filename);
        String timeStr1 = formatter.format(today);
        HG = new Http_Get();
        phoneUnlockedReceiver = new PhoneUnlockedReceiver();
        Intent intentScreen = getIntent();
        if (intentScreen.hasExtra("data")) {
            String intentMsg = intentScreen.getStringExtra("data");
            Log.d("where", intentMsg);
            if (intentMsg.equals("hello_2")) {
                db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
                addScreen("screenOff", timeStr1, "" + toc);
                db.close(); // 關閉資料庫
            }
            if (intentMsg.equals("hello_1")) {
                db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
                addScreen("screenOn", timeStr1, "" + toc);
                db.close(); // 關閉資料庫
            }
        }

        hour = findViewById(R.id.spinner_hour);
        minute = findViewById(R.id.spinner_minute);
        txv = (TextView) findViewById(R.id.txv);
        lv1 = findViewById(R.id.listView1);
        if (!isAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
        Timer CountdownTimer = new Timer(true);
        CountdownTimer.schedule(new CountdownTimerTask(), 1000, 3000);


        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 123:
                        String ss = (String) msg.obj;
                        Toast.makeText(Page2.this, "" + ss, Toast.LENGTH_SHORT).show();
                        if (ss.equals("timeup")) {
                            //設定的提醒時間到時螢幕亮度降為10
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 10);
                        }
                        break;
                }
            }
        };
    }

    //設定時間到達時通知
    public void clickOK3(View v) {
        Intent intent = new Intent(
                this, SettingsActivity.class);

        startActivity(intent);
    }

    //設定時間到達時震動且螢幕亮度漸暗
    public void clickOK4(View v) {
        pickTime(0);
        flagNoti = 1;
        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vb.cancel();
    }

    //建資料表
    public void clickOK5(View v) {
        //建立DB
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        String createTable = "CREATE TABLE IF NOT EXISTS " + tb_name + "(screenState VARCHAR(64), " + "screenTime VARCHAR(64), " + "tictoc VARCHAR(64))";
        db.execSQL(createTable);    // 建立資料表
        db.delete(tb_name, null, null); // 清除資料
        txv.setText("資料庫檔路徑: " + db.getPath() + "\n\n" +   // 取得及顯示資料庫資訊
                "資料庫分頁大小: " + db.getPageSize() + " Bytes\n\n" +
                "資料庫大小上限: " + db.getMaximumSize() + " Bytes");
        db.close(); // 關閉資料庫
    }

    //讀取
    public void clickOK6(View v) {
        txv.setText("");
        lv1.setVisibility(View.VISIBLE);
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        int dataN = 0;
        String[] columns = {};
        Cursor cursor = db.query(tb_name, columns, null, null, null, null, null);
        StringBuilder resultData = new StringBuilder("資料:\n");
        list2.clear();
        while (cursor.moveToNext()) {
            String sS = cursor.getString(0);
            String sT = cursor.getString(1);
            String tt = cursor.getString(2);
            resultData.append(sS).append("\n");
            resultData.append(sT).append("\n");
            resultData.append(tt).append("\n");
            dataN = dataN + 1;
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("sS", sS);
            item.put("sT", sT + "," + tt);
            list2.add(item);
        }
        db.close();
        adapter2 = new SimpleAdapter(this, list2, android.R.layout.simple_expandable_list_item_2, new String[]{"sS", "sT"}, new int[]{android.R.id.text1, android.R.id.text2});
        // ArrayAdapter<String> adapter=new ArrayAdapter<>(Page2.this,android.R.layout.simple_list_item_1,list);
        lv1.setAdapter(adapter2);
    }

    //統計
    public void clickOK7(View v) {
        /*dataCnt=dataCnt+1;
        db=openOrCreateDatabase(db_name, Context.MODE_PRIVATE,null);
        addData("A"+dataCnt,"B"+dataCnt,"C"+dataCnt);
        db.close();*/
        String sT = "";
        sT0 = "";
        sTend = "";
        int[] onT = {0, 0};
        int[] offT = {0, 0};
        int onCnt = 0;
        int offCnt = 0;
        int dataCnt = 0;
        dt = 0;
        txv.setText("");
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        String[] columns = {};
        Cursor cursor = db.query(tb_name, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            dataCnt = dataCnt + 1;
            String sS = cursor.getString(0);
            sT = cursor.getString(1);
            String tt = cursor.getString(2);
            if (dataCnt == 1) {
                sT0 = sT;
            }
            if (sS.equals("screenOn")) {
                tic = Integer.parseInt(tt);
                onCnt += 1;
                onT[onCnt] = tic;
                if ((onT[onCnt] - onT[onCnt - 1]) < 5) {
                    onCnt -= 1;

                } else {
                    onT = Arrays.copyOf(onT, onT.length + 1);

                }
            }
            if (sS.equals("screenOff") && onCnt >= 1) {
                toc = Integer.parseInt(tt);
                offCnt += 1;
                offT[offCnt] = toc;
                if ((offT[offCnt] - offT[offCnt - 1]) < 5) {
                    offCnt -= 1;

                } else {
                    offT = Arrays.copyOf(offT, offT.length + 1);

                }
            }

        }
        sTend = sT;
        db.close();
        StringBuilder resultData = new StringBuilder("資料:\n");
        for (int i = 0; i < (offT.length - 1); i++) {
            dt = dt + offT[i] - onT[i];
        }
        txv.setText(sT0 + "_" + sTend + "使用手機" + dt + "sec" + "\n\n");
    }

    //上傳
    public void clickOK8(View v) {
        txv.setText("");
        lv1.setVisibility(View.INVISIBLE);
        Log.d("bo", "" + sT0);
        String url = "http://120.108.111.85/~foodie/class/adduser.php?userName=" + "u0" + "&startTime=" + sT0 + "&endTime=" + sTend + "&playTime=" + dt;
        //String url="http://120.108.111.85/~foodie/class/adduser.php?userName="+"n0"+"&startTime=0&endTime=10&playTime=50";
        HG.Get(url);
    }


    public void pickTime(int index) {
        //計時器取得時間
        if (index == 0) {
            String[] hours = getResources().            // 取得字串資源中
                    getStringArray(R.array.hour);    // 的字串陣列
            String[] minutes = getResources().            // 取得字串資源中
                    getStringArray(R.array.minute);    // 的字串陣列
            int indexHour = hour.getSelectedItemPosition();    // 取被選取的項目
            int indexMinute = minute.getSelectedItemPosition();    // 取被選取的項目
            int intHour = Integer.valueOf(hours[indexHour]);
            int intMinute = Integer.valueOf(minutes[indexMinute]);
            String timeStr = hours[indexHour] + " 小時  " + minutes[indexMinute] + " 分鐘";
            tocSec = intHour * 60 * 60 + intMinute * 60;
            Log.d("where", "設定 " + tocSec + " sec");
        }
        //時間以3為單位漸減
        if (index == 1) {
            tocSec = tocSec - 3;

            if (tocSec <= 0) {
                tocSec = 0;
            }
            Log.d("where", "剩下 " + tocSec + " sec");

        }
    }

    //權限判斷
    public boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            android.content.pm.ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //計時器
    public class CountdownTimerTask extends TimerTask {
        public void run() {
            if (!isApplicationBroughtToBackground()) {
                Log.d("where", "Application is foreground");
            } else {
                try {
                    printForegroundTask();
                    //Log.d("where","Application is background: "+printForegroundTask());
                    pickTime(1);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if ((flagNoti == 1) && (tocSec == 0)) {
                    Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(new long[]{0, 100, 1000, 100}, 2);      //每秒震動0.1秒,不斷重複

                    flagNoti = 0;
                }
            }
        }
    }

    ;

    public String printForegroundTask() throws PackageManager.NameNotFoundException {
        String currentApp = "NULL";
        //String currentApp2 = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    PackageManager pm = this.getPackageManager();
                    PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(currentApp, 0);
                    String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
                    Log.d("where", "app name: " + foregroundTaskAppName);
                    /*
                    db = openOrCreateDatabase(db_name,  Context.MODE_PRIVATE, null);
                    addData(foregroundTaskAppName,currentApp,"com.huawei.android.launcher");
                    db.close(); // 關閉資料庫
                    */
                    //Log.d("where","cnt = "+cnt);
                    /*
                    if (cnt == 2) {
                        PackageManager packageManager = getPackageManager();
                        Intent intentControl = new Intent();
                        intentControl = packageManager.getLaunchIntentForPackage("com.orpheusdroid.screenrecorder");
                        if (intentControl == null) {
                            Log.d("where", "Application is background:  APP not found!");
                        } else {
                            Log.d("where", "Application is background:  APP open OK !");
                            startActivity(intentControl);
                        }
                    }
                    */
                }
            }

            cnt = cnt + 1;
            if (cnt == 10) {
                cnt = 2;
            }

            //int homepage=0;

            if (cnt == 1) {
                for (UsageStats stat : appList) {
                    //homepage=homepage+1;
                    //if (homepage==2) {
                    String packageName = stat.getPackageName();
                    homeName = packageName;
                    Log.d("where", "all package names: " + packageName);
                    //ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    //String pkgName=(manager.getRunningTasks(1).get(0)).topActivity.getPackageName();
                    //Log.d("where", "pos2: "+ pkgName);
                    PackageManager pm = this.getPackageManager();
                    PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(packageName, 0);
                    String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
                    Log.d("where", "all app names: " + foregroundTaskAppName);
                    //}
                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        //Log.d("where", "Current App in foreground is: " + currentApp);
        return currentApp;
    }

    public void addData(String appName, String appPackage, String screenPackage) {
        ContentValues cv = new ContentValues(3);    // 建立含3個資料項目的物件
        cv.put("appName", appName);
        cv.put("appPackage", appPackage);
        cv.put("screenPackage", screenPackage);
        db.insert(tb_name, null, cv);    // 將資料加到資料表
    }

    public void addScreen(String screenState, String screenTime, String tictoc) {
        ContentValues cv = new ContentValues(3);    // 建立含3個資料項目的物件
        cv.put("screenState", screenState);
        cv.put("screenTime", screenTime);
        cv.put("tictoc", tictoc);
        db.insert(tb_name, null, cv);    // 將資料加到資料表
    }

    public boolean isApplicationBroughtToBackground() {
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(this.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public void goBack(View v) {
        finish(); // 結束 Activity, 即可回到前一個 Activity
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(phoneUnlockedReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(phoneUnlockedReceiver);
    }

}
