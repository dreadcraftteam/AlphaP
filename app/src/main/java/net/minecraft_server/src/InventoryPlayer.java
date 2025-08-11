package net.minecraft_server.src;

public class InventoryPlayer implements IInventory {
	public ItemStack[] mainInventory = new ItemStack[36];
	public ItemStack[] armorInventory = new ItemStack[4];
	public int currentItem = 0;
	public EntityPlayer player;
	private ItemStack itemStack;
	public boolean inventoryChanged = false;

	public InventoryPlayer(EntityPlayer entityplayer) {
		this.player = entityplayer;
	}

	public ItemStack getCurrentItem() {
		return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
	}

	public static int func_25054_e() {
		return 9;
	}

	private int getInventorySlotContainItem(int i) {
		for (int j = 0; j < this.mainInventory.length; ++j) {
			if (this.mainInventory[j] != null && this.mainInventory[j].itemID == i) {
				return j;
			}
		}

		return -1;
	}

	private int func_21082_c(ItemStack itemstack) {
		for (int i = 0; i < this.mainInventory.length; ++i) {
			if (this.mainInventory[i] != null && this.mainInventory[i].itemID == itemstack.itemID
					&& this.mainInventory[i].func_21132_c()
					&& this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize()
					&& this.mainInventory[i].stackSize < this.getInventoryStackLimit()
					&& (!this.mainInventory[i].getHasSubtypes()
							|| this.mainInventory[i].getItemDamage() == itemstack.getItemDamage())) {
				return i;
			}
		}

		return -1;
	}

	private int getFirstEmptyStack() {
		for (int i = 0; i < this.mainInventory.length; ++i) {
			if (this.mainInventory[i] == null) {
				return i;
			}
		}

		return -1;
	}

	private int func_21083_d(ItemStack itemstack) {
		int i = itemstack.itemID;
		int j = itemstack.stackSize;
		int k = this.func_21082_c(itemstack);
		if (k < 0) {
			k = this.getFirstEmptyStack();
		}

		if (k < 0) {
			return j;
		} else {
			if (this.mainInventory[k] == null) {
				this.mainInventory[k] = new ItemStack(i, 0, itemstack.getItemDamage());
			}

			int l = j;
			if (j > this.mainInventory[k].getMaxStackSize() - this.mainInventory[k].stackSize) {
				l = this.mainInventory[k].getMaxStackSize() - this.mainInventory[k].stackSize;
			}

			if (l > this.getInventoryStackLimit() - this.mainInventory[k].stackSize) {
				l = this.getInventoryStackLimit() - this.mainInventory[k].stackSize;
			}

			if (l == 0) {
				return j;
			} else {
				j -= l;
				this.mainInventory[k].stackSize += l;
				this.mainInventory[k].animationsToGo = 5;
				return j;
			}
		}
	}

	public void decrementAnimations() {
		for (int i = 0; i < this.mainInventory.length; ++i) {
			if (this.mainInventory[i] != null) {
				this.mainInventory[i].func_28143_a(this.player.worldObj, this.player, i, this.currentItem == i);
			}
		}

	}

	public boolean consumeInventoryItem(int i) {
		int j = this.getInventorySlotContainItem(i);
		if (j < 0) {
			return false;
		} else {
			if (--this.mainInventory[j].stackSize <= 0) {
				this.mainInventory[j] = null;
			}

			return true;
		}
	}

	public boolean addItemStackToInventory(ItemStack itemstack) {
		int j;
		if (itemstack.isItemDamaged()) {
			j = this.getFirstEmptyStack();
			if (j >= 0) {
				this.mainInventory[j] = ItemStack.func_20117_a(itemstack);
				this.mainInventory[j].animationsToGo = 5;
				itemstack.stackSize = 0;
				return true;
			} else {
				return false;
			}
		} else {
			do {
				j = itemstack.stackSize;
				itemstack.stackSize = this.func_21083_d(itemstack);
			} while (itemstack.stackSize > 0 && itemstack.stackSize < j);

			return itemstack.stackSize < j;
		}
	}

	public ItemStack decrStackSize(int i, int j) {
		ItemStack[] aitemstack = this.mainInventory;
		if (i >= this.mainInventory.length) {
			aitemstack = this.armorInventory;
			i -= this.mainInventory.length;
		}

		if (aitemstack[i] != null) {
			ItemStack itemstack1;
			if (aitemstack[i].stackSize <= j) {
				itemstack1 = aitemstack[i];
				aitemstack[i] = null;
				return itemstack1;
			} else {
				itemstack1 = aitemstack[i].splitStack(j);
				if (aitemstack[i].stackSize == 0) {
					aitemstack[i] = null;
				}

				return itemstack1;
			}
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		ItemStack[] aitemstack = this.mainInventory;
		if (i >= aitemstack.length) {
			i -= aitemstack.length;
			aitemstack = this.armorInventory;
		}

		aitemstack[i] = itemstack;
	}

	public float getStrVsBlock(Block block) {
		float f = 1.0F;
		if (this.mainInventory[this.currentItem] != null) {
			f *= this.mainInventory[this.currentItem].getStrVsBlock(block);
		}

		return f;
	}

	public NBTTagList writeToNBT(NBTTagList nbttaglist) {
		int j;
		NBTTagCompound nbttagcompound1;
		for (j = 0; j < this.mainInventory.length; ++j) {
			if (this.mainInventory[j] != null) {
				nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) j);
				this.mainInventory[j].writeToNBT(nbttagcompound1);
				nbttaglist.setTag(nbttagcompound1);
			}
		}

