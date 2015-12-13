package com.webreattivo.apptest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "note.db";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        NoteEntity note = new NoteEntity();
        String q= note.createTable();
        db.execSQL(q);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        NoteEntity note = new NoteEntity();
        String q= note.dropTable();
        db.execSQL(q);
        onCreate(db);
    }
}
