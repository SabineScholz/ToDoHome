package com.example.android.todohome.activities;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.todohome.R;
import com.example.android.todohome.fragments.EditorFragment;
import com.example.android.todohome.fragments.TaskListFragment;
import com.example.android.todohome.model.TaskContract;
import com.example.android.todohome.model.TaskCursorAdapter;

// TODO combine create and edit task method incl. dialog methods
// TODO combine dialogs of this activity with those of the EditorFragment
// TODO finish language settings
// TODO add due date
// TODO put listview in listfragment
// TODO connect with alarm app / notifications
// TODO several list types (default, work, shopping, personal, wishlist, new list)
// TODO share wishlist with others!
// TODO within unfinished tasks view: if task is set to "done", it should slide to the right (use animations of recycler view?)

/**
 * This Activity is going to contain either one or two Fragments, depending on whether
 * the app is running on a smartphone or a tablet.
 * <p>
 * Smartphone:
 * The MainActivity contains only the TaskListFragment that shows the list of tasks.
 * To create a new task or to edit an existing one, the EditorActivity incl. the EditorFragment is started using an Intent.
 * <p>
 * Tablet:
 * The MainActivity contains both the TaskListFragment and the EditorFragment side-by-side.
 * The EditorActivity is not used.
 * <p>
 */
public class MainActivity extends AppCompatActivity implements TaskListFragment.OnTaskListActionListener, EditorFragment.OnEditorActionListener {

    // Tag for log messages
    private static final String LOG_TAG = MainActivity.class.getSimpleName() + " TEST";

    private static final String EDITOR_FRAGMENT_TAG = "editorTaskFragment";

    // Reference to the TaskListFragment
    private TaskListFragment taskListFragment;

