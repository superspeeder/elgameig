package org.delusion.elgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import org.delusion.elgame.player.Player;
import org.delusion.elgame.screens.MainGameScreen;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.world.World;

/**
 *
 * Main class for the game
 * Home of many things! (mainly the to-do list)
 *
 * DONE: Base World Class
 * DONE: Chunks
 * DONE: Find a good cache library
 * DONE: Caching chunks for threaded generation
 * TODO: Player Sprite Rendering
 * TODO: Player Physics
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
	private Player player;
	private World world;


	@Override
	public void create () {
		TileType.load();
		world = new World(this);
		player = new Player(world);

		mainGameScreen = new MainGameScreen(this);
		setScreen(mainGameScreen);
	}

	@Override
	public void dispose() {
		mainGameScreen.dispose();
	}

	public MainGameScreen getMainGameScreen() {
		return mainGameScreen;
	}

	public Player getPlayer() {
		return player;
	}

	public World getWorld() {
		return world;
	}
}
