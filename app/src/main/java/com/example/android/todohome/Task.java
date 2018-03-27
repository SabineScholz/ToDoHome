package com.example.android.todohome;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Sabine on 27.03.2018.
 */

public class Task {

    private String title;
    private String description;
    private Date creationDate;
    private Date dueDate;
    private boolean done;


    public Task(String title, String description, boolean done) {
        this.title = title;
        this.description = description;
        this.creationDate = GregorianCalendar.getInstance().getTime();
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
