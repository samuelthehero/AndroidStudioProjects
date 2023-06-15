package com.nick.supermariobros.Sprites.objetos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.nick.supermariobros.screens.PlayScreen;
import com.nick.supermariobros.Sprites.Mario;
import com.nick.supermariobros.SuperMarioBros;

public class Champinion extends Item{
    public Champinion(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f, 0);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / SuperMarioBros.PPM);
        fdef.filter.categoryBits = SuperMarioBros.ITEM_BIT;
        fdef.filter.maskBits = SuperMarioBros.MARIO_BIT |
                SuperMarioBros.OBJECT_BIT |
                SuperMarioBros.GROUND_BIT |
                SuperMarioBros.COIN_BIT |
                SuperMarioBros.BRICK_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(Mario mario) {
        destroy();
        mario.grow();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}