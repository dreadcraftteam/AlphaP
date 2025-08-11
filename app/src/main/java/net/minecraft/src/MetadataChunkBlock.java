package net.minecraft.src;

public class MetadataChunkBlock {
	public final EnumSkyBlock lightType;
	public int xStart;
	public int yStart;
	public int zStart;
	public int xEnd;
	public int yEnd;
	public int zEnd;

	public MetadataChunkBlock(EnumSkyBlock enumskyblock, int i, int j, int k, int l, int i1, int j1) {
		this.lightType = enumskyblock;
		this.xStart = i;
		this.yStart = j;
		this.zStart = k;
		this.xEnd = l;
		this.yEnd = i1;
		this.zEnd = j1;
	}

	public void func_4127_a(World world) {
		int xSize = this.xEnd - this.xStart + 1;
		int ySize = this.yEnd - this.yStart + 1;
		int zSize = this.zEnd - this.zStart + 1;
		int volume = xSize * ySize * zSize;
		if(volume > 32768) {
			System.out.println("Light too large, skipping!");
		} else {
			int i1 = 0;
			int j1 = 0;
			boolean flag = false;
			boolean flag1 = false;

			for(int x = this.xStart; x <= this.xEnd; ++x) {
				for(int z = this.zStart; z <= this.zEnd; ++z) {
					int xChunk = x >> 4;
					int zChunk = z >> 4;
					boolean flag2 = false;
					int savedLight;
					if(flag && xChunk == i1 && zChunk == j1) {
						flag2 = flag1;
					} else {
						flag2 = world.checkChunksExist(x - 1, this.yStart - 1, z - 1, x + 1, this.yEnd + 1, z + 1);
						if(flag2) {
							Chunk y = world.getChunkFromChunkCoords(x >> 4, z >> 4);
							if(y.func_21167_h()) {
								flag2 = false;
							}
						} else if(ySize > 50) {
							boolean z27 = false;
							savedLight = this.yStart - 1;

							for(savedLight &= 15; savedLight < this.yEnd; savedLight += 16) {
								if(world.checkChunksExist(x - 1, savedLight, z - 1, x + 1, savedLight, z + 1)) {
									if(!z27) {
										if(savedLight > this.yStart) {
											this.yStart = savedLight;
										}

										z27 = true;
									}
								} else if(z27) {
									if(savedLight < this.yEnd) {
										this.yEnd = savedLight;
									}
									break;
								}
							}

							flag2 = this.yStart < this.yEnd;
						}

						flag1 = flag2;
						i1 = xChunk;
						j1 = zChunk;
					}

					if(flag2) {
						for(int i28 = this.yStart; i28 <= this.yEnd; ++i28) {
							savedLight = world.getSavedLightValue(this.lightType, x, i28, z);
							boolean lightVal = false;
							int blockId = world.getBlockId(x, i28, z);
							int blockOpacity = Block.lightOpacity[blockId];
							if(blockOpacity == 0) {
								blockOpacity = 1;
							}

							int minLightVal = 0;
							if(this.lightType == EnumSkyBlock.Sky) {
								if(world.canExistingBlockSeeTheSky(x, i28, z)) {
									minLightVal = 15;
								}
							} else if(this.lightType == EnumSkyBlock.Block) {
								minLightVal = Block.lightValue[blockId];
							}

							int j4;
							int i29;
							if(blockOpacity >= 15 && minLightVal == 0) {
								i29 = 0;
							} else {
								j4 = world.getSavedLightValue(this.lightType, x - 1, i28, z);
								int k4 = world.getSavedLightValue(this.lightType, x + 1, i28, z);
								int l4 = world.getSavedLightValue(this.lightType, x, i28 - 1, z);
								int i5 = world.getSavedLightValue(this.lightType, x, i28 + 1, z);
								int j5 = world.getSavedLightValue(this.lightType, x, i28, z - 1);
								int k5 = world.getSavedLightValue(this.lightType, x, i28, z + 1);
								i29 = j4;
								if(k4 > j4) {
									i29 = k4;
								}

								if(l4 > i29) {
									i29 = l4;
								}

								if(i5 > i29) {
									i29 = i5;
								}

								if(j5 > i29) {
									i29 = j5;
								}

								if(k5 > i29) {
									i29 = k5;
								}

								i29 -= blockOpacity;
								if(i29 < 0) {
									i29 = 0;
								}

								if(minLightVal > i29) {
									i29 = minLightVal;
								}
							}

							if(savedLight != i29) {
								world.setLightValue(this.lightType, x, i28, z, i29);
								j4 = i29 - 1;
								if(j4 < 0) {
									j4 = 0;
								}

								world.neighborLightPropagationChanged(this.lightType, x - 1, i28, z, j4);
								world.neighborLightPropagationChanged(this.lightType, x, i28 - 1, z, j4);
								world.neighborLightPropagationChanged(this.lightType, x, i28, z - 1, j4);
								if(x + 1 >= this.xEnd) {
									world.neighborLightPropagationChanged(this.lightType, x + 1, i28, z, j4);
								}

								if(i28 + 1 >= this.yEnd) {
									world.neighborLightPropagationChanged(this.lightType, x, i28 + 1, z, j4);
								}

								if(z + 1 >= this.zEnd) {
									world.neighborLightPropagationChanged(this.lightType, x, i28, z + 1, j4);
								}
							}
						}
					}
				}
			}

		}
	}

	public boolean func_866_a(int i, int j, int k, int l, int i1, int j1) {
		if(i >= this.xStart && j >= this.yStart && k >= this.zStart && l <= this.xEnd && i1 <= this.yEnd && j1 <= this.zEnd) {
			return true;
		} else {
			byte k1 = 1;
			if(i >= this.xStart - k1 && j >= this.yStart - k1 && k >= this.zStart - k1 && l <= this.xEnd + k1 && i1 <= this.yEnd + k1 && j1 <= this.zEnd + k1) {
				int l1 = this.xEnd - this.xStart;
				int i2 = this.yEnd - this.yStart;
				int j2 = this.zEnd - this.zStart;
				if(i > this.xStart) {
					i = this.xStart;
				}

				if(j > this.yStart) {
					j = this.yStart;
				}

				if(k > this.zStart) {
					k = this.zStart;
				}

				if(l < this.xEnd) {
					l = this.xEnd;
				}

				if(i1 < this.yEnd) {
					i1 = this.yEnd;
				}

				if(j1 < this.zEnd) {
					j1 = this.zEnd;
				}

				int k2 = l - i;
				int l2 = i1 - j;
				int i3 = j1 - k;
				int j3 = l1 * i2 * j2;
				int k3 = k2 * l2 * i3;
				if(k3 - j3 <= 2) {
					this.xStart = i;
					this.yStart = j;
					this.zStart = k;
					this.xEnd = l;
					this.yEnd = i1;
					this.zEnd = j1;
					return true;
				}
			}

			return false;
		}
	}
}
