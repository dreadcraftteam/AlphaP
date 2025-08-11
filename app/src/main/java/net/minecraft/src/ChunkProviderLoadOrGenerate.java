package net.minecraft.src;

import java.io.IOException;

public class ChunkProviderLoadOrGenerate implements IChunkProvider {
	private Chunk blankChunk;
	private ChunkCube blankCube;
	private IChunkProvider chunkProvider;
	private IChunkLoader chunkLoader;
	private Chunk[] chunks;
	private World worldObj;
	int lastQueriedChunkXPos;
	int lastQueriedChunkYPos;
	int lastQueriedChunkZPos;
	private Chunk lastQueriedChunk;
	private int curChunkX;
	private int curChunkY;
	private int curChunkZ;

	public void setCurrentChunkOver(int i, int j) {
		this.curChunkX = i;
		this.curChunkY = 3;
		this.curChunkZ = j;
	}

	public void setCurrentChunkOver(int i, int y, int j) {
		this.curChunkX = i;
		this.curChunkY = y;
		this.curChunkZ = j;
	}

	public boolean canChunkExist(int i, int y, int j) {
		byte range = 15;
		return i >= this.curChunkX - range && i <= this.curChunkX + range && y >= this.curChunkY - range && y <= this.curChunkY + range && j >= this.curChunkZ - range && j <= this.curChunkZ + range;
	}

	public boolean cubeExists(int i, int y, int j) {
		if(!this.canChunkExist(i, y, j)) {
			return false;
		} else if(i == this.lastQueriedChunkXPos && y == this.lastQueriedChunkYPos && j == this.lastQueriedChunkZPos && this.lastQueriedChunk != null) {
			return true;
		} else {
			int k = i & 31;
			int l = j & 31;
			int i1 = k + l * 32;
			return this.chunks[i1] != null && (this.chunks[i1] == this.blankChunk || this.chunks[i1].isAtLocation(i, j)) && this.chunks[i1].cubes[y + 2047] != null;
		}
	}

	public boolean chunkExists(int i, int j) {
		if(!this.canChunkExist(i, 3, j)) {
			return false;
		} else if(i == this.lastQueriedChunkXPos && j == this.lastQueriedChunkZPos && this.lastQueriedChunk != null) {
			return true;
		} else {
			int k = i & 31;
			int l = j & 31;
			int i1 = k + l * 32;
			return this.chunks[i1] != null && (this.chunks[i1] == this.blankChunk || this.chunks[i1].isAtLocation(i, j));
		}
	}

	public Chunk prepareChunk(int i, int j) {
		return this.provideChunk(i, j);
	}

	public ChunkCube prepareCube(int i, int y, int j) {
		return this.provideCube(i, y, j);
	}

	public Chunk provideChunk(int i, int j) {
		if(i == this.lastQueriedChunkXPos && j == this.lastQueriedChunkZPos && this.lastQueriedChunk != null) {
			return this.lastQueriedChunk;
		} else if(!this.worldObj.findingSpawnPoint && !this.canChunkExist(i, 3, j)) {
			return this.blankChunk;
		} else {
			int k = i & 31;
			int l = j & 31;
			int i1 = k + l * 32;
			if(!this.chunkExists(i, j)) {
				if(this.chunks[i1] != null) {
					this.chunks[i1].onChunkUnload();
					this.saveChunk(this.chunks[i1], true);
					this.saveExtraChunkData(this.chunks[i1]);
				}

				Chunk chunk = this.func_542_c(i, j);
				if(chunk == null) {
					if(this.chunkProvider == null) {
						chunk = this.blankChunk;
					} else {
						chunk = this.chunkProvider.provideChunk(i, j);
						chunk.func_25124_i();
					}
				}

				this.chunks[i1] = chunk;
				chunk.func_4143_d();
				if(this.chunks[i1] != null) {
					this.chunks[i1].onChunkLoad();
				}
			}

			this.lastQueriedChunkXPos = i;
			this.lastQueriedChunkYPos = 3;
			this.lastQueriedChunkZPos = j;
			this.lastQueriedChunk = this.chunks[i1];
			return this.chunks[i1];
		}
	}

