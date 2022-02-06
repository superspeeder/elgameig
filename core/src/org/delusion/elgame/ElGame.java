package org.delusion.elgame;

import com.badlogic.gdx.Game;
import org.delusion.elgame.screens.MainGameScreen;

/**
 *
 * Main class for the game
 * Home of many things! (mainly the to-do list)
 *
 * TODO: Base World Class
 * TODO: Chunks
 * TODO: Find a good cache library
 * TODO: Caching chunks for threaded generation
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 * TODO:
 *
 */

public class ElGame extends Game {

	private MainGameScreen mainGameScreen;

	@Override
	public void create () {
		mainGameScreen = new MainGameScreen(this);
		setScreen(mainGameScreen);
	}

	@Override
	public void dispose() {
		mainGameScreen.dispose();

	}
}
