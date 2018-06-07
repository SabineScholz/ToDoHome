package com.example.android.todohome.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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


/**
 * The EditorFragment is used to
 *      1. create tasks (insert mode)
 *      2. edit existing tasks (edit mode).
 * The mode is determined by checking whether a uri was provided when creating the Fragment.
 * If a uri was provided, we are in edit mode.
 */
public class EditorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Tag for log messages
    private static final String LOG_TAG = EditorFragment.class.getSimpleName() + " TEST";

    // ID of the loader that fetches the data of an existing task to be shown in the editor
    private static final int EDITOR_TASK_LOADER = 1;

    // Key for the task uri in the bundle
    private static final String TASK_URI = "task_uri";

    // References to views
    private View rootView;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private CheckBox doneCheckBox;
    private TextView creationDateTextView;

    // Variable to hold the current time
    private long currentTime;

    // Reference to the parent activity implementing the OnEditorActionListener interface
    private OnEditorActionListener onEditorActionListener;

    // Boolean that indicates whether the user performed any changes
    // in the editor
    private boolean changeDetected;

    // Content URI for the existing task (null if we are in insert mode)
    private Uri currentTaskUri;

    // Required empty public constructor
    public EditorFragment() {

    }

    /**
     * Factory method to create a new instance of
     * this fragment using the provided uri.
     */
    public static EditorFragment newInstance(Uri uri) {
        // Create Fragment
        EditorFragment fragment = new EditorFragment();

        // Check whether we are in insert (uri == null) or edit (uri != null) mode
        if (uri != null) {
            // edit mode
            // save uri in Bundle, so that new Fragments can receive this uri in onCreate()
            Bundle args = new Bundle();
            args.putString(TASK_URI, uri.toString());
            fragment.setArguments(args);
        }
        return fragment;
    }


    /**
     * This is called when this Fragment is attached to its
     * parent activity (= context).
     * Called before onCreate().
     *
     * @param context the parent activity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Make sure the parent activity implements the OnEditorActionListener
        // whose methods we use to communicate with the activity
        if (context instanceof OnEditorActionListener) {
            onEditorActionListener = (OnEditorActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditorActionListener");
        }
    }

    /**
     * The system calls this when creating the fragment. Within your implementation,
     * you should initialize essential components of the fragment that
     * you want to retain when the fragment is paused or stopped, then resumed.
     * (https://developer.android.com/guide/components/fragments.html)
     * Called after onAttach() and before onCreateView().
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");

        String uri = null;
        // Check whether a task uri can be retrieved
        if (getArguments() != null) {
            Bundle args = getArguments();
            uri = args.getString(TASK_URI);
        }
        // if the Bundle contained a uri-String,
        // create a proper uri from that String
        if (uri != null) {
            currentTaskUri = Uri.parse(uri);
        }
    }


    /**
     * The system calls this when it's time for the fragment
     * to draw its user interface for the first time.
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
        rootView = inflater.inflate(R.layout.editor_fragment_layout, container, false);

        // Set up the floating action button the user clicks to save a task
        setUpSubmitButton();

        // Obtain references of the views in the layout
        nameEditText = rootView.findViewById(R.id.edit_text_task_title);
        descriptionEditText = rootView.findViewById(R.id.edit_text_task_description);
        doneCheckBox = rootView.findViewById(R.id.done_checkbox);
        creationDateTextView = rootView.findViewById(R.id.creation_date);

        // display the current date in the creation date task view //TODO overwrite old date?
        currentTime = System.currentTimeMillis();
        creationDateTextView.setText(formatDate(currentTime));

        if (savedInstanceState == null) {
            // init the CursorLoader only in edit mode
            if (editMode()) {
                // Initialize loader that fetches data for the current task from the database
                getLoaderManager().initLoader(EDITOR_TASK_LOADER, null, this);
                Log.d(LOG_TAG, "getLoaderManager().initLoader");
            }
        }

        // add change listeners to the views so that we can warn the user if he leaves the activity
        // while there are unsaved changes
        addChangeListeners();

        // let this fragment's menu-related methods receive calls
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
                boolean saved = saveTask();

                // Inform activity that we're done
                if(saved) onEditorActionListener.onTaskSaved();
            }
        });
    }

    /**
     * Saves or updates a task.
     */
    private boolean saveTask() {
        Log.d(LOG_TAG, "saveTask");

        // Check whether a name is provided. If not, inform the user and return.
        if (TextUtils.isEmpty(nameEditText.getText().toString().trim())) {
            Toast.makeText(getContext(), R.string.missing_name, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Create a ContentValues object form the data in the views
        ContentValues contentValues = getDataFromViews();

        // Check whether we are in edit or insert mode
        if (editMode()) {
            Log.d(LOG_TAG, "editMode");
            // edit mode

            // Check whether the user made any changes to the current task
            // only update when changes have taken place
            if (changeDetected) {
                Log.d(LOG_TAG, "updated uri: " + currentTaskUri);

                // Update the task and get the number of updated rows back
                int updatedRows = getContext().getContentResolver().update(currentTaskUri, contentValues, null, null);

                // Show a toast message depending on whether or not the update was successful
                if (updatedRows == 0) {
                    // If the new content URI is null, then there was an error with update.
                    Toast.makeText(getContext(), getContext().getString(R.string.editor_update_task_failed), Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(getContext(), getContext().getString(R.string.editor_update_task_successful), Toast.LENGTH_SHORT).show();
                }

                // Reset the changeDetected flag (all changes have been saved)
                changeDetected = false;
            }
        } else {
            // insert mode
            Log.d(LOG_TAG, "insertMode");
            Log.d(LOG_TAG, "saveTask, currentTime: " + currentTime);

            // Add the current time to the content values
            contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_CREATION_DATE, currentTime);

            // Insert a new task, get the uri for the new task.
            Uri newTaskUri = getContext().getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

            // Show a toast message depending on whether or not the insertion was successful
            if (newTaskUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(getContext(), getContext().getString(R.string.editor_insert_task_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(getContext(), getContext().getString(R.string.editor_insert_task_successful), Toast.LENGTH_SHORT).show();
            }

            // Reset the changeDetected flag (all changes have been saved)
            changeDetected = false;
        }
        return true;
    }


    /**
     * Create a content values object form the data in the views.
     */
    private ContentValues getDataFromViews() {
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, nameEditText.getText().toString().trim());
        values.put(TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION, descriptionEditText.getText().toString().trim());
        if (doneCheckBox.isChecked()) {
            values.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_YES);
        } else {
            values.put(TaskContract.TaskEntry.COLUMN_TASK_DONE, TaskContract.TaskEntry.DONE_NO);
        }
        return values;
    }


    /**
     * Delete task from database.
     */
    private void deleteTask() {
        // Only perform the delete if we are in edit mode (in insert mode there is no task to delete)
        if (editMode()) {
            Log.d(LOG_TAG, "deleting task " + currentTaskUri);

            // Delete task and get the number of deleted rows back
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

                // Clear all views
                onLoaderReset(null);
            }
        }

        // Reset the changeDetected flag (any potential changes have been deleted)
        changeDetected = false;

        // Inform activity that we're done
        onEditorActionListener.onTaskDeleted();
    }

    /**
     * Creates the menu. This menu will be combined with any preexisting menus from the parent activity.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(LOG_TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_editor, menu);
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(LOG_TAG, "onPrepareOptionsMenu");
        // If we are in insert mode, hide the "Delete" menu item.
        if (!editMode()) {
            MenuItem menuItem = menu.findItem(R.id.menu_item_delete_task);
            menuItem.setVisible(false);
        }
    }

    /**
     * Handles menu item clicks and clicks on the up button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected");
        Log.d(LOG_TAG, "changeDetected: " + changeDetected);
        // Check which menu item was clicked on
        switch (item.getItemId()) {
            case android.R.id.home:
                // up button was clicked
                // show warning dialog if there are unsaved changes
                if (changeDetected) {
                    showExitConfirmationDialog();
                } else {
                    // navigate up
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_delete_task:
                // delete button was clicked
                // show confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
        }
        return false;
    }

    /**
     * This Dialog lets the user confirm that he indeed wants to delete a task.
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.alert_delete_single_task);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User wants to indeed delete the task.
                deleteTask();
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
     * Is shown when the user wants to leave the activity despite unsaved changes.
     */
    public void showExitConfirmationDialog() {
        // ask the user if he wants to discard the changes or cancel the up navigation
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.warning_message);
        builder.setPositiveButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "discard changes");
                if (dialog != null) {
                    // user wants to leave, so we can forget about any unsaved changes
                    changeDetected = false;
                    // navigate up
                    NavUtils.navigateUpFromSameTask(getActivity());
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
     * Adds change listeners to the text fields and checkbox to keep track of whether
     * any changes were made to the task.
     */
    private void addChangeListeners() {

        // Create TextWatcher that sets the changeDetected flag to true
        // if the user changes the content of any of the TextViews (name or description)
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeDetected = true;
                Log.d(LOG_TAG, "onTextChanged " + charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };

        Log.d(LOG_TAG, "addChangeListeners");
        nameEditText.addTextChangedListener(textWatcher);
        descriptionEditText.addTextChangedListener(textWatcher);
        doneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                changeDetected = true;
                Log.d(LOG_TAG, "doneCheckBox onCheckedChanged");
            }
        });
    }


    // -------------------------- Loader callback methods ----------------------------

    /**
     * Called after the initialization of the loader.
     * Returns a CursorLoader that fetches task data from the
     * database.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, Bundle args) {
        // Check whether the current loader_id matches the one
        // we initialized the loader with
        switch (loader_id) {
            case EDITOR_TASK_LOADER:
                // Return cursor loader that queries the task provider for a single task
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


    /**
     * Called when the Loader has finished loading.
     *
     * @param loader the current loader
     * @param cursor cursor containing loaded data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Exit the method if the cursor is empty
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

        // Reset the changeDetected flag (new task has just been loaded)
        changeDetected = false;
    }

    /**
     * Called when the loader is reset. Clears all views in the editor.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText(null);
        descriptionEditText.setText(null);
        creationDateTextView.setText(null);
        doneCheckBox.setChecked(false);
        changeDetected = false;
    }

    /**
     * Formats a date (in ms) to a String
     */
    private String formatDate(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        DateFormat formatter = DateFormat.getDateInstance();
        return formatter.format(calendar.getTime());
    }

    public boolean hasChangeDetected() {
        return changeDetected;
    }

    public void setChangeDetected(boolean changeDetected) {
        this.changeDetected = changeDetected;
    }

    /**
     * Checks whether the EditFragment is in edit or insert mode.
     */
    private boolean editMode() {
        return currentTaskUri != null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment.
     * Currently, the MainActivity and the EditorActivity can contain the
     * EditFragment and must therefore implement this interface.
     */
    public interface OnEditorActionListener {
        void onTaskSaved();

        void onTaskDeleted();
    }


    // ----------------------- Debugging methods ------------------------------
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
}
