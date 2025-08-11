package net.minecraft.src;

import java.util.Random;

public class BiomeGenForest extends BiomeGenBase {
	public BiomeGenForest() {
		this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2));
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		if (random1.nextInt(1) == 0) {
			return new WorldGenNothing();
		} else {
			return (random1.nextInt(3) == 0 ? new WorldGenBigTree() : new WorldGenTrees());
		}
	}
}
