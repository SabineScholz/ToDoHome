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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.todohome.R;
import com.example.android.todohome.model.TaskContract;
import com.example.android.todohome.model.TaskCursorAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTaskListActionListener} interface
 * to handle interaction events.
 */
public class TaskListFragment extends Fragment implements TaskCursorAdapter.CheckboxClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    // ID of the loader that fetches the data for the listview
    public static final int LOADER_ID = 0;

    private int currentCheckPosition = 0;

    // Tag for log messages
    private static final String LOG_TAG = TaskListFragment.class.getSimpleName() + " TEST";
    private TaskCursorAdapter taskCursorAdapter;
    private ListView taskListView;
    private ProgressBar progressBar;
    private View rootView;

    private OnTaskListActionListener mListener;

//    public TaskListFragment() {
//        // Required empty public constructor
//    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     */
//    public static TaskListFragment newInstance() {
//        TaskListFragment fragment = new TaskListFragment();
//        return fragment;
//    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);





    }


    /**
     * The system calls this when it's time for the fragment to
     * draw its user interface for the first time.
     * To draw a UI for your fragment, you must return a
     * View from this method that is the root of your fragment's layout.
     * You can return null if the fragment does not provide a UI.
     * (https://developer.android.com/guide/components/fragments.html)
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.list_fragment_layout, container, false);

        // Obtain references of the views in the layout
        // Find a reference to the ListView in the layout
        taskListView = rootView.findViewById(R.id.list_view);

        // Create an adapter to display task objects in the ListView
        taskCursorAdapter = new TaskCursorAdapter(getActivity(), null, this);

        // Attach adapter to ListView
        taskListView.setAdapter(taskCursorAdapter);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = rootView.findViewById(R.id.empty_view);
        taskListView.setEmptyView(emptyView);

        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // Set a click listener on the listview that is triggered when
        // someone clicks on an item in the list
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(LOG_TAG, "Click on list item with id " + id);
                if (mListener != null) {
                    mListener.onEditTask(id);
                }
            }
        });

        // Set listener on "add new task" button
        FloatingActionButton addNewTaskButton = rootView.findViewById(R.id.add_new_button);
        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "add new task");
                // Let the parent activity take care of opening the editor
                if (mListener != null) {
                    mListener.onCreateTask();
                }
            }
        });



        taskCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                Log.d(LOG_TAG, "Filter");
                if (constraint == TaskCursorAdapter.SHOW_UNFINISHED) {
                    String selection = TaskContract.TaskEntry.COLUMN_TASK_DONE + " = ?";
                    String[] selectionArgs = new String[]{String.valueOf(TaskContract.TaskEntry.DONE_NO)};
                    return getActivity().getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI, null, selection, selectionArgs, null);
                } else {
                    return getActivity().getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI, null, null, null, null);
                }
            }
        });

        // Initialize loader that fetches data from the database
        getLoaderManager().initLoader(LOADER_ID, null, this);


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskListActionListener) {
            mListener = (OnTaskListActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        if (taskDone) {
            contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_YES);
        } else {
            contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_NO);
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", currentCheckPosition);
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
    }

    /**
     * The system calls this method as the first indication
     * that the user is leaving the fragment (though it doesn't always mean
     * the fragment is being destroyed). This is usually where you
     * should commit any changes that should be persisted beyond the
     * current user session (because the user might not come back).
     * (https://developer.android.com/guide/components/fragments.html)
     */
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

    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader");
        progressBar.setVisibility(View.VISIBLE);

        switch (loader_id) {
            case LOADER_ID:
                // Return cursor loader that queries the task provider for the entire task table (no projection or selection)
                return new CursorLoader(getActivity().getApplicationContext(), TaskContract.TaskEntry.CONTENT_URI, null, null, null, null);
            default:
                return null;
        }
    }

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

    public void filter(CharSequence constraint) {
        taskCursorAdapter.getFilter().filter(constraint);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTaskListActionListener {
        void onCreateTask();
        void onEditTask(long id);
    }
}
