package com.example.android.todohome.model;

import android.util.Log;

import com.example.android.todohome.MainActivity;

import java.util.ArrayList;

public class TaskList extends ArrayList<Task> {

    private static final String LOG_TAG = TaskList.class.getSimpleName() + " TEST";

    /**
     * Returns the task with a given id.
     *
     * @param id
     * @return
     */
    public Task findTaskById(int id) {
        for (Task task : this) {
            if (task.getId() == id) return task;
        }
        return null;
    }

    public void update(Task task) {
        // Replace the old task with the new task (keeping the position within the list the same)
        Task oldTask = findTaskById(task.getId());
        int index = indexOf(oldTask);
        set(index, task);
    }

    public TaskList getUnfinishedTasks() {
        Log.d(LOG_TAG, "getUnfinishedTasks");
        TaskList unfinishedTasks = new TaskList();
        for(Task task : this) {
//            Log.d(LOG_TAG, "isDone: " + task.isDone());
            if(!task.isDone()) {
                unfinishedTasks.add(task);
//                Log.d(LOG_TAG, "add unfinished task to list");
            }
        }
        return unfinishedTasks;
    }

    public void deleteFinishedTasks() {
        for(Task task : this){
            if(task.isDone()) {
                remove(task);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Log.d("", "TaskList");
        for(Task task : this) {
            stringBuilder.append(task.toString() + "\n");
        }
        return stringBuilder.toString();
    }
}
