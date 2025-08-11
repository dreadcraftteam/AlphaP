package net.minecraft_server.src;

import java.util.List;

public class EntityFish extends Entity {
	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private int inTile = 0;
	private boolean inGround = false;
	public int shake = 0;
	public EntityPlayer angler;
	private int ticksInGround;
	private int ticksInAir = 0;
	private int ticksCatchable = 0;
	public Entity bobber = null;
	private int field_6149_an;
	private double field_6148_ao;
	private double field_6147_ap;
	private double field_6146_aq;
	private double field_6145_ar;
	private double field_6144_as;

	public EntityFish(World world) {
		super(world);
		this.setSize(0.25F, 0.25F);
		this.field_28008_bI = true;
	}

	public EntityFish(World world, EntityPlayer entityplayer) {
		super(world);
		this.field_28008_bI = true;
		this.angler = entityplayer;
		this.angler.fishEntity = this;
		this.setSize(0.25F, 0.25F);
		this.setLocationAndAngles(entityplayer.posX, entityplayer.posY + 1.62D - (double) entityplayer.yOffset,
				entityplayer.posZ, entityplayer.rotationYaw, entityplayer.rotationPitch);
		this.posX -= (double) (MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F) * 0.16F);
		this.posY -= (double) 0.1F;
		this.posZ -= (double) (MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		float f = 0.4F;
		this.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F)
				* MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F) * f);
		this.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F)
				* MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F) * f);
		this.motionY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F * 3.141593F) * f);
		this.func_6142_a(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
	}

	protected void entityInit() {
	}

	public void func_6142_a(double d, double d1, double d2, float f, float f1) {
		float f2 = MathHelper.sqrt_double(d * d + d1 * d1 + d2 * d2);
		d /= (double) f2;
		d1 /= (double) f2;
		d2 /= (double) f2;
		d += this.rand.nextGaussian() * (double) 0.0075F * (double) f1;
		d1 += this.rand.nextGaussian() * (double) 0.0075F * (double) f1;
		d2 += this.rand.nextGaussian() * (double) 0.0075F * (double) f1;
		d *= (double) f;
		d1 *= (double) f;
		d2 *= (double) f;
		this.motionX = d;
		this.motionY = d1;
		this.motionZ = d2;
		float f3 = MathHelper.sqrt_double(d * d + d2 * d2);
		this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(d, d2) * 180.0D / (double) (float) Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(d1, (double) f3) * 180.0D
				/ (double) (float) Math.PI);
		this.ticksInGround = 0;
	}

	public void onUpdate() {
		super.onUpdate();
		if (this.field_6149_an > 0) {
			double d21 = this.posX + (this.field_6148_ao - this.posX) / (double) this.field_6149_an;
			double d22 = this.posY + (this.field_6147_ap - this.posY) / (double) this.field_6149_an;
			double d23 = this.posZ + (this.field_6146_aq - this.posZ) / (double) this.field_6149_an;

			double d4;
			for (d4 = this.field_6145_ar - (double) this.rotationYaw; d4 < -180.0D; d4 += 360.0D) {
			}

			while (d4 >= 180.0D) {
				d4 -= 360.0D;
			}

			this.rotationYaw = (float) ((double) this.rotationYaw + d4 / (double) this.field_6149_an);
			this.rotationPitch = (float) ((double) this.rotationPitch
					+ (this.field_6144_as - (double) this.rotationPitch) / (double) this.field_6149_an);
			--this.field_6149_an;
			this.setPosition(d21, d22, d23);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		} else {
			if (!this.worldObj.multiplayerWorld) {
				ItemStack vec3d = this.angler.getCurrentEquippedItem();
				if (this.angler.isDead || !this.angler.isEntityAlive() || vec3d == null
						|| vec3d.getItem() != Item.fishingRod || this.getDistanceSqToEntity(this.angler) > 1024.0D) {
					this.setEntityDead();
					this.angler.fishEntity = null;
					return;
				}

				if (this.bobber != null) {
					if (!this.bobber.isDead) {
						this.posX = this.bobber.posX;
						this.posY = this.bobber.boundingBox.minY + (double) this.bobber.height * 0.8D;
						this.posZ = this.bobber.posZ;
						return;
					}

					this.bobber = null;
				}
			}

			if (this.shake > 0) {
				--this.shake;
			}

			if (this.inGround) {
				int i19 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
				if (i19 == this.inTile) {
					++this.ticksInGround;
					if (this.ticksInGround == 1200) {
						this.setEntityDead();
					}

					return;
				}

				this.inGround = false;
				this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			} else {
				++this.ticksInAir;
			}

			Vec3D vec3D20 = Vec3D.createVector(this.posX, this.posY, this.posZ);
			Vec3D vec3d1 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY,
					this.posZ + this.motionZ);
			MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec3D20, vec3d1);
			vec3D20 = Vec3D.createVector(this.posX, this.posY, this.posZ);
			vec3d1 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			if (movingobjectposition != null) {
				vec3d1 = Vec3D.createVector(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord,
						movingobjectposition.hitVec.zCoord);
			}

			Entity entity = null;
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
					this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double d3 = 0.0D;

			double d7;
			for (int f = 0; f < list.size(); ++f) {
				Entity f1 = (Entity) list.get(f);
				if (f1.canBeCollidedWith() && (f1 != this.angler || this.ticksInAir >= 5)) {
					float k = 0.3F;
					AxisAlignedBB d5 = f1.boundingBox.expand((double) k, (double) k, (double) k);
					MovingObjectPosition movingobjectposition1 = d5.func_706_a(vec3D20, vec3d1);
					if (movingobjectposition1 != null) {
						d7 = vec3D20.distanceTo(movingobjectposition1.hitVec);
						if (d7 < d3 || d3 == 0.0D) {
							entity = f1;
							d3 = d7;
						}
					}
				}
			}

			if (entity != null) {
				movingobjectposition = new MovingObjectPosition(entity);
			}

			if (movingobjectposition != null) {
				if (movingobjectposition.entityHit != null) {
					if (movingobjectposition.entityHit.attackEntityFrom(this.angler, 0)) {
						this.bobber = movingobjectposition.entityHit;
					}
				} else {
					this.inGround = true;
				}
			}

			if (!this.inGround) {
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				float f24 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
				this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / (double) (float) Math.PI);

				for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f24) * 180.0D
						/ (double) (float) Math.PI); this.rotationPitch
								- this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
				}

				while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
					this.prevRotationPitch += 360.0F;
				}

				while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
					this.prevRotationYaw -= 360.0F;
				}

				while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
					this.prevRotationYaw += 360.0F;
				}

				this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
				this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
				float f25 = 0.92F;
				if (this.onGround || this.isCollidedHorizontally) {
					f25 = 0.5F;
				}

				byte b26 = 5;
				double d27 = 0.0D;

				for (int i28 = 0; i28 < b26; ++i28) {
					double f3 = this.boundingBox.minY
							+ (this.boundingBox.maxY - this.boundingBox.minY) * (double) (i28 + 0) / (double) b26
							- 0.125D + 0.125D;
					double f5 = this.boundingBox.minY
							+ (this.boundingBox.maxY - this.boundingBox.minY) * (double) (i28 + 1) / (double) b26
							- 0.125D + 0.125D;
					AxisAlignedBB axisalignedbb1 = AxisAlignedBB.getBoundingBoxFromPool(this.boundingBox.minX, f3,
							this.boundingBox.minZ, this.boundingBox.maxX, f5, this.boundingBox.maxZ);
					if (this.worldObj.isAABBInMaterial(axisalignedbb1, Material.water)) {
						d27 += 1.0D / (double) b26;
					}
				}

				if (d27 > 0.0D) {
					if (this.ticksCatchable > 0) {
						--this.ticksCatchable;
					} else {
						short s29 = 500;
						if (this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX),
								MathHelper.floor_double(this.posY) + 1, MathHelper.floor_double(this.posZ))) {
							s29 = 300;
						}

						if (this.rand.nextInt(s29) == 0) {
							this.ticksCatchable = this.rand.nextInt(30) + 10;
							this.motionY -= (double) 0.2F;
							this.worldObj.playSoundAtEntity(this, "random.splash", 0.25F,
									1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
							float f30 = (float) MathHelper.floor_double(this.boundingBox.minY);

							int j1;
							float f7;
							float f31;
							for (j1 = 0; (float) j1 < 1.0F + this.width * 20.0F; ++j1) {
								f31 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
								f7 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
								this.worldObj.spawnParticle("bubble", this.posX + (double) f31, (double) (f30 + 1.0F),
										this.posZ + (double) f7, this.motionX,
										this.motionY - (double) (this.rand.nextFloat() * 0.2F), this.motionZ);
							}

							for (j1 = 0; (float) j1 < 1.0F + this.width * 20.0F; ++j1) {
								f31 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
								f7 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
								this.worldObj.spawnParticle("splash", this.posX + (double) f31, (double) (f30 + 1.0F),
										this.posZ + (double) f7, this.motionX, this.motionY, this.motionZ);
							}
						}
					}
				}

				if (this.ticksCatchable > 0) {
					this.motionY -= (double) (this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat())
							* 0.2D;
				}

				d7 = d27 * 2.0D - 1.0D;
				this.motionY += (double) 0.04F * d7;
				if (d27 > 0.0D) {
					f25 = (float) ((double) f25 * 0.9D);
					this.motionY *= 0.8D;
				}

				this.motionX *= (double) f25;
				this.motionY *= (double) f25;
				this.motionZ *= (double) f25;
				this.setPosition(this.posX, this.posY, this.posZ);
			}
		}
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("xTile", (short) this.xTile);
		nbttagcompound.setShort("yTile", (short) this.yTile);
		nbttagcompound.setShort("zTile", (short) this.zTile);
		nbttagcompound.setByte("inTile", (byte) this.inTile);
		nbttagcompound.setByte("shake", (byte) this.shake);
		nbttagcompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.xTile = nbttagcompound.getShort("xTile");
		this.yTile = nbttagcompound.getShort("yTile");
		this.zTile = nbttagcompound.getShort("zTile");
		this.inTile = nbttagcompound.getByte("inTile") & 255;
		this.shake = nbttagcompound.getByte("shake") & 255;
		this.inGround = nbttagcompound.getByte("inGround") == 1;
	}

	public int catchFish() {
		byte byte0 = 0;
		if (this.bobber != null) {
			double entityitem = this.angler.posX - this.posX;
			double d2 = this.angler.posY - this.posY;
			double d4 = this.angler.posZ - this.posZ;
			double d6 = (double) MathHelper.sqrt_double(entityitem * entityitem + d2 * d2 + d4 * d4);
			double d8 = 0.1D;
			this.bobber.motionX += entityitem * d8;
			this.bobber.motionY += d2 * d8 + (double) MathHelper.sqrt_double(d6) * 0.08D;
			this.bobber.motionZ += d4 * d8;
			byte0 = 3;
		} else if (this.ticksCatchable > 0) {
			EntityItem entityitem1 = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ,
					new ItemStack(Item.fishRaw));
			double d1 = this.angler.posX - this.posX;
			double d3 = this.angler.posY - this.posY;
			double d5 = this.angler.posZ - this.posZ;
			double d7 = (double) MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5);
			double d9 = 0.1D;
			entityitem1.motionX = d1 * d9;
			entityitem1.motionY = d3 * d9 + (double) MathHelper.sqrt_double(d7) * 0.08D;
			entityitem1.motionZ = d5 * d9;
			this.worldObj.entityJoinedWorld(entityitem1);
			byte0 = 1;
		}

		if (this.inGround) {
			byte0 = 2;
		}

		this.setEntityDead();
		this.angler.fishEntity = null;
		return byte0;
	}
}
