package net.minecraft_server.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class McRegionChunkLoader implements IChunkLoader {
	private final File worldFolder;

	public McRegionChunkLoader(File file) {
		this.worldFolder = file;
		ChunkLoader.callbackMCRegion = this;
	}

	public Chunk loadChunk(World world, int i, int j) throws IOException {
		DataInputStream datainputstream = RegionFileCache.getChunkInputStream(this.worldFolder, i, j);
		if (datainputstream == null) {
			return null;
		} else {
			NBTTagCompound nbttagcompound = CompressedStreamTools.func_774_a(datainputstream);
			if (!nbttagcompound.hasKey("Level")) {
				System.out.println("Chunk file at " + i + "," + j + " is missing level data, skipping");
				return null;
			} else {
				NBTTagCompound level = nbttagcompound.getCompoundTag("Level");
				Chunk chunk = ChunkLoader.loadChunkIntoWorldFromCompound(world, level);
				if (!chunk.isAtLocation(i, j)) {
					System.out.println(
							"Chunk file at " + i + "," + j + " is in the wrong location; relocating. (Expected " + i
									+ ", " + j + ", got " + chunk.xPosition + ", " + chunk.zPosition + ")");
					level.setInteger("xPos", i);
					level.setInteger("zPos", j);
					chunk = ChunkLoader.loadChunkIntoWorldFromCompound(world, level);
				}

				chunk.func_25083_h();
				return chunk;
			}
		}
	}

	public ChunkCube loadCube(World world, int x, int y, int z) throws IOException {
		DataInputStream datainputstream = RegionFileCache.getCubeInputStream(this.worldFolder, x, y, z);
		if (datainputstream == null) {
			return null;
		} else {
			NBTTagCompound nbttagcompound = CompressedStreamTools.func_774_a(datainputstream);
			if (!nbttagcompound.hasKey("Level")) {
				System.out.println("Cube file at " + x + "," + y + "," + z + " is missing level data, skipping");
				return null;
			} else {
				NBTTagCompound level = nbttagcompound.getCompoundTag("Level");
				if (!level.hasKey("Blocks")) {
					System.out.println("Cube file at " + x + "," + y + "," + z + " is missing block data, skipping");
					return null;
				} else {
					ChunkCube cube = ChunkLoader.loadCubeIntoWorldFromCompound(world, level, x, y, z);
					return cube;
				}
			}
		}
	}

	public void saveCube(World world, ChunkCube cube) throws IOException {
		if (cube != null && cube.isChunkLoaded && !cube.calculateIsAir()) {
			world.checkSessionLock();

			try {
				NBTTagCompound exception = new NBTTagCompound();
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				if (cube != null && cube.isChunkLoaded && !cube.calculateIsAir()) {
					DataOutputStream worldinfo = RegionFileCache.getCubeOutputStream(this.worldFolder,
							cube.chunk.xPosition, cube.yPosition, cube.chunk.zPosition);
					exception.setTag("Level", nbttagcompound1);
					ChunkLoader.storeCubeInCompound(cube, world, nbttagcompound1);
					CompressedStreamTools.func_771_a(exception, worldinfo);
					worldinfo.close();
				}

				WorldInfo worldinfo1 = world.getWorldInfo();
				worldinfo1.setSizeOnDisk(worldinfo1.getSizeOnDisk() + (long) RegionFileCache
						.getSizeDelta(this.worldFolder, cube.chunk.xPosition, cube.yPosition, cube.chunk.zPosition));
			} catch (Exception exception6) {
				exception6.printStackTrace();
			}
		}

	}

	public void saveConvertedCube(World world, NBTTagCompound outer, int x, int y, int z) {
		world.checkSessionLock();

		try {
			DataOutputStream e = RegionFileCache.getCubeOutputStream(this.worldFolder, x, y, z);
			CompressedStreamTools.func_771_a(outer, e);
			e.close();
		} catch (Exception exception7) {
			exception7.printStackTrace();
		}

	}

	public void saveChunk(World world, Chunk chunk) throws IOException {
		world.checkSessionLock();

		try {
			NBTTagCompound exception = new NBTTagCompound();
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			DataOutputStream dataoutputstream = RegionFileCache.getChunkOutputStream(this.worldFolder, chunk.xPosition,
					chunk.zPosition);
			exception.setTag("Level", nbttagcompound1);
			ChunkLoader.storeChunkInCompound(chunk, world, nbttagcompound1);
			CompressedStreamTools.func_771_a(exception, dataoutputstream);
			dataoutputstream.close();
			WorldInfo worldinfo = world.getWorldInfo();
			worldinfo.setSizeOnDisk(worldinfo.getSizeOnDisk()
					+ (long) RegionFileCache.getSizeDelta(this.worldFolder, chunk.xPosition, chunk.zPosition));
		} catch (Exception exception7) {
			exception7.printStackTrace();
		}

	}

	public void saveExtraChunkData(World world, Chunk chunk) throws IOException {
	}

	public void func_661_a() {
	}

	public void saveExtraData() {
	}
}
