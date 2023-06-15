package com.nick.supermariobros.creator;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.nick.supermariobros.screens.PlayScreen;
import com.nick.supermariobros.Sprites.enemigos.Enemigos;
import com.nick.supermariobros.Sprites.enemigos.Goomba;
import com.nick.supermariobros.Sprites.enemigos.Koopa;
import com.nick.supermariobros.Sprites.otrosObjetos.Ladrillos;
import com.nick.supermariobros.Sprites.otrosObjetos.Moneda;
import com.nick.supermariobros.SuperMarioBros;

public class CreadorMundo2D {
    private Array<Goomba> goombas;
    private Array<Koopa> koopas;

    public CreadorMundo2D(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        //create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / SuperMarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / SuperMarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / SuperMarioBros.PPM, rect.getHeight() / 2 / SuperMarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //create pipe bodies/fixtures
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / SuperMarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / SuperMarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / SuperMarioBros.PPM, rect.getHeight() / 2 / SuperMarioBros.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = SuperMarioBros.OBJECT_BIT;
            body.createFixture(fdef);
        }

        //create brick bodies/fixtures
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Ladrillos(screen, object);
        }

        //create coin bodies/fixtures
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){

            new Moneda(screen, object);
        }

        //create all goombas
        goombas = new Array<Goomba>();
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, rect.getX() / SuperMarioBros.PPM, rect.getY() / SuperMarioBros.PPM));
        }
        koopas = new Array<Koopa>();
        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            koopas.add(new Koopa(screen, rect.getX() / SuperMarioBros.PPM, rect.getY() / SuperMarioBros.PPM));
        }
    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }
    public Array<Enemigos> getEnemies(){
        Array<Enemigos> enemies = new Array<Enemigos>();
        enemies.addAll(goombas);
        enemies.addAll(koopas);
        return enemies;
    }
}
