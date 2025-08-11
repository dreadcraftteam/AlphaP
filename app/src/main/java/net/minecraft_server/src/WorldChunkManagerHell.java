package net.minecraft_server.src;

import java.util.Arrays;

public class WorldChunkManagerHell extends WorldChunkManager {
	private BiomeGenBase field_4262_e;
	private double field_4261_f;
	private double field_4260_g;

	public WorldChunkManagerHell(BiomeGenBase biomegenbase, double d, double d1) {
		this.field_4262_e = biomegenbase;
		this.field_4261_f = d;
		this.field_4260_g = d1;
	}

	public BiomeGenBase GetBiomeGenAtChunkCoord(ChunkCoordIntPair chunkcoordintpair) {
		return this.field_4262_e;
	}

	public BiomeGenBase getBiomeGenAt(int i, int j) {
		return this.field_4262_e;
	}

	public BiomeGenBase[] func_4065_a(int i, int j, int k, int l) {
		this.field_4256_d = this.loadBlockGeneratorData(this.field_4256_d, i, j, k, l);
		return this.field_4256_d;
	}

	public double[] getTemperatures(double[] ad, int i, int j, int k, int l) {
		if (ad == null || ad.length < k * l) {
			ad = new double[k * l];
		}

		Arrays.fill(ad, 0, k * l, this.field_4261_f);
		return ad;
	}

	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] abiomegenbase, int i, int j, int k, int l) {
		if (abiomegenbase == null || abiomegenbase.length < k * l) {
			abiomegenbase = new BiomeGenBase[k * l];
		}

		if (this.temperature == null || this.temperature.length < k * l) {
			this.temperature = new double[k * l];
			this.humidity = new double[k * l];
		}

		Arrays.fill(abiomegenbase, 0, k * l, this.field_4262_e);
		Arrays.fill(this.humidity, 0, k * l, this.field_4260_g);
		Arrays.fill(this.temperature, 0, k * l, this.field_4261_f);
		return abiomegenbase;
	}
}
