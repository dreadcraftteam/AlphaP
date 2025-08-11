package net.minecraft.src;

import java.util.Random;

public class ChunkProviderHell implements IChunkProvider {
	private Random hellRNG;
	private NoiseGeneratorOctaves field_4169_i;
	private NoiseGeneratorOctaves field_4168_j;
	private NoiseGeneratorOctaves field_4167_k;
	private NoiseGeneratorOctaves field_4166_l;
	private NoiseGeneratorOctaves field_4165_m;
	public NoiseGeneratorOctaves field_4177_a;
	public NoiseGeneratorOctaves field_4176_b;
	private World worldObj;
	private double[] field_4163_o;
	private double[] field_4162_p = new double[256];
	private double[] field_4161_q = new double[256];
	private double[] field_4160_r = new double[256];
	private MapGenBase field_4159_s = new MapGenCavesHell();
	double[] field_4175_c;
	double[] field_4174_d;
	double[] field_4173_e;
	double[] field_4172_f;
	double[] field_4171_g;

	public ChunkProviderHell(World world, long l) {
		this.worldObj = world;
		this.hellRNG = new Random(l);
		this.field_4169_i = new NoiseGeneratorOctaves(this.hellRNG, 16);
		this.field_4168_j = new NoiseGeneratorOctaves(this.hellRNG, 16);
		this.field_4167_k = new NoiseGeneratorOctaves(this.hellRNG, 8);
		this.field_4166_l = new NoiseGeneratorOctaves(this.hellRNG, 4);
		this.field_4165_m = new NoiseGeneratorOctaves(this.hellRNG, 4);
		this.field_4177_a = new NoiseGeneratorOctaves(this.hellRNG, 10);
		this.field_4176_b = new NoiseGeneratorOctaves(this.hellRNG, 16);
	}

