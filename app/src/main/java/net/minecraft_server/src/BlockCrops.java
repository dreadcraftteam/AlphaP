package net.minecraft_server.src;

import java.util.Random;

public class BlockCrops extends BlockFlower {
	protected BlockCrops(int i, int j) {
		super(i, j);
		this.blockIndexInTexture = j;
		this.setTickOnLoad(true);
		float f = 0.5F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
	}

	protected boolean canThisPlantGrowOnThisBlockID(int i) {
		return i == Block.tilledField.blockID;
	}

	public void updateTick(World world, int i, int j, int k, Random random) {
		super.updateTick(world, i, j, k, random);
		if (world.getBlockLightValue(i, j + 1, k) >= 9) {
			int l = world.getBlockMetadata(i, j, k);
			if (l < 7) {
				float f = this.getGrowthRate(world, i, j, k);
				if (random.nextInt((int) (100.0F / f)) == 0) {
					++l;
					world.setBlockMetadataWithNotify(i, j, k, l);
				}
			}
		}

	}

	public void fertilize(World world, int i, int j, int k) {
		world.setBlockMetadataWithNotify(i, j, k, 7);
	}

	private float getGrowthRate(World world, int i, int j, int k) {
		float f = 1.0F;
		int l = world.getBlockId(i, j, k - 1);
		int i1 = world.getBlockId(i, j, k + 1);
		int j1 = world.getBlockId(i - 1, j, k);
		int k1 = world.getBlockId(i + 1, j, k);
		int l1 = world.getBlockId(i - 1, j, k - 1);
		int i2 = world.getBlockId(i + 1, j, k - 1);
		int j2 = world.getBlockId(i + 1, j, k + 1);
		int k2 = world.getBlockId(i - 1, j, k + 1);
		boolean flag = j1 == this.blockID || k1 == this.blockID;
		boolean flag1 = l == this.blockID || i1 == this.blockID;
		boolean flag2 = l1 == this.blockID || i2 == this.blockID || j2 == this.blockID || k2 == this.blockID;

		for (int l2 = i - 1; l2 <= i + 1; ++l2) {
			for (int i3 = k - 1; i3 <= k + 1; ++i3) {
				int j3 = world.getBlockId(l2, j - 1, i3);
				float f1 = 0.0F;
				if (j3 == Block.tilledField.blockID) {
					f1 = 1.0F;
					if (world.getBlockMetadata(l2, j - 1, i3) > 0) {
						f1 = 3.0F;
					}
				}

				if (l2 != i || i3 != k) {
					f1 /= 4.0F;
				}

				f += f1;
			}
		}

		if (flag2 || flag && flag1) {
			f /= 2.0F;
		}

		return f;
	}

	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		if (j < 0) {
			j = 7;
		}

		return this.blockIndexInTexture + j;
	}

	public void dropBlockAsItemWithChance(World world, int i, int j, int k, int l, float f) {
		super.dropBlockAsItemWithChance(world, i, j, k, l, f);
		if (!world.multiplayerWorld) {
			for (int i1 = 0; i1 < 3; ++i1) {
				if (world.rand.nextInt(15) <= l) {
					float f1 = 0.7F;
					float f2 = world.rand.nextFloat() * f1 + (1.0F - f1) * 0.5F;
					float f3 = world.rand.nextFloat() * f1 + (1.0F - f1) * 0.5F;
					float f4 = world.rand.nextFloat() * f1 + (1.0F - f1) * 0.5F;
					EntityItem entityitem = new EntityItem(world, (double) ((float) i + f2), (double) ((float) j + f3),
							(double) ((float) k + f4), new ItemStack(Item.seeds));
					entityitem.delayBeforeCanPickup = 10;
					world.entityJoinedWorld(entityitem);
				}
			}

		}
	}

	public int idDropped(int i, Random random) {
		return i == 7 ? Item.wheat.shiftedIndex : -1;
	}

	public int quantityDropped(Random random) {
		return 1;
	}
}
