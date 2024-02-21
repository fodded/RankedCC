package me.fodded.spigotcore.gameplay.gui;

public class GuiSettingsManager {

    private static GuiSettingsManager instance;
    public GuiSettingsManager() {
        instance = this;
    }

    public void registerSettings() {

    }

    public static GuiSettingsManager getInstance() {
        if(instance == null) {
            return new GuiSettingsManager();
        }
        return instance;
    }
}
