package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChunkProvider implements IChunkProvider {
	private Set droppedChunksSet = new HashSet();
	private Chunk field_28064_b;
	private ChunkCube blankCube;
	private IChunkProvider chunkProvider;
	private IChunkLoader chunkLoader;
	private Map chunkMap = new HashMap();
	private List chunkList = new ArrayList();
	private World worldObj;

	public ChunkProvider(World world, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
		this.field_28064_b = new EmptyChunk(world, new byte[32768], 0, 0);
		this.blankCube = new EmptyCube(this.field_28064_b, new byte[4096], 0);
		this.worldObj = world;
		this.chunkLoader = ichunkloader;
		this.chunkProvider = ichunkprovider;
	}

	public boolean cubeExists(int i, int y, int j) {
		if(y < 2047 && y >= -2047) {
			Chunk ch = (Chunk)this.chunkMap.get(ChunkCoordIntPair.chunkXZ2Int(i, j));
			if(ch != null) {
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

	public ChunkCube prepareCube(int x, int y, int z) {
		Chunk chunk = (Chunk)this.chunkMap.get(ChunkCoordIntPair.chunkXZ2Int(x, z));
		if(chunk == null) {
			chunk = this.prepareChunk(x, z);
		}

		ChunkCube cube = chunk.cubes[y + 2047];
		if(cube == null) {
			cube = this.loadCubeFromFile(x, y, z);
			if(cube == null && this.chunkProvider != null) {
				byte dimension = -1;
				if(this.chunkProvider.makeString().equals("RandomLevelSource")) {
					dimension = 0;
				} else if(this.chunkProvider.makeString().equals("HellRandomLevelSource")) {
					dimension = 1;
				} else if(this.chunkProvider.makeString().equals("SkyRandomLevelSource")) {
					dimension = 2;
				}

				List mods = ModLoader.getLoadedMods();
				Iterator iterator = mods.iterator();

				while(iterator.hasNext() && cube == null) {
					BaseMod mod = (BaseMod)iterator.next();
					if(mod instanceof ICubePopulator) {
						ICubePopulator pop = (ICubePopulator)mod;
						if(dimension == 0) {
							if(pop.canGenerateSurface(y)) {
								cube = pop.GenerateSurface(x, y, z);
							}
						} else if(dimension == 1) {
							if(pop.canGenerateNether(y)) {
								cube = pop.GenerateNether(x, y, z);
							}
						} else if(dimension == 2 && pop.canGenerateSky(y)) {
							cube = pop.GenerateSky(x, y, z);
						}
					}
				}

				if(cube == null) {
					cube = this.chunkProvider.provideCube(x, y, z);
				}

				if(!cube.isTerrainPopulated && this.cubeExists(x + 1, y, z) && this.cubeExists(x, y + 1, z) && this.cubeExists(x + 1, y + 1, z) && this.cubeExists(x, y, z + 1) && this.cubeExists(x + 1, y, z + 1) && this.cubeExists(x, y + 1, z + 1) && this.cubeExists(x + 1, y + 1, z + 1)) {
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

			if(cube != null) {
				cube.func_4143_d();
				cube.onChunkLoad();
			}
		}

		return cube;
	}

	private void populateIfNearbyCubesExist(int x, int y, int z) {
		if(this.cubeExists(x, y, z)) {
			ChunkCube cube = this.provideCube(x, y, z);
			if(!cube.isTerrainPopulated && this.cubeExists(x + 1, y, z) && this.cubeExists(x, y + 1, z) && this.cubeExists(x + 1, y + 1, z) && this.cubeExists(x, y, z + 1) && this.cubeExists(x + 1, y, z + 1) && this.cubeExists(x, y + 1, z + 1) && this.cubeExists(x + 1, y + 1, z + 1)) {
				this.populate(this, x, y, z);
			}
		}

	}

	public Chunk prepareChunk(int i, int j) {
		int k = ChunkCoordIntPair.chunkXZ2Int(i, j);
		this.droppedChunksSet.remove(k);
		Chunk chunk = (Chunk)this.chunkMap.get(k);
		if(chunk == null) {
			chunk = this.loadChunkFromFile(i, j);
			if(chunk == null) {
				if(this.chunkProvider == null) {
					chunk = this.field_28064_b;
				} else {
					chunk = this.chunkProvider.provideChunk(i, j);
				}
			}

			this.chunkMap.put(k, chunk);
			this.chunkList.add(chunk);
			if(chunk != null) {
				chunk.func_4143_d();
				chunk.onChunkLoad();
			}

			int x = i;
			int z = j;
			int y = 0;
			this.populateIfNearbyCubesExist(i, y - 1, j);
			this.populateIfNearbyCubesExist(i - 1, y - 1, j);
			this.populateIfNearbyCubesExist(i, y - 1, j - 1);
			this.populateIfNearbyCubesExist(i - 1, y - 1, j - 1);

			while(y < 8) {
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
		Chunk chunk = (Chunk)this.chunkMap.get(ChunkCoordIntPair.chunkXZ2Int(i, j));
		return chunk == null ? this.prepareChunk(i, j) : chunk;
	}

	public ChunkCube provideCube(int x, int y, int z) {
		if(y >= -2047 && y < 2047) {
			Chunk chunk = (Chunk)this.chunkMap.get(ChunkCoordIntPair.chunkXZ2Int(x, z));
			if(chunk == null) {
				chunk = this.prepareChunk(x, z);
			}

			return chunk.cubes[y + 2047] == null ? this.prepareCube(x, y, z) : chunk.cubes[y + 2047];
		} else {
			return this.blankCube;
		}
	}

	private Chunk loadChunkFromFile(int i, int j) {
		if(this.chunkLoader == null) {
			return null;
		} else {
			try {
				Chunk exception = this.chunkLoader.loadChunk(this.worldObj, i, j);
				if(exception != null) {
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
		if(this.chunkLoader == null) {
			return null;
		} else {
			try {
				ChunkCube exception = this.chunkLoader.loadCube(this.worldObj, x, y, z);
				if(exception != null) {
					exception.lastSaveTime = this.worldObj.getWorldTime();
				}

				return exception;
			} catch (Exception exception5) {
				exception5.printStackTrace();
				return null;
			}
		}
	}

	private void func_28063_a(Chunk chunk) {
		if(this.chunkLoader != null) {
			try {
				this.chunkLoader.saveExtraChunkData(this.worldObj, chunk);
			} catch (Exception exception3) {
				exception3.printStackTrace();
			}

		}
	}

	private void func_28062_b(Chunk chunk, boolean forceSave) {
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

	public void populate(IChunkProvider ichunkprovider, int i, int y, int j) {
		ChunkCube cube = this.provideCube(i, y, j);
		if(!cube.isTerrainPopulated) {
			cube.isTerrainPopulated = true;
			if(this.chunkProvider != null) {
				this.chunkProvider.populate(ichunkprovider, i, y, j);
				if(1 == y) {
					ModLoader.PopulateChunk(this.chunkProvider, i << 4, j << 4, this.worldObj);
				}

				byte dimension = -1;
				if(ichunkprovider.makeString().equals("RandomLevelSource")) {
					dimension = 0;
				} else if(ichunkprovider.makeString().equals("HellRandomLevelSource")) {
					dimension = 1;
				} else if(ichunkprovider.makeString().equals("SkyRandomLevelSource")) {
					dimension = 2;
				}

				List mods = ModLoader.getLoadedMods();
				Iterator iterator = mods.iterator();

				while(iterator.hasNext()) {
					BaseMod mod = (BaseMod)iterator.next();
					if(mod instanceof ICubePopulator) {
						ICubePopulator pop = (ICubePopulator)mod;
						if(dimension == 0) {
							pop.PopulateSurface(this.worldObj, this.worldObj.rand, i, y, j);
						} else if(dimension == 1) {
							pop.PopulateNether(this.worldObj, this.worldObj.rand, i, y, j);
						} else if(dimension == 2) {
							pop.PopulateSky(this.worldObj, this.worldObj.rand, i, y, j);
						}
					}
				}

				cube.setCubeModified();
			}
		}

	}

	public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
		int i = 0;

		for(int j = 0; j < this.chunkList.size(); ++j) {
			Chunk chunk = (Chunk)this.chunkList.get(j);
			if(flag && !chunk.neverSave) {
				this.func_28063_a(chunk);
			}

			if(chunk.needsSaving(flag)) {
				this.func_28062_b(chunk, flag);
				chunk.isModified = false;
				++i;
				if(i == 24 && !flag) {
					return false;
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
			this.func_28063_a(chunk);
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
		for(int i = 0; i < 100; ++i) {
			if(!this.droppedChunksSet.isEmpty()) {
				Integer integer = (Integer)this.droppedChunksSet.iterator().next();
				Chunk chunk = (Chunk)this.chunkMap.get(integer);
				chunk.onChunkUnload();
				this.func_28062_b(chunk, true);
				this.func_28063_a(chunk);
				this.droppedChunksSet.remove(integer);
				this.chunkMap.remove(integer);
				this.chunkList.remove(chunk);
			}
		}

		if(this.chunkLoader != null) {
			this.chunkLoader.func_814_a();
		}

		return this.chunkProvider.unload100OldestChunks();
	}

	public boolean canSave() {
		return true;
	}

	public String makeString() {
		return "ServerChunkCache: " + this.chunkMap.size() + " Drop: " + this.droppedChunksSet.size();
	}
}
