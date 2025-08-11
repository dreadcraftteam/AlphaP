package net.minecraft.src;

import java.util.Random;

public abstract class CubePopulatorModBase extends BaseMod implements ICubePopulator {
	public boolean canGenerateSurface(int yChunk) {
		return false;
	}

	public ChunkCube GenerateSurface(int x, int y, int z) {
		return null;
	}

	public boolean canGenerateNether(int yChunk) {
		return false;
	}

	public ChunkCube GenerateNether(int x, int y, int z) {
		return null;
	}

	public boolean canGenerateSky(int yChunk) {
		return false;
	}

	public ChunkCube GenerateSky(int x, int y, int z) {
		return null;
	}

	public void PopulateSurface(World world, Random rand, int x, int y, int z) {
	}

	public void PopulateNether(World world, Random rand, int x, int y, int z) {
	}

	public void PopulateSky(World world, Random rand, int x, int y, int z) {
	}
}
