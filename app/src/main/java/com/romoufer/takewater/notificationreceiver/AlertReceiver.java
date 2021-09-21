package com.romoufer.takewater.notificationreceiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.romoufer.takewater.settings.SettingsApp;
import com.romoufer.takewater.settings.SettingsDAO;
import com.romoufer.takewater.singleton.AudioMusic;

import java.util.List;

import com.romoufer.takewater.R;

public class AlertReceiver extends BroadcastReceiver {

    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";

    @Override
    public void onReceive(Context context, Intent intent) {

        SettingsDAO settingsDAO = new SettingsDAO(context);
        List<SettingsApp> listSettingsApp = settingsDAO.obterTodos();

        for (int i = 0; i < listSettingsApp.size(); i++) {

            if (listSettingsApp.get(i).getSwitchDoAll().equals("true")) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                Ringtone r = RingtoneManager.getRingtone(context, notification);
                AudioMusic.getInstance().setRingtone(r);
                r.play();

                notificar(context);
            } else if (listSettingsApp.get(i).getSwitchOnlyNotify().equals("true")) {
                notificar(context);
            }

        }

    }

    public void notificar(Context context) {

        Intent intentStopAlarm = new Intent(context, StopAlarmReceiver.class);
        PendingIntent pIntentStopAlarm = PendingIntent.getBroadcast(context, 0, intentStopAlarm, 0);

        Intent intentAdiar = new Intent(context, AdiarReceiver.class);
        PendingIntent pIntentSeeMore = PendingIntent.getBroadcast(context, 0, intentAdiar, 0);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        nb.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.its_time_to_drink_some_water))
                .setChannelId(channelID)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .addAction(R.mipmap.ic_launcher, context.getResources().getString(R.string.drink), pIntentStopAlarm)
                .addAction(R.mipmap.ic_launcher, context.getResources().getString(R.string.put_off), pIntentSeeMore);

        notificationHelper.getManager().notify(0, nb.build());
    }

}
