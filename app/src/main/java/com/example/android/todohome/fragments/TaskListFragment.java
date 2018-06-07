package com.example.android.todohome.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.todohome.R;
import com.example.android.todohome.adapter.TaskCursorAdapter;
import com.example.android.todohome.model.TaskContract;

/**
 * This Fragment contains the list of tasks and a button to create new tasks.
 *
 * Interfaces:
 * The interface "TaskCursorAdapter.OnCheckboxClickListener" is implemented to
 * be notified when the user clicks on the "done"-checkbox of a task in the list
 * (via the onCheckboxClick callback method)
 *
 * The interface LoaderManager.LoaderCallbacks<Cursor> is implemented to
 * be notified when the CursorLoader is created (onCreateLoader),
 * has finished loading (onLoadFinished) or needs to be reset (onLoaderReset).
 * The CursorLoader provides "fresh" Cursors when the data in the database have changed.
 * The CursorAdapter will then receive the "fresh" Cursor.
 * The ListView will then be updated the display the fresh data.
 */
public class TaskListFragment extends Fragment implements TaskCursorAdapter.OnCheckboxClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    // ID of the loader that fetches the data for the ListView
    private static final int LOADER_ID = 0;

    // Key to access the current task filter in the bundle (saved in onSaveInstanceState)
    private static final String KEY_TASK_FILTER = "1";

    // Filter options
    public static final int SHOW_UNFINISHED = 0;
    public static final int SHOW_ALL = 1;

    // Current task filter
    private int currentTaskFilter;

    // Tag for log messages
    private static final String LOG_TAG = TaskListFragment.class.getSimpleName() + " TEST";

    // Reference to the CursorAdapter of the ListView
    private TaskCursorAdapter taskCursorAdapter;

    // Reference to the ListView that shows a list of tasks
    private ListView taskListView;

    // Reference to the ProgressBar that is shown while the data is loading
    private ProgressBar progressBar;

    // Reference to the View that is shown when the ListView is empty
    private View emptyView;

    // Reference to the root View of this Fragment's layout
    // Without this reference, we would need to ask the parent activity for the child view
    private View rootView;

    // Reference to the "create task" button
    private FloatingActionButton addNewTaskButton;

    /* (Parent) Activity that implements the OnListActionListener interface.
     This interface must be implemented by all Activities containing
     this Fragment. This allows the Fragment to communicate with its
     parent activity via the interface methods without knowing the Activity itself.
     */
    private OnListActionListener onListActionListener;

    /**
     * The system calls this when it's time for the fragment to
     * draw its user interface for the first time.
     * To draw a UI for your fragment, you must return a
     * View from this method that is the root of your fragment's layout.
     * You can return null if the fragment does not provide a UI.
     * (https://developer.android.com/guide/components/fragments.html)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.list_fragment_layout, container, false);

        // Obtain references of the views in the layout
        findReferences();

        // Create an adapter to display task objects in the ListView.
        // The TaskCursorAdapter needs someone who implements the checkboxClickListener.
        // This Fragment does implement this interface, so hand over "this".
        taskCursorAdapter = new TaskCursorAdapter(getActivity(), null, this);

        // Attach adapter to ListView
        taskListView.setAdapter(taskCursorAdapter);

        // Set empty view on the ListView, so that it only shows when the list has 0 items.
        taskListView.setEmptyView(emptyView);

        // Set click listeners on the items of the list view
        // and on the "create task" button
        setClickListeners();

        // if the Fragment is created for the first time, set the task filter
        // to show all tasks
        if(savedInstanceState == null) {
            currentTaskFilter = SHOW_ALL;
        } else {
            // obtain the restored task filter
            currentTaskFilter = savedInstanceState.getInt(KEY_TASK_FILTER);
            Log.d(LOG_TAG, "restored task filter: " + currentTaskFilter);
        }

        // Initialize loader that fetches data from the database
        getLoaderManager().initLoader(LOADER_ID, null, this);

        return rootView;
    }

    /**
     * Sets click listeners on the items of the list view
     * and on the "create task" button
     */
    private void setClickListeners() {
        // Set a click listener on the ListView that is triggered when
        // someone clicks on an item in the list
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(LOG_TAG, "Click on list item with id " + id);
                Uri uri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
                if (onListActionListener != null) {
                    // Notify the onListActionListener that the user
                    // wants to edit the task with this uri.
                    onListActionListener.onEditTask(uri);
                }
            }
        });

        // Set click listener on "create task" button
        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "add new task");
                if (onListActionListener != null) {
                    // Notify the onListActionListener that the user
                    // wants to create a new task.
                    onListActionListener.onCreateTask();
                }
            }
        });
    }

    /**
     * Finds references to the views in this Fragment's layout
     */
    private void findReferences() {

        // Find a reference to the ListView in the layout
        taskListView = rootView.findViewById(R.id.list_view);

        // Find a reference to the View that is shown when the ListView is empty
        emptyView = rootView.findViewById(R.id.empty_view);

        // Find a reference to the ProgressBar
        progressBar = rootView.findViewById(R.id.progress_bar);

        // Find a reference to the "create task" button
        addNewTaskButton = rootView.findViewById(R.id.add_new_button);

    }

    /**
     * This is called when this Fragment is attached to its
     * parent activity (= context).
     * @param context the parent activity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Make sure the parent activity implements the OnListActionListener
        // whose methods we use to communicate with the activity
        if (context instanceof OnListActionListener) {
            onListActionListener = (OnListActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListActionListener");
        }
    }

    /**
     * Called when this Fragment is detached from its
     * parent activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        onListActionListener = null;
    }

    /**
     * Is called when the "done"-checkbox of a task is clicked in the list view. Updates the task in
     * the database accordingly.
     * This method belongs to the TaskCursorAdapter.OnCheckboxClickListener interface.
     * @param clickedTaskIndex id of the task whose checkbox was clicked
     * @param taskDone boolean that indicates whether the task is done (true) or not (false)
     */
    @Override
    public void onCheckboxClick(long clickedTaskIndex, boolean taskDone) {
        Log.d(LOG_TAG, "onCheckboxClick, task index: " + clickedTaskIndex);

        // Construct the uri of the task whose checkbox was clicked
        Uri uri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, clickedTaskIndex);

        // Put the information of whether or not the task is done in a ContentValues object
        ContentValues contentValues = new ContentValues();
        if (taskDone) {
            contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_YES);
        } else {
            contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_NO);
        }

        // Update the task in the database
        Log.d(LOG_TAG, "updated uri: " + uri);
        int updatedRows = getActivity().getContentResolver().update(uri, contentValues, null, null);

        // Show a toast message depending on whether or not the update was successful
        if (updatedRows == 0) {
            // If the new content URI is null, then there was an error with update.
            Toast.makeText(getActivity(), getString(R.string.editor_update_task_failed), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(getActivity(), getString(R.string.editor_update_task_successful), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Filters the task list
     * @param filterCode code that indicates which filter to apply
     */
    public void setFilter(int filterCode) {
        Log.d(LOG_TAG, "setFilter: " + filterCode);

        // Set the filter code
        currentTaskFilter = filterCode;

        // Restart the loader to apply the filtering
        Log.d(LOG_TAG, "restartLoader");
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


     // -------------------------- Loader callback methods ----------------------------


    /**
     * Called after the initialization of the loader.
     * Returns a CursorLoader that fetches task data from the
     * database.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader");

        // Set the visibility of the ProgressBar to visible, because
        // we still need to load the data
        progressBar.setVisibility(View.VISIBLE);

        // Construct the selection depending on the filter options
        String selection = null;
        String[] selectionArgs = null;
        switch (currentTaskFilter) {
            case (SHOW_ALL):
                // leave selection and selectionArgs at null (get all tasks)
                break;
            case (SHOW_UNFINISHED):
                selection = TaskContract.TaskEntry.COLUMN_TASK_DONE + " = ?";
                selectionArgs = new String[]{String.valueOf(TaskContract.TaskEntry.DONE_NO)};
                break;
        }

        // Check whether the current loader_id matches the one
        // we initialized the loader with
        switch (loader_id) {
            case LOADER_ID:
                // Return cursor loader that queries the task provider for the entire task table (no projection or selection)
                return new CursorLoader(getActivity().getApplicationContext(), TaskContract.TaskEntry.CONTENT_URI, null, selection, selectionArgs, null);
            default:
                return null;
        }
    }


    /**
     * Called when the Loader has finished loading.
     * @param loader the current loader
     * @param cursor cursor containing loaded data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");

        // Hide loading indicator because the data has been loaded
        progressBar.setVisibility(View.GONE);

        // update the adapter with the cursor containing updated task data
        taskCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
        taskCursorAdapter.changeCursor(null);
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");

        // Save the current task filter
        outState.putInt(KEY_TASK_FILTER, currentTaskFilter);
        Log.d(LOG_TAG, "saved task filter: " + currentTaskFilter);
    }

    /**
     * Tests whether there are any tasks in the task list
     */
    public boolean hasTasks() {
        // get the number of tasks
        Cursor cursor = getContext().getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI, null, null, null, null);
        int count = 0;
        if(cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count != 0;
    }

    /**
     * Tests whether there are any finished tasks in the task list
     */
    public boolean hasFinishedTasks() {
        // get the number of finished tasks
        String selection = TaskContract.TaskEntry.COLUMN_TASK_DONE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(TaskContract.TaskEntry.DONE_YES)};
        Cursor cursor = getContext().getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI, null, selection, selectionArgs, null);
        int count = 0;
        if(cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count != 0;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment.
     * Currently, the MainActivity is the (only) parent activity of
     * the TaskListFragment and must therefore implement this interface.
     */
    public interface OnListActionListener {
        // Called when the user clicks on the "create task" button
        void onCreateTask();

        // Called when the user clicks on a task in the list to edit the task
        void onEditTask(Uri uri);
    }


    // ----------------------- Debugging methods ------------------------------
    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
    }
}