	public void func_4059_a(int i, int j, byte[] abyte0) {
		byte byte0 = 4;
		byte byte1 = 32;
		int k = byte0 + 1;
		byte byte2 = 17;
		int l = byte0 + 1;
		this.field_4163_o = this.func_4057_a(this.field_4163_o, i * byte0, 0, j * byte0, k, byte2, l);

		for(int i1 = 0; i1 < byte0; ++i1) {
			for(int j1 = 0; j1 < byte0; ++j1) {
				for(int k1 = 0; k1 < 16; ++k1) {
					double d = 0.125D;
					double d1 = this.field_4163_o[((i1 + 0) * l + j1 + 0) * byte2 + k1 + 0];
					double d2 = this.field_4163_o[((i1 + 0) * l + j1 + 1) * byte2 + k1 + 0];
					double d3 = this.field_4163_o[((i1 + 1) * l + j1 + 0) * byte2 + k1 + 0];
					double d4 = this.field_4163_o[((i1 + 1) * l + j1 + 1) * byte2 + k1 + 0];
					double d5 = (this.field_4163_o[((i1 + 0) * l + j1 + 0) * byte2 + k1 + 1] - d1) * d;
					double d6 = (this.field_4163_o[((i1 + 0) * l + j1 + 1) * byte2 + k1 + 1] - d2) * d;
					double d7 = (this.field_4163_o[((i1 + 1) * l + j1 + 0) * byte2 + k1 + 1] - d3) * d;
					double d8 = (this.field_4163_o[((i1 + 1) * l + j1 + 1) * byte2 + k1 + 1] - d4) * d;

					for(int l1 = 0; l1 < 8; ++l1) {
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for(int i2 = 0; i2 < 4; ++i2) {
							int j2 = i2 + i1 * 4 << 11 | 0 + j1 * 4 << 7 | k1 * 8 + l1;
							short c = 128;
							double d14 = 0.25D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;

							for(int k2 = 0; k2 < 4; ++k2) {
								int l2 = 0;
								if(k1 * 8 + l1 < byte1) {
									l2 = Block.lavaStill.blockID;
								}

								if(d15 > 0.0D) {
									l2 = Block.netherrack.blockID;
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

	public void func_4058_b(int i, int j, byte[] abyte0) {
		byte byte0 = 64;
		double d = 8.0D / 256D;
		this.field_4162_p = this.field_4166_l.generateNoiseOctaves(this.field_4162_p, (double)(i * 16), (double)(j * 16), 0.0D, 16, 16, 1, d, d, 1.0D);
		this.field_4161_q = this.field_4166_l.generateNoiseOctaves(this.field_4161_q, (double)(i * 16), 109.0134D, (double)(j * 16), 16, 1, 16, d, 1.0D, d);
		this.field_4160_r = this.field_4165_m.generateNoiseOctaves(this.field_4160_r, (double)(i * 16), (double)(j * 16), 0.0D, 16, 16, 1, d * 2.0D, d * 2.0D, d * 2.0D);

		for(int k = 0; k < 16; ++k) {
			for(int l = 0; l < 16; ++l) {
				boolean flag = this.field_4162_p[k + l * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
				boolean flag1 = this.field_4161_q[k + l * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
				int i1 = (int)(this.field_4160_r[k + l * 16] / 3.0D + 3.0D + this.hellRNG.nextDouble() * 0.25D);
				int j1 = -1;
				byte byte1 = (byte)Block.netherrack.blockID;
				byte byte2 = (byte)Block.netherrack.blockID;

				for(int k1 = 127; k1 >= 0; --k1) {
					int l1 = (l * 16 + k) * 128 + k1;
					byte byte3 = abyte0[l1];
					if(byte3 == 0) {
						j1 = -1;
					} else if(byte3 == Block.netherrack.blockID) {
						if(j1 == -1) {
							if(i1 <= 0) {
								byte1 = 0;
								byte2 = (byte)Block.netherrack.blockID;
							} else if(k1 >= byte0 - 4 && k1 <= byte0 + 1) {
								byte1 = (byte)Block.netherrack.blockID;
								byte2 = (byte)Block.netherrack.blockID;
								if(flag1) {
									byte1 = (byte)Block.gravel.blockID;
								}

								if(flag1) {
									byte2 = (byte)Block.netherrack.blockID;
								}

								if(flag) {
									byte1 = (byte)Block.slowSand.blockID;
								}

								if(flag) {
									byte2 = (byte)Block.slowSand.blockID;
								}
							}

							if(k1 < byte0 && byte1 == 0) {
								byte1 = (byte)Block.lavaStill.blockID;
							}

							j1 = i1;
							if(k1 >= byte0 - 1) {
								abyte0[l1] = byte1;
							} else {
								abyte0[l1] = byte2;
							}
						} else if(j1 > 0) {
							--j1;
							abyte0[l1] = byte2;
						}
					}
				}
			}
		}

	}

	public Chunk prepareChunk(int i, int j) {
		return this.provideChunk(i, j);
	}

	public ChunkCube prepareCube(int x, int y, int z) {
		return this.provideCube(x, y, z);
	}

	public Chunk provideChunk(int i, int j) {
		this.hellRNG.setSeed((long)i * 341873128712L + (long)j * 132897987541L);
		byte[] data = new byte[32768];
		this.func_4059_a(i, j, data);
		this.func_4058_b(i, j, data);
		this.field_4159_s.func_867_a(this, this.worldObj, i, j, data);
		Chunk chunk = new Chunk(this.worldObj, data, i, j);
		return chunk;
	}

	public ChunkCube provideCube(int x, int y, int z) {
		Chunk chunk = this.worldObj.getChunkFromChunkCoords(x, z);
		if(chunk == null || chunk.neverSave) {
			chunk = this.provideChunk(x, y);
		}

		if(y < 0 || y >= 8) {
			byte[] data = new byte[4096];
			byte fillID = (byte)Block.netherrack.blockID;

			for(int cube = 0; cube < data.length; ++cube) {
				data[cube] = fillID;
			}

			ChunkCube chunkCube8 = chunk.cubes[y + 2047] = new ChunkCube(chunk, data, y + 2047);
			chunkCube8.isChunkLoaded = true;
		}

		return chunk.cubes[y + 2047];
	}

	private double[] func_4057_a(double[] ad, int i, int j, int k, int l, int i1, int j1) {
		if(ad == null) {
			ad = new double[l * i1 * j1];
		}

		double d = 684.412D;
		double d1 = 2053.236D;
		this.field_4172_f = this.field_4177_a.generateNoiseOctaves(this.field_4172_f, (double)i, (double)j, (double)k, l, 1, j1, 1.0D, 0.0D, 1.0D);
		this.field_4171_g = this.field_4176_b.generateNoiseOctaves(this.field_4171_g, (double)i, (double)j, (double)k, l, 1, j1, 100.0D, 0.0D, 100.0D);
		this.field_4175_c = this.field_4167_k.generateNoiseOctaves(this.field_4175_c, (double)i, (double)j, (double)k, l, i1, j1, d / 80.0D, d1 / 60.0D, d / 80.0D);
		this.field_4174_d = this.field_4169_i.generateNoiseOctaves(this.field_4174_d, (double)i, (double)j, (double)k, l, i1, j1, d, d1, d);
		this.field_4173_e = this.field_4168_j.generateNoiseOctaves(this.field_4173_e, (double)i, (double)j, (double)k, l, i1, j1, d, d1, d);
		int k1 = 0;
		int l1 = 0;
		double[] ad1 = new double[i1];

		int j2;
		for(j2 = 0; j2 < i1; ++j2) {
			ad1[j2] = Math.cos((double)j2 * Math.PI * 6.0D / (double)i1) * 2.0D;
			double k2 = (double)j2;
			if(j2 > i1 / 2) {
				k2 = (double)(i1 - 1 - j2);
			}

			if(k2 < 4.0D) {
				k2 = 4.0D - k2;
				ad1[j2] -= k2 * k2 * k2 * 10.0D;
			}
		}

		for(j2 = 0; j2 < l; ++j2) {
			for(int i36 = 0; i36 < j1; ++i36) {
				double d3 = (this.field_4172_f[l1] + 256.0D) / 512.0D;
				if(d3 > 1.0D) {
					d3 = 1.0D;
				}

				double d4 = 0.0D;
				double d5 = this.field_4171_g[l1] / 8000.0D;
				if(d5 < 0.0D) {
					d5 = -d5;
				}

				d5 = d5 * 3.0D - 3.0D;
				if(d5 < 0.0D) {
					d5 /= 2.0D;
					if(d5 < -1.0D) {
						d5 = -1.0D;
					}

					d5 /= 1.4D;
					d5 /= 2.0D;
					d3 = 0.0D;
				} else {
					if(d5 > 1.0D) {
						d5 = 1.0D;
					}

					d5 /= 6.0D;
				}

				d3 += 0.5D;
				d5 = d5 * (double)i1 / 16.0D;
				++l1;

				for(int l2 = 0; l2 < i1; ++l2) {
					double d6 = 0.0D;
					double d7 = ad1[l2];
					double d8 = this.field_4174_d[k1] / 512.0D;
					double d9 = this.field_4173_e[k1] / 512.0D;
					double d10 = (this.field_4175_c[k1] / 10.0D + 1.0D) / 2.0D;
					if(d10 < 0.0D) {
						d6 = d8;
					} else if(d10 > 1.0D) {
						d6 = d9;
					} else {
						d6 = d8 + (d9 - d8) * d10;
					}

					d6 -= d7;
					double d12;
					if(l2 > i1 - 4) {
						d12 = (double)((float)(l2 - (i1 - 4)) / 3.0F);
						d6 = d6 * (1.0D - d12) + -10.0D * d12;
					}

					if((double)l2 < d4) {
						d12 = (d4 - (double)l2) / 4.0D;
						if(d12 < 0.0D) {
							d12 = 0.0D;
						}

						if(d12 > 1.0D) {
							d12 = 1.0D;
						}

						d6 = d6 * (1.0D - d12) + -10.0D * d12;
					}

					ad[k1] = d6;
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
		BlockSand.fallInstantly = true;
		int xBl = x * 16;
		int yBl = y * 16;
		int zBl = z * 16;

		int j1;
		int l2;
		int j4;
		int l5;
		for(j1 = 0; j1 < 1; ++j1) {
			l2 = xBl + this.hellRNG.nextInt(16) + 8;
			j4 = yBl + this.hellRNG.nextInt(16) + 8;
			l5 = zBl + this.hellRNG.nextInt(16) + 8;
			(new WorldGenHellLava(Block.lavaMoving.blockID)).generate(this.worldObj, this.hellRNG, l2, j4, l5);
		}

		j1 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1;

		int k6;
		for(l2 = 0; l2 < j1; ++l2) {
			if(this.hellRNG.nextInt(8) == 0) {
				j4 = xBl + this.hellRNG.nextInt(16) + 8;
				l5 = yBl + this.hellRNG.nextInt(16) + 8;
				k6 = zBl + this.hellRNG.nextInt(16) + 8;
				(new WorldGenFire()).generate(this.worldObj, this.hellRNG, j4, l5, k6);
			}
		}

		j1 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1);

		for(l2 = 0; l2 < j1; ++l2) {
			if(this.hellRNG.nextInt(8) == 0) {
				j4 = xBl + this.hellRNG.nextInt(16) + 8;
				l5 = yBl + this.hellRNG.nextInt(16) + 8;
				k6 = zBl + this.hellRNG.nextInt(16) + 8;
				(new WorldGenGlowStone1()).generate(this.worldObj, this.hellRNG, j4, l5, k6);
			}
		}

		for(l2 = 0; l2 < 5; ++l2) {
			if(this.hellRNG.nextInt(4) == 0) {
				j4 = xBl + this.hellRNG.nextInt(16) + 8;
				l5 = yBl + this.hellRNG.nextInt(16) + 8;
				k6 = zBl + this.hellRNG.nextInt(16) + 8;
				(new WorldGenGlowStone2()).generate(this.worldObj, this.hellRNG, j4, l5, k6);
			}
		}

		if(this.hellRNG.nextInt(8) == 0) {
			l2 = xBl + this.hellRNG.nextInt(16) + 8;
			j4 = yBl + this.hellRNG.nextInt(16) + 8;
			l5 = zBl + this.hellRNG.nextInt(16) + 8;
			(new WorldGenFlowers(Block.mushroomBrown.blockID)).generate(this.worldObj, this.hellRNG, l2, j4, l5);
		}

		if(this.hellRNG.nextInt(8) == 0) {
			l2 = xBl + this.hellRNG.nextInt(16) + 8;
			j4 = yBl + this.hellRNG.nextInt(16) + 8;
			l5 = zBl + this.hellRNG.nextInt(16) + 8;
			(new WorldGenFlowers(Block.mushroomRed.blockID)).generate(this.worldObj, this.hellRNG, l2, j4, l5);
		}

		BlockSand.fallInstantly = false;
	}

	public void a(IChunkProvider ichunkprovider, int i, int j) {
		BlockSand.fallInstantly = true;
		int k = i * 16;
		int l = j * 16;

		int j1;
		int l2;
		int j4;
		int l5;
		for(j1 = 0; j1 < 8; ++j1) {
			l2 = k + this.hellRNG.nextInt(16) + 8;
			j4 = this.hellRNG.nextInt(120) + 4;
			l5 = l + this.hellRNG.nextInt(16) + 8;
			(new WorldGenHellLava(Block.lavaMoving.blockID)).generate(this.worldObj, this.hellRNG, l2, j4, l5);
		}

		j1 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1;

		int k6;
		for(l2 = 0; l2 < j1; ++l2) {
			j4 = k + this.hellRNG.nextInt(16) + 8;
			l5 = this.hellRNG.nextInt(120) + 4;
			k6 = l + this.hellRNG.nextInt(16) + 8;
			(new WorldGenFire()).generate(this.worldObj, this.hellRNG, j4, l5, k6);
		}

		j1 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1);

		for(l2 = 0; l2 < j1; ++l2) {
			j4 = k + this.hellRNG.nextInt(16) + 8;
			l5 = this.hellRNG.nextInt(120) + 4;
			k6 = l + this.hellRNG.nextInt(16) + 8;
			(new WorldGenGlowStone1()).generate(this.worldObj, this.hellRNG, j4, l5, k6);
		}

		for(l2 = 0; l2 < 10; ++l2) {
			j4 = k + this.hellRNG.nextInt(16) + 8;
			l5 = this.hellRNG.nextInt(128);
			k6 = l + this.hellRNG.nextInt(16) + 8;
			(new WorldGenGlowStone2()).generate(this.worldObj, this.hellRNG, j4, l5, k6);
		}

		if(this.hellRNG.nextInt(1) == 0) {
			l2 = k + this.hellRNG.nextInt(16) + 8;
			j4 = this.hellRNG.nextInt(128);
			l5 = l + this.hellRNG.nextInt(16) + 8;
			(new WorldGenFlowers(Block.mushroomBrown.blockID)).generate(this.worldObj, this.hellRNG, l2, j4, l5);
		}

		if(this.hellRNG.nextInt(1) == 0) {
			l2 = k + this.hellRNG.nextInt(16) + 8;
			j4 = this.hellRNG.nextInt(128);
			l5 = l + this.hellRNG.nextInt(16) + 8;
			(new WorldGenFlowers(Block.mushroomRed.blockID)).generate(this.worldObj, this.hellRNG, l2, j4, l5);
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
		return "HellRandomLevelSource";
	}
}
