package net.minecraft_server.src;

public final class ItemStack {
	public int stackSize;
	public int animationsToGo;
	public int itemID;
	private int itemDamage;

	public ItemStack(Block block) {
		this((Block) block, 1);
	}

	public ItemStack(Block block, int i) {
		this(block.blockID, i, 0);
	}

	public ItemStack(Block block, int i, int j) {
		this(block.blockID, i, j);
	}

	public ItemStack(Item item) {
		this(item.shiftedIndex, 1, 0);
	}

	public ItemStack(Item item, int i) {
		this(item.shiftedIndex, i, 0);
	}

	public ItemStack(Item item, int i, int j) {
		this(item.shiftedIndex, i, j);
	}

	public ItemStack(int i, int j, int k) {
		this.stackSize = 0;
		this.itemID = i;
		this.stackSize = j;
		this.itemDamage = k;
	}

	public ItemStack(NBTTagCompound nbttagcompound) {
		this.stackSize = 0;
		this.readFromNBT(nbttagcompound);
	}

	public ItemStack splitStack(int i) {
		this.stackSize -= i;
		return new ItemStack(this.itemID, i, this.itemDamage);
	}

	public Item getItem() {
		return Item.itemsList[this.itemID];
	}

	public boolean useItem(EntityPlayer entityplayer, World world, int i, int j, int k, int l) {
		boolean flag = this.getItem().onItemUse(this, entityplayer, world, i, j, k, l);

		return flag;
	}

	public float getStrVsBlock(Block block) {
		return this.getItem().getStrVsBlock(this, block);
	}

	public ItemStack useItemRightClick(World world, EntityPlayer entityplayer) {
		return this.getItem().onItemRightClick(this, world, entityplayer);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("id", (short) this.itemID);
		nbttagcompound.setByte("Count", (byte) this.stackSize);
		nbttagcompound.setShort("Damage", (short) this.itemDamage);
		return nbttagcompound;
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		this.itemID = nbttagcompound.getShort("id");
		this.stackSize = nbttagcompound.getByte("Count");
		this.itemDamage = nbttagcompound.getShort("Damage");
	}

	public int getMaxStackSize() {
		return this.getItem().getItemStackLimit();
	}

	public boolean func_21132_c() {
		return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
	}

	public boolean isItemStackDamageable() {
		return Item.itemsList[this.itemID].getMaxDamage() > 0;
	}

	public boolean getHasSubtypes() {
		return Item.itemsList[this.itemID].getHasSubtypes();
	}

	public boolean isItemDamaged() {
		return this.isItemStackDamageable() && this.itemDamage > 0;
	}

	public int getItemDamageForDisplay() {
		return this.itemDamage;
	}

	public int getItemDamage() {
		return this.itemDamage;
	}

	public void setItemDamage(int i) {
		this.itemDamage = i;
	}

	public int getMaxDamage() {
		return Item.itemsList[this.itemID].getMaxDamage();
	}

	public void damageItem(int i, Entity entity) {
		if (this.isItemStackDamageable()) {
			this.itemDamage += i;
			if (this.itemDamage > this.getMaxDamage()) {
				--this.stackSize;
				if (this.stackSize < 0) {
					this.stackSize = 0;
				}

				this.itemDamage = 0;
			}

		}
	}

	public void hitEntity(EntityLiving entityliving, EntityPlayer entityplayer) {
		boolean flag = Item.itemsList[this.itemID].hitEntity(this, entityliving, entityplayer);
	}

	public void func_25124_a(int i, int j, int k, int l, EntityPlayer entityplayer) {
		boolean flag = Item.itemsList[this.itemID].func_25007_a(this, i, j, k, l, entityplayer);
	}

	public int getDamageVsEntity(Entity entity) {
		return Item.itemsList[this.itemID].getDamageVsEntity(entity);
	}

	public boolean canHarvestBlock(Block block) {
		return Item.itemsList[this.itemID].canHarvestBlock(block);
	}

	public void onItemDestroyedByUse(EntityPlayer entityplayer) {
	}

	public void useItemOnEntity(EntityLiving entityliving) {
		Item.itemsList[this.itemID].saddleEntity(this, entityliving);
	}

	public ItemStack copy() {
		return new ItemStack(this.itemID, this.stackSize, this.itemDamage);
	}

	public static boolean areItemStacksEqual(ItemStack itemstack, ItemStack itemstack1) {
		return itemstack == null && itemstack1 == null ? true
				: (itemstack != null && itemstack1 != null ? itemstack.isItemStackEqual(itemstack1) : false);
	}

	private boolean isItemStackEqual(ItemStack itemstack) {
		return this.stackSize != itemstack.stackSize ? false
				: (this.itemID != itemstack.itemID ? false : this.itemDamage == itemstack.itemDamage);
	}

	public boolean isItemEqual(ItemStack itemstack) {
		return this.itemID == itemstack.itemID && this.itemDamage == itemstack.itemDamage;
	}

	public static ItemStack func_20117_a(ItemStack itemstack) {
		return itemstack != null ? itemstack.copy() : null;
	}

	public String toString() {
		return this.stackSize + "x" + Item.itemsList[this.itemID].getItemName() + "@" + this.itemDamage;
	}

	public void func_28143_a(World world, Entity entity, int i, boolean flag) {
		if (this.animationsToGo > 0) {
			--this.animationsToGo;
		}

		Item.itemsList[this.itemID].func_28018_a(this, world, entity, i, flag);
	}

	public void func_28142_b(World world, EntityPlayer entityplayer) {
		Item.itemsList[this.itemID].func_28020_c(this, world, entityplayer);
	}

	public boolean func_28144_c(ItemStack itemstack) {
		return this.itemID == itemstack.itemID && this.stackSize == itemstack.stackSize
				&& this.itemDamage == itemstack.itemDamage;
	}
}
