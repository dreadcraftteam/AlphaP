package net.minecraft_server.src;

public interface IChunkProvider {
	boolean chunkExists(int i1, int i2);

	boolean cubeExists(int i1, int i2, int i3);

	Chunk provideChunk(int i1, int i2);

	ChunkCube provideCube(int i1, int i2, int i3);

	Chunk loadChunk(int i1, int i2);

	ChunkCube loadCube(int i1, int i2, int i3);

	void populate(IChunkProvider iChunkProvider1, int i2, int i3, int i4);

	boolean saveChunks(boolean z1, IProgressUpdate iProgressUpdate2);

	boolean saveCube(boolean z1, ChunkCube chunkCube2);

	boolean unload100OldestChunks();

	boolean canSave();
}
