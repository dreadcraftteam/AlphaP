package net.minecraft_server.src;

import java.util.Random;

public class BlockDispenser extends BlockContainer {
	private Random field_28032_a = new Random();

	protected BlockDispenser(int i) {
		super(i, Material.rock);
		this.blockIndexInTexture = 45;
	}

	public int tickRate() {
		return 4;
	}

	public int idDropped(int i, Random random) {
		return Block.dispenser.blockID;
	}

	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		this.setDispenserDefaultDirection(world, i, j, k);
	}

	private void setDispenserDefaultDirection(World world, int i, int j, int k) {
		if (!world.multiplayerWorld) {
			int l = world.getBlockId(i, j, k - 1);
			int i1 = world.getBlockId(i, j, k + 1);
			int j1 = world.getBlockId(i - 1, j, k);
			int k1 = world.getBlockId(i + 1, j, k);
			byte byte0 = 3;
			if (Block.opaqueCubeLookup[l] && !Block.opaqueCubeLookup[i1]) {
				byte0 = 3;
			}

			if (Block.opaqueCubeLookup[i1] && !Block.opaqueCubeLookup[l]) {
				byte0 = 2;
			}

			if (Block.opaqueCubeLookup[j1] && !Block.opaqueCubeLookup[k1]) {
				byte0 = 5;
			}

			if (Block.opaqueCubeLookup[k1] && !Block.opaqueCubeLookup[j1]) {
				byte0 = 4;
			}

			world.setBlockMetadataWithNotify(i, j, k, byte0);
		}
	}

	public int getBlockTextureFromSide(int i) {
		return i == 1 ? this.blockIndexInTexture + 17
				: (i == 0 ? this.blockIndexInTexture + 17
						: (i == 3 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture));
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		if (world.multiplayerWorld) {
			return true;
		} else {
			TileEntityDispenser tileentitydispenser = (TileEntityDispenser) world.getBlockTileEntity(i, j, k);
			entityplayer.displayGUIDispenser(tileentitydispenser);
			return true;
		}
	}

	private void dispenseItem(World world, int i, int j, int k, Random random) {
		int l = world.getBlockMetadata(i, j, k);
		byte i1 = 0;
		byte j1 = 0;
		if (l == 3) {
			j1 = 1;
		} else if (l == 2) {
			j1 = -1;
		} else if (l == 5) {
			i1 = 1;
		} else {
			i1 = -1;
		}

		TileEntityDispenser tileentitydispenser = (TileEntityDispenser) world.getBlockTileEntity(i, j, k);
		ItemStack itemstack = tileentitydispenser.getRandomStackFromInventory();
		double d = (double) i + (double) i1 * 0.6D + 0.5D;
		double d1 = (double) j + 0.5D;
		double d2 = (double) k + (double) j1 * 0.6D + 0.5D;
		if (itemstack == null) {
			world.func_28097_e(1001, i, j, k, 0);
		} else {
			if (itemstack.itemID == Item.arrow.shiftedIndex) {
				EntityArrow entityitem = new EntityArrow(world, d, d1, d2);
				entityitem.setArrowHeading((double) i1, (double) 0.1F, (double) j1, 1.1F, 6.0F);
				entityitem.field_28012_a = true;
				world.entityJoinedWorld(entityitem);
				world.func_28097_e(1002, i, j, k, 0);
			} else if (itemstack.itemID == Item.egg.shiftedIndex) {
				EntityEgg entityitem1 = new EntityEgg(world, d, d1, d2);
				entityitem1.func_20078_a((double) i1, (double) 0.1F, (double) j1, 1.1F, 6.0F);
				world.entityJoinedWorld(entityitem1);
				world.func_28097_e(1002, i, j, k, 0);
			} else if (itemstack.itemID == Item.snowball.shiftedIndex) {
				EntitySnowball entityitem2 = new EntitySnowball(world, d, d1, d2);
				entityitem2.func_6141_a((double) i1, (double) 0.1F, (double) j1, 1.1F, 6.0F);
				world.entityJoinedWorld(entityitem2);
				world.func_28097_e(1002, i, j, k, 0);
			} else {
				EntityItem entityitem3 = new EntityItem(world, d, d1 - 0.3D, d2, itemstack);
				double d3 = random.nextDouble() * 0.1D + 0.2D;
				entityitem3.motionX = (double) i1 * d3;
				entityitem3.motionY = (double) 0.2F;
				entityitem3.motionZ = (double) j1 * d3;
				entityitem3.motionX += random.nextGaussian() * (double) 0.0075F * 6.0D;
				entityitem3.motionY += random.nextGaussian() * (double) 0.0075F * 6.0D;
				entityitem3.motionZ += random.nextGaussian() * (double) 0.0075F * 6.0D;
				world.entityJoinedWorld(entityitem3);
				world.func_28097_e(1000, i, j, k, 0);
			}

			world.func_28097_e(2000, i, j, k, i1 + 1 + (j1 + 1) * 3);
		}

	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		if (l > 0 && Block.blocksList[l].canProvidePower()) {
			boolean flag = world.isBlockIndirectlyGettingPowered(i, j, k)
					|| world.isBlockIndirectlyGettingPowered(i, j + 1, k);
			if (flag) {
				world.scheduleUpdateTick(i, j, k, this.blockID, this.tickRate());
			}
		}

	}

	public void updateTick(World world, int i, int j, int k, Random random) {
		if (world.isBlockIndirectlyGettingPowered(i, j, k) || world.isBlockIndirectlyGettingPowered(i, j + 1, k)) {
			this.dispenseItem(world, i, j, k, random);
		}

	}

	protected TileEntity getBlockEntity() {
		return new TileEntityDispenser();
	}

	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
		int l = MathHelper.floor_double((double) (entityliving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if (l == 0) {
			world.setBlockMetadataWithNotify(i, j, k, 2);
		}

		if (l == 1) {
			world.setBlockMetadataWithNotify(i, j, k, 5);
		}

		if (l == 2) {
			world.setBlockMetadataWithNotify(i, j, k, 3);
		}

		if (l == 3) {
			world.setBlockMetadataWithNotify(i, j, k, 4);
		}

	}

	public void onBlockRemoval(World world, int i, int j, int k) {
		TileEntityDispenser tileentitydispenser = (TileEntityDispenser) world.getBlockTileEntity(i, j, k);

		for (int l = 0; l < tileentitydispenser.getSizeInventory(); ++l) {
			ItemStack itemstack = tileentitydispenser.getStackInSlot(l);
			if (itemstack != null) {
				float f = this.field_28032_a.nextFloat() * 0.8F + 0.1F;
				float f1 = this.field_28032_a.nextFloat() * 0.8F + 0.1F;
				float f2 = this.field_28032_a.nextFloat() * 0.8F + 0.1F;

				while (itemstack.stackSize > 0) {
					int i1 = this.field_28032_a.nextInt(21) + 10;
					if (i1 > itemstack.stackSize) {
						i1 = itemstack.stackSize;
					}

					itemstack.stackSize -= i1;
					EntityItem entityitem = new EntityItem(world, (double) ((float) i + f), (double) ((float) j + f1),
							(double) ((float) k + f2), new ItemStack(itemstack.itemID, i1, itemstack.getItemDamage()));
					float f3 = 0.05F;
					entityitem.motionX = (double) ((float) this.field_28032_a.nextGaussian() * f3);
					entityitem.motionY = (double) ((float) this.field_28032_a.nextGaussian() * f3 + 0.2F);
					entityitem.motionZ = (double) ((float) this.field_28032_a.nextGaussian() * f3);
					world.entityJoinedWorld(entityitem);
				}
			}
		}

		super.onBlockRemoval(world, i, j, k);
	}
}
