package org.nguyendevs.ultimateWarpPad.schematic;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class AdminWarpSchematicData {

    public static class SchematicData {
        public final BlockData[] palette;
        public final byte[] data;

        public SchematicData(BlockData[] palette, byte[] data) {
            this.palette = palette;
            this.data = data;
        }
    }

    private static final int W = 7, H = 3, L = 7;
    private static final SchematicData[] SCHEMATICS = new SchematicData[6];

    static {
        BlockData[][] palettes = new BlockData[6][];
        byte[][] datas = new byte[6][];

        palettes[0] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
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
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:chiseled_quartz_block",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:quartz_pillar[axis=y]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:chiseled_quartz_block",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[0] = new byte[]{
            0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 3, 4, 0, 0, 0, 5, 1, 6, 1, 7, 0, 1, 8, 6, 9, 6, 10, 1, 0, 11, 1, 6, 1, 12, 0, 0, 0, 13, 14, 15, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 16, 16, 16, 0, 0, 0, 9, 9, 9, 9, 9, 0, 17, 9, 18, 18, 18, 9, 19, 17, 9, 18, 18, 18, 9, 19, 17, 9, 18, 18, 18, 9, 19, 0, 9, 9, 9, 9, 9, 0, 0, 0, 20, 20, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 22, 16, 23, 21, 0, 0, 24, 25, 25, 25, 26, 0, 0, 17, 25, 27, 25, 19, 0, 0, 28, 25, 25, 25, 29, 0, 0, 21, 30, 20, 31, 21, 0, 0, 0, 0, 0, 0, 0, 0,
        };

        palettes[1] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
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
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diamond_block",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:quartz_pillar[axis=y]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[1] = new byte[]{
            0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 3, 4, 0, 0, 0, 5, 1, 6, 1, 7, 0, 1, 8, 6, 9, 6, 10, 1, 0, 11, 1, 6, 1, 12, 0, 0, 0, 13, 14, 15, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 16, 16, 16, 0, 0, 0, 17, 17, 6, 17, 17, 0, 18, 17, 19, 19, 19, 17, 20, 18, 6, 19, 19, 19, 6, 20, 18, 17, 19, 19, 19, 17, 20, 0, 17, 17, 6, 17, 17, 0, 0, 0, 21, 21, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22, 23, 16, 24, 22, 0, 0, 25, 26, 26, 26, 27, 0, 0, 18, 26, 9, 26, 20, 0, 0, 28, 26, 26, 26, 29, 0, 0, 22, 30, 21, 31, 22, 0, 0, 0, 0, 0, 0, 0, 0,
        };

        palettes[2] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
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
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diamond_block",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:sea_lantern",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:quartz_pillar[axis=y]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:beacon",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[2] = new byte[]{
            0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 3, 4, 0, 0, 0, 5, 1, 6, 1, 7, 0, 1, 8, 6, 9, 6, 10, 1, 0, 11, 1, 6, 1, 12, 0, 0, 0, 13, 14, 15, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 16, 16, 16, 0, 0, 0, 9, 6, 6, 6, 9, 0, 17, 6, 18, 18, 18, 6, 19, 17, 6, 18, 20, 18, 6, 19, 17, 6, 18, 18, 18, 6, 19, 0, 9, 6, 6, 6, 9, 0, 0, 0, 21, 21, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22, 23, 16, 24, 22, 0, 0, 25, 26, 20, 26, 27, 0, 0, 17, 20, 28, 20, 19, 0, 0, 29, 26, 20, 26, 30, 0, 0, 22, 31, 21, 32, 22, 0, 0, 0, 0, 0, 0, 0, 0,
        };

        palettes[3] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
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
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:sea_lantern",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diamond_block",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:beacon",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[3] = new byte[]{
            0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 3, 4, 0, 0, 0, 5, 1, 6, 1, 7, 0, 1, 8, 6, 9, 6, 10, 1, 0, 11, 1, 6, 1, 12, 0, 0, 0, 13, 14, 15, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 16, 16, 16, 0, 0, 0, 17, 6, 18, 6, 17, 0, 19, 6, 20, 20, 20, 6, 21, 19, 18, 20, 20, 20, 18, 21, 19, 6, 20, 20, 20, 6, 21, 0, 17, 6, 18, 6, 17, 0, 0, 0, 22, 22, 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 23, 24, 16, 25, 23, 0, 0, 26, 18, 18, 18, 27, 0, 0, 19, 18, 28, 18, 21, 0, 0, 29, 18, 18, 18, 30, 0, 0, 23, 31, 22, 32, 23, 0, 0, 0, 0, 0, 0, 0, 0,
        };

        palettes[4] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
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
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:sea_lantern",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diamond_block",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[4] = new byte[]{
            0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 3, 4, 0, 0, 0, 5, 1, 6, 1, 7, 0, 1, 8, 6, 9, 6, 10, 1, 0, 11, 1, 6, 1, 12, 0, 0, 0, 13, 14, 15, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 16, 16, 16, 0, 0, 0, 17, 18, 17, 18, 17, 0, 19, 18, 20, 20, 20, 18, 21, 19, 17, 20, 20, 20, 17, 21, 19, 18, 20, 20, 20, 18, 21, 0, 17, 18, 17, 18, 17, 0, 0, 0, 22, 22, 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 23, 24, 16, 25, 23, 0, 0, 26, 18, 18, 18, 27, 0, 0, 19, 18, 6, 18, 21, 0, 0, 28, 18, 18, 18, 29, 0, 0, 23, 30, 22, 31, 23, 0, 0, 0, 0, 0, 0, 0, 0,
        };

        palettes[5] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_slab[type=top,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=top,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=top,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=top,shape=outer_left,waterlogged=false]",
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
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diamond_block",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:chiseled_quartz_block",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
        });
        datas[5] = new byte[]{
            0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 3, 4, 0, 0, 0, 5, 1, 6, 1, 7, 0, 1, 8, 6, 9, 6, 10, 1, 0, 11, 1, 6, 1, 12, 0, 0, 0, 13, 14, 15, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 16, 16, 16, 0, 0, 0, 17, 17, 17, 17, 17, 0, 18, 17, 19, 19, 19, 17, 20, 18, 17, 19, 19, 19, 17, 20, 18, 17, 19, 19, 19, 17, 20, 0, 17, 17, 17, 17, 17, 0, 0, 0, 21, 21, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22, 23, 16, 24, 22, 0, 0, 25, 6, 9, 6, 26, 0, 0, 18, 9, 27, 9, 20, 0, 0, 28, 6, 9, 6, 29, 0, 0, 22, 30, 21, 31, 22, 0, 0, 0, 0, 0, 0, 0, 0,
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
                    int palIndex = schem.data[idx++] & 0xFF;
                    if (palIndex != 0) {
                        world.getBlockAt(ox + x, oy + y, oz + z).setBlockData(schem.palette[palIndex], false);
                    }
                }
            }
        }
    }

    public static void clearArea(World world, int ox, int oy, int oz) {
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
