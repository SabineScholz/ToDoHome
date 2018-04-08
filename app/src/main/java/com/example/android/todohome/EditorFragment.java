package com.example.android.todohome;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todohome.model.TaskContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EditorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorFragment.class.getSimpleName() + " TEST";
    private static final int EXISTING_TASK_LOADER = 1;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private CheckBox doneCheckBox;
    private TextView creationDateTextView;
    private long currentTime;

    private View rootView;
    private boolean change_detected;

    // Content URI for the existing task (null if the task is null and we are in insert mode)
    private Uri currentTaskUri;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            change_detected = true;
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_task, container, false);

        setUpSubmitButton();

        // Obtain references of the views in the layout
        nameEditText = rootView.findViewById(R.id.edit_text_task_title);
        descriptionEditText = rootView.findViewById(R.id.edit_text_task_description);
        doneCheckBox = rootView.findViewById(R.id.done_checkbox);
        creationDateTextView = rootView.findViewById(R.id.creation_date);

        // Get the intent that started this fragment
        Intent intent = getActivity().getIntent();

        // Check whether we are in insert or edit mode
        currentTaskUri = intent.getData();


        if (currentTaskUri == null) {
            // we are in insert mode
            Log.d(LOG_TAG, "insert mode");

            // set the nameEditText of the activity to reflect that we are in insert mode
            getActivity().setTitle(R.id.editor_activity_title_new_task);

            // display the current date in the creation date task view
            currentTime = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentTime);
            DateFormat formatter = new SimpleDateFormat();
            creationDateTextView.setText(formatter.format(calendar.getTime()));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a task that hasn't been created yet.)
            //  invalidateOptionsMenu();

        } else {
            // we are in edit mode
            Log.d(LOG_TAG, "edit mode");

            // set nameEditText accordingly
            getActivity().setTitle(R.string.editor_activity_title_edit_task);

            // Initialize loader that fetches data for the current task from the database
            getLoaderManager().initLoader(EXISTING_TASK_LOADER, null, this);
        }

        // add change listeners to the views so that we can warn the user if he leaves the activity
        // while there are unsaved changes
        addChangeListeners();

        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Sets up the button with which the user submits the task data
     */
    private void setUpSubmitButton() {
        FloatingActionButton fab = rootView.findViewById(R.id.save_task_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save task to database
                saveTask();
                // Exit activity
                getActivity().finish();
            }
        });
    }

    private void saveTask() {

        // Check whether a name is provided. If not, return.
        if (TextUtils.isEmpty(nameEditText.getText())) return;

        // Create a content values object form the data in the views
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, nameEditText.getText().toString().trim());
        values.put(TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION, descriptionEditText.getText().toString().trim());
        if (doneCheckBox.isChecked()) {
            values.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_YES);
        } else {
            values.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_NO);
        }


        // Check whether we are in edit or insert mode
        if (currentTaskUri == null) {

            // insert mode

            Log.d(LOG_TAG, "saveTask, currentTime: " + currentTime);
            values.put(TaskContract.TaskEntry.COLUMN_TASK_CREATION_DATE, currentTime);

            // Insert a new task into the provider, returning the content uri for the new task.
            Uri newTaskUri = getContext().getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newTaskUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(getContext(), getContext().getString(R.string.editor_insert_task_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(getContext(), getContext().getString(R.string.editor_insert_task_successful), Toast.LENGTH_SHORT).show();
            }
        } else {

            // edit mode

            // Update the existing task
            int updatedRows = getContext().getContentResolver().update(currentTaskUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful
            if (updatedRows == 0) {
                // If the new content URI is null, then there was an error with update.
                Toast.makeText(getContext(), getContext().getString(R.string.editor_update_task_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(getContext(), getContext().getString(R.string.editor_update_task_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Is triggered when the up button is clicked on. If changes have been made
     * to the task data, the user is asked whether the changes should be saved or discarded.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                warnUser();
                return true;
        }
        return false;
    }


    public void warnUser() {
        if (change_detected) {
            new WarningDialogFragment().show(getActivity().getFragmentManager(), "warning");
        } else {
            NavUtils.navigateUpFromSameTask(getActivity());
        }
    }

    /**
     * Adds change listeners to the text fields and checkbox to keep track of whether
     * any changes were made to the task.
     */
    private void addChangeListeners() {
        nameEditText.addTextChangedListener(textWatcher);
        descriptionEditText.addTextChangedListener(textWatcher);
        doneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                change_detected = true;
            }
        });
    }

    public boolean hasChanged() {
        return change_detected;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, Bundle args) {
        switch (loader_id) {
            case EXISTING_TASK_LOADER:
                return new CursorLoader(
                        getContext(),
                        currentTaskUri,
                        null,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.getCount() == 0) return;

        Log.d(LOG_TAG, "onLoadFinished");

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        cursor.moveToFirst();

        // Find the column indexes within the cursor
        int nameColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME);
        int doneColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DONE);
        int descriptionColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION);
        int creationDateColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_CREATION_DATE);

        // Extract task data from the cursor
        String name = cursor.getString(nameColumnIndex);
        int done = cursor.getInt(doneColumnIndex);
        String description = cursor.getString(descriptionColumnIndex);
        long creationDate = cursor.getLong(creationDateColumnIndex);

        // Populate views with extracted data
        nameEditText.setText(name);
        if (done == TaskContract.TaskEntry.DONE_YES) {
            doneCheckBox.setChecked(true);
        } else {
            doneCheckBox.setChecked(false);
        }
        descriptionEditText.setText(description);

        Calendar calendar = Calendar.getInstance();
        Log.d(LOG_TAG, "onLoadFinished, creationDate " + creationDate);
        calendar.setTimeInMillis(creationDate);
        DateFormat formatter = new SimpleDateFormat();
        creationDateTextView.setText(formatter.format(calendar.getTime()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText(null);
        descriptionEditText.setText(null);
        creationDateTextView.setText(null);
        doneCheckBox.setChecked(false);
    }
}
