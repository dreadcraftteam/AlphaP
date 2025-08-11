package net.minecraft.src;

public class ChunkCubeAir extends ChunkCube {
	public ChunkCubeAir(Chunk ichunk, int y) {
		super(ichunk, y);
		this.isAir = true;
		this.blocks = null;
		this.data = null;
		this.skylightMap = new NibbleArray(4096);
		this.blocklightMap = new NibbleArray(4096);
	}

	public boolean calculateIsAir() {
		if(0 <= this.yPosition && this.yPosition <= 7) {
			return false;
		} else {
			if(this.blocks != null) {
				byte[] arr$ = this.blocks;
				int len$ = arr$.length;

				for(int i$ = 0; i$ < len$; ++i$) {
					byte b = arr$[i$];
					if(b != 0) {
						this.isAir = false;
						return false;
					}
				}
			}

			this.isAir = true;
			return true;
		}
	}

	public int getMaxHeight(int x, int z) {
		if(this.blocks == null) {
			return -1;
		} else {
			int offset = x << 8 | z << 4;

			int y;
			for(y = 15; y >= 0 && Block.lightOpacity[this.blocks[offset | y] & 255] == 0; --y) {
			}

			return y;
		}
	}

	public void onChunkLoad() {
		this.isChunkLoaded = true;
		this.chunk.worldObj.func_31054_a(this.chunkTileEntityMap.values());
		this.chunk.worldObj.func_636_a(this.entities);
	}

	public void onChunkUnload() {
		this.isChunkLoaded = false;
		this.chunk.worldObj.loadedTileEntityList.removeAll(this.chunkTileEntityMap.values());
		this.chunk.worldObj.func_632_b(this.entities);
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
	}

	public int getBlockID(int x, int y, int z) {
		if(this.blocks == null) {
			return 0;
		} else {
			int offset = x << 8 | z << 4 | y;
			return this.blocks[offset] & 255;
		}
	}

	public int getBlockMetadata(int x, int y, int z) {
		return this.data == null ? 0 : this.data.getNibble(x, y & 15, z);
	}

	public boolean setBlockID(int x, int y, int z, int id) {
		if(this.blocks == null) {
			if(id == 0) {
				return false;
			}

			this.blocks = new byte[4096];
		}

		byte byteid = (byte)id;
		short height = this.chunk.h[z << 4 | x];
		int oldBlockID = this.getBlockID(x, y, z);
		if(oldBlockID == id) {
			return false;
		} else {
			int xabs = (this.chunk.xPosition << 4) + x;
			int yabs = (this.yPosition << 4) + y;
			int zabs = (this.chunk.zPosition << 4) + z;
			this.blocks[x << 8 | z << 4 | y] = (byte)(byteid & 255);
			if(this.isAir && (byteid & 255) != 0) {
				this.isAir = false;
			}

			if(oldBlockID != 0) {
				Block.blocksList[oldBlockID].onBlockRemoval(this.chunk.worldObj, xabs, yabs, zabs);
			}

			if(this.data != null) {
				this.data.setNibble(x, y, z, 0);
			}

			if(Block.lightOpacity[byteid & 255] != 0) {
				if(yabs >= height) {
					this.chunk.func_1003_g(x, yabs + 1, z);
				}
			} else if(yabs == height - 1) {
				this.chunk.func_1003_g(x, yabs, z);
			}

			this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xabs, yabs, zabs, xabs, yabs, zabs);
			this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Block, xabs, yabs, zabs, xabs, yabs, zabs);
			this.chunk.func_996_c(x, z);
			if(id != 0 && !this.chunk.worldObj.multiplayerWorld) {
				Block.blocksList[id].onBlockAdded(this.chunk.worldObj, xabs, yabs, zabs);
			}

