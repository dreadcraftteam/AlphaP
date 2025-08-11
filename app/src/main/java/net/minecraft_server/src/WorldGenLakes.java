package net.minecraft_server.src;

import java.util.Random;

public class WorldGenLakes extends WorldGenerator {
	private int field_15005_a;

	public WorldGenLakes(int i) {
		this.field_15005_a = i;
	}

	public boolean generate(World world, Random random, int i, int j, int k) {
		i -= 8;

		for (k -= 8; world.isAirBlock(i, j, k); --j) {
		}

		j -= 4;
		boolean[] aflag = new boolean[2048];
		int l = random.nextInt(4) + 4;

		int i2;
		for (i2 = 0; i2 < l; ++i2) {
			double i3 = random.nextDouble() * 6.0D + 3.0D;
			double flag1 = random.nextDouble() * 4.0D + 2.0D;
			double md = random.nextDouble() * 6.0D + 3.0D;
			double d3 = random.nextDouble() * (16.0D - i3 - 2.0D) + 1.0D + i3 / 2.0D;
			double d4 = random.nextDouble() * (8.0D - flag1 - 4.0D) + 2.0D + flag1 / 2.0D;
			double d5 = random.nextDouble() * (16.0D - md - 2.0D) + 1.0D + md / 2.0D;

			for (int j4 = 1; j4 < 15; ++j4) {
				for (int k4 = 1; k4 < 15; ++k4) {
					for (int l4 = 1; l4 < 7; ++l4) {
						double d6 = ((double) j4 - d3) / (i3 / 2.0D);
						double d7 = ((double) l4 - d4) / (flag1 / 2.0D);
						double d8 = ((double) k4 - d5) / (md / 2.0D);
						double d9 = d6 * d6 + d7 * d7 + d8 * d8;
						if (d9 < 1.0D) {
							aflag[(j4 * 16 + k4) * 8 + l4] = true;
						}
					}
				}
			}
		}

		int i4;
		int i32;
		boolean z33;
		for (i2 = 0; i2 < 16; ++i2) {
			for (i32 = 0; i32 < 16; ++i32) {
				for (i4 = 0; i4 < 8; ++i4) {
					z33 = !aflag[(i2 * 16 + i32) * 8 + i4] && (i2 < 15 && aflag[((i2 + 1) * 16 + i32) * 8 + i4]
							|| i2 > 0 && aflag[((i2 - 1) * 16 + i32) * 8 + i4]
							|| i32 < 15 && aflag[(i2 * 16 + i32 + 1) * 8 + i4]
							|| i32 > 0 && aflag[(i2 * 16 + (i32 - 1)) * 8 + i4]
							|| i4 < 7 && aflag[(i2 * 16 + i32) * 8 + i4 + 1]
							|| i4 > 0 && aflag[(i2 * 16 + i32) * 8 + (i4 - 1)]);
					if (z33) {
						Material id = world.getBlockMaterial(i + i2, j + i4, k + i32);
						if (i4 >= 4 && id.getIsLiquid()) {
							return false;
						}

						if (i4 < 4 && !id.isSolid()
								&& world.getBlockId(i + i2, j + i4, k + i32) != this.field_15005_a) {
							return false;
						}
					}
				}
			}
		}

		for (i2 = 0; i2 < 16; ++i2) {
			for (i32 = 0; i32 < 16; ++i32) {
				for (i4 = 7; i4 >= 0; --i4) {
					if (aflag[(i2 * 16 + i32) * 8 + i4]) {
						if (this.field_15005_a != Block.lavaStill.blockID) {
							int i34 = world.getBlockId(i + i2, j + i4, k + i32);
							if (i34 != Block.wood.blockID && i34 != Block.leaves.blockID) {
								int i35 = world.getBlockId(i + i2, j + i4 + 1, k + i32);
								if (i35 == Block.wood.blockID) {
									int i36 = world.getBlockMetadata(i + i2, j + i4 + 1, k + i32);
									world.setBlockAndMetadataWithNotify(i + i2, j + i4, k + i32, i35, i36);
									continue;
								}
							}
						}

						world.setBlock(i + i2, j + i4, k + i32, i4 < 4 ? this.field_15005_a : 0);
					}
				}
			}
		}

		for (i2 = 0; i2 < 16; ++i2) {
			for (i32 = 0; i32 < 16; ++i32) {
				for (i4 = 4; i4 < 8; ++i4) {
					if (aflag[(i2 * 16 + i32) * 8 + i4]
							&& world.getBlockId(i + i2, j + i4 - 1, k + i32) == Block.dirt.blockID
							&& world.getSavedLightValue(EnumSkyBlock.Sky, i + i2, j + i4, k + i32) > 0) {
						world.setBlock(i + i2, j + i4 - 1, k + i32, Block.grass.blockID);
					}
				}
			}
		}

		if (Block.blocksList[this.field_15005_a].blockMaterial == Material.lava) {
			for (i2 = 0; i2 < 16; ++i2) {
				for (i32 = 0; i32 < 16; ++i32) {
					for (i4 = 0; i4 < 8; ++i4) {
						z33 = !aflag[(i2 * 16 + i32) * 8 + i4] && (i2 < 15 && aflag[((i2 + 1) * 16 + i32) * 8 + i4]
								|| i2 > 0 && aflag[((i2 - 1) * 16 + i32) * 8 + i4]
								|| i32 < 15 && aflag[(i2 * 16 + i32 + 1) * 8 + i4]
								|| i32 > 0 && aflag[(i2 * 16 + (i32 - 1)) * 8 + i4]
								|| i4 < 7 && aflag[(i2 * 16 + i32) * 8 + i4 + 1]
								|| i4 > 0 && aflag[(i2 * 16 + i32) * 8 + (i4 - 1)]);
						if (z33 && (i4 < 4 || random.nextInt(2) != 0)
								&& world.getBlockMaterial(i + i2, j + i4, k + i32).isSolid()) {
							world.setBlock(i + i2, j + i4, k + i32, Block.stone.blockID);
						}
					}
				}
			}
		}

		return true;
	}
}
