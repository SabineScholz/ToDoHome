package com.example.android.todohome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.todohome.model.Task;
import com.example.android.todohome.model.TaskList;

public class MainActivity extends AppCompatActivity {

    public static final String TASK_MESSAGE = "com.example.android.todohome.TASK";
    public static final String TASKS_KEY = "com.example.android.todohome.TASKLIST";
    private static final int REQUEST_CODE_EDIT_TASK = 0;
    private static final int REQUEST_CODE_CREATE_TASK = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName() + " TEST";

    /* sources for filter option
    https://stackoverflow.com/questions/24769257/custom-listview-adapter-with-filter-android/24771174#24771174
    https://www.survivingwithandroid.com/2012/10/android-listview-custom-filter.html
    https://gist.github.com/DeepakRattan/26521c404ffd7071d0a4
     */

    private TaskList originalTaskList;
    private TaskAdapter taskAdapter;
    private ListView taskListView;

//    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create list with fake currentTaskList if no old list is available
        if (savedInstanceState == null) {
            originalTaskList = createTaskList();
//            currentTaskList = (TaskList) originalTaskList.clone();
            Log.d(LOG_TAG, "createTaskList");
        } else {
            originalTaskList = (TaskList) savedInstanceState.get(TASKS_KEY);
//            currentTaskList = (TaskList) originalTaskList.clone();
            Log.d(LOG_TAG, "load old list");
        }

        // Find a reference to the ListView in the layout
        taskListView = findViewById(R.id.list_view);
//        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
//        mRecyclerView.setHasFixedSize(true);
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new RecyclerAdapter(originalTaskList);
//        mRecyclerView.setAdapter(mAdapter);

        // Create an adapter to display task objects in the ListView
        taskAdapter = new TaskAdapter(this, originalTaskList);

        // Attach adapter to ListView
        taskListView.setAdapter(taskAdapter);

        // Set a click listener on the listview that is triggered when
        // someone clicks on an item in the list
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(LOG_TAG, "Click on list item " + taskAdapter.getItem(position));

                // Start the activity that show the details of the
                // task that was clicked on. Put the task object into the intent.
                Intent intent = new Intent(getApplicationContext(), EditTaskActivity.class);
                intent.putExtra(TASK_MESSAGE, taskAdapter.getItem(position));
                startActivityForResult(intent, REQUEST_CODE_CREATE_TASK);
            }
        });

        // Set listener on "add new task" button
        FloatingActionButton addNewTaskButton = findViewById(R.id.add_new_button);
        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "add new task");

                // Start the activity with which new currentTaskList can be created
                Intent intent = new Intent(getApplicationContext(), CreateTaskActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_all_tasks:
                taskAdapter.getFilter().filter(TaskAdapter.SHOW_ALL);
                return true;
            case R.id.menu_item_unfinished_tasks:
                taskAdapter.getFilter().filter(TaskAdapter.SHOW_UNFINISHED);
                return true;
            case R.id.menu_item_delete_finished_tasks:
                deleteFinishedTasks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteFinishedTasks() {
        originalTaskList.deleteFinishedTasks();
        taskAdapter.notifyDataSetChanged();
//        originalTaskList = originalTaskList.getUnfinishedTasks();
//        taskAdapter = new TaskAdapter(this, originalTaskList);
//        taskListView.setAdapter(taskAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");
        outState.putParcelableArrayList(TASKS_KEY, originalTaskList);
        super.onSaveInstanceState(outState);
    }

    /**
     * This function is called when the child activity this activity started
     * has finished and returns its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(LOG_TAG, "onActivityResult requestCode: " + requestCode + ", resultCode: " + resultCode);

        // Make sure that this result corresponds to the request we made and that the request was successful
        if ((requestCode == REQUEST_CODE_EDIT_TASK || requestCode == REQUEST_CODE_CREATE_TASK) && resultCode == RESULT_OK) {

            // Extract the task object from the intent
            Task task = intent.getParcelableExtra(TASK_MESSAGE);
            Log.d(LOG_TAG, "onActivityResult: " + task);

            // Update or add the task
            updateOrAddTask(task);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * Updates an already existing task in the currentTaskList list with the data contained
     * in this task.
     * If the task does not exist in the list yet, it is added to the list.
     */
    private void updateOrAddTask(Task task) {
        if (!originalTaskList.contains(task)) {
            originalTaskList.add(task);
            taskAdapter.refreshFilter();
            Log.d(LOG_TAG, "added task");
        } else {
            originalTaskList.update(task);
            taskAdapter.refreshFilter();
            Log.d(LOG_TAG, "updated existing task");
        }
    }

    /**
     * Creates list of task objects.
     *
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

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
    }
}