    // Reference to the EditorFragment
    private EditorFragment editorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_container);

        // Get a reference on the TaskListFragment (which is present in two-pane and one-pane mode)
        taskListFragment = (TaskListFragment) getFragmentManager().findFragmentById(R.id.list_fragment_container);
    }


    @Override
    protected void onResumeFragments() {
        Log.d(LOG_TAG, "onResumeFragments");
        if (getSupportFragmentManager().findFragmentById(R.id.editor_fragment_container_land) != null) {
            Log.d(LOG_TAG, "removed editorFragment");
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.editor_fragment_container_land)).commit();
        }
        super.onResumeFragments();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onPrepareOptionsMenu");
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.menu_item_all_tasks:
                // delegate filtering to the taskListFragment that handles the TaskCursorAdapter
                taskListFragment.filter(TaskCursorAdapter.SHOW_ALL);
                return true;
            case R.id.menu_item_unfinished_tasks:
                // delegate filtering to the taskListFragment that handles the TaskCursorAdapter
                taskListFragment.filter(TaskCursorAdapter.SHOW_UNFINISHED);
                return true;
            case R.id.menu_item_delete_finished_tasks:
                deleteFinishedTasks();
                return true;
            case R.id.menu_item_delete_all_tasks:
                deleteAllTasks();
                return true;
            case R.id.menu_item_settings:
                startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // extract language setting and apply the language
    }


    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Insert dummy data (only for debugging)
     */
    public void insertDummyData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, "Groceries");
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION, "Doing groceries for the weekend");
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_NO);
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_CREATION_DATE, System.currentTimeMillis());
        getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

        contentValues = new ContentValues();
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, "Empty the trash");
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION, "Empty the trash in each room");
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_YES);
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_CREATION_DATE, System.currentTimeMillis());
        getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

        contentValues = new ContentValues();
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, "Walk the dog");
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION, "Walk the dog for 1 hour");
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_NO);
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_CREATION_DATE, System.currentTimeMillis());
        getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

        contentValues = new ContentValues();
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, "Clean the house");
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION, "Clean the house properly");
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_NO);
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_CREATION_DATE, System.currentTimeMillis());
        getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);
    }


    private void deleteAllTasks() {
        Log.d(LOG_TAG, "deleteAllTasks()");
        int deletedRows = getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, null, null);
        Log.d(LOG_TAG, "deletedRows: " + deletedRows);
    }


    private void deleteFinishedTasks() {
        Log.d(LOG_TAG, "deleteFinishedTasks()");
        String selection = TaskContract.TaskEntry.COLUMN_TASK_DONE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(TaskContract.TaskEntry.DONE_YES)};
        int deletedRows = getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, selection, selectionArgs);
        Log.d(LOG_TAG, "deletedRows: " + deletedRows);
    }


    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onCreateTask() {
        /**
         * Steps:
         * 1. check whether there is an EditorFragment (if not, proceed with creating task)
         * 2. ask the EditorFragment whether there are unsaved changes
         * 3. if yes, show dialog. if no, proceed with creating task.
         * 4. dialog: dismiss the dialog or proceed with replacing the current EditorFragment with an empty one
         * in the callback methods of the dialog
         */
        Log.d(LOG_TAG, "onCreateTask");
        // check whether there is an EditorFragment and whether it contains unsaved changes
        if (hasEditorFragment() && editorFragment.hasChangeDetected()) {
            // Ask user on how to proceed (discard changes vs. cancel)
            showUnsavedChangesDialogBeforeCreate();
        } else {
            // Create a new EditorFragment in which a new task can be created
            openCreateTaskFragment();
        }
    }

    private void openCreateTaskFragment() {
        Log.d(LOG_TAG, "openCreateTaskFragment");
        // Check whether we are in portrait or landscape mode by
        // checking whether there is a fragment container for the EditorFragment in
        // our layout
        if (hasTwoPaneMode()) {
            Log.d(LOG_TAG, "landscape mode");
            // landscape mode

            // create EditorFragment and pass null as the task uri because we are in insert mode
            EditorFragment editorFragment = EditorFragment.newInstance(null);
            Log.d(LOG_TAG, "create EditorFragment");

            // Add the EditorFragment to the UI
            // TODO setTransition?
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.editor_fragment_container_land, editorFragment, EDITOR_FRAGMENT_TAG)
                    .commit();
            Log.d(LOG_TAG, "added EditorFragment to UI");

        } else {
            // portrait mode
            Log.d(LOG_TAG, "portrait mode");

            // Start the EditorActivity
            Log.d(LOG_TAG, "Start the EditorActivity");
            Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
            startActivity(intent);
        }
    }

    private void showUnsavedChangesDialogBeforeCreate() {
        Log.d(LOG_TAG, "showUnsavedChangesDialogBeforeCreate");

        // ask the user if he wants to discard the changes or cancel the action
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_message);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "cancel");
            }
        });
        builder.setPositiveButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "discard changes");
                if (dialog != null) {
                    editorFragment.setChangeDetected(false);
                    openCreateTaskFragment();
                }
            }
        });
        builder.setCancelable(false);
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onEditTask(long id) {
        Log.d(LOG_TAG, "onEditTask");
        // Check whether the EditorFragment contains unsaved changes
        if (hasEditorFragment() && editorFragment.hasChangeDetected()) {
            showUnsavedChangesDialogBeforeEdit(id);
        } else {
            openEditTaskFragment(id);
        }
    }

    private void showUnsavedChangesDialogBeforeEdit(final long id) {
        Log.d(LOG_TAG, "showUnsavedChangesDialogBeforeEdit");
        // ask the user if he wants to discard the changes or cancel the up navigation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_message);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "cancel");
            }
        });
        builder.setPositiveButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int idDialog) {
                Log.d(LOG_TAG, "discard changes");
                if (dialog != null) {
                    editorFragment.setChangeDetected(false);
                    openEditTaskFragment(id);
                }
            }
        });
        builder.setCancelable(false);
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openEditTaskFragment(long id) {
        Log.d(LOG_TAG, "openEditTaskFragment");
        // Create task uri
        Uri uri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);

        // Check whether we are in two-pane mode
        if (hasTwoPaneMode()) {
            Log.d(LOG_TAG, "landscape mode");
            // landscape mode

            // create EditorFragment and add the task uri
            editorFragment = EditorFragment.newInstance(uri);

            // Add the EditorFragment to the UI
            // TODO setTransition?
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.editor_fragment_container_land, editorFragment)
                    .commit();

        } else {
            // portrait mode
            Log.d(LOG_TAG, "portrait mode");

            // Start EditorActivity
            // Add the task uri to the intent.
            Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    @Override
    public void onTaskSaved() {
        // do nothing?
    }

    @Override
    public void onTaskDeleted() {
        if (getSupportFragmentManager().findFragmentById(R.id.editor_fragment_container_land) != null) {
            Log.d(LOG_TAG, "removed editorFragment");
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.editor_fragment_container_land)).commit();
        }
    }

    private boolean hasTwoPaneMode() {
        return findViewById(R.id.editor_fragment_container_land) != null;
    }

    private boolean hasEditorFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.editor_fragment_container_land) != null;
    }
}
