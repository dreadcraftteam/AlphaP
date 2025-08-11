package net.minecraft_server.src;

public class ItemFishingRod extends Item {
	public ItemFishingRod(int i) {
		super(i);
		this.setMaxDamage(64);
		this.setMaxStackSize(1);
	}

	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (entityplayer.fishEntity != null) {
			int i = entityplayer.fishEntity.catchFish();
			itemstack.damageItem(i, entityplayer);
			entityplayer.swingItem();
		} else {
			world.playSoundAtEntity(entityplayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			if (!world.multiplayerWorld) {
				world.entityJoinedWorld(new EntityFish(world, entityplayer));
			}

			entityplayer.swingItem();
		}

		return itemstack;
	}
}
