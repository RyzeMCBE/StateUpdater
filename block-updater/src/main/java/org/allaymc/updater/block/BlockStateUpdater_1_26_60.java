package org.allaymc.updater.block;

import org.allaymc.updater.block.context.BlockUpdaterContext;

/**
 * Adds the connection properties introduced by the 1.26.60 block palette.
 * Connections start disconnected and are recalculated from neighbouring blocks by the server.
 */
public final class BlockStateUpdater_1_26_60 extends BlockStateUpdater {
    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_26_60();

    private BlockStateUpdater_1_26_60() {
        super(1, 26, 60);
    }

    @Override
    public void registerUpdaters(BlockUpdaterContext context) {
        for (var direction : new String[]{"north", "east", "south", "west", "up", "down"}) {
            context.addProperty("minecraft:chorus_plant", "minecraft:connection_" + direction, (byte) 0);
            if (!direction.equals("down")) {
                context.addProperty("minecraft:fire", "minecraft:connection_" + direction, (byte) 0);
            }
        }
        for (var direction : new String[]{"north", "east", "south", "west"}) {
            context.addProperty("minecraft:redstone_wire", "redstone_" + direction, "none");
        }
    }
}
