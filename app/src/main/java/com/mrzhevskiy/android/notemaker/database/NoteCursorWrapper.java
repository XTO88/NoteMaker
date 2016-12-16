package com.mrzhevskiy.android.notemaker.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.mrzhevskiy.android.notemaker.Note;

import java.util.Date;
import java.util.UUID;


public class NoteCursorWrapper extends CursorWrapper {
    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Note getNote(){
        String uuidString = (getString(getColumnIndex(NoteDbSchema.UUID)));
        String title = (getString(getColumnIndex(NoteDbSchema.TITLE)));
        String body = (getString(getColumnIndex(NoteDbSchema.BODY)));
        long dateCreated = getLong(getColumnIndex(NoteDbSchema.DATE_CREATED));
        long dateModified = getLong(getColumnIndex(NoteDbSchema.DATE_MODIFIED));

        Note note = new Note(UUID.fromString(uuidString));
        note.setTitle(title);
        note.setBody(body);
        note.setDateCreated(new Date(dateCreated));
        note.setDateModified(new Date(dateModified));

        return note;
    }
}
