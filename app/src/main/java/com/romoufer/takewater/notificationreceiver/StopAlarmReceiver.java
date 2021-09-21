package com.romoufer.takewater.notificationreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.romoufer.takewater.SettingsActivity;
import com.romoufer.takewater.singleton.Alarm;
import com.romoufer.takewater.water.Water;
import com.romoufer.takewater.water.WaterDAO;
import com.romoufer.takewater.settings.SettingsApp;
import com.romoufer.takewater.settings.SettingsDAO;
import com.romoufer.takewater.singleton.AudioMusic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;


public class StopAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SettingsDAO settingsDAO = new SettingsDAO(context);
        List<SettingsApp> listSettingsApp = settingsDAO.obterTodos();

        int valueML = 0;

        for (int i = 0; i < listSettingsApp.size(); i++) {
            if (listSettingsApp.get(i).getSwitchDoAll().equals("true")) {
                AudioMusic.getInstance().getRingtone().stop();
            }

            valueML = listSettingsApp.get(i).getValueML();
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

        String[] dateHour = getDateString();

        WaterDAO waterDAO = new WaterDAO(context);
        Water water = new Water();
        water.setDate(dateHour[0]);
        water.setHour(dateHour[1]);
        water.setMl(valueML);
        waterDAO.inserir(water);

        Alarm.getInstance().ativarAlarme(context);

    }

    private String[] getDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        String[] result;
        String data_hour = dateFormat.format(cal.getTime());
        result = data_hour.split(" ");

        return result;
    }

}
