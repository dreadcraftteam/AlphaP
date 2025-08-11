package net.minecraft_server.src;

import java.util.List;

public class EntityMinecart extends Entity implements IInventory {
	private ItemStack[] cargoItems;
	public int damageTaken;
	public int field_9167_b;
	public int forwardDirection;
	private boolean field_469_aj;
	public int minecartType;
	public int fuel;
	public double pushX;
	public double pushZ;
	private static final int[][][] field_468_ak = new int[][][] { { { 0, 0, -1 }, { 0, 0, 1 } },
			{ { -1, 0, 0 }, { 1, 0, 0 } }, { { -1, -1, 0 }, { 1, 0, 0 } }, { { -1, 0, 0 }, { 1, -1, 0 } },
			{ { 0, 0, -1 }, { 0, -1, 1 } }, { { 0, -1, -1 }, { 0, 0, 1 } }, { { 0, 0, 1 }, { 1, 0, 0 } },
			{ { 0, 0, 1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { 1, 0, 0 } } };
	private int field_9163_an;
	private double field_9162_ao;
	private double field_9161_ap;
	private double field_9160_aq;
	private double field_9159_ar;
	private double field_9158_as;

	public EntityMinecart(World world) {
		super(world);
		this.cargoItems = new ItemStack[36];
		this.damageTaken = 0;
		this.field_9167_b = 0;
		this.forwardDirection = 1;
		this.field_469_aj = false;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.7F);
		this.yOffset = this.height / 2.0F;
	}

	protected boolean func_25017_l() {
		return false;
	}

	protected void entityInit() {
	}

	public AxisAlignedBB func_89_d(Entity entity) {
		return entity.boundingBox;
	}

	public AxisAlignedBB getBoundingBox() {
		return null;
	}

	public boolean canBePushed() {
		return true;
	}

	public EntityMinecart(World world, double d, double d1, double d2, int i) {
		this(world);
		this.setPosition(d, d1 + (double) this.yOffset, d2);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = d;
		this.prevPosY = d1;
		this.prevPosZ = d2;
		this.minecartType = i;
	}

	public double getMountedYOffset() {
		return (double) this.height * 0.0D - (double) 0.3F;
	}

