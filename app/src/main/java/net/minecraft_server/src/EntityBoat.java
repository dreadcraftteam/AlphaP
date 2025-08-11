package net.minecraft_server.src;

import java.util.List;

public class EntityBoat extends Entity {
	public int damageTaken;
	public int field_9177_b;
	public int forwardDirection;
	private int field_9176_d;
	private double field_9174_e;
	private double field_9172_f;
	private double field_9175_aj;
	private double field_9173_ak;
	private double field_9171_al;

	public EntityBoat(World world) {
		super(world);
		this.damageTaken = 0;
		this.field_9177_b = 0;
		this.forwardDirection = 1;
		this.preventEntitySpawning = true;
		this.setSize(1.5F, 0.6F);
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
		return this.boundingBox;
	}

	public boolean canBePushed() {
		return true;
	}

	public EntityBoat(World world, double d, double d1, double d2) {
		this(world);
		this.setPosition(d, d1 + (double) this.yOffset, d2);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = d;
		this.prevPosY = d1;
		this.prevPosZ = d2;
	}

	public double getMountedYOffset() {
		return (double) this.height * 0.0D - (double) 0.3F;
	}

	public boolean attackEntityFrom(Entity entity, int i) {
		if (!this.worldObj.multiplayerWorld && !this.isDead) {
			this.forwardDirection = -this.forwardDirection;
			this.field_9177_b = 10;
			this.damageTaken += i * 10;
			this.setBeenAttacked();
			if (this.damageTaken > 40) {
				if (this.riddenByEntity != null) {
					this.riddenByEntity.mountEntity(this);
				}

				int k;
				for (k = 0; k < 3; ++k) {
					this.dropItemWithOffset(Block.planks.blockID, 1, 0.0F);
				}

				for (k = 0; k < 2; ++k) {
					this.dropItemWithOffset(Item.stick.shiftedIndex, 1, 0.0F);
				}

				this.setEntityDead();
			}

			return true;
		} else {
			return true;
		}
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void onUpdate() {
		super.onUpdate();
		if (this.field_9177_b > 0) {
			--this.field_9177_b;
		}

		if (this.damageTaken > 0) {
			--this.damageTaken;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		byte i = 5;
		double d = 0.0D;

		for (int d4 = 0; d4 < i; ++d4) {
			double d5 = this.boundingBox.minY
					+ (this.boundingBox.maxY - this.boundingBox.minY) * (double) (d4 + 0) / (double) i - 0.125D;
			double d9 = this.boundingBox.minY
					+ (this.boundingBox.maxY - this.boundingBox.minY) * (double) (d4 + 1) / (double) i - 0.125D;
			AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBoxFromPool(this.boundingBox.minX, d5,
					this.boundingBox.minZ, this.boundingBox.maxX, d9, this.boundingBox.maxZ);
			if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
				d += 1.0D / (double) i;
			}
		}

		double d8;
		double d13;
		double d16;
		double d21;
		if (this.worldObj.multiplayerWorld) {
			if (this.field_9176_d > 0) {
				d21 = this.posX + (this.field_9174_e - this.posX) / (double) this.field_9176_d;
				d8 = this.posY + (this.field_9172_f - this.posY) / (double) this.field_9176_d;
				d13 = this.posZ + (this.field_9175_aj - this.posZ) / (double) this.field_9176_d;

				for (d16 = this.field_9173_ak - (double) this.rotationYaw; d16 < -180.0D; d16 += 360.0D) {
				}

				while (d16 >= 180.0D) {
					d16 -= 360.0D;
				}

				this.rotationYaw = (float) ((double) this.rotationYaw + d16 / (double) this.field_9176_d);
				this.rotationPitch = (float) ((double) this.rotationPitch
						+ (this.field_9171_al - (double) this.rotationPitch) / (double) this.field_9176_d);
				--this.field_9176_d;
				this.setPosition(d21, d8, d13);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			} else {
				d21 = this.posX + this.motionX;
				d8 = this.posY + this.motionY;
				d13 = this.posZ + this.motionZ;
				this.setPosition(d21, d8, d13);
				if (this.onGround) {
					this.motionX *= 0.5D;
					this.motionY *= 0.5D;
					this.motionZ *= 0.5D;
				}

				this.motionX *= (double) 0.99F;
				this.motionY *= (double) 0.95F;
				this.motionZ *= (double) 0.99F;
			}

		} else {
			if (d < 1.0D) {
				d21 = d * 2.0D - 1.0D;
				this.motionY += (double) 0.04F * d21;
			} else {
				if (this.motionY < 0.0D) {
					this.motionY /= 2.0D;
				}

				this.motionY += 0.007000000216066837D;
			}

			if (this.riddenByEntity != null) {
				this.motionX += this.riddenByEntity.motionX * 0.2D;
				this.motionZ += this.riddenByEntity.motionZ * 0.2D;
			}

			d21 = 0.4D;
			if (this.motionX < -d21) {
				this.motionX = -d21;
			}

			if (this.motionX > d21) {
				this.motionX = d21;
			}

			if (this.motionZ < -d21) {
				this.motionZ = -d21;
			}

			if (this.motionZ > d21) {
				this.motionZ = d21;
			}

			if (this.onGround) {
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			d8 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			if (d8 > 0.15D) {
				d13 = Math.cos((double) this.rotationYaw * Math.PI / 180.0D);
				d16 = Math.sin((double) this.rotationYaw * Math.PI / 180.0D);

				for (int d17 = 0; (double) d17 < 1.0D + d8 * 60.0D; ++d17) {
					double d18 = (double) (this.rand.nextFloat() * 2.0F - 1.0F);
					double d20 = (double) (this.rand.nextInt(2) * 2 - 1) * 0.7D;
					double k1;
					double i2;
					if (this.rand.nextBoolean()) {
						k1 = this.posX - d13 * d18 * 0.8D + d16 * d20;
						i2 = this.posZ - d16 * d18 * 0.8D - d13 * d20;
						this.worldObj.spawnParticle("splash", k1, this.posY - 0.125D, i2, this.motionX, this.motionY,
								this.motionZ);
					} else {
						k1 = this.posX + d13 + d16 * d18 * 0.7D;
						i2 = this.posZ + d16 - d13 * d18 * 0.7D;
						this.worldObj.spawnParticle("splash", k1, this.posY - 0.125D, i2, this.motionX, this.motionY,
								this.motionZ);
					}
				}
			}

			if (this.isCollidedHorizontally && d8 > 0.15D) {
				if (!this.worldObj.multiplayerWorld) {
					this.setEntityDead();

					int i22;
					for (i22 = 0; i22 < 3; ++i22) {
						this.dropItemWithOffset(Block.planks.blockID, 1, 0.0F);
					}

					for (i22 = 0; i22 < 2; ++i22) {
						this.dropItemWithOffset(Item.stick.shiftedIndex, 1, 0.0F);
					}
				}
			} else {
				this.motionX *= (double) 0.99F;
				this.motionY *= (double) 0.95F;
				this.motionZ *= (double) 0.99F;
			}

			this.rotationPitch = 0.0F;
			d13 = (double) this.rotationYaw;
			d16 = this.prevPosX - this.posX;
			double d23 = this.prevPosZ - this.posZ;
			if (d16 * d16 + d23 * d23 > 0.001D) {
				d13 = (double) ((float) (Math.atan2(d23, d16) * 180.0D / Math.PI));
			}

			double d19;
			for (d19 = d13 - (double) this.rotationYaw; d19 >= 180.0D; d19 -= 360.0D) {
			}

			while (d19 < -180.0D) {
				d19 += 360.0D;
			}

			if (d19 > 20.0D) {
				d19 = 20.0D;
			}

			if (d19 < -20.0D) {
				d19 = -20.0D;
			}

			this.rotationYaw = (float) ((double) this.rotationYaw + d19);
			this.setRotation(this.rotationYaw, this.rotationPitch);
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
					this.boundingBox.expand((double) 0.2F, 0.0D, (double) 0.2F));
			int i24;
			if (list != null && list.size() > 0) {
				for (i24 = 0; i24 < list.size(); ++i24) {
					Entity l1 = (Entity) list.get(i24);
					if (l1 != this.riddenByEntity && l1.canBePushed() && l1 instanceof EntityBoat) {
						l1.applyEntityCollision(this);
					}
				}
			}

			for (i24 = 0; i24 < 4; ++i24) {
				int i25 = MathHelper.floor_double(this.posX + ((double) (i24 % 2) - 0.5D) * 0.8D);
				int i26 = MathHelper.floor_double(this.posY);
				int j2 = MathHelper.floor_double(this.posZ + ((double) (i24 / 2) - 0.5D) * 0.8D);
				if (this.worldObj.getBlockId(i25, i26, j2) == Block.snow.blockID) {
					this.worldObj.setBlockWithNotify(i25, i26, j2, 0);
				}
			}

			if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
				this.riddenByEntity = null;
			}

		}
	}

	public void updateRiderPosition() {
		if (this.riddenByEntity != null) {
			double d = Math.cos((double) this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			double d1 = Math.sin((double) this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			this.riddenByEntity.setPosition(this.posX + d,
					this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d1);
		}
	}

	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
	}

	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
	}

	public boolean interact(EntityPlayer entityplayer) {
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer
				&& this.riddenByEntity != entityplayer) {
			return true;
		} else {
			if (!this.worldObj.multiplayerWorld) {
				entityplayer.mountEntity(this);
			}

			return true;
		}
	}
}
