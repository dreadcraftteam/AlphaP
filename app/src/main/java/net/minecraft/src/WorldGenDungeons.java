package net.minecraft.src;

import java.util.Random;

public class WorldGenDungeons extends WorldGenerator {
	public boolean generate(World world, Random random, int i, int j, int k) {
		byte byte0 = 3;
		int l = random.nextInt(2) + 2;
		int i1 = random.nextInt(2) + 2;
		int j1 = 0;

		int tileentitymobspawner;
		int l2;
		int k3;
		for(tileentitymobspawner = i - l - 1; tileentitymobspawner <= i + l + 1; ++tileentitymobspawner) {
			for(l2 = j - 1; l2 <= j + byte0 + 1; ++l2) {
				for(k3 = k - i1 - 1; k3 <= k + i1 + 1; ++k3) {
					Material l3 = world.getBlockMaterial(tileentitymobspawner, l2, k3);
					if(l2 == j - 1 && !l3.isSolid()) {
						return false;
					}

					if(l2 == j + byte0 + 1 && !l3.isSolid()) {
						return false;
					}

					if((tileentitymobspawner == i - l - 1 || tileentitymobspawner == i + l + 1 || k3 == k - i1 - 1 || k3 == k + i1 + 1) && l2 == j && world.isAirBlock(tileentitymobspawner, l2, k3) && world.isAirBlock(tileentitymobspawner, l2 + 1, k3)) {
						++j1;
					}
				}
			}
		}

		if((j1 < 1 || j1 > 5) && (j > 0 || random.nextInt(8) != 0)) {
			return false;
		} else {
			for(tileentitymobspawner = i - l - 1; tileentitymobspawner <= i + l + 1; ++tileentitymobspawner) {
				for(l2 = j + byte0; l2 >= j - 1; --l2) {
					for(k3 = k - i1 - 1; k3 <= k + i1 + 1; ++k3) {
						if(tileentitymobspawner != i - l - 1 && l2 != j - 1 && k3 != k - i1 - 1 && tileentitymobspawner != i + l + 1 && l2 != j + byte0 + 1 && k3 != k + i1 + 1) {
							world.setBlockWithNotify(tileentitymobspawner, l2, k3, 0);
						} else if(l2 >= 0 && !world.getBlockMaterial(tileentitymobspawner, l2 - 1, k3).isSolid()) {
							world.setBlockWithNotify(tileentitymobspawner, l2, k3, 0);
						} else if(world.getBlockMaterial(tileentitymobspawner, l2, k3).isSolid()) {
							if(l2 == j - 1 && random.nextInt(4) != 0) {
								world.setBlockWithNotify(tileentitymobspawner, l2, k3, Block.cobblestoneMossy.blockID);
							} else {
								world.setBlockWithNotify(tileentitymobspawner, l2, k3, Block.cobblestone.blockID);
							}
						}
					}
				}
			}

			label113:
			for(tileentitymobspawner = 0; tileentitymobspawner < 2; ++tileentitymobspawner) {
				for(l2 = 0; l2 < 3; ++l2) {
					k3 = i + random.nextInt(l * 2 + 1) - l;
					int i4 = k + random.nextInt(i1 * 2 + 1) - i1;
					if(world.isAirBlock(k3, j, i4)) {
						int j4 = 0;
						if(world.getBlockMaterial(k3 - 1, j, i4).isSolid()) {
							++j4;
						}

						if(world.getBlockMaterial(k3 + 1, j, i4).isSolid()) {
							++j4;
						}

						if(world.getBlockMaterial(k3, j, i4 - 1).isSolid()) {
							++j4;
						}

						if(world.getBlockMaterial(k3, j, i4 + 1).isSolid()) {
							++j4;
						}

						if(j4 == 1) {
							world.setBlockWithNotify(k3, j, i4, Block.chest.blockID);
							TileEntityChest tileentitychest = (TileEntityChest)world.getBlockTileEntity(k3, j, i4);
							int k4 = 0;

							while(true) {
								if(k4 >= 8) {
									continue label113;
								}

								ItemStack itemstack = this.pickCheckLootItem(random);
								if(itemstack != null) {
									tileentitychest.setInventorySlotContents(random.nextInt(tileentitychest.getSizeInventory()), itemstack);
								}

								++k4;
							}
						}
					}
				}
			}

			world.setBlockWithNotify(i, j, k, Block.mobSpawner.blockID);
			TileEntityMobSpawner tileEntityMobSpawner19 = (TileEntityMobSpawner)world.getBlockTileEntity(i, j, k);
			tileEntityMobSpawner19.setMobID(this.pickMobSpawner(random));
			return true;
		}
	}

	private ItemStack pickCheckLootItem(Random random) {
		int i = random.nextInt(11);
		return i == 0 ? new ItemStack(Item.saddle) : (i == 1 ? new ItemStack(Item.ingotIron, random.nextInt(4) + 1) : (i == 2 ? new ItemStack(Item.bread) : (i == 3 ? new ItemStack(Item.wheat, random.nextInt(4) + 1) : (i == 4 ? new ItemStack(Item.gunpowder, random.nextInt(4) + 1) : (i == 5 ? new ItemStack(Item.silk, random.nextInt(4) + 1) : (i == 6 ? new ItemStack(Item.bucketEmpty) : (i == 7 && random.nextInt(100) == 0 ? new ItemStack(Item.appleGold) : (i == 8 && random.nextInt(2) == 0 ? new ItemStack(Item.redstone, random.nextInt(4) + 1) : (i == 9 && random.nextInt(10) == 0 ? new ItemStack(Item.itemsList[Item.record13.shiftedIndex + random.nextInt(2)]) : (i == 10 ? new ItemStack(Item.dyePowder, 1, 3) : null))))))))));
	}

	private String pickMobSpawner(Random random) {
		int i = random.nextInt(4);
		return i == 0 ? "Skeleton" : (i == 1 ? "Zombie" : (i == 2 ? "Zombie" : (i == 3 ? "Spider" : "")));
	}
}
