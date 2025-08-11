package net.minecraft_server.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class World implements IBlockAccess {
	public boolean scheduledUpdatesAreImmediate = false;
	private List lightingToUpdate = new ArrayList();
	public List loadedEntityList = new ArrayList();
	List unloadedEntityList = new ArrayList();
	private TreeSet scheduledTickTreeSet = new TreeSet();
	private Set scheduledTickSet = new HashSet();
	public List loadedTileEntityList = new ArrayList();
	private List field_20912_E = new ArrayList();
	public List playerEntities = new ArrayList();
	public List lightningEntities = new ArrayList();
	private long field_6159_E = 16777215L;
	public int skylightSubtracted = 0;
	protected int distHashCounter = (new Random()).nextInt();
	protected final int DIST_HASH_MAGIC = 1013904223;
	protected float field_27079_B;
	protected float field_27078_C;
	protected float field_27077_D;
	protected float field_27076_E;
	protected int field_27075_F = 0;
	public int field_27080_i = 0;
	public boolean editingBlocks = false;
	private long lockTimestamp = System.currentTimeMillis();
	protected int autosavePeriod = 40;
	public int difficultySetting;
	public Random rand = new Random();
	public boolean isNewWorld = false;
	public final WorldProvider worldProvider;
	protected List worldAccesses = new ArrayList();
	protected IChunkProvider chunkProvider;
	protected final ISaveHandler worldFile;
	protected WorldInfo worldInfo;
	public boolean worldChunkLoadOverride;
	private boolean allPlayersSleeping;
	public MapStorage field_28105_z;
	private ArrayList field_9207_I = new ArrayList();
	private boolean field_31048_L;
	private int field_4265_J = 0;
	private boolean spawnHostileMobs = true;
	private boolean spawnPeacefulMobs = true;
	static int lightingUpdatesScheduled = 0;
	private Set activeChunkSet = new HashSet();
	private int ambientTickCountdown = this.rand.nextInt(12000);
	private List field_778_L = new ArrayList();
	public boolean multiplayerWorld = false;

	public WorldChunkManager getWorldChunkManager() {
		return this.worldProvider.worldChunkMgr;
	}

	public World(ISaveHandler isavehandler, String s, long l, WorldProvider worldprovider) {
		this.worldFile = isavehandler;
		this.field_28105_z = new MapStorage(isavehandler);
		this.worldInfo = isavehandler.func_22096_c();
		this.isNewWorld = this.worldInfo == null;
		if (worldprovider != null) {
			this.worldProvider = worldprovider;
		} else if (this.worldInfo != null && this.worldInfo.getDimension() == -1) {
			this.worldProvider = WorldProvider.func_4091_a(-1);
		} else {
			this.worldProvider = WorldProvider.func_4091_a(0);
		}

		boolean flag = false;
		if (this.worldInfo == null) {
			this.worldInfo = new WorldInfo(l, s);
			flag = true;
		} else {
			this.worldInfo.setLevelName(s);
		}

		this.worldProvider.registerWorld(this);
		this.chunkProvider = this.getChunkProvider();
		if (flag) {
			this.generateSpawnPoint();
		}

		this.calculateInitialSkylight();
		this.func_27070_x();
	}

	protected IChunkProvider getChunkProvider() {
		IChunkLoader ichunkloader = this.worldFile.func_22092_a(this.worldProvider);
		return new ChunkProvider(this, ichunkloader, this.worldProvider.getChunkProvider());
	}

	protected void generateSpawnPoint() {
		this.worldChunkLoadOverride = true;
		int i = 0;
		byte byte0 = 64;

		int j;
		for (j = 0; !this.worldProvider.canCoordinateBeSpawn(i, j); j += this.rand.nextInt(64)
				- this.rand.nextInt(64)) {
			i += this.rand.nextInt(64) - this.rand.nextInt(64);
		}

		this.worldInfo.setSpawnPosition(i, byte0, j);
		this.worldChunkLoadOverride = false;
	}

	public int getFirstUncoveredBlock(int i, int j) {
		int k;
		for (k = 63; !this.isAirBlock(i, k + 1, j); ++k) {
		}

		return this.getBlockId(i, k, j);
	}

	public void saveWorld(boolean flag, IProgressUpdate iprogressupdate) {
		if (this.chunkProvider.canSave()) {
			if (iprogressupdate != null) {
				iprogressupdate.func_438_a("Saving level");
			}

			this.saveLevel();
			if (iprogressupdate != null) {
				iprogressupdate.displayLoadingString("Saving chunks");
			}

			this.chunkProvider.saveChunks(flag, iprogressupdate);
		}
	}

	private void saveLevel() {
		this.checkSessionLock();
		this.worldFile.saveWorldInfoAndPlayer(this.worldInfo, this.playerEntities);
		this.field_28105_z.func_28176_a();
	}

	public int getBlockId(int i, int j, int k) {
		return i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000
				? this.getChunkFromChunkCoords(i >> 4, j >> 4, k >> 4).getBlockID(i & 15, j & 15, k & 15)
				: 0;
	}

	public boolean isAirBlock(int i, int j, int k) {
		return this.getBlockId(i, j, k) == 0;
	}

	public boolean blockExists(int i, int j, int k) {
		return this.cubeExists(i >> 4, j >> 4, k >> 4);
	}

	public boolean doChunksNearChunkExist(int i, int j, int k, int l) {
		return this.checkChunksExist(i - l, j - l, k - l, i + l, j + l, k + l);
	}

	public boolean checkChunksExist(int x1, int y1, int z1, int x2, int y2, int z2) {
		x1 >>= 4;
		y1 >>= 4;
		z1 >>= 4;
		x2 >>= 4;
		y2 >>= 4;
		z2 >>= 4;
		if (y2 < -2047) {
			return false;
		} else {
			if (y1 < -2047) {
				y1 = -2047;
			} else if (y1 >= 2047) {
				y1 = 2046;
			}

			if (y2 >= 2047) {
				y2 = 2046;
			}

			for (int x3 = x1; x3 <= x2; ++x3) {
				for (int z3 = z1; z3 <= z2; ++z3) {
					for (int y3 = y1; y3 <= y2; ++y3) {
						if (!this.cubeExists(x3, y3, z3)) {
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	public boolean chunkExists(int i, int j) {
		return this.chunkProvider.chunkExists(i, j);
	}

	public boolean cubeExists(int i, int y, int j) {
		return this.chunkProvider.cubeExists(i, y, j);
	}

	public Chunk getChunkFromBlockCoords(int i, int j) {
		return this.getChunkFromChunkCoords(i >> 4, j >> 4);
	}

	public Chunk getChunkFromChunkCoords(int i, int j) {
		return this.chunkProvider.provideChunk(i, j);
	}

	public ChunkCube getChunkFromBlockCoords(int i, int y, int j) {
		return this.getChunkFromChunkCoords(i >> 4, y >> 4, j >> 4);
	}

	public ChunkCube getChunkFromChunkCoords(int i, int y, int j) {
		return this.chunkProvider.provideCube(i, y, j);
	}

	public boolean setBlockAndMetadata(int i, int j, int k, int l, int i1) {
		if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
			ChunkCube cube = this.getChunkFromChunkCoords(i >> 4, j >> 4, k >> 4);
			return cube.setBlockIDWithMetadata(i & 15, j & 15, k & 15, l, i1);
		} else {
			return false;
		}
	}

	public boolean setBlock(int i, int j, int k, int l) {
		if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
			if (j < -32752) {
				return false;
			} else if (j >= 32752) {
				return false;
			} else {
				ChunkCube cube = this.getChunkFromChunkCoords(i >> 4, j >> 4, k >> 4);
				return cube.setBlockID(i & 15, j & 15, k & 15, l);
			}
		} else {
			return false;
		}
	}

	public Material getBlockMaterial(int i, int j, int k) {
		int l = this.getBlockId(i, j, k);
		return l == 0 ? Material.air : Block.blocksList[l].blockMaterial;
	}

	public int getBlockMetadata(int i, int j, int k) {
		if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
			ChunkCube cube = this.getChunkFromChunkCoords(i >> 4, j >> 4, k >> 4);
			return cube.getBlockMetadata(i & 15, j & 15, k & 15);
		} else {
			return 0;
		}
	}

	public void setBlockMetadataWithNotify(int i, int j, int k, int l) {
		if (this.setBlockMetadata(i, j, k, l)) {
			int i1 = this.getBlockId(i, j, k);
			if (Block.requiresSelfNotify[i1 & 255]) {
				this.notifyBlockChange(i, j, k, i1);
			} else {
				this.notifyBlocksOfNeighborChange(i, j, k, i1);
			}
		}

	}

	public boolean setBlockMetadata(int i, int j, int k, int l) {
		if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
			ChunkCube cube = this.getChunkFromChunkCoords(i >> 4, j >> 4, k >> 4);
			cube.setBlockMetadata(i & 15, j & 15, k & 15, l);
			return true;
		} else {
			return false;
		}
	}

	public boolean setBlockWithNotify(int i, int j, int k, int l) {
		if (this.setBlock(i, j, k, l)) {
			this.notifyBlockChange(i, j, k, l);
			return true;
		} else {
			return false;
		}
	}

	public boolean setBlockAndMetadataWithNotify(int i, int j, int k, int l, int i1) {
		if (this.setBlockAndMetadata(i, j, k, l, i1)) {
			this.notifyBlockChange(i, j, k, l);
			return true;
		} else {
			return false;
		}
	}

	public void markBlockNeedsUpdate(int i, int j, int k) {
		for (int l = 0; l < this.worldAccesses.size(); ++l) {
			((IWorldAccess) this.worldAccesses.get(l)).markBlockNeedsUpdate(i, j, k);
		}

	}

	protected void notifyBlockChange(int i, int j, int k, int l) {
		this.markBlockNeedsUpdate(i, j, k);
		this.notifyBlocksOfNeighborChange(i, j, k, l);
	}

	public void markBlocksDirtyVertical(int i, int j, int k, int l) {
		if (k > l) {
			int i1 = l;
			l = k;
			k = i1;
		}

		this.markBlocksDirty(i, k, j, i, l, j);
	}

	public void markBlockAsNeedsUpdate(int i, int j, int k) {
		for (int l = 0; l < this.worldAccesses.size(); ++l) {
			((IWorldAccess) this.worldAccesses.get(l)).markBlockRangeNeedsUpdate(i, j, k, i, j, k);
		}

	}

	public void markBlocksDirty(int i, int j, int k, int l, int i1, int j1) {
		for (int k1 = 0; k1 < this.worldAccesses.size(); ++k1) {
			((IWorldAccess) this.worldAccesses.get(k1)).markBlockRangeNeedsUpdate(i, j, k, l, i1, j1);
		}

	}

	public void notifyBlocksOfNeighborChange(int i, int j, int k, int l) {
		this.notifyBlockOfNeighborChange(i - 1, j, k, l);
		this.notifyBlockOfNeighborChange(i + 1, j, k, l);
		this.notifyBlockOfNeighborChange(i, j - 1, k, l);
		this.notifyBlockOfNeighborChange(i, j + 1, k, l);
		this.notifyBlockOfNeighborChange(i, j, k - 1, l);
		this.notifyBlockOfNeighborChange(i, j, k + 1, l);
	}

	private void notifyBlockOfNeighborChange(int i, int j, int k, int l) {
		if (!this.editingBlocks && !this.multiplayerWorld) {
			Block block = Block.blocksList[this.getBlockId(i, j, k)];
			if (block != null) {
				block.onNeighborBlockChange(this, i, j, k, l);
			}

		}
	}

	public boolean canBlockSeeTheSky(int i, int j, int k) {
		return this.getChunkFromChunkCoords(i >> 4, k >> 4).canBlockSeeTheSky(i & 15, j, k & 15);
	}

	public int getBlockLightValueNoChecks(int i, int j, int k) {
		return this.getChunkFromChunkCoords(i >> 4, j >> 4, k >> 4).getBlockLightValue(i & 15, j & 15, k & 15, 0);
	}

	public int getBlockLightValue(int i, int j, int k) {
		return this.getBlockLightValue_do(i, j, k, true);
	}

	public int getBlockLightValue_do(int i, int j, int k, boolean flag) {
		if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
			if (flag) {
				int l = this.getBlockId(i, j, k);
				if (l == Block.stairSingle.blockID || l == Block.tilledField.blockID
						|| l == Block.stairCompactCobblestone.blockID || l == Block.stairCompactPlanks.blockID) {
					int i1 = this.getBlockLightValue_do(i, j + 1, k, false);
					int j1 = this.getBlockLightValue_do(i + 1, j, k, false);
					int k1 = this.getBlockLightValue_do(i - 1, j, k, false);
					int l1 = this.getBlockLightValue_do(i, j, k + 1, false);
					int i2 = this.getBlockLightValue_do(i, j, k - 1, false);
					if (j1 > i1) {
						i1 = j1;
					}

					if (k1 > i1) {
						i1 = k1;
					}

					if (l1 > i1) {
						i1 = l1;
					}

					if (i2 > i1) {
						i1 = i2;
					}

					return i1;
				}
			}

			if (j < -32752) {
				return 0;
			} else {
				if (j >= 32752) {
					j = 15;
				}

				return this.getChunkFromChunkCoords(i >> 4, j >> 4, k >> 4).getBlockLightValue(i & 15, j & 15, k & 15,
						this.skylightSubtracted);
			}
		} else {
			return 15;
		}
	}

	public boolean canExistingBlockSeeTheSky(int i, int j, int k) {
		if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
			if (j < -32752) {
				return false;
			} else if (j >= 32752) {
				return true;
			} else if (!this.cubeExists(i >> 4, j >> 4, k >> 4)) {
				return false;
			} else {
				Chunk chunk = this.getChunkFromChunkCoords(i >> 4, k >> 4);
				i &= 15;
				k &= 15;
				return chunk.canBlockSeeTheSky(i, j, k);
			}
		} else {
			return false;
		}
	}

	public int getHeightValue(int i, int j) {
		if (i >= -32000000 && j >= -32000000 && i < 32000000 && j <= 32000000) {
			if (!this.chunkExists(i >> 4, j >> 4)) {
				return 0;
			} else {
				Chunk chunk = this.getChunkFromChunkCoords(i >> 4, j >> 4);
				return chunk.getHeightValue(i & 15, j & 15);
			}
		} else {
			return 0;
		}
	}

	public void neighborLightPropagationChanged(EnumSkyBlock enumskyblock, int i, int j, int k, int l) {
		if (!this.worldProvider.hasNoSky || enumskyblock != EnumSkyBlock.Sky) {
			if (this.blockExists(i, j, k)) {
				if (enumskyblock == EnumSkyBlock.Sky) {
					if (this.canExistingBlockSeeTheSky(i, j, k)) {
						l = 15;
					}
				} else if (enumskyblock == EnumSkyBlock.Block) {
					int i1 = this.getBlockId(i, j, k);
					if (Block.lightValue[i1] > l) {
						l = Block.lightValue[i1];
					}
				}

				if (this.getSavedLightValue(enumskyblock, i, j, k) != l) {
					this.scheduleLightingUpdate(enumskyblock, i, j, k, i, j, k);
				}

			}
		}
	}

	public int getSavedLightValue(EnumSkyBlock enumskyblock, int i, int j, int k) {
		if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
			int l = i >> 4;
			int yChunk = j >> 4;
			int i1 = k >> 4;
			if (!this.cubeExists(l, yChunk, i1)) {
				return 0;
			} else {
				ChunkCube cube = this.getChunkFromChunkCoords(l, yChunk, i1);
				return cube.getSavedLightValue(enumskyblock, i & 15, j & 15, k & 15);
			}
		} else {
			return enumskyblock.field_984_c;
		}
	}

	public void setLightValue(EnumSkyBlock enumskyblock, int i, int j, int k, int l) {
		if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
			if (this.cubeExists(i >> 4, j >> 4, k >> 4)) {
				ChunkCube cube = this.getChunkFromChunkCoords(i >> 4, j >> 4, k >> 4);
				cube.setLightValue(enumskyblock, i & 15, j & 15, k & 15, l);

				for (int i1 = 0; i1 < this.worldAccesses.size(); ++i1) {
					((IWorldAccess) this.worldAccesses.get(i1)).markBlockNeedsUpdate(i, j, k);
				}

			}
		}
	}

	public float getLightBrightness(int i, int j, int k) {
		return this.worldProvider.lightBrightnessTable[this.getBlockLightValue(i, j, k)];
	}

	public boolean isDaytime() {
		return this.skylightSubtracted < 4;
	}

	public MovingObjectPosition rayTraceBlocks(Vec3D vec3d, Vec3D vec3d1) {
		return this.func_28099_a(vec3d, vec3d1, false, false);
	}

	public MovingObjectPosition rayTraceBlocks_do(Vec3D vec3d, Vec3D vec3d1, boolean flag) {
		return this.func_28099_a(vec3d, vec3d1, flag, false);
	}

	public MovingObjectPosition func_28099_a(Vec3D vec3d, Vec3D vec3d1, boolean flag, boolean flag1) {
		if (!Double.isNaN(vec3d.xCoord) && !Double.isNaN(vec3d.yCoord) && !Double.isNaN(vec3d.zCoord)) {
			if (!Double.isNaN(vec3d1.xCoord) && !Double.isNaN(vec3d1.yCoord) && !Double.isNaN(vec3d1.zCoord)) {
				int i = MathHelper.floor_double(vec3d1.xCoord);
				int j = MathHelper.floor_double(vec3d1.yCoord);
				int k = MathHelper.floor_double(vec3d1.zCoord);
				int l = MathHelper.floor_double(vec3d.xCoord);
				int i1 = MathHelper.floor_double(vec3d.yCoord);
				int j1 = MathHelper.floor_double(vec3d.zCoord);
				int k1 = this.getBlockId(l, i1, j1);
				int i2 = this.getBlockMetadata(l, i1, j1);
				Block block = Block.blocksList[k1];
				if ((!flag1 || block == null || block.getCollisionBoundingBoxFromPool(this, l, i1, j1) != null)
						&& k1 > 0 && block.canCollideCheck(i2, flag)) {
					MovingObjectPosition l1 = block.collisionRayTrace(this, l, i1, j1, vec3d, vec3d1);
					if (l1 != null) {
						return l1;
					}
				}

				int i42 = 200;

				while (i42-- >= 0) {
					if (Double.isNaN(vec3d.xCoord) || Double.isNaN(vec3d.yCoord) || Double.isNaN(vec3d.zCoord)) {
						return null;
					}

					if (l == i && i1 == j && j1 == k) {
						return null;
					}

					boolean flag2 = true;
					boolean flag3 = true;
					boolean flag4 = true;
					double d = 999.0D;
					double d1 = 999.0D;
					double d2 = 999.0D;
					if (i > l) {
						d = (double) l + 1.0D;
					} else if (i < l) {
						d = (double) l + 0.0D;
					} else {
						flag2 = false;
					}

					if (j > i1) {
						d1 = (double) i1 + 1.0D;
					} else if (j < i1) {
						d1 = (double) i1 + 0.0D;
					} else {
						flag3 = false;
					}

					if (k > j1) {
						d2 = (double) j1 + 1.0D;
					} else if (k < j1) {
						d2 = (double) j1 + 0.0D;
					} else {
						flag4 = false;
					}

					double d3 = 999.0D;
					double d4 = 999.0D;
					double d5 = 999.0D;
					double d6 = vec3d1.xCoord - vec3d.xCoord;
					double d7 = vec3d1.yCoord - vec3d.yCoord;
					double d8 = vec3d1.zCoord - vec3d.zCoord;
					if (flag2) {
						d3 = (d - vec3d.xCoord) / d6;
					}

					if (flag3) {
						d4 = (d1 - vec3d.yCoord) / d7;
					}

					if (flag4) {
						d5 = (d2 - vec3d.zCoord) / d8;
					}

					boolean byte0 = false;
					byte b43;
					if (d3 < d4 && d3 < d5) {
						if (i > l) {
							b43 = 4;
						} else {
							b43 = 5;
						}

						vec3d.xCoord = d;
						vec3d.yCoord += d7 * d3;
						vec3d.zCoord += d8 * d3;
					} else if (d4 < d5) {
						if (j > i1) {
							b43 = 0;
						} else {
							b43 = 1;
						}

						vec3d.xCoord += d6 * d4;
						vec3d.yCoord = d1;
						vec3d.zCoord += d8 * d4;
					} else {
						if (k > j1) {
							b43 = 2;
						} else {
							b43 = 3;
						}

						vec3d.xCoord += d6 * d5;
						vec3d.yCoord += d7 * d5;
						vec3d.zCoord = d2;
					}

					Vec3D vec3d2 = Vec3D.createVector(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord);
					l = (int) (vec3d2.xCoord = (double) MathHelper.floor_double(vec3d.xCoord));
					if (b43 == 5) {
						--l;
						++vec3d2.xCoord;
					}

					i1 = (int) (vec3d2.yCoord = (double) MathHelper.floor_double(vec3d.yCoord));
					if (b43 == 1) {
						--i1;
						++vec3d2.yCoord;
					}

					j1 = (int) (vec3d2.zCoord = (double) MathHelper.floor_double(vec3d.zCoord));
					if (b43 == 3) {
						--j1;
						++vec3d2.zCoord;
					}

					int j2 = this.getBlockId(l, i1, j1);
					int k2 = this.getBlockMetadata(l, i1, j1);
					Block block1 = Block.blocksList[j2];
					if ((!flag1 || block1 == null || block1.getCollisionBoundingBoxFromPool(this, l, i1, j1) != null)
							&& j2 > 0 && block1.canCollideCheck(k2, flag)) {
						MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(this, l, i1, j1, vec3d,
								vec3d1);
						if (movingobjectposition1 != null) {
							return movingobjectposition1;
						}
					}
				}

				return null;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public void playSoundAtEntity(Entity entity, String s, float f, float f1) {
		for (int i = 0; i < this.worldAccesses.size(); ++i) {
			((IWorldAccess) this.worldAccesses.get(i)).playSound(s, entity.posX, entity.posY - (double) entity.yOffset,
					entity.posZ, f, f1);
		}

	}

	public void playSoundEffect(double d, double d1, double d2, String s, float f, float f1) {
		for (int i = 0; i < this.worldAccesses.size(); ++i) {
			((IWorldAccess) this.worldAccesses.get(i)).playSound(s, d, d1, d2, f, f1);
		}

	}

	public void playRecord(String s, int i, int j, int k) {
		for (int l = 0; l < this.worldAccesses.size(); ++l) {
			((IWorldAccess) this.worldAccesses.get(l)).playRecord(s, i, j, k);
		}

	}

	public void spawnParticle(String s, double d, double d1, double d2, double d3, double d4, double d5) {
		for (int i = 0; i < this.worldAccesses.size(); ++i) {
			((IWorldAccess) this.worldAccesses.get(i)).spawnParticle(s, d, d1, d2, d3, d4, d5);
		}

	}

	public boolean addLightningBolt(Entity entity) {
		this.lightningEntities.add(entity);
		return true;
	}

	public boolean entityJoinedWorld(Entity entity) {
		int x = MathHelper.floor_double(entity.posX / 16.0D);
		int y = MathHelper.floor_double(entity.posY / 16.0D);
		int z = MathHelper.floor_double(entity.posZ / 16.0D);
		boolean flag = false;
		if (entity instanceof EntityPlayer) {
			flag = true;
		}

		if (!flag && !this.cubeExists(x, y, z)) {
			return false;
		} else {
			if (entity instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entity;
				this.playerEntities.add(entityplayer);
				this.updateAllPlayersSleepingFlag();
			}

			this.getChunkFromChunkCoords(x, y, z).addEntity(entity);
			this.loadedEntityList.add(entity);
			this.obtainEntitySkin(entity);
			return true;
		}
	}

	protected void obtainEntitySkin(Entity entity) {
		for (int i = 0; i < this.worldAccesses.size(); ++i) {
			((IWorldAccess) this.worldAccesses.get(i)).obtainEntitySkin(entity);
		}

	}

	protected void releaseEntitySkin(Entity entity) {
		for (int i = 0; i < this.worldAccesses.size(); ++i) {
			((IWorldAccess) this.worldAccesses.get(i)).releaseEntitySkin(entity);
		}

	}

	public void removePlayerForLogoff(Entity entity) {
		if (entity.riddenByEntity != null) {
			entity.riddenByEntity.mountEntity((Entity) null);
		}

		if (entity.ridingEntity != null) {
			entity.mountEntity((Entity) null);
		}

		entity.setEntityDead();
		if (entity instanceof EntityPlayer) {
			this.playerEntities.remove((EntityPlayer) entity);
			this.updateAllPlayersSleepingFlag();
		}

	}

	public void removePlayer(Entity entity) {
		entity.setEntityDead();
		if (entity instanceof EntityPlayer) {
			this.playerEntities.remove((EntityPlayer) entity);
			this.updateAllPlayersSleepingFlag();
		}

		int i = entity.chunkCoordX;
		int j = entity.chunkCoordZ;
		if (entity.addedToChunk && this.chunkExists(i, j)) {
			this.getChunkFromChunkCoords(i, j).removeEntity(entity);
		}

		this.loadedEntityList.remove(entity);
		this.releaseEntitySkin(entity);
	}

	public void addWorldAccess(IWorldAccess iworldaccess) {
		this.worldAccesses.add(iworldaccess);
	}

	public List getCollidingBoundingBoxes(Entity entity, AxisAlignedBB axisalignedbb) {
		this.field_9207_I.clear();
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int yAvg = k + l >> 1;
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);

		for (int d = i; d < j; ++d) {
			for (int l1 = i1; l1 < j1; ++l1) {
				if (this.blockExists(d, yAvg, l1)) {
					for (int list = k - 1; list < l; ++list) {
						Block j2 = Block.blocksList[this.getBlockId(d, list, l1)];
						if (j2 != null) {
							j2.getCollidingBoundingBoxes(this, d, list, l1, axisalignedbb, this.field_9207_I);
						}
					}
				}
			}
		}

		double d15 = 0.25D;
		List list16 = this.getEntitiesWithinAABBExcludingEntity(entity, axisalignedbb.expand(d15, d15, d15));

		for (int i17 = 0; i17 < list16.size(); ++i17) {
			AxisAlignedBB axisalignedbb1 = ((Entity) list16.get(i17)).getBoundingBox();
			if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(axisalignedbb)) {
				this.field_9207_I.add(axisalignedbb1);
			}

			axisalignedbb1 = entity.func_89_d((Entity) list16.get(i17));
			if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(axisalignedbb)) {
				this.field_9207_I.add(axisalignedbb1);
			}
		}

		return this.field_9207_I;
	}

	public int calculateSkylightSubtracted(float f) {
		float f1 = this.getCelestialAngle(f);
		float f2 = 1.0F - (MathHelper.cos(f1 * 3.141593F * 2.0F) * 2.0F + 0.5F);
		if (f2 < 0.0F) {
			f2 = 0.0F;
		}

		if (f2 > 1.0F) {
			f2 = 1.0F;
		}

		f2 = 1.0F - f2;
		f2 = (float) ((double) f2 * (1.0D - (double) (this.func_27074_d(f) * 5.0F) / 16.0D));
		f2 = (float) ((double) f2 * (1.0D - (double) (this.func_27065_c(f) * 5.0F) / 16.0D));
		f2 = 1.0F - f2;
		return (int) (f2 * 11.0F);
	}

	public float getCelestialAngle(float f) {
		return this.worldProvider.calculateCelestialAngle(this.worldInfo.getWorldTime(), f);
	}

	public int getTopSolidOrLiquidBlock(int i, int j) {
		Chunk chunk = this.getChunkFromBlockCoords(i, j);
		int k = 127;
		i &= 15;

		for (j &= 15; k > 0; --k) {
			int l = chunk.getBlockID(i, k, j);
			Material material = l != 0 ? Block.blocksList[l].blockMaterial : Material.air;
			if (material.getIsSolid() || material.getIsLiquid()) {
				return k + 1;
			}
		}

		return -1;
	}

	public int findTopSolidBlock(int i, int j) {
		Chunk chunk = this.getChunkFromBlockCoords(i, j);
		i &= 15;
		j &= 15;

		for (int k = chunk.h[i << 4 | j] + 64; k > 0; --k) {
			int l = chunk.getBlockID(i, k, j);
			if (l != 0 && Block.blocksList[l].blockMaterial.getIsSolid()) {
				return k + 1;
			}
		}

		return -1;
	}

	public void scheduleUpdateTick(int i, int j, int k, int l, int i1) {
		NextTickListEntry nextticklistentry = new NextTickListEntry(i, j, k, l);
		byte byte0 = 8;
		if (this.scheduledUpdatesAreImmediate) {
			if (this.checkChunksExist(nextticklistentry.xCoord - byte0, nextticklistentry.yCoord - byte0,
					nextticklistentry.zCoord - byte0, nextticklistentry.xCoord + byte0,
					nextticklistentry.yCoord + byte0, nextticklistentry.zCoord + byte0)) {
				int j1 = this.getBlockId(nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord);
				if (j1 == nextticklistentry.blockID && j1 > 0) {
					Block.blocksList[j1].updateTick(this, nextticklistentry.xCoord, nextticklistentry.yCoord,
							nextticklistentry.zCoord, this.rand);
				}
			}

		} else {
			if (this.checkChunksExist(i - byte0, j - byte0, k - byte0, i + byte0, j + byte0, k + byte0)) {
				if (l > 0) {
					nextticklistentry.setScheduledTime((long) i1 + this.worldInfo.getWorldTime());
				}

				if (!this.scheduledTickSet.contains(nextticklistentry)) {
					this.scheduledTickSet.add(nextticklistentry);
					this.scheduledTickTreeSet.add(nextticklistentry);
				}
			}

		}
	}

	public void updateEntities() {
		int iterator;
		Entity iterator1;
		for (iterator = 0; iterator < this.lightningEntities.size(); ++iterator) {
			iterator1 = (Entity) this.lightningEntities.get(iterator);
			iterator1.onUpdate();
			if (iterator1.isDead) {
				this.lightningEntities.remove(iterator--);
			}
		}

		this.loadedEntityList.removeAll(this.unloadedEntityList);

		int tileentity1;
		int chunk1;
		for (iterator = 0; iterator < this.unloadedEntityList.size(); ++iterator) {
			iterator1 = (Entity) this.unloadedEntityList.get(iterator);
			tileentity1 = iterator1.chunkCoordX;
			chunk1 = iterator1.chunkCoordZ;
			if (iterator1.addedToChunk && this.chunkExists(tileentity1, chunk1)) {
				this.getChunkFromChunkCoords(tileentity1, chunk1).removeEntity(iterator1);
			}
		}

		for (iterator = 0; iterator < this.unloadedEntityList.size(); ++iterator) {
			this.releaseEntitySkin((Entity) this.unloadedEntityList.get(iterator));
		}

		this.unloadedEntityList.clear();

		for (iterator = 0; iterator < this.loadedEntityList.size(); ++iterator) {
			iterator1 = (Entity) this.loadedEntityList.get(iterator);
			if (iterator1.ridingEntity != null) {
				if (!iterator1.ridingEntity.isDead && iterator1.ridingEntity.riddenByEntity == iterator1) {
					continue;
				}

				iterator1.ridingEntity.riddenByEntity = null;
				iterator1.ridingEntity = null;
			}

			if (!iterator1.isDead) {
				this.updateEntity(iterator1);
			}

			if (iterator1.isDead) {
				tileentity1 = iterator1.chunkCoordX;
				chunk1 = iterator1.chunkCoordZ;
				if (iterator1.addedToChunk && this.chunkExists(tileentity1, chunk1)) {
					this.getChunkFromChunkCoords(tileentity1, chunk1).removeEntity(iterator1);
				}

				this.loadedEntityList.remove(iterator--);
				this.releaseEntitySkin(iterator1);
			}
		}

		this.field_31048_L = true;
		Iterator iterator10 = this.loadedTileEntityList.iterator();

		while (iterator10.hasNext()) {
			TileEntity tileEntity5 = (TileEntity) iterator10.next();
			if (!tileEntity5.isInvalid()) {
				tileEntity5.updateEntity();
			}

			if (tileEntity5.isInvalid()) {
				iterator10.remove();
				Chunk chunk7 = this.getChunkFromChunkCoords(tileEntity5.xCoord >> 4, tileEntity5.zCoord >> 4);
				if (chunk7 != null) {
					chunk7.removeChunkBlockTileEntity(tileEntity5.xCoord & 15, tileEntity5.yCoord,
							tileEntity5.zCoord & 15);
				}
			}
		}

		this.field_31048_L = false;
		if (!this.field_20912_E.isEmpty()) {
			Iterator iterator6 = this.field_20912_E.iterator();

			while (iterator6.hasNext()) {
				TileEntity tileEntity8 = (TileEntity) iterator6.next();
				if (!tileEntity8.isInvalid()) {
					if (!this.loadedTileEntityList.contains(tileEntity8)) {
						this.loadedTileEntityList.add(tileEntity8);
					}

					Chunk chunk9 = this.getChunkFromChunkCoords(tileEntity8.xCoord >> 4, tileEntity8.zCoord >> 4);
					if (chunk9 != null) {
						chunk9.setChunkBlockTileEntity(tileEntity8.xCoord & 15, tileEntity8.yCoord,
								tileEntity8.zCoord & 15, tileEntity8);
					}

					this.markBlockNeedsUpdate(tileEntity8.xCoord, tileEntity8.yCoord, tileEntity8.zCoord);
				}
			}

			this.field_20912_E.clear();
		}

	}

	public void func_31047_a(Collection collection) {
		if (this.field_31048_L) {
			this.field_20912_E.addAll(collection);
		} else {
			this.loadedTileEntityList.addAll(collection);
		}

	}

	public void updateEntity(Entity entity) {
		this.updateEntityWithOptionalForce(entity, true);
	}

	public void updateEntityWithOptionalForce(Entity entity, boolean flag) {
		int i = MathHelper.floor_double(entity.posX);
		int y = MathHelper.floor_double(entity.posY);
		int j = MathHelper.floor_double(entity.posZ);
		byte range = 32;
		if (!flag || this.checkChunksExist(i - range, y - range, j - range, i + range, y + range, j + range)) {
			entity.lastTickPosX = entity.posX;
			entity.lastTickPosY = entity.posY;
			entity.lastTickPosZ = entity.posZ;
			entity.prevRotationYaw = entity.rotationYaw;
			entity.prevRotationPitch = entity.rotationPitch;
			if (flag && entity.addedToChunk) {
				if (entity.ridingEntity != null) {
					entity.updateRidden();
				} else {
					entity.onUpdate();
				}
			}

			if (Double.isNaN(entity.posX) || Double.isInfinite(entity.posX)) {
				entity.posX = entity.lastTickPosX;
			}

			if (Double.isNaN(entity.posY) || Double.isInfinite(entity.posY)) {
				entity.posY = entity.lastTickPosY;
			}

			if (Double.isNaN(entity.posZ) || Double.isInfinite(entity.posZ)) {
				entity.posZ = entity.lastTickPosZ;
			}

			if (Double.isNaN((double) entity.rotationPitch) || Double.isInfinite((double) entity.rotationPitch)) {
				entity.rotationPitch = entity.prevRotationPitch;
			}

			if (Double.isNaN((double) entity.rotationYaw) || Double.isInfinite((double) entity.rotationYaw)) {
				entity.rotationYaw = entity.prevRotationYaw;
			}

			int xCh = MathHelper.floor_double(entity.posX / 16.0D);
			int yCh = MathHelper.floor_double(entity.posY / 16.0D);
			int zCh = MathHelper.floor_double(entity.posZ / 16.0D);
			if (yCh < -2047) {
				yCh = -2047;
			} else if (yCh >= 2047) {
				yCh = 2046;
			}

			if (!entity.addedToChunk || entity.chunkCoordX != xCh || entity.chunkCoordY != yCh
					|| entity.chunkCoordZ != zCh) {
				if (entity.addedToChunk
						&& this.cubeExists(entity.chunkCoordX, entity.chunkCoordY, entity.chunkCoordZ)) {
					this.getChunkFromChunkCoords(entity.chunkCoordX, entity.chunkCoordY, entity.chunkCoordZ)
							.removeEntity(entity);
				}

				if (this.cubeExists(xCh, yCh, zCh)) {
					entity.addedToChunk = true;
					this.getChunkFromChunkCoords(xCh, yCh, zCh).addEntity(entity);
				} else {
					entity.addedToChunk = false;
				}
			}

			if (flag && entity.addedToChunk && entity.riddenByEntity != null) {
				if (!entity.riddenByEntity.isDead && entity.riddenByEntity.ridingEntity == entity) {
					this.updateEntity(entity.riddenByEntity);
				} else {
					entity.riddenByEntity.ridingEntity = null;
					entity.riddenByEntity = null;
				}
			}

		}
	}

	public boolean checkIfAABBIsClear(AxisAlignedBB axisalignedbb) {
		List list = this.getEntitiesWithinAABBExcludingEntity((Entity) null, axisalignedbb);

		for (int i = 0; i < list.size(); ++i) {
			Entity entity = (Entity) list.get(i);
			if (!entity.isDead && entity.preventEntitySpawning) {
				return false;
			}
		}

		return true;
	}

	public boolean func_27069_b(AxisAlignedBB axisalignedbb) {
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);
		if (axisalignedbb.minX < 0.0D) {
			--i;
		}

		if (axisalignedbb.minY < 0.0D) {
			--k;
		}

		if (axisalignedbb.minZ < 0.0D) {
			--i1;
		}

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = k; l1 < l; ++l1) {
				for (int i2 = i1; i2 < j1; ++i2) {
					Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];
					if (block != null) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean getIsAnyLiquid(AxisAlignedBB axisalignedbb) {
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);
		if (axisalignedbb.minX < 0.0D) {
			--i;
		}

		if (axisalignedbb.minZ < 0.0D) {
			--i1;
		}

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = k; l1 < l; ++l1) {
				for (int i2 = i1; i2 < j1; ++i2) {
					Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];
					if (block != null && block.blockMaterial.getIsLiquid()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isBoundingBoxBurning(AxisAlignedBB axisalignedbb) {
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);
		if (this.checkChunksExist(i, k, i1, j, l, j1)) {
			for (int k1 = i; k1 < j; ++k1) {
				for (int l1 = k; l1 < l; ++l1) {
					for (int i2 = i1; i2 < j1; ++i2) {
						int j2 = this.getBlockId(k1, l1, i2);
						if (j2 == Block.fire.blockID || j2 == Block.lavaMoving.blockID
								|| j2 == Block.lavaStill.blockID) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean handleMaterialAcceleration(AxisAlignedBB axisalignedbb, Material material, Entity entity) {
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);
		if (!this.checkChunksExist(i, k, i1, j, l, j1)) {
			return false;
		} else {
			boolean flag = false;
			Vec3D vec3d = Vec3D.createVector(0.0D, 0.0D, 0.0D);

			for (int d = i; d < j; ++d) {
				for (int l1 = k; l1 < l; ++l1) {
					for (int i2 = i1; i2 < j1; ++i2) {
						Block block = Block.blocksList[this.getBlockId(d, l1, i2)];
						if (block != null && block.blockMaterial == material) {
							double d1 = (double) ((float) (l1 + 1)
									- BlockFluid.setFluidHeight(this.getBlockMetadata(d, l1, i2)));
							if ((double) l >= d1) {
								flag = true;
								block.velocityToAddToEntity(this, d, l1, i2, entity, vec3d);
							}
						}
					}
				}
			}

			if (vec3d.lengthVector() > 0.0D) {
				vec3d = vec3d.normalize();
				double d18 = 0.014D;
				entity.motionX += vec3d.xCoord * d18;
				entity.motionY += vec3d.yCoord * d18;
				entity.motionZ += vec3d.zCoord * d18;
			}

			return flag;
		}
	}

	public boolean isMaterialInBB(AxisAlignedBB axisalignedbb, Material material) {
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = k; l1 < l; ++l1) {
				for (int i2 = i1; i2 < j1; ++i2) {
					Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];
					if (block != null && block.blockMaterial == material) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isAABBInMaterial(AxisAlignedBB axisalignedbb, Material material) {
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = k; l1 < l; ++l1) {
				for (int i2 = i1; i2 < j1; ++i2) {
					Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];
					if (block != null && block.blockMaterial == material) {
						int j2 = this.getBlockMetadata(k1, l1, i2);
						double d = (double) (l1 + 1);
						if (j2 < 8) {
							d = (double) (l1 + 1) - (double) j2 / 8.0D;
						}

						if (d >= axisalignedbb.minY) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public Explosion createExplosion(Entity entity, double d, double d1, double d2, float f) {
		return this.newExplosion(entity, d, d1, d2, f, false);
	}

	public Explosion newExplosion(Entity entity, double d, double d1, double d2, float f, boolean flag) {
		Explosion explosion = new Explosion(this, entity, d, d1, d2, f);
		explosion.isFlaming = flag;
		explosion.doExplosion();
		explosion.doEffects(true);
		return explosion;
	}

	public float func_494_a(Vec3D vec3d, AxisAlignedBB axisalignedbb) {
		double d = 1.0D / ((axisalignedbb.maxX - axisalignedbb.minX) * 2.0D + 1.0D);
		double d1 = 1.0D / ((axisalignedbb.maxY - axisalignedbb.minY) * 2.0D + 1.0D);
		double d2 = 1.0D / ((axisalignedbb.maxZ - axisalignedbb.minZ) * 2.0D + 1.0D);
		int i = 0;
		int j = 0;

		for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d)) {
			for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
				for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
					double d3 = axisalignedbb.minX + (axisalignedbb.maxX - axisalignedbb.minX) * (double) f;
					double d4 = axisalignedbb.minY + (axisalignedbb.maxY - axisalignedbb.minY) * (double) f1;
					double d5 = axisalignedbb.minZ + (axisalignedbb.maxZ - axisalignedbb.minZ) * (double) f2;
					if (this.rayTraceBlocks(Vec3D.createVector(d3, d4, d5), vec3d) == null) {
						++i;
					}

					++j;
				}
			}
		}

		return (float) i / (float) j;
	}

	public void func_28096_a(EntityPlayer entityplayer, int i, int j, int k, int l) {
		if (l == 0) {
			--j;
		}

		if (l == 1) {
			++j;
		}

		if (l == 2) {
			--k;
		}

		if (l == 3) {
			++k;
		}

		if (l == 4) {
			--i;
		}

		if (l == 5) {
			++i;
		}

		if (this.getBlockId(i, j, k) == Block.fire.blockID) {
			this.func_28101_a(entityplayer, 1004, i, j, k, 0);
			this.setBlockWithNotify(i, j, k, 0);
		}

	}

	public TileEntity getBlockTileEntity(int i, int j, int k) {
		ChunkCube chunk = this.getChunkFromChunkCoords(i >> 4, j >> 4, k >> 4);
		return chunk != null ? chunk.getChunkBlockTileEntity(i & 15, j & 15, k & 15) : null;
	}

	public void setBlockTileEntity(int i, int j, int k, TileEntity tileentity) {
		if (!tileentity.isInvalid()) {
			if (this.field_31048_L) {
				tileentity.xCoord = i;
				tileentity.yCoord = j;
				tileentity.zCoord = k;
				this.field_20912_E.add(tileentity);
			} else {
				this.loadedTileEntityList.add(tileentity);
				ChunkCube chunk = this.getChunkFromChunkCoords(i >> 4, j >> 4, k >> 4);
				if (chunk != null) {
					chunk.setChunkBlockTileEntity(i & 15, j & 15, k & 15, tileentity);
				}
			}
		}

	}

	public void removeBlockTileEntity(int i, int j, int k) {
		TileEntity tileentity = this.getBlockTileEntity(i, j, k);
		if (tileentity != null && this.field_31048_L) {
			tileentity.invalidate();
		} else {
			if (tileentity != null) {
				this.loadedTileEntityList.remove(tileentity);
			}

			ChunkCube chunk = this.getChunkFromChunkCoords(i >> 4, j >> 4, k >> 4);
			if (chunk != null) {
				chunk.removeChunkBlockTileEntity(i & 15, j & 15, k & 15);
			}
		}

	}

	public boolean isBlockOpaqueCube(int i, int j, int k) {
		Block block = Block.blocksList[this.getBlockId(i, j, k)];
		return block == null ? false : block.isOpaqueCube();
	}

	public boolean isBlockNormalCube(int i, int j, int k) {
		Block block = Block.blocksList[this.getBlockId(i, j, k)];
		return block == null ? false : block.blockMaterial.getIsOpaque() && block.isACube();
	}

	public boolean func_6156_d() {
		if (this.field_4265_J >= 50) {
			return false;
		} else {
			++this.field_4265_J;

			try {
				int i = 500;

				boolean flag1;
				boolean z3;
				while (this.lightingToUpdate.size() > 0) {
					--i;
					if (i <= 0) {
						flag1 = true;
						z3 = flag1;
						return z3;
					}

					((MetadataChunkBlock) this.lightingToUpdate.remove(this.lightingToUpdate.size() - 1))
							.updateChunkLighting(this);
				}

				flag1 = false;
				z3 = flag1;
				return z3;
			} finally {
				--this.field_4265_J;
			}
		}
	}

	public void scheduleLightingUpdate(EnumSkyBlock enumskyblock, int i, int j, int k, int l, int i1, int j1) {
		this.scheduleLightingUpdate_do(enumskyblock, i, j, k, l, i1, j1, true);
	}

	public void scheduleLightingUpdate_do(EnumSkyBlock enumskyblock, int x1, int y1, int z1, int x2, int y2, int z2,
			boolean flag) {
		if (!this.worldProvider.hasNoSky || enumskyblock != EnumSkyBlock.Sky) {
			++lightingUpdatesScheduled;

			try {
				if (lightingUpdatesScheduled == 50) {
					return;
				}

				int xAvg = x2 + x1 >> 1;
				int yAvg = y2 + y1 >> 1;
				int zAvg = z2 + z1 >> 1;
				if (!this.blockExists(xAvg, yAvg, zAvg)) {
					return;
				}

				if (!this.getChunkFromBlockCoords(xAvg, zAvg).func_21101_g()) {
					int i2 = this.lightingToUpdate.size();
					int k2;
					if (flag) {
						k2 = 5;
						if (k2 > i2) {
							k2 = i2;
						}

						for (int l2 = 0; l2 < k2; ++l2) {
							MetadataChunkBlock metadatachunkblock = (MetadataChunkBlock) this.lightingToUpdate
									.get(this.lightingToUpdate.size() - l2 - 1);
							if (metadatachunkblock.lightType == enumskyblock
									&& metadatachunkblock.func_692_a(x1, y1, z1, x2, y2, z2)) {
								return;
							}
						}
					}

					this.lightingToUpdate.add(new MetadataChunkBlock(enumskyblock, x1, y1, z1, x2, y2, z2));
					k2 = 1000000;
					if (this.lightingToUpdate.size() > k2) {
						System.out.println("More than " + k2 + " updates, aborting lighting updates");
						this.lightingToUpdate.clear();
					}

					return;
				}
			} finally {
				--lightingUpdatesScheduled;
			}

		}
	}

	public void calculateInitialSkylight() {
		int i = this.calculateSkylightSubtracted(1.0F);
		if (i != this.skylightSubtracted) {
			this.skylightSubtracted = i;
		}

	}

	public void setAllowedSpawnTypes(boolean flag, boolean flag1) {
		this.spawnHostileMobs = flag;
		this.spawnPeacefulMobs = flag1;
	}

	public void tick() {
		this.updateWeather();
		long l1;
		if (this.isAllPlayersFullyAsleep()) {
			boolean i = false;
			if (this.spawnHostileMobs && this.difficultySetting >= 1) {
				i = SpawnerAnimals.performSleepSpawning(this, this.playerEntities);
			}

			if (!i) {
				l1 = this.worldInfo.getWorldTime() + 24000L;
				this.worldInfo.setWorldTime(l1 - l1 % 24000L);
				this.wakeUpAllPlayers();
			}
		}

		SpawnerAnimals.performSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs);
		this.chunkProvider.unload100OldestChunks();
		int i4 = this.calculateSkylightSubtracted(1.0F);
		if (i4 != this.skylightSubtracted) {
			this.skylightSubtracted = i4;

			for (int i5 = 0; i5 < this.worldAccesses.size(); ++i5) {
				((IWorldAccess) this.worldAccesses.get(i5)).updateAllRenderers();
			}
		}

		l1 = this.worldInfo.getWorldTime() + 1L;
		this.autosavePeriod = 3000;
		if (l1 % (long) this.autosavePeriod == 0L) {
			this.saveWorld(false, (IProgressUpdate) null);
		}

		this.worldInfo.setWorldTime(l1);
		this.TickUpdates(false);
		this.doRandomUpdateTicks();
	}

	private void func_27070_x() {
		if (this.worldInfo.getIsRaining()) {
			this.field_27078_C = 1.0F;
			if (this.worldInfo.getIsThundering()) {
				this.field_27076_E = 1.0F;
			}
		}

	}

	protected void updateWeather() {
		if (!this.worldProvider.hasNoSky) {
			if (this.field_27075_F > 0) {
				--this.field_27075_F;
			}

			int i = this.worldInfo.getThunderTime();
			if (i <= 0) {
				if (this.worldInfo.getIsThundering()) {
					this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
				} else {
					this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
				}
			} else {
				--i;
				this.worldInfo.setThunderTime(i);
				if (i <= 0) {
					this.worldInfo.setIsThundering(!this.worldInfo.getIsThundering());
				}
			}

			int j = this.worldInfo.getRainTime();
			if (j <= 0) {
				if (this.worldInfo.getIsRaining()) {
					this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
				} else {
					this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
				}
			} else {
				--j;
				this.worldInfo.setRainTime(j);
				if (j <= 0) {
					this.worldInfo.setIsRaining(!this.worldInfo.getIsRaining());
				}
			}

			this.field_27079_B = this.field_27078_C;
			if (this.worldInfo.getIsRaining()) {
				this.field_27078_C = (float) ((double) this.field_27078_C + 0.01D);
			} else {
				this.field_27078_C = (float) ((double) this.field_27078_C - 0.01D);
			}

			if (this.field_27078_C < 0.0F) {
				this.field_27078_C = 0.0F;
			}

			if (this.field_27078_C > 1.0F) {
				this.field_27078_C = 1.0F;
			}

			this.field_27077_D = this.field_27076_E;
			if (this.worldInfo.getIsThundering()) {
				this.field_27076_E = (float) ((double) this.field_27076_E + 0.01D);
			} else {
				this.field_27076_E = (float) ((double) this.field_27076_E - 0.01D);
			}

			if (this.field_27076_E < 0.0F) {
				this.field_27076_E = 0.0F;
			}

			if (this.field_27076_E > 1.0F) {
				this.field_27076_E = 1.0F;
			}

		}
	}

	private void clearWeather() {
		this.worldInfo.setRainTime(0);
		this.worldInfo.setIsRaining(false);
		this.worldInfo.setThunderTime(0);
		this.worldInfo.setIsThundering(false);
	}

	protected void doRandomUpdateTicks() {
		this.activeChunkSet.clear();

		int x;
		int y;
		int z;
		int j2;
		int k3;
		for (int iterator = 0; iterator < this.playerEntities.size(); ++iterator) {
			EntityPlayer pos = (EntityPlayer) this.playerEntities.get(iterator);
			x = MathHelper.floor_double(pos.posX / 16.0D);
			y = MathHelper.floor_double(pos.posY / 16.0D);
			z = MathHelper.floor_double(pos.posZ / 16.0D);
			byte chunk = 9;

			for (j2 = -chunk; j2 <= chunk; ++j2) {
				for (k3 = -chunk; k3 <= chunk; ++k3) {
					this.activeChunkSet.add(new ChunkCubeCoord(j2 + x, y, k3 + z));
				}
			}
		}

		if (this.ambientTickCountdown > 0) {
			--this.ambientTickCountdown;
		}

		Iterator iterator13 = this.activeChunkSet.iterator();

		while (iterator13.hasNext()) {
			ChunkCubeCoord chunkCubeCoord14 = (ChunkCubeCoord) iterator13.next();
			x = chunkCubeCoord14.chunkXPos * 16;
			y = chunkCubeCoord14.chunkYPos * 16;
			z = chunkCubeCoord14.chunkZPos * 16;
			Chunk chunk15 = this.getChunkFromChunkCoords(chunkCubeCoord14.chunkXPos, chunkCubeCoord14.chunkZPos);
			int k4;
			int k5;
			int j6;
			if (this.ambientTickCountdown == 0) {
				this.distHashCounter = this.distHashCounter * 3 + 1013904223;
				j2 = this.distHashCounter >> 2;
				k3 = j2 & 15;
				k4 = j2 >> 8 & 15;
				k5 = j2 >> 16 & 127;
				k5 += y;
				j6 = chunk15.getBlockID(k3, k5, k4);
				k3 += x;
				k4 += z;
				if (j6 == 0 && this.getBlockLightValueNoChecks(k3, k5, k4) <= this.rand.nextInt(8)
						&& this.getSavedLightValue(EnumSkyBlock.Sky, k3, k5, k4) <= 0) {
					EntityPlayer l6 = this.getClosestPlayer((double) k3 + 0.5D, (double) k5 + 0.5D, (double) k4 + 0.5D,
							8.0D);
					if (l6 != null
							&& l6.getDistanceSq((double) k3 + 0.5D, (double) k5 + 0.5D, (double) k4 + 0.5D) > 4.0D) {
						this.playSoundEffect((double) k3 + 0.5D, (double) k5 + 0.5D, (double) k4 + 0.5D,
								"ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
						this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
					}
				}
			}

			if (this.rand.nextInt(100000) == 0 && this.func_27068_v() && this.func_27067_u()) {
				this.distHashCounter = this.distHashCounter * 3 + 1013904223;
				j2 = this.distHashCounter >> 2;
				k3 = x + (j2 & 15);
				k4 = z + (j2 >> 8 & 15);
				k5 = this.getTopSolidOrLiquidBlock(k3, k4);
				if (this.canLightningStrikeAt(k3, k5, k4)) {
					this.addLightningBolt(new EntityLightningBolt(this, (double) k3, (double) k5, (double) k4));
					this.field_27075_F = 2;
				}
			}

			int i16;
			if (this.rand.nextInt(16) == 0) {
				this.distHashCounter = this.distHashCounter * 3 + 1013904223;
				j2 = this.distHashCounter >> 2;
				k3 = j2 & 15;
				k4 = j2 >> 8 & 15;
				k5 = this.getTopSolidOrLiquidBlock(k3 + x, k4 + z);
				if (this.getWorldChunkManager().getBiomeGenAt(k3 + x, k4 + z).getEnableSnow()
						&& chunk15.getSavedLightValue(EnumSkyBlock.Block, k3, k5, k4) < 10) {
					j6 = chunk15.getBlockID(k3, k5 - 1, k4);
					i16 = chunk15.getBlockID(k3, k5, k4);
					if (this.func_27068_v() && i16 == 0 && Block.snow.canPlaceBlockAt(this, k3 + x, k5, k4 + z)
							&& j6 != 0 && j6 != Block.ice.blockID && Block.blocksList[j6].blockMaterial.getIsSolid()) {
						this.setBlockWithNotify(k3 + x, k5, k4 + z, Block.snow.blockID);
					}

					if (j6 == Block.waterStill.blockID && chunk15.getBlockMetadata(k3, k5 - 1, k4) == 0) {
						this.setBlockWithNotify(k3 + x, k5 - 1, k4 + z, Block.ice.blockID);
					}
				}
			}

			for (j2 = 0; j2 < 80; ++j2) {
				this.distHashCounter = this.distHashCounter * 3 + 1013904223;
				k3 = this.distHashCounter >> 2;
				k4 = k3 & 15;
				k5 = k3 >> 8 & 15;
				j6 = k3 >> 16 & 127;
				j6 += y;
				i16 = chunk15.getBlockID(k4, j6, k5);
				if (Block.tickOnLoad[i16]) {
					Block.blocksList[i16].updateTick(this, k4 + x, j6, k5 + z, this.rand);
				}
			}
		}

	}

	public boolean TickUpdates(boolean flag) {
		int i = this.scheduledTickTreeSet.size();
		if (i != this.scheduledTickSet.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {
			if (i > 1000) {
				i = 1000;
			}

			for (int j = 0; j < i; ++j) {
				NextTickListEntry nextticklistentry = (NextTickListEntry) this.scheduledTickTreeSet.first();
				if (!flag && nextticklistentry.scheduledTime > this.worldInfo.getWorldTime()) {
					break;
				}

				this.scheduledTickTreeSet.remove(nextticklistentry);
				this.scheduledTickSet.remove(nextticklistentry);
				byte byte0 = 8;
				if (this.checkChunksExist(nextticklistentry.xCoord - byte0, nextticklistentry.yCoord - byte0,
						nextticklistentry.zCoord - byte0, nextticklistentry.xCoord + byte0,
						nextticklistentry.yCoord + byte0, nextticklistentry.zCoord + byte0)) {
					int k = this.getBlockId(nextticklistentry.xCoord, nextticklistentry.yCoord,
							nextticklistentry.zCoord);
					if (k == nextticklistentry.blockID && k > 0) {
						Block.blocksList[k].updateTick(this, nextticklistentry.xCoord, nextticklistentry.yCoord,
								nextticklistentry.zCoord, this.rand);
					}
				}
			}

			return this.scheduledTickTreeSet.size() != 0;
		}
	}

	public List getEntitiesWithinAABBExcludingEntity(Entity entity, AxisAlignedBB axisalignedbb) {
		this.field_778_L.clear();
		int xChunk1 = MathHelper.floor_double((axisalignedbb.minX - 2.0D) / 16.0D);
		int xChunk2 = MathHelper.floor_double((axisalignedbb.maxX + 2.0D) / 16.0D);
		int yChunk1 = MathHelper.floor_double((axisalignedbb.minY - 2.0D) / 16.0D);
		int zChunk1 = MathHelper.floor_double((axisalignedbb.minZ - 2.0D) / 16.0D);
		int zChunk2 = MathHelper.floor_double((axisalignedbb.maxZ + 2.0D) / 16.0D);

		for (int x3 = xChunk1; x3 <= xChunk2; ++x3) {
			for (int z3 = zChunk1; z3 <= zChunk2; ++z3) {
				if (this.cubeExists(x3, yChunk1, z3)) {
					this.getChunkFromChunkCoords(x3, yChunk1, z3).chunk.getEntitiesWithinAABBForEntity(entity,
							axisalignedbb, this.field_778_L);
				}
			}
		}

		return this.field_778_L;
	}

	public List getEntitiesWithinAABB(Class class1, AxisAlignedBB axisalignedbb) {
		int i = MathHelper.floor_double((axisalignedbb.minX - 2.0D) / 16.0D);
		int j = MathHelper.floor_double((axisalignedbb.maxX + 2.0D) / 16.0D);
		int k = MathHelper.floor_double((axisalignedbb.minZ - 2.0D) / 16.0D);
		int l = MathHelper.floor_double((axisalignedbb.maxZ + 2.0D) / 16.0D);
		ArrayList arraylist = new ArrayList();

		for (int i1 = i; i1 <= j; ++i1) {
			for (int j1 = k; j1 <= l; ++j1) {
				if (this.chunkExists(i1, j1)) {
					this.getChunkFromChunkCoords(i1, j1).getEntitiesOfTypeWithinAAAB(class1, axisalignedbb, arraylist);
				}
			}
		}

		return arraylist;
	}

	public void updateTileEntityChunkAndDoNothing(int i, int j, int k, TileEntity tileentity) {
		if (this.blockExists(i, j, k)) {
			this.getChunkFromBlockCoords(i, k).setChunkModified();
		}

		for (int l = 0; l < this.worldAccesses.size(); ++l) {
			((IWorldAccess) this.worldAccesses.get(l)).doNothingWithTileEntity(i, j, k, tileentity);
		}

	}

	public int countEntities(Class class1) {
		int i = 0;

		for (int j = 0; j < this.loadedEntityList.size(); ++j) {
			Entity entity = (Entity) this.loadedEntityList.get(j);
			if (class1.isAssignableFrom(entity.getClass())) {
				++i;
			}
		}

		return i;
	}

	public void addLoadedEntities(List list) {
		this.loadedEntityList.addAll(list);

		for (int i = 0; i < list.size(); ++i) {
			this.obtainEntitySkin((Entity) list.get(i));
		}

	}

	public void addUnloadedEntities(List list) {
		this.unloadedEntityList.addAll(list);
	}

	public boolean canBlockBePlacedAt(int i, int j, int k, int l, boolean flag, int i1) {
		int j1 = this.getBlockId(j, k, l);
		Block block = Block.blocksList[j1];
		Block block1 = Block.blocksList[i];
		AxisAlignedBB axisalignedbb = block1.getCollisionBoundingBoxFromPool(this, j, k, l);
		if (flag) {
			axisalignedbb = null;
		}

		if (axisalignedbb != null && !this.checkIfAABBIsClear(axisalignedbb)) {
			return false;
		} else {
			if (block == Block.waterMoving || block == Block.waterStill || block == Block.lavaMoving
					|| block == Block.lavaStill || block == Block.fire || block == Block.snow) {
				block = null;
			}

			return i > 0 && block == null && block1.canPlaceBlockOnSide(this, j, k, l, i1);
		}
	}

	public PathEntity getPathToEntity(Entity entity, Entity entity1, float f) {
		int i = MathHelper.floor_double(entity.posX);
		int j = MathHelper.floor_double(entity.posY);
		int k = MathHelper.floor_double(entity.posZ);
		int l = (int) (f + 16.0F);
		int i1 = i - l;
		int j1 = j - l;
		int k1 = k - l;
		int l1 = i + l;
		int i2 = j + l;
		int j2 = k + l;
		ChunkCache chunkcache = new ChunkCache(this, i1, j1, k1, l1, i2, j2);
		return (new Pathfinder(chunkcache)).createEntityPathTo(entity, entity1, f);
	}

	public PathEntity getEntityPathToXYZ(Entity entity, int i, int j, int k, float f) {
		int l = MathHelper.floor_double(entity.posX);
		int i1 = MathHelper.floor_double(entity.posY);
		int j1 = MathHelper.floor_double(entity.posZ);
		int k1 = (int) (f + 8.0F);
		int l1 = l - k1;
		int i2 = i1 - k1;
		int j2 = j1 - k1;
		int k2 = l + k1;
		int l2 = i1 + k1;
		int i3 = j1 + k1;
		ChunkCache chunkcache = new ChunkCache(this, l1, i2, j2, k2, l2, i3);
		return (new Pathfinder(chunkcache)).createEntityPathTo(entity, i, j, k, f);
	}

	public boolean isBlockProvidingPowerTo(int i, int j, int k, int l) {
		int i1 = this.getBlockId(i, j, k);
		return i1 == 0 ? false : Block.blocksList[i1].isIndirectlyPoweringTo(this, i, j, k, l);
	}

	public boolean isBlockGettingPowered(int i, int j, int k) {
		return this.isBlockProvidingPowerTo(i, j - 1, k, 0) ? true
				: (this.isBlockProvidingPowerTo(i, j + 1, k, 1) ? true
						: (this.isBlockProvidingPowerTo(i, j, k - 1, 2) ? true
								: (this.isBlockProvidingPowerTo(i, j, k + 1, 3) ? true
										: (this.isBlockProvidingPowerTo(i - 1, j, k, 4) ? true
												: this.isBlockProvidingPowerTo(i + 1, j, k, 5)))));
	}

	public boolean isBlockIndirectlyProvidingPowerTo(int i, int j, int k, int l) {
		if (this.isBlockNormalCube(i, j, k)) {
			return this.isBlockGettingPowered(i, j, k);
		} else {
			int i1 = this.getBlockId(i, j, k);
			return i1 == 0 ? false : Block.blocksList[i1].isPoweringTo(this, i, j, k, l);
		}
	}

	public boolean isBlockIndirectlyGettingPowered(int i, int j, int k) {
		return this.isBlockIndirectlyProvidingPowerTo(i, j - 1, k, 0) ? true
				: (this.isBlockIndirectlyProvidingPowerTo(i, j + 1, k, 1) ? true
						: (this.isBlockIndirectlyProvidingPowerTo(i, j, k - 1, 2) ? true
								: (this.isBlockIndirectlyProvidingPowerTo(i, j, k + 1, 3) ? true
										: (this.isBlockIndirectlyProvidingPowerTo(i - 1, j, k, 4) ? true
												: this.isBlockIndirectlyProvidingPowerTo(i + 1, j, k, 5)))));
	}

	public EntityPlayer getClosestPlayerToEntity(Entity entity, double d) {
		return this.getClosestPlayer(entity.posX, entity.posY, entity.posZ, d);
	}

	public EntityPlayer getClosestPlayer(double d, double d1, double d2, double d3) {
		double d4 = -1.0D;
		EntityPlayer entityplayer = null;

		for (int i = 0; i < this.playerEntities.size(); ++i) {
			EntityPlayer entityplayer1 = (EntityPlayer) this.playerEntities.get(i);
			double d5 = entityplayer1.getDistanceSq(d, d1, d2);
			if ((d3 < 0.0D || d5 < d3 * d3) && (d4 == -1.0D || d5 < d4)) {
				d4 = d5;
				entityplayer = entityplayer1;
			}
		}

		return entityplayer;
	}

	public EntityPlayer getPlayerEntityByName(String s) {
		for (int i = 0; i < this.playerEntities.size(); ++i) {
			if (s.equals(((EntityPlayer) this.playerEntities.get(i)).username)) {
				return (EntityPlayer) this.playerEntities.get(i);
			}
		}

		return null;
	}

	public byte[] getChunkData(int x1, int y1, int z1, int x2, int y2, int z2) {
		byte[] data = new byte[x2 * y2 * z2 * 5 / 2];
		int x1Ch = x1 >> 4;
		int y1Ch = y1 >> 4;
		int z1Ch = z1 >> 4;
		int sumChX = x1 + x2 - 1 >> 4;
		int sumChY = y1 + y2 - 1 >> 4;
		int sumChZ = z1 + z2 - 1 >> 4;
		int offset = 0;

		for (int x3Ch = x1Ch; x3Ch <= sumChX; ++x3Ch) {
			int xStart = x1 - x3Ch * 16;
			int xEnd = x1 + x2 - x3Ch * 16;
			if (xStart < 0) {
				xStart = 0;
			}

			if (xEnd > 16) {
				xEnd = 16;
			}

			for (int z3Ch = z1Ch; z3Ch <= sumChZ; ++z3Ch) {
				int zStart = z1 - z3Ch * 16;
				int zEnd = z1 + z2 - z3Ch * 16;
				if (zStart < 0) {
					zStart = 0;
				}

				if (zEnd > 16) {
					zEnd = 16;
				}

				for (int y3Ch = y1Ch; y3Ch <= sumChY; ++y3Ch) {
					int yStart = y1 - y3Ch * 16;
					int yEnd = y1 + y2 - y3Ch * 16;
					if (yStart < 0) {
						yStart = 0;
					}

					if (yEnd > 16) {
						yEnd = 16;
					}

					offset = this.getChunkFromChunkCoords(x3Ch, y3Ch, z3Ch).getChunkData(data, xStart, yStart, zStart,
							xEnd, yEnd, zEnd, offset);
				}
			}
		}

		return data;
	}

	public void checkSessionLock() {
		this.worldFile.func_22091_b();
	}

	public void setWorldTime(long l) {
		this.worldInfo.setWorldTime(l);
	}

	public void func_32005_b(long l) {
		long l1 = l - this.worldInfo.getWorldTime();

		NextTickListEntry nextticklistentry;
		for (Iterator iterator = this.scheduledTickSet.iterator(); iterator
				.hasNext(); nextticklistentry.scheduledTime += l1) {
			nextticklistentry = (NextTickListEntry) iterator.next();
		}

		this.setWorldTime(l);
	}

	public long getRandomSeed() {
		return this.worldInfo.getRandomSeed();
	}

	public long getWorldTime() {
		return this.worldInfo.getWorldTime();
	}

	public ChunkCoordinates getSpawnPoint() {
		return new ChunkCoordinates(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
	}

	public boolean canMineBlock(EntityPlayer entityplayer, int i, int j, int k) {
		return true;
	}

	public void sendTrackedEntityStatusUpdatePacket(Entity entity, byte byte0) {
	}

	public IChunkProvider o() {
		return this.chunkProvider;
	}

	public void playNoteAt(int i, int j, int k, int l, int i1) {
		int j1 = this.getBlockId(i, j, k);
		if (j1 > 0) {
			Block.blocksList[j1].playBlock(this, i, j, k, l, i1);
		}

	}

	public ISaveHandler getWorldFile() {
		return this.worldFile;
	}

	public WorldInfo getWorldInfo() {
		return this.worldInfo;
	}

	public void updateAllPlayersSleepingFlag() {
		this.allPlayersSleeping = !this.playerEntities.isEmpty();
		Iterator iterator = this.playerEntities.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();
			if (!entityplayer.func_22057_E()) {
				this.allPlayersSleeping = false;
				break;
			}
		}

	}

	protected void wakeUpAllPlayers() {
		this.allPlayersSleeping = false;
		Iterator iterator = this.playerEntities.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();
			if (entityplayer.func_22057_E()) {
				entityplayer.wakeUpPlayer(false, false, true);
			}
		}

		this.clearWeather();
	}

	public boolean isAllPlayersFullyAsleep() {
		if (this.allPlayersSleeping && !this.multiplayerWorld) {
			Iterator iterator = this.playerEntities.iterator();

			EntityPlayer entityplayer;
			do {
				if (!iterator.hasNext()) {
					return true;
				}

				entityplayer = (EntityPlayer) iterator.next();
			} while (entityplayer.isPlayerFullyAsleep());

			return false;
		} else {
			return false;
		}
	}

	public float func_27065_c(float f) {
		return (this.field_27077_D + (this.field_27076_E - this.field_27077_D) * f) * this.func_27074_d(f);
	}

	public float func_27074_d(float f) {
		return this.field_27079_B + (this.field_27078_C - this.field_27079_B) * f;
	}

	public boolean func_27067_u() {
		return (double) this.func_27065_c(1.0F) > 0.9D;
	}

	public boolean func_27068_v() {
		return (double) this.func_27074_d(1.0F) > 0.2D;
	}

	public boolean canLightningStrikeAt(int i, int j, int k) {
		if (!this.func_27068_v()) {
			return false;
		} else if (!this.canBlockSeeTheSky(i, j, k)) {
			return false;
		} else if (this.getTopSolidOrLiquidBlock(i, k) > j) {
			return false;
		} else {
			BiomeGenBase biomegenbase = this.getWorldChunkManager().getBiomeGenAt(i, k);
			return biomegenbase.getEnableSnow() ? false : biomegenbase.canSpawnLightningBolt();
		}
	}

	public void func_28102_a(String s, MapDataBase mapdatabase) {
		this.field_28105_z.func_28177_a(s, mapdatabase);
	}

	public MapDataBase func_28103_a(Class class1, String s) {
		return this.field_28105_z.func_28178_a(class1, s);
	}

	public int func_28104_b(String s) {
		return this.field_28105_z.func_28173_a(s);
	}

	public void func_28097_e(int i, int j, int k, int l, int i1) {
		this.func_28101_a((EntityPlayer) null, i, j, k, l, i1);
	}

	public void func_28101_a(EntityPlayer entityplayer, int i, int j, int k, int l, int i1) {
		for (int j1 = 0; j1 < this.worldAccesses.size(); ++j1) {
			((IWorldAccess) this.worldAccesses.get(j1)).func_28133_a(entityplayer, i, j, k, l, i1);
		}

	}

	public boolean isChunkCubeAir(int x, int y, int z) {
		return this.cubeExists(x, y, z) && this.getChunkFromChunkCoords(x, y, z).isAir;
	}
}
