package com.example.android.todohome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {


    // Tag for log messages
    private static final String LOG_TAG = EditorActivity.class.getSimpleName() + " TEST";

    private static final String FRAGMENT_TAG = "taskFragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.fragment_container);

        if (savedInstanceState == null) {
            Log.d(LOG_TAG, "savedInstanceState == null");

            // Add EditorFragment to this Activity
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            EditorFragment editorFragment = new EditorFragment();
            fragmentTransaction.add(R.id.container, editorFragment, FRAGMENT_TAG);
            fragmentTransaction.commit();
        } else {
            Log.d(LOG_TAG, "savedInstanceState NOT null");
        }
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




    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy()");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState()");
    }
}
