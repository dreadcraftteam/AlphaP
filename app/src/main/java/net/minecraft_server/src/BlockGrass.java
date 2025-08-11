package net.minecraft_server.src;

import java.util.Random;

public class BlockGrass extends Block {
	protected BlockGrass(int i) {
		super(i, Material.grass);
		this.blockIndexInTexture = 3;
		this.setTickOnLoad(true);
	}

	public void updateTick(World world, int i, int j, int k, Random random) {
		if (!world.multiplayerWorld) {
			if (world.getBlockLightValue(i, j + 1, k) < 4 && Block.lightOpacity[world.getBlockId(i, j + 1, k)] > 2) {
				if (random.nextInt(4) != 0) {
					return;
				}

				world.setBlockWithNotify(i, j, k, Block.dirt.blockID);
			} else if (world.getBlockLightValue(i, j + 1, k) >= 9) {
				int l = i + random.nextInt(3) - 1;
				int i1 = j + random.nextInt(5) - 3;
				int j1 = k + random.nextInt(3) - 1;
				int k1 = world.getBlockId(l, i1 + 1, j1);
				if (world.getBlockId(l, i1, j1) == Block.dirt.blockID && world.getBlockLightValue(l, i1 + 1, j1) >= 4
						&& Block.lightOpacity[k1] <= 2) {
					world.setBlockWithNotify(l, i1, j1, Block.grass.blockID);
				}
			}

		}
	}

	public int idDropped(int i, Random random) {
		return Block.dirt.idDropped(0, random);
	}
}
