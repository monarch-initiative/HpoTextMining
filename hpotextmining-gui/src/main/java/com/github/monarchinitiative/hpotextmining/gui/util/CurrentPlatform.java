package com.github.monarchinitiative.hpotextmining.gui.util;

public enum CurrentPlatform {
    LINUX("Linux"),
    WINDOWS("Windows"),
    OSX("Os X"),
    UNKNOWN("Unknown");

    private String name;

    CurrentPlatform(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return this.name;
    }
}
