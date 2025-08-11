package net.minecraft.src;

import java.util.Random;

public class WorldGenMegaTree extends WorldGenerator {

    public World world;
    int height = 32;

    public WorldGenMegaTree() {
        
    }

    public boolean generate(World world1, Random random2, int x, int y, int z) {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                for(int h = 0; h < height; h++) {
                    world.setBlock(x + i, y + h, z + j, Block.wood.blockID);
                }
            }
        }
        return false;
    }

}
