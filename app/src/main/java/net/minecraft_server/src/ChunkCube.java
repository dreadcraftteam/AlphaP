package net.minecraft_server.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkCube {
	public static boolean isLit;
	public byte[] blocks;
	public boolean isChunkLoaded;
	public Chunk chunk;
	public NibbleArray data;
	public NibbleArray skylightMap;
	public NibbleArray blocklightMap;
	public final int yPosition;
	public boolean isTerrainPopulated;
	public boolean isModified;
	public boolean neverSave;
	public long lastSaveTime;
	public Map chunkTileEntityMap;
	List entities;
	public boolean isAir;
	public static final int XSIZE = 16;
	public static final int YSIZE = 16;
	public static final int ZSIZE = 16;
	public static final int XSHIFT = 8;
	public static final int ZSHIFT = 4;

	public ChunkCube(Chunk ichunk, int y) {
		this.chunkTileEntityMap = new HashMap();
		this.entities = new ArrayList();
		this.isAir = false;
		this.isTerrainPopulated = false;
		this.isModified = false;
		this.lastSaveTime = 0L;
		this.chunk = ichunk;
		this.yPosition = y - 2047;
	}

	public ChunkCube(Chunk ichunk, byte[] iblocks, int y) {
		this(ichunk, y);
		this.blocks = iblocks;
		this.data = new NibbleArray(iblocks.length);
		this.skylightMap = new NibbleArray(iblocks.length);
		this.blocklightMap = new NibbleArray(iblocks.length);
	}

	public boolean calculateIsAir() {
		if (0 <= this.yPosition && this.yPosition <= 7) {
			return false;
		} else {
			byte[] arr$ = this.blocks;
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				byte b = arr$[i$];
				if (b != 0) {
					this.isAir = false;
					return false;
				}
			}

			this.isAir = true;
			return true;
		}
	}

	public int getMaxHeight(int x, int z) {
		int offset = x << 8 | z << 4;

		int y;
		for (y = 15; y >= 0 && Block.lightOpacity[this.blocks[offset | y] & 255] == 0; --y) {
		}

		return y;
	}

	public void onChunkLoad() {
		this.isChunkLoaded = true;
		this.chunk.worldObj.func_31047_a(this.chunkTileEntityMap.values());
		this.chunk.worldObj.addLoadedEntities(this.entities);
	}

	public void onChunkUnload() {
		this.isChunkLoaded = false;
		this.chunk.worldObj.loadedTileEntityList.removeAll(this.chunkTileEntityMap.values());
		this.chunk.worldObj.addUnloadedEntities(this.entities);
	}

	public void removeUnknownBlocks() {
		ChunkBlockMap.removeUnknownBlocks(this.blocks);
	}

	public void func_4143_d() {
	}

	public boolean isAtLocation(int x, int y, int z) {
		return x == this.chunk.xPosition && y == this.yPosition && z == this.chunk.zPosition;
	}

	public void setCubeModified() {
		this.isModified = true;
		this.chunk.isModified = true;
	}

	public boolean needsSaving(boolean flag) {
		if (this.neverSave) {
			return false;
		} else {
			if (flag) {
				if (!this.entities.isEmpty() && this.chunk.worldObj.getWorldTime() != this.lastSaveTime) {
					return true;
				}
			} else if (!this.entities.isEmpty() && this.chunk.worldObj.getWorldTime() >= this.lastSaveTime + 600L) {
				return true;
			}

			return this.isModified;
		}
	}

	public int getBlockID(int x, int y, int z) {
		int offset = x << 8 | z << 4 | y;
		return this.blocks[offset] & 255;
	}

	public int getBlockMetadata(int x, int y, int z) {
		return this.data.getNibble(x, y & 15, z);
	}

	public boolean setBlockID(int x, int y, int z, int id) {
		byte byteid = (byte) id;
		short height = this.chunk.h[z << 4 | x];
		int oldBlockID = this.getBlockID(x, y, z);
		if (oldBlockID == id) {
			return false;
		} else {
			int xabs = (this.chunk.xPosition << 4) + x;
			int yabs = (this.yPosition << 4) + y;
			int zabs = (this.chunk.zPosition << 4) + z;
			this.blocks[x << 8 | z << 4 | y] = (byte) (byteid & 255);
			if (this.isAir && (byteid & 255) != 0) {
				this.isAir = false;
			}

			if (oldBlockID != 0) {
				Block.blocksList[oldBlockID].onBlockRemoval(this.chunk.worldObj, xabs, yabs, zabs);
			}

			this.data.setNibble(x, y, z, 0);
			if (Block.lightOpacity[byteid & 255] != 0) {
				if (yabs >= height) {
					this.chunk.func_339_g(x, yabs + 1, z);
				}
			} else if (yabs == height - 1) {
				this.chunk.func_339_g(x, yabs, z);
			}

			this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xabs, yabs, zabs, xabs, yabs, zabs);
			this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Block, xabs, yabs, zabs, xabs, yabs, zabs);
			this.chunk.propagateSkylightOcclusion(x, z);
			if (id != 0 && !this.chunk.worldObj.multiplayerWorld) {
				Block.blocksList[id].onBlockAdded(this.chunk.worldObj, xabs, yabs, zabs);
			}

			this.chunk.isModified = true;
			this.isModified = true;
			return true;
		}
	}

	public void setBlockMetadata(int x, int y, int z, int md) {
		this.chunk.isModified = true;
		this.isModified = true;
		this.data.setNibble(x, y, z, md);
	}

	public boolean setBlockIDWithMetadata(int x, int y, int z, int id, int md) {
		byte byteid = (byte) id;
		short height = this.chunk.h[z << 4 | x];
		int oldBlockID = this.getBlockID(x, y, z);
		if (oldBlockID == id && this.getBlockMetadata(x, y, z) == md) {
			return false;
		} else {
			int xabs = (this.chunk.xPosition << 4) + x;
			int yabs = (this.yPosition << 4) + y;
			int zabs = (this.chunk.zPosition << 4) + z;
			this.blocks[x << 8 | z << 4 | y] = (byte) (byteid & 255);
			if (this.isAir && (byteid & 255) != 0) {
				this.isAir = false;
			}

			if (oldBlockID != 0 && !this.chunk.worldObj.multiplayerWorld) {
				Block.blocksList[oldBlockID].onBlockRemoval(this.chunk.worldObj, xabs, yabs, zabs);
			}

			this.data.setNibble(x, y, z, md);
			if (!this.chunk.worldObj.worldProvider.hasNoSky) {
				if (Block.lightOpacity[byteid & 255] != 0) {
					if (yabs >= height) {
						this.chunk.func_339_g(x, yabs + 1, z);
					}
				} else if (yabs == height - 1) {
					this.chunk.func_339_g(x, yabs, z);
				}

				this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xabs, yabs, zabs, xabs, yabs, zabs);
			}

			this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Block, xabs, yabs, zabs, xabs, yabs, zabs);
			this.chunk.propagateSkylightOcclusion(x, z);
			this.data.setNibble(x, y, z, md);
			if (id != 0) {
				Block.blocksList[id].onBlockAdded(this.chunk.worldObj, xabs, yabs, zabs);
			}

			this.chunk.isModified = true;
			this.isModified = true;
			return true;
		}
	}

	public int getSavedLightValue(EnumSkyBlock enumskyblock, int i, int j, int k) {
		return enumskyblock == EnumSkyBlock.Sky ? this.skylightMap.getNibble(i, j, k)
				: (enumskyblock == EnumSkyBlock.Block ? this.blocklightMap.getNibble(i, j, k) : 0);
	}

	public void setLightValue(EnumSkyBlock enumskyblock, int x, int y, int z, int level) {
		this.isModified = true;
		this.chunk.isModified = true;
		if (enumskyblock == EnumSkyBlock.Sky) {
			this.skylightMap.setNibble(x, y, z, level);
		} else {
			if (enumskyblock != EnumSkyBlock.Block) {
				return;
			}

			this.blocklightMap.setNibble(x, y, z, level);
		}

	}

	public int getBlockLightValue(int x, int y, int z, int skyDrop) {
		int level = this.skylightMap.getNibble(x, y, z);
		if (level > 0) {
			isLit = true;
		}

		level -= skyDrop;
		int blockLightLevel = this.blocklightMap.getNibble(x, y, z);
		if (blockLightLevel > level) {
			level = blockLightLevel;
		}

		return level;
	}

	public void addEntity(Entity entity) {
		this.chunk.hasEntities = true;
		int x = MathHelper.floor_double(entity.posX / 16.0D);
		int y = MathHelper.floor_double(entity.posY / 16.0D);
		int z = MathHelper.floor_double(entity.posZ / 16.0D);
		if (y < -2047) {
			y = -2047;
		} else if (y >= 2047) {
			y = 2046;
		}

		if (x != this.chunk.xPosition || z != this.chunk.zPosition || y != this.yPosition) {
			System.out.println("Wrong location! " + entity + " Was at chunk coords: " + x + ", " + y + ", " + z + ", "
					+ ". Expected: " + this.chunk.xPosition + ", " + this.yPosition + ", " + this.chunk.zPosition
					+ ", ");
			Thread.dumpStack();
		}

		entity.addedToChunk = true;
		entity.chunkCoordX = this.chunk.xPosition;
		entity.chunkCoordY = this.yPosition;
		entity.chunkCoordZ = this.chunk.zPosition;
		this.entities.add(entity);
	}

	public void removeEntity(Entity entity) {
		this.entities.remove(entity);
	}

	public TileEntity getChunkBlockTileEntity(int x, int y, int z) {
		ChunkPosition chunkposition = new ChunkPosition(x, y, z);
		TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);
		if (tileentity == null) {
			int l = this.getBlockID(x, y, z);
			if (!Block.isBlockContainer[l]) {
				return null;
			}

			BlockContainer blockcontainer = (BlockContainer) Block.blocksList[l];
			blockcontainer.onBlockAdded(this.chunk.worldObj, this.chunk.xPosition * 16 + x, this.yPosition * 16 + y,
					this.chunk.zPosition * 16 + z);
			tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);
		}

		if (tileentity != null && tileentity.isInvalid()) {
			this.chunkTileEntityMap.remove(chunkposition);
			return null;
		} else {
			return tileentity;
		}
	}

	public void setChunkBlockTileEntity(int x, int y, int z, TileEntity tileentity) {
		ChunkPosition chunkposition = new ChunkPosition(x, y, z);
		tileentity.worldObj = this.chunk.worldObj;
		tileentity.xCoord = this.chunk.xPosition * 16 + x;
		tileentity.yCoord = this.yPosition * 16 + y;
		tileentity.zCoord = this.chunk.zPosition * 16 + z;
		int blockID = this.getBlockID(x, y, z);
		if (blockID != 0 && Block.blocksList[blockID] instanceof BlockContainer) {
			if (this.isChunkLoaded) {
				if (this.chunkTileEntityMap.get(chunkposition) != null) {
					this.chunk.worldObj.loadedTileEntityList.remove(this.chunkTileEntityMap.get(chunkposition));
				}

				tileentity.validate();
			}

			this.chunkTileEntityMap.put(chunkposition, tileentity);
		} else {
			System.out.println("Attempted to place a tile entity where there was no entity tile!");
		}
	}

	public void removeChunkBlockTileEntity(int i, int j, int k) {
		ChunkPosition chunkposition = new ChunkPosition(i, j, k);
		if (this.isChunkLoaded) {
			TileEntity te = (TileEntity) this.chunkTileEntityMap.remove(chunkposition);
			if (te != null) {
				te.invalidate();
			}
		}

	}

	public int getChunkData(byte[] adata, int x1, int y1, int z1, int x2, int y2, int z2, int offset) {
		int xW = x2 - x1;
		int yW = y2 - y1;
		int zW = z2 - z1;
		if (xW * yW * zW == this.blocks.length) {
			System.arraycopy(this.blocks, 0, adata, offset, this.blocks.length);
			offset += this.blocks.length;
			System.arraycopy(this.data.data, 0, adata, offset, this.data.data.length);
			offset += this.data.data.length;
			System.arraycopy(this.blocklightMap.data, 0, adata, offset, this.blocklightMap.data.length);
			offset += this.blocklightMap.data.length;
			System.arraycopy(this.skylightMap.data, 0, adata, offset, this.skylightMap.data.length);
			offset += this.skylightMap.data.length;
			return offset;
		} else {
			int j3;
			int j4;
			int j5;
			int j6;
			for (j3 = x1; j3 < x2; ++j3) {
				for (j4 = z1; j4 < z2; ++j4) {
					j5 = j3 << 8 | j4 << 4 | y1;
					j6 = y2 - y1;
					System.arraycopy(this.blocks, j5, adata, offset, j6);
					offset += j6;
				}
			}

			for (j3 = x1; j3 < x2; ++j3) {
				for (j4 = z1; j4 < z2; ++j4) {
					j5 = (j3 << 8 | j4 << 4 | y1) >> 1;
					j6 = (y2 - y1) / 2;
					System.arraycopy(this.data.data, j5, adata, offset, j6);
					offset += j6;
				}
			}

			for (j3 = x1; j3 < x2; ++j3) {
				for (j4 = z1; j4 < z2; ++j4) {
					j5 = (j3 << 8 | j4 << 4 | y1) >> 1;
					j6 = (y2 - y1) / 2;
					System.arraycopy(this.blocklightMap.data, j5, adata, offset, j6);
					offset += j6;
				}
			}

			for (j3 = x1; j3 < x2; ++j3) {
				for (j4 = z1; j4 < z2; ++j4) {
					j5 = (j3 << 8 | j4 << 4 | y1) >> 1;
					j6 = (y2 - y1) / 2;
					System.arraycopy(this.skylightMap.data, j5, adata, offset, j6);
					offset += j6;
				}
			}

			return offset;
		}
	}

	public void regenHeightMap() {
		boolean heightChanged = false;

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				if (this.chunk.h[z << 4 | x] < (this.yPosition + 1) * 16) {
					int tempHeight = this.getMaxHeight(x, z);
					if (tempHeight >= 0) {
						short height = (short) (this.yPosition * 16 + tempHeight);
						heightChanged |= height != this.chunk.h[z << 4 | x];
						this.chunk.h[z << 4 | x] = height;
					}
				}
			}
		}

		if (heightChanged) {
			this.chunk.regenMinHeight();
		}

	}

	private ChunkCube getCube(int xDist, int yDist, int zDist) {
		return this.chunk.worldObj.cubeExists(this.chunk.xPosition + xDist, this.yPosition + yDist,
				this.chunk.zPosition + zDist)
						? this.chunk.worldObj.getChunkFromChunkCoords(this.chunk.xPosition + xDist,
								this.yPosition + yDist, this.chunk.zPosition + zDist)
						: null;
	}

	public void regenSkylightMap() {
		if (!this.chunk.worldObj.worldProvider.hasNoSky) {
			int x = this.chunk.xPosition;
			int y = this.yPosition;
			int z = this.chunk.zPosition;
			int xBl = x * 16;
			int yBl = y * 16;
			int zBl = z * 16;
			if (this.chunk.worldObj.cubeExists(x + 1, y, z)) {
				this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xBl + 15, yBl, zBl, xBl + 16, yBl + 15,
						zBl + 15);
			}

			if (this.chunk.worldObj.cubeExists(x - 1, y, z)) {
				this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xBl - 1, yBl, zBl, xBl, yBl + 15,
						zBl + 15);
			}

			if (this.chunk.worldObj.cubeExists(x, y + 1, z)) {
				this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xBl, yBl + 15, zBl, xBl + 15, yBl + 16,
						zBl + 15);
			}

			if (this.chunk.worldObj.cubeExists(x, y - 1, z)) {
				this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xBl, yBl - 1, zBl, xBl + 16, yBl,
						zBl + 16);
			}

			if (this.chunk.worldObj.cubeExists(x, y, z + 1)) {
				this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xBl, yBl, zBl + 15, xBl + 16, yBl + 16,
						zBl + 16);
			}

			if (this.chunk.worldObj.cubeExists(x, y, z - 1)) {
				this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xBl, yBl, zBl - 1, xBl + 16, yBl + 16,
						zBl);
			}

		}
	}

	public void regenOnLoad() {
		this.regenHeightMap();
		this.regenSkylightMap();
	}
}
