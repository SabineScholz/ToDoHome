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
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.android.todohome.model.Task;
import com.example.android.todohome.model.TaskList;

import java.util.Collection;

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



    public TaskAdapter(Context context, TaskList tasks) {
        super(context, 0, tasks); //?
        this.originalData = tasks;
        this.filteredData = tasks;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
//        Log.d(LOG_TAG, "getCount: " + filteredData.size());
        return filteredData.size();
    }

    @Override
    public Task getItem(int position) {
//        Log.d(LOG_TAG, "getItem: " + position);
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

        // Fill the views with data from the current Task object
        checkBox.setChecked(task.isDone());
//        Log.d(LOG_TAG, "TEST getView: " + task.isDone() + " checkBox.isSelected: " + checkBox.isChecked());
        titleTextView.setText(task.getTitle());

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getItem(position).setDone(checkBox.isChecked());
//                Log.d(LOG_TAG, "TEST checked changed: " + b);
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return taskFilter;
    }

    private class TaskFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint.toString().equals(SHOW_ALL)) {
                return getAllTasks();
            } else if (constraint.toString().equals(SHOW_UNFINISHED)) {
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
