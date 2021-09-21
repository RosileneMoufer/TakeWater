package com.romoufer.takewater.notificationreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.romoufer.takewater.R;
import com.romoufer.takewater.settings.SettingsApp;
import com.romoufer.takewater.settings.SettingsDAO;
import com.romoufer.takewater.singleton.AudioMusic;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;


public class AdiarReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent alertReceiver = new Intent(context, AlertReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(context, 0, alertReceiver, 0);


        // setando horário do alarme
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)); //alarmHour from TextView
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)); //alarmMinute from TextView
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        // ativando o alarme
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        long time = calendar.getTimeInMillis() + (10 * 60 * 1000);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, p);


        SettingsDAO settingsDAO = new SettingsDAO(context);
        List<SettingsApp> listSettingsApp = settingsDAO.obterTodos();

        for (int i = 0; i < listSettingsApp.size(); i++) {
            if (listSettingsApp.get(i).getSwitchDoAll().equals("true")) {
                AudioMusic.getInstance().getRingtone().stop();
            }
        }

        Toast.makeText(context, R.string.remind, Toast.LENGTH_SHORT).show();

    }
}
