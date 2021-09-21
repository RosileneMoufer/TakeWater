package com.romoufer.takewater.singleton;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.romoufer.takewater.notificationreceiver.AlertReceiver;
import com.romoufer.takewater.settings.SettingsApp;
import com.romoufer.takewater.settings.SettingsDAO;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class Alarm {

   static Alarm instance;

    public static Alarm getInstance() {
        if (instance == null) {
            instance = new Alarm();
        }
        return instance;
    }

    public void ativarAlarme(Context context) {
        SettingsDAO settingsDAO = new SettingsDAO(context);
        List<SettingsApp> listSettingsApp = settingsDAO.obterTodos();

        int nextAlarm = 0;

        Intent alertReceiver = new Intent(context, AlertReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(context, 0, alertReceiver, 0);

        for (int i = 0; i < listSettingsApp.size(); i++) {
            nextAlarm = listSettingsApp.get(i).getTimeInterval();
        }

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
        long time = calendar.getTimeInMillis();
        //alarmManager.set(AlarmManager.RTC_WAKEUP, time, p);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, nextAlarm, p);
    }
}
