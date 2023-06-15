package com.nick.supermariobros.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.io.File;


public class R {

    private static String TEXTURE_ATLAS = "characters.pack";
    // Clase de libGDX que permite la gestión de assets
    private static AssetManager assets = new AssetManager();

    /**
     * Carga todos los recursos del juego
     */
    public static void cargarTodo() {
        assets.load(TEXTURE_ATLAS, TextureAtlas.class);

        cargarSonidos();
        cargarMusicas();
    }

    public static void finalizarCarga() {
        assets.finishLoading();
    }

    /** Actualiza la carga de recursos */
    public static boolean update() {
        return assets.update();
    }

    /**
     * Carga los sonidos
     */
    public static void cargarSonidos() {
        assets.load("sounds/bump.wav", Sound.class);

    }

    /**
     * Carga las músicas
     */
    public static void cargarMusicas() {
    }

    /**
     * Obtiene una región de textura o la primera de una animación
     * @param nombre
     * @return
     */
    public static TextureRegion getTextura(String nombre) {

        return assets.get(TEXTURE_ATLAS, TextureAtlas.class).findRegion(nombre);
    }

    /**
     * Obtiene una región de textura determinada de las que forman una animación
     */
    public static TextureRegion getTextura(String nombre, int position) {

        return assets.get(TEXTURE_ATLAS, TextureAtlas.class).findRegion(nombre, position);
    }

    /**
     * Obtiene todas las regiones de textura que forman una misma animación
     * @param nombre
     * @return
     */
    public static Array<TextureAtlas.AtlasRegion> getTexturas(String nombre) {

        return assets.get(TEXTURE_ATLAS, TextureAtlas.class).findRegions(nombre);
    }

    /**
     * Obtiene un sonido determinado
     */
    public static Sound getSonido(String nombre) {
        return assets.get("sounds/" + nombre, Sound.class);
    }

    /**
     * Obtiene una música determinada
     */
    public static Music getMusica(String nombre) {
        return assets.get(nombre, Music.class);
    }
}
