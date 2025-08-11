package net.minecraft.src;

import java.util.Random;

public class BlockFlowing extends BlockFluid {
	int numAdjacentSources = 0;
	boolean[] isOptimalFlowDirection = new boolean[4];
	int[] flowCost = new int[4];

	protected BlockFlowing(int i, Material material) {
		super(i, material);
	}

	private void func_30003_j(World world, int i, int j, int k) {
		int l = world.getBlockMetadata(i, j, k);
		world.setBlockAndMetadata(i, j, k, this.blockID + 1, l);
		world.markBlocksDirty(i, j, k, i, j, k);
		world.markBlockNeedsUpdate(i, j, k);
	}

	public void updateTick(World world, int i, int j, int k, Random random) {
		int l = this.getFlowDecay(world, i, j, k);
		byte byte0 = 1;
		if(this.blockMaterial == Material.lava && !world.worldProvider.isHellWorld) {
			byte0 = 2;
		}

		boolean flag = true;
		boolean blockBelowExists = world.blockExists(i, j - 1, k);
		if(!blockBelowExists) {
			world.getChunkFromBlockCoords(i, j - 1, k);
			blockBelowExists = world.blockExists(i, j - 1, k);
		}

		int k1;
		if(l > 0) {
			byte aflag = -100;
			this.numAdjacentSources = 0;
			int aflag1 = this.getSmallestFlowDecay(world, i - 1, j, k, aflag);
			aflag1 = this.getSmallestFlowDecay(world, i + 1, j, k, aflag1);
			aflag1 = this.getSmallestFlowDecay(world, i, j, k - 1, aflag1);
			aflag1 = this.getSmallestFlowDecay(world, i, j, k + 1, aflag1);
			k1 = aflag1 + byte0;
			if(k1 >= 8 || aflag1 < 0) {
				k1 = -1;
			}

			if(this.getFlowDecay(world, i, j + 1, k) >= 0) {
				int l1 = this.getFlowDecay(world, i, j + 1, k);
				if(l1 >= 8) {
					k1 = l1;
				} else {
					k1 = l1 + 8;
				}
			}

			if(this.numAdjacentSources >= 2 && this.blockMaterial == Material.water) {
				if(world.getBlockMaterial(i, j - 1, k).isSolid()) {
					k1 = 0;
				} else if(world.getBlockMaterial(i, j - 1, k) == this.blockMaterial && world.getBlockMetadata(i, j, k) == 0) {
					k1 = 0;
				}
			}

			if(this.blockMaterial == Material.lava && l < 8 && k1 < 8 && k1 > l && random.nextInt(4) != 0) {
				k1 = l;
				flag = false;
			}

			if(k1 != l) {
				l = k1;
				if(k1 < 0) {
					world.setBlockWithNotify(i, j, k, 0);
				} else {
					world.setBlockMetadataWithNotify(i, j, k, k1);
					world.scheduleBlockUpdate(i, j, k, this.blockID, this.tickRate());
					world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
				}
			} else if(flag && blockBelowExists) {
				this.func_30003_j(world, i, j, k);
			}
		} else if(blockBelowExists) {
			this.func_30003_j(world, i, j, k);
		}

		if(this.liquidCanDisplaceBlock(world, i, j - 1, k)) {
			if(l >= 8) {
				this.flowIntoBlock(world, i, j - 1, k, l);
			} else {
				this.flowIntoBlock(world, i, j - 1, k, l + 8);
			}
		} else if(l >= 0 && (l == 0 || this.blockBlocksFlow(world, i, j - 1, k))) {
			boolean[] aflag2 = this.getOptimalFlowDirections(world, i, j, k);
			k1 = l + byte0;
			if(l >= 8) {
				k1 = 1;
			}

			if(k1 >= 8) {
				return;
			}

			if(aflag2[0]) {
				this.flowIntoBlock(world, i - 1, j, k, k1);
			}

			if(aflag2[1]) {
				this.flowIntoBlock(world, i + 1, j, k, k1);
			}

			if(aflag2[2]) {
				this.flowIntoBlock(world, i, j, k - 1, k1);
			}

			if(aflag2[3]) {
				this.flowIntoBlock(world, i, j, k + 1, k1);
			}
		}

	}