	public boolean attackEntityFrom(Entity entity, int i) {
		if (!this.worldObj.multiplayerWorld && !this.isDead) {
			this.forwardDirection = -this.forwardDirection;
			this.field_9167_b = 10;
			this.setBeenAttacked();
			this.damageTaken += i * 10;
			if (this.damageTaken > 40) {
				if (this.riddenByEntity != null) {
					this.riddenByEntity.mountEntity(this);
				}

				this.setEntityDead();
				this.dropItemWithOffset(Item.minecartEmpty.shiftedIndex, 1, 0.0F);
				if (this.minecartType == 1) {
					EntityMinecart entityminecart = this;

					for (int j = 0; j < entityminecart.getSizeInventory(); ++j) {
						ItemStack itemstack = entityminecart.getStackInSlot(j);
						if (itemstack != null) {
							float f = this.rand.nextFloat() * 0.8F + 0.1F;
							float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
							float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

							while (itemstack.stackSize > 0) {
								int k = this.rand.nextInt(21) + 10;
								if (k > itemstack.stackSize) {
									k = itemstack.stackSize;
								}

								itemstack.stackSize -= k;
								EntityItem entityitem = new EntityItem(this.worldObj, this.posX + (double) f,
										this.posY + (double) f1, this.posZ + (double) f2,
										new ItemStack(itemstack.itemID, k, itemstack.getItemDamage()));
								float f3 = 0.05F;
								entityitem.motionX = (double) ((float) this.rand.nextGaussian() * f3);
								entityitem.motionY = (double) ((float) this.rand.nextGaussian() * f3 + 0.2F);
								entityitem.motionZ = (double) ((float) this.rand.nextGaussian() * f3);
								this.worldObj.entityJoinedWorld(entityitem);
							}
						}
					}

					this.dropItemWithOffset(Block.chest.blockID, 1, 0.0F);
				} else if (this.minecartType == 2) {
					this.dropItemWithOffset(Block.stoneOvenIdle.blockID, 1, 0.0F);
				}
			}

			return true;
		} else {
			return true;
		}
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void setEntityDead() {
		for (int i = 0; i < this.getSizeInventory(); ++i) {
			ItemStack itemstack = this.getStackInSlot(i);
			if (itemstack != null) {
				float f = this.rand.nextFloat() * 0.8F + 0.1F;
				float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
				float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

				while (itemstack.stackSize > 0) {
					int j = this.rand.nextInt(21) + 10;
					if (j > itemstack.stackSize) {
						j = itemstack.stackSize;
					}

					itemstack.stackSize -= j;
					EntityItem entityitem = new EntityItem(this.worldObj, this.posX + (double) f,
							this.posY + (double) f1, this.posZ + (double) f2,
							new ItemStack(itemstack.itemID, j, itemstack.getItemDamage()));
					float f3 = 0.05F;
					entityitem.motionX = (double) ((float) this.rand.nextGaussian() * f3);
					entityitem.motionY = (double) ((float) this.rand.nextGaussian() * f3 + 0.2F);
					entityitem.motionZ = (double) ((float) this.rand.nextGaussian() * f3);
					this.worldObj.entityJoinedWorld(entityitem);
				}
			}
		}

		super.setEntityDead();
	}

	public void onUpdate() {
		if (this.field_9167_b > 0) {
			--this.field_9167_b;
		}

		if (this.damageTaken > 0) {
			--this.damageTaken;
		}

		double d5;
		if (this.worldObj.multiplayerWorld && this.field_9163_an > 0) {
			if (this.field_9163_an > 0) {
				double d46 = this.posX + (this.field_9162_ao - this.posX) / (double) this.field_9163_an;
				double d47 = this.posY + (this.field_9161_ap - this.posY) / (double) this.field_9163_an;
				double d3 = this.posZ + (this.field_9160_aq - this.posZ) / (double) this.field_9163_an;

				for (d5 = this.field_9159_ar - (double) this.rotationYaw; d5 < -180.0D; d5 += 360.0D) {
				}

				while (d5 >= 180.0D) {
					d5 -= 360.0D;
				}

				this.rotationYaw = (float) ((double) this.rotationYaw + d5 / (double) this.field_9163_an);
				this.rotationPitch = (float) ((double) this.rotationPitch
						+ (this.field_9158_as - (double) this.rotationPitch) / (double) this.field_9163_an);
				--this.field_9163_an;
				this.setPosition(d46, d47, d3);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			} else {
				this.setPosition(this.posX, this.posY, this.posZ);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			}

		} else {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.motionY -= (double) 0.04F;
			int i = MathHelper.floor_double(this.posX);
			int j = MathHelper.floor_double(this.posY);
			int k = MathHelper.floor_double(this.posZ);
			if (BlockRail.func_27029_g(this.worldObj, i, j - 1, k)) {
				--j;
			}

			double d2 = 0.4D;
			boolean flag = false;
			d5 = 2.0D / 256D;
			int l = this.worldObj.getBlockId(i, j, k);
			if (BlockRail.func_27030_c(l)) {
				Vec3D d6 = this.func_182_g(this.posX, this.posY, this.posZ);
				int i1 = this.worldObj.getBlockMetadata(i, j, k);
				this.posY = (double) j;
				boolean d7 = false;
				boolean flag2 = false;
				if (l == Block.railPowered.blockID) {
					d7 = (i1 & 8) != 0;
					flag2 = !d7;
				}

				if (((BlockRail) Block.blocksList[l]).func_27028_d()) {
					i1 &= 7;
				}

				if (i1 >= 2 && i1 <= 5) {
					this.posY = (double) (j + 1);
				}

				if (i1 == 2) {
					this.motionX -= d5;
				}

				if (i1 == 3) {
					this.motionX += d5;
				}

				if (i1 == 4) {
					this.motionZ += d5;
				}

				if (i1 == 5) {
					this.motionZ -= d5;
				}

				int[][] d8 = field_468_ak[i1];
				double d9 = (double) (d8[1][0] - d8[0][0]);
				double j1 = (double) (d8[1][2] - d8[0][2]);
				double d11 = Math.sqrt(d9 * d9 + j1 * j1);
				double d12 = this.motionX * d9 + this.motionZ * j1;
				if (d12 < 0.0D) {
					d9 = -d9;
					j1 = -j1;
				}

				double d13 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
				this.motionX = d13 * d9 / d11;
				this.motionZ = d13 * j1 / d11;
				double d17;
				if (flag2) {
					d17 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
					if (d17 < 0.03D) {
						this.motionX *= 0.0D;
						this.motionY *= 0.0D;
						this.motionZ *= 0.0D;
					} else {
						this.motionX *= 0.5D;
						this.motionY *= 0.0D;
						this.motionZ *= 0.5D;
					}
				}

				d17 = 0.0D;
				double d18 = (double) i + 0.5D + (double) d8[0][0] * 0.5D;
				double d19 = (double) k + 0.5D + (double) d8[0][2] * 0.5D;
				double d20 = (double) i + 0.5D + (double) d8[1][0] * 0.5D;
				double d21 = (double) k + 0.5D + (double) d8[1][2] * 0.5D;
				d9 = d20 - d18;
				j1 = d21 - d19;
				double d23;
				double d25;
				double vec3d1;
				if (d9 == 0.0D) {
					this.posX = (double) i + 0.5D;
					d17 = this.posZ - (double) k;
				} else if (j1 == 0.0D) {
					this.posZ = (double) k + 0.5D;
					d17 = this.posX - (double) i;
				} else {
					d23 = this.posX - d18;
					d25 = this.posZ - d19;
					vec3d1 = (d23 * d9 + d25 * j1) * 2.0D;
					d17 = vec3d1;
				}

				this.posX = d18 + d9 * d17;
				this.posZ = d19 + j1 * d17;
				this.setPosition(this.posX, this.posY + (double) this.yOffset, this.posZ);
				d23 = this.motionX;
				d25 = this.motionZ;
				if (this.riddenByEntity != null) {
					d23 *= 0.75D;
					d25 *= 0.75D;
				}

				if (d23 < -d2) {
					d23 = -d2;
				}

				if (d23 > d2) {
					d23 = d2;
				}

				if (d25 < -d2) {
					d25 = -d2;
				}

				if (d25 > d2) {
					d25 = d2;
				}

				this.moveEntity(d23, 0.0D, d25);
				if (d8[0][1] != 0 && MathHelper.floor_double(this.posX) - i == d8[0][0]
						&& MathHelper.floor_double(this.posZ) - k == d8[0][2]) {
					this.setPosition(this.posX, this.posY + (double) d8[0][1], this.posZ);
				} else if (d8[1][1] != 0 && MathHelper.floor_double(this.posX) - i == d8[1][0]
						&& MathHelper.floor_double(this.posZ) - k == d8[1][2]) {
					this.setPosition(this.posX, this.posY + (double) d8[1][1], this.posZ);
				}

				if (this.riddenByEntity != null) {
					this.motionX *= (double) 0.997F;
					this.motionY *= 0.0D;
					this.motionZ *= (double) 0.997F;
				} else {
					if (this.minecartType == 2) {
						vec3d1 = (double) MathHelper.sqrt_double(this.pushX * this.pushX + this.pushZ * this.pushZ);
						if (vec3d1 > 0.01D) {
							flag = true;
							this.pushX /= vec3d1;
							this.pushZ /= vec3d1;
							double l1 = 0.04D;
							this.motionX *= (double) 0.8F;
							this.motionY *= 0.0D;
							this.motionZ *= (double) 0.8F;
							this.motionX += this.pushX * l1;
							this.motionZ += this.pushZ * l1;
						} else {
							this.motionX *= (double) 0.9F;
							this.motionY *= 0.0D;
							this.motionZ *= (double) 0.9F;
						}
					}

					this.motionX *= (double) 0.96F;
					this.motionY *= 0.0D;
					this.motionZ *= (double) 0.96F;
				}

				Vec3D vec3D52 = this.func_182_g(this.posX, this.posY, this.posZ);
				double d31;
				if (vec3D52 != null && d6 != null) {
					double k1 = (d6.yCoord - vec3D52.yCoord) * 0.05D;
					d31 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
					if (d31 > 0.0D) {
						this.motionX = this.motionX / d31 * (d31 + k1);
						this.motionZ = this.motionZ / d31 * (d31 + k1);
					}

					this.setPosition(this.posX, vec3D52.yCoord, this.posZ);
				}

				int i53 = MathHelper.floor_double(this.posX);
				int i54 = MathHelper.floor_double(this.posZ);
				if (i53 != i || i54 != k) {
					d31 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
					this.motionX = d31 * (double) (i53 - i);
					this.motionZ = d31 * (double) (i54 - k);
				}

				if (this.minecartType == 2) {
					d31 = (double) MathHelper.sqrt_double(this.pushX * this.pushX + this.pushZ * this.pushZ);
					if (d31 > 0.01D && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001D) {
						this.pushX /= d31;
						this.pushZ /= d31;
						if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0D) {
							this.pushX = 0.0D;
							this.pushZ = 0.0D;
						} else {
							this.pushX = this.motionX;
							this.pushZ = this.motionZ;
						}
					}
				}

				if (d7) {
					d31 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
					if (d31 > 0.01D) {
						double d32 = 0.06D;
						this.motionX += this.motionX / d31 * d32;
						this.motionZ += this.motionZ / d31 * d32;
					} else if (i1 == 1) {
						if (this.worldObj.isBlockNormalCube(i - 1, j, k)) {
							this.motionX = 0.02D;
						} else if (this.worldObj.isBlockNormalCube(i + 1, j, k)) {
							this.motionX = -0.02D;
						}
					} else if (i1 == 0) {
						if (this.worldObj.isBlockNormalCube(i, j, k - 1)) {
							this.motionZ = 0.02D;
						} else if (this.worldObj.isBlockNormalCube(i, j, k + 1)) {
							this.motionZ = -0.02D;
						}
					}
				}
			} else {
				if (this.motionX < -d2) {
					this.motionX = -d2;
				}

				if (this.motionX > d2) {
					this.motionX = d2;
				}

				if (this.motionZ < -d2) {
					this.motionZ = -d2;
				}

				if (this.motionZ > d2) {
					this.motionZ = d2;
				}

				if (this.onGround) {
					this.motionX *= 0.5D;
					this.motionY *= 0.5D;
					this.motionZ *= 0.5D;
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				if (!this.onGround) {
					this.motionX *= (double) 0.95F;
					this.motionY *= (double) 0.95F;
					this.motionZ *= (double) 0.95F;
				}
			}

			this.rotationPitch = 0.0F;
			double d48 = this.prevPosX - this.posX;
			double d49 = this.prevPosZ - this.posZ;
			if (d48 * d48 + d49 * d49 > 0.001D) {
				this.rotationYaw = (float) (Math.atan2(d49, d48) * 180.0D / Math.PI);
				if (this.field_469_aj) {
					this.rotationYaw += 180.0F;
				}
			}

			double d50;
			for (d50 = (double) (this.rotationYaw - this.prevRotationYaw); d50 >= 180.0D; d50 -= 360.0D) {
			}

			while (d50 < -180.0D) {
				d50 += 360.0D;
			}

			if (d50 < -170.0D || d50 >= 170.0D) {
				this.rotationYaw += 180.0F;
				this.field_469_aj = !this.field_469_aj;
			}

			this.setRotation(this.rotationYaw, this.rotationPitch);
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
					this.boundingBox.expand((double) 0.2F, 0.0D, (double) 0.2F));
			if (list != null && list.size() > 0) {
				for (int i51 = 0; i51 < list.size(); ++i51) {
					Entity entity = (Entity) list.get(i51);
					if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityMinecart) {
						entity.applyEntityCollision(this);
					}
				}
			}

			if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
				this.riddenByEntity = null;
			}

