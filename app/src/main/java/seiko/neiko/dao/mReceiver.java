package seiko.neiko.dao;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import seiko.neiko.R;

/**
 * Created by Seiko on 2017/1/22. Y
 * 加载时间、电池服务
 */

public class mReceiver {

    private BatteryReceiver batteryReceiver;
    private DateReceiver dateReceiver;

    private Activity activity;
    private TextView battery;
    private TextView time;

    public mReceiver(Activity activity) {
        this.activity = activity;
        this.battery = (TextView) activity.findViewById(R.id.section_battery);
        this.time = (TextView) activity.findViewById(R.id.section_time);
        addReceiver();
    }

    private void addReceiver() {
        batteryReceiver = new BatteryReceiver();
        activity.registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        dateReceiver = new DateReceiver();
        activity.registerReceiver(dateReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        getNowDate();  //第一次读取时间
    }

    class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra("level", 0);     // 获取当前电量
                battery.setText(String.format(Locale.US, "  %d%s", level, "%"));
            }
        }
    }

    class DateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                getNowDate();  // new
            }
        }
    }

    //获得当前时间
    private void getNowDate() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.US);// 设置日期格式
        String date = df.format(new Date());
        time.setText(String.format(Locale.US, "  %s", date));
    }

    //============================
    /** 注销服务 */
    public void unSub() {
        if (activity != null) {
            activity.unregisterReceiver(batteryReceiver);
            activity.unregisterReceiver(dateReceiver);
        }
    }

}
