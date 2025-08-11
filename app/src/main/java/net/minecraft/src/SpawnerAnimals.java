package net.minecraft.src;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class SpawnerAnimals {
	private static Set eligibleChunksForSpawning = new HashSet();
	protected static final Class[] nightSpawnEntities = new Class[]{EntitySpider.class, EntityZombie.class, EntitySkeleton.class};

	protected static ChunkPosition getRandomSpawningPointInChunk(World world, int x, int y, int z) {
		int x1 = x + world.rand.nextInt(16);
		int y1 = y - 64 + world.rand.nextInt(128);
		int z1 = z + world.rand.nextInt(16);
		return new ChunkPosition(x1, y1, z1);
	}

	public static final int performSpawning(World world, boolean alP, boolean alH) {
		if(!alP && !alH) {
			return 0;
		} else {
			eligibleChunksForSpawning.clear();
			boolean n = false;

			int numTypes;
			int var37;
			int i40;
			for(i40 = 0; i40 < world.playerEntities.size(); ++i40) {
				EntityPlayer var35 = (EntityPlayer)world.playerEntities.get(i40);
				int var36 = MathHelper.floor_double(var35.posX / 16.0D);
				numTypes = MathHelper.floor_double(var35.posY / 16.0D);
				var37 = MathHelper.floor_double(var35.posZ / 16.0D);
				byte creature = 8;

				for(int entityCount = -creature; entityCount <= creature; ++entityCount) {
					for(int maxNum = -creature; maxNum <= creature; ++maxNum) {
						eligibleChunksForSpawning.add(new ChunkCubeCoord(entityCount + var36, numTypes, maxNum + var37));
					}
				}
				
			}

			ChunkCoordinates chunkCoordinates41 = world.getSpawnPoint();
			EnumCreatureType[] enumCreatureType42 = EnumCreatureType.values();
			numTypes = enumCreatureType42.length;

			label138:
			for(var37 = 0; var37 < numTypes; ++var37) {
				EnumCreatureType enumCreatureType43 = enumCreatureType42[var37];
				float f45 = (float)world.countEntities(enumCreatureType43.getCreatureClass());
				float f46 = (float)(enumCreatureType43.getMaxNumberOfCreature() * eligibleChunksForSpawning.size() >> 8);
				if((!enumCreatureType43.getPeacefulCreature() || alH) && (enumCreatureType43.getPeacefulCreature() || alP) && f45 <= f46) {
					Iterator iterator47 = eligibleChunksForSpawning.iterator();

					label135:
					while(true) {
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
										if(!iterator47.hasNext()) {
											continue label138;
										}

										coord = (ChunkCubeCoord)iterator47.next();
										BiomeGenBase biomeGenBase48 = world.getWorldChunkManager().getBiomeGenAtChunkCoord(coord.toChunkCoord());
										creatures = biomeGenBase48.getSpawnableList(enumCreatureType43);
									} while(creatures == null);
								} while(creatures.isEmpty());

								int i49 = 0;

								for(Iterator var40 = creatures.iterator(); var40.hasNext(); i49 += entry.spawnRarityRate) {
									entry = (SpawnListEntry)var40.next();
								}

								int i50 = world.rand.nextInt(i49);
								entry = (SpawnListEntry)creatures.get(0);
								Iterator var16 = creatures.iterator();

								while(var16.hasNext()) {
									SpawnListEntry point = (SpawnListEntry)var16.next();
									i50 -= point.spawnRarityRate;
									if(i50 < 0) {
										entry = point;
										break;
									}
								}

								ChunkPosition chunkPosition51 = getRandomSpawningPointInChunk(world, coord.chunkXPos * 16, coord.chunkYPos * 16, coord.chunkZPos * 16);
								cX = chunkPosition51.x;
								cY = chunkPosition51.y;
								cZ = chunkPosition51.z;
							} while(world.isBlockNormalCube(cX, cY, cZ));
						} while(world.getBlockMaterial(cX, cY, cZ) != enumCreatureType43.getCreatureMaterial());

						int var20 = 0;

						for(int var21 = 0; var21 < 3; ++var21) {
							int eX = cX;
							int eY = cY;
							int eZ = cZ;
							byte range = 6;

							for(int var26 = 0; var26 < 4; ++var26) {
								eX += world.rand.nextInt(range) - world.rand.nextInt(range);
								eY += world.rand.nextInt(1) - world.rand.nextInt(1);
								eZ += world.rand.nextInt(range) - world.rand.nextInt(range);
								if(canCreatureTypeSpawnAtLocation(enumCreatureType43, world, eX, eY, eZ)) {
									float var27 = (float)eX + 0.5F;
									float var28 = (float)eY;
									float var29 = (float)eZ + 0.5F;
									if(world.getClosestPlayer((double)var27, (double)var28, (double)var29, 24.0D) == null) {
										float var30 = var27 - (float)chunkCoordinates41.x;
										float var31 = var28 - (float)chunkCoordinates41.y;
										float var32 = var29 - (float)chunkCoordinates41.z;
										float var33 = var30 * var30 + var31 * var31 + var32 * var32;
										if(var33 >= 576.0F) {
											EntityLiving var43;
											try {
												var43 = (EntityLiving)entry.entityClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
											} catch (Exception exception39) {
												exception39.printStackTrace();
												return i40;
											}

											var43.setLocationAndAngles((double)var27, (double)var28, (double)var29, world.rand.nextFloat() * 360.0F, 0.0F);
											if(var43.getCanSpawnHere()) {
												++var20;
												world.entityJoinedWorld(var43);
												creatureSpecificInit(var43, world, var27, var28, var29);
												if(var20 >= var43.getMaxSpawnedInChunk()) {
													continue label135;
												}
											}

											i40 += var20;
										}
									}
								}
							}
						}
					}
				}
			}

			return i40;
		}
	}

	private static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType enumcreaturetype, World world, int i, int j, int k) {
		return enumcreaturetype.getCreatureMaterial() == Material.water ? world.getBlockMaterial(i, j, k).getIsLiquid() && !world.isBlockNormalCube(i, j + 1, k) : world.isBlockNormalCube(i, j - 1, k) && !world.isBlockNormalCube(i, j, k) && !world.getBlockMaterial(i, j, k).getIsLiquid() && !world.isBlockNormalCube(i, j + 1, k);
	}

	private static void creatureSpecificInit(EntityLiving entityliving, World world, float f, float f1, float f2) {
		if(entityliving instanceof EntitySpider && world.rand.nextInt(100) == 0) {
			EntitySkeleton entityskeleton = new EntitySkeleton(world);
			entityskeleton.setLocationAndAngles((double)f, (double)f1, (double)f2, entityliving.rotationYaw, 0.0F);
			world.entityJoinedWorld(entityskeleton);
			entityskeleton.mountEntity(entityliving);
		} else if(entityliving instanceof EntitySheep) {
			((EntitySheep)entityliving).setFleeceColor(EntitySheep.getRandomFleeceColor(world.rand));
		}

	}

	public static boolean performSleepSpawning(World world, List list) {
		boolean flag = false;
		Pathfinder pathfinder = new Pathfinder(world);
		Iterator iterator = list.iterator();

		while(true) {
			EntityPlayer entityplayer;
			Class[] aclass;
			do {
				do {
					if(!iterator.hasNext()) {
						return flag;
					}

					entityplayer = (EntityPlayer)iterator.next();
					aclass = nightSpawnEntities;
				} while(aclass == null);
			} while(aclass.length == 0);

			boolean flag1 = false;

			for(int i = 0; i < 20 && !flag1; ++i) {
				int x = MathHelper.floor_double(entityplayer.posX) + world.rand.nextInt(32) - world.rand.nextInt(32);
				int z = MathHelper.floor_double(entityplayer.posZ) + world.rand.nextInt(32) - world.rand.nextInt(32);
				int y = MathHelper.floor_double(entityplayer.posY) + world.rand.nextInt(16) - world.rand.nextInt(16);
				int i1 = world.rand.nextInt(aclass.length);

				int j1;
				for(j1 = y; !world.isBlockNormalCube(x, j1 - 1, z); --j1) {
				}

				while(!canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, world, x, j1, z) && j1 < y + 16) {
					++j1;
				}

				if(j1 < y + 16) {
					float f = (float)x + 0.5F;
					float f1 = (float)j1;
					float f2 = (float)z + 0.5F;

					EntityLiving entityliving;
					try {
						entityliving = (EntityLiving)aclass[i1].getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
					} catch (Exception exception21) {
						exception21.printStackTrace();
						return flag;
					}

					entityliving.setLocationAndAngles((double)f, (double)f1, (double)f2, world.rand.nextFloat() * 360.0F, 0.0F);
					if(entityliving.getCanSpawnHere()) {
						PathEntity pathentity = pathfinder.createEntityPathTo(entityliving, entityplayer, 32.0F);
						if(pathentity != null && pathentity.pathLength > 1) {
							PathPoint pathpoint = pathentity.func_22328_c();
							if(Math.abs((double)pathpoint.xCoord - entityplayer.posX) < 1.5D && Math.abs((double)pathpoint.zCoord - entityplayer.posZ) < 1.5D && Math.abs((double)pathpoint.yCoord - entityplayer.posY) < 1.5D) {
								ChunkCoordinates chunkcoordinates = BlockBed.getNearestEmptyChunkCoordinates(world, MathHelper.floor_double(entityplayer.posX), MathHelper.floor_double(entityplayer.posY), MathHelper.floor_double(entityplayer.posZ), 1);
								if(chunkcoordinates == null) {
									chunkcoordinates = new ChunkCoordinates(x, j1 + 1, z);
								}

								entityliving.setLocationAndAngles((double)((float)chunkcoordinates.x + 0.5F), (double)chunkcoordinates.y, (double)((float)chunkcoordinates.z + 0.5F), 0.0F, 0.0F);
								world.entityJoinedWorld(entityliving);
								creatureSpecificInit(entityliving, world, (float)chunkcoordinates.x + 0.5F, (float)chunkcoordinates.y, (float)chunkcoordinates.z + 0.5F);
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
