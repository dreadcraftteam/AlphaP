package net.minecraft_server.src;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class SpawnerAnimals {
	private static Set eligibleChunksForSpawning = new HashSet();
	protected static final Class[] nightSpawnEntities = new Class[] { EntitySpider.class, EntityZombie.class,
			EntitySkeleton.class };

	protected static ChunkPosition getRandomSpawningPointInChunk(World world, int x, int y, int z) {
		int x1 = x + world.rand.nextInt(16);
		int y1 = y - 64 + world.rand.nextInt(128);
		int z1 = z + world.rand.nextInt(16);
		return new ChunkPosition(x1, y1, z1);
	}

	public static final int performSpawning(World world, boolean alP, boolean alH) {
		if (!alP && !alH) {
			return 0;
		} else {
			eligibleChunksForSpawning.clear();

			int n;
			int numTypes;
			int var37;
			for (n = 0; n < world.playerEntities.size(); ++n) {
				EntityPlayer var35 = (EntityPlayer) world.playerEntities.get(n);
				int var36 = MathHelper.floor_double(var35.posX / 16.0D);
				numTypes = MathHelper.floor_double(var35.posY / 16.0D);
				var37 = MathHelper.floor_double(var35.posZ / 16.0D);
				byte creature = 8;

				for (int entityCount = -creature; entityCount <= creature; ++entityCount) {
					for (int maxNum = -creature; maxNum <= creature; ++maxNum) {
						eligibleChunksForSpawning
								.add(new ChunkCubeCoord(entityCount + var36, numTypes, maxNum + var37));
					}
				}
			}

			n = 0;
			ChunkCoordinates chunkCoordinates40 = world.getSpawnPoint();
			EnumCreatureType[] enumCreatureType41 = EnumCreatureType.values();
			numTypes = enumCreatureType41.length;

			label126: for (var37 = 0; var37 < numTypes; ++var37) {
				EnumCreatureType enumCreatureType42 = enumCreatureType41[var37];
				float f43 = (float) world.countEntities(enumCreatureType42.getCreatureClass());
				float f44 = (float) (enumCreatureType42.getMaxNumberOfCreature()
						* eligibleChunksForSpawning.size() >> 8);
				if ((!enumCreatureType42.getPeacefulCreature() || alH)
						&& (enumCreatureType42.getPeacefulCreature() || alP) && f43 <= f44) {
					Iterator var39 = eligibleChunksForSpawning.iterator();

					label123: while (true) {
						SpawnListEntry entry;
						int cX;
						int cY;
						int cZ;
						do {
							do {
								ChunkCubeCoord coord;
								List creatures;
								do {
									do {
										if (!var39.hasNext()) {
											continue label126;
										}

										coord = (ChunkCubeCoord) var39.next();
										BiomeGenBase biome = world.getWorldChunkManager()
												.GetBiomeGenAtChunkCoord(coord.toChunkCoord());
										creatures = biome.getSpawnableList(enumCreatureType42);
									} while (creatures == null);
								} while (creatures.isEmpty());

								int var13 = 0;

								for (Iterator var40 = creatures.iterator(); var40
										.hasNext(); var13 += entry.spawnRarityRate) {
									entry = (SpawnListEntry) var40.next();
								}

								int i45 = world.rand.nextInt(var13);
								entry = (SpawnListEntry) creatures.get(0);
								Iterator var16 = creatures.iterator();

								while (var16.hasNext()) {
									SpawnListEntry point = (SpawnListEntry) var16.next();
									i45 -= point.spawnRarityRate;
									if (i45 < 0) {
										entry = point;
										break;
									}
								}

								ChunkPosition chunkPosition46 = getRandomSpawningPointInChunk(world,
										coord.chunkXPos * 16, coord.chunkYPos * 16, coord.chunkZPos * 16);
								cX = chunkPosition46.x;
								cY = chunkPosition46.y;
								cZ = chunkPosition46.z;
							} while (world.isBlockNormalCube(cX, cY, cZ));
						} while (world.getBlockMaterial(cX, cY, cZ) != enumCreatureType42.getCreatureMaterial());

						int var20 = 0;

						for (int var21 = 0; var21 < 3; ++var21) {
							int eX = cX;
							int eY = cY;
							int eZ = cZ;
							byte range = 6;

							for (int var26 = 0; var26 < 4; ++var26) {
								eX += world.rand.nextInt(range) - world.rand.nextInt(range);
								eY += world.rand.nextInt(1) - world.rand.nextInt(1);
								eZ += world.rand.nextInt(range) - world.rand.nextInt(range);
								if (canCreatureTypeSpawnAtLocation(enumCreatureType42, world, eX, eY, eZ)) {
									float var27 = (float) eX + 0.5F;
									float var28 = (float) eY;
									float var29 = (float) eZ + 0.5F;
									if (world.getClosestPlayer((double) var27, (double) var28, (double) var29,
											24.0D) == null) {
										float var30 = var27 - (float) chunkCoordinates40.posX;
										float var31 = var28 - (float) chunkCoordinates40.posY;
										float var32 = var29 - (float) chunkCoordinates40.posZ;
										float var33 = var30 * var30 + var31 * var31 + var32 * var32;
										if (var33 >= 576.0F) {
											EntityLiving var43;
											try {
												var43 = (EntityLiving) entry.entityClass
														.getConstructor(new Class[] { World.class })
														.newInstance(new Object[] { world });
											} catch (Exception exception39) {
												exception39.printStackTrace();
												return n;
											}

											var43.setLocationAndAngles((double) var27, (double) var28, (double) var29,
													world.rand.nextFloat() * 360.0F, 0.0F);
											if (var43.getCanSpawnHere()) {
												++var20;
												world.entityJoinedWorld(var43);
												creatureSpecificInit(var43, world, var27, var28, var29);
												if (var20 >= var43.getMaxSpawnedInChunk()) {
													continue label123;
												}
											}

											n += var20;
										}
									}
								}
							}
						}
					}
				}
			}

			return n;
		}
	}

	private static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType enumcreaturetype, World world, int i, int j,
			int k) {
		return enumcreaturetype.getCreatureMaterial() == Material.water
				? world.getBlockMaterial(i, j, k).getIsLiquid() && !world.isBlockNormalCube(i, j + 1, k)
				: world.isBlockNormalCube(i, j - 1, k) && !world.isBlockNormalCube(i, j, k)
						&& !world.getBlockMaterial(i, j, k).getIsLiquid() && !world.isBlockNormalCube(i, j + 1, k);
	}

	private static void creatureSpecificInit(EntityLiving entityliving, World world, float f, float f1, float f2) {
		if (entityliving instanceof EntitySpider && world.rand.nextInt(100) == 0) {
			EntitySkeleton entityskeleton = new EntitySkeleton(world);
			entityskeleton.setLocationAndAngles((double) f, (double) f1, (double) f2, entityliving.rotationYaw, 0.0F);
			world.entityJoinedWorld(entityskeleton);
			entityskeleton.mountEntity(entityliving);
		} else if (entityliving instanceof EntitySheep) {
			((EntitySheep) entityliving).setFleeceColor(EntitySheep.func_21066_a(world.rand));
		}

	}

	public static boolean performSleepSpawning(World world, List list) {
		boolean flag = false;
		Pathfinder pathfinder = new Pathfinder(world);
		Iterator iterator = list.iterator();

		while (true) {
			EntityPlayer entityplayer;
			Class[] aclass;
			do {
				do {
					if (!iterator.hasNext()) {
						return flag;
					}

					entityplayer = (EntityPlayer) iterator.next();
					aclass = nightSpawnEntities;
				} while (aclass == null);
			} while (aclass.length == 0);

			boolean flag1 = false;

			for (int i = 0; i < 20 && !flag1; ++i) {
				int x = MathHelper.floor_double(entityplayer.posX) + world.rand.nextInt(32) - world.rand.nextInt(32);
				int z = MathHelper.floor_double(entityplayer.posZ) + world.rand.nextInt(32) - world.rand.nextInt(32);
				int y = MathHelper.floor_double(entityplayer.posY) + world.rand.nextInt(16) - world.rand.nextInt(16);
				int i1 = world.rand.nextInt(aclass.length);

				int j1;
				for (j1 = y; !world.isBlockNormalCube(x, j1 - 1, z); --j1) {
				}

				while (!canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, world, x, j1, z) && j1 < y + 16) {
					++j1;
				}

				if (j1 < y + 16) {
					float f = (float) x + 0.5F;
					float f1 = (float) j1;
					float f2 = (float) z + 0.5F;

					EntityLiving entityliving;
					try {
						entityliving = (EntityLiving) aclass[i1].getConstructor(new Class[] { World.class })
								.newInstance(new Object[] { world });
					} catch (Exception exception21) {
						exception21.printStackTrace();
						return flag;
					}

					entityliving.setLocationAndAngles((double) f, (double) f1, (double) f2,
							world.rand.nextFloat() * 360.0F, 0.0F);
					if (entityliving.getCanSpawnHere()) {
						PathEntity pathentity = pathfinder.createEntityPathTo(entityliving, entityplayer, 32.0F);
						if (pathentity != null && pathentity.pathLength > 1) {
							PathPoint pathpoint = pathentity.func_22211_c();
							if (Math.abs((double) pathpoint.xCoord - entityplayer.posX) < 1.5D
									&& Math.abs((double) pathpoint.zCoord - entityplayer.posZ) < 1.5D
									&& Math.abs((double) pathpoint.yCoord - entityplayer.posY) < 1.5D) {
								ChunkCoordinates chunkcoordinates = BlockBed.func_22021_g(world,
										MathHelper.floor_double(entityplayer.posX),
										MathHelper.floor_double(entityplayer.posY),
										MathHelper.floor_double(entityplayer.posZ), 1);
								if (chunkcoordinates == null) {
									chunkcoordinates = new ChunkCoordinates(x, j1 + 1, z);
								}

								entityliving.setLocationAndAngles((double) ((float) chunkcoordinates.posX + 0.5F),
										(double) chunkcoordinates.posY, (double) ((float) chunkcoordinates.posZ + 0.5F),
										0.0F, 0.0F);
								world.entityJoinedWorld(entityliving);
								creatureSpecificInit(entityliving, world, (float) chunkcoordinates.posX + 0.5F,
										(float) chunkcoordinates.posY, (float) chunkcoordinates.posZ + 0.5F);
								entityplayer.wakeUpPlayer(true, false, false);
								entityliving.playLivingSound();
								flag = true;
								flag1 = true;
							}
						}
					}
				}
			}
		}
	}
}
