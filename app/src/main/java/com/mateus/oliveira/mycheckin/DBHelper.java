package com.mateus.oliveira.mycheckin;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "checkin.db";
    private static final int DATABASE_VERSION = 1;

    // Tabela Categoria
    public static final String TABLE_CATEGORIA = "Categoria";
    public static final String COLUMN_CATEGORIA_ID = "idCategoria";
    public static final String COLUMN_CATEGORIA_NOME = "nome";

    // Tabela Checkin
    public static final String TABLE_CHECKIN = "Checkin";
    public static final String COLUMN_CHECKIN_LOCAL = "Local";
    public static final String COLUMN_CHECKIN_VISITAS = "qtdVisitas";
    public static final String COLUMN_CHECKIN_CAT_ID = "cat";
    public static final String COLUMN_CHECKIN_LATITUDE = "latitude";
    public static final String COLUMN_CHECKIN_LONGITUDE = "longitude";

    // Comando de criação da tabela Categoria
    private static final String CREATE_TABLE_CATEGORIA = "CREATE TABLE " + TABLE_CATEGORIA + " (" +
            COLUMN_CATEGORIA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CATEGORIA_NOME + " TEXT NOT NULL);";

    // Comando de criação da tabela Checkin
    private static final String CREATE_TABLE_CHECKIN = "CREATE TABLE " + TABLE_CHECKIN + " (" +
            COLUMN_CHECKIN_LOCAL + " TEXT PRIMARY KEY, " +
            COLUMN_CHECKIN_VISITAS + " INTEGER NOT NULL, " +
            COLUMN_CHECKIN_CAT_ID + " INTEGER NOT NULL, " +
            COLUMN_CHECKIN_LATITUDE + " TEXT NOT NULL, " +
            COLUMN_CHECKIN_LONGITUDE + " TEXT NOT NULL, " +
            "CONSTRAINT fkey0 FOREIGN KEY (" + COLUMN_CHECKIN_CAT_ID + ") REFERENCES " + TABLE_CATEGORIA + "(" + COLUMN_CATEGORIA_ID + "));";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Executa os comandos de criação das tabelas
        db.execSQL(CREATE_TABLE_CATEGORIA);
        db.execSQL(CREATE_TABLE_CHECKIN);

        // Insere as categorias iniciais
        insertInitialCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Se houver uma atualização do banco, apaga as tabelas antigas e cria de novo
        // (Em um app de produção, seria necessário migrar os dados do usuário)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIA);
        onCreate(db);
    }

    private void insertInitialCategories(SQLiteDatabase db) {
        String[] categorias = {"Restaurante", "Bar", "Cinema", "Universidade", "Estádio", "Parque", "Outros"};
        for (String nomeCategoria : categorias) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORIA_NOME, nomeCategoria);
            db.insert(TABLE_CATEGORIA, null, values);
        }
    }
}

