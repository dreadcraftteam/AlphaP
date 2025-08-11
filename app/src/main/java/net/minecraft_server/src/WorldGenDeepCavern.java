package net.minecraft_server.src;

import java.util.Random;

public class WorldGenDeepCavern extends WorldGenerator {
	private byte id;
	private static final int maxXSize = 32;
	private static final int maxZSize = 32;
	private static final int maxYSize = 32;
	private static final int minXSize = 8;
	private static final int minZSize = 8;
	private static final int minYSize = 4;

	public WorldGenDeepCavern(int blockID) {
		this.id = (byte) blockID;
	}

	public boolean generate(World world, Random random, int x, int y, int z) {
		int xSize = random.nextInt(24) + 8;
		byte ySize = 8;
		byte zSize = 16;
		x -= 8;

		for (z -= 8; y > 0 && world.isAirBlock(x, y, z); --y) {
		}

		y -= 4;
		boolean[] aflag = new boolean[xSize * ySize * zSize];
		int r1 = random.nextInt(4) + 4;

		int x1;
		for (x1 = 0; x1 < r1; ++x1) {
			double z1 = random.nextDouble() * 6.0D + 3.0D;
			double blockid = random.nextDouble() * 4.0D + 2.0D;
			double d2 = random.nextDouble() * 6.0D + 3.0D;
			double d3 = random.nextDouble() * (16.0D - z1 - 2.0D) + 1.0D + z1 / 2.0D;
			double d4 = random.nextDouble() * (8.0D - blockid - 4.0D) + 2.0D + blockid / 2.0D;
			double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

			for (int x11 = 1; x11 < xSize - 1; ++x11) {
				for (int z11 = 1; z11 < zSize - 1; ++z11) {
					for (int y11 = 1; y11 < ySize - 1; ++y11) {
						double d6 = ((double) x11 - d3) / (z1 / 2.0D);
						double d7 = ((double) y11 - d4) / (blockid / 2.0D);
						double d8 = ((double) z11 - d5) / (d2 / 2.0D);
						double d9 = d6 * d6 + d7 * d7 + d8 * d8;
						if (d9 < 1.0D) {
							try {
								aflag[(x11 * zSize + z11) * ySize + y11] = true;
							} catch (Exception exception37) {
								boolean noBreak1 = true;
								if (noBreak1) {
									;
								}
							}
						}
					}
				}
			}
		}

		int y1;
		int i39;
		for (x1 = 0; x1 < xSize; ++x1) {
			for (i39 = 0; i39 < zSize; ++i39) {
				for (y1 = 0; y1 < ySize; ++y1) {
					try {
						boolean z40 = !aflag[(x1 * zSize + i39) * ySize + y1]
								&& (x1 < 15 && aflag[((x1 + 1) * zSize + i39) * ySize + y1]
										|| x1 > 0 && aflag[((x1 - 1) * zSize + i39) * ySize + y1]
										|| i39 < 15 && aflag[(x1 * zSize + i39 + 1) * ySize + y1]
										|| i39 > 0 && aflag[(x1 * zSize + (i39 - 1)) * ySize + y1]
										|| y1 < 7 && aflag[(x1 * zSize + i39) * ySize + y1 + 1]
										|| y1 > 0 && aflag[(x1 * zSize + i39) * ySize + y1 - 1]);
						if (z40) {
							Material material42 = world.getBlockMaterial(x + x1, y + y1, z + i39);
							if (y1 >= 4 && material42.getIsLiquid()) {
								return false;
							}

							if (y1 < 4 && !material42.isSolid()
									&& world.getBlockId(x + x1, y + y1, z + i39) != this.id) {
								return false;
							}
						}
					} catch (Exception exception38) {
						boolean noBreak = true;
						if (noBreak) {
							;
						}
					}
				}
			}
		}

		for (x1 = 0; x1 < xSize; ++x1) {
			for (i39 = 0; i39 < zSize; ++i39) {
				for (y1 = 0; y1 < ySize; ++y1) {
					if (aflag[(x1 * zSize + i39) * ySize + y1]) {
						int i41 = world.getBlockId(x + x1, y + y1, z + i39);
						if (this.id == 0 || i41 == Block.stone.blockID || i41 == Block.dirt.blockID
								|| i41 == Block.gravel.blockID || i41 == Block.sand.blockID
								|| i41 == Block.sandStone.blockID || i41 == 0 && i41 != this.id) {
							world.setBlock(x + x1, y + y1, z + i39, this.id);
						}
					}
				}
			}
		}

		return true;
	}
}
