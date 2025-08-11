package net.minecraft_server.src;

import java.util.ArrayList;
import java.util.Random;

public class BlockPistonExtension extends Block {
	private int field_31046_a = -1;

	public BlockPistonExtension(int i, int j) {
		super(i, j, Material.piston);
		this.setStepSound(soundStoneFootstep);
		this.setHardness(0.5F);
	}

	public void onBlockRemoval(World world, int i, int j, int k) {
		super.onBlockRemoval(world, i, j, k);
		int l = world.getBlockMetadata(i, j, k);
		int j1 = PistonBlockTextures.field_31052_a[func_31045_b(l)];
		i += PistonBlockTextures.offsetsXForSide[j1];
		j += PistonBlockTextures.offsetsYForSide[j1];
		k += PistonBlockTextures.offsetsZForSide[j1];
		int k1 = world.getBlockId(i, j, k);
		if (k1 == Block.pistonBase.blockID || k1 == Block.pistonStickyBase.blockID) {
			int i1 = world.getBlockMetadata(i, j, k);
			if (BlockPistonBase.isExtended(i1)) {
				Block.blocksList[k1].dropBlockAsItem(world, i, j, k, i1);
				world.setBlockWithNotify(i, j, k, 0);
			}
		}

	}

	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		int k = func_31045_b(j);
		return i == k
				? (this.field_31046_a >= 0 ? this.field_31046_a
						: ((j & 8) != 0 ? this.blockIndexInTexture - 1 : this.blockIndexInTexture))
				: (i != PistonBlockTextures.field_31052_a[k] ? 108 : 107);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean isACube() {
		return false;
	}

	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		return false;
	}

	public boolean canPlaceBlockOnSide(World world, int i, int j, int k, int l) {
		return false;
	}

	public int quantityDropped(Random random) {
		return 0;
	}

	public void getCollidingBoundingBoxes(World world, int i, int j, int k, AxisAlignedBB axisalignedbb,
			ArrayList arraylist) {
		int l = world.getBlockMetadata(i, j, k);
		switch (func_31045_b(l)) {
			case 0:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
				this.setBlockBounds(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
				break;
			case 1:
				this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
				this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
				break;
			case 2:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
				this.setBlockBounds(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
				break;
			case 3:
				this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
				this.setBlockBounds(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
				break;
			case 4:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
				this.setBlockBounds(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
				break;
			case 5:
				this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
				this.setBlockBounds(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
				super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
		}

		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		int l = iblockaccess.getBlockMetadata(i, j, k);
		switch (func_31045_b(l)) {
			case 0:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
				break;
			case 1:
				this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
				break;
			case 2:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
				break;
			case 3:
				this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
				break;
			case 4:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
				break;
			case 5:
				this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}

	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		int i1 = func_31045_b(world.getBlockMetadata(i, j, k));
		int j1 = world.getBlockId(i - PistonBlockTextures.offsetsXForSide[i1],
				j - PistonBlockTextures.offsetsYForSide[i1], k - PistonBlockTextures.offsetsZForSide[i1]);
		if (j1 != Block.pistonBase.blockID && j1 != Block.pistonStickyBase.blockID) {
			world.setBlockWithNotify(i, j, k, 0);
		} else {
			Block.blocksList[j1].onNeighborBlockChange(world, i - PistonBlockTextures.offsetsXForSide[i1],
					j - PistonBlockTextures.offsetsYForSide[i1], k - PistonBlockTextures.offsetsZForSide[i1], l);
		}

	}

	public static int func_31045_b(int i) {
		return i & 7;
	}
}