	public ChunkCube provideCube(int x, int y, int z) {
		if(x == this.lastQueriedChunkXPos && y == this.lastQueriedChunkYPos && z == this.lastQueriedChunkZPos && this.lastQueriedChunk != null) {
			return this.lastQueriedChunk.cubes[this.lastQueriedChunkYPos + 2047];
		} else if(!this.worldObj.findingSpawnPoint && !this.canChunkExist(x, y, z)) {
			return this.blankCube;
		} else {
			int xArray = x & 31;
			int zArray = z & 31;
			int offset = xArray + zArray * 32;
			if(!this.chunkExists(x, z)) {
				if(this.chunks[offset] != null) {
					this.chunks[offset].onChunkUnload();
					this.saveChunk(this.chunks[offset], true);
					this.saveExtraChunkData(this.chunks[offset]);
				}

				ChunkCube cube = this.getCube(x, y, z);
				if(cube == null) {
					if(this.chunkProvider == null) {
						cube = this.blankCube;
					} else {
						cube = this.chunkProvider.provideCube(x, y, z);
						cube.removeUnknownBlocks();
					}
				}

				if(this.chunks[offset] == null) {
					this.chunks[offset] = new Chunk(this.worldObj, x, z);
				}

				this.chunks[offset].onChunkLoad();
				this.chunks[offset].cubes[y + 2047] = cube;
				cube.func_4143_d();
				if(cube != null) {
					cube.onChunkLoad();
				}

				this.populateIfNearbyCubesExist(x, y, z);
				this.populateIfNearbyCubesExist(x - 1, y, z);
				this.populateIfNearbyCubesExist(x, y - 1, z);
				this.populateIfNearbyCubesExist(x - 1, y - 1, z);
				this.populateIfNearbyCubesExist(x, y, z - 1);
				this.populateIfNearbyCubesExist(x - 1, y, z - 1);
				this.populateIfNearbyCubesExist(x, y - 1, z - 1);
				this.populateIfNearbyCubesExist(x - 1, y - 1, z - 1);
			}

			this.lastQueriedChunkXPos = x;
			this.lastQueriedChunkYPos = y;
			this.lastQueriedChunkZPos = z;
			this.lastQueriedChunk = this.chunks[offset];
			return this.chunks[offset].cubes[y + 2047];
		}
	}

	private void populateIfNearbyCubesExist(int x, int y, int z) {
		if(this.cubeExists(x, y, z)) {
			ChunkCube cube = this.provideCube(x, y, z);
			if(!cube.isTerrainPopulated && this.cubeExists(x + 1, y, z) && this.cubeExists(x, y + 1, z) && this.cubeExists(x + 1, y + 1, z) && this.cubeExists(x, y, z + 1) && this.cubeExists(x + 1, y, z + 1) && this.cubeExists(x, y + 1, z + 1) && this.cubeExists(x + 1, y + 1, z + 1)) {
				this.populate(this, x, y, z);
			}
		}

	}

	private Chunk func_542_c(int i, int j) {
		if(this.chunkLoader == null) {
			return this.blankChunk;
		} else {
			try {
				Chunk exception = this.chunkLoader.loadChunk(this.worldObj, i, j);
				if(exception != null) {
					exception.lastSaveTime = this.worldObj.getWorldTime();
				}

				return exception;
			} catch (Exception exception4) {
				exception4.printStackTrace();
				return this.blankChunk;
			}
		}
	}

