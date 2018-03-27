package com.example.android.todohome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

public class TaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        // Obtain references of the views in the layout
        EditText title = findViewById(R.id.edit_text_task_title);
        EditText description = findViewById(R.id.edit_text_task_description);
        CheckBox done = findViewById(R.id.done_checkbox);

        // Get the intent that started the activity
        Intent intent = getIntent();

        // Extract the task object that came with the intent
        Task task = intent.getParcelableExtra(MainActivity.EXTRA_MESSAGE);

        // Fill the views with data from the task
        title.setText(task.getTitle());
        description.setText(task.getDescription());
        done.setSelected(task.isDone());


    }
}
