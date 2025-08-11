package net.minecraft_server.src;

public class ItemRecord extends Item {
	public final String recordName;

	protected ItemRecord(int i, String s) {
		super(i);
		this.recordName = s;
		this.maxStackSize = 1;
	}

	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l) {
		if (world.getBlockId(i, j, k) == Block.jukebox.blockID && world.getBlockMetadata(i, j, k) == 0) {
			if (world.multiplayerWorld) {
				return true;
			} else {
				((BlockJukeBox) Block.jukebox).ejectRecord(world, i, j, k, this.shiftedIndex);
				world.func_28101_a((EntityPlayer) null, 1005, i, j, k, this.shiftedIndex);
				--itemstack.stackSize;
				return true;
			}
		} else {
			return false;
		}
	}
}
