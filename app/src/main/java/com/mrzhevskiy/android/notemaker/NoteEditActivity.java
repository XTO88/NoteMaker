package com.mrzhevskiy.android.notemaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class NoteEditActivity extends AppCompatActivity {

    private EditText mTitleText;
    private EditText mBodyText;
    Button mPhotoButton;
    private Note mNote;
    int imageCount;
    private boolean existed = false;
    private RecyclerView mImageRecyclerView;
    private List<File> mPhotoFiles;
    private static final int REQUEST_PHOTO = 0;
    private static final String NOTE_ID = "note_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        mTitleText = (EditText)findViewById(R.id.title_edittext);
        mBodyText = (EditText)findViewById(R.id.body_edittext);

        if(savedInstanceState!=null){
            mNote = NoteLab.get(this).getNote((UUID) savedInstanceState.getSerializable(NOTE_ID));
        }
        else if(getIntent().hasExtra(NoteFragment.NOTE_ID)){
            mNote = NoteLab.get(this).getNote((UUID) getIntent().getSerializableExtra(NoteFragment.NOTE_ID));
            mTitleText.setText(mNote.getTitle());
            mBodyText.setText(mNote.getBody());
            existed = true;
        }
        else {
            mNote = new Note();
        }
        Button mSubmitButton = (Button)findViewById(R.id.button_submit_note);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitNote();
            }
        });

        mPhotoButton = (Button)findViewById(R.id.button_add_picture);
        mPhotoFiles = NoteLab.get(this).getPhotoFiles(mNote);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageCount = NoteLab.get(this).getImageCount(mNote);
        boolean canTakePhoto = mPhotoFiles != null && captureImage.resolveActivity(getPackageManager())!=null && imageCount<5;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.fromFile(mPhotoFiles.get(imageCount));
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });

        mImageRecyclerView = (RecyclerView) findViewById(R.id.note_edit_recycler_view);
        mImageRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        updateAdapter(mNote);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(NOTE_ID,mNote.getId());
    }

    private void submitNote(){
        mNote.setTitle(mTitleText.getText().toString());
        mNote.setBody(mBodyText.getText().toString());
        if(existed){
            mNote.setDateModified(new Date());
            NoteLab.get(this).updateNote(mNote);
        } else {
            NoteLab.get(this).addNote(mNote);
        }
        finish();
    }

    private void updateAdapter(Note note){
        imageCount = NoteLab.get(this).getImageCount(note);
        mImageRecyclerView.setAdapter(new ImageAdapter(note));
        if(NoteLab.get(this).getImageCount(note)>4) mPhotoButton.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK) return;
        if(requestCode == REQUEST_PHOTO){
            updateAdapter(mNote);
        }
    }

    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mImageView;
        private File mImage;
        private Context mContext;


        private ImageHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.gallery_image_view);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindImage(File image){
            mImage = image;
            Bitmap bitmap = PictureUtils.getScaledBitmap(image.getAbsolutePath(),((Activity)mContext));
            mImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onClick(View view) {
            ImageDialog.setImage(mImage.getAbsolutePath(),getApplicationContext());
            new ImageDialog().show(getSupportFragmentManager(),"image_dialog");
        }
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageHolder>{

        private Note mNote;
        private List<File> mImages;
        int imageCount;

        public ImageAdapter(Note note) {
            mNote = note;
            imageCount = NoteLab.get(getApplicationContext()).getImageCount(mNote);
        }

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item,parent,false);
            return new ImageHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, int position) {
            if(imageCount>0) {
                mImages = NoteLab.get(getApplicationContext()).getPhotoFiles(mNote).subList(0, imageCount);
                holder.bindImage(mImages.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return imageCount;
        }
    }
}
