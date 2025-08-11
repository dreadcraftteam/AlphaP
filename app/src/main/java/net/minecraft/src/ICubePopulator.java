package net.minecraft.src;

import java.util.Random;

public interface ICubePopulator {
	boolean canGenerateSurface(int i1);

	ChunkCube GenerateSurface(int i1, int i2, int i3);

	boolean canGenerateNether(int i1);

	ChunkCube GenerateNether(int i1, int i2, int i3);

	boolean canGenerateSky(int i1);

	ChunkCube GenerateSky(int i1, int i2, int i3);

	void PopulateSurface(World world1, Random random2, int i3, int i4, int i5);

	void PopulateNether(World world1, Random random2, int i3, int i4, int i5);

	void PopulateSky(World world1, Random random2, int i3, int i4, int i5);
}
