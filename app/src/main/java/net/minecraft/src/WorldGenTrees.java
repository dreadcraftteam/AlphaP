package net.minecraft.src;

import java.util.Random;

public class WorldGenTrees extends WorldGenerator {
	public boolean generate(World world, Random random, int i, int j, int k) {
		int l = random.nextInt(3) + 4;
		boolean flag = true;

		int j1;
		int k2;
		int i3;
		int k3;
		for(j1 = j; j1 <= j + 1 + l; ++j1) {
			byte l1 = 1;
			if(j1 == j) {
				l1 = 0;
			}

			if(j1 >= j + 1 + l - 2) {
				l1 = 2;
			}

			for(k2 = i - l1; k2 <= i + l1 && flag; ++k2) {
				for(i3 = k - l1; i3 <= k + l1 && flag; ++i3) {
					k3 = world.getBlockId(k2, j1, i3);
					if(k3 != 0 && k3 != Block.leaves.blockID) {
						flag = false;
					}
				}
			}
		}

		if(!flag) {
			return false;
		} else {
			j1 = world.getBlockId(i, j - 1, k);
			if(j1 != Block.grass.blockID && j1 != Block.dirt.blockID) {
				return false;
			} else {
				world.setBlock(i, j - 1, k, Block.dirt.blockID);

				int i16;
				for(i16 = j - 3 + l; i16 <= j + l; ++i16) {
					k2 = i16 - (j + l);
					i3 = 1 - k2 / 2;

					for(k3 = i - i3; k3 <= i + i3; ++k3) {
						int l3 = k3 - i;

						for(int i4 = k - i3; i4 <= k + i3; ++i4) {
							int j4 = i4 - k;
							if((Math.abs(l3) != i3 || Math.abs(j4) != i3 || random.nextInt(2) != 0 && k2 != 0) && !Block.opaqueCubeLookup[world.getBlockId(k3, i16, i4)]) {
								world.setBlock(k3, i16, i4, Block.leaves.blockID);
							}
						}
					}
				}

				for(i16 = 0; i16 < l; ++i16) {
					k2 = world.getBlockId(i, j + i16, k);
					if(k2 == 0 || k2 == Block.leaves.blockID) {
						world.setBlock(i, j + i16, k, Block.wood.blockID);
					}
				}

				return true;
			}
		}
	}
}
