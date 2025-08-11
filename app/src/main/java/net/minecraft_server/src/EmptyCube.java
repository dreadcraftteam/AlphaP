package net.minecraft_server.src;

public class EmptyCube extends ChunkCube {
	public EmptyCube(Chunk ichunk, int y) {
		super(ichunk, y);
		this.neverSave = true;
	}

	public EmptyCube(Chunk ichunk, byte[] iblocks, int y) {
		super(ichunk, iblocks, y);
		this.neverSave = true;
	}

	public int getMaxHeight(int x, int z) {
		return 0;
	}

	public void onChunkLoad() {
	}

	public void onChunkUnload() {
	}

	public void removeUnknownBlocks() {
	}

	public void func_4143_d() {
	}

	public boolean isAtLocation(int x, int y, int z) {
		return x == this.chunk.xPosition && y == this.yPosition && z == this.chunk.zPosition;
	}

	public void setCubeModified() {
	}

	public int getBlockID(int x, int y, int z) {
		return 0;
	}

	public int getBlockMetadata(int x, int y, int z) {
		return 0;
	}

	public boolean setBlockID(int x, int y, int z, int id) {
		return true;
	}

	public void setBlockMetadata(int x, int y, int z, int md) {
	}

	public boolean setBlockIDWithMetadata(int x, int y, int z, int id, int md) {
		return true;
	}

	public int getSavedLightValue(EnumSkyBlock enumskyblock, int i, int j, int k) {
		return 0;
	}

	public void setLightValue(EnumSkyBlock enumskyblock, int x, int y, int z, int level) {
	}

	public int getBlockLightValue(int x, int y, int z, int skyDrop) {
		return 0;
	}

	public void addEntity(Entity entity) {
	}

	public TileEntity getChunkBlockTileEntity(int x, int y, int z) {
		return null;
	}

	public void setChunkBlockTileEntity(int x, int y, int z, TileEntity tileentity) {
	}

	public void removeChunkBlockTileEntity(int i, int j, int k) {
	}

	public void regenHeightMap() {
	}

	public void regenSkylightMap() {
	}

	public void regenOnLoad() {
	}
}
