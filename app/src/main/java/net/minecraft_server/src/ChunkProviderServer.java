package net.minecraft_server.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChunkProviderServer implements IChunkProvider {
	private Set field_725_a = new HashSet();
	private Chunk dummyChunk;
	private ChunkCube dummyCube;
	private IChunkProvider serverChunkGenerator;
	private IChunkLoader field_729_d;
	public boolean chunkLoadOverride = false;
	private Map id2ChunkMap = new HashMap();
	private List field_727_f = new ArrayList();
	private WorldServer world;

	public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
		this.dummyChunk = new EmptyChunk(worldserver, new byte[32768], 0, 0);
		this.dummyCube = new EmptyCube(this.dummyChunk, new byte[4096], 0);
		this.world = worldserver;
		this.field_729_d = ichunkloader;
		this.serverChunkGenerator = ichunkprovider;
	}

	public boolean cubeExists(int i, int y, int j) {
		if (y < 2047 && y >= -2047) {
			Chunk ch = (Chunk) this.id2ChunkMap.get(ChunkCoordIntPair.chunkXZ2Int(i, j));
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
		return this.id2ChunkMap.containsKey(ChunkCoordIntPair.chunkXZ2Int(i, j));
	}

	public ChunkCube loadCube(int x, int y, int z) {
		Chunk chunk = (Chunk) this.id2ChunkMap.get(ChunkCoordIntPair.chunkXZ2Int(x, z));
		if (chunk == null) {
			chunk = this.loadChunk(x, z);
		}

		ChunkCube cube = chunk.cubes[y + 2047];
		if (cube == null) {
			cube = this.loadCubeFromFile(x, y, z);
			if (cube == null && this.serverChunkGenerator != null) {
				cube = this.serverChunkGenerator.loadCube(x, y, z);
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
			ChunkCube cube = this.loadCube(x, y, z);
			if (!cube.isTerrainPopulated && this.cubeExists(x + 1, y, z) && this.cubeExists(x, y + 1, z)
					&& this.cubeExists(x + 1, y + 1, z) && this.cubeExists(x, y, z + 1)
					&& this.cubeExists(x + 1, y, z + 1) && this.cubeExists(x, y + 1, z + 1)
					&& this.cubeExists(x + 1, y + 1, z + 1)) {
				this.populate(this, x, y, z);
			}
		}

	}

	public void func_374_c(int i, int j) {
		ChunkCoordinates chunkcoordinates = this.world.getSpawnPoint();
		int k = i * 16 + 8 - chunkcoordinates.posX;
		int l = j * 16 + 8 - chunkcoordinates.posZ;
		short c = 128;
		if (k < -c || k > c || l < -c || l > c) {
			this.field_725_a.add(ChunkCoordIntPair.chunkXZ2Int(i, j));
		}

	}

	public Chunk loadChunk(int i, int j) {
		int k = ChunkCoordIntPair.chunkXZ2Int(i, j);
		this.field_725_a.remove(k);
		Chunk chunk = (Chunk) this.id2ChunkMap.get(k);
		if (chunk == null) {
			chunk = this.func_4063_e(i, j);
			if (chunk == null) {
				if (this.serverChunkGenerator == null) {
					chunk = this.dummyChunk;
				} else {
					chunk = this.serverChunkGenerator.provideChunk(i, j);
				}
			}

			this.id2ChunkMap.put(k, chunk);
			this.field_727_f.add(chunk);
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
		Chunk chunk = (Chunk) this.id2ChunkMap.get(ChunkCoordIntPair.chunkXZ2Int(i, j));
		return chunk == null
				? (!this.world.worldChunkLoadOverride && !this.chunkLoadOverride ? this.dummyChunk
						: this.loadChunk(i, j))
				: chunk;
	}

	public ChunkCube provideCube(int x, int y, int z) {
		if (y >= -2047 && y < 2047) {
			Chunk chunk = (Chunk) this.id2ChunkMap.get(ChunkCoordIntPair.chunkXZ2Int(x, z));
			if (chunk == null) {
				chunk = this.loadChunk(x, z);
			}

			return chunk.cubes[y + 2047] == null ? this.loadCube(x, y, z) : chunk.cubes[y + 2047];
		} else {
			return this.dummyCube;
		}
	}

	private Chunk func_4063_e(int i, int j) {
		if (this.field_729_d == null) {
			return null;
		} else {
			try {
				Chunk exception = this.field_729_d.loadChunk(this.world, i, j);
				if (exception != null) {
					exception.lastSaveTime = this.world.getWorldTime();
				}

				return exception;
			} catch (Exception exception4) {
				exception4.printStackTrace();
				return null;
			}
		}
	}

	private ChunkCube loadCubeFromFile(int x, int y, int z) {
		if (this.field_729_d == null) {
			return null;
		} else {
			try {
				ChunkCube exception = this.field_729_d.loadCube(this.world, x, y, z);
				if (exception != null) {
					exception.lastSaveTime = this.world.getWorldTime();
				}

				return exception;
			} catch (Exception exception5) {
				exception5.printStackTrace();
				return null;
			}
		}
	}

	private void func_375_a(Chunk chunk) {
		if (this.field_729_d != null) {
			try {
				this.field_729_d.saveExtraChunkData(this.world, chunk);
			} catch (Exception exception3) {
				exception3.printStackTrace();
			}

		}
	}

	private void func_373_b(Chunk chunk) {
		if (this.field_729_d != null) {
			try {
				chunk.lastSaveTime = this.world.getWorldTime();
				this.field_729_d.saveChunk(this.world, chunk);
			} catch (IOException iOException3) {
				iOException3.printStackTrace();
			}

		}
	}

	public void populate(IChunkProvider ichunkprovider, int i, int y, int j) {
		ChunkCube cube = this.provideCube(i, y, j);
		if (!cube.isTerrainPopulated) {
			cube.isTerrainPopulated = true;
			if (this.serverChunkGenerator != null) {
				this.serverChunkGenerator.populate(ichunkprovider, i, y, j);
				cube.setCubeModified();
			}
		}

	}

	public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
		int i = 0;

		for (int j = 0; j < this.field_727_f.size(); ++j) {
			Chunk chunk = (Chunk) this.field_727_f.get(j);
			if (flag && !chunk.neverSave) {
				this.func_375_a(chunk);
			}

			if (chunk.needsSaving(flag)) {
				this.func_373_b(chunk);
				chunk.isModified = false;
				++i;
				if (i == 24 && !flag) {
					return false;
				}
			}
		}

		if (flag) {
			if (this.field_729_d == null) {
				return true;
			}

			this.field_729_d.saveExtraData();
		}

		return true;
	}

	public boolean saveCube(boolean flag, ChunkCube cube) {
		Chunk chunk = cube.chunk;
		if (flag && !chunk.neverSave) {
			this.func_375_a(chunk);
		}

		if (cube.needsSaving(flag)) {
			if (this.field_729_d != null) {
				try {
					cube.lastSaveTime = this.world.getWorldTime();
					this.field_729_d.saveCube(this.world, cube);
				} catch (IOException iOException5) {
					iOException5.printStackTrace();
				}
			}

			cube.isModified = false;
		}

		if (flag && this.field_729_d != null) {
			this.field_729_d.saveExtraData();
		}

		return true;
	}

	public boolean unload100OldestChunks() {
		if (!this.world.levelSaving) {
			for (int i = 0; i < 100; ++i) {
				if (!this.field_725_a.isEmpty()) {
					Integer integer = (Integer) this.field_725_a.iterator().next();
					Chunk chunk = (Chunk) this.id2ChunkMap.get(integer);
					if (chunk != null) {
						chunk.onChunkUnload();
						this.func_373_b(chunk);
						this.func_375_a(chunk);
						this.field_727_f.remove(chunk);
					}

					this.field_725_a.remove(integer);
					this.id2ChunkMap.remove(integer);
				}
			}

			if (this.field_729_d != null) {
				this.field_729_d.func_661_a();
			}
		}

		return this.serverChunkGenerator.unload100OldestChunks();
	}

	public boolean canSave() {
		return !this.world.levelSaving;
	}
}
