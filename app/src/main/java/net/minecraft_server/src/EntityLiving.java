package net.minecraft_server.src;

import java.util.List;

public abstract class EntityLiving extends Entity {
	public int field_9099_av = 20;
	public float field_9098_aw;
	public float field_9096_ay;
	public float renderYawOffset = 0.0F;
	public float prevRenderYawOffset = 0.0F;
	protected float field_9124_aB;
	protected float field_9123_aC;
	protected float field_9122_aD;
	protected float field_9121_aE;
	protected boolean field_9120_aF = true;
	protected String texture = "/mob/char.png";
	protected boolean field_9118_aH = true;
	protected float field_9117_aI = 0.0F;
	protected String entityType = null;
	protected float field_9115_aK = 1.0F;
	protected int scoreValue = 0;
	protected float field_9113_aM = 0.0F;
	public boolean isMultiplayerEntity = false;
	public float prevSwingProgress;
	public float swingProgress;
	public int health = 10;
	public int prevHealth;
	private int livingSoundTime;
	public int hurtTime;
	public int maxHurtTime;
	public float attackedAtYaw = 0.0F;
	public int deathTime = 0;
	public int attackTime = 0;
	public float prevCameraPitch;
	public float cameraPitch;
	protected boolean unused_flag = false;
	public int field_9144_ba = -1;
	public float field_9143_bb = (float) (Math.random() * (double) 0.9F + (double) 0.1F);
	public float field_9142_bc;
	public float field_9141_bd;
	public float field_386_ba;
	protected int field_9140_bf;
	protected double field_9139_bg;
	protected double field_9138_bh;
	protected double field_9137_bi;
	protected double field_9136_bj;
	protected double field_9135_bk;
	float field_9134_bl = 0.0F;
	protected int field_9133_bm = 0;
	protected int age = 0;
	protected float moveStrafing;
	protected float moveForward;
	protected float randomYawVelocity;
	protected boolean isJumping = false;
	protected float defaultPitch = 0.0F;
	protected float moveSpeed = 0.7F;
	private Entity currentTarget;
	protected int numTicksToChaseTarget = 0;

	public EntityLiving(World world) {
		super(world);
		this.preventEntitySpawning = true;
		this.field_9096_ay = (float) (Math.random() + 1.0D) * 0.01F;
		this.setPosition(this.posX, this.posY, this.posZ);
		this.field_9098_aw = (float) Math.random() * 12398.0F;
		this.rotationYaw = (float) (Math.random() * (double) (float) Math.PI * 2.0D);
		this.stepHeight = 0.5F;
	}

	protected void entityInit() {
	}

