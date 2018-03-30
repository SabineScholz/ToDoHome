package com.example.android.todohome;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.android.todohome.model.TaskList;

class RecyclerAdapter extends RecyclerView.Adapter {

    private TaskList originalData = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView titleTextView;

        public ViewHolder(View itemView, CheckBox checkBox, TextView titleTextView) {
            super(itemView);
            this.checkBox = checkBox;
            this.titleTextView = titleTextView;
        }
    }


    public RecyclerAdapter(TaskList originalTaskList) {
        this.originalData = originalTaskList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
