package org.nguyendevs.ultimateWarpPad.schematic;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class ProxyWarpSchematicData {

    public static class SchematicData {
        public final BlockData[] palette;
        public final byte[] data;

        public SchematicData(BlockData[] palette, byte[] data) {
            this.palette = palette;
            this.data = data;
        }
    }

    private static final int W = 11, H = 4, L = 11;
    private static final SchematicData[] SCHEMATICS = new SchematicData[6];

    static {
        BlockData[][] palettes = new BlockData[6][];
        byte[][] datas = new byte[6][];

        palettes[0] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=low,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=low,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=low,north=low,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=none]",
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
            "minecraft:end_rod[facing=up]"
        });
        datas[0] = new byte[]{
            0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 1, 0, 0, 0, 3, 2, 
            2, 2, 2, 2, 2, 2, 4, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 2, 2, 2, 2, 2, 
            2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 
            2, 4, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 
            0, 5, 2, 2, 2, 2, 2, 5, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 1, 1, 1, 6, 0, 0, 0, 0, 0, 7, 2, 2, 2, 2, 
            2, 8, 0, 0, 0, 9, 2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 
            0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 9, 
            2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 0, 11, 2, 2, 2, 2, 2, 12, 0, 0, 0, 0, 0, 13, 5, 
            5, 5, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 14, 0, 0, 
            0, 0, 0, 0, 15, 1, 16, 0, 0, 0, 0, 0, 0, 0, 17, 18, 18, 18, 19, 0, 0, 0, 0, 0, 0, 
            3, 18, 20, 18, 4, 0, 0, 0, 0, 0, 0, 21, 18, 18, 18, 22, 0, 0, 0, 0, 0, 0, 0, 23, 5, 
            24, 0, 0, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 25, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0
        };

        palettes[1] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=low,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=low,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=low,north=low,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:quartz_pillar[axis=y]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:sea_lantern",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:end_rod[facing=up]"
        });
        datas[1] = new byte[]{
            0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 1, 0, 0, 0, 3, 2, 
            2, 2, 2, 2, 2, 2, 4, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 2, 2, 2, 2, 2, 
            2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 
            2, 4, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 
            0, 5, 2, 2, 2, 2, 2, 5, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 1, 1, 1, 6, 0, 0, 0, 0, 0, 7, 2, 2, 2, 2, 
            2, 8, 0, 0, 0, 9, 2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 
            0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 9, 
            2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 0, 11, 2, 2, 2, 2, 2, 12, 0, 0, 0, 0, 0, 13, 5, 
            5, 5, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 14, 0, 0, 
            0, 0, 0, 0, 15, 1, 16, 0, 0, 0, 0, 0, 0, 0, 17, 18, 18, 18, 19, 0, 0, 0, 0, 0, 0, 
            3, 18, 20, 18, 4, 0, 0, 0, 0, 0, 0, 21, 18, 18, 18, 22, 0, 0, 0, 0, 0, 0, 0, 23, 5, 
            24, 0, 0, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 25, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0
        };

        palettes[2] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=low,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=low,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diamond_block",
            "minecraft:sea_lantern",
            "minecraft:diorite_wall[east=low,north=low,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=none]",
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
            "minecraft:end_rod[facing=up]"
        });
        datas[2] = new byte[]{
            0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 1, 0, 0, 0, 3, 2, 
            2, 2, 2, 2, 2, 2, 4, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 2, 2, 2, 2, 2, 
            2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 
            2, 4, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 
            0, 5, 2, 2, 2, 2, 2, 5, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 1, 1, 1, 6, 0, 0, 0, 0, 0, 7, 2, 2, 2, 2, 
            2, 8, 0, 0, 0, 9, 2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 3, 2, 2, 11, 11, 11, 2, 2, 4, 
            0, 0, 3, 2, 2, 11, 12, 11, 2, 2, 4, 0, 0, 3, 2, 2, 11, 11, 11, 2, 2, 4, 0, 0, 9, 
            2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 0, 13, 2, 2, 2, 2, 2, 14, 0, 0, 0, 0, 0, 15, 5, 
            5, 5, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 16, 0, 0, 
            0, 0, 0, 0, 17, 1, 18, 0, 0, 0, 0, 0, 0, 0, 19, 20, 12, 20, 21, 0, 0, 0, 0, 0, 0, 
            3, 12, 22, 12, 4, 0, 0, 0, 0, 0, 0, 23, 20, 12, 20, 24, 0, 0, 0, 0, 0, 0, 0, 25, 5, 
            26, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 27, 0, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0
        };

        palettes[3] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=low,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=low,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diamond_block",
            "minecraft:diorite_wall[east=low,north=low,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:sea_lantern",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:beacon",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:end_rod[facing=up]"
        });
        datas[3] = new byte[]{
            0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 1, 0, 0, 0, 3, 2, 
            2, 2, 2, 2, 2, 2, 4, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 2, 2, 2, 2, 2, 
            2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 
            2, 4, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 
            0, 5, 2, 2, 2, 2, 2, 5, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 1, 1, 1, 6, 0, 0, 0, 0, 0, 7, 2, 2, 2, 2, 
            2, 8, 0, 0, 0, 9, 2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 3, 2, 2, 11, 11, 11, 2, 2, 4, 
            0, 0, 3, 2, 2, 11, 11, 11, 2, 2, 4, 0, 0, 3, 2, 2, 11, 11, 11, 2, 2, 4, 0, 0, 9, 
            2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 0, 12, 2, 2, 2, 2, 2, 13, 0, 0, 0, 0, 0, 14, 5, 
            5, 5, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 15, 0, 0, 
            0, 0, 0, 0, 16, 1, 17, 0, 0, 0, 0, 0, 0, 0, 18, 19, 19, 19, 20, 0, 0, 0, 0, 0, 0, 
            3, 19, 21, 19, 4, 0, 0, 0, 0, 0, 0, 22, 19, 19, 19, 23, 0, 0, 0, 0, 0, 0, 0, 24, 5, 
            25, 0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 26, 0, 0, 0, 0, 0, 26, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 26, 0, 0, 0, 0, 0, 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0
        };

        palettes[4] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=low,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=low,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diamond_block",
            "minecraft:chiseled_quartz_block",
            "minecraft:diorite_wall[east=low,north=low,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:sea_lantern",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:end_rod[facing=up]"
        });
        datas[4] = new byte[]{
            0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 1, 0, 0, 0, 3, 2, 
            2, 2, 2, 2, 2, 2, 4, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 2, 2, 2, 2, 2, 
            2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 
            2, 4, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 
            0, 5, 2, 2, 2, 2, 2, 5, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 1, 1, 1, 6, 0, 0, 0, 0, 0, 7, 2, 2, 2, 2, 
            2, 8, 0, 0, 0, 9, 2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 3, 2, 2, 11, 11, 11, 2, 2, 4, 
            0, 0, 3, 2, 2, 11, 12, 11, 2, 2, 4, 0, 0, 3, 2, 2, 11, 11, 11, 2, 2, 4, 0, 0, 9, 
            2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 0, 13, 2, 2, 2, 2, 2, 14, 0, 0, 0, 0, 0, 15, 5, 
            5, 5, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 16, 0, 0, 
            0, 0, 0, 0, 17, 1, 18, 0, 0, 0, 0, 0, 0, 0, 19, 20, 20, 20, 21, 0, 0, 0, 0, 0, 0, 
            3, 20, 12, 20, 4, 0, 0, 0, 0, 0, 0, 22, 20, 20, 20, 23, 0, 0, 0, 0, 0, 0, 0, 24, 5, 
            25, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 26, 0, 0, 0, 0, 0, 26, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 26, 0, 0, 0, 0, 0, 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0
        };

        palettes[5] = createPalette(new String[]{
            "minecraft:air",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:calcite",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=low,north=none,south=low,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=low,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=low,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diamond_block",
            "minecraft:chiseled_quartz_block",
            "minecraft:diorite_wall[east=low,north=low,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=low]",
            "minecraft:diorite_wall[east=none,north=low,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:diorite_wall[east=none,north=none,south=none,up=true,waterlogged=false,west=none]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:sea_lantern",
            "minecraft:quartz_pillar[axis=y]",
            "minecraft:smooth_quartz_stairs[facing=south,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=north,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=east,half=bottom,shape=outer_left,waterlogged=false]",
            "minecraft:smooth_quartz_stairs[facing=west,half=bottom,shape=outer_right,waterlogged=false]",
            "minecraft:end_rod[facing=up]"
        });
        datas[5] = new byte[]{
            0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 1, 0, 0, 0, 3, 2, 
            2, 2, 2, 2, 2, 2, 4, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 3, 2, 2, 2, 2, 2, 
            2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 
            2, 4, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 
            0, 5, 2, 2, 2, 2, 2, 5, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 1, 1, 1, 6, 0, 0, 0, 0, 0, 7, 2, 2, 2, 2, 
            2, 8, 0, 0, 0, 9, 2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 3, 2, 2, 11, 11, 11, 2, 2, 4, 
            0, 0, 3, 2, 2, 11, 12, 11, 2, 2, 4, 0, 0, 3, 2, 2, 11, 11, 11, 2, 2, 4, 0, 0, 9, 
            2, 2, 2, 2, 2, 2, 2, 10, 0, 0, 0, 13, 2, 2, 2, 2, 2, 14, 0, 0, 0, 0, 0, 15, 5, 
            5, 5, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 16, 0, 0, 
            0, 0, 0, 0, 17, 1, 18, 0, 0, 0, 0, 0, 0, 0, 19, 20, 21, 20, 22, 0, 0, 0, 0, 0, 0, 
            3, 21, 12, 21, 4, 0, 0, 0, 0, 0, 0, 23, 20, 21, 20, 24, 0, 0, 0, 0, 0, 0, 0, 25, 5, 
            26, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 27, 0, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0
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

