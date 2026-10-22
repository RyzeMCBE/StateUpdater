package org.allaymc.updater.common.util;

import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagUtilsTest {
    @Test
    void compoundListsSurviveMutableRoundTrip() {
        var entry = NbtMap.builder()
                .putString("name", "minecraft:stone")
                .putInt("count", 3)
                .build();
        var original = NbtMap.builder()
                .put("entries", new NbtList<>(NbtType.COMPOUND, List.of(entry)))
                .build();

        var roundTripped = TagUtils.toImmutable(TagUtils.toMutable(original));
        assertEquals(original, roundTripped);
    }
}
