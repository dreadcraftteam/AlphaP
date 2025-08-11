package net.minecraft_server.src;

public class ChunkCubeCoord {
	public final int chunkXPos;
	public final int chunkYPos;
	public final int chunkZPos;

	public ChunkCubeCoord(int x, int y, int z) {
		this.chunkXPos = x;
		this.chunkYPos = y;
		this.chunkZPos = z;
	}

	public static int chunkXYZ2Int(int x, int y, int z) {
		return (x >= 0 ? 0 : Integer.MIN_VALUE) | (x & 32767) << 16 | (z >= 0 ? 0 : 32768) | z & 32767 + y;
	}

	public int hashCode() {
		return chunkXYZ2Int(this.chunkXPos, this.chunkYPos, this.chunkZPos);
	}

	public boolean equals(Object obj) {
		ChunkCubeCoord other = (ChunkCubeCoord) obj;
		return other.chunkXPos == this.chunkXPos && other.chunkYPos == this.chunkYPos
				&& other.chunkZPos == this.chunkZPos;
	}

	public ChunkCoordIntPair toChunkCoord() {
		return new ChunkCoordIntPair(this.chunkXPos, this.chunkZPos);
	}
}
