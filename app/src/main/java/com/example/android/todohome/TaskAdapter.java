package com.example.android.todohome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.android.todohome.model.Task;

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
        checkBox.setChecked(task.isDone());
//        Log.d(LOG_TAG, "TEST getView: " + task.isDone() + " checkBox.isSelected: " + checkBox.isChecked());
        titleTextView.setText(task.getTitle());

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Log.d(LOG_TAG, "TEST checked changed: " + b);
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(LOG_TAG, "TEST checkbox clicked");
                // How to update the task in the array list of the adapter from here? position of
                // clicked item in list unknown...?
            }
        });

        return convertView;
    }
}
