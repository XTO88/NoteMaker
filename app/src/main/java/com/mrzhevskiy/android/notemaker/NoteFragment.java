package com.mrzhevskiy.android.notemaker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.UUID;


public class NoteFragment extends Fragment {



    private Note mNote;
    private Callbacks mCallbacks;
    private TextView mTitleView;
    private TextView mBodyView;
    private TextView mDateView;
    private RecyclerView mImageRecyclerView;
    public static final String NOTE_ID = "note_id";
    private Context mContext;
    private static final String TAG = "NoteFragment";

    public interface Callbacks{
        void onNoteUpdated(Note note);
    }

    public static NoteFragment newInstance(UUID id){
      Bundle args = new Bundle();
        args.putSerializable(NOTE_ID,id);

        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID noteID = (UUID) getArguments().getSerializable(NOTE_ID);
        mNote = NoteLab.get(getActivity()).getNote(noteID);
        setHasOptionsMenu(true);

        mContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detailed_note,container,false);

        mTitleView = (TextView)view.findViewById(R.id.title_textview);
        mBodyView = (TextView)view.findViewById(R.id.body_textView);
        mDateView = (TextView)view.findViewById(R.id.date_textview);
        mImageRecyclerView = (RecyclerView) view.findViewById(R.id.image_recyclerview);
        mImageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        updateUI();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    public void updateUI(){
        if((mNote!=null) &&(NoteLab.get(getActivity()).getNote(mNote.getId())!=null)) {
            mNote = NoteLab.get(getActivity()).getNote(mNote.getId());
            mTitleView.setText(mNote.getTitle());
            mBodyView.setText(mNote.getBody());
            mDateView.setText("Created: " + mNote.getDateCreated() + "\n" + "Modified: " + mNote.getDateModified());
            mImageRecyclerView.setAdapter(new ImageAdapter(getActivity(),mNote));
        } else {
            mTitleView.setText("");
            mBodyView.setText("");
            mDateView.setText("");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_note,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_edit_note:
                Intent intent = new Intent(getActivity(),NoteEditActivity.class);
                intent.putExtra(NOTE_ID,mNote.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_delete_note:
                NoteLab.get(getActivity()).deleteNote(mNote);
                mCallbacks.onNoteUpdated(mNote);
                setHasOptionsMenu(false);
                updateUI();
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mImageView;
        private File mImage;

        private ImageHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.gallery_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindImage(File image){
            mImage = image;
            Bitmap bitmap = PictureUtils.getScaledBitmap(image.getAbsolutePath(),getActivity());
            mImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onClick(View view) {
            ImageDialog.setImage(mImage.getAbsolutePath(),getActivity());
            new ImageDialog().show(getFragmentManager(),"dialog");
        }
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageHolder>{

        private Note mNote;
        private Context mContext;
        private List<File> mImages;
        int imageCount;

        public ImageAdapter(Context context,Note note) {
            mContext = context;
            mNote = note;
            imageCount = NoteLab.get(getActivity()).getImageCount(mNote);
        }

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item,parent,false);
            return new ImageHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, int position) {
            if(imageCount>0) {
                mImages = NoteLab.get(getActivity()).getPhotoFiles(mNote).subList(0, imageCount);
                holder.bindImage(mImages.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return imageCount;
        }
    }
}
