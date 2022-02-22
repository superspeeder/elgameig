package org.delusion.elgame.item;

import org.delusion.elgame.inventory.Stack;
import org.delusion.elgame.player.Player;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.tile.TileTypes;
import org.delusion.elgame.utils.Vector2i;
import org.delusion.elgame.world.World;

import java.util.stream.Stream;

@FunctionalInterface
public interface ItemUsageAction {
    float REACH_MAX = 8;
    ItemUsageAction none = (player, world, tilePos, stack, firstUse) -> {};

    static ItemUsageAction placeTile(TileType ttype) {
        return (player, world, tilePos, stack, firstUse) -> {
            if (player.distanceToTileCenter(tilePos) > REACH_MAX) return;
            if (stack.getCount() > 0) {
                if (world.canPlaceAt(tilePos, ttype)) {
                    world.setTile(tilePos, ttype);
                    stack.add(-1);
                }
            }
        };
    }

    static ItemUsageAction placeTileBg(TileType ttype) {
        return (player, world, tilePos, stack, firstUse) -> {
            if (player.distanceToTileCenter(tilePos) > REACH_MAX) return;
            if (stack.getCount() > 0) {
                if (world.canPlaceAtBg(tilePos)) {
                    world.setTileBg(tilePos, ttype);
                    stack.add(-1);
                }
            }
        };
    }

    static ItemUsageAction placeTileBG(TileType ttype) {
        return (player, world, tilePos, stack, firstUse) -> {
            if (player.distanceToTileCenter(tilePos) > REACH_MAX) return;
            if (stack.getCount() > 0) {
                if (world.canPlaceAtBg(tilePos)) {
                    world.setTileBg(tilePos, ttype);
                    stack.add(-1);
                }
            }
        };
    }

    ItemUsageAction mineTile = (player, world, tilePos, stack, firstUse) -> {
        if (player.distanceToTileCenter(tilePos) > REACH_MAX) return;
        TileType tt = world.getTile(tilePos).getNow(TileTypes.Air);
        if (tt != TileTypes.Air) {

            if (stack.getItem().getToolTypes().stream().anyMatch(tt.getProperties().breakingTools::contains) && stack.getItem().toolStrength() >= tt.getProperties().hardness) {
                Stream<Stack> drops = tt.getDrop(player, world, tilePos, stack, World.Layer.Main);
                if (drops != null) {
                    drops.forEach(player::tryInsertStack);
                }
                world.setTile(tilePos.x, tilePos.y, TileTypes.Air);
            }
        }
    };

    ItemUsageAction mineTileBg = (player, world, tilePos, stack, firstUse) -> {
        if (player.distanceToTileCenter(tilePos) > REACH_MAX) return;
        TileType tt = world.getTileBg(tilePos).getNow(TileTypes.Air);
        if (tt != TileTypes.Air) {

            if (stack.getItem().getToolTypes().stream().anyMatch(tt.getProperties().breakingTools::contains) && stack.getItem().toolStrength() >= tt.getProperties().hardness) {
                Stream<Stack> drops = tt.getDrop(player, world, tilePos, stack, World.Layer.BG);
                if (drops != null) {
                    drops.forEach(player::tryInsertStack);
                }
                world.setTileBg(tilePos.x, tilePos.y, TileTypes.Air);
            }
        }
    };

    void onUse(Player player, World world, Vector2i tilePos, Stack stack, boolean firstUse);
}
