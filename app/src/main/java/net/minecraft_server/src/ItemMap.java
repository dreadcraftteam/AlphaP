package net.minecraft_server.src;

public class ItemMap extends ItemMapBase {
	protected ItemMap(int i) {
		super(i);
		this.setMaxStackSize(1);
	}

	public MapData func_28023_a(ItemStack itemstack, World world) {
		(new StringBuilder()).append("map_").append(itemstack.getItemDamage()).toString();
		MapData mapdata = (MapData) world.func_28103_a(MapData.class, "map_" + itemstack.getItemDamage());
		if (mapdata == null) {
			itemstack.setItemDamage(world.func_28104_b("map"));
			String s1 = "map_" + itemstack.getItemDamage();
			mapdata = new MapData(s1);
			mapdata.field_28164_b = world.getWorldInfo().getSpawnX();
			mapdata.field_28163_c = world.getWorldInfo().getSpawnZ();
			mapdata.field_28161_e = 3;
			mapdata.field_28162_d = (byte) world.worldProvider.worldType;
			mapdata.func_28146_a();
			world.func_28102_a(s1, mapdata);
		}

		return mapdata;
	}

	public void func_28024_a(World world, Entity entity, MapData mapdata) {
		if (world.worldProvider.worldType == mapdata.field_28162_d) {
			short c = 128;
			short c1 = 128;
			int i = 1 << mapdata.field_28161_e;
			int j = mapdata.field_28164_b;
			int k = mapdata.field_28163_c;
			int l = MathHelper.floor_double(entity.posX - (double) j) / i + c / 2;
			int i1 = MathHelper.floor_double(entity.posZ - (double) k) / i + c1 / 2;
			int j1 = 128 / i;
			if (world.worldProvider.hasNoSky) {
				j1 /= 2;
			}

			++mapdata.field_28159_g;

			for (int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
				if ((k1 & 15) == (mapdata.field_28159_g & 15)) {
					int l1 = 255;
					int i2 = 0;
					double d = 0.0D;

					for (int j2 = i1 - j1 - 1; j2 < i1 + j1; ++j2) {
						if (k1 >= 0 && j2 >= -1 && k1 < c && j2 < c1) {
							int k2 = k1 - l;
							int l2 = j2 - i1;
							boolean flag = k2 * k2 + l2 * l2 > (j1 - 2) * (j1 - 2);
							int i3 = (j / i + k1 - c / 2) * i;
							int j3 = (k / i + j2 - c1 / 2) * i;
							byte k3 = 0;
							byte l3 = 0;
							byte i4 = 0;
							int[] ai = new int[256];
							Chunk chunk = world.getChunkFromBlockCoords(i3, j3);

							int j4;
							for (j4 = 0; j4 < 8; ++j4) {
								world.getChunkFromBlockCoords(i3, j4, j3);
							}

							j4 = i3 & 15;
							int k4 = j3 & 15;
							int l4 = 0;
							double d1 = 0.0D;
							int k5;
							int i6;
							int d2;
							int j7;
							if (world.worldProvider.hasNoSky) {
								k5 = i3 + j3 * 231871;
								k5 = k5 * k5 * 31287121 + k5 * 11;
								if ((k5 >> 20 & 1) == 0) {
									ai[Block.dirt.blockID] += 10;
								} else {
									ai[Block.stone.blockID] += 10;
								}

								d1 = 100.0D;
							} else {
								for (k5 = 0; k5 < i; ++k5) {
									for (i6 = 0; i6 < i; ++i6) {
										d2 = chunk.getHeightValue(k5 + j4, i6 + k4) + 1;
										int l6 = 0;
										if (d2 > 1) {
											boolean byte0 = false;

											label175: while (true) {
												byte0 = true;
												l6 = chunk.getBlockID(k5 + j4, d2 - 1, i6 + k4);
												if (d2 > -64) {
													if (l6 == 0) {
														byte0 = false;
													} else if (d2 > 0 && l6 > 0
															&& Block.blocksList[l6].blockMaterial.materialMapColor == MapColor.airColor) {
														byte0 = false;
													}
												}

												if (!byte0) {
													--d2;
													l6 = chunk.getBlockID(k5 + j4, d2 - 1, i6 + k4);
												}

												if (byte0) {
													if (l6 == 0 || !Block.blocksList[l6].blockMaterial.getIsLiquid()) {
														break;
													}

													j7 = d2 - 1;
													boolean byte1 = false;

													while (true) {
														int i44 = chunk.getBlockID(k5 + j4, j7--, i6 + k4);
														++l4;
														if (j7 <= 0 || i44 == 0
																|| !Block.blocksList[i44].blockMaterial.getIsLiquid()) {
															break label175;
														}
													}
												}
											}
										}

										d1 += (double) d2 / (double) (i * i);
										++ai[l6];
									}
								}
							}

							l4 /= i * i;
							int i10000 = k3 / (i * i);
							i10000 = l3 / (i * i);
							i10000 = i4 / (i * i);
							k5 = 0;
							i6 = 0;

							for (d2 = 0; d2 < 256; ++d2) {
								if (ai[d2] > k5) {
									i6 = d2;
									k5 = ai[d2];
								}
							}

							double d42 = (d1 - d) * 4.0D / (double) (i + 4) + ((double) (k1 + j2 & 1) - 0.5D) * 0.4D;
							byte b43 = 1;
							if (d42 > 0.6D) {
								b43 = 2;
							}

							if (d42 < -0.6D) {
								b43 = 0;
							}

							j7 = 0;
							if (i6 > 0) {
								MapColor mapColor45 = Block.blocksList[i6].blockMaterial.materialMapColor;
								if (mapColor45 == MapColor.field_28187_n) {
									double byte2 = (double) l4 * 0.1D + (double) (k1 + j2 & 1) * 0.2D;
									b43 = 1;
									if (byte2 < 0.5D) {
										b43 = 2;
									}

									if (byte2 > 0.9D) {
										b43 = 0;
									}
								}

								j7 = mapColor45.field_28184_q;
							}

							d = d1;
							if (j2 >= 0 && k2 * k2 + l2 * l2 < j1 * j1 && (!flag || (k1 + j2 & 1) != 0)) {
								byte b46 = mapdata.field_28160_f[k1 + j2 * c];
								byte b47 = (byte) (j7 * 4 + b43);
								if (b46 != b47) {
									if (l1 > j2) {
										l1 = j2;
									}

									if (i2 < j2) {
										i2 = j2;
									}

									mapdata.field_28160_f[k1 + j2 * c] = b47;
								}
							}
						}
					}

					if (l1 <= i2) {
						mapdata.func_28153_a(k1, l1, i2);
					}
				}
			}

		}
	}

	public void func_28018_a(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
		if (!world.multiplayerWorld) {
			MapData mapdata = this.func_28023_a(itemstack, world);
			if (entity instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entity;
				mapdata.func_28155_a(entityplayer, itemstack);
			}

			if (flag) {
				this.func_28024_a(world, entity, mapdata);
			}

		}
	}

	public void func_28020_c(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		itemstack.setItemDamage(world.func_28104_b("map"));
		String s = "map_" + itemstack.getItemDamage();
		MapData mapdata = new MapData(s);
		world.func_28102_a(s, mapdata);
		mapdata.field_28164_b = MathHelper.floor_double(entityplayer.posX);
		mapdata.field_28163_c = MathHelper.floor_double(entityplayer.posZ);
		mapdata.field_28161_e = 3;
		mapdata.field_28162_d = (byte) world.worldProvider.worldType;
		mapdata.func_28146_a();
	}

	public Packet func_28022_b(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		byte[] abyte0 = this.func_28023_a(itemstack, world).func_28154_a(itemstack, world, entityplayer);
		return abyte0 == null ? null
				: new Packet131MapData((short) Item.field_28021_bb.shiftedIndex, (short) itemstack.getItemDamage(),
						abyte0);
	}
}
