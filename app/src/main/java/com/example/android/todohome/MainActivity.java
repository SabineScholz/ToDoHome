package com.example.android.todohome;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.todohome.model.TaskContract;
import com.example.android.todohome.model.TaskCursorAdapter;


// TODO back and up button dialogs
// TODO creation date show date only
// TODO delete single tasks
// TODO delete all tasks
// TODO delete unfinished tasks
// TODO filtering, show unfinished tasks only
// TODO save button editorActivity
// TODO dont show "task updated" when no changes have taken place
// TODO what should happen if the user applies the show-unfinished filter and marks a task as done afterwards? let task disappear immediately?
// TODO add due date

public class MainActivity extends AppCompatActivity implements TaskCursorAdapter.CheckboxClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TASK_MESSAGE = "com.example.android.todohome.TASK";
    public static final String TASKS_KEY = "com.example.android.todohome.TASKLIST";
    public static final int LOADER_ID = 0;

    private static final int REQUEST_CODE_EDIT_TASK = 0;
    private static final int REQUEST_CODE_CREATE_TASK = 1;

    // Tag for log messages
    private static final String LOG_TAG = MainActivity.class.getSimpleName() + " TEST";

    /* sources for filter option
    https://stackoverflow.com/questions/24769257/custom-listview-adapter-with-filter-android/24771174#24771174
    https://www.survivingwithandroid.com/2012/10/android-listview-custom-filter.html
    https://gist.github.com/DeepakRattan/26521c404ffd7071d0a4
     */

    private TaskCursorAdapter taskCursorAdapter;
    private ListView taskListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the ListView in the layout
        taskListView = findViewById(R.id.list_view);

        // Create an adapter to display task objects in the ListView
        taskCursorAdapter = new TaskCursorAdapter(this, null, this);

        // Attach adapter to ListView
        taskListView.setAdapter(taskCursorAdapter);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        taskListView.setEmptyView(emptyView);

        // Initialize loader that fetches data from the database
        getLoaderManager().initLoader(LOADER_ID, null, this);


        // Set a click listener on the listview that is triggered when
        // someone clicks on an item in the list
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(LOG_TAG, "Click on list item with id " + id);

                // Start the activity that show the details of the
                // task that was clicked on. Add the task uri to the intent.
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        // Set listener on "add new task" button
        FloatingActionButton addNewTaskButton = findViewById(R.id.add_new_button);
        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "add new task");

                // Start the EditorActivity
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_insert_dummy_data:
                insertDummyData();
            case R.id.menu_item_all_tasks:
//                taskCursorAdapter.getFilter().filter(TaskCursorAdapter.SHOW_ALL);
                return true;
            case R.id.menu_item_unfinished_tasks:
//                taskCursorAdapter.getFilter().filter(TaskCursorAdapter.SHOW_UNFINISHED);
                return true;
            case R.id.menu_item_delete_finished_tasks:
                deleteFinishedTasks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void deleteFinishedTasks() {
        Log.d(LOG_TAG, "deleteFinishedTasks()");
        String selection = TaskContract.TaskEntry.COLUMN_TASK_DONE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(TaskContract.TaskEntry.DONE_YES)};
        int deletedRows = getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, selection, selectionArgs);
        Log.d(LOG_TAG, "deletedRows: " + deletedRows);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
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

    /**
     * Is called when the "done"-checkbox is clicked in the list view. Updates the task in
     * the database accordingly.
     */
    @Override
    public void onCheckboxClick(long clickedTaskIndex, boolean taskDone) {
        Log.d(LOG_TAG, "onCheckboxClick, task index: " + clickedTaskIndex);
        Uri uri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, clickedTaskIndex);
        ContentValues contentValues = new ContentValues();
        if(taskDone) {
            contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_YES);
        } else {
            contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_NO);
        }
        Log.d(LOG_TAG, "updated uri: " + uri);
        int updatedRows = getContentResolver().update(uri, contentValues, null, null);

        // Show a toast message depending on whether or not the update was successful
        if (updatedRows == 0) {
            // If the new content URI is null, then there was an error with update.
            Toast.makeText(this,getString(R.string.editor_update_task_failed), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_update_task_successful), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader");

        switch (loader_id) {
            case LOADER_ID:
                // Return cursor loader that queries the task provider for the entire task table (no projection or selection)
                return new CursorLoader(getApplicationContext(), TaskContract.TaskEntry.CONTENT_URI, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");
        // update the adapter with the cursor containing updated task data
        taskCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
        taskCursorAdapter.changeCursor(null);
    }
}
