package net.minecraft.src;

import java.util.Random;

public class BlockMushroom extends BlockFlower {
	protected BlockMushroom(int i, int j) {
		super(i, j);
		float f = 0.2F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
		this.setTickOnLoad(true);
	}

	public void updateTick(World world, int i, int j, int k, Random random) {
		if(random.nextInt(100) == 0) {
			int l = i + random.nextInt(3) - 1;
			int i1 = j + random.nextInt(2) - random.nextInt(2);
			int j1 = k + random.nextInt(3) - 1;
			if(world.isAirBlock(l, i1, j1) && this.canBlockStay(world, l, i1, j1)) {
				int i10000 = i + (random.nextInt(3) - 1);
				i10000 = k + (random.nextInt(3) - 1);
				if(world.isAirBlock(l, i1, j1) && this.canBlockStay(world, l, i1, j1)) {
					world.setBlockWithNotify(l, i1, j1, this.blockID);
				}
			}
		}

	}

	protected boolean canThisPlantGrowOnThisBlockID(int i) {
		return Block.opaqueCubeLookup[i];
	}

	public boolean canBlockStay(World world, int i, int j, int k) {
		return world.getFullBlockLightValue(i, j, k) < 13 && this.canThisPlantGrowOnThisBlockID(world.getBlockId(i, j - 1, k));
	}
}
