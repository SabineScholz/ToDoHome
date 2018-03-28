package com.example.android.todohome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.android.todohome.TASK";
    public static final int REQUEST_CODE = 0;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    TaskAdapter taskAdapter;
    ListView taskListView;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "TEST: onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create list with fake tasks
        List<Task> tasks = createTaskList();

        // Find a reference to the ListView in the layout
        taskListView = (ListView) findViewById(R.id.list_view);

        // Create an Adapter to display Task objects in the ListView
        taskAdapter = new TaskAdapter(this, tasks);

        // Attach adapter to ListView
        taskListView.setAdapter(taskAdapter);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                currentPosition = position;
                Log.d(LOG_TAG, "TEST: Click on Task " + taskAdapter.getItem(position));
                Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
                intent.putExtra(EXTRA_MESSAGE, taskAdapter.getItem(position));
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure that this result corresponds to the request we made and that the request was successful
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            // Extract the result
            Task task = data.getParcelableExtra(EXTRA_MESSAGE);

            // Update the task in the adapter
            taskAdapter.remove(currentPosition);
            taskAdapter.add(task);

            Log.d(LOG_TAG,"TEST onActivityResult, task send back from TaskActivity: " + task);

            // Update the listview
            taskAdapter.notifyDataSetChanged();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private List<Task> createTaskList() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Groceries", "Doing groceries (bananas)", false));
        tasks.add(new Task("Empty the trash", "[description]", true));
        tasks.add(new Task("Walk the dog", "[description]", false));
        tasks.add(new Task("Clean the house", "[description]", true));
        return tasks;
    }
}
