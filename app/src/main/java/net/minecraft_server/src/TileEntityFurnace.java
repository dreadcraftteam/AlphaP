package net.minecraft_server.src;

public class TileEntityFurnace extends TileEntity implements IInventory {
	private ItemStack[] furnaceItemStacks = new ItemStack[3];
	public int furnaceBurnTime = 0;
	public int currentItemBurnTime = 0;
	public int furnaceCookTime = 0;

	public int getSizeInventory() {
		return this.furnaceItemStacks.length;
	}

	public ItemStack getStackInSlot(int i) {
		return this.furnaceItemStacks[i];
	}

	public ItemStack decrStackSize(int i, int j) {
		if (this.furnaceItemStacks[i] != null) {
			ItemStack itemstack1;
			if (this.furnaceItemStacks[i].stackSize <= j) {
				itemstack1 = this.furnaceItemStacks[i];
				this.furnaceItemStacks[i] = null;
				return itemstack1;
			} else {
				itemstack1 = this.furnaceItemStacks[i].splitStack(j);
				if (this.furnaceItemStacks[i].stackSize == 0) {
					this.furnaceItemStacks[i] = null;
				}

				return itemstack1;
			}
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.furnaceItemStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

	}

	public String getInvName() {
		return "Furnace";
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
		this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.furnaceItemStacks.length) {
				this.furnaceItemStacks[byte0] = new ItemStack(nbttagcompound1);
			}
		}

		this.furnaceBurnTime = nbttagcompound.getShort("BurnTime");
		this.furnaceCookTime = nbttagcompound.getShort("CookTime");
		this.currentItemBurnTime = this.getItemBurnTime(this.furnaceItemStacks[1]);
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setShort("BurnTime", (short) this.furnaceBurnTime);
		nbttagcompound.setShort("CookTime", (short) this.furnaceCookTime);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.furnaceItemStacks.length; ++i) {
			if (this.furnaceItemStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.furnaceItemStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.setTag(nbttagcompound1);
			}
		}

		nbttagcompound.setTag("Items", nbttaglist);
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isBurning() {
		return this.furnaceBurnTime > 0;
	}

	public void updateEntity() {
		boolean flag = this.furnaceBurnTime > 0;
		boolean flag1 = false;
		if (this.furnaceBurnTime > 0) {
			--this.furnaceBurnTime;
		}

		if (!this.worldObj.multiplayerWorld) {
			if (this.furnaceBurnTime == 0 && this.canSmelt()) {
				this.currentItemBurnTime = this.furnaceBurnTime = this.getItemBurnTime(this.furnaceItemStacks[1]);
				if (this.furnaceBurnTime > 0) {
					flag1 = true;
					if (this.furnaceItemStacks[1] != null) {
						--this.furnaceItemStacks[1].stackSize;
						if (this.furnaceItemStacks[1].stackSize == 0) {
							this.furnaceItemStacks[1] = null;
						}
					}
				}
			}

			if (this.isBurning() && this.canSmelt()) {
				++this.furnaceCookTime;
				if (this.furnaceCookTime == 200) {
					this.furnaceCookTime = 0;
					this.smeltItem();
					flag1 = true;
				}
			} else {
				this.furnaceCookTime = 0;
			}

			if (flag != this.furnaceBurnTime > 0) {
				flag1 = true;
				BlockFurnace.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord,
						this.zCoord);
			}
		}

		if (flag1) {
			this.onInventoryChanged();
		}

	}

	private boolean canSmelt() {
		if (this.furnaceItemStacks[0] == null) {
			return false;
		} else {
			ItemStack itemstack = FurnaceRecipes.smelting()
					.getSmeltingResult(this.furnaceItemStacks[0].getItem().shiftedIndex);
			return itemstack == null ? false
					: (this.furnaceItemStacks[2] == null ? true
							: (!this.furnaceItemStacks[2].isItemEqual(itemstack) ? false
									: (this.furnaceItemStacks[2].stackSize < this.getInventoryStackLimit()
											&& this.furnaceItemStacks[2].stackSize < this.furnaceItemStacks[2]
													.getMaxStackSize() ? true
															: this.furnaceItemStacks[2].stackSize < itemstack
																	.getMaxStackSize())));
		}
	}

	public void smeltItem() {
		if (this.canSmelt()) {
			ItemStack itemstack = FurnaceRecipes.smelting()
					.getSmeltingResult(this.furnaceItemStacks[0].getItem().shiftedIndex);
			if (this.furnaceItemStacks[2] == null) {
				this.furnaceItemStacks[2] = itemstack.copy();
			} else if (this.furnaceItemStacks[2].itemID == itemstack.itemID) {
				++this.furnaceItemStacks[2].stackSize;
			}

			--this.furnaceItemStacks[0].stackSize;
			if (this.furnaceItemStacks[0].stackSize <= 0) {
				this.furnaceItemStacks[0] = null;
			}

		}
	}

	private int getItemBurnTime(ItemStack itemstack) {
		if (itemstack == null) {
			return 0;
		} else {
			int i = itemstack.getItem().shiftedIndex;
			return i < 256 && Block.blocksList[i].blockMaterial == Material.wood ? 300
					: (i == Item.stick.shiftedIndex ? 100
							: (i == Item.coal.shiftedIndex ? 1600
									: (i == Item.bucketLava.shiftedIndex ? 20000
											: (i != Block.sapling.blockID ? 0 : 100))));
		}
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false
				: entityplayer.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D,
						(double) this.zCoord + 0.5D) <= 64.0D;
	}
}
