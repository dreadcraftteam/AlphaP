package net.minecraft_server.src;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Chunk {
	public static boolean isLit;
	public ChunkCube[] cubes;
	public boolean isChunkLoaded;
	public World worldObj;
	public short[] h;
	public int lowestBlockHeight;
	public final int xPosition;
	public final int zPosition;
	public boolean isModified;
	public boolean neverSave;
	public boolean hasEntities;
	public long lastSaveTime;
	public static final int cubesLength = 4094;
	public static final int cubesOffset = 2047;
	public static final short cubesYOffset = 32752;
	public static final int maxPossibleY = 32752;
	public static final int minPossibleY = -32752;

	public Chunk(World world, int i, int j) {
		this.cubes = new ChunkCube[4094];
		this.isModified = false;
		this.hasEntities = false;
		this.lastSaveTime = 0L;
		this.worldObj = world;
		this.xPosition = i;
		this.zPosition = j;
		this.h = new short[256];
	}

	public Chunk(World world, ChunkCube[] icubes, int i, int j) {
		this(world, i, j);
		this.cubes = icubes;
	}

	public Chunk(World world, byte[] data, int i, int j) {
		this(world, data, i, j, false, 3);
	}

	public Chunk(World world, byte[] data, int i, int j, boolean isTerrainPopulated, int extra16) {
		this(world, i, j);
		int numCubes = 1 << extra16;
		if (numCubes > this.cubes.length) {
			numCubes = this.cubes.length;
		}

		byte[][] subData = new byte[numCubes][4096];
		int xShift = 8 + extra16;
		int zShift = 4 + extra16;
		int yMax = numCubes * 16;

		int n;
		for (n = 0; n < 16; ++n) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < yMax; ++y) {
					int srcOffset = n << xShift | z << zShift | y;
					int destOffset = n << 8 | z << 4 | y & 15;
					subData[y >> 4][destOffset] = data[srcOffset];
				}
			}
		}

		for (n = 0; n < numCubes; ++n) {
			this.cubes[n + 2047] = new ChunkCube(this, subData[n], n + 2047);
			this.cubes[n + 2047].isChunkLoaded = true;
			this.cubes[n + 2047].isTerrainPopulated = isTerrainPopulated;
		}

	}

	public void setMetaData(byte[] md, int extra16) {
		int xShift = 8 + extra16;
		int zShift = 4 + extra16;
		int yMax = 16 << extra16;

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < yMax; y += 2) {
					int srcOffset = (x << xShift | z << zShift | y) >> 1;
					int destOffset = (x << 8 | z << 4 | y & 15) >> 1;
					this.cubes[(y >> 4) + 2047].data.data[destOffset] = md[srcOffset];
				}
			}
		}

	}

	public void setSkylight(byte[] md, int extra16) {
		int xShift = 8 + extra16;
		int zShift = 4 + extra16;
		int yMax = 16 << extra16;

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < yMax; y += 2) {
					int srcOffset = (x << xShift | z << zShift | y) >> 1;
					int destOffset = (x << 8 | z << 4 | y & 15) >> 1;
					this.cubes[(y >> 4) + 2047].skylightMap.data[destOffset] = md[srcOffset];
				}
			}
		}

	}

	public void setBlockLight(byte[] md, int extra16) {
		int xShift = 8 + extra16;
		int zShift = 4 + extra16;
		int yMax = 16 << extra16;

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < yMax; y += 2) {
					int srcOffset = (x << xShift | z << zShift | y) >> 1;
					int destOffset = (x << 8 | z << 4 | y & 15) >> 1;
					this.cubes[(y >> 4) + 2047].blocklightMap.data[destOffset] = md[srcOffset];
				}
			}
		}

	}

	public boolean isAtLocation(int i, int j) {
		return i == this.xPosition && j == this.zPosition;
	}

	public int getHeightValue(int i, int j) {
		return this.h[j << 4 | i];
	}

	public void func_348_a() {
	}

	public void generateHeightMap() {
		short i = 32752;

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int yCube = this.cubes.length - 1; yCube >= 0; --yCube) {
					ChunkCube cube = this.cubes[yCube];
					if (cube != null && cube.isChunkLoaded && !cube.isAir) {
						int tempHeight = cube.getMaxHeight(x, z);
						if (tempHeight > 0) {
							short height = (short) (16 * yCube + tempHeight - 32752 + 1);
							this.h[z << 4 | x] = height;
							if (height < i) {
								i = height;
							}
							break;
						}
					}
				}
			}
		}

		this.lowestBlockHeight = i;
		this.isModified = true;
	}

	public void spreadLighting() {
		short lowestH = 32752;

		int x;
		int z;
		for (x = 0; x < 16; ++x) {
			for (z = 0; z < 16; ++z) {
				int lightLevel;
				int yCube;
				for (lightLevel = this.cubes.length - 1; lightLevel >= 0; --lightLevel) {
					ChunkCube arrayOffset = this.cubes[lightLevel];
					if (arrayOffset != null && arrayOffset.isChunkLoaded && !arrayOffset.isAir) {
						yCube = arrayOffset.getMaxHeight(x, z);
						if (yCube >= 0) {
							short cube = (short) (16 * lightLevel + yCube - 32752 + 1);
							this.h[z << 4 | x] = cube;
							if (cube < lowestH) {
								lowestH = cube;
							}
							break;
						}
					}
				}

				if (!this.worldObj.worldProvider.hasNoSky) {
					lightLevel = 15;
					int i9 = x << 8 | z << 4;

					for (yCube = this.cubes.length - 1; yCube >= 0; --yCube) {
						ChunkCube chunkCube10 = this.cubes[yCube];
						if (chunkCube10 != null && chunkCube10.isChunkLoaded) {
							for (int yoffset = 15; yoffset >= 0; --yoffset) {
								lightLevel -= Block.lightOpacity[chunkCube10.blocks[i9 | yoffset] & 255];
								if (lightLevel > 0) {
									chunkCube10.skylightMap.setNibble(x, yoffset, z, lightLevel);
								} else {
									chunkCube10.skylightMap.setNibble(x, yoffset, z, 0);
								}
							}
						}
					}
				}
			}
		}

		this.lowestBlockHeight = lowestH;

		for (x = 0; x < 16; ++x) {
			for (z = 0; z < 16; ++z) {
				this.propagateSkylightOcclusion(x, z);
			}
		}

		this.isModified = true;
	}

	public void func_4053_c() {
	}

	public void propagateSkylightOcclusion(int i, int j) {
		int k = this.getHeightValue(i, j);
		int l = this.xPosition * 16 + i;
		int i1 = this.zPosition * 16 + j;
		this.checkSkylightNeighborHeight(l - 1, i1, k);
		this.checkSkylightNeighborHeight(l + 1, i1, k);
		this.checkSkylightNeighborHeight(l, i1 - 1, k);
		this.checkSkylightNeighborHeight(l, i1 + 1, k);
	}

	private void checkSkylightNeighborHeight(int i, int j, int k) {
		int l = this.worldObj.getHeightValue(i, j);
		if (l > k) {
			this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, i, k, j, i, l, j);
			this.isModified = true;
		} else if (l < k) {
			this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, i, l, j, i, k, j);
			this.isModified = true;
		}

	}

	public void func_339_g(int x, int y, int z) {
		short colHeight = this.h[z << 4 | x];
		int height = colHeight;
		if (y > colHeight) {
			height = y;
		}

		int arrayOffset = x << 8 | z << 4;

		int xabs;
		int lightLevel;
		label109: for (xabs = this.cubes.length - 1; xabs >= 0; --xabs) {
			ChunkCube zabs = this.cubes[xabs];
			if (zabs != null && zabs.isChunkLoaded && !zabs.isAir) {
				for (lightLevel = 15; lightLevel >= 0; --lightLevel) {
					height = (xabs << 4 | lightLevel) - 32752 + 1;
					if (Block.lightOpacity[zabs.blocks[arrayOffset | lightLevel] & 255] != 0) {
						break label109;
					}
				}
			}
		}

		if (height != colHeight) {
			xabs = this.xPosition * 16 + x;
			int i12 = this.zPosition * 16 + z;
			this.worldObj.markBlocksDirtyVertical(xabs, i12, height, colHeight);
			this.h[z << 4 | x] = (short) height;
			if (height < this.lowestBlockHeight) {
				this.lowestBlockHeight = height;
			} else if (colHeight == this.lowestBlockHeight) {
				this.regenMinHeight();
			}

			if (height < colHeight) {
				for (lightLevel = height; lightLevel < colHeight; ++lightLevel) {
					this.setSkylightMapNibble(x, lightLevel, z, 15);
				}
			} else {
				this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xabs, colHeight, i12, xabs, height, i12);

				for (lightLevel = colHeight; lightLevel < height; ++lightLevel) {
					this.setSkylightMapNibble(x, lightLevel, z, 0);
				}
			}

			if (height >= 32752) {
				height = 32751;
			}

			lightLevel = 15;

			int heightB4;
			for (heightB4 = height; height > -32752 && this.cubes[(height >> 4) + 2047] != null && lightLevel > 0; this
					.setSkylightMapNibble(x, height, z, lightLevel)) {
				--height;
				int l3 = Block.lightOpacity[this.getBlockID(x, height, z)];
				if (l3 == 0) {
					l3 = 1;
				}

				lightLevel -= l3;
				if (lightLevel < 0) {
					lightLevel = 0;
				}
			}

			while (height > -32752 && this.cubes[(height >> 4) + 2047] != null
					&& Block.lightOpacity[this.getBlockID(x, height - 1, z)] == 0) {
				--height;
			}

			if (height != heightB4) {
				this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xabs - 1, height, i12 - 1, xabs + 1, heightB4,
						i12 + 1);
			}

			this.isModified = true;
		}
	}

	public int getBlockID(int x, int y, int z) {
		int cube = (y >> 4) + 2047;
		return cube >= 0 && cube < this.cubes.length && this.cubes[cube] != null
				? this.cubes[cube].getBlockID(x, y & 15, z) & 255
				: 0;
	}

	public boolean setBlockIDWithMetadata(int x, int y, int z, int id, int md) {
		int cube = (y >> 4) + 2047;
		if (cube >= 0 && cube < this.cubes.length && this.cubes[cube] != null) {
			byte byteid = (byte) id;
			short height = this.h[z << 4 | x];
			int oldBlockID = this.getBlockID(x, y, z);
			if (oldBlockID == id && this.getBlockMetadata(x, y, z) == md) {
				return false;
			} else {
				int xabs = this.xPosition * 16 + x;
				int zabs = this.zPosition * 16 + z;
				this.cubes[cube].blocks[x << 8 | z << 4 | y & 15] = (byte) (byteid & 255);
				if (this.cubes[cube].isAir && (byteid & 255) != 0) {
					this.cubes[cube].isAir = false;
				}

				if (oldBlockID != 0 && !this.worldObj.multiplayerWorld) {
					Block.blocksList[oldBlockID].onBlockRemoval(this.worldObj, xabs, y, zabs);
				}

				this.cubes[cube].data.setNibble(x, y & 15, z, md);
				if (!this.worldObj.worldProvider.hasNoSky) {
					if (Block.lightOpacity[byteid & 255] != 0) {
						if (y >= height) {
							this.func_339_g(x, y + 1, z);
						}
					} else if (y == height - 1) {
						this.func_339_g(x, y, z);
					}

					this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xabs, y, zabs, xabs, y, zabs);
				}

				this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Block, xabs, y, zabs, xabs, y, zabs);
				this.propagateSkylightOcclusion(x, z);
				this.cubes[cube].data.setNibble(x, y & 15, z, md);
				if (id != 0) {
					Block.blocksList[id].onBlockAdded(this.worldObj, xabs, y, zabs);
				}

				this.isModified = true;
				this.cubes[cube].isModified = true;
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean setBlockID(int x, int y, int z, int id) {
		int cube = (y >> 4) + 2047;
		if (cube >= 0 && cube < this.cubes.length && this.cubes[cube] != null) {
			byte byteid = (byte) id;
			short height = this.h[z << 4 | x];
			int oldBlockID = this.getBlockID(x, y, z);
			if (oldBlockID == id) {
				return false;
			} else {
				int xabs = this.xPosition * 16 + x;
				int zabs = this.zPosition * 16 + z;
				this.cubes[cube].blocks[x << 8 | z << 4 | y & 15] = (byte) (byteid & 255);
				if (this.cubes[cube].isAir && (byteid & 255) != 0) {
					this.cubes[cube].isAir = false;
				}

				if (oldBlockID != 0) {
					Block.blocksList[oldBlockID].onBlockRemoval(this.worldObj, xabs, y, zabs);
				}

				this.cubes[cube].data.setNibble(x, y & 15, z, 0);
				if (Block.lightOpacity[byteid & 255] != 0) {
					if (y >= height) {
						this.func_339_g(x, y + 1, z);
					}
				} else if (y == height - 1) {
					this.func_339_g(x, y, z);
				}

				this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, xabs, y, zabs, xabs, y, zabs);
				this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Block, xabs, y, zabs, xabs, y, zabs);
				this.propagateSkylightOcclusion(x, z);
				if (id != 0 && !this.worldObj.multiplayerWorld) {
					Block.blocksList[id].onBlockAdded(this.worldObj, xabs, y, zabs);
				}

				this.isModified = true;
				this.cubes[cube].isModified = true;
				return true;
			}
		} else {
			return false;
		}
	}

	public int getBlockMetadata(int x, int y, int z) {
		int cube = (y >> 4) + 2047;
		return cube >= 0 && cube < this.cubes.length && this.cubes[cube] != null
				? this.cubes[cube].getBlockMetadata(x, y & 15, z)
				: 0;
	}

	public void setBlockMetadata(int x, int y, int z, int md) {
		int cube = (y >> 4) + 2047;
		if (cube >= 0 && cube < this.cubes.length && this.cubes[cube] != null) {
			this.isModified = true;
			this.cubes[cube].isModified = true;
			this.cubes[cube].data.setNibble(x, y & 15, z, md);
		}

	}

	public int getSavedLightValue(EnumSkyBlock enumskyblock, int i, int j, int k) {
		int cube = j >> 2051;
		return cube < 0 ? 0
				: (cube >= this.cubes.length ? (enumskyblock == EnumSkyBlock.Sky ? 15 : 0)
						: (enumskyblock == EnumSkyBlock.Sky ? this.getSkylightMapNibble(i, j, k)
								: (enumskyblock == EnumSkyBlock.Block ? this.getBlocklightMapNibble(i, j, k) : 0)));
	}

	public void setLightValue(EnumSkyBlock enumskyblock, int x, int y, int z, int level) {
		int cube = (y >> 4) + 2047;
		if (cube >= 0 && cube < this.cubes.length) {
			this.isModified = true;
			if (enumskyblock == EnumSkyBlock.Sky) {
				this.setSkylightMapNibble(x, y, z, level);
			} else {
				if (enumskyblock != EnumSkyBlock.Block) {
					return;
				}

				this.setBlocklightMapNibble(x, y, z, level);
			}
		}

	}

	public int getBlockLightValue(int x, int y, int z, int skyDrop) {
		int cube = (y >> 4) + 2047;
		if (cube >= 0 && cube < this.cubes.length) {
			int level = this.getSkylightMapNibble(x, y, z);
			if (level > 0) {
				isLit = true;
			}

			level -= skyDrop;
			int blockLightLevel = this.getBlocklightMapNibble(x, y, z);
			if (blockLightLevel > level) {
				level = blockLightLevel;
			}

			return level;
		} else {
			return 0;
		}
	}

	public void addEntity(Entity entity) {
		this.hasEntities = true;
		int i = MathHelper.floor_double(entity.posX / 16.0D);
		int j = MathHelper.floor_double(entity.posZ / 16.0D);
		if (i == this.xPosition && j == this.zPosition) {
			int yChunk = MathHelper.floor_double(entity.posY / 16.0D);
			if (yChunk < -2047) {
				yChunk = -2047;
			} else if (yChunk >= 2047) {
				yChunk = 2046;
			}

			entity.addedToChunk = true;
			entity.chunkCoordX = this.xPosition;
			entity.chunkCoordY = yChunk;
			entity.chunkCoordZ = this.zPosition;
			if (this.cubes[yChunk + 2047] != null) {
				this.cubes[yChunk + 2047].entities.add(entity);
			}

		} else {
			System.out.println("Wrong location! " + entity);
		}
	}

	public void removeEntity(Entity entity) {
		this.removeEntityAtIndex(entity, entity.chunkCoordY);
	}

	public void removeEntityAtIndex(Entity entity, int yChunk) {
		if (yChunk < -2047) {
			yChunk = -2047;
		}

		if (yChunk >= 2047) {
			yChunk = 2046;
		}

		if (this.cubes[yChunk + 2047] != null) {
			this.cubes[yChunk + 2047].entities.remove(entity);
		}

	}

	public boolean canBlockSeeTheSky(int i, int j, int k) {
		return j >= this.h[k << 4 | i];
	}

	public TileEntity getChunkBlockTileEntity(int i, int j, int k) {
		int cubePos = (j >> 4) + 2047;
		if (cubePos >= 0 && cubePos <= this.cubes.length) {
			ChunkCube cube = this.cubes[cubePos];
			return cube == null ? null : cube.getChunkBlockTileEntity(i, j & 15, k);
		} else {
			return null;
		}
	}

	public void addTileEntity(TileEntity tileentity) {
		int i = tileentity.xCoord - this.xPosition * 16;
		int j = tileentity.yCoord;
		int k = tileentity.zCoord - this.zPosition * 16;
		this.setChunkBlockTileEntity(i, j, k, tileentity);
		if (this.isChunkLoaded) {
			this.worldObj.loadedTileEntityList.add(tileentity);
		}

	}

	public void setChunkBlockTileEntity(int i, int j, int k, TileEntity tileentity) {
		int cubePos = (j >> 4) + 2047;
		if (cubePos >= 0 && cubePos <= this.cubes.length) {
			ChunkCube cube = this.cubes[cubePos];
			if (cube == null) {
				System.out.println("Attempted to place a tile entity in an unloaded ChunkCube!");
			} else {
				cube.setChunkBlockTileEntity(i, j & 15, k, tileentity);
			}
		} else {
			System.out.println("Attempted to place a tile entity beyond the edge of the world!");
		}
	}

	public void removeChunkBlockTileEntity(int i, int j, int k) {
		int cubePos = (j >> 4) + 2047;
		if (cubePos >= 0 && cubePos < this.cubes.length) {
			ChunkCube cube = this.cubes[cubePos];
			if (cube != null) {
				cube.removeChunkBlockTileEntity(i, j & 15, k);
			}
		}
	}

	public void onChunkLoad() {
		this.isChunkLoaded = true;

		for (int y = 0; y < this.cubes.length; ++y) {
			if (this.cubes[y] != null) {
				this.worldObj.func_31047_a(this.cubes[y].chunkTileEntityMap.values());
				this.worldObj.addLoadedEntities(this.cubes[y].entities);
			}
		}

	}

	public void onChunkUnload() {
		this.isChunkLoaded = false;

		for (int y = 0; y < this.cubes.length; ++y) {
			if (this.cubes[y] != null) {
				Iterator i$ = this.cubes[y].chunkTileEntityMap.values().iterator();

				while (i$.hasNext()) {
					TileEntity tileentity = (TileEntity) i$.next();
					tileentity.invalidate();
				}

				this.worldObj.addUnloadedEntities(this.cubes[y].entities);
			}
		}

	}

	public void setChunkModified() {
		this.isModified = true;
	}

	public void getEntitiesWithinAABBForEntity(Entity entity, AxisAlignedBB axisalignedbb, List list) {
		int y1 = MathHelper.floor_double((axisalignedbb.minY - 2.0D) / 16.0D);
		int y2 = MathHelper.floor_double((axisalignedbb.maxY + 2.0D) / 16.0D);
		if (y1 < -2047) {
			y1 = -2047;
		}

		if (y2 >= 2047) {
			y2 = 2046;
		}

		for (int y3 = y1; y3 <= y2; ++y3) {
			if (this.cubes[y3 + 2047] != null) {
				List list1 = this.cubes[y3 + 2047].entities;

				for (int l = 0; l < list1.size(); ++l) {
					Entity entity1 = (Entity) list1.get(l);
					if (entity1 != entity && entity1.boundingBox.intersectsWith(axisalignedbb)) {
						list.add(entity1);
					}
				}
			}
		}

	}

	public void getEntitiesOfTypeWithinAAAB(Class class1, AxisAlignedBB axisalignedbb, List list) {
		int y1 = MathHelper.floor_double((axisalignedbb.minY - 2.0D) / 16.0D);
		int y2 = MathHelper.floor_double((axisalignedbb.maxY + 2.0D) / 16.0D);
		if (y1 < -2047) {
			y1 = -2047;
		}

		if (y2 >= 2047) {
			y2 = 2046;
		}

		for (int y3 = y1; y3 <= y2; ++y3) {
			if (this.cubes[y3 + 2047] != null) {
				List list1 = this.cubes[y3 + 2047].entities;

				for (int l = 0; l < list1.size(); ++l) {
					Entity entity = (Entity) list1.get(l);
					if (class1.isAssignableFrom(entity.getClass())
							&& entity.boundingBox.intersectsWith(axisalignedbb)) {
						list.add(entity);
					}
				}
			}
		}

	}

	public boolean needsSaving(boolean flag) {
		if (this.neverSave) {
			return false;
		} else {
			if (flag) {
				if (this.hasEntities && this.worldObj.getWorldTime() != this.lastSaveTime) {
					return true;
				}
			} else if (this.hasEntities && this.worldObj.getWorldTime() >= this.lastSaveTime + 600L) {
				return true;
			}

			return this.isModified;
		}
	}

	public Random func_334_a(long l) {
		return new Random(this.worldObj.getRandomSeed() + (long) (this.xPosition * this.xPosition * 4987142)
				+ (long) (this.xPosition * 5947611) + (long) (this.zPosition * this.zPosition) * 4392871L
				+ (long) (this.zPosition * 389711) ^ l);
	}

	public boolean func_21101_g() {
		return false;
	}

	public void func_25083_h() {
		ChunkCube[] arr$ = this.cubes;
		int len$ = arr$.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			ChunkCube cube = arr$[i$];
			if (cube != null) {
				ChunkBlockMap.removeUnknownBlocks(cube.blocks);
			}
		}

	}

	public ChunkCube cubeAtYIndex(int y) {
		return this.cubes[y + 2047];
	}

	public void setCubeAtYIndex(int y, ChunkCube cube) {
		this.cubes[y + 2047] = cube;
	}

	private void setSkylightMapNibble(int x, int y, int z, int level) {
		int cube = (y >> 4) + 2047;
		if (this.cubes[cube] != null) {
			this.cubes[cube].skylightMap.setNibble(x, y & 15, z, level);
			this.cubes[cube].isModified = true;
		}

	}

	private int getSkylightMapNibble(int x, int y, int z) {
		int cube = (y >> 4) + 2047;
		return this.cubes[cube] == null ? (y > 64 ? 15 : 0) : this.cubes[cube].skylightMap.getNibble(x, y & 15, z);
	}

	private void setBlocklightMapNibble(int x, int y, int z, int level) {
		int cube = (y >> 4) + 2047;
		if (this.cubes[cube] != null) {
			this.cubes[cube].blocklightMap.setNibble(x, y & 15, z, level);
			this.cubes[cube].isModified = true;
		}

	}

	private int getBlocklightMapNibble(int x, int y, int z) {
		int cube = (y >> 4) + 2047;
		return this.cubes[cube] == null ? (y > 64 ? 15 : 0) : this.cubes[cube].blocklightMap.getNibble(x, y & 15, z);
	}

	public boolean cubeExists(int y) {
		int cube = (y >> 4) + 2047;
		return this.cubes[cube] != null;
	}

	public void regenMinHeight() {
		short lowestH = 32752;

		for (int i2 = 0; i2 < 16; ++i2) {
			for (int k2 = 0; k2 < 16; ++k2) {
				if ((this.h[k2 << 4 | i2] & 255) < lowestH) {
					lowestH = this.h[k2 << 4 | i2];
				}
			}
		}

		this.lowestBlockHeight = lowestH;
	}
}
