package com.nick.supermariobros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.nick.supermariobros.managers.R;
import com.nick.supermariobros.personajes.Enemigo;
import com.nick.supermariobros.personajes.Mario;
import com.nick.supermariobros.managers.ConfigurationManager;

import static com.nick.supermariobros.utils.Constantes.ALTURA_EN_CELDAS;
import static com.nick.supermariobros.utils.Constantes.ANCHURA_CELDA;
import static com.nick.supermariobros.utils.Constantes.ANCHURA_EN_CELDAS;


/**
 * Pantalla de juego
 */
public class GameScreen implements Screen {

    Batch batch;
    OrthographicCamera camara;
    TiledMap mapa;
    OrthogonalTiledMapRenderer mapRenderer;
    Mario mario;
    Array<Enemigo> enemigos;
    BitmapFont fuente;

    @Override
    public void show() {

        camara = new OrthographicCamera();
        // Fija la anchura y altura de la camara en base al número de tiles que se mostrarán
        camara.setToOrtho(false, ANCHURA_EN_CELDAS * ANCHURA_CELDA, ALTURA_EN_CELDAS * ANCHURA_CELDA);
        camara.update();

        mapa = new TmxMapLoader().load("levels/level1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(mapa);
        batch = mapRenderer.getBatch();

        mapRenderer.setView(camara);

        mario = new Mario(0, 100, 3, R.getTextura("mario_idle_right"));
        cargarEnemigos();

        fuente = new BitmapFont();
    }

    @Override
    public void render(float dt) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapRenderer.render();

        controlarCamara();
        comprobarColisiones();

        batch.begin();
        mario.render(batch);
        for (Enemigo enemigo : enemigos)
            enemigo.render(batch);
        mostrarHud(batch);
        batch.end();

        comprobarTeclado(dt);
        mario.update(dt);
        for (Enemigo enemigo : enemigos)
            enemigo.update(dt);
    }

    /**
     * Muestra información del juego en pantalla (Head-up display)
     * @param batch
     */
    private void mostrarHud(Batch batch) {

        batch.draw(R.getTextura("mario_idle_right"), camara.position.x - ANCHURA_CELDA * ANCHURA_EN_CELDAS / 2 + 5, ANCHURA_CELDA * ALTURA_EN_CELDAS - 14, 0, 0, 10, 12, 1, 1, 0);
        fuente.getData().setScale(0.5f);
        fuente.draw(batch, "X " + mario.vidas, camara.position.x - ANCHURA_CELDA * ANCHURA_EN_CELDAS / 2 + 17, ANCHURA_CELDA * ALTURA_EN_CELDAS - 5);
    }

    /**
     * Comprueba las colisiones con los objetos que forman el suelo
     */
    private void comprobarColisiones() {

        // Comprueba colisiones con el suelo
        MapLayer capaSuelo = mapa.getLayers().get("objects");
        for (MapObject objetoMapa : capaSuelo.getObjects()) {
            if (objetoMapa.getProperties().containsKey("suelo")) {
                RectangleMapObject objectoRect = (RectangleMapObject) objetoMapa;
                Rectangle rect = objectoRect.getRectangle();
                // Comprueba si el jugador colisiona con algún objeto suelo
                if (mario.rect.overlaps(rect)) {
                    mario.posicion.y = rect.y + rect.getHeight();
                    mario.rect.y = mario.posicion.y;
                    mario.saltando = false;
                }
                // Comprueba si algún enemigo colisiona con algún objeto suelo
                for (Enemigo enemigo : enemigos) {
                    if (enemigo.rect.overlaps(rect)) {
                        enemigo.posicion.y = rect.y + rect.getHeight();
                        enemigo.rect.y = enemigo.posicion.y;
                    }
                }
            }
        }

        // Comprueba colisiones con los enemigos
        for (Enemigo enemigo : enemigos) {
            if (enemigo.rect.overlaps(mario.rect)) {
                // Si Mario está más alto mata al enemigo
                if (mario.posicion.y > enemigo.rect.y) {
                    enemigos.removeValue(enemigo, true);
                    if (ConfigurationManager.haySonido())
                        R.getSonido("bump.wav").play();
                    mario.saltar();
                }
                else {
                    if (!mario.reposo) {
                        mario.vidas -= 1;
                        if (mario.vidas == 0) {
                            // TODO Fin de partida
                        }
                        mario.reposo = true;
                        Timer.schedule(new Timer.Task() {

                            @Override
                            public void run() {
                                mario.reposo = false;
                            }
                        }, 0.5f);
                    }
                }
            }
        }
    }

    /**
     * Comprueba la entrada de teclado
     * @param dt
     */
    private void comprobarTeclado(float dt) {

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mario.desplazar(50 * dt);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mario.desplazar(-50 * dt);
        }
        else
            mario.estado = Mario.Estado.QUIETO;

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (!mario.saltando) {
                mario.saltar();
            }
        }
    }

    private void cargarEnemigos() {

        enemigos = new Array<Enemigo>();

        MapLayer capaObjetos = mapa.getLayers().get("objects");
        for (MapObject objetoMapa : capaObjetos.getObjects()) {
            if (objetoMapa.getProperties().containsKey("enemy")) {
                if (objetoMapa instanceof TiledMapTileMapObject) {
                    TiledMapTileMapObject objetoRect = (TiledMapTileMapObject) objetoMapa;
                    Enemigo enemigo = new Enemigo(objetoRect.getX(), objetoRect.getY(), 1, R.getTextura("enemy"));
                    enemigos.add(enemigo);
                }
            }
        }
    }

    /**
     * Controla la cámara para que siempre enfoque al jugador
     */
    private void controlarCamara() {

    }

    @Override
    public void resize(int width, int height) {

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

    }
}