	public boolean canEntityBeSeen(Entity entity) {
		return this.worldObj.rayTraceBlocks(
				Vec3D.createVector(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ),
				Vec3D.createVector(entity.posX, entity.posY + (double) entity.getEyeHeight(), entity.posZ)) == null;
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public boolean canBePushed() {
		return !this.isDead;
	}

	public float getEyeHeight() {
		return this.height * 0.85F;
	}

	public int getTalkInterval() {
		return 80;
	}

	public void playLivingSound() {
		String s = this.getLivingSound();
		if (s != null) {
			this.worldObj.playSoundAtEntity(this, s, this.getSoundVolume(),
					(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
		}

	}

	public void onEntityUpdate() {
		this.prevSwingProgress = this.swingProgress;
		super.onEntityUpdate();
		if (this.rand.nextInt(1000) < this.livingSoundTime++) {
			this.livingSoundTime = -this.getTalkInterval();
			this.playLivingSound();
		}

		if (this.isEntityAlive() && this.isEntityInsideOpaqueBlock()) {
			this.attackEntityFrom((Entity) null, 1);
		}

		if (this.isImmuneToFire || this.worldObj.multiplayerWorld) {
			this.fire = 0;
		}

		int j;
		if (this.isEntityAlive() && this.isInsideOfMaterial(Material.water) && !this.canBreatheUnderwater()) {
			--this.air;
			if (this.air == -20) {
				this.air = 0;

				for (j = 0; j < 8; ++j) {
					float d = this.rand.nextFloat() - this.rand.nextFloat();
					float f1 = this.rand.nextFloat() - this.rand.nextFloat();
					float d1 = this.rand.nextFloat() - this.rand.nextFloat();
					this.worldObj.spawnParticle("bubble", this.posX + (double) d, this.posY + (double) f1,
							this.posZ + (double) d1, this.motionX, this.motionY, this.motionZ);
				}

				this.attackEntityFrom((Entity) null, 2);
			}

			this.fire = 0;
		} else {
			this.air = this.maxAir;
		}

		this.prevCameraPitch = this.cameraPitch;
		if (this.attackTime > 0) {
			--this.attackTime;
		}

		if (this.hurtTime > 0) {
			--this.hurtTime;
		}

		if (this.field_9083_ac > 0) {
			--this.field_9083_ac;
		}

		if (this.health <= 0) {
			++this.deathTime;
			if (this.deathTime > 20) {
				this.func_6101_K();
				this.setEntityDead();

				for (j = 0; j < 20; ++j) {
					double d8 = this.rand.nextGaussian() * 0.02D;
					double d9 = this.rand.nextGaussian() * 0.02D;
					double d2 = this.rand.nextGaussian() * 0.02D;
					this.worldObj.spawnParticle("explode",
							this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
							this.posY + (double) (this.rand.nextFloat() * this.height),
							this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d8,
							d9, d2);
				}
			}
		}

		this.field_9121_aE = this.field_9122_aD;
		this.prevRenderYawOffset = this.renderYawOffset;
		this.prevRotationYaw = this.rotationYaw;
		this.prevRotationPitch = this.rotationPitch;
	}

	public void spawnExplosionParticle() {
		for (int i = 0; i < 20; ++i) {
			double d = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			double d3 = 10.0D;
			this.worldObj.spawnParticle("explode",
					this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width - d * d3,
					this.posY + (double) (this.rand.nextFloat() * this.height) - d1 * d3,
					this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width - d2 * d3, d,
					d1, d2);
		}

	}

	public void updateRidden() {
		super.updateRidden();
		this.field_9124_aB = this.field_9123_aC;
		this.field_9123_aC = 0.0F;
	}

	public void onUpdate() {
		super.onUpdate();
		this.onLivingUpdate();
		double d = this.posX - this.prevPosX;
		double d1 = this.posZ - this.prevPosZ;
		float f = MathHelper.sqrt_double(d * d + d1 * d1);
		float f1 = this.renderYawOffset;
		float f2 = 0.0F;
		this.field_9124_aB = this.field_9123_aC;
		float f3 = 0.0F;
		if (f > 0.05F) {
			f3 = 1.0F;
			f2 = f * 3.0F;
			f1 = (float) Math.atan2(d1, d) * 180.0F / 3.141593F - 90.0F;
		}

		if (this.swingProgress > 0.0F) {
			f1 = this.rotationYaw;
		}

		if (!this.onGround) {
			f3 = 0.0F;
		}

		this.field_9123_aC += (f3 - this.field_9123_aC) * 0.3F;

		float f4;
		for (f4 = f1 - this.renderYawOffset; f4 < -180.0F; f4 += 360.0F) {
		}

		while (f4 >= 180.0F) {
			f4 -= 360.0F;
		}

		this.renderYawOffset += f4 * 0.3F;

		float f5;
		for (f5 = this.rotationYaw - this.renderYawOffset; f5 < -180.0F; f5 += 360.0F) {
		}

		while (f5 >= 180.0F) {
			f5 -= 360.0F;
		}

		boolean flag = f5 < -90.0F || f5 >= 90.0F;
		if (f5 < -75.0F) {
			f5 = -75.0F;
		}

		if (f5 >= 75.0F) {
			f5 = 75.0F;
		}

		this.renderYawOffset = this.rotationYaw - f5;
		if (f5 * f5 > 2500.0F) {
			this.renderYawOffset += f5 * 0.2F;
		}

		if (flag) {
			f2 *= -1.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
			this.prevRenderYawOffset -= 360.0F;
		}

		while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
			this.prevRenderYawOffset += 360.0F;
		}

		while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
			this.prevRotationPitch -= 360.0F;
		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		this.field_9122_aD += f2;
	}

	protected void setSize(float f, float f1) {
		super.setSize(f, f1);
	}

	public void heal(int i) {
		if (this.health > 0) {
			this.health += i;
			if (this.health > 20) {
				this.health = 20;
			}

			this.field_9083_ac = this.field_9099_av / 2;
		}
	}

	public boolean attackEntityFrom(Entity entity, int i) {
		if (this.worldObj.multiplayerWorld) {
			return false;
		} else {
			this.age = 0;
			if (this.health <= 0) {
				return false;
			} else {
				this.field_9141_bd = 1.5F;
				boolean flag = true;
				if ((float) this.field_9083_ac > (float) this.field_9099_av / 2.0F) {
					if (i <= this.field_9133_bm) {
						return false;
					}

					this.damageEntity(i - this.field_9133_bm);
					this.field_9133_bm = i;
					flag = false;
				} else {
					this.field_9133_bm = i;
					this.prevHealth = this.health;
					this.field_9083_ac = this.field_9099_av;
					this.damageEntity(i);
					this.hurtTime = this.maxHurtTime = 10;
				}

				this.attackedAtYaw = 0.0F;
				if (flag) {
					this.worldObj.sendTrackedEntityStatusUpdatePacket(this, (byte) 2);
					this.setBeenAttacked();
					if (entity != null) {
						double d = entity.posX - this.posX;

						double d1;
						for (d1 = entity.posZ - this.posZ; d * d
								+ d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
							d = (Math.random() - Math.random()) * 0.01D;
						}

						this.attackedAtYaw = (float) (Math.atan2(d1, d) * 180.0D / (double) (float) Math.PI)
								- this.rotationYaw;
						this.knockBack(entity, i, d, d1);
					} else {
						this.attackedAtYaw = (float) ((int) (Math.random() * 2.0D) * 180);
					}
				}

				if (this.health <= 0) {
					if (flag) {
						this.worldObj.playSoundAtEntity(this, this.getDeathSound(), this.getSoundVolume(),
								(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
					}

					this.onDeath(entity);
				} else if (flag) {
					this.worldObj.playSoundAtEntity(this, this.getHurtSound(), this.getSoundVolume(),
							(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
				}

				return true;
			}
		}
	}

	protected void damageEntity(int i) {
		this.health -= i;
	}

	protected float getSoundVolume() {
		return 1.0F;
	}

	protected String getLivingSound() {
		return null;
	}

	protected String getHurtSound() {
		return "random.hurt";
	}

	protected String getDeathSound() {
		return "random.hurt";
	}

	public void knockBack(Entity entity, int i, double d, double d1) {
		float f = MathHelper.sqrt_double(d * d + d1 * d1);
		float f1 = 0.4F;
		this.motionX /= 2.0D;
		this.motionY /= 2.0D;
		this.motionZ /= 2.0D;
		this.motionX -= d / (double) f * (double) f1;
		this.motionY += (double) 0.4F;
		this.motionZ -= d1 / (double) f * (double) f1;
		if (this.motionY > (double) 0.4F) {
			this.motionY = (double) 0.4F;
		}

	}

	public void onDeath(Entity entity) {
		if (this.scoreValue >= 0 && entity != null) {
			entity.addToPlayerScore(this, this.scoreValue);
		}

		if (entity != null) {
			entity.func_27010_a(this);
		}

		this.unused_flag = true;
		if (!this.worldObj.multiplayerWorld) {
			this.dropFewItems();
		}

		this.worldObj.sendTrackedEntityStatusUpdatePacket(this, (byte) 3);
	}

	protected void dropFewItems() {
		int i = this.getDropItemId();
		if (i > 0) {
			int j = this.rand.nextInt(3);

			for (int k = 0; k < j; ++k) {
				this.dropItem(i, 1);
			}
		}

	}

	protected int getDropItemId() {
		return 0;
	}

	protected void fall(float f) {
		super.fall(f);
		int i = (int) Math.ceil((double) (f - 3.0F));
		if (i > 0) {
			this.attackEntityFrom((Entity) null, i);
			int j = this.worldObj.getBlockId(MathHelper.floor_double(this.posX),
					MathHelper.floor_double(this.posY - (double) 0.2F - (double) this.yOffset),
					MathHelper.floor_double(this.posZ));
			if (j > 0) {
				StepSound stepsound = Block.blocksList[j].stepSound;
				this.worldObj.playSoundAtEntity(this, stepsound.func_737_c(), stepsound.getVolume() * 0.5F,
						stepsound.getPitch() * 0.75F);
			}
		}

	}

	public void moveEntityWithHeading(float f, float f1) {
		double d2;
		if (this.isInWater()) {
			d2 = this.posY;
			this.moveFlying(f, f1, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double) 0.8F;
			this.motionY *= (double) 0.8F;
			this.motionZ *= (double) 0.8F;
			if (this.posY > 100000.0D) {
				this.motionY -= 0.002D;
			} else {
				this.motionY -= 0.02D;
			}

			if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX,
					this.motionY + (double) 0.6F - this.posY + d2, this.motionZ)) {
				this.motionY = (double) 0.3F;
			}
		} else if (this.handleLavaMovement()) {
			d2 = this.posY;
			this.moveFlying(f, f1, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
			this.motionY -= 0.02D;
			if (this.posY > 100000.0D) {
				this.motionY -= 0.002D;
			} else {
				this.motionY -= 0.02D;
			}

			if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX,
					this.motionY + (double) 0.6F - this.posY + d2, this.motionZ)) {
				this.motionY = (double) 0.3F;
			}
		} else {
			float d21 = 0.91F;
			if (this.onGround) {
				d21 = 0.5460001F;
				int f3 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX),
						MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
				if (f3 > 0) {
					d21 = Block.blocksList[f3].slipperiness * 0.91F;
				}
			}

			float f31 = 0.1627714F / (d21 * d21 * d21);
			this.moveFlying(f, f1, this.onGround ? 0.1F * f31 : 0.02F);
			d21 = 0.91F;
			if (this.onGround) {
				d21 = 0.5460001F;
				int d3 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX),
						MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
				if (d3 > 0) {
					d21 = Block.blocksList[d3].slipperiness * 0.91F;
				}
			}

			if (this.isOnLadder()) {
				if (this.posY > 100000.0D) {
					this.motionY -= 0.072D;
				}

				float d31 = 0.15F;
				if (this.motionX < (double) (-d31)) {
					this.motionX = (double) (-d31);
				}

				if (this.motionX > (double) d31) {
					this.motionX = (double) d31;
				}

				if (this.motionZ < (double) (-d31)) {
					this.motionZ = (double) (-d31);
				}

				if (this.motionZ > (double) d31) {
					this.motionZ = (double) d31;
				}

				this.fallDistance = 0.0F;
				if (this.motionY < -0.15D) {
					this.motionY = -0.15D;
				}

				if (this.isSneaking() && this.motionY < 0.0D) {
					this.motionY = 0.0D;
				}
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			if (this.isCollidedHorizontally && this.isOnLadder()) {
				this.motionY = 0.2D;
			}

			if (this.posY > 100000.0D) {
				this.motionY -= 0.008D;
			} else {
				this.motionY -= 0.08D;
			}

			this.motionY *= (double) 0.98F;
			this.motionX *= (double) d21;
			this.motionZ *= (double) d21;
		}

		this.field_9142_bc = this.field_9141_bd;
		d2 = this.posX - this.prevPosX;
		double d32 = this.posZ - this.prevPosZ;
		float f5 = MathHelper.sqrt_double(d2 * d2 + d32 * d32) * 4.0F;
		if (f5 > 1.0F) {
			f5 = 1.0F;
		}

		this.field_9141_bd += (f5 - this.field_9141_bd) * 0.4F;
		this.field_386_ba += this.field_9141_bd;
	}

	public boolean isOnLadder() {
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.boundingBox.minY);
		int k = MathHelper.floor_double(this.posZ);
		return this.worldObj.getBlockId(i, j, k) == Block.ladder.blockID;
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("Health", (short) this.health);
		nbttagcompound.setShort("HurtTime", (short) this.hurtTime);
		nbttagcompound.setShort("DeathTime", (short) this.deathTime);
		nbttagcompound.setShort("AttackTime", (short) this.attackTime);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.health = nbttagcompound.getShort("Health");
		if (!nbttagcompound.hasKey("Health")) {
			this.health = 10;
		}

		this.hurtTime = nbttagcompound.getShort("HurtTime");
		this.deathTime = nbttagcompound.getShort("DeathTime");
		this.attackTime = nbttagcompound.getShort("AttackTime");
	}

	public boolean isEntityAlive() {
		return !this.isDead && this.health > 0;
	}

	public boolean canBreatheUnderwater() {
		return false;
	}

	public void onLivingUpdate() {
		if (this.field_9140_bf > 0) {
			double inWater = this.posX + (this.field_9139_bg - this.posX) / (double) this.field_9140_bf;
			double list = this.posY + (this.field_9138_bh - this.posY) / (double) this.field_9140_bf;
			double entity = this.posZ + (this.field_9137_bi - this.posZ) / (double) this.field_9140_bf;

			double d3;
			for (d3 = this.field_9136_bj - (double) this.rotationYaw; d3 < -180.0D; d3 += 360.0D) {
			}

			while (d3 >= 180.0D) {
				d3 -= 360.0D;
			}

			this.rotationYaw = (float) ((double) this.rotationYaw + d3 / (double) this.field_9140_bf);
			this.rotationPitch = (float) ((double) this.rotationPitch
					+ (this.field_9135_bk - (double) this.rotationPitch) / (double) this.field_9140_bf);
			--this.field_9140_bf;
			this.setPosition(inWater, list, entity);
			this.setRotation(this.rotationYaw, this.rotationPitch);
			List list1 = this.worldObj.getCollidingBoundingBoxes(this,
					this.boundingBox.getInsetBoundingBox(8.0D / 256D, 0.0D, 8.0D / 256D));
			if (list1.size() > 0) {
				double d4 = 0.0D;

				for (int j = 0; j < list1.size(); ++j) {
					AxisAlignedBB axisalignedbb = (AxisAlignedBB) list1.get(j);
					if (axisalignedbb.maxY > d4) {
						d4 = axisalignedbb.maxY;
					}
				}

				list += d4 - this.boundingBox.minY;
				this.setPosition(inWater, list, entity);
			}
		}

		if (this.isMovementBlocked()) {
			this.isJumping = false;
			this.moveStrafing = 0.0F;
			this.moveForward = 0.0F;
			this.randomYawVelocity = 0.0F;
		} else if (!this.isMultiplayerEntity) {
			this.updatePlayerActionState();
		}

		boolean z14 = this.isInWater();
		boolean inLava = this.handleLavaMovement();
		if (this.isJumping) {
			if (z14) {
				this.motionY += (double) 0.04F;
			} else if (inLava) {
				this.motionY += (double) 0.04F;
			} else if (this.onGround) {
				this.jump();
			} else {
				this.fly();
			}
		}

		this.moveStrafing *= 0.98F;
		this.moveForward *= 0.98F;
		this.randomYawVelocity *= 0.9F;
		this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
		List list15 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
				this.boundingBox.expand((double) 0.2F, 0.0D, (double) 0.2F));
		if (list15 != null && list15.size() > 0) {
			for (int i = 0; i < list15.size(); ++i) {
				Entity entity16 = (Entity) list15.get(i);
				if (entity16.canBePushed()) {
					entity16.applyEntityCollision(this);
				}
			}
		}

	}

