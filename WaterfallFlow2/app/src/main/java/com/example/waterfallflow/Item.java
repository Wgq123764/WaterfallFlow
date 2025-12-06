package com.example.waterfallflow;

public class Item {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_FULL_WIDTH = 1;
    private int type;
    private int imageResId;
    private String title;
    private String description;
    // private int height; // 用于模拟不同高度的项

    public Item(int type, int imageResId, String title, String description/*, int height*/) {
        this.type = type;
        this.imageResId = imageResId;
        this.title = title;
        this.description = description;
    }

    // Getters
    public int getType() { return type; }
    public int getImageResId() { return imageResId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public boolean isFullWidth() {
        return type == TYPE_FULL_WIDTH;
    }
}