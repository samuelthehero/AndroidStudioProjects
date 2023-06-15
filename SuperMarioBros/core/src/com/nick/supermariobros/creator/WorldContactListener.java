package com.nick.supermariobros.creator;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.nick.supermariobros.Sprites.enemigos.Enemigos;
import com.nick.supermariobros.Sprites.objetos.Item;
import com.nick.supermariobros.Sprites.Mario;
import com.nick.supermariobros.Sprites.elementos.BolaDeFuego;
import com.nick.supermariobros.Sprites.otrosObjetos.InteraccionObjetos;
import com.nick.supermariobros.SuperMarioBros;

public class WorldContactListener implements ContactListener{
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case SuperMarioBros.MARIO_HEAD_BIT | SuperMarioBros.BRICK_BIT:
            case SuperMarioBros.MARIO_HEAD_BIT | SuperMarioBros.COIN_BIT:
                if(fixA.getFilterData().categoryBits == SuperMarioBros.MARIO_HEAD_BIT)
                    ((InteraccionObjetos) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    ((InteraccionObjetos) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
            case SuperMarioBros.ENEMY_HEAD_BIT | SuperMarioBros.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == SuperMarioBros.ENEMY_HEAD_BIT)
                    ((Enemigos)fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                else
                    ((Enemigos)fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                break;
            case SuperMarioBros.ENEMY_BIT | SuperMarioBros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == SuperMarioBros.ENEMY_BIT)
                    ((Enemigos)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemigos)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case SuperMarioBros.MARIO_BIT | SuperMarioBros.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == SuperMarioBros.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit((Enemigos) fixB.getUserData());
                else
                    ((Mario) fixB.getUserData()).hit((Enemigos) fixA.getUserData());
                break;
            case SuperMarioBros.ENEMY_BIT | SuperMarioBros.ENEMY_BIT:
                ((Enemigos)fixA.getUserData()).hitByEnemy((Enemigos)fixB.getUserData());
                ((Enemigos)fixB.getUserData()).hitByEnemy((Enemigos)fixA.getUserData());
                break;
            case SuperMarioBros.ITEM_BIT | SuperMarioBros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == SuperMarioBros.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case SuperMarioBros.ITEM_BIT | SuperMarioBros.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == SuperMarioBros.ITEM_BIT)
                    ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                break;
            case SuperMarioBros.FIREBALL_BIT | SuperMarioBros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == SuperMarioBros.FIREBALL_BIT)
                    ((BolaDeFuego)fixA.getUserData()).setToDestroy();
                else
                    ((BolaDeFuego)fixB.getUserData()).setToDestroy();
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}