			this.chunk.isModified = true;
			this.isModified = true;
			return true;
		}
	}

	public void setBlockMetadata(int x, int y, int z, int md) {
		if(this.data == null) {
			this.data = new NibbleArray(4096);
		}

		this.chunk.isModified = true;
		this.isModified = true;
		this.data.setNibble(x, y, z, md);
	}

	public boolean setBlockIDWithMetadata(int x, int y, int z, int id, int md) {
		if(this.blocks == null) {
			if(id == 0 && md == 0) {
				return false;
			}

			this.blocks = new byte[4096];
		}

		if(this.data == null) {
			this.data = new NibbleArray(4096);
		}

		byte byteid = (byte)id;
		short height = this.chunk.h[z << 4 | x];
		int oldBlockID = this.getBlockID(x, y, z);
		if(oldBlockID == id && this.getBlockMetadata(x, y, z) == md) {
			return false;
		} else {
			int xabs = (this.chunk.xPosition << 4) + x;
			int yabs = (this.yPosition << 4) + y;
			int zabs = (this.chunk.zPosition << 4) + z;
			this.blocks[x << 8 | z << 4 | y] = (byte)(byteid & 255);
			if(this.isAir && (byteid & 255) != 0) {
				this.isAir = false;
			}

			if(oldBlockID != 0 && !this.chunk.worldObj.multiplayerWorld) {
				Block.blocksList[oldBlockID].onBlockRemoval(this.chunk.worldObj, xabs, yabs, zabs);
			}

			this.data.setNibble(x, y, z, md);
			if(!this.chunk.worldObj.worldProvider.hasNoSky) {
				if(Block.lightOpacity[byteid & 255] != 0) {
					if(yabs >= height) {
						this.chunk.func_1003_g(x, yabs + 1, z);
					}
				} else if(yabs == height - 1) {
					this.chunk.func_1003_g(x, yabs, z);
				}

				this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xabs, yabs, zabs, xabs, yabs, zabs);
			}

			this.chunk.worldObj.scheduleLightingUpdate(EnumSkyBlock.Block, xabs, yabs, zabs, xabs, yabs, zabs);
			this.chunk.func_996_c(x, z);
			this.data.setNibble(x, y, z, md);
			if(id != 0) {
				Block.blocksList[id].onBlockAdded(this.chunk.worldObj, xabs, yabs, zabs);
			}

			this.chunk.isModified = true;
			this.isModified = true;
			return true;
		}
	}

	public int getSavedLightValue(EnumSkyBlock enumskyblock, int i, int j, int k) {
		return enumskyblock == EnumSkyBlock.Sky ? this.skylightMap.getNibble(i, j, k) : (enumskyblock == EnumSkyBlock.Block ? this.blocklightMap.getNibble(i, j, k) : 0);
	}

	public void setLightValue(EnumSkyBlock enumskyblock, int x, int y, int z, int level) {
		this.isModified = true;
		this.chunk.isModified = true;
		if(enumskyblock == EnumSkyBlock.Sky) {
			this.skylightMap.setNibble(x, y, z, level);
		} else {
			if(enumskyblock != EnumSkyBlock.Block) {
				return;
			}

			this.blocklightMap.setNibble(x, y, z, level);
		}

	}

	public int getBlockLightValue(int x, int y, int z, int skyDrop) {
		int level = this.skylightMap.getNibble(x, y, z);
		if(level > 0) {
			isLit = true;
		}

		level -= skyDrop;
		int blockLightLevel = this.blocklightMap.getNibble(x, y, z);
		if(blockLightLevel > level) {
			level = blockLightLevel;
		}

		return level;
	}

	public void addEntity(Entity entity) {
		this.chunk.hasEntities = true;
		int x = MathHelper.floor_double(entity.posX / 16.0D);
		int y = MathHelper.floor_double(entity.posY / 16.0D);
		int z = MathHelper.floor_double(entity.posZ / 16.0D);
		if(y < -2047) {
			y = -2047;
		} else if(y >= 2047) {
			y = 2046;
		}

		if(x != this.chunk.xPosition || z != this.chunk.zPosition || y != this.yPosition) {
			System.out.println("Wrong location! " + entity + " Was at chunk coords: " + x + ", " + y + ", " + z + ", " + ". Expected: " + this.chunk.xPosition + ", " + this.yPosition + ", " + this.chunk.zPosition + ", ");
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
		TileEntity tileentity = (TileEntity)this.chunkTileEntityMap.get(chunkposition);
		if(tileentity == null) {
			int l = this.getBlockID(x, y, z);
			if(!Block.isBlockContainer[l]) {
				return null;
			}

			BlockContainer blockcontainer = (BlockContainer)Block.blocksList[l];
			blockcontainer.onBlockAdded(this.chunk.worldObj, this.chunk.xPosition * 16 + x, this.yPosition * 16 + y, this.chunk.zPosition * 16 + z);
			tileentity = (TileEntity)this.chunkTileEntityMap.get(chunkposition);
		}

		if(tileentity != null && tileentity.func_31006_g()) {
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
		if(blockID != 0 && Block.blocksList[blockID] instanceof BlockContainer) {
			if(this.isChunkLoaded) {
				if(this.chunkTileEntityMap.get(chunkposition) != null) {
					this.chunk.worldObj.loadedTileEntityList.remove(this.chunkTileEntityMap.get(chunkposition));
				}

				tileentity.func_31004_j();
			}

			this.chunkTileEntityMap.put(chunkposition, tileentity);
		} else {
			System.out.println("Attempted to place a tile entity where there was no entity tile!");
		}
	}

	public void removeChunkBlockTileEntity(int i, int j, int k) {
		ChunkPosition chunkposition = new ChunkPosition(i, j, k);
		if(this.isChunkLoaded) {
			TileEntity te = (TileEntity)this.chunkTileEntityMap.remove(chunkposition);
			if(te != null) {
				te.func_31005_i();
			}
		}

	}

	public int setChunkData(byte[] adata, int x1, int y1, int z1, int x2, int y2, int z2, int mOffset) {
		if(this.blocks == null) {
			this.blocks = new byte[4096];
		}

		if(this.data == null) {
			this.data = new NibbleArray(4096);
		}

		return super.setChunkData(adata, x1, y1, z1, x2, y2, z2, mOffset);
	}
}
