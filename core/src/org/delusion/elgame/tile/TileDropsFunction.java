package org.delusion.elgame.tile;

import org.delusion.elgame.inventory.Stack;
import org.delusion.elgame.player.Player;
import org.delusion.elgame.utils.Vector2i;
import org.delusion.elgame.world.World;
import org.delusion.elgame.world.World.Layer;

import java.util.function.Supplier;
import java.util.stream.Stream;

@FunctionalInterface
public interface TileDropsFunction {

    Stream<Stack> getDrops(Player player, World world, Vector2i tilePos, Stack stack, Layer layer);

    static TileDropsFunction supplier(Supplier<Stack> drop) {
        return (player, world, tilePos, stack, layer) -> Stream.ofNullable(drop.get());
    }

    TileDropsFunction none = (player, world, tilePos, stack, layer) -> Stream.empty();

}
