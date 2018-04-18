package com.example.android.todohome.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.todohome.R;
import com.example.android.todohome.fragments.EditorFragment;


/**
 * The EditorActivity is used to show the EditorFragment
 * when the device is in portrait orientation.
 * In landscape orientation, this Activity is not used, because the EditorFragment is
 * displayed in the MainActivity.
 * <p>
 * This Activity is used to 1. create tasks (insert mode) and 2. edit existing tasks (edit mode).
 * The mode is determined by checking whether the intent that started this
 * Activity contains a uri, in which case we are in edit mode.
 * <p>
 * ---------------------------------------------------------
 * This Activity implements the following interfaces:
 * <p>
 * EditorFragment.OnEditorActionListener:
 * The EditorFragment communicates with this Activity through the methods
 * of this interface (onSaveTask(), onDeleteTask())
 */
public class EditorActivity extends AppCompatActivity implements EditorFragment.OnEditorActionListener {

    // Tag for log messages
    private static final String LOG_TAG = EditorActivity.class.getSimpleName() + " TEST";

    // Tag for the EditorFragment
    private static final String FRAGMENT_TAG = "editorFragment";

    /**
     * Called when the activity is first created.
     * This is where you should do all of your normal static set up:
     * create views, bind data to lists, etc. This method also provides
     * you with a Bundle containing the activity's previously frozen state,
     * if there was one. Always followed by onStart().
     * (https://developer.android.com/reference/android/app/Activity.html)
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");

        // Set the layout for this Activity
        // Here, this layout is only a container for the EditorFragment
        setContentView(R.layout.editor_activity_layout);

        // Extract the task uri from the intent (null if we are in insert mode)
        Uri uri = getIntent().getData();

        // Set activity title to reflect that we're in insert or edit mode
        if (uri == null) {
            setTitle(R.string.editor_activity_title_new_task);
        } else {
            setTitle(R.string.editor_activity_title_edit_task);
        }

        // only add the EditorFragment the first time this Activity is created (
        // forgot the reason...
        if (savedInstanceState == null) {

            // Create EditorFragment and add task uri (which is null in insert mode)
            EditorFragment editorFragment = EditorFragment.newInstance(uri);

            // Add EditorFragment to this Activity
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.editor_fragment_container_port, editorFragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    /**
     * Handles the user's back press (= up navigation in our case).
     * Delegates handling the back press to the EditorFragment, which knows
     * whether or not the user needs to be warned about unsaved changes.
     */
    @Override
    public void onBackPressed() {
        EditorFragment editorFragment = (EditorFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (editorFragment.hasChangeDetected()) {
            editorFragment.showExitConfirmationDialog();
        } else {
            // navigate up
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    /**
     * Called by the EditorFragment after a task has been saved.
     * Finishes this Activity.
     */
    @Override
    public void onTaskSaved() {
        finish();
    }

    /**
     * Called by the EditorFragment after a task has been deleted.
     * Finishes this Activity.
     */
    @Override
    public void onTaskDeleted() {
        finish();
    }


    // ----------------------- Debugging methods ------------------------------

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState()");
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
}
