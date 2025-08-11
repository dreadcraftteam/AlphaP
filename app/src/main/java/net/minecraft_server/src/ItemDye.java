package net.minecraft_server.src;

public class ItemDye extends Item {
	public static final String[] bk = new String[] { "black", "red", "green", "brown", "blue", "purple", "cyan",
			"silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white" };
	public static final int[] field_31023_bk = new int[] { 1973019, 11743532, 3887386, 5320730, 2437522, 8073150,
			2651799, 2651799, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320 };

	public ItemDye(int i) {
		super(i);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l) {
		if (itemstack.getItemDamage() == 15) {
			int i1 = world.getBlockId(i, j, k);
			if (i1 == Block.sapling.blockID) {
				if (!world.multiplayerWorld) {
					((BlockSapling) Block.sapling).growTree(world, i, j, k, world.rand);
					--itemstack.stackSize;
				}

				return true;
			}

			if (i1 == Block.crops.blockID) {
				if (!world.multiplayerWorld) {
					((BlockCrops) Block.crops).fertilize(world, i, j, k);
					--itemstack.stackSize;
				}

				return true;
			}

			if (i1 == Block.grass.blockID) {
				if (!world.multiplayerWorld) {
					--itemstack.stackSize;

					label54: for (int j1 = 0; j1 < 128; ++j1) {
						int k1 = i;
						int l1 = j + 1;
						int i2 = k;

						for (int j2 = 0; j2 < j1 / 16; ++j2) {
							k1 += itemRand.nextInt(3) - 1;
							l1 += (itemRand.nextInt(3) - 1) * itemRand.nextInt(3) / 2;
							i2 += itemRand.nextInt(3) - 1;
							if (world.getBlockId(k1, l1 - 1, i2) != Block.grass.blockID
									|| world.isBlockNormalCube(k1, l1, i2)) {
								continue label54;
							}
						}

						if (world.getBlockId(k1, l1, i2) == 0) {
							if (itemRand.nextInt(10) != 0) {
								world.setBlockAndMetadataWithNotify(k1, l1, i2, Block.tallGrass.blockID, 1);
							} else if (itemRand.nextInt(3) != 0) {
								world.setBlockWithNotify(k1, l1, i2, Block.plantYellow.blockID);
							} else {
								world.setBlockWithNotify(k1, l1, i2, Block.plantRed.blockID);
							}
						}
					}
				}

				return true;
			}
		}

		return false;
	}

	public void saddleEntity(ItemStack itemstack, EntityLiving entityliving) {
		if (entityliving instanceof EntitySheep) {
			EntitySheep entitysheep = (EntitySheep) entityliving;
			int i = BlockCloth.func_21033_c(itemstack.getItemDamage());
			if (!entitysheep.func_21069_f_() && entitysheep.getFleeceColor() != i) {
				entitysheep.setFleeceColor(i);
				--itemstack.stackSize;
			}
		}

	}
}
