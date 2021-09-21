package com.romoufer.takewater.bancodedados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConnectionDatabaseSettings extends SQLiteOpenHelper {

    private static final String name = "takewater.db.settingsgeneral";
    private static final int version = 1;

    public ConnectionDatabaseSettings(@Nullable Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table settingsgeneral(id integer primary key autoincrement, " +
                "switchDoAll String(8), " +
                "switchOnlyNotify String(8), " +
                "switchDoNothing String(8), " +
                "timeInterval int(50), " +
                "valueML int(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
