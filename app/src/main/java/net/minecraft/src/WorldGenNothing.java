package net.minecraft.src;

import java.util.Random;

public class WorldGenNothing extends WorldGenerator {
    public boolean generate(World world, Random random, int x, int y, int z) {
        return false; 
    }
}

