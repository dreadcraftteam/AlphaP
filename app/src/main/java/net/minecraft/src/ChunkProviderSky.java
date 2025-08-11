package net.minecraft.src;

import java.util.Random;

public class ChunkProviderSky implements IChunkProvider {
	private Random field_28087_j;
	private NoiseGeneratorOctaves field_28086_k;
	private NoiseGeneratorOctaves field_28085_l;
	private NoiseGeneratorOctaves field_28084_m;
	private NoiseGeneratorOctaves field_28083_n;
	private NoiseGeneratorOctaves field_28082_o;
	public NoiseGeneratorOctaves field_28096_a;
	public NoiseGeneratorOctaves field_28095_b;
	public NoiseGeneratorOctaves field_28094_c;
	private World worldObj;
	private double[] field_28080_q;
	private double[] field_28079_r = new double[256];
	private double[] field_28078_s = new double[256];
	private double[] field_28077_t = new double[256];
	private MapGenBase field_28076_u = new MapGenCaves();
	private BiomeGenBase[] field_28075_v;
	double[] field_28093_d;
	double[] field_28092_e;
	double[] field_28091_f;
	double[] field_28090_g;
	double[] field_28089_h;
	int[][] field_28088_i = new int[32][32];
	private double[] field_28074_w;

	public ChunkProviderSky(World world, long l) {
		this.worldObj = world;
		this.field_28087_j = new Random(l);
		this.field_28086_k = new NoiseGeneratorOctaves(this.field_28087_j, 16);
		this.field_28085_l = new NoiseGeneratorOctaves(this.field_28087_j, 16);
		this.field_28084_m = new NoiseGeneratorOctaves(this.field_28087_j, 8);
		this.field_28083_n = new NoiseGeneratorOctaves(this.field_28087_j, 4);
		this.field_28082_o = new NoiseGeneratorOctaves(this.field_28087_j, 4);
		this.field_28096_a = new NoiseGeneratorOctaves(this.field_28087_j, 10);
		this.field_28095_b = new NoiseGeneratorOctaves(this.field_28087_j, 16);
		this.field_28094_c = new NoiseGeneratorOctaves(this.field_28087_j, 8);
	}

