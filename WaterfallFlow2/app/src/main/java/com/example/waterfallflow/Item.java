package com.example.waterfallflow;

public class Item {
    private int imageResId;
    private String title;
    private String description;
    private int height; // 用于模拟不同高度的项
    private boolean isFullWidth;

    public Item(int imageResId, String title, String description, int height, boolean isFullWidth) {
        this.imageResId = imageResId;
        this.title = title;
        this.description = description;
        this.height = height;
        this.isFullWidth = isFullWidth;
    }

    public Item(int imageResId, String title, String description, int height) {
        this(imageResId, title, description, height, false);
    }

    // Getters
    public int getImageResId() { return imageResId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getHeight() { return height; }
    public boolean isFullWidth() {
        return isFullWidth;
    }
}
