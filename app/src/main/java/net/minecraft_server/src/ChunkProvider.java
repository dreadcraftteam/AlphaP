package net.minecraft_server.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChunkProvider implements IChunkProvider {
	private Set droppedChunksSet = new HashSet();
	private Chunk field_28061_b;
	private ChunkCube blankCube = new EmptyCube(this.field_28061_b, new byte[4096], 0);
	private IChunkProvider chunkProvider;
	private IChunkLoader chunkLoader;
	private Map chunkMap = new HashMap();
	private List chunkList = new ArrayList();
	private World worldObj;

	public ChunkProvider(World world, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
		this.field_28061_b = new EmptyChunk(world, new byte[32768], 0, 0);
		this.worldObj = world;
		this.chunkLoader = ichunkloader;
		this.chunkProvider = ichunkprovider;
	}

	public boolean cubeExists(int i, int y, int j) {
		if (y < 2047 && y >= -2047) {
			Chunk ch = (Chunk) this.chunkMap.get(ChunkCoordIntPair.chunkXZ2Int(i, j));
			if (ch != null) {
				int cube = y + 2047;
				return ch.cubes[cube] != null;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean chunkExists(int i, int j) {
		return this.chunkMap.containsKey(ChunkCoordIntPair.chunkXZ2Int(i, j));
	}

	public ChunkCube loadCube(int x, int y, int z) {
		Chunk chunk = (Chunk) this.chunkMap.get(ChunkCoordIntPair.chunkXZ2Int(x, z));
		if (chunk == null) {
			chunk = this.loadChunk(x, z);
		}

		ChunkCube cube = chunk.cubes[y + 2047];
		if (cube == null) {
			cube = this.loadCubeFromFile(x, y, z);
			if (cube == null && this.chunkProvider != null) {
				cube = this.chunkProvider.provideCube(x, y, z);
				if (!cube.isTerrainPopulated && this.cubeExists(x + 1, y, z) && this.cubeExists(x, y + 1, z)
						&& this.cubeExists(x + 1, y + 1, z) && this.cubeExists(x, y, z + 1)
						&& this.cubeExists(x + 1, y, z + 1) && this.cubeExists(x, y + 1, z + 1)
						&& this.cubeExists(x + 1, y + 1, z + 1)) {
					this.populate(this, x, y, z);
				}

				this.populateIfNearbyCubesExist(x - 1, y, z);
				this.populateIfNearbyCubesExist(x, y - 1, z);
				this.populateIfNearbyCubesExist(x - 1, y - 1, z);
				this.populateIfNearbyCubesExist(x, y, z - 1);
				this.populateIfNearbyCubesExist(x - 1, y, z - 1);
				this.populateIfNearbyCubesExist(x, y - 1, z - 1);
				this.populateIfNearbyCubesExist(x - 1, y - 1, z - 1);
			}

			if (cube != null) {
				cube.func_4143_d();
				cube.onChunkLoad();
			}
		}

		return cube;
	}

	private void populateIfNearbyCubesExist(int x, int y, int z) {
		if (this.cubeExists(x, y, z)) {
			ChunkCube cube = this.provideCube(x, y, z);
			if (!cube.isTerrainPopulated && this.cubeExists(x + 1, y, z) && this.cubeExists(x, y + 1, z)
					&& this.cubeExists(x + 1, y + 1, z) && this.cubeExists(x, y, z + 1)
					&& this.cubeExists(x + 1, y, z + 1) && this.cubeExists(x, y + 1, z + 1)
					&& this.cubeExists(x + 1, y + 1, z + 1)) {
				this.populate(this, x, y, z);
			}
		}

	}

	public Chunk loadChunk(int i, int j) {
		int k = ChunkCoordIntPair.chunkXZ2Int(i, j);
		this.droppedChunksSet.remove(k);
		Chunk chunk = (Chunk) this.chunkMap.get(k);
		if (chunk == null) {
			chunk = this.func_28058_d(i, j);
			if (chunk == null) {
				if (this.chunkProvider == null) {
					chunk = this.field_28061_b;
				} else {
					chunk = this.chunkProvider.provideChunk(i, j);
				}
			}

			this.chunkMap.put(k, chunk);
			this.chunkList.add(chunk);
			if (chunk != null) {
				chunk.func_4053_c();
				chunk.onChunkLoad();
			}

			int x = i;
			int z = j;
			int y = 0;
			this.populateIfNearbyCubesExist(i, y - 1, j);
			this.populateIfNearbyCubesExist(i - 1, y - 1, j);
			this.populateIfNearbyCubesExist(i, y - 1, j - 1);
			this.populateIfNearbyCubesExist(i - 1, y - 1, j - 1);

			while (y < 8) {
				this.populateIfNearbyCubesExist(x, y, z);
				this.populateIfNearbyCubesExist(x - 1, y, z);
				this.populateIfNearbyCubesExist(x, y, z - 1);
				this.populateIfNearbyCubesExist(x - 1, y, z - 1);
				++y;
			}
		}

		return chunk;
	}

	public Chunk provideChunk(int i, int j) {
		Chunk chunk = (Chunk) this.chunkMap.get(ChunkCoordIntPair.chunkXZ2Int(i, j));
		return chunk == null ? this.loadChunk(i, j) : chunk;
	}

	public ChunkCube provideCube(int x, int y, int z) {
		if (y >= -2047 && y < 2047) {
			Chunk chunk = (Chunk) this.chunkMap.get(ChunkCoordIntPair.chunkXZ2Int(x, z));
			if (chunk == null) {
				chunk = this.loadChunk(x, z);
			}

			return chunk.cubes[y + 2047] == null ? this.loadCube(x, y, z) : chunk.cubes[y + 2047];
		} else {
			return this.blankCube;
		}
	}

	private Chunk func_28058_d(int i, int j) {
		if (this.chunkLoader == null) {
			return null;
		} else {
			try {
				Chunk exception = this.chunkLoader.loadChunk(this.worldObj, i, j);
				if (exception != null) {
					exception.lastSaveTime = this.worldObj.getWorldTime();
				}

				return exception;
			} catch (Exception exception4) {
				exception4.printStackTrace();
				return null;
			}
		}
	}

	private ChunkCube loadCubeFromFile(int x, int y, int z) {
		if (this.chunkLoader == null) {
			return null;
		} else {
			try {
				ChunkCube exception = this.chunkLoader.loadCube(this.worldObj, x, y, z);
				if (exception != null) {
					exception.lastSaveTime = this.worldObj.getWorldTime();
				}

				return exception;
			} catch (Exception exception5) {
				exception5.printStackTrace();
				return null;
			}
		}
	}

	private void func_28060_a(Chunk chunk) {
		if (this.chunkLoader != null) {
			try {
				this.chunkLoader.saveExtraChunkData(this.worldObj, chunk);
			} catch (Exception exception3) {
				exception3.printStackTrace();
			}

		}
	}

	private void func_28059_b(Chunk chunk) {
		if (this.chunkLoader != null) {
			try {
				chunk.lastSaveTime = this.worldObj.getWorldTime();
				this.chunkLoader.saveChunk(this.worldObj, chunk);
			} catch (IOException iOException3) {
				iOException3.printStackTrace();
			}

		}
	}

	public void populate(IChunkProvider ichunkprovider, int i, int y, int j) {
		ChunkCube cube = this.provideCube(i, y, j);
		if (!cube.isTerrainPopulated) {
			cube.isTerrainPopulated = true;
			if (this.chunkProvider != null) {
				this.chunkProvider.populate(ichunkprovider, i, y, j);
				cube.setCubeModified();
			}
		}

	}

	public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
		int i = 0;

		for (int j = 0; j < this.chunkList.size(); ++j) {
			Chunk chunk = (Chunk) this.chunkList.get(j);
			if (flag && !chunk.neverSave) {
				this.func_28060_a(chunk);
			}

			if (chunk.needsSaving(flag)) {
				this.func_28059_b(chunk);
				chunk.isModified = false;
				++i;
				if (i == 24 && !flag) {
					return false;
				}
			}
		}

		if (flag) {
			if (this.chunkLoader == null) {
				return true;
			}

			this.chunkLoader.saveExtraData();
		}

		return true;
	}

	public boolean saveCube(boolean flag, ChunkCube cube) {
		Chunk chunk = cube.chunk;
		if (flag && !chunk.neverSave) {
			this.func_28060_a(chunk);
		}

		if (cube.needsSaving(flag)) {
			if (this.chunkLoader != null) {
				try {
					cube.lastSaveTime = this.worldObj.getWorldTime();
					this.chunkLoader.saveCube(this.worldObj, cube);
				} catch (IOException iOException5) {
					iOException5.printStackTrace();
				}
			}

			cube.isModified = false;
		}

		if (flag && this.chunkLoader != null) {
			this.chunkLoader.saveExtraData();
		}

		return true;
	}

	public boolean unload100OldestChunks() {
		for (int i = 0; i < 100; ++i) {
			if (!this.droppedChunksSet.isEmpty()) {
				Integer integer = (Integer) this.droppedChunksSet.iterator().next();
				Chunk chunk = (Chunk) this.chunkMap.get(integer);
				chunk.onChunkUnload();
				this.func_28059_b(chunk);
				this.func_28060_a(chunk);
				this.droppedChunksSet.remove(integer);
				this.chunkMap.remove(integer);
				this.chunkList.remove(chunk);
			}
		}

		if (this.chunkLoader != null) {
			this.chunkLoader.func_661_a();
		}

		return this.chunkProvider.unload100OldestChunks();
	}

	public boolean canSave() {
		return true;
	}
}
