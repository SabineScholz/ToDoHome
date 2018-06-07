package com.example.android.todohome.activities;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
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

import java.net.URI;

// TODO while in editor: rotating --> stay in editor, keep data!

/**
 * This Activity contains either one or two Fragments, depending on whether
 * the device is in portrait/one-pane or landscape/two-pane mode.
 *
 * Glossary:
 *      TaskListFragment: contains the list of tasks
 *      EditorFragment:   contains a form to enter the data for a new task or to
 *                        edit the data of an existing task
 * <p>
 * Portrait mode:
 *      The MainActivity contains only the TaskListFragment that shows the list of tasks.
 *      To create a new task or to edit an existing one,
 *      the EditorActivity incl. the EditorFragment is started.
 * <p>
 * Landscape mode:
 *      The MainActivity contains both the TaskListFragment and the EditorFragment side-by-side.
 *      The EditorActivity is not used.
 * <p>
 * ---------------------------------------------------------
 * This Activity implements the following interfaces:
 * <p>
 * TaskListFragment.OnListActionListener:
 * The TaskListFragment communicates with the MainActivity via the methods
 * of this interface (onEditTask(), onCreateTask())
 * <p>
 * EditorFragment.OnEditorActionListener:
 * The EditorFragment communicates with the MainActivity via the methods
 * of this interface (onSaveTask(), onDeleteTask())
 */
public class MainActivity extends AppCompatActivity implements TaskListFragment.OnListActionListener, EditorFragment.OnEditorActionListener {

    // Tag for log messages
    private static final String LOG_TAG = MainActivity.class.getSimpleName() + " TEST";
    private static final String TASK_ID = "1";

    // Reference to the TaskListFragment
    private TaskListFragment taskListFragment;

    // Reference to the EditorFragment
    private EditorFragment editorFragment;

    // Tag for the EditorFragment
    private static final String EDITOR_FRAGMENT_TAG = "editorFragment";

