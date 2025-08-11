package net.minecraft_server.src;

public class ItemHoe extends Item {
	public ItemHoe(int i, EnumToolMaterial enumtoolmaterial) {
		super(i);
		this.maxStackSize = 1;
		this.setMaxDamage(enumtoolmaterial.getMaxUses());
	}

	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l) {
		int i1 = world.getBlockId(i, j, k);
		int j1 = world.getBlockId(i, j + 1, k);
		if ((l == 0 || j1 != 0 || i1 != Block.grass.blockID) && i1 != Block.dirt.blockID) {
			return false;
		} else {
			Block block = Block.tilledField;
			world.playSoundEffect((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F),
					block.stepSound.func_737_c(), (block.stepSound.getVolume() + 1.0F) / 2.0F,
					block.stepSound.getPitch() * 0.8F);
			if (world.multiplayerWorld) {
				return true;
			} else {
				world.setBlockWithNotify(i, j, k, block.blockID);
				itemstack.damageItem(1, entityplayer);
				return true;
			}
		}
	}
}
