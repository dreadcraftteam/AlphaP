package net.minecraft_server.src;

import java.util.Random;

public class BlockTNT extends Block {
	public BlockTNT(int i, int j) {
		super(i, j, Material.tnt);
	}

	public int getBlockTextureFromSide(int i) {
		return i == 0 ? this.blockIndexInTexture + 2
				: (i == 1 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture);
	}

	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		if (world.isBlockIndirectlyGettingPowered(i, j, k)) {
			this.onBlockDestroyedByPlayer(world, i, j, k, 1);
			world.setBlockWithNotify(i, j, k, 0);
		}

	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		if (l > 0 && Block.blocksList[l].canProvidePower() && world.isBlockIndirectlyGettingPowered(i, j, k)) {
			this.onBlockDestroyedByPlayer(world, i, j, k, 1);
			world.setBlockWithNotify(i, j, k, 0);
		}

	}

	public int quantityDropped(Random random) {
		return 0;
	}

	public void onBlockDestroyedByExplosion(World world, int i, int j, int k) {
		EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) ((float) i + 0.5F),
				(double) ((float) j + 0.5F), (double) ((float) k + 0.5F));
		entitytntprimed.fuse = world.rand.nextInt(entitytntprimed.fuse / 4) + entitytntprimed.fuse / 8;
		world.entityJoinedWorld(entitytntprimed);
	}

	public void onBlockDestroyedByPlayer(World world, int i, int j, int k, int l) {
		if (!world.multiplayerWorld) {
			if ((l & 1) == 0) {
				this.dropBlockAsItem_do(world, i, j, k, new ItemStack(Block.tnt.blockID, 1, 0));
			} else {
				EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) ((float) i + 0.5F),
						(double) ((float) j + 0.5F), (double) ((float) k + 0.5F));
				world.entityJoinedWorld(entitytntprimed);
				world.playSoundAtEntity(entitytntprimed, "random.fuse", 1.0F, 1.0F);
			}

		}
	}

	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
		if (entityplayer.getCurrentEquippedItem() != null
				&& entityplayer.getCurrentEquippedItem().itemID == Item.flintAndSteel.shiftedIndex) {
			world.setBlockMetadata(i, j, k, 1);
		}

		super.onBlockClicked(world, i, j, k, entityplayer);
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		return super.blockActivated(world, i, j, k, entityplayer);
	}
}