		for (j = 0; j < this.armorInventory.length; ++j) {
			if (this.armorInventory[j] != null) {
				nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) (j + 100));
				this.armorInventory[j].writeToNBT(nbttagcompound1);
				nbttaglist.setTag(nbttagcompound1);
			}
		}

		return nbttaglist;
	}

	public void readFromNBT(NBTTagList nbttaglist) {
		this.mainInventory = new ItemStack[36];
		this.armorInventory = new ItemStack[4];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = (NBTTagCompound) nbttaglist.tagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			ItemStack itemstack = new ItemStack(nbttagcompound);
			if (itemstack.getItem() != null) {
				if (j >= 0 && j < this.mainInventory.length) {
					this.mainInventory[j] = itemstack;
				}

				if (j >= 100 && j < this.armorInventory.length + 100) {
					this.armorInventory[j - 100] = itemstack;
				}
			}
		}

	}

	public int getSizeInventory() {
		return this.mainInventory.length + 4;
	}

	public ItemStack getStackInSlot(int i) {
		ItemStack[] aitemstack = this.mainInventory;
		if (i >= aitemstack.length) {
			i -= aitemstack.length;
			aitemstack = this.armorInventory;
		}

		return aitemstack[i];
	}

	public String getInvName() {
		return "Inventory";
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public int getDamageVsEntity(Entity entity) {
		ItemStack itemstack = this.getStackInSlot(this.currentItem);
		return itemstack != null ? itemstack.getDamageVsEntity(entity) : 1;
	}

	public boolean canHarvestBlock(Block block) {
		if (block.blockMaterial.func_31055_i()) {
			return true;
		} else {
			ItemStack itemstack = this.getStackInSlot(this.currentItem);
			return itemstack != null ? itemstack.canHarvestBlock(block) : false;
		}
	}

	public int getTotalArmorValue() {
		int i = 0;
		int j = 0;
		int k = 0;

		for (int l = 0; l < this.armorInventory.length; ++l) {
			if (this.armorInventory[l] != null && this.armorInventory[l].getItem() instanceof ItemArmor) {
				int i1 = this.armorInventory[l].getMaxDamage();
				int j1 = this.armorInventory[l].getItemDamageForDisplay();
				int k1 = i1 - j1;
				j += k1;
				k += i1;
				int l1 = ((ItemArmor) this.armorInventory[l].getItem()).damageReduceAmount;
				i += l1;
			}
		}

		if (k == 0) {
			return 0;
		} else {
			return (i - 1) * j / k + 1;
		}
	}

	public void damageArmor(int i) {
		for (int j = 0; j < this.armorInventory.length; ++j) {
			if (this.armorInventory[j] != null && this.armorInventory[j].getItem() instanceof ItemArmor) {
				this.armorInventory[j].damageItem(i, this.player);
				if (this.armorInventory[j].stackSize == 0) {
					this.armorInventory[j].onItemDestroyedByUse(this.player);
					this.armorInventory[j] = null;
				}
			}
		}

	}

	public void dropAllItems() {
		int j;
		for (j = 0; j < this.mainInventory.length; ++j) {
			if (this.mainInventory[j] != null) {
				this.player.dropPlayerItemWithRandomChoice(this.mainInventory[j], true);
				this.mainInventory[j] = null;
			}
		}

		for (j = 0; j < this.armorInventory.length; ++j) {
			if (this.armorInventory[j] != null) {
				this.player.dropPlayerItemWithRandomChoice(this.armorInventory[j], true);
				this.armorInventory[j] = null;
			}
		}

	}

	public void onInventoryChanged() {
		this.inventoryChanged = true;
	}

	public void setItemStack(ItemStack itemstack) {
		this.itemStack = itemstack;
		this.player.onItemStackChanged(itemstack);
	}

	public ItemStack getItemStack() {
		return this.itemStack;
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return this.player.isDead ? false : entityplayer.getDistanceSqToEntity(this.player) <= 64.0D;
	}

	public boolean func_28010_c(ItemStack itemstack) {
		int j;
		for (j = 0; j < this.armorInventory.length; ++j) {
			if (this.armorInventory[j] != null && this.armorInventory[j].func_28144_c(itemstack)) {
				return true;
			}
		}

		for (j = 0; j < this.mainInventory.length; ++j) {
			if (this.mainInventory[j] != null && this.mainInventory[j].func_28144_c(itemstack)) {
				return true;
			}
		}

		return false;
	}
}
