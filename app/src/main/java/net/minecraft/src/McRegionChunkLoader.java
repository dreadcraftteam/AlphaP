package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class McRegionChunkLoader implements IChunkLoader {
	private final File worldDir;

	public McRegionChunkLoader(File file) {
		this.worldDir = file;
		ChunkLoader.callbackMCRegion = this;
	}

	public Chunk loadChunk(World world, int i, int j) throws IOException {
		DataInputStream datainputstream = RegionFileCache.getChunkInputStream(this.worldDir, i, j);
		if(datainputstream == null) {
			return null;
		} else {
			NBTTagCompound nbttagcompound = CompressedStreamTools.func_1141_a(datainputstream);
			if(!nbttagcompound.hasKey("Level")) {
				System.out.println("Chunk file at " + i + "," + j + " is missing level data, skipping");
				return null;
			} else {
				NBTTagCompound level = nbttagcompound.getCompoundTag("Level");
				Chunk chunk = ChunkLoader.loadChunkIntoWorldFromCompound(world, level);
				if(!chunk.isAtLocation(i, j)) {
					System.out.println("Chunk file at " + i + "," + j + " is in the wrong location; relocating. (Expected " + i + ", " + j + ", got " + chunk.xPosition + ", " + chunk.zPosition + ")");
					level.setInteger("xPos", i);
					level.setInteger("zPos", j);
					chunk = ChunkLoader.loadChunkIntoWorldFromCompound(world, level);
				}

				chunk.func_25124_i();
				return chunk;
			}
		}
	}

	public ChunkCube loadCube(World world, int x, int y, int z) throws IOException {
		DataInputStream datainputstream = RegionFileCache.getCubeInputStream(this.worldDir, x, y, z);
		if(datainputstream == null) {
			return null;
		} else {
			NBTTagCompound nbttagcompound = CompressedStreamTools.func_1141_a(datainputstream);
			if(!nbttagcompound.hasKey("Level")) {
				System.out.println("Cube file at " + x + "," + y + "," + z + " is missing level data, skipping");
				return null;
			} else {
				NBTTagCompound level = nbttagcompound.getCompoundTag("Level");
				if(!level.hasKey("Blocks")) {
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
		if(cube != null && cube.isChunkLoaded && !cube.calculateIsAir()) {
			world.checkSessionLock();

			try {
				NBTTagCompound exception = new NBTTagCompound();
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				DataOutputStream dataoutputstream = RegionFileCache.getCubeOutputStream(this.worldDir, cube.chunk.xPosition, cube.yPosition, cube.chunk.zPosition);
				exception.setTag("Level", nbttagcompound1);
				ChunkLoader.storeCubeInCompound(cube, world, nbttagcompound1);
				CompressedStreamTools.func_1139_a(exception, dataoutputstream);
				dataoutputstream.close();
				WorldInfo worldinfo = world.getWorldInfo();
				worldinfo.setSizeOnDisk(worldinfo.getSizeOnDisk() + (long)RegionFileCache.getSizeDelta(this.worldDir, cube.chunk.xPosition, cube.yPosition, cube.chunk.zPosition));
			} catch (Exception exception7) {
				exception7.printStackTrace();
			}
		}

	}

	public void saveConvertedCube(World world, NBTTagCompound outer, int x, int y, int z) {
		world.checkSessionLock();

		try {
			DataOutputStream e = RegionFileCache.getCubeOutputStream(this.worldDir, x, y, z);
			CompressedStreamTools.func_1139_a(outer, e);
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
			DataOutputStream dataoutputstream = RegionFileCache.getChunkOutputStream(this.worldDir, chunk.xPosition, chunk.zPosition);
			exception.setTag("Level", nbttagcompound1);
			ChunkLoader.storeChunkInCompound(chunk, world, nbttagcompound1);
			CompressedStreamTools.func_1139_a(exception, dataoutputstream);
			dataoutputstream.close();
			WorldInfo worldinfo = world.getWorldInfo();
			worldinfo.setSizeOnDisk(worldinfo.getSizeOnDisk() + (long)RegionFileCache.getSizeDelta(this.worldDir, chunk.xPosition, chunk.zPosition));
		} catch (Exception exception7) {
			exception7.printStackTrace();
		}

	}

	public void saveExtraChunkData(World world, Chunk chunk) throws IOException {
	}

	public void func_814_a() {
	}

	public void saveExtraData() {
	}
}