    // id of the currently edited task
    private long taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            Log.d(LOG_TAG, "recreate task id: " + savedInstanceState.getLong(TASK_ID));
            taskId = savedInstanceState.getLong(TASK_ID);
        }

        // Set the view for this Activity
        // There are two main_container layouts:
        //      portrait orientation:   contains only the TaskListFragment (added statically)
        //      landscape orientation:  contains the TaskListFragment (added statically) and
        //                              a container in which the EditorFragment can be dynamically placed
        setContentView(R.layout.main_container);

        // Get a reference on the TaskListFragment (which is present in portrait and landscape mode)
        taskListFragment = (TaskListFragment) getFragmentManager().findFragmentById(R.id.list_fragment_container);

        // Check whether the MainActivity was started by the EditorActivity due to a change from portrait to landscape mode
        Uri uri = getIntent().getData();
        if(uri != null) {
            // MainActivity was started by the EditorActivity
            // get task id to display task details in the EditorFragment
            Log.d(LOG_TAG, "EditorActivity started MainActivity with uri " + uri.toString());
        }
    }

    /**
     * Creates the main menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Determines how to handle clicks on the menu items
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.menu_item_all_tasks:
                // delegate filtering to the TaskListFragment
                taskListFragment.setFilter(TaskListFragment.SHOW_ALL);
                return true;
            case R.id.menu_item_unfinished_tasks:
                // delegate filtering to the TaskListFragment
                taskListFragment.setFilter(TaskListFragment.SHOW_UNFINISHED);
                return true;
            case R.id.menu_item_delete_finished_tasks:
                showDeleteFinishedConfirmationDialog();
                return true;
            case R.id.menu_item_delete_all_tasks:
                showDeleteAllConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Insert dummy data (only for testing)
     */
    private void insertDummyData() {
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

    /**
     * Delete all tasks
     */
    private void deleteAllTasks() {
        Log.d(LOG_TAG, "deleteAllTasks()");
        int deletedRows = getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, null, null);
        Log.d(LOG_TAG, "deletedRows: " + deletedRows);
    }

    /**
     * Delete finished tasks only
     */
    private void deleteFinishedTasks() {
        Log.d(LOG_TAG, "deleteFinishedTasks()");
        String selection = TaskContract.TaskEntry.COLUMN_TASK_DONE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(TaskContract.TaskEntry.DONE_YES)};
        int deletedRows = getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, selection, selectionArgs);
        Log.d(LOG_TAG, "deletedRows: " + deletedRows);
    }

    /**
     * This Dialog lets the user confirm that he indeed wants to delete all tasks.
     */
    private void showDeleteAllConfirmationDialog() {
        // Check whether there are tasks to be deleted. If not, exit.
        if (!taskListFragment.hasTasks()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_delete_all_tasks);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User wants to indeed delete the task.
                deleteAllTasks();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This Dialog lets the user confirm that he indeed wants to delete the finished tasks.
     */
    private void showDeleteFinishedConfirmationDialog() {
        // Check whether there are finished tasks to be deleted. If not, exit.
        if (!taskListFragment.hasFinishedTasks()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_delete_finished_tasks);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User wants to indeed delete the task.
                deleteFinishedTasks();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


      /*
    ======================================================
                        CREATING TASKS
    ======================================================
     */

    /**
     * Called when the floating action button
     * for creating a new task is clicked
     */
    @Override
    public void onCreateTask() {
        Log.d(LOG_TAG, "onCreateTask");
        // check whether there is an EditorFragment and whether it contains unsaved changes
        if (editorFragment != null && editorFragment.hasChangeDetected()) {
            // Ask user on how to proceed (discard changes vs. cancel)
            showUnsavedChangesDialogBeforeCreate();
        } else {
            // Create a new EditorFragment in which a new task can be created
            startCreatingNewTask();
        }
    }

    /**
     * Starts the creation of a new task.
     * If we are in portrait mode, the EditorActivity is started (new screen).
     * If we are landscape mode, the EditorFragment is added to the current UI
     */
    private void startCreatingNewTask() {
        Log.d(LOG_TAG, "startCreatingNewTask");
        // Check whether we are in landscape or portrait mode
        if (hasTwoPaneMode()) {
            Log.d(LOG_TAG, "landscape mode");
            // landscape mode

            // create EditorFragment and pass null as the task uri because we are in insert mode
            EditorFragment editorFragment = EditorFragment.newInstance(null);
            Log.d(LOG_TAG, "create EditorFragment");

            // Add the EditorFragment to the UI (replace existing fragment)
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


    /**
     * The dialog is shown when the user wants to create a new task while
     * there are unsaved changes in the editor
     */
    private void showUnsavedChangesDialogBeforeCreate() {
        Log.d(LOG_TAG, "showUnsavedChangesDialogBeforeCreate");

        // ask the user if he wants to discard the changes or cancel the action
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_message);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Nothing happens, creating new task is cancelled
                Log.d(LOG_TAG, "cancel");
            }
        });
        builder.setPositiveButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "discard changes");
                if (dialog != null) {
                    // user wants to discard changes, so inform the EditorFragment
                    // that there are no more changes to remember
                    editorFragment.setChangeDetected(false);

                    // let the user create a new task
                    startCreatingNewTask();
                }
            }
        });
        builder.setCancelable(false);
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    /*
    ======================================================
                        EDITING TASKS
    ======================================================
     */


    /**
     * Called when the user clicks on a task item in the list
     * to edit it
     */
    @Override
    public void onEditTask(long id) {
        Log.d(LOG_TAG, "onEditTask id " + id);
        taskId = id;
        // check whether there is an EditorFragment and whether it contains unsaved changes
        if (editorFragment != null && editorFragment.hasChangeDetected()) {
            // warn the user that there are unsaved changes
            // add id, so that the dialog callback methods can start the editor with this id
            showUnsavedChangesDialogBeforeEdit(id);
        } else {
            // let the user edit the task
            startEditingTask(id);
        }
    }

    /**
     * The dialog is shown when the user wants to edit a task while
     * there are unsaved changes in the editor
     */
    private void showUnsavedChangesDialogBeforeEdit(final long id) {
        Log.d(LOG_TAG, "showUnsavedChangesDialogBeforeEdit");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_message);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing, keep everything as it is
                Log.d(LOG_TAG, "cancel");
            }
        });
        builder.setPositiveButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int idDialog) {
                Log.d(LOG_TAG, "discard changes");
                if (dialog != null) {
                    // user discards changes, so inform the EditorFragment that
                    // there are no more changes to remember
                    editorFragment.setChangeDetected(false);

                    // let the user edit the task
                    startEditingTask(id);
                }
            }
        });
        builder.setCancelable(false);
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Starts the editor.
     * If we are in portrait mode, the EditorActivity is started (new screen).
     * If we are landscape mode, the EditorFragment is added to the current UI
     */
    private void startEditingTask(long id) {
        Log.d(LOG_TAG, "startEditingTask");
        // Create task uri with the id of the task that was clicked on in the list
        Uri uri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);

        // Check whether we are in landscape mode
        if (hasTwoPaneMode()) {
            Log.d(LOG_TAG, "landscape mode");
            // landscape mode

            // create EditorFragment and add the task uri
            editorFragment = EditorFragment.newInstance(uri);

            // Add the EditorFragment to the UI
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

    /**
     * Handles the user's back press
     */
    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed");
        if (editorFragment != null && editorFragment.hasChangeDetected()) {
            showExitConfirmationDialog();
        } else {
            Log.d(LOG_TAG, "finish");
            finish();
        }
    }

    /**
     * Is shown when the user wants to leave the activity despite unsaved changes.
     */
    private void showExitConfirmationDialog() {
        // ask the user if he wants to discard the changes or cancel the up navigation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_message);
        builder.setPositiveButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "discard changes");
                if (dialog != null) {
                    // user wants to leave, so we can forget about any unsaved changes
                    editorFragment.setChangeDetected(false);
                    finish();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing, stay in the current activity
                Log.d(LOG_TAG, "cancel");
            }
        });
        builder.setCancelable(false);
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Called by the EditorFragment after a task has been saved
     */
    @Override
    public void onTaskSaved() {
        // do nothing
    }

    /**
     * Called by the EditorFragment after a task has been deleted
     */
    @Override
    public void onTaskDeleted() {
        if (getSupportFragmentManager().findFragmentById(R.id.editor_fragment_container_land) != null) {
            Log.d(LOG_TAG, "removed editorFragment");
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.editor_fragment_container_land)).commit();
        }
    }

    /**
     * Checks whether we are in one-pane (portrait) or two-pane (landscape) mode
     */
    private boolean hasTwoPaneMode() {
        return findViewById(R.id.editor_fragment_container_land) != null;
    }

    /**
     * Removes the EditorFragment (if present) when the Activity is restarted
     */
    @Override
    protected void onResumeFragments() {
        Log.d(LOG_TAG, "onResumeFragments");

        // remove the EditorFragment incl. its menu
        if (getSupportFragmentManager().findFragmentById(R.id.editor_fragment_container_land) != null) {
            Log.d(LOG_TAG, "removed editorFragment");
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.editor_fragment_container_land)).commit();

            if(!hasTwoPaneMode()) {
                // start EditorActivity
                Log.d(LOG_TAG, "starting EditorActivity with task id " + taskId);
                startEditingTask(taskId);
            }
        }
        super.onResumeFragments();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");
        // Save the id of the currently edited task
        outState.putLong(TASK_ID, taskId);
        Log.d(LOG_TAG, "save task id to bundle: " + taskId);
        super.onSaveInstanceState(outState);
    }

    // ----------------------- Debugging methods ------------------------------

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
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
