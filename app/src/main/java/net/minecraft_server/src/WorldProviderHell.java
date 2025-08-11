package net.minecraft_server.src;

public class WorldProviderHell extends WorldProvider {
	public void registerWorldChunkManager() {
		this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 1.0D, 0.0D);
		this.field_6167_c = true;
		this.isHellWorld = true;
		this.hasNoSky = true;
		this.worldType = -1;
	}

	protected void generateLightBrightnessTable() {
		float f = 0.1F;

		for (int i = 0; i <= 15; ++i) {
			float f1 = 1.0F - (float) i / 15.0F;
			this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
		}

	}

	public IChunkProvider getChunkProvider() {
		return new ChunkProviderHell(this.worldObj, this.worldObj.getRandomSeed());
	}

	public boolean canCoordinateBeSpawn(int i, int j) {
		int k = this.worldObj.getFirstUncoveredBlock(i, j);
		return k == Block.bedrock.blockID ? false : (k == 0 ? false : Block.opaqueCubeLookup[k]);
	}

	public float calculateCelestialAngle(long l, float f) {
		return 0.5F;
	}

	public boolean func_28108_d() {
		return false;
	}
}
