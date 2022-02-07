package org.delusion.elgame.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.delusion.elgame.utils.SimpleRenderable;
import org.delusion.elgame.world.World;

public class Player implements SimpleRenderable {

    private static final float SPEED = 400;
    private OrthographicCamera camera;
    private Vector2 position, velocity, acceleration;
    private Sprite internalSprite;

    public Player(World world) {
        position = new Vector2(0,0);
        camera = new OrthographicCamera(1920, 1080);
        camera.setToOrtho(false);
        world.linkToPlayer(this); // dual dependency handshake
    }

    @Override
    public void render() {
    }

    public Vector2 getPosition() {
        return position;
    }

    public Camera getCamera() {
        return camera;
    }

    public void update(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x = -SPEED * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x = -SPEED * dt;
        }


        position.x += velocity.x;



        position.y += dpos.y;

        camera.position.set(position, camera.position.z);
        camera.update();
    }
}
