package com.example.android.todohome;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class TaskActivity extends AppCompatActivity {

    private Task currentTask;
    private int position;
    EditText title;
    EditText description;
    CheckBox done;

    private static final String LOG_TAG = TaskActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTaskBack();
            }
        });

        // Obtain references of the views in the layout
        title = findViewById(R.id.edit_text_task_title);
        description = findViewById(R.id.edit_text_task_description);
        done = findViewById(R.id.done_checkbox);

        // Get the intent that started the activity
        Intent intent = getIntent();

        // Extract the task object that came with the intent
        currentTask = intent.getParcelableExtra(MainActivity.EXTRA_MESSAGE);
        Log.d(LOG_TAG, "TEST: Received task object " + currentTask);

        // Fill the views with data from the task
        title.setText(currentTask.getTitle());
        description.setText(currentTask.getDescription());
        done.setChecked(currentTask.isDone());
    }


    private void sendTaskBack() {
        Log.d(LOG_TAG, "TEST sendTaskBack");
        // Update task object with UI data
        currentTask.setTitle(title.getText().toString());
        currentTask.setDescription(description.getText().toString());
        currentTask.setDone(done.isChecked());

        // Send the potentially updated task back to the MainActivity (we could also check whether any changes were made at all and only send the task back if changes were made)
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_MESSAGE, currentTask);
        setResult(RESULT_OK, intent);
        finish();
    }
}
