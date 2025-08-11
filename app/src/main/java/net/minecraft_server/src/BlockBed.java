package net.minecraft_server.src;

import java.util.Iterator;
import java.util.Random;

public class BlockBed extends Block {
	public static final int[][] field_22023_a = new int[][] { { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 0 } };

	public BlockBed(int i) {
		super(i, 134, Material.cloth);
		this.setBounds();
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		if (world.multiplayerWorld) {
			return true;
		} else {
			int l = world.getBlockMetadata(i, j, k);
			if (!func_22020_d(l)) {
				int enumstatus = func_22019_c(l);
				i += field_22023_a[enumstatus][0];
				k += field_22023_a[enumstatus][1];
				if (world.getBlockId(i, j, k) != this.blockID) {
					return true;
				}

				l = world.getBlockMetadata(i, j, k);
			}

			if (!world.worldProvider.func_28108_d()) {
				double enumstatus3 = (double) i + 0.5D;
				double entityplayer21 = (double) j + 0.5D;
				double d2 = (double) k + 0.5D;
				world.setBlockWithNotify(i, j, k, 0);
				int j1 = func_22019_c(l);
				i += field_22023_a[j1][0];
				k += field_22023_a[j1][1];
				if (world.getBlockId(i, j, k) == this.blockID) {
					world.setBlockWithNotify(i, j, k, 0);
					enumstatus3 = (enumstatus3 + (double) i + 0.5D) / 2.0D;
					entityplayer21 = (entityplayer21 + (double) j + 0.5D) / 2.0D;
					d2 = (d2 + (double) k + 0.5D) / 2.0D;
				}

				world.newExplosion((Entity) null, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F),
						(double) ((float) k + 0.5F), 5.0F, true);
				return true;
			} else {
				if (func_22018_f(l)) {
					EntityPlayer enumstatus1 = null;
					Iterator iterator = world.playerEntities.iterator();

					while (iterator.hasNext()) {
						EntityPlayer entityplayer2 = (EntityPlayer) iterator.next();
						if (entityplayer2.func_22057_E()) {
							ChunkCoordinates chunkcoordinates = entityplayer2.playerLocation;
							if (chunkcoordinates.posX == i && chunkcoordinates.posY == j
									&& chunkcoordinates.posZ == k) {
								enumstatus1 = entityplayer2;
							}
						}
					}

					if (enumstatus1 != null) {
						entityplayer.func_22061_a("tile.bed.occupied");
						return true;
					}

					func_22022_a(world, i, j, k, false);
				}

				EnumStatus enumstatus2 = entityplayer.goToSleep(i, j, k);
				if (enumstatus2 == EnumStatus.OK) {
					func_22022_a(world, i, j, k, true);
					return true;
				} else {
					if (enumstatus2 == EnumStatus.NOT_POSSIBLE_NOW) {
						entityplayer.func_22061_a("tile.bed.noSleep");
					}

					return true;
				}
			}
		}
	}

	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		if (i == 0) {
			return Block.planks.blockIndexInTexture;
		} else {
			int k = func_22019_c(j);
			int l = ModelBed.field_22155_c[k][i];
			return func_22020_d(j)
					? (l == 2 ? this.blockIndexInTexture + 2 + 16
							: (l != 5 && l != 4 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture + 1 + 16))
					: (l == 3 ? this.blockIndexInTexture - 1 + 16
							: (l != 5 && l != 4 ? this.blockIndexInTexture : this.blockIndexInTexture + 16));
		}
	}

	public boolean isACube() {
		return false;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		this.setBounds();
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		int i1 = world.getBlockMetadata(i, j, k);
		int j1 = func_22019_c(i1);
		if (func_22020_d(i1)) {
			if (world.getBlockId(i - field_22023_a[j1][0], j, k - field_22023_a[j1][1]) != this.blockID) {
				world.setBlockWithNotify(i, j, k, 0);
			}
		} else if (world.getBlockId(i + field_22023_a[j1][0], j, k + field_22023_a[j1][1]) != this.blockID) {
			world.setBlockWithNotify(i, j, k, 0);
			if (!world.multiplayerWorld) {
				this.dropBlockAsItem(world, i, j, k, i1);
			}
		}

	}

	public int idDropped(int i, Random random) {
		return func_22020_d(i) ? 0 : Item.bed.shiftedIndex;
	}

	private void setBounds() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
	}

	public static int func_22019_c(int i) {
		return i & 3;
	}

	public static boolean func_22020_d(int i) {
		return (i & 8) != 0;
	}

	public static boolean func_22018_f(int i) {
		return (i & 4) != 0;
	}

	public static void func_22022_a(World world, int i, int j, int k, boolean flag) {
		int l = world.getBlockMetadata(i, j, k);
		if (flag) {
			l |= 4;
		} else {
			l &= -5;
		}

		world.setBlockMetadataWithNotify(i, j, k, l);
	}

	public static ChunkCoordinates func_22021_g(World world, int i, int j, int k, int l) {
		int i1 = world.getBlockMetadata(i, j, k);
		int j1 = func_22019_c(i1);

		for (int k1 = 0; k1 <= 1; ++k1) {
			int l1 = i - field_22023_a[j1][0] * k1 - 1;
			int i2 = k - field_22023_a[j1][1] * k1 - 1;
			int j2 = l1 + 2;
			int k2 = i2 + 2;

			for (int l2 = l1; l2 <= j2; ++l2) {
				for (int i3 = i2; i3 <= k2; ++i3) {
					if (world.isBlockNormalCube(l2, j - 1, i3) && world.isAirBlock(l2, j, i3)
							&& world.isAirBlock(l2, j + 1, i3)) {
						if (l <= 0) {
							return new ChunkCoordinates(l2, j, i3);
						}

						--l;
					}
				}
			}
		}

		return null;
	}

	public void dropBlockAsItemWithChance(World world, int i, int j, int k, int l, float f) {
		if (!func_22020_d(l)) {
			super.dropBlockAsItemWithChance(world, i, j, k, l, f);
		}

	}

	public int getMobilityFlag() {
		return 1;
	}
}
