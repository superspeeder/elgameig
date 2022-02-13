package org.delusion.elgame.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.utils.SimpleRenderable;
import org.delusion.elgame.world.World;

public class Player implements SimpleRenderable {

    private static final float SPEED = 450;
    private OrthographicCamera camera;
    private Vector2 position, velocity, acceleration;
    private Sprite internalSprite;
    private World world;
    private Texture box = new Texture(Gdx.files.internal("textures/selection_box.png"));
    private Texture boxg = new Texture(Gdx.files.internal("textures/selection_boxg.png"));
    private boolean grounded = false;
    private int framesSinceGrounded = 100000;
    private boolean jumpedSinceGrounded = false;

    public Player(World world) {
        this.world = world;
        position = new Vector2(0,64);
        velocity = new Vector2(0,0);
        acceleration = new Vector2(0,0);
        camera = new OrthographicCamera(1920, 1080);
        camera.setToOrtho(false);
        world.linkToPlayer(this); // dual dependency handshake
        internalSprite = new Sprite(new Texture(Gdx.files.internal("textures/player.png")));
        internalSprite.setSize(16, 24);
        internalSprite.setOrigin(0,0);
    }

    public Player setPosition(Vector2 position) {
        this.position = position;
        return this;
    }

    public Player setVelocity(Vector2 velocity) {
        this.velocity = velocity;
        return this;
    }

    public Player setAcceleration(Vector2 acceleration) {
        this.acceleration = acceleration;
        return this;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getAcceleration() {
        return acceleration;
    }

    public World getWorld() {
        return world;
    }

    public boolean isGrounded() {
        return grounded;
    }

    @Override
    public void render() {
        internalSprite.setPosition(position.x, position.y);
        internalSprite.draw(world.getEntityBatch());
    }

    public Vector2 getPosition() {
        return position;
    }

    public Camera getCamera() {
        return camera;
    }

    public void update(float dt) {
        grounded = false;
        velocity.x = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x = -SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x = SPEED;
        }

        position.x += velocity.x * dt;
        internalSprite.setPosition(position.x,position.y);

        checkCollisionsX();

        velocity.y -= 1100 * dt;
        position.y += velocity.y * dt;
        internalSprite.setPosition(position.x,position.y);
        checkCollisionsY();

        if (!grounded) framesSinceGrounded++;

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && framesSinceGrounded < 10 && !jumpedSinceGrounded) {
            velocity.y += 500;
            jumpedSinceGrounded = true;
        }

        camera.position.set(position, camera.position.z);
        camera.update();
    }

    private void checkCollisionsX() {
        Rectangle bbox = new Rectangle(position.x, position.y, internalSprite.getWidth(), internalSprite.getHeight());
        double left = Math.floor(bbox.getX() / World.TILE_SIZE);
        double right = Math.ceil((bbox.getX() + bbox.getWidth()) / World.TILE_SIZE);
        double top = Math.ceil((bbox.getY() + bbox.getHeight()) / World.TILE_SIZE);
        double bottom = Math.floor(bbox.getY() / World.TILE_SIZE);


        Rectangle intersection_blank = new Rectangle();

        if (velocity.x > 0) { // moving right
            float nearest = bbox.getX() + bbox.getWidth();
            boolean collided = false;

            for (int x = (int) left; x < right; x++) {
                for (int y = (int) bottom; y < top ; y++) {
                    TileType ttype = world.getTile(x,y).getNow(null);
                    if (ttype == null || ttype.getProperties().solid) {
                        // try collide
                        Rectangle trect = new Rectangle(x * World.TILE_SIZE, y * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
                        if (Intersector.intersectRectangles(bbox, trect, intersection_blank)) {
                            if (nearest > trect.getX()) {
                                nearest = trect.getX();
                                collided = true;
                            }
                        }
                    }
                }
            }

            if (collided) {
                position.x = nearest - bbox.getWidth();
                velocity.x = 0;
            }

        } else if (velocity.x < 0) { // moving left
            float nearest = bbox.getX();
            boolean collided = false;

            for (int x = (int) left; x < right ; x++) {
                for (int y = (int) bottom; y < top ; y++) {
                    TileType ttype = world.getTile(x,y).getNow(null);
                    if (ttype == null || ttype.getProperties().solid) {
                        // try collide
                        Rectangle trect = new Rectangle(x * World.TILE_SIZE, y * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
                        if (Intersector.intersectRectangles(bbox, trect, intersection_blank)) {
                            if (nearest < trect.getX() + trect.getWidth()) {
                                nearest = trect.getX() + trect.getWidth();
                                collided = true;
                            }
                        }
                    }
                }
            }

            if (collided) {
                position.x = nearest;
                velocity.x = 0;
            }
        }
    }

    private void checkCollisionsY() {
        Rectangle bbox = internalSprite.getBoundingRectangle();
        double left = Math.floor(bbox.getX() / World.TILE_SIZE);
        double right = Math.ceil((bbox.getX() + bbox.getWidth()) / World.TILE_SIZE);
        double top = Math.ceil((bbox.getY() + bbox.getHeight()) / World.TILE_SIZE);
        double bottom = Math.floor(bbox.getY() / World.TILE_SIZE);



        Rectangle intersection_blank = new Rectangle();

        if (velocity.y > 0) { // moving up
            float nearest = bbox.getY() + bbox.getHeight();
            boolean collided = false;

            for (int x = (int) left; x < right ; x++) {
                for (int y = (int) bottom; y < top ; y++) {
                    TileType ttype = world.getTile(x,y).getNow(null);
                    if (ttype == null || ttype.getProperties().solid) {
                        // try collide
                        Rectangle trect = new Rectangle(x * World.TILE_SIZE, y * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
                        if (Intersector.intersectRectangles(bbox, trect, intersection_blank)) {
                            if (nearest > trect.getY()) {
                                nearest = trect.getY();
                                collided = true;
                            }
                        }
                    }
                }
            }

            if (collided) {
                position.y = nearest - bbox.getHeight();
                velocity.y = -velocity.y * 0.667f;
            }

        } else if (velocity.y < 0) { // moving down
            float nearest = bbox.getY();
            boolean collided = false;

            for (int x = (int) left; x < right ; x++) {
                for (int y = (int) bottom; y < top ; y++) {
                    TileType ttype = world.getTile(x,y).getNow(null);
                    if (ttype == null || ttype.getProperties().solid) {
                        // try collide
                        Rectangle trect = new Rectangle(x * World.TILE_SIZE, y * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
                        if (Intersector.intersectRectangles(bbox, trect, intersection_blank)) {
                            if (nearest < trect.getY() + trect.getHeight()) {
                                nearest = trect.getY() + trect.getHeight();
                                collided = true;
                            }
                        }
                    }
                }
            }

            if (collided) {
                position.y = nearest;
                velocity.y = -5;
                grounded = true;
                framesSinceGrounded = 0;
                jumpedSinceGrounded = false;
            }
        }
    }
}