	private void flowIntoBlock(World world, int i, int j, int k, int l) {
		if(this.liquidCanDisplaceBlock(world, i, j, k)) {
			int i1 = world.getBlockId(i, j, k);
			if(i1 > 0) {
				if(this.blockMaterial == Material.lava) {
					this.triggerLavaMixEffects(world, i, j, k);
				} else {
					Block.blocksList[i1].dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k));
				}
			}

			world.setBlockAndMetadataWithNotify(i, j, k, this.blockID, l);
		}

	}

	private int calculateFlowCost(World world, int i, int j, int k, int l, int i1) {
		int j1 = 1000;

		for(int k1 = 0; k1 < 4; ++k1) {
			if((k1 != 0 || i1 != 1) && (k1 != 1 || i1 != 0) && (k1 != 2 || i1 != 3) && (k1 != 3 || i1 != 2)) {
				int l1 = i;
				int j2 = k;
				if(k1 == 0) {
					l1 = i - 1;
				}

				if(k1 == 1) {
					++l1;
				}

				if(k1 == 2) {
					j2 = k - 1;
				}

				if(k1 == 3) {
					++j2;
				}

				if(!this.blockBlocksFlow(world, l1, j, j2) && (world.getBlockMaterial(l1, j, j2) != this.blockMaterial || world.getBlockMetadata(l1, j, j2) != 0)) {
					if(!this.blockBlocksFlow(world, l1, j - 1, j2)) {
						return l;
					}

					if(l < 4) {
						int k2 = this.calculateFlowCost(world, l1, j, j2, l + 1, k1);
						if(k2 < j1) {
							j1 = k2;
						}
					}
				}
			}
		}

		return j1;
	}

	private boolean[] getOptimalFlowDirections(World world, int i, int j, int k) {
		int i1;
		int l1;
		for(i1 = 0; i1 < 4; ++i1) {
			this.flowCost[i1] = 1000;
			l1 = i;
			int j2 = k;
			if(i1 == 0) {
				l1 = i - 1;
			}

			if(i1 == 1) {
				++l1;
			}

			if(i1 == 2) {
				j2 = k - 1;
			}

			if(i1 == 3) {
				++j2;
			}

			if(!this.blockBlocksFlow(world, l1, j, j2) && (world.getBlockMaterial(l1, j, j2) != this.blockMaterial || world.getBlockMetadata(l1, j, j2) != 0)) {
				if(!this.blockBlocksFlow(world, l1, j - 1, j2)) {
					this.flowCost[i1] = 0;
				} else {
					this.flowCost[i1] = this.calculateFlowCost(world, l1, j, j2, 1, i1);
				}
			}
		}

		i1 = this.flowCost[0];

		for(l1 = 1; l1 < 4; ++l1) {
			if(this.flowCost[l1] < i1) {
				i1 = this.flowCost[l1];
			}
		}

		for(l1 = 0; l1 < 4; ++l1) {
			this.isOptimalFlowDirection[l1] = this.flowCost[l1] == i1;
		}

		return this.isOptimalFlowDirection;
	}

	private boolean blockBlocksFlow(World world, int i, int j, int k) {
		int l = world.getBlockId(i, j, k);
		if(l != Block.doorWood.blockID && l != Block.doorSteel.blockID && l != Block.signPost.blockID && l != Block.ladder.blockID && l != Block.reed.blockID) {
			if(l == 0) {
				return false;
			} else {
				Material material = Block.blocksList[l].blockMaterial;
				return material.getIsSolid();
			}
		} else {
			return true;
		}
	}

	protected int getSmallestFlowDecay(World world, int i, int j, int k, int l) {
		int i1 = this.getFlowDecay(world, i, j, k);
		if(i1 < 0) {
			return l;
		} else {
			if(i1 == 0) {
				++this.numAdjacentSources;
			}

			if(i1 >= 8) {
				i1 = 0;
			}

			return l >= 0 && i1 >= l ? l : i1;
		}
	}

	private boolean liquidCanDisplaceBlock(World world, int i, int j, int k) {
		Material material = world.getBlockMaterial(i, j, k);
		return material == this.blockMaterial ? false : (material == Material.lava ? false : !this.blockBlocksFlow(world, i, j, k));
	}

	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		if(world.getBlockId(i, j, k) == this.blockID) {
			world.scheduleBlockUpdate(i, j, k, this.blockID, this.tickRate());
		}

	}
}
