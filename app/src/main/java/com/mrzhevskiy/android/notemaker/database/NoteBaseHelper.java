package com.mrzhevskiy.android.notemaker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NoteBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME ="notes.db";

    public NoteBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+ NoteDbSchema.NAME + "(" + " _id integer primary key autoincrement, "+
                NoteDbSchema.UUID + ", " + NoteDbSchema.TITLE + ", " + NoteDbSchema.BODY + ", " + NoteDbSchema.DATE_CREATED + ", " + NoteDbSchema.DATE_MODIFIED + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
