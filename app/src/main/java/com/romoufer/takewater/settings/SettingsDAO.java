package com.romoufer.takewater.settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.romoufer.takewater.bancodedados.ConnectionDatabaseSettings;

import java.util.ArrayList;
import java.util.List;

public class SettingsDAO {

    private ConnectionDatabaseSettings conexao;
    private SQLiteDatabase database;

    public SettingsDAO(Context context) {
        conexao = new ConnectionDatabaseSettings(context);
        database = conexao.getWritableDatabase();
    }

    public long inserir() {
        int timeInterval = 60 * 60 * 1000;

        ContentValues values = new ContentValues();
        values.put("switchDoAll", "false");
        values.put("switchOnlyNotify", "true");
        values.put("switchDoNothing", "false");
        values.put("timeInterval", timeInterval);
        values.put("valueML", 150);

        return database.insert("settingsgeneral", null, values);
    }

    public List<SettingsApp> obterTodos() {
        List<SettingsApp> settingsAll = new ArrayList<>();
        Cursor cursor = database.query("settingsgeneral", new String[] {"id", "switchDoAll", "switchOnlyNotify", "switchDoNothing", "timeInterval", "valueML"},
                null, null, null, null, null);

        while(cursor.moveToNext()) {
            SettingsApp settings = new SettingsApp();
            settings.setId(cursor.getInt(0));
            settings.setSwitchDoAll(cursor.getString(1));
            settings.setSwitchOnlyNotify(cursor.getString(2));
            settings.setSwitchDoNothing(cursor.getString(3));
            settings.setTimeInterval(cursor.getInt(4));
            settings.setValueML(cursor.getInt(5));

            settingsAll.add(settings);
        }

        return settingsAll;
    }

    public int updade(int id, String switchDoAll, String switchOnlyNotify, String switchDoNothing, int timeInterval, int valueML) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("switchDoAll", switchDoAll); //These Fields should be your String values of actual column names
        contentValues.put("switchOnlyNotify", switchOnlyNotify);
        contentValues.put("switchDoNothing", switchDoNothing);
        contentValues.put("timeInterval", timeInterval);
        contentValues.put("valueML", valueML);

        return database.update("settingsgeneral", contentValues, "id = " + id, null);
    }
}
