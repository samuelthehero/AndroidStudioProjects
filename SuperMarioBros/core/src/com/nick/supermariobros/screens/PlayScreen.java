package com.nick.supermariobros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nick.supermariobros.escenas.Hud;
import com.nick.supermariobros.Sprites.enemigos.Enemigos;
import com.nick.supermariobros.Sprites.objetos.Champinion;
import com.nick.supermariobros.Sprites.objetos.Item;
import com.nick.supermariobros.Sprites.objetos.ItemDef;
import com.nick.supermariobros.Sprites.Mario;
import com.nick.supermariobros.SuperMarioBros;
import com.nick.supermariobros.creator.CreadorMundo2D;
import com.nick.supermariobros.creator.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    //Referencia al juego, usado para iniciar la pantalla
    private SuperMarioBros game;
    private TextureAtlas atlas;
    public static boolean alreadyDestroyed = false;

    //basic playscreen variables
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    //Tiled map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private CreadorMundo2D creator;

    //sprites
    private Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;


    public PlayScreen(SuperMarioBros game){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
        //esto crea una cámara utilizada para seguir a mario a través del mundo de cámaras
        gamecam = new OrthographicCamera();

        //FitViewport para mantener la relación de aspecto virtual independientemente del tamaño de la pantalla
        gamePort = new FitViewport(SuperMarioBros.V_WIDTH / SuperMarioBros.PPM, SuperMarioBros.V_HEIGHT / SuperMarioBros.PPM, gamecam);

        //esto crea el juego HUD para puntajes/cronómetros/información de nivel
        hud = new Hud(game.batch);

        //Carga el mapa y configura el renderizador de mapas
        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1  / SuperMarioBros.PPM);

        //Inicialmente configura la gamecam para que se centre correctamente al comienzo del mapa.
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //crea el mundo Box2D, no establecer gravedad en X, -10 gravedad en Y, y permitir que los cuerpos se paren
        world = new World(new Vector2(0, -10), true);
        //allows for debug lines of our box2d world.
        b2dr = new Box2DDebugRenderer();

        creator = new CreadorMundo2D(this);

        //Crea a mario en el mundo
        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = SuperMarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }


    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Champinion.class){
                items.add(new Champinion(this, idef.position.x, idef.position.y));
            }
        }
    }


    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {


    }

    public void handleInput(float dt){
        //controla al jugador mediante impulsos inmediatos
        if(player.currentState != Mario.State.DEAD) {
            if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.UP))
                player.jump();
            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                player.Fuego();
        }

    }

    public void update(float dt){
        //maneja la entrada del usuario primero
        handleInput(dt);
        handleSpawningItems();

        //toma 1 paso en la simulación física (60 veces por segundo)
        world.step(1 / 60f, 6, 2);

        player.update(dt);
        for(Enemigos enemigos : creator.getEnemies()) {
            enemigos.update(dt);
            if(enemigos.getX() < player.getX() + 224 / SuperMarioBros.PPM) {
                enemigos.b2body.setActive(true);
            }
        }

        for(Item item : items)
            item.update(dt);

        hud.update(dt);

        //adjunta ala cámara de juego a la coordenada del jugador
        if(player.currentState != Mario.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
        }

        //actualiza la cámara de juego con las coordenadas correctas después de los cambios
        gamecam.update();
        //le dice al renderizador que dibuje solo lo que la cámara puede ver en nuestro mundo de juego.
        renderer.setView(gamecam);

    }


    @Override
    public void render(float delta) {
        //separar la lógica de actualización de renderizar
        update(delta);

        //Limpia la pantalla del juego con el color negro
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //renderiza el mapa del juego
        renderer.render();

        //renderiza el Box2DDebugLines
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemigos enemigos : creator.getEnemies())
            enemigos.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);
        game.batch.end();

        //Configura el lote para dibujar ahora lo que ve la cámara Hud.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()){
            game.setScreen(new GameOver(game));
            dispose();
        }

    }

    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        //actualiza la vista del juego
        gamePort.update(width,height);

    }

    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        //dispone de todos los recursos abiertos
        atlas.dispose();
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public Hud getHud(){ return hud; }
}