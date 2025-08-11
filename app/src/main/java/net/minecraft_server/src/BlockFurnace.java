package net.minecraft_server.src;

import java.util.Random;

public class BlockFurnace extends BlockContainer {
	private Random field_28033_a = new Random();
	private final boolean isActive;
	private static boolean field_28034_c = false;

	protected BlockFurnace(int i, boolean flag) {
		super(i, Material.rock);
		this.isActive = flag;
		this.blockIndexInTexture = 45;
	}

	public int idDropped(int i, Random random) {
		return Block.stoneOvenIdle.blockID;
	}

	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		this.setDefaultDirection(world, i, j, k);
	}

	private void setDefaultDirection(World world, int i, int j, int k) {
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
						: (i == 3 ? this.blockIndexInTexture - 1 : this.blockIndexInTexture));
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		if (world.multiplayerWorld) {
			return true;
		} else {
			TileEntityFurnace tileentityfurnace = (TileEntityFurnace) world.getBlockTileEntity(i, j, k);
			entityplayer.displayGUIFurnace(tileentityfurnace);
			return true;
		}
	}

	public static void updateFurnaceBlockState(boolean flag, World world, int i, int j, int k) {
		int l = world.getBlockMetadata(i, j, k);
		TileEntity tileentity = world.getBlockTileEntity(i, j, k);
		field_28034_c = true;
		if (flag) {
			world.setBlockWithNotify(i, j, k, Block.stoneOvenActive.blockID);
		} else {
			world.setBlockWithNotify(i, j, k, Block.stoneOvenIdle.blockID);
		}

		field_28034_c = false;
		world.setBlockMetadataWithNotify(i, j, k, l);
		tileentity.validate();
		world.setBlockTileEntity(i, j, k, tileentity);
	}

	protected TileEntity getBlockEntity() {
		return new TileEntityFurnace();
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
		if (!field_28034_c) {
			TileEntityFurnace tileentityfurnace = (TileEntityFurnace) world.getBlockTileEntity(i, j, k);

			for (int l = 0; l < tileentityfurnace.getSizeInventory(); ++l) {
				ItemStack itemstack = tileentityfurnace.getStackInSlot(l);
				if (itemstack != null) {
					float f = this.field_28033_a.nextFloat() * 0.8F + 0.1F;
					float f1 = this.field_28033_a.nextFloat() * 0.8F + 0.1F;
					float f2 = this.field_28033_a.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0) {
						int i1 = this.field_28033_a.nextInt(21) + 10;
						if (i1 > itemstack.stackSize) {
							i1 = itemstack.stackSize;
						}

						itemstack.stackSize -= i1;
						EntityItem entityitem = new EntityItem(world, (double) ((float) i + f),
								(double) ((float) j + f1), (double) ((float) k + f2),
								new ItemStack(itemstack.itemID, i1, itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (double) ((float) this.field_28033_a.nextGaussian() * f3);
						entityitem.motionY = (double) ((float) this.field_28033_a.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double) ((float) this.field_28033_a.nextGaussian() * f3);
						world.entityJoinedWorld(entityitem);
					}
				}
			}
		}

		super.onBlockRemoval(world, i, j, k);
	}
}
