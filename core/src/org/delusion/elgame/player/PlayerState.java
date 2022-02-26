package org.delusion.elgame.player;

import com.badlogic.gdx.math.Vector2;
import org.delusion.elgame.entity.data.PlayerData;
import org.delusion.elgame.inventory.Stack;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PlayerState implements Serializable {
    public Vector2 position;
    public Vector2 velocity;
    public Vector2 acceleration;
    public Map<Integer, Stack> inventory = new HashMap<>();
    public Map<Integer, Stack> hotbar = new HashMap<>();
    public int selectedHotbarSlot = 0;
    public PlayerData data;
}
