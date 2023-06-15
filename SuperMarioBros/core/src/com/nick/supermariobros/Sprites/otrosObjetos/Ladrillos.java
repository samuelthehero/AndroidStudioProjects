package com.nick.supermariobros.Sprites.otrosObjetos;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.nick.supermariobros.escenas.Hud;
import com.nick.supermariobros.screens.PlayScreen;
import com.nick.supermariobros.Sprites.Mario;
import com.nick.supermariobros.SuperMarioBros;

public class Ladrillos extends InteraccionObjetos{
    public Ladrillos(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(SuperMarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()) {
            setCategoryFilter(SuperMarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            SuperMarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        SuperMarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }

}