	protected boolean isMovementBlocked() {
		return this.health <= 0;
	}

	protected void jump() {
		this.motionY = (double) 0.42F;
	}

	protected void fly() {
	}

	protected boolean func_25020_s() {
		return true;
	}

	protected void func_27013_Q() {
		EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, -1.0D);
		if (this.func_25020_s() && entityplayer != null) {
			double d = entityplayer.posX - this.posX;
			double d1 = entityplayer.posY - this.posY;
			double d2 = entityplayer.posZ - this.posZ;
			double d3 = d * d + d1 * d1 + d2 * d2;
			if (d3 > 16384.0D) {
				this.setEntityDead();
			}

			if (this.age > 600 && this.rand.nextInt(800) == 0) {
				if (d3 < 1024.0D) {
					this.age = 0;
				} else {
					this.setEntityDead();
				}
			}
		}

	}

	protected void updatePlayerActionState() {
		++this.age;
		EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, -1.0D);
		this.func_27013_Q();
		this.moveStrafing = 0.0F;
		this.moveForward = 0.0F;
		float f = 8.0F;
		if (this.rand.nextFloat() < 0.02F) {
			EntityPlayer flag = this.worldObj.getClosestPlayerToEntity(this, (double) f);
			if (flag != null) {
				this.currentTarget = flag;
				this.numTicksToChaseTarget = 10 + this.rand.nextInt(20);
			} else {
				this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
			}
		}

		if (this.currentTarget != null) {
			this.faceEntity(this.currentTarget, 10.0F, (float) this.func_25018_n_());
			if (this.numTicksToChaseTarget-- <= 0 || this.currentTarget.isDead
					|| this.currentTarget.getDistanceSqToEntity(this) > (double) (f * f)) {
				this.currentTarget = null;
			}
		} else {
			if (this.rand.nextFloat() < 0.05F) {
				this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
			}

			this.rotationYaw += this.randomYawVelocity;
			this.rotationPitch = this.defaultPitch;
		}

		boolean z5 = this.isInWater();
		boolean flag1 = this.handleLavaMovement();
		if (z5 || flag1) {
			this.isJumping = this.rand.nextFloat() < 0.8F;
		}

	}

	protected int func_25018_n_() {
		return 40;
	}

	public void faceEntity(Entity entity, float f, float f1) {
		double d = entity.posX - this.posX;
		double d2 = entity.posZ - this.posZ;
		double d1;
		if (entity instanceof EntityLiving) {
			EntityLiving d3 = (EntityLiving) entity;
			d1 = this.posY + (double) this.getEyeHeight() - (d3.posY + (double) d3.getEyeHeight());
		} else {
			d1 = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0D
					- (this.posY + (double) this.getEyeHeight());
		}

		double d31 = (double) MathHelper.sqrt_double(d * d + d2 * d2);
		float f2 = (float) (Math.atan2(d2, d) * 180.0D / (double) (float) Math.PI) - 90.0F;
		float f3 = (float) (-(Math.atan2(d1, d31) * 180.0D / (double) (float) Math.PI));
		this.rotationPitch = -this.updateRotation(this.rotationPitch, f3, f1);
		this.rotationYaw = this.updateRotation(this.rotationYaw, f2, f);
	}

	public boolean func_25021_O() {
		return this.currentTarget != null;
	}

	public Entity getCurrentTarget() {
		return this.currentTarget;
	}

	private float updateRotation(float f, float f1, float f2) {
		float f3;
		for (f3 = f1 - f; f3 < -180.0F; f3 += 360.0F) {
		}

		while (f3 >= 180.0F) {
			f3 -= 360.0F;
		}

		if (f3 > f2) {
			f3 = f2;
		}

		if (f3 < -f2) {
			f3 = -f2;
		}

		return f + f3;
	}

	public void func_6101_K() {
	}

	public boolean getCanSpawnHere() {
		return this.worldObj.checkIfAABBIsClear(this.boundingBox)
				&& this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0
				&& !this.worldObj.getIsAnyLiquid(this.boundingBox);
	}

	protected void kill() {
		this.attackEntityFrom((Entity) null, 4);
	}

	public Vec3D getLookVec() {
		return this.getLook(1.0F);
	}

	public Vec3D getLook(float f) {
		float f2;
		float f4;
		float f6;
		float f8;
		if (f == 1.0F) {
			f2 = MathHelper.cos(-this.rotationYaw * 0.01745329F - 3.141593F);
			f4 = MathHelper.sin(-this.rotationYaw * 0.01745329F - 3.141593F);
			f6 = -MathHelper.cos(-this.rotationPitch * 0.01745329F);
			f8 = MathHelper.sin(-this.rotationPitch * 0.01745329F);
			return Vec3D.createVector((double) (f4 * f6), (double) f8, (double) (f2 * f6));
		} else {
			f2 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * f;
			f4 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * f;
			f6 = MathHelper.cos(-f4 * 0.01745329F - 3.141593F);
			f8 = MathHelper.sin(-f4 * 0.01745329F - 3.141593F);
			float f9 = -MathHelper.cos(-f2 * 0.01745329F);
			float f10 = MathHelper.sin(-f2 * 0.01745329F);
			return Vec3D.createVector((double) (f8 * f9), (double) f10, (double) (f6 * f9));
		}
	}

	public int getMaxSpawnedInChunk() {
		return 4;
	}

	public boolean func_22057_E() {
		return false;
	}
}
