package com.example.android.todohome.model;

import java.util.ArrayList;

public class TaskList extends ArrayList<Task> {

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
}
