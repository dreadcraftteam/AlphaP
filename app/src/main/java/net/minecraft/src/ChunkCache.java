package net.minecraft.src;

public class ChunkCache implements IBlockAccess {
	private int chunkX;
	private int chunkZ;
	private Chunk[][] chunkArray;
	private World worldObj;

	public ChunkCache(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.worldObj = world;
		this.chunkX = x1 >> 4;
		int chunkY = y1 >> 4;
		this.chunkZ = z1 >> 4;
		int chunkX2 = x2 >> 4;
		int chunkY2 = y2 >> 4;
		int chunkZ2 = z2 >> 4;
		this.chunkArray = new Chunk[chunkX2 - this.chunkX + 1][chunkZ2 - this.chunkZ + 1];

		for(int i2 = this.chunkX; i2 <= chunkX2; ++i2) {
			for(int j2 = this.chunkZ; j2 <= chunkZ2; ++j2) {
				this.chunkArray[i2 - this.chunkX][j2 - this.chunkZ] = world.getChunkFromChunkCoords(i2, j2);

				for(int y3 = chunkY; y3 < chunkY2; ++y3) {
					world.getChunkFromChunkCoords(i2, y3, j2);
				}
			}
		}

	}

	public int getBlockId(int x, int y, int z) {
		int offsetX = (x >> 4) - this.chunkX;
		int offsetZ = (z >> 4) - this.chunkZ;
		if(offsetX >= 0 && offsetX < this.chunkArray.length && offsetZ >= 0 && offsetZ < this.chunkArray[offsetX].length) {
			Chunk chunk = this.chunkArray[offsetX][offsetZ];
			return chunk == null ? 0 : chunk.getBlockID(x & 15, y, z & 15);
		} else {
			return 0;
		}
	}

	public TileEntity getBlockTileEntity(int x, int y, int z) {
		int offsetX = (x >> 4) - this.chunkX;
		int offsetZ = (z >> 4) - this.chunkZ;
		return this.chunkArray[offsetX][offsetZ].getChunkBlockTileEntity(x & 15, y, z & 15);
	}

	public float getBrightness(int i, int j, int k, int l) {
		int i1 = this.getLightValue(i, j, k);
		if(i1 < l) {
			i1 = l;
		}

		return this.worldObj.worldProvider.lightBrightnessTable[i1];
	}

	public float getLightBrightness(int i, int j, int k) {
		return this.worldObj.worldProvider.lightBrightnessTable[this.getLightValue(i, j, k)];
	}

	public int getLightValue(int i, int j, int k) {
		return this.getLightValueExt(i, j, k, true);
	}

	public int getLightValueExt(int i, int j, int k, boolean flag) {
		if(i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
			int j1;
			int l1;
			if(flag) {
				j1 = this.getBlockId(i, j, k);
				if(j1 == Block.stairSingle.blockID || j1 == Block.tilledField.blockID || j1 == Block.stairCompactPlanks.blockID || j1 == Block.stairCompactCobblestone.blockID) {
					l1 = this.getLightValueExt(i, j + 1, k, false);
					int i2 = this.getLightValueExt(i + 1, j, k, false);
					int j2 = this.getLightValueExt(i - 1, j, k, false);
					int k2 = this.getLightValueExt(i, j, k + 1, false);
					int l2 = this.getLightValueExt(i, j, k - 1, false);
					if(i2 > l1) {
						l1 = i2;
					}

					if(j2 > l1) {
						l1 = j2;
					}

					if(k2 > l1) {
						l1 = k2;
					}

					if(l2 > l1) {
						l1 = l2;
					}

					return l1;
				}
			}

			j1 = (i >> 4) - this.chunkX;
			l1 = (k >> 4) - this.chunkZ;
			return this.chunkArray[j1][l1].getBlockLightValue(i & 15, j, k & 15, this.worldObj.skylightSubtracted);
		} else {
			return 15;
		}
	}

	public int getBlockMetadata(int i, int j, int k) {
		int l = (i >> 4) - this.chunkX;
		int i1 = (k >> 4) - this.chunkZ;
		return this.chunkArray[l][i1].getBlockMetadata(i & 15, j, k & 15);
	}

	public Material getBlockMaterial(int i, int j, int k) {
		int l = this.getBlockId(i, j, k);
		return l == 0 ? Material.air : Block.blocksList[l].blockMaterial;
	}

	public WorldChunkManager getWorldChunkManager() {
		return this.worldObj.getWorldChunkManager();
	}

	public boolean isBlockOpaqueCube(int i, int j, int k) {
		Block block = Block.blocksList[this.getBlockId(i, j, k)];
		return block == null ? false : block.isOpaqueCube();
	}

	public boolean isBlockNormalCube(int i, int j, int k) {
		Block block = Block.blocksList[this.getBlockId(i, j, k)];
		return block == null ? false : block.blockMaterial.getIsSolid() && block.renderAsNormalBlock();
	}
}