	private ChunkCube getCube(int x, int y, int z) {
		if(this.chunkLoader == null) {
			return this.blankCube;
		} else {
			try {
				ChunkCube exception = this.chunkLoader.loadCube(this.worldObj, x, y, z);
				if(exception != null) {
					exception.lastSaveTime = this.worldObj.getWorldTime();
				}

				return exception;
			} catch (Exception exception5) {
				exception5.printStackTrace();
				return this.blankCube;
			}
		}
	}

	private void saveExtraChunkData(Chunk chunk) {
		if(this.chunkLoader != null) {
			try {
				this.chunkLoader.saveExtraChunkData(this.worldObj, chunk);
			} catch (Exception exception3) {
				exception3.printStackTrace();
			}

		}
	}

	private void saveChunk(Chunk chunk, boolean forceSave) {
		if(this.chunkLoader != null) {
			try {
				chunk.lastSaveTime = this.worldObj.getWorldTime();
				this.chunkLoader.saveChunk(this.worldObj, chunk);

				for(int ioexception = 0; ioexception < chunk.cubes.length; ++ioexception) {
					ChunkCube cube = chunk.cubes[ioexception];
					if(cube != null && cube.isChunkLoaded && !cube.calculateIsAir() && cube.needsSaving(forceSave)) {
						cube.lastSaveTime = this.worldObj.getWorldTime();
						this.chunkLoader.saveCube(this.worldObj, cube);
					}
				}
			} catch (IOException iOException5) {
				iOException5.printStackTrace();
			}

		}
	}

	public void populate(IChunkProvider ichunkprovider, int x, int y, int z) {
		ChunkCube cube = this.provideCube(x, y, z);
		if(!cube.isTerrainPopulated) {
			cube.isTerrainPopulated = true;
			if(this.chunkProvider != null) {
				this.chunkProvider.populate(ichunkprovider, x, y, z);
				cube.setCubeModified();
			}
		}

	}

	public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
		int i = 0;
		int j = 0;
		int l;
		if(iprogressupdate != null) {
			for(l = 0; l < this.chunks.length; ++l) {
				if(this.chunks[l] != null && this.chunks[l].needsSaving(flag)) {
					++j;
				}
			}
		}

		l = 0;

		for(int i1 = 0; i1 < this.chunks.length; ++i1) {
			if(this.chunks[i1] != null) {
				if(flag && !this.chunks[i1].neverSave) {
					this.saveExtraChunkData(this.chunks[i1]);
				}

				if(this.chunks[i1].needsSaving(flag)) {
					this.saveChunk(this.chunks[i1], flag);
					this.chunks[i1].isModified = false;
					++i;
					if(i == 2 && !flag) {
						return false;
					}

					if(iprogressupdate != null) {
						++l;
						if(l % 10 == 0) {
							iprogressupdate.setLoadingProgress(l * 100 / j);
						}
					}
				}
			}
		}

		if(flag) {
			if(this.chunkLoader == null) {
				return true;
			}

			this.chunkLoader.saveExtraData();
		}

		return true;
	}

	public boolean saveCube(boolean flag, ChunkCube cube) {
		Chunk chunk = cube.chunk;
		if(flag && !chunk.neverSave) {
			this.saveExtraChunkData(chunk);
		}

		if(cube.needsSaving(flag)) {
			if(this.chunkLoader != null) {
				try {
					cube.lastSaveTime = this.worldObj.getWorldTime();
					this.chunkLoader.saveCube(this.worldObj, cube);
				} catch (IOException iOException5) {
					iOException5.printStackTrace();
				}
			}

			cube.isModified = false;
		}

		if(flag && this.chunkLoader != null) {
			this.chunkLoader.saveExtraData();
		}

		return true;
	}

	public boolean unload100OldestChunks() {
		if(this.chunkLoader != null) {
			this.chunkLoader.func_814_a();
		}

		return this.chunkProvider.unload100OldestChunks();
	}

	public boolean canSave() {
		return true;
	}

	public String makeString() {
		return "ChunkCache: " + this.chunks.length;
	}
}
