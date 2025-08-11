package net.minecraft_server.src;

import java.util.List;
import java.util.Random;

public class BlockDetectorRail extends BlockRail {
	public BlockDetectorRail(int i, int j) {
		super(i, j, true);
		this.setTickOnLoad(true);
	}

	public int tickRate() {
		return 20;
	}

	public boolean canProvidePower() {
		return true;
	}

	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		if (!world.multiplayerWorld) {
			int l = world.getBlockMetadata(i, j, k);
			if ((l & 8) == 0) {
				this.func_27035_f(world, i, j, k, l);
			}
		}
	}

	public void updateTick(World world, int i, int j, int k, Random random) {
		if (!world.multiplayerWorld) {
			int l = world.getBlockMetadata(i, j, k);
			if ((l & 8) != 0) {
				this.func_27035_f(world, i, j, k, l);
			}
		}
	}

	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return (iblockaccess.getBlockMetadata(i, j, k) & 8) != 0;
	}

	public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
		return (world.getBlockMetadata(i, j, k) & 8) == 0 ? false : l == 1;
	}

	private void func_27035_f(World world, int i, int j, int k, int l) {
		boolean flag = (l & 8) != 0;
		boolean flag1 = false;
		float f = 0.125F;
		List list = world.getEntitiesWithinAABB(EntityMinecart.class,
				AxisAlignedBB.getBoundingBoxFromPool((double) ((float) i + f), (double) j, (double) ((float) k + f),
						(double) ((float) (i + 1) - f), (double) j + 0.25D, (double) ((float) (k + 1) - f)));
		if (list.size() > 0) {
			flag1 = true;
		}

		if (flag1 && !flag) {
			world.setBlockMetadataWithNotify(i, j, k, l | 8);
			world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
			world.markBlocksDirty(i, j, k, i, j, k);
		}

		if (!flag1 && flag) {
			world.setBlockMetadataWithNotify(i, j, k, l & 7);
			world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
			world.markBlocksDirty(i, j, k, i, j, k);
		}

		if (flag1) {
			world.scheduleUpdateTick(i, j, k, this.blockID, this.tickRate());
		}

	}
}
