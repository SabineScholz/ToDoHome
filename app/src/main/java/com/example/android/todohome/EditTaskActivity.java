package com.example.android.todohome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.android.todohome.model.Task;

public class EditTaskActivity extends AppCompatActivity {

    private Task currentTask;
    private EditText title;
    private EditText description;
    private CheckBox done;

    private static final String LOG_TAG = EditTaskActivity.class.getSimpleName() + " TEST";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        setUpSubmitButton();

        // Obtain references of the views in the layout
        initReferences();

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Extract the task object that came with the intent
        currentTask = intent.getParcelableExtra(MainActivity.EXTRA_MESSAGE);
        Log.d(LOG_TAG, "Received task object " + currentTask);

        // Fill the views with data from the task
        setDataInViews();

        // add change listeners to the views so that we can warn the user if he leaves the activity
        // while there are unsaved changes
        addChangeListeners();
    }

    /**
     * Fills the views with data from the task
     */
    private void setDataInViews() {
        title.setText(currentTask.getTitle());
        description.setText(currentTask.getDescription());
        done.setChecked(currentTask.isDone());
    }

    /**
     * Obtains references of the views in the layout
     */
    private void initReferences() {
        title = findViewById(R.id.edit_text_task_title);
        description = findViewById(R.id.edit_text_task_description);
        done = findViewById(R.id.done_checkbox);
    }

    /**
     * Sets up the button with which the user submits the task data
     */
    private void setUpSubmitButton() {
        FloatingActionButton fab = findViewById(R.id.save_task_button);
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
        currentTask.setTitle(title.getText().toString());
        currentTask.setDescription(description.getText().toString());
        currentTask.setDone(done.isChecked());

        Log.d(LOG_TAG, "sendTaskBack " + currentTask);

        // Send the potentially updated task back to the MainActivity (we could also check whether any changes were made at all and only send the task back if changes were made)
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_MESSAGE, currentTask);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Is triggered when the up button is clicked on. If changes have been made
     * to the task data, the user is asked whether the changes should be saved or discarded.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (change_detected) {
                    new WarningDialogFragment().show(getFragmentManager(), "warning");
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
}
