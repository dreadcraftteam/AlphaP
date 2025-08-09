package net.minecraft.src;

import java.util.ArrayList;

public class BlockFence extends Block {
	public BlockFence(int blockID, int tex) {
		super(blockID, tex, Material.wood);
	}

	public void getCollidingBoundingBoxes(World worldObj, int x, int y, int z, AxisAlignedBB aabb, ArrayList collidingBoundingBoxes) {
		collidingBoundingBoxes.add(AxisAlignedBB.getBoundingBoxFromPool((double)x, (double)y, (double)z, (double)(x + 1), (double)y + 1.5D, (double)(z + 1)));
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return world1.getBlockId(i2, i3 - 1, i4) == this.blockID ? false : (!world1.getBlockMaterial(i2, i3 - 1, i4).isSolid() ? false : super.canPlaceBlockAt(world1, i2, i3, i4));
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 11;
	}
}
