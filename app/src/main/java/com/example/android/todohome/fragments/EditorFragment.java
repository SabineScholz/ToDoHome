package com.example.android.todohome.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todohome.R;
import com.example.android.todohome.model.TaskContract;

import java.text.DateFormat;
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
            Log.d(LOG_TAG, "onTextChanged");
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

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

        // display the current date in the creation date task view
        currentTime = System.currentTimeMillis();
        creationDateTextView.setText(formatDate(currentTime));

        if (savedInstanceState == null) {
            if (currentTaskUri == null) {
                // we are in insert mode
                Log.d(LOG_TAG, "insert mode");

                // set the nameEditText of the activity to reflect that we are in insert mode
                getActivity().setTitle(R.string.editor_activity_title_new_task);
            } else {
                // we are in edit mode
                Log.d(LOG_TAG, "edit mode");

                // set nameEditText accordingly
                getActivity().setTitle(R.string.editor_activity_title_edit_task);

                // Initialize loader that fetches data for the current task from the database
                getLoaderManager().initLoader(EXISTING_TASK_LOADER, null, this);
            }
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

            // Update the existing task only if changes have taken place
            if (change_detected) {
                Log.d(LOG_TAG, "updated uri: " + currentTaskUri);
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
    }


    private void deleteTask() {
        // Only perform the delete if this is an existing task.
        if (currentTaskUri != null) {
            Log.d(LOG_TAG, "deleting task " + currentTaskUri);
            int deletedRows = getContext().getContentResolver().delete(currentTaskUri, null, null);

            // Show a toast message depending on whether or not the deletion was successful
            if (deletedRows == 0) {
                // If the new content URI is null, then there was an error with deletion.
                Toast.makeText(getContext(), getContext().getString(R.string.editor_delete_task_failed), Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "failed to delete");
            } else {
                // Otherwise, the deletion was successful and we can display a toast.
                Toast.makeText(getContext(), getContext().getString(R.string.editor_delete_task_successful), Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "task deleted");
            }
        }
        getActivity().finish();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_editor, menu);
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new task, hide the "Delete" menu item.
        if (currentTaskUri == null) {
            MenuItem menuItem = menu.findItem(R.id.menu_item_delete_task);
            menuItem.setVisible(false);
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
                showExitConfirmationDialog();
                return true;
            case R.id.menu_item_delete_task:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
        }
        return false;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.alert_delete_single_task);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Delete" button, so delete the task.
                deleteTask();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the task.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showExitConfirmationDialog() {
        if (change_detected) {
            // ask the user if he wants to discard the changes or cancel the up navigation
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.warning_message);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.d(LOG_TAG, "cancel");
                }
            });
            builder.setPositiveButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.d(LOG_TAG, "discard changes");
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
            });
            builder.setCancelable(false);
            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            // navigate up
            NavUtils.navigateUpFromSameTask(getActivity());
        }
    }

    /**
     * Adds change listeners to the text fields and checkbox to keep track of whether
     * any changes were made to the task.
     */
    private void addChangeListeners() {
        Log.d(LOG_TAG, "addChangeListeners");
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
        creationDateTextView.setText(formatDate(creationDate));

        change_detected = false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText(null);
        descriptionEditText.setText(null);
        creationDateTextView.setText(null);
        doneCheckBox.setChecked(false);
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
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy()");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState()");
    }


    private String formatDate(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        DateFormat formatter = DateFormat.getDateInstance();
        return formatter.format(calendar.getTime());
    }

}
