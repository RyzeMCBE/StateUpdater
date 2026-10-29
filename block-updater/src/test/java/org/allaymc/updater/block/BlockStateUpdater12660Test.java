package org.allaymc.updater.block;

import org.cloudburstmc.nbt.NbtMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockStateUpdater12660Test {
    @Test
    void upgradesLegacyConnectionsWithoutChangingFireAgeOrRedstonePower() {
        var fire = update("minecraft:fire", NbtMap.builder().putInt("age", 13).build());
        assertEquals(13, fire.getInt("age"));
        for (var face : new String[]{"north", "east", "south", "west", "up"}) {
            assertEquals((byte) 0, fire.get("minecraft:connection_" + face));
        }
        assertFalse(fire.containsKey("minecraft:connection_down"));

        var wire = update("minecraft:redstone_wire", NbtMap.builder().putInt("redstone_signal", 11).build());
        assertEquals(11, wire.getInt("redstone_signal"));
        for (var face : new String[]{"north", "east", "south", "west"}) {
            assertEquals("none", wire.getString("redstone_" + face));
        }
        assertEquals(6, update("minecraft:chorus_plant", NbtMap.EMPTY).size());
    }

    @Test
    void preservesAlreadyMigratedConnections() {
        var states = NbtMap.builder().putInt("age", 4).putByte("minecraft:connection_up", (byte) 1).build();
        assertEquals((byte) 1, update("minecraft:fire", states).get("minecraft:connection_up"));
        var wire = NbtMap.builder().putInt("redstone_signal", 7).putString("redstone_north", "up").build();
        assertEquals("up", update("minecraft:redstone_wire", wire).getString("redstone_north"));
    }

    @Test
    void leavesEarlierTargetSchemasUnchanged() {
        var tag = NbtMap.builder().putString("name", "minecraft:chorus_plant").putCompound("states", NbtMap.EMPTY).build();
        var previous = BlockStateUpdaters.updateBlockState(tag, BlockStateUpdater_1_26_50.INSTANCE.getVersion());
        assertTrue(previous.getCompound("states").isEmpty());
    }

    private static NbtMap update(String name, NbtMap states) {
        return BlockStateUpdaters.updateBlockState(NbtMap.builder()
                .putString("name", name).putCompound("states", states).build(),
                BlockStateUpdaters.LATEST_VERSION).getCompound("states");
    }
}
