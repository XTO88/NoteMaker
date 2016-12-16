package com.mrzhevskiy.android.notemaker;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.mrzhevskiy.android.notemaker.database.NoteBaseHelper;
import com.mrzhevskiy.android.notemaker.database.NoteCursorWrapper;
import com.mrzhevskiy.android.notemaker.database.NoteDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteLab {
    private static NoteLab sNoteLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static NoteLab get(Context context){
        if(sNoteLab == null) sNoteLab = new NoteLab(context);
        return sNoteLab;
    }

    private NoteLab(Context context) {
        mContext = context;
        mDatabase = new NoteBaseHelper(mContext).getWritableDatabase();
    }

    public void addNote(Note note){
        ContentValues values = getContentValues(note);
        mDatabase.insert(NoteDbSchema.NAME,null,values);
    }

    public void deleteNote(Note note){
        mDatabase.delete(NoteDbSchema.NAME,NoteDbSchema.UUID + " = ?", new String[]{note.getId().toString()});
    }

    public List<Note> getNotes(){
        List<Note> notes = new ArrayList<>();
        NoteCursorWrapper cursor = queryNotes(null,null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return notes;
    }

    public Note getNote(UUID id){

        NoteCursorWrapper cursor = queryNotes(NoteDbSchema.UUID+ " = ?",new String[]{id.toString()});
        try{
            if(cursor.getCount() == 0) return null;
            cursor.moveToFirst();
            return cursor.getNote();
        }
        finally {
            cursor.close();
        }
    }

    public void updateNote(Note note){
        String uuidString = note.getId().toString();
        ContentValues values = getContentValues(note);
        mDatabase.update(NoteDbSchema.NAME,values,NoteDbSchema.UUID + " = ?",new String[]{uuidString});
    }

    private static ContentValues getContentValues(Note note){
        ContentValues values = new ContentValues();
        values.put(NoteDbSchema.UUID,note.getId().toString());
        values.put(NoteDbSchema.TITLE,note.getTitle());
        values.put(NoteDbSchema.BODY,note.getBody());
        values.put(NoteDbSchema.DATE_CREATED,note.getDateCreated().getTime());
        values.put(NoteDbSchema.DATE_MODIFIED,note.getDateModified().getTime());

        return values;
    }

    private NoteCursorWrapper queryNotes(String whereClause,String[] whereArgs){
      Cursor cursor = mDatabase.query(NoteDbSchema.NAME,null,whereClause,whereArgs,null,null,null);
        return  new NoteCursorWrapper(cursor);
    }

    public List<File> getPhotoFiles(Note note){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFilesDir == null) return null;
        else{
            List<File> photoFiles = new ArrayList<>();
            for(int i = 0;i<note.getPhotoFileNames().size();i++){
                photoFiles.add(new File(externalFilesDir,note.getPhotoFileNames().get(i)));
            }
            return photoFiles;
        }
    }

    public int getImageCount(Note note){
        List<File> photoFiles = getPhotoFiles(note);
        int counter = 0;
        for(int i = 0;i<photoFiles.size();i++){
            if(photoFiles.get(i).exists()) counter++;
        }
        return counter;
    }
}
