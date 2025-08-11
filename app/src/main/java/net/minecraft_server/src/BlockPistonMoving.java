package net.minecraft_server.src;

import java.util.Random;

public class BlockPistonMoving extends BlockContainer {
	public BlockPistonMoving(int i) {
		super(i, Material.piston);
		this.setHardness(-1.0F);
	}

	protected TileEntity getBlockEntity() {
		return null;
	}

	public void onBlockAdded(World world, int i, int j, int k) {
	}

	public void onBlockRemoval(World world, int i, int j, int k) {
		TileEntity tileentity = world.getBlockTileEntity(i, j, k);
		if (tileentity != null && tileentity instanceof TileEntityPiston) {
			((TileEntityPiston) tileentity).clearPistonTileEntity();
		} else {
			super.onBlockRemoval(world, i, j, k);
		}

	}

	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		return false;
	}

	public boolean canPlaceBlockOnSide(World world, int i, int j, int k, int l) {
		return false;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean isACube() {
		return false;
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		if (!world.multiplayerWorld && world.getBlockTileEntity(i, j, k) == null) {
			world.setBlockWithNotify(i, j, k, 0);
			return true;
		} else {
			return false;
		}
	}

	public int idDropped(int i, Random random) {
		return 0;
	}

	public void dropBlockAsItemWithChance(World world, int i, int j, int k, int l, float f) {
		if (!world.multiplayerWorld) {
			TileEntityPiston tileentitypiston = this.getTileEntityAtLocation(world, i, j, k);
			if (tileentitypiston != null) {
				Block.blocksList[tileentitypiston.getStoredBlockID()].dropBlockAsItem(world, i, j, k,
						tileentitypiston.func_31005_e());
			}
		}
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		if (!world.multiplayerWorld && world.getBlockTileEntity(i, j, k) != null) {
			;
		}

	}

	public static TileEntity getTileEntity(int i, int j, int k, boolean flag, boolean flag1) {
		return new TileEntityPiston(i, j, k, flag, flag1);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		TileEntityPiston tileentitypiston = this.getTileEntityAtLocation(world, i, j, k);
		if (tileentitypiston == null) {
			return null;
		} else {
			float f = tileentitypiston.func_31007_a(0.0F);
			if (tileentitypiston.func_31010_c()) {
				f = 1.0F - f;
			}

			return this.func_31032_a(world, i, j, k, tileentitypiston.getStoredBlockID(), f,
					tileentitypiston.func_31008_d());
		}
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		TileEntityPiston tileentitypiston = this.getTileEntityAtLocation(iblockaccess, i, j, k);
		if (tileentitypiston != null) {
			Block block = Block.blocksList[tileentitypiston.getStoredBlockID()];
			if (block == null || block == this) {
				return;
			}

			block.setBlockBoundsBasedOnState(iblockaccess, i, j, k);
			float f = tileentitypiston.func_31007_a(0.0F);
			if (tileentitypiston.func_31010_c()) {
				f = 1.0F - f;
			}

			int l = tileentitypiston.func_31008_d();
			this.minX = block.minX - (double) ((float) PistonBlockTextures.offsetsXForSide[l] * f);
			this.minY = block.minY - (double) ((float) PistonBlockTextures.offsetsYForSide[l] * f);
			this.minZ = block.minZ - (double) ((float) PistonBlockTextures.offsetsZForSide[l] * f);
			this.maxX = block.maxX - (double) ((float) PistonBlockTextures.offsetsXForSide[l] * f);
			this.maxY = block.maxY - (double) ((float) PistonBlockTextures.offsetsYForSide[l] * f);
			this.maxZ = block.maxZ - (double) ((float) PistonBlockTextures.offsetsZForSide[l] * f);
		}

	}

	public AxisAlignedBB func_31032_a(World world, int i, int j, int k, int l, float f, int i1) {
		if (l != 0 && l != this.blockID) {
			AxisAlignedBB axisalignedbb = Block.blocksList[l].getCollisionBoundingBoxFromPool(world, i, j, k);
			if (axisalignedbb == null) {
				return null;
			} else {
				axisalignedbb.minX -= (double) ((float) PistonBlockTextures.offsetsXForSide[i1] * f);
				axisalignedbb.maxX -= (double) ((float) PistonBlockTextures.offsetsXForSide[i1] * f);
				axisalignedbb.minY -= (double) ((float) PistonBlockTextures.offsetsYForSide[i1] * f);
				axisalignedbb.maxY -= (double) ((float) PistonBlockTextures.offsetsYForSide[i1] * f);
				axisalignedbb.minZ -= (double) ((float) PistonBlockTextures.offsetsZForSide[i1] * f);
				axisalignedbb.maxZ -= (double) ((float) PistonBlockTextures.offsetsZForSide[i1] * f);
				return axisalignedbb;
			}
		} else {
			return null;
		}
	}

	private TileEntityPiston getTileEntityAtLocation(IBlockAccess iblockaccess, int i, int j, int k) {
		TileEntity tileentity = iblockaccess.getBlockTileEntity(i, j, k);
		return tileentity != null && tileentity instanceof TileEntityPiston ? (TileEntityPiston) tileentity : null;
	}
}
