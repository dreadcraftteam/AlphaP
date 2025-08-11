package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class PlayerController {
	protected final Minecraft mc;
	public boolean field_1064_b = false;

	public PlayerController(Minecraft minecraft) {
		this.mc = minecraft;
	}

	public void func_717_a(World world) {
	}

	public void clickBlock(int i, int j, int k, int l) {
		this.mc.theWorld.onBlockHit(this.mc.thePlayer, i, j, k, l);
		this.sendBlockRemoved(i, j, k, l);
	}

	public boolean sendBlockRemoved(int i, int j, int k, int l) {
		World world = this.mc.theWorld;
		Block block = Block.blocksList[world.getBlockId(i, j, k)];
		world.func_28106_e(2001, i, j, k, block.blockID + world.getBlockMetadata(i, j, k) * 256);
		int i1 = world.getBlockMetadata(i, j, k);
		boolean flag = world.setBlockWithNotify(i, j, k, 0);
		if(block != null && flag) {
			block.onBlockDestroyedByPlayer(world, i, j, k, i1);
		}

		return flag;
	}

	public void sendBlockRemoving(int i, int j, int k, int l) {
	}

	public void resetBlockRemoving() {
	}

	public void setPartialTime(float f) {
	}

	public float getBlockReachDistance() {
		return 5.0F;
	}

	public boolean sendUseItem(EntityPlayer entityplayer, World world, ItemStack itemstack) {
		int i = itemstack.stackSize;
		ItemStack itemstack1 = itemstack.useItemRightClick(world, entityplayer);
		if(itemstack1 != itemstack || itemstack1 != null && itemstack1.stackSize != i) {
			entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = itemstack1;
			if(itemstack1.stackSize == 0) {
				entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
			}

			return true;
		} else {
			return false;
		}
	}

	public void flipPlayer(EntityPlayer entityplayer) {
	}

	public void updateController() {
	}

	public boolean shouldDrawHUD() {
		return true;
	}

	public void func_6473_b(EntityPlayer entityplayer) {
	}

	public boolean sendPlaceBlock(EntityPlayer entityplayer, World world, ItemStack itemstack, int i, int j, int k, int l) {
		int id = world.getBlockId(i, j, k);
		return id > 0 && Block.blocksList[id].blockActivated(world, i, j, k, entityplayer) ? true : (itemstack == null ? false : itemstack.useItem(entityplayer, world, i, j, k, l));
	}

	public EntityPlayer createPlayer(World world) {
		return new EntityPlayerSP(this.mc, world, this.mc.session, world.worldProvider.worldType);
	}

	public void interactWithEntity(EntityPlayer entityplayer, Entity entity) {
		entityplayer.useCurrentItemOnEntity(entity);
	}

	public void attackEntity(EntityPlayer entityplayer, Entity entity) {
		entityplayer.attackTargetEntityWithCurrentItem(entity);
	}

	public ItemStack func_27174_a(int i, int j, int k, boolean flag, EntityPlayer entityplayer) {
		return entityplayer.craftingInventory.func_27280_a(j, k, flag, entityplayer);
	}

	public void func_20086_a(int i, EntityPlayer entityplayer) {
		entityplayer.craftingInventory.onCraftGuiClosed(entityplayer);
		entityplayer.craftingInventory = entityplayer.inventorySlots;
	}
}
