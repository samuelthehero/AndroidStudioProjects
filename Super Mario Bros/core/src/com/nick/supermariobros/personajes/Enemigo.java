package com.nick.supermariobros.personajes;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nick.supermariobros.managers.ConfigurationManager;

public class Enemigo extends Personaje {

    private int velocidad;

    public Enemigo(float x, float y, int vidas, TextureRegion frameInicial) {
        super(x, y, vidas, frameInicial);

        velocidad = 15;
        if (ConfigurationManager.getDificultad().equals("Dificil"))
            velocidad *= 2;
    }

    @Override
    public void update(float dt) {
        posicion.x -= velocidad * dt;
        rect.x = posicion.x;

        super.update(dt);
    }
}
