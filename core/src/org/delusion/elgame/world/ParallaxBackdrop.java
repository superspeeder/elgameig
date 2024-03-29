package org.delusion.elgame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import org.apache.commons.lang3.tuple.Pair;
import org.delusion.elgame.ElGame;

import java.util.Collection;
import java.util.TreeSet;

public class ParallaxBackdrop {

    private TreeSet<Pair<Texture, Integer>> layers;

    public ParallaxBackdrop(Collection<Pair<Texture,Integer>> layers) {
        this.layers = new TreeSet<>((l, r) -> Integer.compare(r.getRight(), l.getRight()));
        this.layers.addAll(layers);
    }

    public void render(SpriteBatch batch, ElGame game) {
        layers.forEach(p -> renderLayer(batch, game, p.getLeft(),p.getRight()));
    }

    private void renderLayer(SpriteBatch batch, ElGame game, Texture tex, int distance) {
        Camera c = game.getPlayer().getCamera();
        Vector3 cornerpos = c.unproject(new Vector3(0,Gdx.graphics.getHeight(),0));
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float posx = game.getPlayer().getPosition().x;
        float posy = game.getPlayer().getPosition().y;
        float zoom = game.getPlayer().getZoom();
        float xwrap, ywrap;
        if (Math.floorMod(distance, 10) == 0) {
            ywrap = 0;
        } else {
            ywrap = (posy / height) / Math.floorMod(distance,10);
        }
        if (Math.floorDiv(distance, 10) == 0 || Math.floorDiv(distance, 10) == 10) {
            xwrap = 0;
        } else {
            xwrap = (posx / width) / Math.floorDiv(distance, 10);
        }
        batch.draw(tex, cornerpos.x, cornerpos.y, width*zoom, height*zoom,xwrap, 1-ywrap, 1+xwrap, 0-ywrap);
    }
}
