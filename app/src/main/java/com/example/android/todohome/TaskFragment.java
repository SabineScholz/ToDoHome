package com.example.android.todohome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.android.todohome.model.Task;


public class TaskFragment extends Fragment {

    private static final String LOG_TAG = TaskFragment.class.getSimpleName() + " TEST";
    private Task currentTask;
    private EditText title;
    private EditText description;
    private CheckBox done;
    private View rootView;
    private boolean change_detected;

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
        initReferences();

        // Get the intent that started this fragment
        Intent intent = getActivity().getIntent();


        // Check whether the intent contains a task that is to be edited
        if (intent.hasExtra(MainActivity.TASK_MESSAGE)) {

            // Extract the task object that came with the intent
            currentTask = intent.getParcelableExtra(MainActivity.TASK_MESSAGE);
            Log.d(LOG_TAG, "Received task object " + currentTask);

            // Fill the views with data from the task
            setDataInViews();

        } else {

            // Create new task
            currentTask = new Task();
        }

        // add change listeners to the views so that we can warn the user if he leaves the activity
        // while there are unsaved changes
        addChangeListeners();

        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Fills the views with data from the task
     */
    private void setDataInViews() {
        title.setText(currentTask.getName());
        description.setText(currentTask.getDescription());
        done.setChecked(currentTask.isDone());
    }

    /**
     * Obtains references of the views in the layout
     */
    private void initReferences() {
        title = rootView.findViewById(R.id.edit_text_task_title);
        description = rootView.findViewById(R.id.edit_text_task_description);
        done = rootView.findViewById(R.id.done_checkbox);
    }

    /**
     * Sets up the button with which the user submits the task data
     */
    private void setUpSubmitButton() {
        FloatingActionButton fab = rootView.findViewById(R.id.save_task_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTaskBack();
            }
        });
    }

    /**
     * Sends the task whose details are shown in this activity back to the MainActivity.
     */
    public void sendTaskBack() {

        // Update task object with UI data
        currentTask.setName(title.getText().toString());
        currentTask.setDescription(description.getText().toString());
        currentTask.setDone(done.isChecked());

        Log.d(LOG_TAG, "sendTaskBack " + currentTask);

        // Send the potentially updated task back to the MainActivity (we could also check whether any changes were made at all and only send the task back if changes were made)
        Intent intent = new Intent();
        intent.putExtra(MainActivity.TASK_MESSAGE, currentTask);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
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
        title.addTextChangedListener(textWatcher);
        description.addTextChangedListener(textWatcher);
        done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                change_detected = true;
            }
        });
    }

    public boolean hasChanged() {
        return change_detected;
    }
}