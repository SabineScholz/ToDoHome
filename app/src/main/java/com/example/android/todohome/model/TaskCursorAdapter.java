package com.example.android.todohome.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
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

public class TaskCursorAdapter extends CursorAdapter implements Filterable {

    private static final String LOG_TAG = TaskCursorAdapter.class.getSimpleName() + " TEST";

    private CheckboxClickListener checkboxClickListener;

    public TaskCursorAdapter(Context context, Cursor c, CheckboxClickListener checkboxClickListener) {
        super(context, c);
        this.checkboxClickListener = checkboxClickListener;
        Log.d(LOG_TAG, "TaskCursorAdapter()");
    }


//    public static final String SHOW_ALL = "1";
//    public static final String SHOW_UNFINISHED = "2";


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(LOG_TAG, "newView");

        // Create new row view
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);

        // Bind view holder to view
        TaskViewHolder taskViewHolder = new TaskViewHolder(view);

        // Store the view holder as a tag of the row view
        // If the row view is to be reused, be can access its view holder using this tag (see bindView())
        view.setTag(taskViewHolder);

        return view;
    }

    /**
     * Bind the task data in the cursor to the sub views of the given view.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(LOG_TAG, "bindView");

        // Retrieve view holder from row view
        TaskViewHolder taskViewHolder = (TaskViewHolder) view.getTag();

        // Delegate the binding to the view holder, which extracts the data from the cursor and
        // fills the sub views of the row view with these data
        taskViewHolder.bind(cursor);
    }

    public interface CheckboxClickListener {
        void onCheckboxClick(int clickedItemIndex);
    }


    private class TaskViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private CheckBox checkBox;

        public TaskViewHolder(View itemView) {
            Log.d(LOG_TAG, "TaskViewHolder()");

            // Find sub views of the row view
            nameTextView = itemView.findViewById(R.id.name_text_view);
            checkBox = itemView.findViewById(R.id.checkbox);
//            checkBox.setOnClickListener(this);
        }

        public void bind(Cursor cursor) {

            // Find the column indexes within the cursor
            int nameColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME);
            int doneColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DONE);

            // Extract task data from the cursor
            String name = cursor.getString(nameColumnIndex);
            int done = cursor.getInt(doneColumnIndex);

            // Populate views with extracted data
            nameTextView.setText(name);
            if (done == TaskContract.TaskEntry.DONE_YES) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            checkBox.setTag(cursor.getPosition());
        }

        @Override
        public void onClick(View view) {
            checkboxClickListener.onCheckboxClick((int) view.getTag());
        }
    }


    private String formatTime(Calendar date) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
        return sdf.format(date.getTime());
    }

    private String formatDate(Date date) {
        return DateFormat.getDateInstance().format(date);
    }

//    @Override
//    public Filter getFilter() {
//        return taskFilter;
//    }


//    // TODO: what should happen if the user applies the show-unfinished filter and marks a task as done afterwards? let task disappear immediately?
//    public void refreshFilter() {
//        Log.d(LOG_TAG, "refreshFilter()");
//        if(mode.equals(TaskCursorAdapter.SHOW_ALL)) {
//            getFilter().filter(TaskCursorAdapter.SHOW_ALL);
//            Log.d(LOG_TAG, "SHOW_ALL");
//        } else {
//            getFilter().filter(TaskCursorAdapter.SHOW_UNFINISHED);
//            Log.d(LOG_TAG, "SHOW_UNFINISHED");
//        }
//    }

//    private class TaskFilter extends Filter {
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//
//            if (constraint.toString().equals(SHOW_ALL)) {
//                mode = SHOW_ALL;
//                return getAllTasks();
//            } else if (constraint.toString().equals(SHOW_UNFINISHED)) {
//                mode = SHOW_UNFINISHED;
//                return getUnfinishedTasks();
//            } else {
//                return null;
//            }
//        }
//
//        private FilterResults getUnfinishedTasks() {
//            FilterResults results = new FilterResults();
//            TaskList unfinishedTasks = new TaskList();
//            for (Task task : originalData) {
//                if (!task.isDone()) {
//                    unfinishedTasks.add(task);
//                }
//            }
//            results.values = unfinishedTasks;
//            results.count = unfinishedTasks.size();
//            return results;
//        }
//
//        private FilterResults getAllTasks() {
//            FilterResults results = new FilterResults();
//            results.values = originalData;
//            results.count = originalData.size();
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//            filteredData = (TaskList) filterResults.values;
//            notifyDataSetChanged();
//        }
//    }
}
