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

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create list with fake tasks
        List<Task> tasks = createTaskList();
        //List<Task> tasks = new ArrayList<>();

        // Find a reference to the ListView in the layout
        ListView taskListView = (ListView) findViewById(R.id.list_view);

        // Create an Adapter to display Task objects in the ListView
        taskAdapter = new TaskAdapter(this, tasks);

        // Attach adapter to ListView
        taskListView.setAdapter(taskAdapter);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private List<Task> createTaskList() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Groceries", "Doing groceries (bananas)", false));
        tasks.add(new Task("Empty the trash", "description...", true));
        tasks.add(new Task("Walk the dog", "description", false));
        tasks.add(new Task("Clean the house", "description", true));
        return tasks;
    }
}
