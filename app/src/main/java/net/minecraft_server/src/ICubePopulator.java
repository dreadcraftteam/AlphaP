package net.minecraft_server.src;

import java.util.Random;

public interface ICubePopulator {
	void GenerateSurface(World world1, Random random2, int i3, int i4, int i5);

	void GenerateNether(World world1, Random random2, int i3, int i4, int i5);

	void GenerateSky(World world1, Random random2, int i3, int i4, int i5);
}
