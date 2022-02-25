package org.delusion.elgame;

import org.delusion.elgame.data.DataManager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Settings {
    public boolean advancedLightCascade = true;

    public void save() {
        DataManager.writeTextData("settings.json", ElGame.INSTANCE.gson.toJson(this));
    }

    public static Settings load() {
        try (FileReader fr = DataManager.fileReader("settings.json")){
            if (fr != null) {
                return ElGame.INSTANCE.gson.fromJson(fr, Settings.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Settings settings = new Settings();
        settings.save();
        return settings;
    }
}