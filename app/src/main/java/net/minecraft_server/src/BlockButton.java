package net.minecraft_server.src;

import java.util.Random;

public class BlockButton extends Block {
	protected BlockButton(int i, int j) {
		super(i, j, Material.circuits);
		this.setTickOnLoad(true);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		return null;
	}

	public int tickRate() {
		return 20;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean isACube() {
		return false;
	}

	public boolean canPlaceBlockOnSide(World world, int i, int j, int k, int l) {
		return l == 2 && world.isBlockNormalCube(i, j, k + 1) ? true
				: (l == 3 && world.isBlockNormalCube(i, j, k - 1) ? true
						: (l == 4 && world.isBlockNormalCube(i + 1, j, k) ? true
								: l == 5 && world.isBlockNormalCube(i - 1, j, k)));
	}

	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		return world.isBlockNormalCube(i - 1, j, k) ? true
				: (world.isBlockNormalCube(i + 1, j, k) ? true
						: (world.isBlockNormalCube(i, j, k - 1) ? true : world.isBlockNormalCube(i, j, k + 1)));
	}

	public void onBlockPlaced(World world, int i, int j, int k, int l) {
		int i1 = world.getBlockMetadata(i, j, k);
		int j1 = i1 & 8;
		i1 &= 7;
		if (l == 2 && world.isBlockNormalCube(i, j, k + 1)) {
			i1 = 4;
		} else if (l == 3 && world.isBlockNormalCube(i, j, k - 1)) {
			i1 = 3;
		} else if (l == 4 && world.isBlockNormalCube(i + 1, j, k)) {
			i1 = 2;
		} else if (l == 5 && world.isBlockNormalCube(i - 1, j, k)) {
			i1 = 1;
		} else {
			i1 = this.getOrientation(world, i, j, k);
		}

		world.setBlockMetadataWithNotify(i, j, k, i1 + j1);
	}

	private int getOrientation(World world, int i, int j, int k) {
		return world.isBlockNormalCube(i - 1, j, k) ? 1
				: (world.isBlockNormalCube(i + 1, j, k) ? 2
						: (world.isBlockNormalCube(i, j, k - 1) ? 3 : (!world.isBlockNormalCube(i, j, k + 1) ? 1 : 4)));
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		if (this.func_322_g(world, i, j, k)) {
			int i1 = world.getBlockMetadata(i, j, k) & 7;
			boolean flag = false;
			if (!world.isBlockNormalCube(i - 1, j, k) && i1 == 1) {
				flag = true;
			}

			if (!world.isBlockNormalCube(i + 1, j, k) && i1 == 2) {
				flag = true;
			}

			if (!world.isBlockNormalCube(i, j, k - 1) && i1 == 3) {
				flag = true;
			}

			if (!world.isBlockNormalCube(i, j, k + 1) && i1 == 4) {
				flag = true;
			}

			if (flag) {
				this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k));
				world.setBlockWithNotify(i, j, k, 0);
			}
		}

	}

	private boolean func_322_g(World world, int i, int j, int k) {
		if (!this.canPlaceBlockAt(world, i, j, k)) {
			this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k));
			world.setBlockWithNotify(i, j, k, 0);
			return false;
		} else {
			return true;
		}
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		int l = iblockaccess.getBlockMetadata(i, j, k);
		int i1 = l & 7;
		boolean flag = (l & 8) > 0;
		float f = 0.375F;
		float f1 = 0.625F;
		float f2 = 0.1875F;
		float f3 = 0.125F;
		if (flag) {
			f3 = 0.0625F;
		}

		if (i1 == 1) {
			this.setBlockBounds(0.0F, f, 0.5F - f2, f3, f1, 0.5F + f2);
		} else if (i1 == 2) {
			this.setBlockBounds(1.0F - f3, f, 0.5F - f2, 1.0F, f1, 0.5F + f2);
		} else if (i1 == 3) {
			this.setBlockBounds(0.5F - f2, f, 0.0F, 0.5F + f2, f1, f3);
		} else if (i1 == 4) {
			this.setBlockBounds(0.5F - f2, f, 1.0F - f3, 0.5F + f2, f1, 1.0F);
		}

	}

	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
		this.blockActivated(world, i, j, k, entityplayer);
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		int l = world.getBlockMetadata(i, j, k);
		int i1 = l & 7;
		int j1 = 8 - (l & 8);
		if (j1 == 0) {
			return true;
		} else {
			world.setBlockMetadataWithNotify(i, j, k, i1 + j1);
			world.markBlocksDirty(i, j, k, i, j, k);
			world.playSoundEffect((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "random.click", 0.3F, 0.6F);
			world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
			if (i1 == 1) {
				world.notifyBlocksOfNeighborChange(i - 1, j, k, this.blockID);
			} else if (i1 == 2) {
				world.notifyBlocksOfNeighborChange(i + 1, j, k, this.blockID);
			} else if (i1 == 3) {
				world.notifyBlocksOfNeighborChange(i, j, k - 1, this.blockID);
			} else if (i1 == 4) {
				world.notifyBlocksOfNeighborChange(i, j, k + 1, this.blockID);
			} else {
				world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
			}

			world.scheduleUpdateTick(i, j, k, this.blockID, this.tickRate());
			return true;
		}
	}

	public void onBlockRemoval(World world, int i, int j, int k) {
		int l = world.getBlockMetadata(i, j, k);
		if ((l & 8) > 0) {
			world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
			int i1 = l & 7;
			if (i1 == 1) {
				world.notifyBlocksOfNeighborChange(i - 1, j, k, this.blockID);
			} else if (i1 == 2) {
				world.notifyBlocksOfNeighborChange(i + 1, j, k, this.blockID);
			} else if (i1 == 3) {
				world.notifyBlocksOfNeighborChange(i, j, k - 1, this.blockID);
			} else if (i1 == 4) {
				world.notifyBlocksOfNeighborChange(i, j, k + 1, this.blockID);
			} else {
				world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
			}
		}

		super.onBlockRemoval(world, i, j, k);
	}

	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return (iblockaccess.getBlockMetadata(i, j, k) & 8) > 0;
	}

	public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
		int i1 = world.getBlockMetadata(i, j, k);
		if ((i1 & 8) == 0) {
			return false;
		} else {
			int j1 = i1 & 7;
			return j1 == 5 && l == 1 ? true
					: (j1 == 4 && l == 2 ? true
							: (j1 == 3 && l == 3 ? true : (j1 == 2 && l == 4 ? true : j1 == 1 && l == 5)));
		}
	}

	public boolean canProvidePower() {
		return true;
	}

	public void updateTick(World world, int i, int j, int k, Random random) {
		if (!world.multiplayerWorld) {
			int l = world.getBlockMetadata(i, j, k);
			if ((l & 8) != 0) {
				world.setBlockMetadataWithNotify(i, j, k, l & 7);
				world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
				int i1 = l & 7;
				if (i1 == 1) {
					world.notifyBlocksOfNeighborChange(i - 1, j, k, this.blockID);
				} else if (i1 == 2) {
					world.notifyBlocksOfNeighborChange(i + 1, j, k, this.blockID);
				} else if (i1 == 3) {
					world.notifyBlocksOfNeighborChange(i, j, k - 1, this.blockID);
				} else if (i1 == 4) {
					world.notifyBlocksOfNeighborChange(i, j, k + 1, this.blockID);
				} else {
					world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
				}

				world.playSoundEffect((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "random.click", 0.3F,
						0.5F);
				world.markBlocksDirty(i, j, k, i, j, k);
			}
		}
	}
}
