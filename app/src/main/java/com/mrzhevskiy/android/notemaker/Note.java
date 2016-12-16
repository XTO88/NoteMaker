package com.mrzhevskiy.android.notemaker;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Note  implements Comparable<Note>{

    private UUID mId;
    private String mTitle;
    private String mBody;
    private Date dateCreated;
    private Date dateModified;

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    public Note(){
        this(UUID.randomUUID());
    }

    public List<String> getPhotoFileNames(){
        List<String> photoFiles = new ArrayList<>();
for(int i = 0;i<5;i++){
    photoFiles.add("IMG_" + getId().toString()+ "_" + i+ ".jpg");
}
        return photoFiles;
    }

    public Note(UUID id){
      mId = id;
        dateCreated = new Date();
        dateModified = dateCreated;
    }

    @Override
    public int compareTo(Note note) {
        return note.getDateModified().compareTo(getDateModified());
    }
}
