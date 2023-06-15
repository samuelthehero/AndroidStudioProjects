package com.nick.supermariobros.personajes;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Personaje {

    public Vector2 posicion;
    public float velocidadY;
    public Rectangle rect;
    public int vidas;
    public TextureRegion frameActual;

    public Personaje(float x, float y, int vidas, TextureRegion frameInicial) {

        posicion = new Vector2(x, y);
        frameActual = frameInicial;
        rect = new Rectangle(x, y, frameInicial.getRegionWidth(), frameInicial.getRegionHeight());
        this.vidas = vidas;
    }

    public void render(Batch batch) {

        batch.draw(frameActual, posicion.x, posicion.y);
    }

    public void update(float dt) {

        // Simula el efecto de la gravedad
        velocidadY -= 10 * dt;
        if (velocidadY < -5) {
            velocidadY = -5;
        }

        // Aplica la gravedad sobre el eje y al jugador
        posicion.y += velocidadY;
        rect.y = posicion.y;
    }
}
