package net.minecraft_server.src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BlockRedstoneWire extends Block {
	private boolean wiresProvidePower = true;
	private Set field_21032_b = new HashSet();

	public BlockRedstoneWire(int i, int j) {
		super(i, j, Material.circuits);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
	}

	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		return this.blockIndexInTexture;
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

	private void updateAndPropagateCurrentStrength(World world, int i, int j, int k) {
		this.func_21031_a(world, i, j, k, i, j, k);
		ArrayList arraylist = new ArrayList(this.field_21032_b);
		this.field_21032_b.clear();

		for (int l = 0; l < arraylist.size(); ++l) {
			ChunkPosition chunkposition = (ChunkPosition) arraylist.get(l);
			world.notifyBlocksOfNeighborChange(chunkposition.x, chunkposition.y, chunkposition.z, this.blockID);
		}

	}

	private void func_21031_a(World world, int i, int j, int k, int l, int i1, int j1) {
		int k1 = world.getBlockMetadata(i, j, k);
		int l1 = 0;
		this.wiresProvidePower = false;
		boolean flag = world.isBlockIndirectlyGettingPowered(i, j, k);
		this.wiresProvidePower = true;
		int j2;
		int l2;
		int j3;
		if (flag) {
			l1 = 15;
		} else {
			for (j2 = 0; j2 < 4; ++j2) {
				l2 = i;
				j3 = k;
				if (j2 == 0) {
					l2 = i - 1;
				}

				if (j2 == 1) {
					++l2;
				}

				if (j2 == 2) {
					j3 = k - 1;
				}

				if (j2 == 3) {
					++j3;
				}

				if (l2 != l || j != i1 || j3 != j1) {
					l1 = this.getMaxCurrentStrength(world, l2, j, j3, l1);
				}

				if (world.isBlockNormalCube(l2, j, j3) && !world.isBlockNormalCube(i, j + 1, k)) {
					if (l2 != l || j + 1 != i1 || j3 != j1) {
						l1 = this.getMaxCurrentStrength(world, l2, j + 1, j3, l1);
					}
				} else if (!world.isBlockNormalCube(l2, j, j3) && (l2 != l || j - 1 != i1 || j3 != j1)) {
					l1 = this.getMaxCurrentStrength(world, l2, j - 1, j3, l1);
				}
			}

			if (l1 > 0) {
				--l1;
			} else {
				l1 = 0;
			}
		}

		if (k1 != l1) {
			world.editingBlocks = true;
			world.setBlockMetadataWithNotify(i, j, k, l1);
			world.markBlocksDirty(i, j, k, i, j, k);
			world.editingBlocks = false;

			for (j2 = 0; j2 < 4; ++j2) {
				l2 = i;
				j3 = k;
				int k3 = j - 1;
				if (j2 == 0) {
					l2 = i - 1;
				}

				if (j2 == 1) {
					++l2;
				}

				if (j2 == 2) {
					j3 = k - 1;
				}

				if (j2 == 3) {
					++j3;
				}

				if (world.isBlockNormalCube(l2, j, j3)) {
					k3 += 2;
				}

				boolean l3 = false;
				int i16 = this.getMaxCurrentStrength(world, l2, j, j3, -1);
				l1 = world.getBlockMetadata(i, j, k);
				if (l1 > 0) {
					--l1;
				}

				if (i16 >= 0 && i16 != l1) {
					this.func_21031_a(world, l2, j, j3, i, j, k);
				}

				i16 = this.getMaxCurrentStrength(world, l2, k3, j3, -1);
				l1 = world.getBlockMetadata(i, j, k);
				if (l1 > 0) {
					--l1;
				}

				if (i16 >= 0 && i16 != l1) {
					this.func_21031_a(world, l2, k3, j3, i, j, k);
				}
			}

			if (k1 == 0 || l1 == 0) {
				this.field_21032_b.add(new ChunkPosition(i, j, k));
				this.field_21032_b.add(new ChunkPosition(i - 1, j, k));
				this.field_21032_b.add(new ChunkPosition(i + 1, j, k));
				this.field_21032_b.add(new ChunkPosition(i, j - 1, k));
				this.field_21032_b.add(new ChunkPosition(i, j + 1, k));
				this.field_21032_b.add(new ChunkPosition(i, j, k - 1));
				this.field_21032_b.add(new ChunkPosition(i, j, k + 1));
			}
		}

	}

	private void notifyWireNeighborsOfNeighborChange(World world, int i, int j, int k) {
		if (world.getBlockId(i, j, k) == this.blockID) {
			world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i - 1, j, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i + 1, j, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j, k - 1, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j, k + 1, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j + 1, k, this.blockID);
		}
	}

	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		if (!world.multiplayerWorld) {
			this.updateAndPropagateCurrentStrength(world, i, j, k);
			world.notifyBlocksOfNeighborChange(i, j + 1, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
			this.notifyWireNeighborsOfNeighborChange(world, i - 1, j, k);
			this.notifyWireNeighborsOfNeighborChange(world, i + 1, j, k);
			this.notifyWireNeighborsOfNeighborChange(world, i, j, k - 1);
			this.notifyWireNeighborsOfNeighborChange(world, i, j, k + 1);
			if (world.isBlockNormalCube(i - 1, j, k)) {
				this.notifyWireNeighborsOfNeighborChange(world, i - 1, j + 1, k);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i - 1, j - 1, k);
			}

			if (world.isBlockNormalCube(i + 1, j, k)) {
				this.notifyWireNeighborsOfNeighborChange(world, i + 1, j + 1, k);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i + 1, j - 1, k);
			}

			if (world.isBlockNormalCube(i, j, k - 1)) {
				this.notifyWireNeighborsOfNeighborChange(world, i, j + 1, k - 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i, j - 1, k - 1);
			}

			if (world.isBlockNormalCube(i, j, k + 1)) {
				this.notifyWireNeighborsOfNeighborChange(world, i, j + 1, k + 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i, j - 1, k + 1);
			}

		}
	}

	public void onBlockRemoval(World world, int i, int j, int k) {
		super.onBlockRemoval(world, i, j, k);
		if (!world.multiplayerWorld) {
			world.notifyBlocksOfNeighborChange(i, j + 1, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
			this.updateAndPropagateCurrentStrength(world, i, j, k);
			this.notifyWireNeighborsOfNeighborChange(world, i - 1, j, k);
			this.notifyWireNeighborsOfNeighborChange(world, i + 1, j, k);
			this.notifyWireNeighborsOfNeighborChange(world, i, j, k - 1);
			this.notifyWireNeighborsOfNeighborChange(world, i, j, k + 1);
			if (world.isBlockNormalCube(i - 1, j, k)) {
				this.notifyWireNeighborsOfNeighborChange(world, i - 1, j + 1, k);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i - 1, j - 1, k);
			}

			if (world.isBlockNormalCube(i + 1, j, k)) {
				this.notifyWireNeighborsOfNeighborChange(world, i + 1, j + 1, k);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i + 1, j - 1, k);
			}

			if (world.isBlockNormalCube(i, j, k - 1)) {
				this.notifyWireNeighborsOfNeighborChange(world, i, j + 1, k - 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i, j - 1, k - 1);
			}

			if (world.isBlockNormalCube(i, j, k + 1)) {
				this.notifyWireNeighborsOfNeighborChange(world, i, j + 1, k + 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i, j - 1, k + 1);
			}

		}
	}

	private int getMaxCurrentStrength(World world, int i, int j, int k, int l) {
		if (world.getBlockId(i, j, k) != this.blockID) {
			return l;
		} else {
			int i1 = world.getBlockMetadata(i, j, k);
			return i1 > l ? i1 : l;
		}
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		if (!world.multiplayerWorld) {
			int i1 = world.getBlockMetadata(i, j, k);
			boolean flag = this.canPlaceBlockAt(world, i, j, k);
			if (!flag) {
				this.dropBlockAsItem(world, i, j, k, i1);
				world.setBlockWithNotify(i, j, k, 0);
			} else {
				this.updateAndPropagateCurrentStrength(world, i, j, k);
			}

			super.onNeighborBlockChange(world, i, j, k, l);
		}
	}

	public int idDropped(int i, Random random) {
		return Item.redstone.shiftedIndex;
	}

	public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
		return !this.wiresProvidePower ? false : this.isPoweringTo(world, i, j, k, l);
	}

	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		if (!this.wiresProvidePower) {
			return false;
		} else if (iblockaccess.getBlockMetadata(i, j, k) == 0) {
			return false;
		} else if (l == 1) {
			return true;
		} else {
			boolean flag = isPowerProviderOrWire(iblockaccess, i - 1, j, k, 1)
					|| !iblockaccess.isBlockNormalCube(i - 1, j, k)
							&& isPowerProviderOrWire(iblockaccess, i - 1, j - 1, k, -1);
			boolean flag1 = isPowerProviderOrWire(iblockaccess, i + 1, j, k, 3)
					|| !iblockaccess.isBlockNormalCube(i + 1, j, k)
							&& isPowerProviderOrWire(iblockaccess, i + 1, j - 1, k, -1);
			boolean flag2 = isPowerProviderOrWire(iblockaccess, i, j, k - 1, 2)
					|| !iblockaccess.isBlockNormalCube(i, j, k - 1)
							&& isPowerProviderOrWire(iblockaccess, i, j - 1, k - 1, -1);
			boolean flag3 = isPowerProviderOrWire(iblockaccess, i, j, k + 1, 0)
					|| !iblockaccess.isBlockNormalCube(i, j, k + 1)
							&& isPowerProviderOrWire(iblockaccess, i, j - 1, k + 1, -1);
			if (!iblockaccess.isBlockNormalCube(i, j + 1, k)) {
				if (iblockaccess.isBlockNormalCube(i - 1, j, k)
						&& isPowerProviderOrWire(iblockaccess, i - 1, j + 1, k, -1)) {
					flag = true;
				}

				if (iblockaccess.isBlockNormalCube(i + 1, j, k)
						&& isPowerProviderOrWire(iblockaccess, i + 1, j + 1, k, -1)) {
					flag1 = true;
				}

				if (iblockaccess.isBlockNormalCube(i, j, k - 1)
						&& isPowerProviderOrWire(iblockaccess, i, j + 1, k - 1, -1)) {
					flag2 = true;
				}

				if (iblockaccess.isBlockNormalCube(i, j, k + 1)
						&& isPowerProviderOrWire(iblockaccess, i, j + 1, k + 1, -1)) {
					flag3 = true;
				}
			}

			return !flag2 && !flag1 && !flag && !flag3 && l >= 2 && l <= 5 ? true
					: (l == 2 && flag2 && !flag && !flag1 ? true
							: (l == 3 && flag3 && !flag && !flag1 ? true
									: (l == 4 && flag && !flag2 && !flag3 ? true
											: l == 5 && flag1 && !flag2 && !flag3)));
		}
	}

	public boolean canProvidePower() {
		return this.wiresProvidePower;
	}

	public static boolean isPowerProviderOrWire(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		int i1 = iblockaccess.getBlockId(i, j, k);
		if (i1 == Block.redstoneWire.blockID) {
			return true;
		} else if (i1 == 0) {
			return false;
		} else if (Block.blocksList[i1].canProvidePower()) {
			return true;
		} else if (i1 != Block.redstoneRepeaterIdle.blockID && i1 != Block.redstoneRepeaterActive.blockID) {
			return false;
		} else {
			int j1 = iblockaccess.getBlockMetadata(i, j, k);
			return l == ModelBed.field_22153_b[j1 & 3];
		}
	}
}
