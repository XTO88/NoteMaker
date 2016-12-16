package com.mrzhevskiy.android.notemaker;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class NoteListActivity extends SingleFragmentActivity implements NoteListFragment.Callbacks,NoteFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new NoteListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onNoteSelected(Note note) {
        if(findViewById(R.id.detail_fragment_container) == null){
            startActivity(NotePagerActivity.newIntent(this,note.getId()));

        }
        else{
            Fragment newFragment = NoteFragment.newInstance(note.getId());
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container,newFragment).commit();
        }
    }

    @Override
    public void onNoteUpdated(Note note) {
        NoteListFragment listFragment = (NoteListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
