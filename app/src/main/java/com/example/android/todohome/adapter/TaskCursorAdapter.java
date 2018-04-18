package com.example.android.todohome.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.todohome.R;
import com.example.android.todohome.model.TaskContract;


public class TaskCursorAdapter extends CursorAdapter {

    public static final CharSequence SHOW_UNFINISHED = "0";
    public static final CharSequence SHOW_ALL = "1";
    private static final String LOG_TAG = TaskCursorAdapter.class.getSimpleName() + " TEST";
    private CheckboxClickListener checkboxClickListener;

    public TaskCursorAdapter(Context context, Cursor c, CheckboxClickListener checkboxClickListener) {
        super(context, c);
        this.checkboxClickListener = checkboxClickListener;
        Log.d(LOG_TAG, "TaskCursorAdapter()");
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        Log.d(LOG_TAG, "newView");

        // Create new row view
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);

        // Bind view holder to view
        TaskViewHolder taskViewHolder = new TaskViewHolder(view);

        // Store the view holder as a tag of the row view
        // If the row view is to be reused, be can access its view holder using this tag (see bindView())
        view.setTag(taskViewHolder);
        view.setTag(R.id.TAG_TASK_ID, parent.getId());

        return view;
    }

    /**
     * Bind the task data in the cursor to the sub views of the given view.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Retrieve view holder from row view
        TaskViewHolder taskViewHolder = (TaskViewHolder) view.getTag();

        // Delegate the binding to the view holder, which extracts the data from the cursor and
        // fills the sub views of the row view with these data
        taskViewHolder.bind(cursor);
    }


    public interface CheckboxClickListener {
        void onCheckboxClick(long clickedItemIndex, boolean taskDone);
    }


    private class TaskViewHolder {

        private TextView nameTextView;
        private CheckBox checkBox;

        public TaskViewHolder(View itemView) {
//            Log.d(LOG_TAG, "TaskViewHolder()");

            // Find sub views of the row view
            nameTextView = itemView.findViewById(R.id.name_text_view);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View parentRow = (View) view.getParent();
                    ListView listView = (ListView) parentRow.getParent();
                    int position = listView.getPositionForView(parentRow);
                    long id = listView.getItemIdAtPosition(position);
                    CheckBox checkBox = (CheckBox) view;
                    checkboxClickListener.onCheckboxClick(id, checkBox.isChecked());
                }
            });
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
        }
    }
}
