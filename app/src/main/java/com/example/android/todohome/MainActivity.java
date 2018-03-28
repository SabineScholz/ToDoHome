package com.example.android.todohome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.todohome.model.Task;
import com.example.android.todohome.model.TaskList;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.android.todohome.TASK";
    public static final int REQUEST_CODE = 0;
    private static final String LOG_TAG = MainActivity.class.getSimpleName() + " TEST";

    private TaskList tasks;
    private TaskAdapter taskAdapter;
    private ListView taskListView;
//    private int currentPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create list with fake tasks
        tasks = createTaskList();

        // Find a reference to the ListView in the layout
        taskListView = (ListView) findViewById(R.id.list_view);

        // Create an adapter to display task objects in the ListView
        taskAdapter = new TaskAdapter(this, tasks);

        // Attach adapter to ListView
        taskListView.setAdapter(taskAdapter);

        // Set a click listener on the listview that is triggered when
        // someone clicks on an item in the list
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                currentPosition = position;
                Log.d(LOG_TAG, "Click on list item " + taskAdapter.getItem(position));

                // Start the activity that show the details of the
                // task that was clicked on. Put the task object into the intent.
                Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
                intent.putExtra(EXTRA_MESSAGE, taskAdapter.getItem(position));
                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     * This function is called when the child activity this activity started
     * has finished and returns its result.
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // Make sure that this result corresponds to the request we made and that the request was successful
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            // Extract the task object from the intent
            Task task = intent.getParcelableExtra(EXTRA_MESSAGE);
            Log.d(LOG_TAG,"onActivityResult: " + task);

            // Update the task in the list
            updateOrAddTask(task);

            // Notify the adapter that its data have changed
            taskAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * Updates an already existing task in the tasks list with the data contained
     * in this task.
     * If the task does not exist in the list yet, it is added to the list.
     * @param task
     */
    private void updateOrAddTask(Task task) {
        if(!tasks.contains(task)) {
            tasks.add(task);
            Log.d(LOG_TAG, "added task");
        } else {
            tasks.update(task);
            Log.d(LOG_TAG, "updated existing task");
        }
    }

    /**
     * Creates list of task objects.
     * @return TaskList (ArrayList)
     */
    private TaskList createTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("Groceries", "Doing groceries (bananas)", false));
        tasks.add(new Task("Empty the trash", "[description]", true));
        tasks.add(new Task("Walk the dog", "[description]", false));
        tasks.add(new Task("Clean the house", "[description]", true));
        return tasks;
    }
}
