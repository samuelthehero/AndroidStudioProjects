package com.nick.supermariobros.Sprites.otrosObjetos;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.nick.supermariobros.screens.PlayScreen;
import com.nick.supermariobros.Sprites.Mario;
import com.nick.supermariobros.SuperMarioBros;

public abstract class InteraccionObjetos {
    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected PlayScreen screen;
    protected MapObject object;

    protected Fixture fixture;

    public InteraccionObjetos(PlayScreen screen, MapObject object){
        this.object = object;
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject) object).getRectangle();

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / SuperMarioBros.PPM, (bounds.getY() + bounds.getHeight() / 2) / SuperMarioBros.PPM);

        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth() / 2 / SuperMarioBros.PPM, bounds.getHeight() / 2 / SuperMarioBros.PPM);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);

    }

    public abstract void onHeadHit(Mario mario);
    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x * SuperMarioBros.PPM / 16),
                (int)(body.getPosition().y * SuperMarioBros.PPM / 16));
    }

}