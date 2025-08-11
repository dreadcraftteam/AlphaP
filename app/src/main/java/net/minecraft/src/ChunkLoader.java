package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class ChunkLoader implements IChunkLoader {
	private File saveDir;
	private boolean createIfNecessary;
	public static McRegionChunkLoader callbackMCRegion;

	public ChunkLoader(File file, boolean flag) {
		this.saveDir = file;
		this.createIfNecessary = flag;
	}

	private File chunkFileForXZ(int i, int j) {
		String s = "c." + Integer.toString(i, 36) + "." + Integer.toString(j, 36) + ".dat";
		String s1 = Integer.toString(i & 63, 36);
		String s2 = Integer.toString(j & 63, 36);
		File file = new File(this.saveDir, s1);
		if(!file.exists()) {
			if(!this.createIfNecessary) {
				return null;
			}

			file.mkdir();
		}

		file = new File(file, s2);
		if(!file.exists()) {
			if(!this.createIfNecessary) {
				return null;
			}

			file.mkdir();
		}

		file = new File(file, s);
		return !file.exists() && !this.createIfNecessary ? null : file;
	}

	public Chunk loadChunk(World world, int i, int j) throws IOException {
		File file = this.chunkFileForXZ(i, j);
		if(file != null && file.exists()) {
			try {
				FileInputStream exception = new FileInputStream(file);
				NBTTagCompound nbttagcompound = CompressedStreamTools.func_1138_a(exception);
				if(!nbttagcompound.hasKey("Level")) {
					System.out.println("Chunk file at " + i + "," + j + " is missing level data, skipping");
					return null;
				}

				if(!nbttagcompound.getCompoundTag("Level").hasKey("Blocks")) {
					System.out.println("Chunk file at " + i + "," + j + " is missing block data, skipping");
					return null;
				}

				Chunk chunk = loadChunkIntoWorldFromCompound(world, nbttagcompound.getCompoundTag("Level"), 0, 7);
				if(!chunk.isAtLocation(i, j)) {
					System.out.println("Chunk file at " + i + "," + j + " is in the wrong location; relocating. (Expected " + i + ", " + j + ", got " + chunk.xPosition + ", " + chunk.zPosition + ")");
					nbttagcompound.setInteger("xPos", i);
					nbttagcompound.setInteger("zPos", j);
					chunk = loadChunkIntoWorldFromCompound(world, nbttagcompound.getCompoundTag("Level"), 0, 7);
				}

				chunk.func_25124_i();
				return chunk;
			} catch (Exception exception8) {
				exception8.printStackTrace();
			}
		}

		return null;
	}

	public ChunkCube loadCube(World world, int x, int y, int z) throws IOException {
		File file = this.chunkFileForXZ(x, z);
		if(file != null && file.exists()) {
			try {
				FileInputStream exception = new FileInputStream(file);
				NBTTagCompound nbttagcompound = CompressedStreamTools.func_1138_a(exception);
				if(!nbttagcompound.hasKey("Level")) {
					System.out.println("Chunk file at " + x + "," + z + " is missing level data, skipping");
					return null;
				}

				NBTTagCompound level = nbttagcompound.getCompoundTag("Level");
				if(!level.hasKey("Cubes")) {
					System.out.println("Chunk file at " + x + "," + z + " is missing block data, skipping");
					return null;
				}

				NBTTagCompound cubes = level.getCompoundTag("Cubes");
				String chunkName = "Ch" + (y + 2047);
				if(!cubes.hasKey(chunkName)) {
					System.out.println("Chunk file at " + x + "," + z + " is missing cube at" + y + ", skipping");
					return null;
				}

				ChunkCube cube = loadChunkIntoWorldFromCompound(world, level, y, y).cubes[y + 2047];
				if(!cube.isAtLocation(x, y, z)) {
					System.out.println("Cube file at " + x + "," + y + "," + z + " is in the wrong location; relocating. (Expected " + x + ", " + z + ", got " + cube.chunk.xPosition + ", " + cube.yPosition + ", " + cube.chunk.zPosition + ")");
				}

				cube.removeUnknownBlocks();
				return cube;
			} catch (Exception exception12) {
				exception12.printStackTrace();
			}
		}

		return null;
	}

	public void saveChunk(World world, Chunk chunk) throws IOException {
		world.checkSessionLock();
		File file = this.chunkFileForXZ(chunk.xPosition, chunk.zPosition);
		if(file.exists()) {
			WorldInfo exception = world.getWorldInfo();
			exception.setSizeOnDisk(exception.getSizeOnDisk() - file.length());
		}

		try {
			File exception1 = new File(this.saveDir, "tmp_chunk.dat");
			FileOutputStream fileoutputstream = new FileOutputStream(exception1);
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound.setTag("Level", nbttagcompound1);
			storeChunkInCompound(chunk, world, nbttagcompound1);
			CompressedStreamTools.writeGzippedCompoundToOutputStream(nbttagcompound, fileoutputstream);
			fileoutputstream.close();
			if(file.exists()) {
				file.delete();
			}

			exception1.renameTo(file);
			WorldInfo worldinfo1 = world.getWorldInfo();
			worldinfo1.setSizeOnDisk(worldinfo1.getSizeOnDisk() + file.length());
		} catch (Exception exception9) {
			exception9.printStackTrace();
		}

	}

	public void saveCube(World world, ChunkCube cube) {
	}

	public static void storeCubeInCompound(ChunkCube cube, World world, NBTTagCompound level) {
		world.checkSessionLock();
		level.setInteger("yPos", cube.yPosition);
		level.setByteArray("Blocks", cube.blocks);
		level.setByteArray("Data", cube.data.data);
		level.setByteArray("SkyLight", cube.skylightMap.data);
		level.setByteArray("BlockLight", cube.blocklightMap.data);
		level.setBoolean("TerrainPopulated", cube.isTerrainPopulated);
		NBTTagList entities = new NBTTagList();
		Iterator tileEntities = cube.entities.iterator();

		while(tileEntities.hasNext()) {
			Entity i$ = (Entity)tileEntities.next();
			cube.chunk.hasEntities = true;
			NBTTagCompound tileEntity = new NBTTagCompound();
			if(i$.addEntityID(tileEntity)) {
				entities.setTag(tileEntity);
			}
		}

		level.setTag("Entities", entities);
		NBTTagList tileEntities1 = new NBTTagList();
		Iterator i$1 = cube.chunkTileEntityMap.values().iterator();

		while(i$1.hasNext()) {
			TileEntity tileEntity1 = (TileEntity)i$1.next();
			if(tileEntity1.yCoord >> 4 == cube.yPosition) {
				NBTTagCompound temp = new NBTTagCompound();
				tileEntity1.writeToNBT(temp);
				tileEntities1.setTag(temp);
			} else {
				System.out.println("Attempted to save a TileEntity in the wrong ChunkCube!");
			}
		}

		level.setTag("TileEntities", tileEntities1);
	}

	public static void storeChunkInCompound(Chunk chunk, World world, NBTTagCompound nbttagcompound) {
		world.checkSessionLock();
		nbttagcompound.setInteger("xPos", chunk.xPosition);
		nbttagcompound.setInteger("zPos", chunk.zPosition);
		nbttagcompound.setLong("LastUpdate", world.getWorldTime());
		nbttagcompound.setShortArray("HeightMap", chunk.h);
		chunk.hasEntities = false;
	}

	public static ChunkCube loadCubeIntoWorldFromCompound(World world, NBTTagCompound cubeTag, int x, int y, int z) {
		int yStored = cubeTag.getInteger("yPos");
		if(yStored != y) {
			System.out.println("Cube file at " + x + "," + y + "," + z + " is in the wrong location; relocating. (Expected yPosition: " + y + ", got " + yStored + ")");
		}

		Chunk chunk = world.getChunkFromChunkCoords(x, z);
		ChunkCube cube = chunk.cubes[y + 2047] = new ChunkCube(chunk, y + 2047);
		cube.blocks = cubeTag.getByteArray("Blocks");
		cube.isChunkLoaded = true;
		cube.data = new NibbleArray(cubeTag.getByteArray("Data"));
		cube.skylightMap = new NibbleArray(cubeTag.getByteArray("SkyLight"));
		cube.blocklightMap = new NibbleArray(cubeTag.getByteArray("BlockLight"));
		cube.isTerrainPopulated = cubeTag.getBoolean("TerrainPopulated");
		if(cube.blocks == null || cube.blocks.length != 4096) {
			cube.blocks = new byte[4096];
			cube.isTerrainPopulated = false;
		}

		if(!cube.data.isValid() || cube.data.data.length == 0) {
			cube.data = new NibbleArray(cube.blocks.length);
		}

		if(!cube.blocklightMap.isValid() || cube.blocklightMap.data.length == 0) {
			cube.blocklightMap = new NibbleArray(cube.blocks.length);
			chunk.func_1014_a();
		}

		if(!cube.skylightMap.isValid() || cube.skylightMap.data.length == 0) {
			cube.skylightMap = new NibbleArray(cube.blocks.length);
			chunk.func_1024_c();
		}

		if(y == 0) {
			ChunkBlockMap.enableBedrockConversion();
			cube.removeUnknownBlocks();
			ChunkBlockMap.disableBedrockConversion();
		} else {
			cube.removeUnknownBlocks();
		}

		loadEntities(cubeTag, chunk, world);
		loadTileEntities(cubeTag, chunk);
		cube.regenOnLoad();
		return cube;
	}

	public static Chunk loadChunkIntoWorldFromCompound(World world, NBTTagCompound compound) {
		return loadChunkIntoWorldFromCompound(world, compound, -2047, 2046);
	}

	public static Chunk loadChunkIntoWorldFromCompound(World world, NBTTagCompound nbttagcompound, int yStart, int yEnd) {
		yStart += 2047;
		yEnd += 2047;
		int x = nbttagcompound.getInteger("xPos");
		int z = nbttagcompound.getInteger("zPos");
		Chunk chunk;
		if(world.chunkExists(x, z)) {
			chunk = world.getChunkFromChunkCoords(x, z);
		} else {
			chunk = new Chunk(world, x, z);
		}

		boolean regenBlocklightMap = false;
		boolean regenSkylightMap = false;
		int n;
		byte[] b15;
		if(nbttagcompound.hasKey("Cubes")) {
			NBTTagCompound heightMap = nbttagcompound.getCompoundTag("Cubes");

			for(n = yStart; n <= yEnd; ++n) {
				String isPopulated = "Ch" + n;
				if(heightMap.hasKey(isPopulated)) {
					int md = n - 2047;
					NBTTagCompound outer = new NBTTagCompound();
					NBTTagCompound level = heightMap.getCompoundTag(isPopulated);
					level.setInteger("yPos", md);
					outer.setTag("Level", level);
					callbackMCRegion.saveConvertedCube(world, outer, x, md, z);
				}
			}
		} else if(nbttagcompound.hasKey("Blocks")) {
			b15 = nbttagcompound.getByteArray("Blocks");
			if(b15.length != 0) {
				byte b16 = 3;
				if(b15.length != 32768) {
					if(b15.length == 65536) {
						b16 = 4;
					} else if(b15.length == 131072) {
						b16 = 5;
					} else if(b15.length == 262144) {
						b16 = 6;
					} else if(b15.length == 524288) {
						b16 = 7;
					}
				}

				boolean z17 = false;
				if(nbttagcompound.hasKey("TerrainPopulated")) {
					z17 = nbttagcompound.getBoolean("TerrainPopulated");
				}

				chunk = new Chunk(world, b15, x, z, z17, b16);
				byte[] b18;
				if(nbttagcompound.hasKey("Data")) {
					b18 = nbttagcompound.getByteArray("Data");
					chunk.setMetaData(b18, b16);
				}

				if(nbttagcompound.hasKey("BlockLight")) {
					b18 = nbttagcompound.getByteArray("BlockLight");
					chunk.setBlockLight(b18, b16);
				}

				if(nbttagcompound.hasKey("SkyLight")) {
					b18 = nbttagcompound.getByteArray("SkyLight");
					chunk.setSkylight(b18, b16);
				}

				ChunkBlockMap.enableBedrockConversion();
				ChunkBlockMap.removeUnknownBlocks(chunk.cubeAtYIndex(0).blocks);
				ChunkBlockMap.disableBedrockConversion();
			}
		}

		if(!nbttagcompound.hasKey("HeightMap")) {
			chunk.h = new short[256];
			chunk.func_1024_c();
		} else if(nbttagcompound.getIsType("HeightMap", NBTTagByteArray.class)) {
			b15 = nbttagcompound.getByteArray("HeightMap");
			chunk.h = new short[256];
			if(b15 != null && b15.length != 0 && !regenSkylightMap) {
				if(b15.length == 512) {
					for(n = 0; n < chunk.h.length; ++n) {
						chunk.h[n] = (short)(b15[n << 1] << 8);
						chunk.h[n] = (short)(chunk.h[n] | b15[(n << 1) + 1]);
					}
				} else {
					for(n = 0; n < b15.length; ++n) {
						chunk.h[n] = (short)b15[n];
					}
				}
			} else {
				chunk.func_1024_c();
			}
		} else if(nbttagcompound.getIsType("HeightMap", NBTTagShortArray.class)) {
			chunk.h = nbttagcompound.getShortArray("HeightMap");
		} else if(nbttagcompound.getIsType("HeightMap", NBTTagIntArray.class)) {
			chunk.h = nbttagcompound.getCastedIntArray("HeightMap");
		}

		if(regenBlocklightMap) {
			chunk.func_1014_a();
		}

		loadEntities(nbttagcompound, chunk, world);
		loadTileEntities(nbttagcompound, chunk);
		return chunk;
	}

	public static void loadEntities(NBTTagCompound tag, Chunk chunk, World world) {
		NBTTagList nbttaglist = tag.getTagList("Entities");
		if(nbttaglist != null) {
			for(int k = 0; k < nbttaglist.tagCount(); ++k) {
				NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(k);
				Entity entity = EntityList.createEntityFromNBT(nbttagcompound1, world);
				chunk.hasEntities = true;
				if(entity != null) {
					chunk.addEntity(entity);
				}
			}
		}

	}

	public static void loadTileEntities(NBTTagCompound tag, Chunk chunk) {
		NBTTagList nbttaglist1 = tag.getTagList("TileEntities");
		if(nbttaglist1 != null) {
			for(int l = 0; l < nbttaglist1.tagCount(); ++l) {
				NBTTagCompound nbttagcompound2 = (NBTTagCompound)nbttaglist1.tagAt(l);
				TileEntity tileentity = TileEntity.createAndLoadEntity(nbttagcompound2);
				if(tileentity != null) {
					chunk.addTileEntity(tileentity);
				}
			}
		}

	}

	public void func_814_a() {
	}

	public void saveExtraData() {
	}

	public void saveExtraChunkData(World world, Chunk chunk) throws IOException {
	}
}
