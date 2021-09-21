package com.romoufer.takewater.water;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.romoufer.takewater.bancodedados.ConnectionDatabaseWater;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WaterDAO {

    private ConnectionDatabaseWater conexao;
    private SQLiteDatabase database;

    public WaterDAO(Context context) {
        conexao = new ConnectionDatabaseWater(context);
        database = conexao.getWritableDatabase();
    }

    public long inserir(Water water) {
        ContentValues values = new ContentValues();
        values.put("date", water.getDate());
        values.put("hour", water.getHour());
        values.put("ml", water.getMl());

        return database.insert("water", null, values);
    }

    public List<Water> obterTodos() {
        List<Water> listWater = new ArrayList<>();

        Cursor cursor = database.query("water", new String[] {"id", "date", "hour", "ml"},
                null, null, null, null, "id DESC");

        while(cursor.moveToNext()) {
            Water water = new Water();

            water.setId(cursor.getInt(0));
            water.setDate(cursor.getString(1));
            water.setHour(cursor.getString(2));
            water.setMl(cursor.getInt(3));

            listWater.add(water);
        }

        return listWater;
    }

    public Water getWaterThroughTheId(int id) {
        Water water = null;
        Cursor cursor = database.query("water", new String[] {"id", "date", "hour", "ml"},
                null, null, null, null, null);

        while(cursor.moveToNext()) {
            if (id == cursor.getInt(0)) {
                water = new Water();

                water.setId(cursor.getInt(0));
                water.setDate(cursor.getString(1));
                water.setHour(cursor.getString(2));
                water.setMl(cursor.getInt(3));
            }
        }

        return water;
    }

    public List<Water> getWaterThroughTheDate(String date) {

        List<Water> listWater = new ArrayList<>();

        Cursor cursor = database.query("water", new String[] {"id", "date", "hour", "ml"},
                null, null, null, null, "id DESC");

        while(cursor.moveToNext()) {

            if (date.equals(cursor.getString(1))) {
                Water water = new Water();

                water.setId(cursor.getInt(0));
                water.setDate(cursor.getString(1));
                water.setHour(cursor.getString(2));
                water.setMl(cursor.getInt(3));

                listWater.add(water);
            }

        }

        return listWater;
    }

    public List<Water> getWaterIntervalDate(String date) {

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

        Date dataFinal = null;
        Date dataInicial = null;

        try {
            dataFinal = sdf1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        List<Water> listWater = new ArrayList<>();

        Cursor cursor = database.query("water", new String[] {"id", "date", "hour", "ml"},
                null, null, null, null, "id DESC");

        while(cursor.moveToNext()) {

            try {
                dataInicial = sdf2.parse(cursor.getString(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (dataFinal.after(dataInicial)) {
                System.out.println(dataFinal + " " + dataInicial);
                System.out.println(" i m here");
                break;
            } else {
                Water water = new Water();

                water.setId(cursor.getInt(0));
                water.setDate(cursor.getString(1));
                water.setHour(cursor.getString(2));
                water.setMl(cursor.getInt(3));

                listWater.add(water);
            }

        }

        return listWater;
    }

    public int excluir(int id) {
        int excluidoComSucesso = 0;

        excluidoComSucesso = database.delete("water", "id =" + id, null);

        return excluidoComSucesso;
    }

}
