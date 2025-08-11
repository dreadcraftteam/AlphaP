package net.minecraft_server.src;

import java.util.List;

public class EntitySnowball extends Entity {
	private int xTileSnowball = -1;
	private int yTileSnowball = -1;
	private int zTileSnowball = -1;
	private int inTileSnowball = 0;
	private boolean inGroundSnowball = false;
	public int shakeSnowball = 0;
	private EntityLiving owner;
	private int ticksOnGround;
	private int ticksInAir = 0;

	public EntitySnowball(World world) {
		super(world);
		this.setSize(0.25F, 0.25F);
	}

	protected void entityInit() {
	}

	public EntitySnowball(World world, EntityLiving entityliving) {
		super(world);
		this.owner = entityliving;
		this.setSize(0.25F, 0.25F);
		this.setLocationAndAngles(entityliving.posX, entityliving.posY + (double) entityliving.getEyeHeight(),
				entityliving.posZ, entityliving.rotationYaw, entityliving.rotationPitch);
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
		this.func_6141_a(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
	}

	public EntitySnowball(World world, double d, double d1, double d2) {
		super(world);
		this.ticksOnGround = 0;
		this.setSize(0.25F, 0.25F);
		this.setPosition(d, d1, d2);
		this.yOffset = 0.0F;
	}

	public void func_6141_a(double d, double d1, double d2, float f, float f1) {
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
		this.ticksOnGround = 0;
	}

	public void onUpdate() {
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		super.onUpdate();
		if (this.shakeSnowball > 0) {
			--this.shakeSnowball;
		}

		if (this.inGroundSnowball) {
			int vec3d = this.worldObj.getBlockId(this.xTileSnowball, this.yTileSnowball, this.zTileSnowball);
			if (vec3d == this.inTileSnowball) {
				++this.ticksOnGround;
				if (this.ticksOnGround == 1200) {
					this.setEntityDead();
				}

				return;
			}

			this.inGroundSnowball = false;
			this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
			this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
			this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
			this.ticksOnGround = 0;
			this.ticksInAir = 0;
		} else {
			++this.ticksInAir;
		}

		Vec3D vec3D15 = Vec3D.createVector(this.posX, this.posY, this.posZ);
		Vec3D vec3d1 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec3D15, vec3d1);
		vec3D15 = Vec3D.createVector(this.posX, this.posY, this.posZ);
		vec3d1 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		if (movingobjectposition != null) {
			vec3d1 = Vec3D.createVector(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord,
					movingobjectposition.hitVec.zCoord);
		}

		if (!this.worldObj.multiplayerWorld) {
			Entity f = null;
			List f1 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
					this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double f2 = 0.0D;

			for (int f3 = 0; f3 < f1.size(); ++f3) {
				Entity entity1 = (Entity) f1.get(f3);
				if (entity1.canBeCollidedWith() && (entity1 != this.owner || this.ticksInAir >= 5)) {
					float f4 = 0.3F;
					AxisAlignedBB axisalignedbb = entity1.boundingBox.expand((double) f4, (double) f4, (double) f4);
					MovingObjectPosition movingobjectposition1 = axisalignedbb.func_706_a(vec3D15, vec3d1);
					if (movingobjectposition1 != null) {
						double d1 = vec3D15.distanceTo(movingobjectposition1.hitVec);
						if (d1 < f2 || f2 == 0.0D) {
							f = entity1;
							f2 = d1;
						}
					}
				}
			}

			if (f != null) {
				movingobjectposition = new MovingObjectPosition(f);
			}
		}

		if (movingobjectposition != null) {
			if (movingobjectposition.entityHit != null
					&& !movingobjectposition.entityHit.attackEntityFrom(this.owner, 0)) {
				;
			}

			for (int i16 = 0; i16 < 8; ++i16) {
				this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
			}

			this.setEntityDead();
		}

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float f17 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / (double) (float) Math.PI);

		for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f17) * 180.0D
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
		float f18 = 0.99F;
		float f19 = 0.03F;
		if (this.isInWater()) {
			for (int k = 0; k < 4; ++k) {
				float f20 = 0.25F;
				this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double) f20,
						this.posY - this.motionY * (double) f20, this.posZ - this.motionZ * (double) f20, this.motionX,
						this.motionY, this.motionZ);
			}

			f18 = 0.8F;
		}

		this.motionX *= (double) f18;
		this.motionY *= (double) f18;
		this.motionZ *= (double) f18;
		this.motionY -= (double) f19;
		this.setPosition(this.posX, this.posY, this.posZ);
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("xTile", (short) this.xTileSnowball);
		nbttagcompound.setShort("yTile", (short) this.yTileSnowball);
		nbttagcompound.setShort("zTile", (short) this.zTileSnowball);
		nbttagcompound.setByte("inTile", (byte) this.inTileSnowball);
		nbttagcompound.setByte("shake", (byte) this.shakeSnowball);
		nbttagcompound.setByte("inGround", (byte) (this.inGroundSnowball ? 1 : 0));
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.xTileSnowball = nbttagcompound.getShort("xTile");
		this.yTileSnowball = nbttagcompound.getShort("yTile");
		this.zTileSnowball = nbttagcompound.getShort("zTile");
		this.inTileSnowball = nbttagcompound.getByte("inTile") & 255;
		this.shakeSnowball = nbttagcompound.getByte("shake") & 255;
		this.inGroundSnowball = nbttagcompound.getByte("inGround") == 1;
	}

	public void onCollideWithPlayer(EntityPlayer entityplayer) {
		if (this.inGroundSnowball && this.owner == entityplayer && this.shakeSnowball <= 0
				&& entityplayer.inventory.addItemStackToInventory(new ItemStack(Item.arrow, 1))) {
			this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F,
					((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			entityplayer.onItemPickup(this, 1);
			this.setEntityDead();
		}

	}
}
