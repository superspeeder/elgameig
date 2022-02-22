package org.delusion.elgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import org.delusion.elgame.item.Item;
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

	private FrameBuffer screenFramebuffer;
	private SpriteBatch screenBatch;
	private Settings settings = new Settings();

	@Override
	public void create () {
		TileType.load();
		Slot.load();
		Item.load();
		Stack.load();

		uibatch = new SpriteBatch();

		screenFramebuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		screenFramebuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		screenBatch = new SpriteBatch();

		world = new World(this);
		player = new Player(world);

		mainGameScreen = new MainGameScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		inventoryScreen = new InventoryScreen(this);

		setScreen(mainMenuScreen);
	}

	@Override
	public void render() {
		screenFramebuffer.begin();
		super.render();
		screenFramebuffer.end();

		screenBatch.begin();
		screenBatch.draw(screenFramebuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
		screenBatch.end();
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

	public Settings getSettings() {
		return settings;
	}
}
