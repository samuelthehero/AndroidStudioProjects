package com.nick.supermariobros.managers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class ConfigurationManager {

    private static Preferences prefs = Gdx.app.getPreferences("Super Mario Bros");

    public static boolean haySonido() {
        return prefs.getBoolean("sonido");
    }

    public static String getDificultad() {
        return prefs.getString("dificultad");
    }
}
