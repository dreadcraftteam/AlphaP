package net.minecraft_server.src;

import java.util.Arrays;
import java.util.Random;

public class ChunkProviderGenerate implements IChunkProvider {
	private Random rand;
	private NoiseGeneratorOctaves noiseGen1;
	private NoiseGeneratorOctaves noiseGen2;
	private NoiseGeneratorOctaves noiseGen3;
	private NoiseGeneratorOctaves noiseGen4;
	private NoiseGeneratorOctaves noiseGen5;
	public NoiseGeneratorOctaves noiseGen6;
	public NoiseGeneratorOctaves noiseGen7;
	public NoiseGeneratorOctaves mobSpawnerNoise;
	private World worldObj;
	private double[] field_4180_q;
	private double[] sandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	private double[] stoneNoise = new double[256];
	private MapGenBase mapGenCaves = new MapGenCaves();
	private BiomeGenBase[] biomesForGeneration;
	double[] field_4229_d;
	double[] field_4228_e;
	double[] field_4227_f;
	double[] field_4226_g;
	double[] field_4225_h;
	int[][] unusedIntArray32x32 = new int[32][32];
	private double[] generatedTemperatures;
	public static final byte seaLevel = 64;

	public ChunkProviderGenerate(World world, long l) {
		this.worldObj = world;
		this.rand = new Random(l);
		this.noiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
		this.noiseGen4 = new NoiseGeneratorOctaves(this.rand, 4);
		this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 4);
		this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 10);
		this.noiseGen7 = new NoiseGeneratorOctaves(this.rand, 16);
		this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
	}

	public void generateTerrain(int i, int j, byte[] abyte0, BiomeGenBase[] abiomegenbase, double[] ad) {
		byte byte0 = 4;
		byte byte1 = 64;
		int k = byte0 + 1;
		byte byte2 = 65;
		int l = byte0 + 1;
		this.field_4180_q = this.func_4061_a(this.field_4180_q, i * byte0, 0, j * byte0, k, byte2, l);

		for (int i1 = 0; i1 < byte0; ++i1) {
			for (int j1 = 0; j1 < byte0; ++j1) {
				for (int k1 = 0; k1 < 16; ++k1) {
					double d = 0.125D;
					double d1 = this.field_4180_q[((i1 + 0) * l + j1 + 0) * byte2 + k1 + 0];
					double d2 = this.field_4180_q[((i1 + 0) * l + j1 + 1) * byte2 + k1 + 0];
					double d3 = this.field_4180_q[((i1 + 1) * l + j1 + 0) * byte2 + k1 + 0];
					double d4 = this.field_4180_q[((i1 + 1) * l + j1 + 1) * byte2 + k1 + 0];
					double d5 = (this.field_4180_q[((i1 + 0) * l + j1 + 0) * byte2 + k1 + 1] - d1) * d;
					double d6 = (this.field_4180_q[((i1 + 0) * l + j1 + 1) * byte2 + k1 + 1] - d2) * d;
					double d7 = (this.field_4180_q[((i1 + 1) * l + j1 + 0) * byte2 + k1 + 1] - d3) * d;
					double d8 = (this.field_4180_q[((i1 + 1) * l + j1 + 1) * byte2 + k1 + 1] - d4) * d;

					for (int l1 = 0; l1 < 8; ++l1) {
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for (int i2 = 0; i2 < 4; ++i2) {
							int j2 = i2 + i1 * 4 << 11 | 0 + j1 * 4 << 7 | k1 * 8 + l1;
							short c = 128;
							double d14 = 0.25D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;

							for (int k2 = 0; k2 < 4; ++k2) {
								double d17 = ad[(i1 * 4 + i2) * 16 + j1 * 4 + k2];
								int l2 = 0;
								if (k1 * 8 + l1 < byte1) {
									if (d17 < 0.5D && k1 * 8 + l1 >= byte1 - 1) {
										l2 = Block.ice.blockID;
									} else {
										l2 = Block.waterStill.blockID;
									}
								}

								if (d15 > 0.0D) {
									l2 = Block.stone.blockID;
								}

								abyte0[j2] = (byte) l2;
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

	public void replaceBlocksForBiome(int i, int j, byte[] abyte0, BiomeGenBase[] abiomegenbase) {
		byte byte0 = 64;
		double d = 8.0D / 256D;
		this.sandNoise = this.noiseGen4.generateNoiseOctaves(this.sandNoise, (double) (i * 16), (double) (j * 16), 0.0D,
				16, 16, 1, d, d, 1.0D);
		this.gravelNoise = this.noiseGen4.generateNoiseOctaves(this.gravelNoise, (double) (i * 16), 109.0134D,
				(double) (j * 16), 16, 1, 16, d, 1.0D, d);
		this.stoneNoise = this.noiseGen5.generateNoiseOctaves(this.stoneNoise, (double) (i * 16), (double) (j * 16),
				0.0D, 16, 16, 1, d * 2.0D, d * 2.0D, d * 2.0D);

		for (int k = 0; k < 16; ++k) {
			for (int l = 0; l < 16; ++l) {
				BiomeGenBase biomegenbase = abiomegenbase[k + l * 16];
				boolean flag = this.sandNoise[k + l * 16] + this.rand.nextDouble() * 0.2D > 0.0D;
				boolean flag1 = this.gravelNoise[k + l * 16] + this.rand.nextDouble() * 0.2D > 3.0D;
				int i1 = (int) (this.stoneNoise[k + l * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
				int j1 = -1;
				byte byte1 = biomegenbase.topBlock;
				byte byte2 = biomegenbase.fillerBlock;

				for (int k1 = 127; k1 >= 0; --k1) {
					int l1 = (l * 16 + k) * 128 + k1;
					byte byte3 = abyte0[l1];
					if (byte3 == 0) {
						j1 = -1;
					} else if (byte3 == Block.stone.blockID) {
						if (j1 == -1) {
							if (i1 <= 0) {
								byte1 = 0;
								byte2 = (byte) Block.stone.blockID;
							} else if (k1 >= byte0 - 4 && k1 <= byte0 + 1) {
								byte1 = biomegenbase.topBlock;
								byte2 = biomegenbase.fillerBlock;
								if (flag1) {
									byte1 = 0;
								}

								if (flag1) {
									byte2 = (byte) Block.gravel.blockID;
								}

								if (flag) {
									byte1 = (byte) Block.sand.blockID;
								}

								if (flag) {
									byte2 = (byte) Block.sand.blockID;
								}
							}

							if (k1 < byte0 && byte1 == 0) {
								byte1 = (byte) Block.waterStill.blockID;
							}

							j1 = i1;
							if (k1 >= byte0 - 1) {
								abyte0[l1] = byte1;
							} else {
								abyte0[l1] = byte2;
							}
						} else if (j1 > 0) {
							--j1;
							abyte0[l1] = byte2;
							if (j1 == 0 && byte2 == Block.sand.blockID) {
								j1 = this.rand.nextInt(4);
								byte2 = (byte) Block.sandStone.blockID;
							}
						}
					}
				}
			}
		}

	}

	public void generateTerrain(int x, int y, int z, byte[] data, BiomeGenBase[] biomes, double[] temperatures) {
		byte byte0 = 4;
		int k = byte0 + 1;
		byte byte2 = 65;
		int l = byte0 + 1;
		this.field_4180_q = this.func_4061_a(this.field_4180_q, x * byte0, 0, z * byte0, k, byte2, l);

		for (int x1 = 0; x1 < byte0; ++x1) {
			for (int z1 = 0; z1 < byte0; ++z1) {
				for (int y1 = 0; y1 < 16; ++y1) {
					double d = 0.125D;
					double d1 = this.field_4180_q[((x1 + 0) * l + z1 + 0) * byte2 + y1 + 0];
					double d2 = this.field_4180_q[((x1 + 0) * l + z1 + 1) * byte2 + y1 + 0];
					double d3 = this.field_4180_q[((x1 + 1) * l + z1 + 0) * byte2 + y1 + 0];
					double d4 = this.field_4180_q[((x1 + 1) * l + z1 + 1) * byte2 + y1 + 0];
					double d5 = (this.field_4180_q[((x1 + 0) * l + z1 + 0) * byte2 + y1 + 1] - d1) * d;
					double d6 = (this.field_4180_q[((x1 + 0) * l + z1 + 1) * byte2 + y1 + 1] - d2) * d;
					double d7 = (this.field_4180_q[((x1 + 1) * l + z1 + 0) * byte2 + y1 + 1] - d3) * d;
					double d8 = (this.field_4180_q[((x1 + 1) * l + z1 + 1) * byte2 + y1 + 1] - d4) * d;

					for (int y2 = 0; y2 < 8; ++y2) {
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for (int x2 = 0; x2 < 4; ++x2) {
							int j2 = x2 + x1 * 4 << 11 | 0 + z1 * 4 << 7 | y1 * 8 + y2;
							short c = 128;
							double d14 = 0.25D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;

							for (int k2 = 0; k2 < 4; ++k2) {
								double d17 = temperatures[(x1 * 4 + x2) * 16 + z1 * 4 + k2];
								int l2 = 0;
								if (y1 * 8 + y2 < 64) {
									if (d17 < 0.5D && y1 * 8 + y2 >= 63) {
										l2 = Block.ice.blockID;
									} else {
										l2 = Block.waterStill.blockID;
									}
								}

								if (d15 > 0.0D) {
									l2 = Block.stone.blockID;
								}

								data[j2] = (byte) l2;
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

	public void replaceBlocksForBiome(int xCh, int yCh, int zCh, byte[] data, BiomeGenBase[] biomes) {
		int yChBlocks = yCh << 4;
		double noiseModifier = 8.0D / 256D;
		this.sandNoise = this.noiseGen4.generateNoiseOctaves(this.sandNoise, (double) (xCh * 16), (double) (zCh * 16),
				0.0D, 16, 16, 1, noiseModifier, noiseModifier, 1.0D);
		this.gravelNoise = this.noiseGen4.generateNoiseOctaves(this.gravelNoise, (double) (xCh * 16), 109.0134D,
				(double) (zCh * 16), 16, 1, 16, noiseModifier, 1.0D, noiseModifier);
		this.stoneNoise = this.noiseGen5.generateNoiseOctaves(this.stoneNoise, (double) (xCh * 16), (double) (zCh * 16),
				0.0D, 16, 16, 1, noiseModifier * 2.0D, noiseModifier * 2.0D, noiseModifier * 2.0D);

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				BiomeGenBase biome = biomes[x + z * 16];
				boolean sufficientSandNoise = this.sandNoise[x + z * 16] + this.rand.nextDouble() * 0.2D > 0.0D;
				boolean sufficientGravelNoise = this.gravelNoise[x + z * 16] + this.rand.nextDouble() * 0.2D > 3.0D;
				int stoneNoiseVal = (int) (this.stoneNoise[x + z * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
				int j1 = -1;
				byte topBlock = biome.topBlock;
				byte fillerBlock = biome.fillerBlock;

				for (int y = 15; y >= 0; --y) {
					int offset = (z << 8) + (x << 4) + y;
					byte curid = data[offset];
					if (curid == 0) {
						j1 = -1;
					} else if (curid == Block.stone.blockID) {
						if (j1 == -1) {
							if (stoneNoiseVal <= 0) {
								topBlock = 0;
								fillerBlock = (byte) Block.stone.blockID;
							} else if (y + yChBlocks >= 60 && y + yChBlocks <= 65) {
								topBlock = biome.topBlock;
								fillerBlock = biome.fillerBlock;
								if (sufficientGravelNoise) {
									topBlock = 0;
									fillerBlock = (byte) Block.gravel.blockID;
								}

								if (sufficientSandNoise) {
									topBlock = (byte) Block.sandStone.blockID;
									fillerBlock = (byte) Block.sandStone.blockID;
								}
							}

							if (y + yChBlocks < 64 && topBlock == 0) {
								topBlock = (byte) Block.waterStill.blockID;
							}

							j1 = stoneNoiseVal;
							if (y + yChBlocks >= 63) {
								data[offset] = topBlock;
							} else {
								data[offset] = fillerBlock;
							}
						} else if (j1 > 0) {
							--j1;
							if (fillerBlock == Block.sand.blockID) {
								data[offset] = (byte) Block.sandStone.blockID;
							} else {
								data[offset] = fillerBlock;
							}
						}
					}
				}
			}
		}

	}

	public Chunk loadChunk(int i, int j) {
		return this.provideChunk(i, j);
	}

	public ChunkCube loadCube(int x, int y, int z) {
		return this.provideCube(x, y, z);
	}

	public Chunk provideChunk(int x, int z) {
		this.rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		byte[] data = new byte[32768];
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration,
				x * 16, z * 16, 16, 16);
		double[] ad = this.worldObj.getWorldChunkManager().temperature;
		this.generateTerrain(x, z, data, this.biomesForGeneration, ad);
		this.replaceBlocksForBiome(x, z, data, this.biomesForGeneration);
		this.mapGenCaves.func_667_a(this, this.worldObj, x, z, data);
		Chunk chunk = new Chunk(this.worldObj, data, x, z);
		chunk.generateHeightMap();
		return chunk;
	}

	public ChunkCube provideCube(int x, int y, int z) {
		Chunk chunk = this.worldObj.getChunkFromChunkCoords(x, z);
		if (chunk == null || chunk.neverSave) {
			chunk = this.provideChunk(x, z);
		}

		byte[] data;
		byte cube;
		int cube1;
		ChunkCube chunkCube13;
		if (y < -2043) {
			data = new byte[4096];
			cube = (byte) Block.lavaStill.blockID;

			for (cube1 = 0; cube1 < data.length; ++cube1) {
				data[cube1] = cube;
			}

			if (y == -2047) {
				byte b12 = (byte) Block.bedrock.blockID;

				for (int x1 = 0; x1 < 16; ++x1) {
					for (int z1 = 0; z1 < 16; ++z1) {
						for (int y1 = 0; y1 < 16; ++y1) {
							if (y1 <= this.rand.nextInt(8)) {
								data[x1 << 8 | z1 << 4 | y1] = b12;
							}
						}
					}
				}
			}

			chunkCube13 = chunk.cubes[y + 2047] = new ChunkCube(chunk, data, y + 2047);
			chunkCube13.isChunkLoaded = true;
			chunkCube13.isTerrainPopulated = true;
		} else if (y < 0) {
			this.rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
			data = new byte[4096];
			cube = (byte) Block.stone.blockID;

			for (cube1 = 0; cube1 < data.length; ++cube1) {
				data[cube1] = cube;
			}

			chunkCube13 = chunk.cubes[y + 2047] = new ChunkCube(chunk, data, y + 2047);
			chunkCube13.isChunkLoaded = true;
			if (y << 4 >= chunk.lowestBlockHeight) {
				chunk.spreadLighting();
			}
		} else if (chunk.cubes[y + 2047] == null) {
			data = new byte[4096];
			ChunkCube chunkCube11 = new ChunkCube(chunk, data, y + 2047);
			chunkCube11.isAir = true;
			chunkCube11.isChunkLoaded = true;
			chunkCube11.isTerrainPopulated = true;
			chunk.cubes[y + 2047] = chunkCube11;
			Arrays.fill(chunkCube11.skylightMap.data, (byte) -1);
		}

		return chunk.cubes[y + 2047];
	}

	private double[] func_4061_a(double[] ad, int x, int y, int z, int xSize, int ySize, int zSize) {
		if (ad == null) {
			ad = new double[xSize * ySize * zSize];
		}

		double d = 6000.0D;
		double d1 = 8000.0D;
		// double d = 684.412D;
		// double d1 = 684.412D;
		double[] ad1 = this.worldObj.getWorldChunkManager().temperature;
		double[] ad2 = this.worldObj.getWorldChunkManager().humidity;
		this.field_4226_g = this.noiseGen6.func_4103_a(this.field_4226_g, x, z, xSize, zSize, 1.121D, 1.121D, 0.5D);
		this.field_4225_h = this.noiseGen7.func_4103_a(this.field_4225_h, x, z, xSize, zSize, 200.0D, 200.0D, 0.5D);
		this.field_4229_d = this.noiseGen3.generateNoiseOctaves(this.field_4229_d, (double) x, (double) y, (double) z,
				xSize, ySize, zSize, d / 160.0D, d1 / 3200.0D, d / 1600.0D);
		this.field_4228_e = this.noiseGen1.generateNoiseOctaves(this.field_4228_e, (double) x, (double) y, (double) z,
				xSize, ySize, zSize, d, d1, d);
		this.field_4227_f = this.noiseGen2.generateNoiseOctaves(this.field_4227_f, (double) x, (double) y, (double) z,
				xSize, ySize, zSize, d, d1, d);
		int k1 = 0;
		int l1 = 0;
		int i2 = 16 / xSize;

		for (int j2 = 0; j2 < xSize; ++j2) {
			int k2 = j2 * i2 + i2 / 2;

			for (int l2 = 0; l2 < zSize; ++l2) {
				int i3 = l2 * i2 + i2 / 2;
				double d2 = ad1[k2 * 16 + i3];
				double d3 = ad2[k2 * 16 + i3] * d2;
				double d4 = 1.0D - d3;
				d4 *= d4;
				d4 *= d4;
				d4 = 1.0D - d4;
				double d5 = (this.field_4226_g[l1] + 256.0D) / 512.0D;
				d5 *= d4;
				if (d5 > 1.0D) {
					d5 = 1.0D;
				}

				double d6 = this.field_4225_h[l1] / 8000.0D;
				if (d6 < 0.0D) {
					d6 = -d6 * 0.3D;
				}

				d6 = d6 / 8.0d;
				d6 = Math.pow(Math.abs(d6) * 50.0D, 1.2D) * Math.signum(d6);
				d6 = d6 * 50.0D - 2.0D;
				if (d6 < 0.0D) {
					d6 /= 2.0D;
					if (d6 < -1.0D) {
						d6 = -1.0D;
					}

					d6 /= 1.4D;
					d6 /= 2.0D;
					d5 = 0.0D;
				} else {
					if (d6 > 1.0D) {
						d6 = 1.0D;
					}

					d6 /= 8.0D;
				}

				if (d5 < 0.0D) {
					d5 = 0.0D;
				}

				d5 += 0.5D;
				d6 = d6 * (double) ySize / 16.0D;
				double d7 = (double) ySize / 2.0D + d6 * 4.0D;
				++l1;

				for (int j3 = 0; j3 < ySize; ++j3) {
					double d8 = 0.0D;
					double d9 = ((double) j3 - d7) * 8.0D / d5;
					if (d9 < 0.0D) {
						d9 *= 4.0D;
					}

					double d10 = this.field_4228_e[k1] / 512.0D;
					double d11 = this.field_4227_f[k1] / 512.0D;
					double d12 = (this.field_4229_d[k1] / 10.0D + 1.0D) / 2.0D;
					if (d12 < 0.0D) {
						d8 = d10;
					} else if (d12 > 1.0D) {
						d8 = d11;
					} else {
						d8 = d10 + (d11 - d10) * d12;
					}

					d8 -= d9;
					if (j3 > ySize - 6) {
						double d13 = (double) ((float) (j3 - (ySize - 4)) / 5.0F);
						d8 = d8 * (1.0D - d13) + -10.0D * d13;
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

	public void populate(IChunkProvider ichunkprovider, int xCh, int yCh, int zCh) {
		BlockSand.fallInstantly = true;
		int xBlock = xCh * 16;
		int yBlock = yCh * 16;
		int zBlock = zCh * 16;
		BiomeGenBase biomegenbase = this.worldObj.getWorldChunkManager().getBiomeGenAt(xBlock + 16, zBlock + 16);
		this.rand.setSeed(this.worldObj.getRandomSeed());
		long randomL1 = this.rand.nextLong() / 2L * 2L + 1L;
		long randomL2 = this.rand.nextLong() / 2L * 2L + 1L;
		long randomL3 = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed(
				(long) xCh * randomL1 + (long) zCh * randomL2 ^ this.worldObj.getRandomSeed() + (long) yCh * randomL3
						^ this.worldObj.getRandomSeed() * this.worldObj.getRandomSeed());
		double d = 0.25D;
		int k4;
		int treeChance;
		int plantYellowChance;
		int tallGrassChance;
		// if(yCh < 8) {
		// if(this.rand.nextInt(32) == 0) {
		// k4 = xBlock + this.rand.nextInt(16) + 8;
		// treeChance = yBlock + this.rand.nextInt(16) + 8;
		// plantYellowChance = zBlock + this.rand.nextInt(16) + 8;
		// (new WorldGenLakes(Block.waterStill.blockID)).generate(this.worldObj,
		// this.rand, k4, treeChance, plantYellowChance);
		// }

		// k4 = 64 + yCh / 32;
		// if(this.rand.nextInt(k4) == 0) {
		// treeChance = xBlock + this.rand.nextInt(16) + 8;
		// plantYellowChance = yBlock + this.rand.nextInt(16) + 8;
		// tallGrassChance = zBlock + this.rand.nextInt(16) + 8;
		// if(plantYellowChance < 64 || this.rand.nextInt(10) == 0) {
		// (new WorldGenLakes(Block.lavaStill.blockID)).generate(this.worldObj,
		// this.rand, treeChance, plantYellowChance, tallGrassChance);
		// }
		// }
		// }

		if (yCh < 0 && this.rand.nextInt(8) == 0) {
			k4 = xBlock + this.rand.nextInt(16) + 8;
			treeChance = yBlock + this.rand.nextInt(16) + 8;
			plantYellowChance = zBlock + this.rand.nextInt(16) + 8;
			(new WorldGenDeepCavern(0)).generate(this.worldObj, this.rand, k4, treeChance, plantYellowChance);
			if (this.rand.nextInt(4) == 0) {
				(new WorldGenFlowers(Block.mushroomBrown.blockID)).generate(this.worldObj, this.rand, k4, treeChance,
						plantYellowChance);
			}

			if (this.rand.nextInt(4) == 0) {
				(new WorldGenFlowers(Block.mushroomRed.blockID)).generate(this.worldObj, this.rand, k4, treeChance,
						plantYellowChance);
			}
		}

		k4 = xBlock + this.rand.nextInt(16) + 8;
		treeChance = yBlock + this.rand.nextInt(16) + 8;
		plantYellowChance = zBlock + this.rand.nextInt(16) + 8;
		boolean z29 = (new WorldGenDungeons()).generate(this.worldObj, this.rand, k4, treeChance, plantYellowChance);
		int cactusChance;
		if (z29 && yCh > 0 && this.rand.nextInt(1) == 0) {
			cactusChance = this.worldObj.getHeightValue(k4, plantYellowChance) + this.rand.nextInt(4)
					- this.rand.nextInt(4);
			(new WorldGenPumpkin()).generate(this.worldObj, this.rand, k4, cactusChance, plantYellowChance);
		}

		if (yCh >= 2 && yCh <= 8) {
			for (k4 = 0; k4 < 4; ++k4) {
				treeChance = xBlock + this.rand.nextInt(16);
				plantYellowChance = yBlock + this.rand.nextInt(16);
				tallGrassChance = zBlock + this.rand.nextInt(16);
				(new WorldGenClay(32)).generate(this.worldObj, this.rand, treeChance, plantYellowChance,
						tallGrassChance);
			}

			k4 = xBlock + this.rand.nextInt(16);
			treeChance = yBlock + this.rand.nextInt(16);
			plantYellowChance = zBlock + this.rand.nextInt(16);
			(new WorldGenClay(64)).generate(this.worldObj, this.rand, k4, treeChance, plantYellowChance);
		}

		for (k4 = 0; k4 < 5; ++k4) {
			if (this.rand.nextBoolean()) {
				treeChance = xBlock + this.rand.nextInt(16);
				plantYellowChance = yBlock + this.rand.nextInt(16);
				tallGrassChance = zBlock + this.rand.nextInt(16);
				(new WorldGenMinable(Block.dirt.blockID, 32)).generate(this.worldObj, this.rand, treeChance,
						plantYellowChance, tallGrassChance);
			}
		}

		for (k4 = 0; k4 < 5; ++k4) {
			if (this.rand.nextInt(4) == 0) {
				treeChance = xBlock + this.rand.nextInt(16);
				plantYellowChance = yBlock + this.rand.nextInt(16);
				tallGrassChance = zBlock + this.rand.nextInt(16);
				(new WorldGenMinable(Block.gravel.blockID, 32)).generate(this.worldObj, this.rand, treeChance,
						plantYellowChance, tallGrassChance);
			}
		}

		for (k4 = 0; k4 < 5; ++k4) {
			if (this.rand.nextBoolean()) {
				treeChance = xBlock + this.rand.nextInt(16);
				plantYellowChance = yBlock + this.rand.nextInt(16);
				tallGrassChance = zBlock + this.rand.nextInt(16);
				(new WorldGenMinable(Block.oreCoal.blockID, 16)).generate(this.worldObj, this.rand, treeChance,
						plantYellowChance, tallGrassChance);
			}
		}

		if (yCh < 4) {
			for (k4 = 0; k4 < 5; ++k4) {
				treeChance = xBlock + this.rand.nextInt(16);
				plantYellowChance = yBlock + this.rand.nextInt(16);
				tallGrassChance = zBlock + this.rand.nextInt(16);
				(new WorldGenMinable(Block.oreIron.blockID, 8)).generate(this.worldObj, this.rand, treeChance,
						plantYellowChance, tallGrassChance);
			}
		}

		if (yCh < 2) {
			k4 = xBlock + this.rand.nextInt(16);
			treeChance = yBlock + this.rand.nextInt(16);
			plantYellowChance = zBlock + this.rand.nextInt(16);
			(new WorldGenMinable(Block.oreGold.blockID, 8)).generate(this.worldObj, this.rand, k4, treeChance,
					plantYellowChance);
		}

		if (yCh < 1) {
			for (k4 = 0; k4 < 4; ++k4) {
				treeChance = xBlock + this.rand.nextInt(16);
				plantYellowChance = yBlock + this.rand.nextInt(16);
				tallGrassChance = zBlock + this.rand.nextInt(16);
				(new WorldGenMinable(Block.oreRedstone.blockID, 7)).generate(this.worldObj, this.rand, treeChance,
						plantYellowChance, tallGrassChance);
			}
		}

		if (yCh < 1) {
			k4 = xBlock + this.rand.nextInt(16);
			treeChance = yBlock + this.rand.nextInt(16);
			plantYellowChance = zBlock + this.rand.nextInt(16);
			(new WorldGenMinable(Block.oreDiamond.blockID, 7)).generate(this.worldObj, this.rand, k4, treeChance,
					plantYellowChance);
		}

		if (yCh < 1 || yCh == 1 && this.rand.nextBoolean()) {
			k4 = xBlock + this.rand.nextInt(16);
			treeChance = yBlock + this.rand.nextInt(16);
			plantYellowChance = zBlock + this.rand.nextInt(16);
			(new WorldGenMinable(Block.oreLapis.blockID, 6)).generate(this.worldObj, this.rand, k4, treeChance,
					plantYellowChance);
		}

		if (yCh < 8 && this.rand.nextBoolean()) {
			k4 = xBlock + this.rand.nextInt(16);
			treeChance = yBlock + this.rand.nextInt(16);
			plantYellowChance = zBlock + this.rand.nextInt(16);
			(new WorldGenMinable(Block.blockClay.blockID, 3 + this.rand.nextInt(15))).generate(this.worldObj, this.rand,
					k4, treeChance, plantYellowChance);
		}

		if (yCh < 0 && this.rand.nextInt(32) == 0) {
			k4 = xBlock + this.rand.nextInt(16) + 8;
			treeChance = yBlock + this.rand.nextInt(16) + 8;
			plantYellowChance = zBlock + this.rand.nextInt(16) + 8;
			(new WorldGenDeepCavern(Block.lavaMoving.blockID)).generate(this.worldObj, this.rand, k4, treeChance,
					plantYellowChance);
		}

		if (yCh < 1 && this.rand.nextBoolean()) {
			k4 = xBlock + this.rand.nextInt(16);
			treeChance = yBlock + this.rand.nextInt(16);
			plantYellowChance = zBlock + this.rand.nextInt(16);
			(new WorldGenMinable(Block.sandStone.blockID, 16 + this.rand.nextInt(64))).generate(this.worldObj,
					this.rand, k4, treeChance, plantYellowChance);
		}

		d = 0.5D;
		k4 = (int) ((this.mobSpawnerNoise.func_647_a((double) xBlock * d, (double) zBlock * d) / 8.0D
				+ this.rand.nextDouble() * 4.0D + 4.0D) / 3.0D);
		treeChance = 0;
		if (this.rand.nextInt(10) == 0) {
			++treeChance;
		}

		if (biomegenbase == BiomeGenBase.plains) {
			treeChance += k4 + 5;
		} else if (biomegenbase == BiomeGenBase.forest) {
			treeChance += k4 + 5;
		} else if (biomegenbase == BiomeGenBase.forest) {
			treeChance += k4 + 2;
		} else if (biomegenbase == BiomeGenBase.plains) {
			treeChance += k4 + 5;
		} else if (biomegenbase == BiomeGenBase.plains) {
			treeChance -= 20;
		} else if (biomegenbase == BiomeGenBase.plains) {
			treeChance -= 20;
		} else if (biomegenbase == BiomeGenBase.plains) {
			treeChance -= 20;
		}

		int x2;
		for (plantYellowChance = 0; plantYellowChance < treeChance; ++plantYellowChance) {
			tallGrassChance = xBlock + this.rand.nextInt(16) + 8;
			cactusChance = zBlock + this.rand.nextInt(16) + 8;
			x2 = this.worldObj.getHeightValue(tallGrassChance, cactusChance);
			if (x2 >> 4 == yCh) {
				WorldGenerator z2 = biomegenbase.getRandomWorldGenForTrees(this.rand);
				z2.func_420_a(1.0D, 1.0D, 1.0D);
				z2.generate(this.worldObj, this.rand, tallGrassChance, x2, cactusChance);
			}
		}

		byte b33 = 0;
		if (biomegenbase == BiomeGenBase.forest) {
			b33 = 2;
		}

		if (biomegenbase == BiomeGenBase.plains) {
			b33 = 4;
		}

		if (biomegenbase == BiomeGenBase.plains) {
			b33 = 2;
		}

		if (biomegenbase == BiomeGenBase.plains) {
			b33 = 3;
		}

		int i31;
		for (tallGrassChance = 0; tallGrassChance < b33; ++tallGrassChance) {
			if (this.rand.nextInt(8) == 0) {
				cactusChance = xBlock + this.rand.nextInt(16) + 8;
				x2 = yBlock + this.rand.nextInt(16) + 8;
				i31 = zBlock + this.rand.nextInt(16) + 8;
				(new WorldGenFlowers(Block.plantYellow.blockID)).generate(this.worldObj, this.rand, cactusChance, x2,
						i31);
			}
		}

		byte b32 = 0;
		if (biomegenbase == BiomeGenBase.forest) {
			b32 = 2;
		}

		if (biomegenbase == BiomeGenBase.plains) {
			b32 = 10;
		}

		if (biomegenbase == BiomeGenBase.plains) {
			b32 = 2;
		}

		if (biomegenbase == BiomeGenBase.plains) {
			b32 = 1;
		}

		if (biomegenbase == BiomeGenBase.plains) {
			b32 = 10;
		}

		int x3;
		int z3;
		// for(cactusChance = 0; cactusChance < b32; ++cactusChance) {
		// if(this.rand.nextInt(8) == 0) {
		// byte b30 = 1;
		// if(biomegenbase == BiomeGenBase.rainforest && this.rand.nextInt(3) != 0) {
		// b30 = 2;
		// }

		// i31 = xBlock + this.rand.nextInt(16) + 8;
		// x3 = yBlock + this.rand.nextInt(16) + 8;
		// z3 = zBlock + this.rand.nextInt(16) + 8;
		// (new WorldGenTallGrass(Block.tallGrass.blockID, b30)).generate(this.worldObj,
		// this.rand, i31, x3, z3);
		// }
		// }

		b32 = 0;
		if (biomegenbase == BiomeGenBase.plains) {
			b32 = 2;
		}

		for (cactusChance = 0; cactusChance < b32; ++cactusChance) {
			if (this.rand.nextInt(8) == 0) {
				x2 = xBlock + this.rand.nextInt(16) + 8;
				i31 = yBlock + this.rand.nextInt(16) + 8;
				x3 = zBlock + this.rand.nextInt(16) + 8;
				(new WorldGenDeadBush(Block.deadBush.blockID)).generate(this.worldObj, this.rand, x2, i31, x3);
			}
		}

		if (this.rand.nextInt(16) == 0) {
			cactusChance = xBlock + this.rand.nextInt(16) + 8;
			x2 = yBlock + this.rand.nextInt(16) + 8;
			i31 = zBlock + this.rand.nextInt(16) + 8;
			(new WorldGenFlowers(Block.plantRed.blockID)).generate(this.worldObj, this.rand, cactusChance, x2, i31);
		}

		if (this.rand.nextInt(32) == 0) {
			cactusChance = xBlock + this.rand.nextInt(16) + 8;
			x2 = yBlock + this.rand.nextInt(16) + 8;
			i31 = zBlock + this.rand.nextInt(16) + 8;
			(new WorldGenFlowers(Block.mushroomBrown.blockID)).generate(this.worldObj, this.rand, cactusChance, x2,
					i31);
		}

		if (this.rand.nextInt(64) == 0) {
			cactusChance = xBlock + this.rand.nextInt(16) + 8;
			x2 = yBlock + this.rand.nextInt(16) + 8;
			i31 = zBlock + this.rand.nextInt(16) + 8;
			(new WorldGenFlowers(Block.mushroomRed.blockID)).generate(this.worldObj, this.rand, cactusChance, x2, i31);
		}

		for (cactusChance = 0; cactusChance < 5; ++cactusChance) {
			if (this.rand.nextInt(4) == 0) {
				x2 = xBlock + this.rand.nextInt(16) + 8;
				i31 = yBlock + this.rand.nextInt(16) + 8;
				x3 = zBlock + this.rand.nextInt(16) + 8;
				(new WorldGenReed()).generate(this.worldObj, this.rand, x2, i31, x3);
			}
		}

		if (this.rand.nextInt(256) == 0) {
			cactusChance = xBlock + this.rand.nextInt(16) + 8;
			x2 = yBlock + this.rand.nextInt(16) + 8;
			i31 = zBlock + this.rand.nextInt(16) + 8;
			(new WorldGenPumpkin()).generate(this.worldObj, this.rand, cactusChance, x2, i31);
		}

		cactusChance = 0;
		if (biomegenbase == BiomeGenBase.desert) {
			cactusChance += 10;
		}

		for (x2 = 0; x2 < cactusChance; ++x2) {
			if (this.rand.nextInt(8) == 0) {
				i31 = xBlock + this.rand.nextInt(16) + 8;
				x3 = yBlock + this.rand.nextInt(16) + 8;
				z3 = zBlock + this.rand.nextInt(16) + 8;
				(new WorldGenCactus()).generate(this.worldObj, this.rand, i31, x3, z3);
			}
		}

		if (yCh < 0) {
			for (x2 = 0; x2 < 25; ++x2) {
				if (this.rand.nextInt(4) == 0) {
					i31 = xBlock + this.rand.nextInt(16) + 8;
					x3 = yBlock + this.rand.nextInt(16) + 8;
					z3 = zBlock + this.rand.nextInt(16) + 8;
					(new WorldGenLiquids(Block.waterMoving.blockID)).generate(this.worldObj, this.rand, i31, x3, z3);
				}
			}

			for (x2 = 0; x2 < 5; ++x2) {
				if (this.rand.nextBoolean()) {
					i31 = xBlock + this.rand.nextInt(16) + 8;
					x3 = yBlock + this.rand.nextInt(16) + 8;
					z3 = zBlock + this.rand.nextInt(16) + 8;
					(new WorldGenLiquids(Block.lavaMoving.blockID)).generate(this.worldObj, this.rand, i31, x3, z3);
				}
			}
		} else {
			for (x2 = 0; x2 < 50; ++x2) {
				i31 = this.rand.nextInt(this.rand.nextInt(120) + 8);
				if (i31 / 16 == yCh) {
					x3 = xBlock + this.rand.nextInt(16) + 8;
					z3 = zBlock + this.rand.nextInt(16) + 8;
					(new WorldGenLiquids(Block.waterMoving.blockID)).generate(this.worldObj, this.rand, x3, i31, z3);
				}
			}

			for (x2 = 0; x2 < 20; ++x2) {
				i31 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(112) + 8) + 8);
				if (i31 / 16 == yCh) {
					x3 = xBlock + this.rand.nextInt(16) + 8;
					z3 = zBlock + this.rand.nextInt(16) + 8;
					(new WorldGenLiquids(Block.lavaMoving.blockID)).generate(this.worldObj, this.rand, x3, i31, z3);
				}
			}
		}

		this.generatedTemperatures = this.worldObj.getWorldChunkManager().getTemperatures(this.generatedTemperatures,
				xBlock + 8, zBlock + 8, 16, 16);

		for (x2 = xBlock + 8; x2 < xBlock + 8 + 16; ++x2) {
			for (i31 = zBlock + 8; i31 < zBlock + 8 + 16; ++i31) {
				x3 = x2 - (xBlock + 8);
				z3 = i31 - (zBlock + 8);
				int y2 = this.worldObj.findTopSolidBlock(x2, i31);
				if (y2 / 16 == yCh) {
					double d1 = this.generatedTemperatures[x3 * 16 + z3] - (double) (y2 - 64) / 64.0D * 0.3D;
					if (d1 < 0.5D && y2 > 0 && y2 < 128 && this.worldObj.isAirBlock(x2, y2, i31)
							&& this.worldObj.getBlockMaterial(x2, y2 - 1, i31).getIsSolid()
							&& this.worldObj.getBlockMaterial(x2, y2 - 1, i31) != Material.ice) {
						this.worldObj.setBlockWithNotify(x2, y2, i31, Block.snow.blockID);
					}
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
}
