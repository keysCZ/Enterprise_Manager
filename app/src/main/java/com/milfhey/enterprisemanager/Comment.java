package com.milfhey.enterprisemanager;

public class Comment {
    private String id;
    private String userId;
    private String commentText;
    private long timestamp;

    public Comment() {
        // Constructeur vide nécessaire pour Firebase
    }

    public Comment(String id, String userId, String commentText, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

