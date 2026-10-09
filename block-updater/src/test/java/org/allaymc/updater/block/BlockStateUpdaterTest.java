package org.allaymc.updater.block;

import org.cloudburstmc.nbt.NbtMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author IWareQ
 */
class BlockStateUpdaterTest {
    @Test
    void update_from_legacy() {
        var stone = BlockStateUpdaters.updateBlockState(
                NbtMap.builder()
                        .putString("name", "minecraft:stone")
                        .putInt("val", 1)
                        .build(),
                BlockStateUpdaters.LATEST_VERSION
        );
        assertEquals("minecraft:granite", stone.getString("name"));
        assertTrue(stone.getCompound("states").isEmpty());

        var goldenRail = BlockStateUpdaters.updateBlockState(
                NbtMap.builder()
                        .putString("name", "minecraft:golden_rail")
                        .putInt("val", 6)
                        .build(),
                BlockStateUpdaters.LATEST_VERSION
        );
        assertEquals("minecraft:golden_rail", goldenRail.getString("name"));
        var goldenRailStates = goldenRail.getCompound("states");
        assertEquals(0, goldenRailStates.getByte("rail_data_bit"));
        assertEquals(0, goldenRailStates.getInt("rail_direction"));
    }

    @Test
    void remapState() {
        var anvil = BlockStateUpdaters.updateBlockState(
                NbtMap.builder()
                        .putString("name", "minecraft:anvil")
                        .putCompound("states", NbtMap.builder()
                                .putString("damage", "very_damaged")
                                .putInt("direction", 3)
                                .build())
                        .build(),
                BlockStateUpdaters.LATEST_VERSION
        );
        assertEquals("minecraft:damaged_anvil", anvil.getString("name"));
        assertEquals("east", anvil.getCompound("states").getString("minecraft:cardinal_direction"));
    }

    @Test
    void testRenameId() {
        var dandelion = BlockStateUpdaters.updateBlockState(
                NbtMap.builder()
                        .putString("name", "minecraft:yellow_flower")
                        .putCompound("states", NbtMap.EMPTY)
                        .build(),
                BlockStateUpdaters.LATEST_VERSION
        );
        assertEquals("minecraft:dandelion", dandelion.getString("name"));
    }

    @Test
    void testStonebrick() {
        var defaultStoneBricks = BlockStateUpdaters.updateBlockState(
                NbtMap.builder()
                        .putString("name", "minecraft:stonebrick")
                        .putCompound("states", NbtMap.builder()
                                .putString("stone_brick_type", "default")
                                .build()
                        )
                        .build(),
                BlockStateUpdaters.LATEST_VERSION
        );
        assertEquals("minecraft:stone_bricks", defaultStoneBricks.getString("name"));
    }

    @Test
    void testRemapWood() {
        var wood0 = BlockStateUpdaters.updateBlockState(
                NbtMap.builder()
                        .putString("name", "minecraft:wood")
                        .putCompound("states", NbtMap.builder()
                                .putInt("stripped_bit", 0)
                                .putString("wood_type", "oak")
                                .putString("pillar_axis", "y")
                                .build()
                        )
                        .build(),
                BlockStateUpdaters.LATEST_VERSION
        );
        assertEquals("minecraft:oak_wood", wood0.getString("name"));
        assertFalse(wood0.getCompound("states").containsKey("stripped_bit"));
        assertFalse(wood0.getCompound("states").containsKey("wood_type"));
        assertTrue(wood0.getCompound("states").containsKey("pillar_axis"));

        var wood1 = BlockStateUpdaters.updateBlockState(
                NbtMap.builder()
                        .putString("name", "minecraft:wood")
                        .putCompound("states", NbtMap.builder()
                                .putInt("stripped_bit", 1)
                                .putString("wood_type", "birch")
                                .putString("pillar_axis", "y")
                                .build()
                        )
                        .build(),
                BlockStateUpdaters.LATEST_VERSION
        );
        assertEquals("minecraft:stripped_birch_wood", wood1.getString("name"));
        assertFalse(wood1.getCompound("states").containsKey("stripped_bit"));
        assertFalse(wood1.getCompound("states").containsKey("wood_type"));
        assertTrue(wood1.getCompound("states").containsKey("pillar_axis"));
    }

    @Test
    void testRemapTnt() {
        var tnt0 = BlockStateUpdaters.updateBlockState(
                NbtMap.builder()
                        .putString("name", "minecraft:tnt")
                        .putCompound("states", NbtMap.builder()
                                .putBoolean("allow_underwater_bit", false)
                                .build()
                        )
                        .build(),
                BlockStateUpdaters.LATEST_VERSION
        );
        assertEquals("minecraft:tnt", tnt0.getString("name"));
        assertTrue(tnt0.getCompound("states").isEmpty());

        var tnt1 = BlockStateUpdaters.updateBlockState(
                NbtMap.builder()
                        .putString("name", "minecraft:tnt")
                        .putCompound("states", NbtMap.builder()
                                .putBoolean("allow_underwater_bit", true)
                                .build()
                        )
                        .build(),
                BlockStateUpdaters.LATEST_VERSION
        );
        assertEquals("minecraft:underwater_tnt", tnt1.getString("name"));
        assertTrue(tnt1.getCompound("states").isEmpty());
    }

