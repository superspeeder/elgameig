package org.delusion.elgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.delusion.elgame.inventory.Item;
import org.delusion.elgame.inventory.Stack;
import org.delusion.elgame.menu.Slot;
import org.delusion.elgame.player.Player;
import org.delusion.elgame.screens.InventoryScreen;
import org.delusion.elgame.screens.MainGameScreen;
import org.delusion.elgame.screens.MainMenuScreen;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.world.World;

/**
 *
 * Main class for the game
 */

public class ElGame extends Game {

	private MainGameScreen mainGameScreen;
	private Player player;
	private World world;
	private MainMenuScreen mainMenuScreen;
	private InventoryScreen inventoryScreen;
	private SpriteBatch uibatch;


	@Override
	public void create () {
		TileType.load();
		Slot.load();
		Item.load();
		Stack.load();

		uibatch = new SpriteBatch();


		world = new World(this);
		player = new Player(world);

		mainGameScreen = new MainGameScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		inventoryScreen = new InventoryScreen(this);

		setScreen(mainMenuScreen);
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

	public MainMenuScreen getMainMenuScreen() {
		return mainMenuScreen;
	}

	public SpriteBatch getUIBatch() {
		return uibatch;
	}

	public InventoryScreen getInventoryScreen() {
		return inventoryScreen;
	}
}
