package com.nick.supermariobros.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.nick.supermariobros.screens.PlayScreen;
import com.nick.supermariobros.Sprites.enemigos.Enemigos;
import com.nick.supermariobros.Sprites.enemigos.Koopa;
import com.nick.supermariobros.Sprites.elementos.BolaDeFuego;
import com.nick.supermariobros.SuperMarioBros;

public class Mario extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD };
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;
    private Texture texture;

    private TextureRegion marioStand;
    private Animation marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation bigMarioRun;
    private Animation growMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;
    private PlayScreen screen;

    private Array<BolaDeFuego> bolaDeFuego;

    public Mario(PlayScreen screen){
        //Inicializa los valores por defecto
        this.screen = screen;
        screen.getAtlas();
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // Arranca los frames animados y los añade a la animacion de mario
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        marioRun = new Animation(0.1f, frames);

        frames.clear();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        bigMarioRun = new Animation(0.1f, frames);

        frames.clear();

        //obtiene los marcos de animación establecidos de mario en crecimiento
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);


        //obtiene fotogramas de animación de salto y los agrega a marioJump Animation
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        //crea una región de textura para mario de pie
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        //crear región de textura de mario muerto
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        //define mario en Box2d
        defineMario();

        //establece los valores iniciales para la ubicación, el ancho y la altura de mario y el marco inicial como marioStand.
        setBounds(0, 0, 16 / SuperMarioBros.PPM, 16 / SuperMarioBros.PPM);
        setRegion(marioStand);

        bolaDeFuego = new Array<BolaDeFuego>();

    }

    public void update(float dt){

        if (screen.getHud().isTimeUp() && !isDead()) {
            die();
        }

        //actualiza los sprite correspondientes con la posicion del cuerpo del Box2D
        if(marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / SuperMarioBros.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        //actualiza los sprite con el frame correcto dependiendo de la accion actual de matio
        setRegion(getFrame(dt));
        if(timeToDefineBigMario)
            defineBigMario();
        if(timeToRedefineMario)
            redefineMario();

        for(BolaDeFuego  ball : bolaDeFuego) {
            ball.update(dt);
            if(ball.isDestroyed())
                bolaDeFuego.removeValue(ball, true);
        }

    }

    public TextureRegion getFrame(float dt){
        //obtiene el estado actual de mario. es decir. saltar, correr, pararse...
        currentState = getState();

        TextureRegion region;

        //dependiendo del estado, se obtiene el fotograma clave de animación correspondiente.
        switch(currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = (TextureRegion) growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = (TextureRegion) (marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true));
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }

        //si mario corre hacia la izquierda y la textura no mira hacia la izquierda... le da la vuelta.
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }

        //si mario está corriendo bien y la textura no está mirando hacia la derecha... le da la vuelta.
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        //si el estado actual es el mismo que el estado anterior, se aumenta el temporizador de estado.
        //de lo contrario, el estado cambia y se necesita restablecer el temporizador.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        //actualiza estado anterior
        previousState = currentState;
        //devuelve el marco ajustado final
        return region;

    }

    public State getState(){
        //Test de velocidad para Box2D en los puntos X e Y-Axis
        //si mario es positivo en el eje Y, está saltando... o si acaba de saltar y está cayendo, permanece en estado de salto
        if(marioIsDead)
            return State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
            //if negative in Y-Axis mario is falling
        else if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
            //if mario is positive or negative in the X axis he is running
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
            //if none of these return then he must be standing
        else
            return State.STANDING;
    }

    public void grow(){
        if( !isBig() ) {
            runGrowAnimation = true;
            marioIsBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
            SuperMarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();
        }
    }

    public void die() {

        if (!isDead()) {

            SuperMarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            SuperMarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            marioIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = SuperMarioBros.NOTHING_BIT;

            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }

            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }

    public boolean isDead(){
        return marioIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public boolean isBig(){
        return marioIsBig;
    }

    public void jump(){
        if ( currentState != State.JUMPING ) {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
//            setPosition((b2body.getPosition().x - 0.5f) * com.nick.supermariobros.escenas.Constants.PIXELS_IN_METER,
//            (b2body.getPosition().y - 0.5f) * com.nick.supermariobros.escenas.Constants.PIXELS_IN_METER);
//            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
            currentState = State.JUMPING;
        }
    }

    public void hit(Enemigos enemigos){
        if(enemigos instanceof Koopa && ((Koopa) enemigos).currentState == Koopa.State.STANDING_SHELL)
            ((Koopa) enemigos).kick(enemigos.b2body.getPosition().x > b2body.getPosition().x ? Koopa.KICK_RIGHT : Koopa.KICK_LEFT);
        else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                SuperMarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                die();
            }
        }
    }

    public void redefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / SuperMarioBros.PPM);
        fdef.filter.categoryBits = SuperMarioBros.MARIO_BIT;
        fdef.filter.maskBits = SuperMarioBros.GROUND_BIT |
                SuperMarioBros.COIN_BIT |
                SuperMarioBros.BRICK_BIT |
                SuperMarioBros.ENEMY_BIT |
                SuperMarioBros.OBJECT_BIT |
                SuperMarioBros.ENEMY_HEAD_BIT |
                SuperMarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / SuperMarioBros.PPM, 6 / SuperMarioBros.PPM), new Vector2(2 / SuperMarioBros.PPM, 6 / SuperMarioBros.PPM));
        fdef.filter.categoryBits = SuperMarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineMario = false;

    }

    public void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / SuperMarioBros.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / SuperMarioBros.PPM);
        fdef.filter.categoryBits = SuperMarioBros.MARIO_BIT;
        fdef.filter.maskBits = SuperMarioBros.GROUND_BIT |
                SuperMarioBros.COIN_BIT |
                SuperMarioBros.BRICK_BIT |
                SuperMarioBros.ENEMY_BIT |
                SuperMarioBros.OBJECT_BIT |
                SuperMarioBros.ENEMY_HEAD_BIT |
                SuperMarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / SuperMarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / SuperMarioBros.PPM, 6 / SuperMarioBros.PPM), new Vector2(2 / SuperMarioBros.PPM, 6 / SuperMarioBros.PPM));
        fdef.filter.categoryBits = SuperMarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / SuperMarioBros.PPM, 32 / SuperMarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / SuperMarioBros.PPM);
        fdef.filter.categoryBits = SuperMarioBros.MARIO_BIT;
        fdef.filter.maskBits = SuperMarioBros.GROUND_BIT |
                SuperMarioBros.COIN_BIT |
                SuperMarioBros.BRICK_BIT |
                SuperMarioBros.ENEMY_BIT |
                SuperMarioBros.OBJECT_BIT |
                SuperMarioBros.ENEMY_HEAD_BIT |
                SuperMarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / SuperMarioBros.PPM, 6 / SuperMarioBros.PPM), new Vector2(2 / SuperMarioBros.PPM, 6 / SuperMarioBros.PPM));
        fdef.filter.categoryBits = SuperMarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public void Fuego(){
        bolaDeFuego.add(new BolaDeFuego(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false));
    }

    public void draw(Batch batch){
        super.draw(batch);
        for(BolaDeFuego ball : bolaDeFuego)
            ball.draw(batch);
    }
}