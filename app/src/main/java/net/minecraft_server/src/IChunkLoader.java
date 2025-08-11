package net.minecraft_server.src;

import java.io.IOException;

public interface IChunkLoader {
	Chunk loadChunk(World world1, int i2, int i3) throws IOException;

	ChunkCube loadCube(World world1, int i2, int i3, int i4) throws IOException;

	void saveChunk(World world1, Chunk chunk2) throws IOException;

	void saveCube(World world1, ChunkCube chunkCube2) throws IOException;

	void saveExtraChunkData(World world1, Chunk chunk2) throws IOException;

	void func_661_a();

	void saveExtraData();
}
