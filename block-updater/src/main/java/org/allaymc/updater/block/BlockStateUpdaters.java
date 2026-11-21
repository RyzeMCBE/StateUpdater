package org.allaymc.updater.block;

import lombok.experimental.UtilityClass;
import org.allaymc.updater.block.context.BlockUpdaterContext;
import org.cloudburstmc.nbt.NbtMap;

import java.util.ArrayList;
import java.util.List;

import static org.allaymc.updater.common.util.TagUtils.generateVersionFor;

@UtilityClass
public class BlockStateUpdaters {
    public static final int LATEST_VERSION;

    private static final BlockUpdaterContext CONTEXT;

    static {
        List<BlockStateUpdater> updaters = new ArrayList<>();
        updaters.add(BlockStateUpdaterBase.INSTANCE);
        updaters.add(BlockStateUpdater_1_10_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_12_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_13_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_14_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_16_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_16_100.INSTANCE);
        updaters.add(BlockStateUpdater_1_16_210.INSTANCE);
        updaters.add(BlockStateUpdater_1_17_30.INSTANCE);
        updaters.add(BlockStateUpdater_1_17_40.INSTANCE);
        updaters.add(BlockStateUpdater_1_18_10.INSTANCE);
        updaters.add(BlockStateUpdater_1_18_20.INSTANCE);
        updaters.add(BlockStateUpdater_1_18_30.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_20.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_60.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_70.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_80.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_10.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_20.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_30.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_40.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_50.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_60.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_70.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_80.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_20.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_30.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_40.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_60.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_110.INSTANCE);
        updaters.add(BlockStateUpdater_1_26_30.INSTANCE);
        updaters.add(BlockStateUpdater_1_26_50.INSTANCE);
        updaters.add(BlockStateUpdater_1_26_60.INSTANCE);

        var context = new BlockUpdaterContext();
        updaters.forEach(updater -> {
            context.setVersion(updater.getVersion());
            updater.registerUpdaters(context);
        });

        CONTEXT = context;
        LATEST_VERSION = context.getLatestVersion();
    }


    /**
     * Some imported worlds contain a mixture of old log properties and the new split
     * identifiers (for example oak_log with direction instead of pillar_axis).
     * Decode only known legacy directions; leave unknown/conflicting values untouched
     * so Engine can preserve the original block NBT instead of guessing an axis.
     */
    private static NbtMap normalizeLegacyLogDirection(NbtMap tag) {
        String name = tag.getString("name");
        if (!isLegacyLogType(name)) return tag;
        var states = tag.getCompound("states");
        Object direction = states.get("direction");
        if (direction == null) return tag;

        String axis = null;
        if (direction instanceof Number number) {
            int value = number.intValue();
            if (number.doubleValue() == value) {
                axis = switch (value) {
                    case 0, 3 -> "y";
                    case 1 -> "x";
                    case 2 -> "z";
                    default -> null;
                };
            }
        } else if (direction instanceof String value) {
            axis = switch (value) {
                case "0", "3", "y" -> "y";
                case "1", "x" -> "x";
                case "2", "z" -> "z";
                default -> null;
            };
        }
        if (axis == null) return tag;

        Object existingAxis = states.get("pillar_axis");
        if (existingAxis != null && !axis.equals(existingAxis)) return tag;

        var builder = states.toBuilder();
        builder.remove("direction");
        builder.putString("pillar_axis", axis);
        // Legacy direction=3 denotes the all-bark wood variant, not a vertical log.
        boolean bark = direction instanceof Number number && number.doubleValue() == 3 ||
                direction instanceof String value && value.equals("3");
        if (bark && name.endsWith("_log")) {
            name = name.substring(0, name.length() - "_log".length()) + "_wood";
        }
        return tag.toBuilder().putString("name", name).putCompound("states", builder.build()).build();
    }

    private static boolean isLegacyLogType(String name) {
        if (!name.startsWith("minecraft:")) return false;
        String path = name.substring("minecraft:".length());
        if (path.startsWith("stripped_")) path = path.substring("stripped_".length());
        if (!path.endsWith("_log") && !path.endsWith("_wood")) return false;
        String wood = path.substring(0, path.lastIndexOf('_'));
        return switch (wood) {
            case "oak", "spruce", "birch", "jungle", "acacia", "dark_oak" -> true;
            default -> false;
        };
    }

    public static NbtMap updateBlockState(NbtMap tag, int updateToVersion) {
        var validTagBuilder = NbtMap.builder();
        validTagBuilder.putString("name", tag.getString("name"));
        if (tag.containsKey("val")) {
            validTagBuilder.putInt("val", tag.getInt("val"));
        } else {
            validTagBuilder.putCompound("states", tag.getCompound("states"));
        }

        return generateVersionFor(normalizeLegacyLogDirection(CONTEXT.updateStates(validTagBuilder.build(), updateToVersion)));
    }
}
