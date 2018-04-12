package com.example.android.todohome.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.todohome.R;
import com.example.android.todohome.fragments.EditorFragment;

public class EditorActivity extends AppCompatActivity {


    // Tag for log messages
    private static final String LOG_TAG = EditorActivity.class.getSimpleName() + " TEST";

    private static final String FRAGMENT_TAG = "taskFragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.editor_activity_layout);

        // Extract the task uri from the intent
        Uri uri = getIntent().getData();

        Log.d(LOG_TAG, "savedInstanceState == null");

        if (savedInstanceState == null) {

            // Create EditorFragment and add task uri
            EditorFragment editorFragment = EditorFragment.newInstance(uri);

            // Add EditorFragment to this Activity
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.editor_fragment_container_port, editorFragment, FRAGMENT_TAG);
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
