package net.minecraft_server.src;

public abstract class WorldProvider {
	public World worldObj;
	public WorldChunkManager worldChunkMgr;
	public boolean field_6167_c = false;
	public boolean isHellWorld = false;
	public boolean hasNoSky = false;
	public float[] lightBrightnessTable = new float[16];
	public int worldType = 0;
	private float[] field_6164_h = new float[4];

	public final void registerWorld(World world) {
		this.worldObj = world;
		this.registerWorldChunkManager();
		this.generateLightBrightnessTable();
	}

	protected void generateLightBrightnessTable() {
		float f = 0.05F;

		for (int i = 0; i <= 15; ++i) {
			float f1 = 1.0F - (float) i / 15.0F;
			this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
		}

	}

	protected void registerWorldChunkManager() {
		this.worldChunkMgr = new WorldChunkManager(this.worldObj);
	}

	public IChunkProvider getChunkProvider() {
		return new ChunkProviderGenerate(this.worldObj, this.worldObj.getRandomSeed());
	}

	public boolean canCoordinateBeSpawn(int i, int j) {
		int k = this.worldObj.getFirstUncoveredBlock(i, j);
		return k == Block.sand.blockID;
	}

	public float calculateCelestialAngle(long l, float f) {
		int i = (int) (l % 24000L);
		float f1 = ((float) i + f) / 24000.0F - 0.25F;
		if (f1 < 0.0F) {
			++f1;
		}

		if (f1 > 1.0F) {
			--f1;
		}

		float f2 = f1;
		f1 = 1.0F - (float) ((Math.cos((double) f1 * Math.PI) + 1.0D) / 2.0D);
		f1 = f2 + (f1 - f2) / 3.0F;
		return f1;
	}

	public boolean func_28108_d() {
		return true;
	}

	public static WorldProvider func_4091_a(int i) {
		return (WorldProvider) (i == -1 ? new WorldProviderHell()
				: (i == 0 ? new WorldProviderSurface() : (i == 1 ? new WorldProviderSky() : null)));
	}
}
