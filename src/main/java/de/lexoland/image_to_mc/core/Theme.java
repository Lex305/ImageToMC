package de.lexoland.image_to_mc.core;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.*;

import javax.swing.*;

public enum Theme {

    ONE_DARK("One Dark", FlatOneDarkIJTheme::setup),
    DARK_PURPLE("Dark purple", FlatDarkPurpleIJTheme::setup),
    GRAY("Gray", FlatGrayIJTheme::setup),
    SOLARIZED_LIGHT("Solarized Light", FlatSolarizedLightIJTheme::setup);

    private final String name;
    private final Runnable theme;

    Theme(String name, Runnable theme) {
        this.theme = theme;
        this.name = name;
    }

    public void setTheme() {
        theme.run();
        FlatLightLaf.updateUI();
    }

    @Override
    public String toString() {
        return name;
    }
}
