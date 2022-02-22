package org.delusion.elgame.item;

import org.delusion.elgame.item.tool.ToolType;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.tile.TileTypes;

public class Items {
    public static final Item Dirt = Item.Builder
        .create("dirt", 200)
        .placeResult(TileTypes.Dirt).build();

    public static final Item Grass = Item.Builder
        .create("grass", 200)
        .placeResult(TileTypes.Grass).build();

    public static final Item Stone = Item.Builder
        .create("stone", 200)
        .placeResult(TileTypes.Stone).build();

    public static final Item Pickaxe = Item.Builder
        .create("pick", 1).miningTool(1.0f)
        .toolTypes(ToolType.Pickaxe).build();
    
    public static final Item SuperPickaxe = Item.Builder
        .create("super-pick", 1).miningTool(9999.0f)
        .toolTypes(ToolType.Pickaxe).build();

    public static final Item Spade = Item.Builder
        .create("spade", 1).miningTool(1.0f)
        .toolTypes(ToolType.Shovel).build();

    public static final Item Torch = Item.Builder
        .create("torch", 99).placeResultForeground(TileTypes.Torch).build();
}
