package net.minecraft_server.src;

import java.util.List;
import java.util.Random;

public class BlockPressurePlate extends Block {
	private EnumMobType triggerMobType;

	protected BlockPressurePlate(int i, int j, EnumMobType enummobtype, Material material) {
		super(i, j, material);
		this.triggerMobType = enummobtype;
		this.setTickOnLoad(true);
		float f = 0.0625F;
		this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.03125F, 1.0F - f);
	}

	public int tickRate() {
		return 20;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		return null;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean isACube() {
		return false;
	}

	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		return world.isBlockNormalCube(i, j - 1, k);
	}

	public void onBlockAdded(World world, int i, int j, int k) {
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		boolean flag = false;
		if (!world.isBlockNormalCube(i, j - 1, k)) {
			flag = true;
		}

		if (flag) {
			this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k));
			world.setBlockWithNotify(i, j, k, 0);
		}

	}

	public void updateTick(World world, int i, int j, int k, Random random) {
		if (!world.multiplayerWorld) {
			if (world.getBlockMetadata(i, j, k) != 0) {
				this.setStateIfMobInteractsWithPlate(world, i, j, k);
			}
		}
	}

	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		if (!world.multiplayerWorld) {
			if (world.getBlockMetadata(i, j, k) != 1) {
				this.setStateIfMobInteractsWithPlate(world, i, j, k);
			}
		}
	}

	private void setStateIfMobInteractsWithPlate(World world, int i, int j, int k) {
		boolean flag = world.getBlockMetadata(i, j, k) == 1;
		boolean flag1 = false;
		float f = 0.125F;
		List list = null;
		if (this.triggerMobType == EnumMobType.everything) {
			list = world.getEntitiesWithinAABBExcludingEntity((Entity) null,
					AxisAlignedBB.getBoundingBoxFromPool((double) ((float) i + f), (double) j, (double) ((float) k + f),
							(double) ((float) (i + 1) - f), (double) j + 0.25D, (double) ((float) (k + 1) - f)));
		}

		if (this.triggerMobType == EnumMobType.mobs) {
			list = world.getEntitiesWithinAABB(EntityLiving.class,
					AxisAlignedBB.getBoundingBoxFromPool((double) ((float) i + f), (double) j, (double) ((float) k + f),
							(double) ((float) (i + 1) - f), (double) j + 0.25D, (double) ((float) (k + 1) - f)));
		}

		if (this.triggerMobType == EnumMobType.players) {
			list = world.getEntitiesWithinAABB(EntityPlayer.class,
					AxisAlignedBB.getBoundingBoxFromPool((double) ((float) i + f), (double) j, (double) ((float) k + f),
							(double) ((float) (i + 1) - f), (double) j + 0.25D, (double) ((float) (k + 1) - f)));
		}

		if (list.size() > 0) {
			flag1 = true;
		}

		if (flag1 && !flag) {
			world.setBlockMetadataWithNotify(i, j, k, 1);
			world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
			world.markBlocksDirty(i, j, k, i, j, k);
			world.playSoundEffect((double) i + 0.5D, (double) j + 0.1D, (double) k + 0.5D, "random.click", 0.3F, 0.6F);
		}

		if (!flag1 && flag) {
			world.setBlockMetadataWithNotify(i, j, k, 0);
			world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
			world.markBlocksDirty(i, j, k, i, j, k);
			world.playSoundEffect((double) i + 0.5D, (double) j + 0.1D, (double) k + 0.5D, "random.click", 0.3F, 0.5F);
		}

		if (flag1) {
			world.scheduleUpdateTick(i, j, k, this.blockID, this.tickRate());
		}

	}

	public void onBlockRemoval(World world, int i, int j, int k) {
		int l = world.getBlockMetadata(i, j, k);
		if (l > 0) {
			world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
		}

		super.onBlockRemoval(world, i, j, k);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		boolean flag = iblockaccess.getBlockMetadata(i, j, k) == 1;
		float f = 0.0625F;
		if (flag) {
			this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.03125F, 1.0F - f);
		} else {
			this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.0625F, 1.0F - f);
		}

	}

	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return iblockaccess.getBlockMetadata(i, j, k) > 0;
	}

	public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
		return world.getBlockMetadata(i, j, k) == 0 ? false : l == 1;
	}

	public boolean canProvidePower() {
		return true;
	}

	public int getMobilityFlag() {
		return 1;
	}
}
