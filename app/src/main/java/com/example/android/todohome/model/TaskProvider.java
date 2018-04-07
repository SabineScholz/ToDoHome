package com.example.android.todohome.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.android.todohome.model.TaskContract.CONTENT_AUTHORITY;
import static com.example.android.todohome.model.TaskContract.PATH_TASKS;

public class TaskProvider extends ContentProvider {

    public static final String LOG_TAG = TaskProvider.class.getSimpleName() + " TEST";

    // Set up uri codes for uri matcher
    private static final int TASKS = 100;
    private static final int TASK_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Inform the uri matcher about the uris and their respective codes
    static {
        // uri of tasks table
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_TASKS, TASKS);

        // uri of a single task entry
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_TASKS + "/#", TASK_ID);
    }

    private TaskDbHelper taskDbHelper;

    @Override
    public boolean onCreate() {
        taskDbHelper = new TaskDbHelper(getContext());
        return true;
    }


    /**
     * Perform the query for the given uri using the given
     * projection, selection, selection arguments and sort order.
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase db = taskDbHelper.getReadableDatabase();
        Cursor cursor = null;

        int uriCode = uriMatcher.match(uri);

        switch (uriCode) {
            case TASKS:
                // perform a query on the entire tasks table
                cursor = db.query(
                        TaskContract.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TASK_ID:
                // a single task entry is queried
                // extract the task id from the uri and set it as the selection argument
                selection = TaskContract.TaskEntry._ID + " = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(
                        TaskContract.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set up notification uri on the cursor.
        // The cursor will listen to changes in that uri.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int uriCode = uriMatcher.match(uri);
        switch (uriCode) {
            case TASKS:
                return TaskContract.TaskEntry.CONTENT_LIST_TYPE;
            case TASK_ID:
                return TaskContract.TaskEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri);
        }
    }


    /**
     * Insert a new task with the given content values into the tasks table.
     * @param uri
     * @param contentValues
     * @return Uri of the inserted task.
     */
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int uriCode = uriMatcher.match(uri);
        switch (uriCode) {
            case TASKS:
                return insertTask(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertTask(Uri uri, ContentValues contentValues) {

        // TODO sanity check (?)

        // Get a writable database
        SQLiteDatabase db = taskDbHelper.getWritableDatabase();

        // Insert new task
        long newTaskId = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, contentValues);

        // Check whether the insertion was successful
        if(newTaskId == -1) {
            Log.e(LOG_TAG,  "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners (cursors) that the tasks table has changed
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the uri of the inserted task
        return ContentUris.withAppendedId(uri, newTaskId);
    }

    /**
     * Delete tasks matching the selection.
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return number of deleted rows
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Get a writable database
        SQLiteDatabase db = taskDbHelper.getWritableDatabase();

        // Track the number of deleted rows.
        int rowsDeleted;

        int uriCode = uriMatcher.match(uri);
        switch (uriCode) {
            case TASKS:
                // A selection of tasks should be deleted
                rowsDeleted = db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                // Delete a single task given by the id in the uri
                selection = TaskContract.TaskEntry._ID + " = ?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given uri has changed
        if(rowsDeleted > 0 ){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Update tasks with the given content values, matching the selection.
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return number of updated rows
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriCode = uriMatcher.match(uri);
        switch (uriCode) {
            case TASKS:
                // Update a selection of tasks
                return updateTask(uri, contentValues, selection, selectionArgs);
            case TASK_ID:
                // Update a single task given by the id in the uri
                selection = TaskContract.TaskEntry._ID + " = ?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateTask(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateTask(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }

        // TODO sanitycheck

        // Get a writable database
        SQLiteDatabase db = taskDbHelper.getWritableDatabase();

        // Track the number of updated rows.
        int rowsUpdated = db.update(TaskContract.TaskEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given uri has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
