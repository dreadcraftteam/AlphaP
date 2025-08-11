package net.minecraft_server.src;

public class ItemInWorldManager {
	private WorldServer thisWorld;
	public EntityPlayer thisPlayer;
	private float field_672_d = 0.0F;
	private int field_22055_d;
	private int field_22054_g;
	private int field_22053_h;
	private int field_22052_i;
	private int field_22051_j;
	private boolean field_22050_k;
	private int field_22049_l;
	private int field_22048_m;
	private int field_22047_n;
	private int field_22046_o;

	public ItemInWorldManager(WorldServer worldserver) {
		this.thisWorld = worldserver;
	}

	public void func_328_a() {
		++this.field_22051_j;
		if (this.field_22050_k) {
			int i = this.field_22051_j - this.field_22046_o;
			int j = this.thisWorld.getBlockId(this.field_22049_l, this.field_22048_m, this.field_22047_n);
			if (j != 0) {
				Block block = Block.blocksList[j];
				float f = block.blockStrength(this.thisPlayer) * (float) (i + 1);
				if (f >= 1.0F) {
					this.field_22050_k = false;
					this.func_325_c(this.field_22049_l, this.field_22048_m, this.field_22047_n);
				}
			} else {
				this.field_22050_k = false;
			}
		}

	}

	public void func_324_a(int i, int j, int k, int l) {
		this.thisWorld.func_28096_a((EntityPlayer) null, i, j, k, l);
		this.field_22055_d = this.field_22051_j;
		int i1 = this.thisWorld.getBlockId(i, j, k);
		if (i1 > 0) {
			Block.blocksList[i1].onBlockClicked(this.thisWorld, i, j, k, this.thisPlayer);
		}

		if (i1 > 0 && Block.blocksList[i1].blockStrength(this.thisPlayer) >= 1.0F) {
			this.func_325_c(i, j, k);
		} else {
			this.field_22054_g = i;
			this.field_22053_h = j;
			this.field_22052_i = k;
		}

	}

	public void func_22045_b(int i, int j, int k) {
		if (i == this.field_22054_g && j == this.field_22053_h && k == this.field_22052_i) {
			int l = this.field_22051_j - this.field_22055_d;
			int i1 = this.thisWorld.getBlockId(i, j, k);
			if (i1 != 0) {
				Block block = Block.blocksList[i1];
				float f = block.blockStrength(this.thisPlayer) * (float) (l + 1);
				if (f >= 0.7F) {
					this.func_325_c(i, j, k);
				} else if (!this.field_22050_k) {
					this.field_22050_k = true;
					this.field_22049_l = i;
					this.field_22048_m = j;
					this.field_22047_n = k;
					this.field_22046_o = this.field_22055_d;
				}
			}
		}

		this.field_672_d = 0.0F;
	}

	public boolean removeBlock(int i, int j, int k) {
		Block block = Block.blocksList[this.thisWorld.getBlockId(i, j, k)];
		int l = this.thisWorld.getBlockMetadata(i, j, k);
		boolean flag = this.thisWorld.setBlockWithNotify(i, j, k, 0);
		if (block != null && flag) {
			block.onBlockDestroyedByPlayer(this.thisWorld, i, j, k, l);
		}

		return flag;
	}

	public boolean func_325_c(int i, int j, int k) {
		int l = this.thisWorld.getBlockId(i, j, k);
		int i1 = this.thisWorld.getBlockMetadata(i, j, k);
		this.thisWorld.func_28101_a(this.thisPlayer, 2001, i, j, k, l + this.thisWorld.getBlockMetadata(i, j, k) * 256);
		boolean flag = this.removeBlock(i, j, k);
		ItemStack itemstack = this.thisPlayer.getCurrentEquippedItem();
		if (itemstack != null) {
			itemstack.func_25124_a(l, i, j, k, this.thisPlayer);
			if (itemstack.stackSize == 0) {
				itemstack.onItemDestroyedByUse(this.thisPlayer);
				this.thisPlayer.destroyCurrentEquippedItem();
			}
		}

		if (flag && this.thisPlayer.canHarvestBlock(Block.blocksList[l])) {
			Block.blocksList[l].harvestBlock(this.thisWorld, this.thisPlayer, i, j, k, i1);
			((EntityPlayerMP) this.thisPlayer).playerNetServerHandler
					.sendPacket(new Packet53BlockChange(i, j, k, this.thisWorld));
		}

		return flag;
	}

	public boolean func_6154_a(EntityPlayer entityplayer, World world, ItemStack itemstack) {
		int i = itemstack.stackSize;
		ItemStack itemstack1 = itemstack.useItemRightClick(world, entityplayer);
		if (itemstack1 != itemstack || itemstack1 != null && itemstack1.stackSize != i) {
			entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = itemstack1;
			if (itemstack1.stackSize == 0) {
				entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean activeBlockOrUseItem(EntityPlayer entityplayer, World world, ItemStack itemstack, int i, int j,
			int k, int l) {
		int i1 = world.getBlockId(i, j, k);
		return i1 > 0 && Block.blocksList[i1].blockActivated(world, i, j, k, entityplayer) ? true
				: (itemstack == null ? false : itemstack.useItem(entityplayer, world, i, j, k, l));
	}
}
