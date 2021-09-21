package com.romoufer.takewater.bancodedados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConnectionDatabaseWater extends SQLiteOpenHelper {

    private static final String name = "takewater.db.water";
    private static final int version = 2;

    public ConnectionDatabaseWater(@Nullable Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table water(id integer primary key autoincrement, " +
                "date varchar(50), " +
                "hour varchar(50), " +
                "ml varchar(50))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
