package com.example.waterfallflow;

public class Item {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_FULL_WIDTH = 1;
    private int type;
    private int imageResId;
    private int imageWidth;
    private int imageHeight;
    private String title;
    private String description;
    // private int height; // 用于模拟不同高度的项

    public Item(int type, int imageResId, String title, String description/*, int height*/) {
        this.type = type;
        this.imageResId = imageResId;
        this.title = title;
        this.description = description;
        // this.height = height;
    }

    public void setImageSize(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
    }

    // Getters
    public int getType() { return type; }
    public int getImageResId() { return imageResId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getImageWidth() { return imageWidth; }
    public int getImageHeight() { return imageHeight; }
    // public int getHeight() { return height; }

    public int calculateImageHeight(int targetWidth) {
        if (imageWidth <= 0 || imageHeight <= 0) {
            return targetWidth; // 默认正方形
        }
        return ImageUtils.calculateProportionalHeight(imageWidth, imageHeight, targetWidth);
    }

    public boolean isFullWidth() {
        return type == TYPE_FULL_WIDTH;
    }
}