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
            .breakingHealth(0.6f)
            .breakingTools(ToolType.Shovel, ToolType.Pickaxe).build(), "tile");

    public static final TileType StoneTile = new TileType(2, "Stone Tile", TileProperties.builder()
            .drops(TileDropsFunction.supplier(() -> new Stack(Items.Stone)))
            .hardness(2.0f)
            .breakingHealth(1.3f)
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

    public static final TileType Torch = new TileType(5, "Torch", TileProperties.builder()
            .breakingTools(ToolType.Pickaxe, ToolType.Shovel)
            .drops(TileDropsFunction.supplier(() -> new Stack(Items.Torch)))
            .emmission(0.8f).intangible().build(), "tile");

    public static final TileType SuperbrightTorch = new TileType(6, "Superbright Torch", TileProperties.builder()
            .breakingTools(ToolType.Pickaxe, ToolType.Shovel)
            .drops(TileDropsFunction.supplier(() -> new Stack(Items.Torch)))
            .emmission(1.4f).intangible().build(), "tile");

    public static final TileType CoalOre = new TileType(50, "Coal Ore", TileProperties.builder()
            .hardness(1.5f)
            .drops(TileDropsFunction.supplier(() -> new Stack(Items.CoalOre)))
            .breakingTools(ToolType.Pickaxe).build(), "tile");

    public static final TileType CopperOre = new TileType(51, "Copper Ore", TileProperties.builder()
            .hardness(2.0f)
            .drops(TileDropsFunction.supplier(() -> new Stack(Items.CopperOre)))
            .breakingTools(ToolType.Pickaxe).build(), "tile");

    public static final TileType IronOre = new TileType(52, "Iron Ore", TileProperties.builder()
            .hardness(2.5f)
            .drops(TileDropsFunction.supplier(() -> new Stack(Items.IronOre)))
            .breakingTools(ToolType.Pickaxe).build(), "tile");
}
