package net.minecraft_server.src;

import java.util.List;

public class EntityArrow extends Entity {
	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private int inTile = 0;
	private int field_28011_h = 0;
	private boolean inGround = false;
	public boolean field_28012_a = false;
	public int arrowShake = 0;
	public EntityLiving owner;
	private int ticksInGround;
	private int ticksInAir = 0;

	public EntityArrow(World world) {
		super(world);
		this.setSize(0.5F, 0.5F);
	}

	public EntityArrow(World world, double d, double d1, double d2) {
		super(world);
		this.setSize(0.5F, 0.5F);
		this.setPosition(d, d1, d2);
		this.yOffset = 0.0F;
	}

	public EntityArrow(World world, EntityLiving entityliving) {
		super(world);
		this.owner = entityliving;
		this.field_28012_a = entityliving instanceof EntityPlayer;
		this.setSize(0.5F, 0.5F);
		this.setLocationAndAngles(entityliving.posX, entityliving.posY + (double) entityliving.getEyeHeight(),
				entityliving.posZ, entityliving.rotationYaw, entityliving.rotationPitch);
		this.posX -= (double) (MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F) * 0.16F);
		this.posY -= (double) 0.1F;
		this.posZ -= (double) (MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F)
				* MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F));
		this.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F)
				* MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F));
		this.motionY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F * 3.141593F));
		this.setArrowHeading(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
	}

	protected void entityInit() {
	}

	public void setArrowHeading(double d, double d1, double d2, float f, float f1) {
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
		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float i = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D
					/ (double) (float) Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, (double) i) * 180.0D
					/ (double) (float) Math.PI);
		}

		int i16 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
		if (i16 > 0) {
			Block.blocksList[i16].setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
			AxisAlignedBB vec3d = Block.blocksList[i16].getCollisionBoundingBoxFromPool(this.worldObj, this.xTile,
					this.yTile, this.zTile);
			if (vec3d != null && vec3d.isVecInXYZ(Vec3D.createVector(this.posX, this.posY, this.posZ))) {
				this.inGround = true;
			}
		}

		if (this.arrowShake > 0) {
			--this.arrowShake;
		}

		if (this.inGround) {
			int i18 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
			int i19 = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
			if (i18 == this.inTile && i19 == this.field_28011_h) {
				++this.ticksInGround;
				if (this.ticksInGround == 1200) {
					this.setEntityDead();
				}

			} else {
				this.inGround = false;
				this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			}
		} else {
			++this.ticksInAir;
			Vec3D vec3D17 = Vec3D.createVector(this.posX, this.posY, this.posZ);
			Vec3D vec3d1 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY,
					this.posZ + this.motionZ);
			MovingObjectPosition movingobjectposition = this.worldObj.func_28099_a(vec3D17, vec3d1, false, true);
			vec3D17 = Vec3D.createVector(this.posX, this.posY, this.posZ);
			vec3d1 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			if (movingobjectposition != null) {
				vec3d1 = Vec3D.createVector(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord,
						movingobjectposition.hitVec.zCoord);
			}

			Entity entity = null;
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
					this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double d = 0.0D;

			float f5;
			for (int f2 = 0; f2 < list.size(); ++f2) {
				Entity f3 = (Entity) list.get(f2);
				if (f3.canBeCollidedWith() && (f3 != this.owner || this.ticksInAir >= 5)) {
					f5 = 0.3F;
					AxisAlignedBB i1 = f3.boundingBox.expand((double) f5, (double) f5, (double) f5);
					MovingObjectPosition f6 = i1.func_706_a(vec3D17, vec3d1);
					if (f6 != null) {
						double d1 = vec3D17.distanceTo(f6.hitVec);
						if (d1 < d || d == 0.0D) {
							entity = f3;
							d = d1;
						}
					}
				}
			}

			if (entity != null) {
				movingobjectposition = new MovingObjectPosition(entity);
			}

			float f20;
			if (movingobjectposition != null) {
				if (movingobjectposition.entityHit != null) {
					if (movingobjectposition.entityHit.attackEntityFrom(this.owner, 4)) {
						this.worldObj.playSoundAtEntity(this, "random.drr", 1.0F,
								1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
						this.setEntityDead();
					} else {
						this.motionX *= -0.10000000149011612D;
						this.motionY *= -0.10000000149011612D;
						this.motionZ *= -0.10000000149011612D;
						this.rotationYaw += 180.0F;
						this.prevRotationYaw += 180.0F;
						this.ticksInAir = 0;
					}
				} else {
					this.xTile = movingobjectposition.blockX;
					this.yTile = movingobjectposition.blockY;
					this.zTile = movingobjectposition.blockZ;
					this.inTile = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
					this.field_28011_h = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
					this.motionX = (double) ((float) (movingobjectposition.hitVec.xCoord - this.posX));
					this.motionY = (double) ((float) (movingobjectposition.hitVec.yCoord - this.posY));
					this.motionZ = (double) ((float) (movingobjectposition.hitVec.zCoord - this.posZ));
					f20 = MathHelper.sqrt_double(
							this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
					this.posX -= this.motionX / (double) f20 * (double) 0.05F;
					this.posY -= this.motionY / (double) f20 * (double) 0.05F;
					this.posZ -= this.motionZ / (double) f20 * (double) 0.05F;
					this.worldObj.playSoundAtEntity(this, "random.drr", 1.0F,
							1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					this.inGround = true;
					this.arrowShake = 7;
				}
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			f20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / (double) (float) Math.PI);

			for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f20) * 180.0D
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
			float f21 = 0.99F;
			f5 = 0.03F;
			if (this.isInWater()) {
				for (int i22 = 0; i22 < 4; ++i22) {
					float f23 = 0.25F;
					this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double) f23,
							this.posY - this.motionY * (double) f23, this.posZ - this.motionZ * (double) f23,
							this.motionX, this.motionY, this.motionZ);
				}

				f21 = 0.8F;
			}

			this.motionX *= (double) f21;
			this.motionY *= (double) f21;
			this.motionZ *= (double) f21;
			this.motionY -= (double) f5;
			this.setPosition(this.posX, this.posY, this.posZ);
		}
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("xTile", (short) this.xTile);
		nbttagcompound.setShort("yTile", (short) this.yTile);
		nbttagcompound.setShort("zTile", (short) this.zTile);
		nbttagcompound.setByte("inTile", (byte) this.inTile);
		nbttagcompound.setByte("inData", (byte) this.field_28011_h);
		nbttagcompound.setByte("shake", (byte) this.arrowShake);
		nbttagcompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
		nbttagcompound.setBoolean("player", this.field_28012_a);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.xTile = nbttagcompound.getShort("xTile");
		this.yTile = nbttagcompound.getShort("yTile");
		this.zTile = nbttagcompound.getShort("zTile");
		this.inTile = nbttagcompound.getByte("inTile") & 255;
		this.field_28011_h = nbttagcompound.getByte("inData") & 255;
		this.arrowShake = nbttagcompound.getByte("shake") & 255;
		this.inGround = nbttagcompound.getByte("inGround") == 1;
		this.field_28012_a = nbttagcompound.getBoolean("player");
	}

	public void onCollideWithPlayer(EntityPlayer entityplayer) {
		if (!this.worldObj.multiplayerWorld) {
			if (this.inGround && this.field_28012_a && this.arrowShake <= 0
					&& entityplayer.inventory.addItemStackToInventory(new ItemStack(Item.arrow, 1))) {
				this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F,
						((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				entityplayer.onItemPickup(this, 1);
				this.setEntityDead();
			}

		}
	}
}
