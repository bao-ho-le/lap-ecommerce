package com.ptithcm.frontend.models;

public class ProfileOption {

    private final int iconResId;
    private final String title;
    private final String subtitle;

    public ProfileOption(int iconResId, String title, String subtitle) {
        this.iconResId = iconResId;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}