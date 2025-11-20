package com.example.waterfallflow;

public class ProfileOption {
    public static final int TYPE_SECTION = 0;
    public static final int TYPE_OPTION = 1;

    private int type;
    private String title;
    private String sectionTitle;

    public ProfileOption(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public ProfileOption(int type, String title, String sectionTitle) {
        this.type = type;
        this.title = title;
        this.sectionTitle = sectionTitle;
    }

    // Getters
    public int getType() { return type; }
    public String getTitle() { return title; }
    public String getSectionTitle() { return sectionTitle; }
}