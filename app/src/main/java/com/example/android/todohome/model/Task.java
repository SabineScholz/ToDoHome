package com.example.android.todohome.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Sabine on 27.03.2018.
 * based on the Task class obtained from Thorsten
 */

public class Task implements Parcelable {

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    // simple ID generator
    private static int MAX_ID = 0;

    private int id;
    private String name;
    private String description;
    private Date creationDate;
    private Date dueDate;
    private boolean done;


    public Task() {
        this.id = MAX_ID++;
    }

    public Task(String name) {
        this();
        this.name = name;
        this.creationDate = GregorianCalendar.getInstance().getTime();
    }

    public Task(String name, String description) {
        this(name);
        this.description = description;
    }


    public Task(String name, String description, boolean done) {
        this(name, description);
        this.done = done;
    }

    protected Task(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        long tmpMCreationDate = in.readLong();
        this.creationDate = tmpMCreationDate == -1 ? null : new Date(tmpMCreationDate);
        this.done = in.readByte() != 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Task) {
            return this.getId() == ((Task) obj).getId();
        }
        return false;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Title: " + name + ", Description: " + description + ", done: " + done;
    }

    /**
     * Implementation of Parcelable interface
     * (generated by Android Studio plugin "Android Parcelable code generator")
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeLong(this.creationDate != null ? this.creationDate.getTime() : -1);
        dest.writeByte(this.done ? (byte) 1 : (byte) 0);
    }
}
