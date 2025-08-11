package net.minecraft_server.src;

public class ItemMinecart extends Item {
	public int minecartType;

	public ItemMinecart(int i, int j) {
		super(i);
		this.maxStackSize = 1;
		this.minecartType = j;
	}

	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l) {
		int i1 = world.getBlockId(i, j, k);
		if (BlockRail.func_27030_c(i1)) {
			if (!world.multiplayerWorld) {
				world.entityJoinedWorld(new EntityMinecart(world, (double) ((float) i + 0.5F),
						(double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this.minecartType));
			}

			--itemstack.stackSize;
			return true;
		} else {
			return false;
		}
	}
}
