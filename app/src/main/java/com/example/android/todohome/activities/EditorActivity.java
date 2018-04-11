package com.example.android.todohome.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.todohome.R;
import com.example.android.todohome.fragments.EditorFragment;
import com.example.android.todohome.fragments.WarningDialogFragment;

public class EditorActivity extends AppCompatActivity {


    // Tag for log messages
    private static final String LOG_TAG = EditorActivity.class.getSimpleName() + " TEST";

    private static final String FRAGMENT_TAG = "taskFragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.fragment_container);

        Log.d(LOG_TAG, "savedInstanceState == null");

        if (savedInstanceState == null) {
            // Add EditorFragment to this Activity
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, new EditorFragment(), FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        EditorFragment editorFragment = (EditorFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        editorFragment.showExitConfirmationDialog();
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
