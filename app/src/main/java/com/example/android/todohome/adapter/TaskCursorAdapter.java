package com.example.android.todohome.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.todohome.R;
import com.example.android.todohome.model.TaskContract;


/**
 * The TaskCursorAdapter creates the row views for the ListView using cursor data.
 * This is done using the ViewHolder design pattern.
 */
public class TaskCursorAdapter extends CursorAdapter {

    // Tag for log messages
    private static final String LOG_TAG = TaskCursorAdapter.class.getSimpleName() + " TEST";

    // Filter options
    public static final CharSequence SHOW_UNFINISHED = "0";
    public static final CharSequence SHOW_ALL = "1";

    // Reference to activity or fragment that implements the onCheckboxClickListener interface
    private onCheckboxClickListener onCheckboxClickListener;

    public TaskCursorAdapter(Context context, Cursor c, onCheckboxClickListener onCheckboxClickListener) {
        super(context, c, 0); // TODO flags = 0 okay?
        this.onCheckboxClickListener = onCheckboxClickListener;
        Log.d(LOG_TAG, "TaskCursorAdapter()");
    }


    /**
     * This method creates a new item view and wires it up with a view holder.
     * No data is added to the view yet (done in bindView())
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        Log.d(LOG_TAG, "newView");

        // Create new item view
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

    /**
     * Inner ViewHolder class.
     * The ViewHolder keeps references to the subviews of each row view.
     * If an row view is to be reused, its view holder binds the new data
     * to the subviews using the stored references to these views.
     *
     */
    private class TaskViewHolder {

        // References to the subviews
        private TextView nameTextView;
        private CheckBox checkBox;

        public TaskViewHolder(View itemView) {
//            Log.d(LOG_TAG, "TaskViewHolder()");

            // Find sub views of the row view
            nameTextView = itemView.findViewById(R.id.name_text_view);
            checkBox = itemView.findViewById(R.id.checkbox);

            // Set click listener on the checkbox of the row view,
            // so that users can mark a task as done or not done
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // All of the following is necessary to
                    // get the id (not the position!) of the task whose
                    // checkbox was clicked

                    // Get the parent of the view that was clicked on (parent will be the row view)
                    View parentRow = (View) view.getParent();

                    // Get the parent of the row view (which will be the ListView)
                    ListView listView = (ListView) parentRow.getParent();

                    // Get the position of the row view within the listView
                    int position = listView.getPositionForView(parentRow);

                    // Get the id of the item that is shown in listView at this position
                    long id = listView.getItemIdAtPosition(position);

                    // Make the clicked view a checkbox (which it always was)
                    CheckBox checkBox = (CheckBox) view;

                    // Tell the Activity or Fragment that implements the OnCheckboxClickListener interface
                    // that the checkbox corresponding to the task having a given id has been clicked on.
                    // Only the id of the task and whether or not the checkbox is checked needs to
                    // be send. The Activity or Fragment will take care of updating the task
                    // in the database.
                    onCheckboxClickListener.onCheckboxClick(id, checkBox.isChecked());
                }
            });
        }

        /**
         * Fills the sub views of the row view with data from a cursor.
         */
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

    /**
     * The Activity or Fragment that needs to be informed
     * about clicks on the "done"-checkbox in a row view of the ListView
     * has to implement this interface.
     */
    public interface onCheckboxClickListener {
        void onCheckboxClick(long clickedItemIndex, boolean taskDone);
    }
}
