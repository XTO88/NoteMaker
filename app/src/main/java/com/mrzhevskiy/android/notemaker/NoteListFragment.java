package com.mrzhevskiy.android.notemaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NoteListFragment extends Fragment {

    private List<Note> mNotes;

    public interface Callbacks{
        void onNoteSelected(Note note);
    }

    private RecyclerView mRecyclerView;
    private Callbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list,container,false);


        Log.i("notes",String.valueOf(NoteLab.get(getActivity()).getNotes().size()));

        mRecyclerView = (RecyclerView) view.findViewById(R.id.note_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI(){
        mNotes = NoteLab.get(getActivity()).getNotes();
        Collections.sort(mNotes);
        mRecyclerView.setAdapter(new NoteAdapter(mNotes));
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
        inflater.inflate(R.menu.list_activity_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_create_note:
                startActivity(new Intent(getActivity(),NoteEditActivity.class));
                return true;
            case R.id.menu_item_delete_all:
                List<Note> mNotes = NoteLab.get(getActivity()).getNotes();
                for(Note note:mNotes){
                    NoteLab.get(getActivity()).deleteNote(note);
                }
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Note mNote;
        private TextView mTextView;

        public NoteHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.title_note);
            itemView.setOnClickListener(this);
        }

        public void bindNote(Note note){
            mNote = note;
            String date = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH).format(note.getDateModified());
            mTextView.setText(note.getTitle()+"\n" + "Last modified: " + date);
        }

        @Override
        public void onClick(View view) {
            mCallbacks.onNoteSelected(mNote);
        }
    }

    public class NoteAdapter extends RecyclerView.Adapter<NoteHolder>{

        private List<Note> mNotes;

        public NoteAdapter(List<Note> notes) {
            mNotes = notes;
        }

        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.note_list_item,parent,false);
            return new NoteHolder(view);
        }

        @Override
        public void onBindViewHolder(NoteHolder holder, int position) {
            Note note = mNotes.get(position);
            holder.bindNote(note);
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }
    }
}
