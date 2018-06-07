package com.example.android.todohome.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class TaskDbHelper extends SQLiteOpenHelper {

    // Tag for log messages
    public static final String LOG_TAG = TaskDbHelper.class.getSimpleName() + " TEST";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tasks.db";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME;

    private static final String SQL_CREATE_DATABASE =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " ( " +
                    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TaskContract.TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, " +
                    TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION + " TEXT, " +
                    TaskContract.TaskEntry.COLUMN_TASK_CREATION_DATE + " BIGINT NOT NULL, " +
                    TaskContract.TaskEntry.COLUMN_TASK_DONE + " INTEGER NOT NULL);";

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
