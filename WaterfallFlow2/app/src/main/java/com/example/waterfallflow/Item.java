package com.example.waterfallflow;

public class Item {
    private int imageResId;
    private String title;
    private String description;
    private int height; // 用于模拟不同高度的项

    public Item(int imageResId, String title, String description, int height) {
        this.imageResId = imageResId;
        this.title = title;
        this.description = description;
        this.height = height;
    }

    // Getters
    public int getImageResId() { return imageResId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getHeight() { return height; }
}