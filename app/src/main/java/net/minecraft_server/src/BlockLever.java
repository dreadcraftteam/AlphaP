package net.minecraft_server.src;

public class BlockLever extends Block {
	protected BlockLever(int i, int j) {
		super(i, j, Material.circuits);
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

	public boolean canPlaceBlockOnSide(World world, int i, int j, int k, int l) {
		return l == 1 && world.isBlockNormalCube(i, j - 1, k) ? true
				: (l == 2 && world.isBlockNormalCube(i, j, k + 1) ? true
						: (l == 3 && world.isBlockNormalCube(i, j, k - 1) ? true
								: (l == 4 && world.isBlockNormalCube(i + 1, j, k) ? true
										: l == 5 && world.isBlockNormalCube(i - 1, j, k))));
	}

	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		return world.isBlockNormalCube(i - 1, j, k) ? true
				: (world.isBlockNormalCube(i + 1, j, k) ? true
						: (world.isBlockNormalCube(i, j, k - 1) ? true
								: (world.isBlockNormalCube(i, j, k + 1) ? true
										: world.isBlockNormalCube(i, j - 1, k))));
	}

	public void onBlockPlaced(World world, int i, int j, int k, int l) {
		int i1 = world.getBlockMetadata(i, j, k);
		int j1 = i1 & 8;
		i1 &= 7;
		i1 = -1;
		if (l == 1 && world.isBlockNormalCube(i, j - 1, k)) {
			i1 = 5 + world.rand.nextInt(2);
		}

		if (l == 2 && world.isBlockNormalCube(i, j, k + 1)) {
			i1 = 4;
		}

		if (l == 3 && world.isBlockNormalCube(i, j, k - 1)) {
			i1 = 3;
		}

		if (l == 4 && world.isBlockNormalCube(i + 1, j, k)) {
			i1 = 2;
		}

		if (l == 5 && world.isBlockNormalCube(i - 1, j, k)) {
			i1 = 1;
		}

		if (i1 == -1) {
			this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k));
			world.setBlockWithNotify(i, j, k, 0);
		} else {
			world.setBlockMetadataWithNotify(i, j, k, i1 + j1);
		}
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		if (this.checkIfAttachedToBlock(world, i, j, k)) {
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

			if (!world.isBlockNormalCube(i, j - 1, k) && i1 == 5) {
				flag = true;
			}

			if (!world.isBlockNormalCube(i, j - 1, k) && i1 == 6) {
				flag = true;
			}

			if (flag) {
				this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k));
				world.setBlockWithNotify(i, j, k, 0);
			}
		}

	}

	private boolean checkIfAttachedToBlock(World world, int i, int j, int k) {
		if (!this.canPlaceBlockAt(world, i, j, k)) {
			this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k));
			world.setBlockWithNotify(i, j, k, 0);
			return false;
		} else {
			return true;
		}
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		int l = iblockaccess.getBlockMetadata(i, j, k) & 7;
		float f = 0.1875F;
		if (l == 1) {
			this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
		} else if (l == 2) {
			this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
		} else if (l == 3) {
			this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
		} else if (l == 4) {
			this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
		} else {
			float f1 = 0.25F;
			this.setBlockBounds(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, 0.6F, 0.5F + f1);
		}

	}

	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
		this.blockActivated(world, i, j, k, entityplayer);
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		if (world.multiplayerWorld) {
			return true;
		} else {
			int l = world.getBlockMetadata(i, j, k);
			int i1 = l & 7;
			int j1 = 8 - (l & 8);
			world.setBlockMetadataWithNotify(i, j, k, i1 + j1);
			world.markBlocksDirty(i, j, k, i, j, k);
			world.playSoundEffect((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "random.click", 0.3F,
					j1 <= 0 ? 0.5F : 0.6F);
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
			return j1 == 6 && l == 1 ? true
					: (j1 == 5 && l == 1 ? true
							: (j1 == 4 && l == 2 ? true
									: (j1 == 3 && l == 3 ? true : (j1 == 2 && l == 4 ? true : j1 == 1 && l == 5))));
		}
	}

	public boolean canProvidePower() {
		return true;
	}
}
