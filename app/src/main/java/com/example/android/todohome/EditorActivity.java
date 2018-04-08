package com.example.android.todohome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class EditorActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "taskFragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        EditorFragment editorFragment = new EditorFragment();
        fragmentTransaction.add(R.id.container, editorFragment, FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        EditorFragment editorFragment = (EditorFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (editorFragment.hasChanged()) {
            new WarningDialogFragment().show(getFragmentManager(), "warning");
        } else {
            super.onBackPressed();
        }

        // This does not work: (Dialog disappears automatically...)
//        editorFragment.warnUser();
    }
}
