package net.minecraft_server.src;

import java.util.Random;

public class BlockChest extends BlockContainer {
	private Random random = new Random();

	protected BlockChest(int i) {
		super(i, Material.wood);
		this.blockIndexInTexture = 26;
	}

	public int getBlockTextureFromSide(int i) {
		return i == 1 ? this.blockIndexInTexture - 1
				: (i == 0 ? this.blockIndexInTexture - 1
						: (i == 3 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture));
	}

	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		int l = 0;
		if (world.getBlockId(i - 1, j, k) == this.blockID) {
			++l;
		}

		if (world.getBlockId(i + 1, j, k) == this.blockID) {
			++l;
		}

		if (world.getBlockId(i, j, k - 1) == this.blockID) {
			++l;
		}

		if (world.getBlockId(i, j, k + 1) == this.blockID) {
			++l;
		}

		return l > 1 ? false
				: (this.isThereANeighborChest(world, i - 1, j, k) ? false
						: (this.isThereANeighborChest(world, i + 1, j, k) ? false
								: (this.isThereANeighborChest(world, i, j, k - 1) ? false
										: !this.isThereANeighborChest(world, i, j, k + 1))));
	}

	private boolean isThereANeighborChest(World world, int i, int j, int k) {
		return world.getBlockId(i, j, k) != this.blockID ? false
				: (world.getBlockId(i - 1, j, k) == this.blockID ? true
						: (world.getBlockId(i + 1, j, k) == this.blockID ? true
								: (world.getBlockId(i, j, k - 1) == this.blockID ? true
										: world.getBlockId(i, j, k + 1) == this.blockID)));
	}

	public void onBlockRemoval(World world, int i, int j, int k) {
		TileEntityChest tileentitychest = (TileEntityChest) world.getBlockTileEntity(i, j, k);
		if (tileentitychest != null) {
			for (int l = 0; l < tileentitychest.getSizeInventory(); ++l) {
				ItemStack itemstack = tileentitychest.getStackInSlot(l);
				if (itemstack != null) {
					float f = this.random.nextFloat() * 0.8F + 0.1F;
					float f1 = this.random.nextFloat() * 0.8F + 0.1F;
					float f2 = this.random.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0) {
						int i1 = this.random.nextInt(21) + 10;
						if (i1 > itemstack.stackSize) {
							i1 = itemstack.stackSize;
						}

						itemstack.stackSize -= i1;
						EntityItem entityitem = new EntityItem(world, (double) ((float) i + f),
								(double) ((float) j + f1), (double) ((float) k + f2),
								new ItemStack(itemstack.itemID, i1, itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (double) ((float) this.random.nextGaussian() * f3);
						entityitem.motionY = (double) ((float) this.random.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double) ((float) this.random.nextGaussian() * f3);
						world.entityJoinedWorld(entityitem);
					}
				}
			}

			super.onBlockRemoval(world, i, j, k);
		}
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		Object obj = (TileEntityChest) world.getBlockTileEntity(i, j, k);
		if (world.isBlockNormalCube(i, j + 1, k)) {
			return true;
		} else if (world.getBlockId(i - 1, j, k) == this.blockID && world.isBlockNormalCube(i - 1, j + 1, k)) {
			return true;
		} else if (world.getBlockId(i + 1, j, k) == this.blockID && world.isBlockNormalCube(i + 1, j + 1, k)) {
			return true;
		} else if (world.getBlockId(i, j, k - 1) == this.blockID && world.isBlockNormalCube(i, j + 1, k - 1)) {
			return true;
		} else if (world.getBlockId(i, j, k + 1) == this.blockID && world.isBlockNormalCube(i, j + 1, k + 1)) {
			return true;
		} else {
			if (world.getBlockId(i - 1, j, k) == this.blockID) {
				obj = new InventoryLargeChest("Large chest", (TileEntityChest) world.getBlockTileEntity(i - 1, j, k),
						(IInventory) ((IInventory) obj));
			}

			if (world.getBlockId(i + 1, j, k) == this.blockID) {
				obj = new InventoryLargeChest("Large chest", (IInventory) ((IInventory) obj),
						(TileEntityChest) world.getBlockTileEntity(i + 1, j, k));
			}

			if (world.getBlockId(i, j, k - 1) == this.blockID) {
				obj = new InventoryLargeChest("Large chest", (TileEntityChest) world.getBlockTileEntity(i, j, k - 1),
						(IInventory) ((IInventory) obj));
			}

			if (world.getBlockId(i, j, k + 1) == this.blockID) {
				obj = new InventoryLargeChest("Large chest", (IInventory) ((IInventory) obj),
						(TileEntityChest) world.getBlockTileEntity(i, j, k + 1));
			}

			if (world.multiplayerWorld) {
				return true;
			} else {
				entityplayer.displayGUIChest((IInventory) ((IInventory) obj));
				return true;
			}
		}
	}

	protected TileEntity getBlockEntity() {
		return new TileEntityChest();
	}
}
