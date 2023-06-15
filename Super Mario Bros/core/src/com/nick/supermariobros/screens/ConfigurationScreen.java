package com.nick.supermariobros.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

public class ConfigurationScreen implements Screen {

    private Stage stage;
    private Preferences prefs;
    private VisCheckBox checkSonido;
    private VisSelectBox<String> sbDificultad;

    public ConfigurationScreen() {
        cargarConfiguracion();
    }

    private void cargarConfiguracion() {
        prefs = Gdx.app.getPreferences("Plataformas");
    }

    private void grabarConfiguracion() {

        prefs.putBoolean("sonido", checkSonido.isChecked());
        prefs.putString("dificultad", sbDificultad.getSelected());
        prefs.flush();
    }

    @Override
    public void show() {

        if (!VisUI.isLoaded())
            VisUI.load();

        stage = new Stage();

        VisTable table = new VisTable(true);
        table.setFillParent(true);
        stage.addActor(table);

        checkSonido = new VisCheckBox("SONIDO");
        checkSonido.setChecked(prefs.getBoolean("sonido", false));

        sbDificultad = new VisSelectBox<String>();
        Array<String> items = new Array<String>();
        items.add("Fácil");
        items.add("Normal");
        items.add("Dificil");
        sbDificultad.setItems(items);
        sbDificultad.setSelected(prefs.getString("dificultad", "Normal"));

        VisTextButton quitButton = new VisTextButton("VOLVER");
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                grabarConfiguracion();
                dispose();
                ((Game) Gdx.app.getApplicationListener()).
                        setScreen(new MainMenuScreen());

            }
        });

        // Añade filas a la tabla y añade los componentes
        table.row();
        table.add(checkSonido).center().width(200).height(100).pad(5);
        table.row();
        table.add(sbDificultad).center().width(200).height(50).pad(5);
        table.row();
        table.add(quitButton).center().width(200).height(50).pad(5);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Pinta la UI en la pantalla
        stage.act(dt);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
        stage.dispose();
    }
}
