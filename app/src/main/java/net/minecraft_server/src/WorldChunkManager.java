package net.minecraft_server.src;

import java.util.Random;

public class WorldChunkManager {
	private NoiseGeneratorOctaves2 llRand;
	private NoiseGeneratorOctaves2 field_4255_e;
	private NoiseGeneratorOctaves2 field_4254_f;
	private NoiseGeneratorOctaves2 field_4253_g;
	public double[] temperature;
	public double[] lavaLevel;
	public double[] humidity;
	public double[] field_4257_c;
	public BiomeGenBase[] field_4256_d;

	protected WorldChunkManager() {
	}

	public WorldChunkManager(World world) {
		this.field_4255_e = new NoiseGeneratorOctaves2(new Random(world.getRandomSeed() * 9871L), 4);
		this.llRand = new NoiseGeneratorOctaves2(new Random(world.getRandomSeed() * 1094L), 4);
		this.field_4254_f = new NoiseGeneratorOctaves2(new Random(world.getRandomSeed() * 39811L), 4);
		this.field_4253_g = new NoiseGeneratorOctaves2(new Random(world.getRandomSeed() * 543321L), 2);
	}

	public BiomeGenBase GetBiomeGenAtChunkCoord(ChunkCoordIntPair chunkcoordintpair) {
		return this.getBiomeGenAt(chunkcoordintpair.chunkXPos << 4, chunkcoordintpair.chunkZPos << 4);
	}

	public BiomeGenBase getBiomeGenAt(int i, int j) {
		return this.func_4065_a(i, j, 1, 1)[0];
	}

	public BiomeGenBase[] func_4065_a(int i, int j, int k, int l) {
		this.field_4256_d = this.loadBlockGeneratorData(this.field_4256_d, i, j, k, l);
		return this.field_4256_d;
	}

	public double[] getTemperatures(double[] ad, int i, int j, int k, int l) {
		if (ad == null || ad.length < k * l) {
			ad = new double[k * l];
		}

		ad = this.field_4255_e.func_4101_a(ad, (double) i, (double) j, k, l, 0.02500000037252903D, 0.02500000037252903D,
				0.25D);
		this.field_4257_c = this.field_4253_g.func_4101_a(this.field_4257_c, (double) i, (double) j, k, l, 0.25D, 0.25D,
				0.5882352941176471D);
		int i1 = 0;

		for (int j1 = 0; j1 < k; ++j1) {
			for (int k1 = 0; k1 < l; ++k1) {
				double d = this.field_4257_c[i1] * 1.1D + 0.5D;
				double d1 = 0.01D;
				double d2 = 1.0D - d1;
				double d3 = (ad[i1] * 0.15D + 0.7D) * d2 + d * d1;
				d3 = 1.0D - (1.0D - d3) * (1.0D - d3);
				if (d3 < 0.0D) {
					d3 = 0.0D;
				}

				if (d3 > 1.0D) {
					d3 = 1.0D;
				}

				ad[i1] = d3;
				++i1;
			}
		}

		return ad;
	}

	public double getLavaLevel(int x, int z) {
		this.lavaLevel = this.llRand.func_4101_a(this.lavaLevel, (double) x, (double) z, 1, 1, 0.02500000037252903D,
				0.02500000037252903D, 0.5D);
		return this.lavaLevel[0];
	}

	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] abiomegenbase, int i, int j, int k, int l) {
		if (abiomegenbase == null || abiomegenbase.length < k * l) {
			abiomegenbase = new BiomeGenBase[k * l];
		}

		this.temperature = this.field_4255_e.func_4101_a(this.temperature, (double) i, (double) j, k, k,
				0.02500000037252903D, 0.02500000037252903D, 0.25D);
		this.humidity = this.field_4254_f.func_4101_a(this.humidity, (double) i, (double) j, k, k, (double) 0.05F,
				(double) 0.05F, 0.3333333333333333D);
		this.field_4257_c = this.field_4253_g.func_4101_a(this.field_4257_c, (double) i, (double) j, k, k, 0.25D, 0.25D,
				0.5882352941176471D);
		int i1 = 0;

		for (int j1 = 0; j1 < k; ++j1) {
			for (int k1 = 0; k1 < l; ++k1) {
				double d = this.field_4257_c[i1] * 1.1D + 0.5D;
				double d1 = 0.01D;
				double d2 = 1.0D - d1;
				double d3 = (this.temperature[i1] * 0.15D + 0.7D) * d2 + d * d1;
				d1 = 0.002D;
				d2 = 1.0D - d1;
				double d4 = (this.humidity[i1] * 0.15D + 0.5D) * d2 + d * d1;
				d3 = 1.0D - (1.0D - d3) * (1.0D - d3);
				if (d3 < 0.0D) {
					d3 = 0.0D;
				}

				if (d4 < 0.0D) {
					d4 = 0.0D;
				}

				if (d3 > 1.0D) {
					d3 = 1.0D;
				}

				if (d4 > 1.0D) {
					d4 = 1.0D;
				}

				this.temperature[i1] = d3;
				this.humidity[i1] = d4;
				abiomegenbase[i1++] = BiomeGenBase.getBiomeFromLookup(d3, d4);
			}
		}

		return abiomegenbase;
	}
}
