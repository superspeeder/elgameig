package org.delusion.elgame.tile;

import org.delusion.elgame.inventory.Stack;
import org.delusion.elgame.item.Items;
import org.delusion.elgame.item.tool.ToolType;

import java.util.stream.Stream;

public class TileTypes {
    public static final TileType Air = new TileType(0, "Air", TileProperties.builder().intangible().invisible().build(), null);

    public static final TileType Dirt = new TileType(1, "Dirt", TileProperties.builder()
            .drops(TileDropsFunction.supplier(() -> new Stack(Items.Dirt)))
            .hardness(0.5f)
            .breakingTools(ToolType.Shovel, ToolType.Pickaxe).build(), "tile");

    public static final TileType StoneTile = new TileType(2, "Stone Tile", TileProperties.builder()
            .drops(TileDropsFunction.supplier(() -> new Stack(Items.Stone)))
            .hardness(2.0f)
            .breakingTools(ToolType.Pickaxe).build(), "tile");

    public static final TileType Stone = new TileType(3, "Stone", TileProperties.builder()
            .drops(TileDropsFunction.supplier(() -> new Stack(Items.Stone)))
            .hardness(1.0f)
            .breakingTools(ToolType.Pickaxe).build(), "tile");

    public static final TileType Grass = new TileType(4, "Grass", TileProperties.builder()
            .hardness(1.0f)
            .drops((player, world, tilePos, stack, layer) -> {
                if (stack.getItem() == Items.Spade) {
                    return Stream.of(new Stack(Items.Grass));
                } else {
                    return Stream.of(new Stack(Items.Dirt));
                }
            }).breakingTools(ToolType.Shovel, ToolType.Pickaxe).build(), "tile");

}
