package com.nick.supermariobros.Sprites.enemigos;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.nick.supermariobros.screens.PlayScreen;
import com.nick.supermariobros.Sprites.Mario;
import com.nick.supermariobros.SuperMarioBros;

public class Goomba extends Enemigos{
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    float angle;


    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        walkAnimation = new Animation(0.4f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 16 / SuperMarioBros.PPM, 16 / SuperMarioBros.PPM);
        setToDestroy = false;
        destroyed = false;
        angle = 0;
    }

    public void update(float dt){
        stateTime += dt;
        if(setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTime = 0;
        }
        else if(!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / SuperMarioBros.PPM);
        fdef.filter.categoryBits = SuperMarioBros.ENEMY_BIT;
        fdef.filter.maskBits = SuperMarioBros.GROUND_BIT |
                SuperMarioBros.COIN_BIT |
                SuperMarioBros.BRICK_BIT |
                SuperMarioBros.ENEMY_BIT |
                SuperMarioBros.OBJECT_BIT |
                SuperMarioBros.MARIO_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //Create the Head here:
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / SuperMarioBros.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / SuperMarioBros.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / SuperMarioBros.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / SuperMarioBros.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = SuperMarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);

    }

    public void draw(Batch batch){
        if(!destroyed || stateTime < 1)
            super.draw(batch);
    }



    @Override
    public void hitOnHead(Mario mario) {
        setToDestroy = true;
        SuperMarioBros.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    @Override
    public void hitByEnemy(Enemigos enemigos) {
        if(enemigos instanceof Koopa && ((Koopa) enemigos).currentState == Koopa.State.MOVING_SHELL)
            setToDestroy = true;
        else
            reverseVelocity(true, false);
    }
}
