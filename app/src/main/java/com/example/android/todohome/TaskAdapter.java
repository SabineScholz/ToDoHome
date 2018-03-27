package com.example.android.todohome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabine on 27.03.2018.
 */

public class TaskAdapter extends ArrayAdapter<Task> {

    private static final String LOG_TAG = TaskAdapter.class.getSimpleName();


    public TaskAdapter(@NonNull Context context, List<Task> tasks) {
        super(context, 0, tasks);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }

        // Get the Views from the convertView
        CheckBox checkBox = convertView.findViewById(R.id.checkbox);
        TextView titleTextView = convertView.findViewById(R.id.title_text_view);

        // Fill the views with data from the current Task object
        checkBox.setSelected(task.isDone());
        titleTextView.setText(task.getTitle());

        return convertView;
    }
}
