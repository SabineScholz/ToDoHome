package com.example.android.todohome.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class TaskContract {

    // Set name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.tasks";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Set the path for the tasks table (appended to the base content uri)
    public static final String PATH_TASKS = "tasks";

    // Contract class should not be instantiated, so make constructor private
    private TaskContract() {
    }

    /**
     * Inner class that defines the constant values for the tasks table
     */
    public static abstract class TaskEntry implements BaseColumns {

        // Content uri to access the tasks
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TASKS);

        // MIME type of the tasks list
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

        // MIME type for a single task
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

        // table name
        public static final String TABLE_NAME = "tasks";

        // column names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TASK_NAME = "name";
        public static final String COLUMN_TASK_DESCRIPTION = "description";
        public static final String COLUMN_TASK_CREATION_DATE = "creation_date";
        public static final String COLUMN_TASK_DONE = "done";

        // values for the done column
        public static final int DONE_NO = 0;
        public static final int DONE_YES = 1;

        // method to check whether a "done" integer is valid
        public static boolean isValidDoneValue(Integer done) {
            return done == TaskEntry.DONE_YES || done == TaskEntry.DONE_NO;
        }
    }
}
