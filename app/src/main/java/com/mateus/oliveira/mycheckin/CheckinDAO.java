package com.mateus.oliveira.mycheckin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class CheckinDAO {

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public CheckinDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public List<String> getAllLocalNames() {
        List<String> names = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.TABLE_CHECKIN, new String[]{DBHelper.COLUMN_CHECKIN_LOCAL}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHECKIN_LOCAL)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return names;
    }

    public List<String> getAllCategoryNames() {
        List<String> names = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.TABLE_CATEGORIA, new String[]{DBHelper.COLUMN_CATEGORIA_NOME}, null, null, null, null, DBHelper.COLUMN_CATEGORIA_ID + " ASC");
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORIA_NOME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return names;
    }

    public boolean checkinExists(String localName) {
        Cursor cursor = db.query(DBHelper.TABLE_CHECKIN, new String[]{DBHelper.COLUMN_CHECKIN_LOCAL},
                DBHelper.COLUMN_CHECKIN_LOCAL + " = ?", new String[]{localName}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void insertNewCheckin(String local, int categoryId, String latitude, String longitude) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CHECKIN_LOCAL, local);
        values.put(DBHelper.COLUMN_CHECKIN_VISITAS, 1);
        values.put(DBHelper.COLUMN_CHECKIN_CAT_ID, categoryId);
        values.put(DBHelper.COLUMN_CHECKIN_LATITUDE, latitude);
        values.put(DBHelper.COLUMN_CHECKIN_LONGITUDE, longitude);
        db.insert(DBHelper.TABLE_CHECKIN, null, values);
    }

    public void incrementCheckin(String localName) {
        String query = "UPDATE " + DBHelper.TABLE_CHECKIN +
                " SET " + DBHelper.COLUMN_CHECKIN_VISITAS + " = " + DBHelper.COLUMN_CHECKIN_VISITAS + " + 1" +
                " WHERE " + DBHelper.COLUMN_CHECKIN_LOCAL + " = ?";
        db.execSQL(query, new String[]{localName});
    }

    // --- Operações para a Tela de Gestão ---

    public void deleteCheckin(String localName) {
        db.delete(DBHelper.TABLE_CHECKIN, DBHelper.COLUMN_CHECKIN_LOCAL + " = ?", new String[]{localName});
    }

    // --- Operações para a Tela de Mapa e Relatório (precisaremos de um objeto para carregar os dados) ---
    // (Vamos criar uma classe CheckinData.java para isso)

    public List<CheckinData> getAllCheckins() {
        List<CheckinData> checkins = new ArrayList<>();
        // Query que faz o JOIN entre as tabelas para pegar o nome da categoria
        String query = "SELECT c.*, cat." + DBHelper.COLUMN_CATEGORIA_NOME +
                " FROM " + DBHelper.TABLE_CHECKIN + " c" +
                " JOIN " + DBHelper.TABLE_CATEGORIA + " cat ON c." + DBHelper.COLUMN_CHECKIN_CAT_ID + " = cat." + DBHelper.COLUMN_CATEGORIA_ID;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                CheckinData data = new CheckinData();
                data.setLocal(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHECKIN_LOCAL)));
                data.setQtdVisitas(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHECKIN_VISITAS)));
                data.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHECKIN_LATITUDE)));
                data.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHECKIN_LONGITUDE)));
                data.setCategoriaNome(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORIA_NOME)));
                checkins.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return checkins;
    }

    public List<CheckinData> getCheckinsOrderByVisits() {
        List<CheckinData> checkins = new ArrayList<>();
        String query = "SELECT c.*, cat." + DBHelper.COLUMN_CATEGORIA_NOME +
                " FROM " + DBHelper.TABLE_CHECKIN + " c" +
                " JOIN " + DBHelper.TABLE_CATEGORIA + " cat ON c." + DBHelper.COLUMN_CHECKIN_CAT_ID + " = cat." + DBHelper.COLUMN_CATEGORIA_ID +
                " ORDER BY " + DBHelper.COLUMN_CHECKIN_VISITAS + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                CheckinData data = new CheckinData();
                data.setLocal(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHECKIN_LOCAL)));
                data.setQtdVisitas(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHECKIN_VISITAS)));
                data.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHECKIN_LATITUDE)));
                data.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHECKIN_LONGITUDE)));
                data.setCategoriaNome(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORIA_NOME)));
                checkins.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return checkins;
    }
}

