package com.example.android.todohome.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.android.todohome.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sabine on 27.03.2018.
 */

public class TaskAdapter extends ArrayAdapter<Task> implements Filterable {

    private static final String LOG_TAG = TaskAdapter.class.getSimpleName() + " TEST";
    public static final String SHOW_ALL = "1";
    public static final String SHOW_UNFINISHED = "2";

    private TaskList originalData = null;
    private TaskList filteredData = null;
    private LayoutInflater inflater;
    private TaskFilter taskFilter = new TaskFilter();
    private String mode;


    public TaskAdapter(Context context, TaskList tasks) {
        super(context, 0, tasks); //?
        this.originalData = tasks;
        this.filteredData = tasks;
        this.inflater = LayoutInflater.from(context);
        mode = SHOW_ALL;
    }

    @Override
    public int getCount() {
        Log.d(LOG_TAG, "getCount: " + filteredData.size());
        return filteredData.size();
    }

    @Override
    public Task getItem(int position) {
        Log.d(LOG_TAG, "getItem: " + position);
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * TODO add viewHolder
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }

        // Get the Views from the convertView
        final CheckBox checkBox = convertView.findViewById(R.id.checkbox);
        TextView titleTextView = convertView.findViewById(R.id.title_text_view);
        TextView creationdateTextView = convertView.findViewById(R.id.creation_date);

        // Fill the views with data from the current Task object
        checkBox.setChecked(task.isDone());
        titleTextView.setText(task.getTitle());
        creationdateTextView.setText(formatDate(task.getCreationDate()));

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getItem(position).setDone(checkBox.isChecked());
            }
        });

        return convertView;
    }

    private String formatTime(Calendar date) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
        return sdf.format(date.getTime());
    }

    private String formatDate(Date date) {
        return DateFormat.getDateInstance().format(date);
    }

    @Override
    public Filter getFilter() {
        return taskFilter;
    }


    // TODO: what should happen if the user applies the show-unfinished filter and marks a task as done afterwards? let task disappear immediately?
    public void refreshFilter() {
        Log.d(LOG_TAG, "refreshFilter()");
        if(mode.equals(TaskAdapter.SHOW_ALL)) {
            getFilter().filter(TaskAdapter.SHOW_ALL);
            Log.d(LOG_TAG, "SHOW_ALL");
        } else {
            getFilter().filter(TaskAdapter.SHOW_UNFINISHED);
            Log.d(LOG_TAG, "SHOW_UNFINISHED");
        }
    }

    private class TaskFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint.toString().equals(SHOW_ALL)) {
                mode = SHOW_ALL;
                return getAllTasks();
            } else if (constraint.toString().equals(SHOW_UNFINISHED)) {
                mode = SHOW_UNFINISHED;
                return getUnfinishedTasks();
            } else {
                return null;
            }
        }

        private FilterResults getUnfinishedTasks() {
            FilterResults results = new FilterResults();
            TaskList unfinishedTasks = new TaskList();
            for (Task task : originalData) {
                if (!task.isDone()) {
                    unfinishedTasks.add(task);
                }
            }
            results.values = unfinishedTasks;
            results.count = unfinishedTasks.size();
            return results;
        }

        private FilterResults getAllTasks() {
            FilterResults results = new FilterResults();
            results.values = originalData;
            results.count = originalData.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredData = (TaskList) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
