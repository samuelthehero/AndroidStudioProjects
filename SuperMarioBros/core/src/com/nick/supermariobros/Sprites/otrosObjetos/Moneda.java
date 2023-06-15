package com.nick.supermariobros.Sprites.otrosObjetos;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.nick.supermariobros.escenas.Hud;
import com.nick.supermariobros.screens.PlayScreen;
import com.nick.supermariobros.Sprites.objetos.Champinion;
import com.nick.supermariobros.Sprites.objetos.ItemDef;
import com.nick.supermariobros.Sprites.Mario;
import com.nick.supermariobros.SuperMarioBros;

public class Moneda extends InteraccionObjetos{
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Moneda(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(SuperMarioBros.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(getCell().getTile().getId() == BLANK_COIN)
            SuperMarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        else {
            if(object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / SuperMarioBros.PPM),
                        Champinion.class));
                SuperMarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            }
            else
                SuperMarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
            getCell().setTile(tileSet.getTile(BLANK_COIN));
            Hud.addScore(100);
        }
    }
}