    @Test
    void testRequiredVersionUpdater() {
        var creakingHeart0 = BlockStateUpdaters.updateBlockState(
                NbtMap.builder()
                        .putString("name", "minecraft:creaking_heart")
                        .putCompound("states", NbtMap.builder()
                                .putBoolean("active", true)
                                .putBoolean("natural", true)
                                .putString("pillar_axis", "x")
                                .build()
                        )
                        .build(),
                BlockStateUpdater_1_21_60.INSTANCE.getVersion()
        );
        assertEquals("minecraft:creaking_heart", creakingHeart0.getString("name"));
        var states = creakingHeart0.getCompound("states");
        assertFalse(states.isEmpty());
        assertFalse(states.containsKey("active"));
        assertEquals("dormant", states.getString("creaking_heart_state"));
    }

    /**
     * 1.26.30 adımı özelliği olmayan {@code minecraft:potent_sulfur}'a {@code potent_sulfur_state=dry} ekler. Hedef
     * sürüm daha eskiyse uygulanmaz; özellik zaten varsa (daha yeni sürümle yazılmış veri) değerine dokunmaz.
     */
    @Test
    void testAddPotentSulfurState_1_26_30() {
        var withoutState = NbtMap.builder()
                .putString("name", "minecraft:potent_sulfur")
                .putCompound("states", NbtMap.EMPTY)
                .build();
        var version_1_26_30 = BlockStateUpdater_1_26_30.INSTANCE.getVersion();

        var upgraded = BlockStateUpdaters.updateBlockState(withoutState, version_1_26_30);
        assertEquals("dry", upgraded.getCompound("states").getString("potent_sulfur_state"));

        var olderTarget = BlockStateUpdaters.updateBlockState(withoutState, BlockStateUpdater_1_21_110.INSTANCE.getVersion());
        assertFalse(olderTarget.getCompound("states").containsKey("potent_sulfur_state"));

        var wet = BlockStateUpdaters.updateBlockState(
                withoutState.toBuilder()
                        .putCompound("states", NbtMap.builder().putString("potent_sulfur_state", "wet").build())
                        .build(),
                version_1_26_30
        );
        assertEquals("wet", wet.getCompound("states").getString("potent_sulfur_state"));
    }

    /**
     * 1.26.50 adımı eski merdivene köşe, çit/panel/parmaklık/tuzak ipine bağlantı durumlarını varsayılanla ekler. Değer
     * zaten varsa (PocketMine'ın 1.26.50 verisi) dokunmaz; bağlantı byte olarak eklenir.
     */
    @Test
    void testAddCornerAndConnections_1_26_50() {
        var version = BlockStateUpdater_1_26_50.INSTANCE.getVersion();
        var stairs = BlockStateUpdaters.updateBlockState(NbtMap.builder()
                .putString("name", "minecraft:oak_stairs")
                .putCompound("states", NbtMap.builder().putByte("upside_down_bit", (byte) 1).putInt("weirdo_direction", 2).build())
                .build(), version);
        assertEquals("none", stairs.getCompound("states").getString("minecraft:corner"));
        assertEquals(1, stairs.getCompound("states").getByte("upside_down_bit"));

        for (var name : new String[]{"minecraft:oak_fence", "minecraft:glass_pane", "minecraft:iron_bars", "minecraft:trip_wire"}) {
            var states = BlockStateUpdaters.updateBlockState(NbtMap.builder()
                    .putString("name", name)
                    .putCompound("states", NbtMap.EMPTY)
                    .build(), version).getCompound("states");
            for (var direction : new String[]{"north", "east", "south", "west"}) {
                assertEquals((byte) 0, states.get("minecraft:connection_" + direction), name + " " + direction);
            }
        }

        var connected = BlockStateUpdaters.updateBlockState(NbtMap.builder()
                .putString("name", "minecraft:oak_fence")
                .putCompound("states", NbtMap.builder().putByte("minecraft:connection_north", (byte) 1).build())
                .build(), version);
        assertEquals((byte) 1, connected.getCompound("states").get("minecraft:connection_north"));

        var olderTarget = BlockStateUpdaters.updateBlockState(NbtMap.builder()
                .putString("name", "minecraft:oak_stairs")
                .putCompound("states", NbtMap.EMPTY)
                .build(), BlockStateUpdater_1_26_30.INSTANCE.getVersion());
        assertFalse(olderTarget.getCompound("states").containsKey("minecraft:corner"));
    }
}
