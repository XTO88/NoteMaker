package com.mrzhevskiy.android.notemaker;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;
import java.util.UUID;

public class NotePagerActivity extends AppCompatActivity implements NoteFragment.Callbacks{

    private ViewPager mViewPager;
    private List<Note> mNotes;
    private static final String EXTRA_NOTE_ID = "note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pager);

        mNotes = NoteLab.get(this).getNotes();
        UUID id = (UUID)getIntent().getSerializableExtra(EXTRA_NOTE_ID);
        mViewPager = (ViewPager)findViewById(R.id.note_pager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Note note = mNotes.get(position);
                return NoteFragment.newInstance(note.getId());
            }

            @Override
            public int getCount() {
                return mNotes.size();
            }
        });

        for(int i=0;i<mNotes.size();i++){
            if(mNotes.get(i).getId().equals(id)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent newIntent(Context context, UUID id){
        Intent intent = new Intent(context,NotePagerActivity.class);
        intent.putExtra(EXTRA_NOTE_ID,id);
        return intent;
    }

    @Override
    public void onNoteUpdated(Note note) {
        finish();
    }
}