	public void func_28071_a(int i, int j, byte[] abyte0, BiomeGenBase[] abiomegenbase, double[] ad) {
		byte byte0 = 2;
		int k = byte0 + 1;
		byte byte1 = 33;
		int l = byte0 + 1;
		this.field_28080_q = this.func_28073_a(this.field_28080_q, i * byte0, 0, j * byte0, k, byte1, l);

		for(int i1 = 0; i1 < byte0; ++i1) {
			for(int j1 = 0; j1 < byte0; ++j1) {
				for(int k1 = 0; k1 < 32; ++k1) {
					double d = 0.25D;
					double d1 = this.field_28080_q[((i1 + 0) * l + j1 + 0) * byte1 + k1 + 0];
					double d2 = this.field_28080_q[((i1 + 0) * l + j1 + 1) * byte1 + k1 + 0];
					double d3 = this.field_28080_q[((i1 + 1) * l + j1 + 0) * byte1 + k1 + 0];
					double d4 = this.field_28080_q[((i1 + 1) * l + j1 + 1) * byte1 + k1 + 0];
					double d5 = (this.field_28080_q[((i1 + 0) * l + j1 + 0) * byte1 + k1 + 1] - d1) * d;
					double d6 = (this.field_28080_q[((i1 + 0) * l + j1 + 1) * byte1 + k1 + 1] - d2) * d;
					double d7 = (this.field_28080_q[((i1 + 1) * l + j1 + 0) * byte1 + k1 + 1] - d3) * d;
					double d8 = (this.field_28080_q[((i1 + 1) * l + j1 + 1) * byte1 + k1 + 1] - d4) * d;

					for(int l1 = 0; l1 < 4; ++l1) {
						double d9 = 0.125D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for(int i2 = 0; i2 < 8; ++i2) {
							int j2 = i2 + i1 * 8 << 11 | 0 + j1 * 8 << 7 | k1 * 4 + l1;
							short c = 128;
							double d14 = 0.125D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;

							for(int k2 = 0; k2 < 8; ++k2) {
								int l2 = 0;
								if(d15 > 0.0D) {
									l2 = Block.stone.blockID;
								}

								abyte0[j2] = (byte)l2;
								j2 += c;
								d15 += d16;
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}

	}

	public void func_28072_a(int i, int j, byte[] abyte0, BiomeGenBase[] abiomegenbase) {
		double d = 8.0D / 256D;
		this.field_28079_r = this.field_28083_n.generateNoiseOctaves(this.field_28079_r, (double)(i * 16), (double)(j * 16), 0.0D, 16, 16, 1, d, d, 1.0D);
		this.field_28078_s = this.field_28083_n.generateNoiseOctaves(this.field_28078_s, (double)(i * 16), 109.0134D, (double)(j * 16), 16, 1, 16, d, 1.0D, d);
		this.field_28077_t = this.field_28082_o.generateNoiseOctaves(this.field_28077_t, (double)(i * 16), (double)(j * 16), 0.0D, 16, 16, 1, d * 2.0D, d * 2.0D, d * 2.0D);

		for(int k = 0; k < 16; ++k) {
			for(int l = 0; l < 16; ++l) {
				BiomeGenBase biomegenbase = abiomegenbase[k + l * 16];
				int i1 = (int)(this.field_28077_t[k + l * 16] / 3.0D + 3.0D + this.field_28087_j.nextDouble() * 0.25D);
				int j1 = -1;
				byte byte0 = biomegenbase.topBlock;
				byte byte1 = biomegenbase.fillerBlock;

				for(int k1 = 127; k1 >= 0; --k1) {
					int l1 = (l * 16 + k) * 128 + k1;
					byte byte2 = abyte0[l1];
					if(byte2 == 0) {
						j1 = -1;
					} else if(byte2 == Block.stone.blockID) {
						if(j1 == -1) {
							if(i1 <= 0) {
								byte0 = 0;
								byte1 = (byte)Block.stone.blockID;
							}

							j1 = i1;
							if(k1 >= 0) {
								abyte0[l1] = byte0;
							} else {
								abyte0[l1] = byte1;
							}
						} else if(j1 > 0) {
							--j1;
							abyte0[l1] = byte1;
							if(j1 == 0 && byte1 == Block.sand.blockID) {
								j1 = this.field_28087_j.nextInt(4);
								byte1 = (byte)Block.sandStone.blockID;
							}
						}
					}
				}
			}
		}

	}

	public Chunk prepareChunk(int i, int j) {
		return this.provideChunk(i, j);
	}

	public ChunkCube prepareCube(int i, int y, int j) {
		return this.provideCube(i, y, j);
	}

	public ChunkCube provideCube(int x, int y, int z) {
		Chunk chunk = this.worldObj.getChunkFromChunkCoords(x, z);
		if(chunk == null || chunk.neverSave) {
			chunk = this.provideChunk(x, y);
		}

		if(y >= 0 && y < 8) {
			chunk.func_1024_c();
			return chunk.cubes[y + 2047];
		} else {
			byte[] data = new byte[4096];
			ChunkCube cube = new ChunkCube(chunk, data, y + 2047);
			cube.isAir = true;
			cube.isChunkLoaded = true;
			chunk.cubes[y + 2047] = cube;

			for(int n = 0; n < cube.skylightMap.data.length; ++n) {
				cube.skylightMap.data[n] = -1;
			}

			return cube;
		}
	}

	public Chunk provideChunk(int i, int j) {
		this.field_28087_j.setSeed((long)i * 341873128712L + (long)j * 132897987541L);
		byte[] data = new byte[32768];
		this.field_28075_v = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.field_28075_v, i * 16, j * 16, 16, 16);
		double[] ad = this.worldObj.getWorldChunkManager().temperature;
		this.func_28071_a(i, j, data, this.field_28075_v, ad);
		this.func_28072_a(i, j, data, this.field_28075_v);
		this.field_28076_u.func_867_a(this, this.worldObj, i, j, data);
		Chunk chunk = new Chunk(this.worldObj, data, i, j);
		chunk.func_1024_c();
		return chunk;
	}

	private double[] func_28073_a(double[] ad, int i, int j, int k, int l, int i1, int j1) {
		if(ad == null) {
			ad = new double[l * i1 * j1];
		}

		double d = 684.412D;
		double d1 = 684.412D;
		double[] ad1 = this.worldObj.getWorldChunkManager().temperature;
		double[] ad2 = this.worldObj.getWorldChunkManager().humidity;
		this.field_28090_g = this.field_28096_a.func_4109_a(this.field_28090_g, i, k, l, j1, 1.121D, 1.121D, 0.5D);
		this.field_28089_h = this.field_28095_b.func_4109_a(this.field_28089_h, i, k, l, j1, 200.0D, 200.0D, 0.5D);
		d *= 2.0D;
		this.field_28093_d = this.field_28084_m.generateNoiseOctaves(this.field_28093_d, (double)i, (double)j, (double)k, l, i1, j1, d / 80.0D, d1 / 160.0D, d / 80.0D);
		this.field_28092_e = this.field_28086_k.generateNoiseOctaves(this.field_28092_e, (double)i, (double)j, (double)k, l, i1, j1, d, d1, d);
		this.field_28091_f = this.field_28085_l.generateNoiseOctaves(this.field_28091_f, (double)i, (double)j, (double)k, l, i1, j1, d, d1, d);
		int k1 = 0;
		int l1 = 0;
		int i2 = 16 / l;

		for(int j2 = 0; j2 < l; ++j2) {
			int k2 = j2 * i2 + i2 / 2;

			for(int l2 = 0; l2 < j1; ++l2) {
				int i3 = l2 * i2 + i2 / 2;
				double d2 = ad1[k2 * 16 + i3];
				double d3 = ad2[k2 * 16 + i3] * d2;
				double d4 = 1.0D - d3;
				d4 *= d4;
				d4 *= d4;
				d4 = 1.0D - d4;
				double d5 = (this.field_28090_g[l1] + 256.0D) / 512.0D;
				d5 *= d4;
				if(d5 > 1.0D) {
					d5 = 1.0D;
				}

				double d6 = this.field_28089_h[l1] / 8000.0D;
				if(d6 < 0.0D) {
					d6 = -d6 * 0.3D;
				}

				d6 = d6 * 3.0D - 2.0D;
				if(d6 > 1.0D) {
					d6 = 1.0D;
				}

				d6 /= 8.0D;
				d6 = 0.0D;
				if(d5 < 0.0D) {
					d5 = 0.0D;
				}

				d5 += 0.5D;
				d6 = d6 * (double)i1 / 16.0D;
				++l1;
				double d7 = (double)i1 / 2.0D;

				for(int j3 = 0; j3 < i1; ++j3) {
					double d8 = 0.0D;
					double d9 = ((double)j3 - d7) * 8.0D / d5;
					if(d9 < 0.0D) {
						d9 *= -1.0D;
					}

					double d10 = this.field_28092_e[k1] / 512.0D;
					double d11 = this.field_28091_f[k1] / 512.0D;
					double d12 = (this.field_28093_d[k1] / 10.0D + 1.0D) / 2.0D;
					if(d12 < 0.0D) {
						d8 = d10;
					} else if(d12 > 1.0D) {
						d8 = d11;
					} else {
						d8 = d10 + (d11 - d10) * d12;
					}

					d8 -= 8.0D;
					byte k3 = 32;
					double d14;
					if(j3 > i1 - k3) {
						d14 = (double)((float)(j3 - (i1 - k3)) / ((float)k3 - 1.0F));
						d8 = d8 * (1.0D - d14) + -30.0D * d14;
					}

					k3 = 8;
					if(j3 < k3) {
						d14 = (double)((float)(k3 - j3) / ((float)k3 - 1.0F));
						d8 = d8 * (1.0D - d14) + -30.0D * d14;
					}

					ad[k1] = d8;
					++k1;
				}
			}
		}

		return ad;
	}

	public boolean chunkExists(int i, int j) {
		return true;
	}

	public boolean cubeExists(int i, int y, int j) {
		return true;
	}

	public void populate(IChunkProvider ichunkprovider, int x, int y, int z) {
	}

	public void a(IChunkProvider ichunkprovider, int i, int j) {
		BlockSand.fallInstantly = true;
		int k = i * 16;
		int l = j * 16;
		BiomeGenBase biomegenbase = this.worldObj.getWorldChunkManager().getBiomeGenAt(k + 16, l + 16);
		this.field_28087_j.setSeed(this.worldObj.getRandomSeed());
		long l1 = this.field_28087_j.nextLong() / 2L * 2L + 1L;
		long l2 = this.field_28087_j.nextLong() / 2L * 2L + 1L;
		this.field_28087_j.setSeed((long)i * l1 + (long)j * l2 ^ this.worldObj.getRandomSeed());
		double d = 0.25D;
		int k4;
		int l7;
		int l12;
		// if(this.field_28087_j.nextInt(4) == 0) {
		// 	k4 = k + this.field_28087_j.nextInt(16) + 8;
		// 	l7 = this.field_28087_j.nextInt(128);
		// 	l12 = l + this.field_28087_j.nextInt(16) + 8;
		// 	(new WorldGenLakes(Block.waterStill.blockID)).generate(this.worldObj, this.field_28087_j, k4, l7, l12);
		// }

		// if(this.field_28087_j.nextInt(8) == 0) {
		// 	k4 = k + this.field_28087_j.nextInt(16) + 8;
		// 	l7 = this.field_28087_j.nextInt(this.field_28087_j.nextInt(120) + 8);
		// 	l12 = l + this.field_28087_j.nextInt(16) + 8;
		// 	if(l7 < 64 || this.field_28087_j.nextInt(10) == 0) {
		// 		(new WorldGenLakes(Block.lavaStill.blockID)).generate(this.worldObj, this.field_28087_j, k4, l7, l12);
		// 	}
		// }

		int i18;
		for(k4 = 0; k4 < 8; ++k4) {
			l7 = k + this.field_28087_j.nextInt(16) + 8;
			l12 = this.field_28087_j.nextInt(128);
			i18 = l + this.field_28087_j.nextInt(16) + 8;
			(new WorldGenDungeons()).generate(this.worldObj, this.field_28087_j, l7, l12, i18);
		}

		for(k4 = 0; k4 < 10; ++k4) {
			l7 = k + this.field_28087_j.nextInt(16);
			l12 = this.field_28087_j.nextInt(128);
			i18 = l + this.field_28087_j.nextInt(16);
			(new WorldGenClay(32)).generate(this.worldObj, this.field_28087_j, l7, l12, i18);
		}

		for(k4 = 0; k4 < 20; ++k4) {
			l7 = k + this.field_28087_j.nextInt(16);
			l12 = this.field_28087_j.nextInt(128);
			i18 = l + this.field_28087_j.nextInt(16);
			(new WorldGenMinable(Block.dirt.blockID, 32)).generate(this.worldObj, this.field_28087_j, l7, l12, i18);
		}

		for(k4 = 0; k4 < 10; ++k4) {
			l7 = k + this.field_28087_j.nextInt(16);
			l12 = this.field_28087_j.nextInt(128);
			i18 = l + this.field_28087_j.nextInt(16);
			(new WorldGenMinable(Block.gravel.blockID, 32)).generate(this.worldObj, this.field_28087_j, l7, l12, i18);
		}

		for(k4 = 0; k4 < 20; ++k4) {
			l7 = k + this.field_28087_j.nextInt(16);
			l12 = this.field_28087_j.nextInt(128);
			i18 = l + this.field_28087_j.nextInt(16);
			(new WorldGenMinable(Block.oreCoal.blockID, 16)).generate(this.worldObj, this.field_28087_j, l7, l12, i18);
		}

		for(k4 = 0; k4 < 20; ++k4) {
			l7 = k + this.field_28087_j.nextInt(16);
			l12 = this.field_28087_j.nextInt(64);
			i18 = l + this.field_28087_j.nextInt(16);
			(new WorldGenMinable(Block.oreIron.blockID, 8)).generate(this.worldObj, this.field_28087_j, l7, l12, i18);
		}

		for(k4 = 0; k4 < 2; ++k4) {
			l7 = k + this.field_28087_j.nextInt(16);
			l12 = this.field_28087_j.nextInt(32);
			i18 = l + this.field_28087_j.nextInt(16);
			(new WorldGenMinable(Block.oreGold.blockID, 8)).generate(this.worldObj, this.field_28087_j, l7, l12, i18);
		}

		for(k4 = 0; k4 < 8; ++k4) {
			l7 = k + this.field_28087_j.nextInt(16);
			l12 = this.field_28087_j.nextInt(16);
			i18 = l + this.field_28087_j.nextInt(16);
			(new WorldGenMinable(Block.oreRedstone.blockID, 7)).generate(this.worldObj, this.field_28087_j, l7, l12, i18);
		}

		for(k4 = 0; k4 < 1; ++k4) {
			l7 = k + this.field_28087_j.nextInt(16);
			l12 = this.field_28087_j.nextInt(16);
			i18 = l + this.field_28087_j.nextInt(16);
			(new WorldGenMinable(Block.oreDiamond.blockID, 7)).generate(this.worldObj, this.field_28087_j, l7, l12, i18);
		}

		for(k4 = 0; k4 < 1; ++k4) {
			l7 = k + this.field_28087_j.nextInt(16);
			l12 = this.field_28087_j.nextInt(16) + this.field_28087_j.nextInt(16);
			i18 = l + this.field_28087_j.nextInt(16);
			(new WorldGenMinable(Block.oreLapis.blockID, 6)).generate(this.worldObj, this.field_28087_j, l7, l12, i18);
		}

		d = 0.5D;
		k4 = (int)((this.field_28094_c.func_806_a((double)k * d, (double)l * d) / 8.0D + this.field_28087_j.nextDouble() * 4.0D + 4.0D) / 3.0D);
		l7 = 0;
		if(this.field_28087_j.nextInt(10) == 0) {
			++l7;
		}

		if(biomegenbase == BiomeGenBase.forest) {
			l7 += k4 + 5;
		}

		if(biomegenbase == BiomeGenBase.plains) {
			l7 += k4 + 5;
		}

		if(biomegenbase == BiomeGenBase.plains) {
			l7 += k4 + 2;
		}

		if(biomegenbase == BiomeGenBase.forest) {
			l7 += k4 + 5;
		}

		if(biomegenbase == BiomeGenBase.desert) {
			l7 -= 20;
		}

		if(biomegenbase == BiomeGenBase.forest) {
			l7 -= 20;
		}

		if(biomegenbase == BiomeGenBase.plains) {
			l7 -= 20;
		}

		int l20;
		for(l12 = 0; l12 < l7; ++l12) {
			i18 = k + this.field_28087_j.nextInt(16) + 8;
			l20 = l + this.field_28087_j.nextInt(16) + 8;
			WorldGenerator j22 = biomegenbase.getRandomWorldGenForTrees(this.field_28087_j);
			j22.func_517_a(1.0D, 1.0D, 1.0D);
			j22.generate(this.worldObj, this.field_28087_j, i18, this.worldObj.getHeightValue(i18, l20), l20);
		}

		int i23;
		for(l12 = 0; l12 < 2; ++l12) {
			i18 = k + this.field_28087_j.nextInt(16) + 8;
			l20 = this.field_28087_j.nextInt(128);
			i23 = l + this.field_28087_j.nextInt(16) + 8;
			(new WorldGenFlowers(Block.plantYellow.blockID)).generate(this.worldObj, this.field_28087_j, i18, l20, i23);
		}

		if(this.field_28087_j.nextInt(2) == 0) {
			l12 = k + this.field_28087_j.nextInt(16) + 8;
			i18 = this.field_28087_j.nextInt(128);
			l20 = l + this.field_28087_j.nextInt(16) + 8;
			(new WorldGenFlowers(Block.plantRed.blockID)).generate(this.worldObj, this.field_28087_j, l12, i18, l20);
		}

		if(this.field_28087_j.nextInt(4) == 0) {
			l12 = k + this.field_28087_j.nextInt(16) + 8;
			i18 = this.field_28087_j.nextInt(128);
			l20 = l + this.field_28087_j.nextInt(16) + 8;
			(new WorldGenFlowers(Block.mushroomBrown.blockID)).generate(this.worldObj, this.field_28087_j, l12, i18, l20);
		}

		if(this.field_28087_j.nextInt(8) == 0) {
			l12 = k + this.field_28087_j.nextInt(16) + 8;
			i18 = this.field_28087_j.nextInt(128);
			l20 = l + this.field_28087_j.nextInt(16) + 8;
			(new WorldGenFlowers(Block.mushroomRed.blockID)).generate(this.worldObj, this.field_28087_j, l12, i18, l20);
		}

		for(l12 = 0; l12 < 10; ++l12) {
			i18 = k + this.field_28087_j.nextInt(16) + 8;
			l20 = this.field_28087_j.nextInt(128);
			i23 = l + this.field_28087_j.nextInt(16) + 8;
			(new WorldGenReed()).generate(this.worldObj, this.field_28087_j, i18, l20, i23);
		}

		if(this.field_28087_j.nextInt(32) == 0) {
			l12 = k + this.field_28087_j.nextInt(16) + 8;
			i18 = this.field_28087_j.nextInt(128);
			l20 = l + this.field_28087_j.nextInt(16) + 8;
			(new WorldGenPumpkin()).generate(this.worldObj, this.field_28087_j, l12, i18, l20);
		}

		l12 = 0;
		if(biomegenbase == BiomeGenBase.plains) {
			l12 += 10;
		}

		int j23;
		for(i18 = 0; i18 < l12; ++i18) {
			l20 = k + this.field_28087_j.nextInt(16) + 8;
			i23 = this.field_28087_j.nextInt(128);
			j23 = l + this.field_28087_j.nextInt(16) + 8;
			(new WorldGenCactus()).generate(this.worldObj, this.field_28087_j, l20, i23, j23);
		}

		for(i18 = 0; i18 < 50; ++i18) {
			l20 = k + this.field_28087_j.nextInt(16) + 8;
			i23 = this.field_28087_j.nextInt(this.field_28087_j.nextInt(120) + 8);
			j23 = l + this.field_28087_j.nextInt(16) + 8;
			(new WorldGenLiquids(Block.waterMoving.blockID)).generate(this.worldObj, this.field_28087_j, l20, i23, j23);
		}

		for(i18 = 0; i18 < 20; ++i18) {
			l20 = k + this.field_28087_j.nextInt(16) + 8;
			i23 = this.field_28087_j.nextInt(this.field_28087_j.nextInt(this.field_28087_j.nextInt(112) + 8) + 8);
			j23 = l + this.field_28087_j.nextInt(16) + 8;
			(new WorldGenLiquids(Block.lavaMoving.blockID)).generate(this.worldObj, this.field_28087_j, l20, i23, j23);
		}

		this.field_28074_w = this.worldObj.getWorldChunkManager().getTemperatures(this.field_28074_w, k + 8, l + 8, 16, 16);

		for(i18 = k + 8; i18 < k + 8 + 16; ++i18) {
			for(l20 = l + 8; l20 < l + 8 + 16; ++l20) {
				i23 = i18 - (k + 8);
				j23 = l20 - (l + 8);
				int k23 = this.worldObj.findTopSolidBlock(i18, l20);
				double d1 = this.field_28074_w[i23 * 16 + j23] - (double)(k23 - 64) / 64.0D * 0.3D;
				if(d1 < 0.5D && k23 > 0 && k23 < 128 && this.worldObj.isAirBlock(i18, k23, l20) && this.worldObj.getBlockMaterial(i18, k23 - 1, l20).getIsSolid() && this.worldObj.getBlockMaterial(i18, k23 - 1, l20) != Material.ice) {
					this.worldObj.setBlockWithNotify(i18, k23, l20, Block.snow.blockID);
				}
			}
		}

		BlockSand.fallInstantly = false;
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
		return true;
	}

	public String makeString() {
		return "RandomLevelSource";
	}
}
