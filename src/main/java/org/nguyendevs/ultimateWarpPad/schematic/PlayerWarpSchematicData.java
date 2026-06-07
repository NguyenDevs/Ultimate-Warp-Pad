package org.nguyendevs.ultimateWarpPad.schematic;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class PlayerWarpSchematicData {

    public static class SchematicData {
        public final BlockData[] palette;
        public final byte[] data;

        public SchematicData(BlockData[] palette, byte[] data) {
            this.palette = palette;
            this.data = data;
        }
    }

    private static final int W = 5, H = 3, L = 5;
    private static final SchematicData[] SCHEMATICS = new SchematicData[6];

    static {
        BlockData[][] palettes = new BlockData[6][];
        byte[][] datas = new byte[6][];

        palettes[0] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:sea_lantern",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:chiseled_quartz_block",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:quartz_pillar[axis=y]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:chiseled_quartz_block",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[0] = new byte[]{
            0, 1, 2, 3, 0, 4, 5, 6, 5, 7, 8, 6, 9, 6, 10, 11, 5, 6, 5, 12, 0, 13, 14, 15, 0,
            0, 9, 9, 9, 0, 9, 16, 16, 16, 9, 9, 16, 16, 16, 9, 9, 16, 16, 16, 9, 0, 9, 9, 9, 0,
            0, 17, 18, 19, 0, 20, 21, 21, 21, 22, 23, 21, 24, 21, 25, 26, 21, 21, 21, 27, 0, 28, 29, 30, 0,
        };

        palettes[1] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:chiseled_quartz_block",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=straight,waterlogged=false]",
            "minecraft:sea_lantern",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:diamond_block",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:quartz_pillar[axis=y]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[1] = new byte[]{
            0, 1, 2, 3, 0, 4, 5, 6, 5, 7, 8, 6, 9, 6, 10, 11, 5, 6, 5, 12, 0, 13, 14, 15, 0,
            0, 16, 6, 16, 0, 16, 17, 17, 17, 16, 6, 17, 17, 17, 6, 16, 17, 17, 17, 16, 0, 16, 6, 16, 0,
            0, 18, 19, 20, 0, 21, 22, 22, 22, 23, 24, 22, 9, 22, 25, 26, 22, 22, 22, 27, 0, 28, 29, 30, 0,
        };

        palettes[2] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:chiseled_quartz_block",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:diamond_block",
            "minecraft:sea_lantern",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:quartz_pillar[axis=y]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:beacon",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[2] = new byte[]{
            0, 1, 2, 3, 0, 4, 5, 6, 5, 7, 8, 6, 9, 6, 10, 11, 5, 6, 5, 12, 0, 13, 14, 15, 0,
            0, 6, 6, 6, 0, 6, 16, 16, 16, 6, 6, 16, 17, 16, 6, 6, 16, 16, 16, 6, 0, 6, 6, 6, 0,
            0, 18, 19, 20, 0, 21, 22, 17, 22, 23, 24, 17, 25, 17, 26, 27, 22, 17, 22, 28, 0, 29, 30, 31, 0,
        };

        palettes[3] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:chiseled_quartz_block",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=straight,waterlogged=false]",
            "minecraft:quartz_pillar[axis=y]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:sea_lantern",
            "minecraft:diamond_block",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:beacon",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[3] = new byte[]{
            0, 1, 2, 3, 0, 4, 5, 6, 5, 7, 8, 6, 9, 6, 10, 11, 5, 6, 5, 12, 0, 13, 14, 15, 0,
            0, 6, 16, 6, 0, 6, 17, 17, 17, 6, 16, 17, 17, 17, 16, 6, 17, 17, 17, 6, 0, 6, 16, 6, 0,
            0, 18, 19, 20, 0, 21, 16, 16, 16, 22, 23, 16, 24, 16, 25, 26, 16, 16, 16, 27, 0, 28, 29, 30, 0,
        };

        palettes[4] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:chiseled_quartz_block",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=straight,waterlogged=false]",
            "minecraft:quartz_pillar[axis=y]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:sea_lantern",
            "minecraft:calcite",
            "minecraft:diamond_block",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[4] = new byte[]{
            0, 1, 2, 3, 0, 4, 5, 6, 5, 7, 8, 6, 9, 6, 10, 11, 5, 6, 5, 12, 0, 13, 14, 15, 0,
            0, 16, 17, 16, 0, 16, 18, 18, 18, 16, 17, 18, 18, 18, 17, 16, 18, 18, 18, 16, 0, 16, 17, 16, 0,
            0, 19, 20, 21, 0, 22, 16, 16, 16, 23, 24, 16, 6, 16, 25, 26, 16, 16, 16, 27, 0, 28, 29, 30, 0,
        };

        palettes[5] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:sea_lantern",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=straight,waterlogged=false]",
            "minecraft:quartz_pillar[axis=y]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:diamond_block",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:chiseled_quartz_block",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[5] = new byte[]{
            0, 1, 2, 3, 0, 4, 5, 6, 5, 7, 8, 6, 9, 6, 10, 11, 5, 6, 5, 12, 0, 13, 14, 15, 0,
            0, 16, 16, 16, 0, 16, 17, 17, 17, 16, 16, 17, 17, 17, 16, 16, 17, 17, 17, 16, 0, 16, 16, 16, 0,
            0, 18, 19, 20, 0, 21, 6, 9, 6, 22, 23, 9, 24, 9, 25, 26, 6, 9, 6, 27, 0, 28, 29, 30, 0,
        };

        for (int i = 0; i < 6; i++) {
            SCHEMATICS[i] = new SchematicData(palettes[i], datas[i]);
        }
    }

    private static BlockData[] createPalette(String[] strs) {
        BlockData[] palette = new BlockData[strs.length];
        for (int i = 0; i < strs.length; i++) {
            palette[i] = Bukkit.createBlockData(strs[i]);
        }
        return palette;
    }

    public static boolean isLoaded() {
        return true;
    }

    public static int getSchematicCount() {
        return 6;
    }

    public static void paste(World world, int ox, int oy, int oz, int schematicIndex) {
        if (schematicIndex < 0 || schematicIndex >= 6) return;
        SchematicData schem = SCHEMATICS[schematicIndex];

        int idx = 0;
        for (int y = 0; y < H; y++) {
            for (int z = 0; z < L; z++) {
                for (int x = 0; x < W; x++) {
                    int wx = ox + x;
                    int wy = oy + y;
                    int wz = oz + z;
                    int palIndex = schem.data[idx++] & 0xFF;
                    if (palIndex != 0) {
                        world.getBlockAt(wx, wy, wz).setBlockData(schem.palette[palIndex], false);
                    }
                }
            }
        }
    }

    public static void clearArea(World world, int ox, int oy, int oz, int schematicIndex) {
        if (schematicIndex < 0 || schematicIndex >= 6) return;

        for (int y = 0; y < H; y++) {
            for (int z = 0; z < L; z++) {
                for (int x = 0; x < W; x++) {
                    world.getBlockAt(ox + x, oy + y, oz + z).setType(Material.AIR, false);
                }
            }
        }
    }

    public static List<String> captureArea(World world, int ox, int oy, int oz) {
        List<String> snapshot = new ArrayList<>(W * H * L);
        for (int y = 0; y < H; y++) {
            for (int z = 0; z < L; z++) {
                for (int x = 0; x < W; x++) {
                    snapshot.add(world.getBlockAt(ox + x, oy + y, oz + z).getBlockData().getAsString());
                }
            }
        }
        return snapshot;
    }

    public static void restoreArea(World world, int ox, int oy, int oz, List<String> snapshot) {
        if (snapshot == null || snapshot.size() != W * H * L) return;
        int idx = 0;
        for (int y = 0; y < H; y++) {
            for (int z = 0; z < L; z++) {
                for (int x = 0; x < W; x++) {
                    try {
                        BlockData bd = Bukkit.createBlockData(snapshot.get(idx));
                        world.getBlockAt(ox + x, oy + y, oz + z).setBlockData(bd, false);
                    } catch (Exception e) {
                        world.getBlockAt(ox + x, oy + y, oz + z).setType(Material.AIR, false);
                    }
                    idx++;
                }
            }
        }
    }
}
