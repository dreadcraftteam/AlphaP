package net.minecraft.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkProviderClient implements IChunkProvider {
	private Chunk blankChunk;
	private ChunkCube blankCube;
	private Map chunkMapping = new HashMap();
	private List field_889_c = new ArrayList();
	private World worldObj;

	public ChunkProviderClient(World world) {
		this.blankChunk = new EmptyChunk(world, new byte[32768], 0, 0);
		this.blankCube = new EmptyCube(this.blankChunk, new byte[4096], 0);
		this.worldObj = world;
	}

	public boolean cubeExists(int i, int y, int j) {
		if(this != null) {
			return true;
		} else {
			ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
			Chunk ch = (Chunk)this.chunkMapping.get(chunkcoordintpair);
			if(ch != null) {
				int cube = y + 2047;
				return cube >= 0 && cube <= ch.cubes.length ? ch.cubes[cube] != null : false;
			} else {
				return false;
			}
		}
	}

	public boolean chunkExists(int i, int j) {
		if(this != null) {
			return true;
		} else {
			ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
			return this.chunkMapping.containsKey(chunkcoordintpair);
		}
	}

	public void func_539_c(int i, int j) {
		Chunk chunk = this.provideChunk(i, j);
		if(!chunk.func_21167_h()) {
			chunk.onChunkUnload();
		}

		this.chunkMapping.remove(new ChunkCoordIntPair(i, j));
		this.field_889_c.remove(chunk);
	}

	public void func_539_c(int x, int y, int z) {
		ChunkCube cube = this.provideCube(x, y, z);
		if(!cube.chunk.func_21167_h()) {
			cube.onChunkUnload();
		}

	}

	public Chunk prepareChunk(int i, int j) {
		ChunkCoordIntPair pos = new ChunkCoordIntPair(i, j);
		Chunk chunk = new Chunk(this.worldObj, i, j);
		Arrays.fill(chunk.h, (short)-32752);
		this.chunkMapping.put(pos, chunk);
		chunk.isChunkLoaded = true;
		return chunk;
	}

	public ChunkCube prepareCube(int x, int y, int z) {
		ChunkCoordIntPair pos = new ChunkCoordIntPair(x, z);
		byte[] data = new byte[4096];
		Chunk chunk = (Chunk)this.chunkMapping.get(pos);
		if(chunk == null) {
			chunk = this.prepareChunk(x, z);
		}

		ChunkCube cube = chunk.cubes[y + 2047] = new ChunkCube(chunk, data, y + 2047);
		Arrays.fill(cube.skylightMap.data, (byte)-1);
		cube.isChunkLoaded = true;
		cube.isTerrainPopulated = false;
		return cube;
	}

	public Chunk provideChunk(int i, int j) {
		ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
		Chunk chunk = (Chunk)this.chunkMapping.get(chunkcoordintpair);
		return chunk == null ? this.blankChunk : chunk;
	}

	public ChunkCube provideCube(int x, int y, int z) {
		if(y < 2047 && y >= -2047) {
			ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(x, z);
			Chunk chunk = (Chunk)this.chunkMapping.get(chunkcoordintpair);
			if(chunk != null) {
				ChunkCube cube = chunk.cubeAtYIndex(y);
				if(cube != null) {
					return cube;
				}
			}

			return this.blankCube;
		} else {
			return this.blankCube;
		}
	}

	public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
		return true;
	}

	public boolean saveCube(boolean flag, ChunkCube cube) {
		return true;
	}

	public boolean unload100OldestChunks() {
		return false;
	}

	public boolean canSave() {
		return false;
	}

	public void a(IChunkProvider ichunkprovider, int i, int j) {
	}

	public void populate(IChunkProvider ichunkprovider, int x, int y, int z) {
	}

	public String makeString() {
		return "MultiplayerChunkCache: " + this.chunkMapping.size();
	}
}
