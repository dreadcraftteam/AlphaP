package net.minecraft_server.src;

import java.util.Random;

public class BlockSand extends Block {
	public static boolean fallInstantly = false;

	public BlockSand(int i, int j) {
		super(i, j, Material.sand);
	}

	public void onBlockAdded(World world, int i, int j, int k) {
		world.scheduleUpdateTick(i, j, k, this.blockID, this.tickRate());
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		world.scheduleUpdateTick(i, j, k, this.blockID, this.tickRate());
	}

	public void updateTick(World world, int i, int j, int k, Random random) {
		this.tryToFall(world, i, j, k);
	}

	private void tryToFall(World world, int i, int j, int k) {
		if (canFallBelow(world, i, j - 1, k)) {
			byte byte0 = 32;
			if (!fallInstantly
					&& world.checkChunksExist(i - byte0, j - byte0, k - byte0, i + byte0, j + byte0, k + byte0)) {
				EntityFallingSand entityfallingsand = new EntityFallingSand(world, (double) ((float) i + 0.5F),
						(double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this.blockID);
				world.entityJoinedWorld(entityfallingsand);
			} else {
				world.setBlockWithNotify(i, j, k, 0);

				while (canFallBelow(world, i, j - 1, k)) {
					--j;
				}

				world.setBlockWithNotify(i, j, k, this.blockID);
			}
		}

	}

	public int tickRate() {
		return 3;
	}

	public static boolean canFallBelow(World world, int i, int j, int k) {
		int l = world.getBlockId(i, j, k);
		if (l == 0) {
			return true;
		} else if (l == Block.fire.blockID) {
			return true;
		} else {
			Material material = Block.blocksList[l].blockMaterial;
			return material == Material.water ? true : material == Material.lava;
		}
	}
}
