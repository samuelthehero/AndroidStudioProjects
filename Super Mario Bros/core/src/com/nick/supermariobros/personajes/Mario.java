package com.nick.supermariobros.personajes;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nick.supermariobros.managers.R;

public class Mario extends Personaje {

    public enum Estado {
        DERECHA, IZQUIERDA, QUIETO, SALTO
    }

    private float tiempo;
    public boolean saltando;
    public int monedas;
    private Animation<TextureRegion> animacionDerecha, animacionIzquierda;
    public Estado estado;
    public boolean reposo;

    public Mario(float x, float y, int vidas, TextureRegion frameInicial) {
        super(x, y, vidas, frameInicial);

        saltando = false;
        monedas = 0;

        animacionDerecha = new Animation<TextureRegion>(0.15f, R.getTexturas("mario_walk_right"));
        animacionIzquierda = new Animation<TextureRegion>(0.15f, R.getTexturas("mario_walk_left"));

        estado = Estado.QUIETO;
    }

    @Override
    public void update(float dt) {

        tiempo += dt;

        switch(estado) {
            case SALTO:
                frameActual = R.getTextura("mario_jump_right");
                break;
            case DERECHA:
                frameActual = animacionDerecha.getKeyFrame(tiempo, true);
                break;
            case IZQUIERDA:
                frameActual = animacionIzquierda.getKeyFrame(tiempo, true);
                break;
            case QUIETO:
                frameActual = R.getTextura("mario_idle_right");
                break;
            default:
                frameActual = R.getTextura("mario_idle_right");
        }

        super.update(dt);
    }

    /**
     * Desplaza al jugador sobre el eje X
     * @param x
     */
    public void desplazar(float x) {

        if (x > 0)
            estado = Estado.DERECHA;
        else
            estado = Estado.IZQUIERDA;

        posicion.x += x;
        rect.x = posicion.x;
    }

    /**
     * Aplica fuerza de salto al jugador
     */
    public void saltar() {

        saltando = true;
        velocidadY = 5;
    }
}
