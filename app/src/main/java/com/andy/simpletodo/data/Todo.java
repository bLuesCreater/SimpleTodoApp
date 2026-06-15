package com.andy.simpletodo.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Todo {
    private long id;
    private String title;
    private String content;
    private long createdAt;
    private long updatedAt;
    private boolean completed;

    public Todo() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.completed = false;
    }

    public Todo(String title, String content) {
        this();
        this.title = title;
        this.content = content;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(updatedAt));
    }
}