			if (flag && this.rand.nextInt(4) == 0) {
				--this.fuel;
				if (this.fuel < 0) {
					this.pushX = this.pushZ = 0.0D;
				}

				this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
			}

		}
	}

	public Vec3D func_182_g(double d, double d1, double d2) {
		int i = MathHelper.floor_double(d);
		int j = MathHelper.floor_double(d1);
		int k = MathHelper.floor_double(d2);
		if (BlockRail.func_27029_g(this.worldObj, i, j - 1, k)) {
			--j;
		}

		int l = this.worldObj.getBlockId(i, j, k);
		if (BlockRail.func_27030_c(l)) {
			int i1 = this.worldObj.getBlockMetadata(i, j, k);
			d1 = (double) j;
			if (((BlockRail) Block.blocksList[l]).func_27028_d()) {
				i1 &= 7;
			}

			if (i1 >= 2 && i1 <= 5) {
				d1 = (double) (j + 1);
			}

			int[][] ai = field_468_ak[i1];
			double d3 = 0.0D;
			double d4 = (double) i + 0.5D + (double) ai[0][0] * 0.5D;
			double d5 = (double) j + 0.5D + (double) ai[0][1] * 0.5D;
			double d6 = (double) k + 0.5D + (double) ai[0][2] * 0.5D;
			double d7 = (double) i + 0.5D + (double) ai[1][0] * 0.5D;
			double d8 = (double) j + 0.5D + (double) ai[1][1] * 0.5D;
			double d9 = (double) k + 0.5D + (double) ai[1][2] * 0.5D;
			double d10 = d7 - d4;
			double d11 = (d8 - d5) * 2.0D;
			double d12 = d9 - d6;
			if (d10 == 0.0D) {
				d = (double) i + 0.5D;
				d3 = d2 - (double) k;
			} else if (d12 == 0.0D) {
				d2 = (double) k + 0.5D;
				d3 = d - (double) i;
			} else {
				double d13 = d - d4;
				double d14 = d2 - d6;
				double d15 = (d13 * d10 + d14 * d12) * 2.0D;
				d3 = d15;
			}

			d = d4 + d10 * d3;
			d1 = d5 + d11 * d3;
			d2 = d6 + d12 * d3;
			if (d11 < 0.0D) {
				++d1;
			}

			if (d11 > 0.0D) {
				d1 += 0.5D;
			}

			return Vec3D.createVector(d, d1, d2);
		} else {
			return null;
		}
	}

	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("Type", this.minecartType);
		if (this.minecartType == 2) {
			nbttagcompound.setDouble("PushX", this.pushX);
			nbttagcompound.setDouble("PushZ", this.pushZ);
			nbttagcompound.setShort("Fuel", (short) this.fuel);
		} else if (this.minecartType == 1) {
			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 0; i < this.cargoItems.length; ++i) {
				if (this.cargoItems[i] != null) {
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("Slot", (byte) i);
					this.cargoItems[i].writeToNBT(nbttagcompound1);
					nbttaglist.setTag(nbttagcompound1);
				}
			}

			nbttagcompound.setTag("Items", nbttaglist);
		}

	}

	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.minecartType = nbttagcompound.getInteger("Type");
		if (this.minecartType == 2) {
			this.pushX = nbttagcompound.getDouble("PushX");
			this.pushZ = nbttagcompound.getDouble("PushZ");
			this.fuel = nbttagcompound.getShort("Fuel");
		} else if (this.minecartType == 1) {
			NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
			this.cargoItems = new ItemStack[this.getSizeInventory()];

			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
				int j = nbttagcompound1.getByte("Slot") & 255;
				if (j >= 0 && j < this.cargoItems.length) {
					this.cargoItems[j] = new ItemStack(nbttagcompound1);
				}
			}
		}

	}

	public void applyEntityCollision(Entity entity) {
		if (!this.worldObj.multiplayerWorld) {
			if (entity != this.riddenByEntity) {
				if (entity instanceof EntityLiving && !(entity instanceof EntityPlayer) && this.minecartType == 0
						&& this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D
						&& this.riddenByEntity == null && entity.ridingEntity == null) {
					entity.mountEntity(this);
				}

				double d = entity.posX - this.posX;
				double d1 = entity.posZ - this.posZ;
				double d2 = d * d + d1 * d1;
				if (d2 >= 9.999999747378752E-5D) {
					d2 = (double) MathHelper.sqrt_double(d2);
					d /= d2;
					d1 /= d2;
					double d3 = 1.0D / d2;
					if (d3 > 1.0D) {
						d3 = 1.0D;
					}

					d *= d3;
					d1 *= d3;
					d *= (double) 0.1F;
					d1 *= (double) 0.1F;
					d *= (double) (1.0F - this.entityCollisionReduction);
					d1 *= (double) (1.0F - this.entityCollisionReduction);
					d *= 0.5D;
					d1 *= 0.5D;
					if (entity instanceof EntityMinecart) {
						double d4 = entity.posX - this.posX;
						double d5 = entity.posZ - this.posZ;
						double d6 = d4 * entity.motionZ + d5 * entity.prevPosX;
						d6 *= d6;
						if (d6 > 5.0D) {
							return;
						}

						double d7 = entity.motionX + this.motionX;
						double d8 = entity.motionZ + this.motionZ;
						if (((EntityMinecart) entity).minecartType == 2 && this.minecartType != 2) {
							this.motionX *= (double) 0.2F;
							this.motionZ *= (double) 0.2F;
							this.addVelocity(entity.motionX - d, 0.0D, entity.motionZ - d1);
							entity.motionX *= (double) 0.7F;
							entity.motionZ *= (double) 0.7F;
						} else if (((EntityMinecart) entity).minecartType != 2 && this.minecartType == 2) {
							entity.motionX *= (double) 0.2F;
							entity.motionZ *= (double) 0.2F;
							entity.addVelocity(this.motionX + d, 0.0D, this.motionZ + d1);
							this.motionX *= (double) 0.7F;
							this.motionZ *= (double) 0.7F;
						} else {
							d7 /= 2.0D;
							d8 /= 2.0D;
							this.motionX *= (double) 0.2F;
							this.motionZ *= (double) 0.2F;
							this.addVelocity(d7 - d, 0.0D, d8 - d1);
							entity.motionX *= (double) 0.2F;
							entity.motionZ *= (double) 0.2F;
							entity.addVelocity(d7 + d, 0.0D, d8 + d1);
						}
					} else {
						this.addVelocity(-d, 0.0D, -d1);
						entity.addVelocity(d / 4.0D, 0.0D, d1 / 4.0D);
					}
				}

			}
		}
	}

	public int getSizeInventory() {
		return 27;
	}

	public ItemStack getStackInSlot(int i) {
		return this.cargoItems[i];
	}

	public ItemStack decrStackSize(int i, int j) {
		if (this.cargoItems[i] != null) {
			ItemStack itemstack1;
			if (this.cargoItems[i].stackSize <= j) {
				itemstack1 = this.cargoItems[i];
				this.cargoItems[i] = null;
				return itemstack1;
			} else {
				itemstack1 = this.cargoItems[i].splitStack(j);
				if (this.cargoItems[i].stackSize == 0) {
					this.cargoItems[i] = null;
				}

				return itemstack1;
			}
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.cargoItems[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

	}

	public String getInvName() {
		return "Minecart";
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void onInventoryChanged() {
	}

	public boolean interact(EntityPlayer entityplayer) {
		if (this.minecartType == 0) {
			if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer
					&& this.riddenByEntity != entityplayer) {
				return true;
			}

			if (!this.worldObj.multiplayerWorld) {
				entityplayer.mountEntity(this);
			}
		} else if (this.minecartType == 1) {
			if (!this.worldObj.multiplayerWorld) {
				entityplayer.displayGUIChest(this);
			}
		} else if (this.minecartType == 2) {
			ItemStack itemstack = entityplayer.inventory.getCurrentItem();
			if (itemstack != null && itemstack.itemID == Item.coal.shiftedIndex) {
				if (--itemstack.stackSize == 0) {
					entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem,
							(ItemStack) null);
				}

				this.fuel += 1200;
			}

			this.pushX = this.posX - entityplayer.posX;
			this.pushZ = this.posZ - entityplayer.posZ;
		}

		return true;
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return this.isDead ? false : entityplayer.getDistanceSqToEntity(this) <= 64.0D;
	}
}
