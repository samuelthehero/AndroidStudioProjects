package com.nick.supermariobros;

import com.badlogic.gdx.Game;
import com.nick.supermariobros.managers.R;
import com.nick.supermariobros.screens.GameScreen;
import com.nick.supermariobros.screens.MainMenuScreen;

public class SuperMarioBros extends Game {
	
	@Override
	public void create () {

		R.cargarTodo();
		R.finalizarCarga();
		setScreen(new MainMenuScreen());
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
	}
}
