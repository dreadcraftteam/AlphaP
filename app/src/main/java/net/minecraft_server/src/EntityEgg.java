package net.minecraft_server.src;

import java.util.List;

public class EntityEgg extends Entity {
	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private int inTile = 0;
	private boolean inGround = false;
	public int shake = 0;
	private EntityLiving field_20083_aj;
	private int field_20081_ak;
	private int field_20079_al = 0;

	public EntityEgg(World world) {
		super(world);
		this.setSize(0.25F, 0.25F);
	}

	protected void entityInit() {
	}

	public EntityEgg(World world, EntityLiving entityliving) {
		super(world);
		this.field_20083_aj = entityliving;
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
		this.func_20078_a(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
	}

	public EntityEgg(World world, double d, double d1, double d2) {
		super(world);
		this.field_20081_ak = 0;
		this.setSize(0.25F, 0.25F);
		this.setPosition(d, d1, d2);
		this.yOffset = 0.0F;
	}

	public void func_20078_a(double d, double d1, double d2, float f, float f1) {
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
		this.field_20081_ak = 0;
	}

	public void onUpdate() {
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		super.onUpdate();
		if (this.shake > 0) {
			--this.shake;
		}

		if (this.inGround) {
			int vec3d = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
			if (vec3d == this.inTile) {
				++this.field_20081_ak;
				if (this.field_20081_ak == 1200) {
					this.setEntityDead();
				}

				return;
			}

			this.inGround = false;
			this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
			this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
			this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
			this.field_20081_ak = 0;
			this.field_20079_al = 0;
		} else {
			++this.field_20079_al;
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
				if (entity1.canBeCollidedWith() && (entity1 != this.field_20083_aj || this.field_20079_al >= 5)) {
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
					&& !movingobjectposition.entityHit.attackEntityFrom(this.field_20083_aj, 0)) {
				;
			}

			if (!this.worldObj.multiplayerWorld && this.rand.nextInt(8) == 0) {
				byte b16 = 1;
				if (this.rand.nextInt(32) == 0) {
					b16 = 4;
				}

				for (int i17 = 0; i17 < b16; ++i17) {
					EntityChicken entityChicken21 = new EntityChicken(this.worldObj);
					entityChicken21.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
					this.worldObj.entityJoinedWorld(entityChicken21);
				}
			}

			for (int i18 = 0; i18 < 8; ++i18) {
				this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
			}

			this.setEntityDead();
		}

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float f20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
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
		float f19 = 0.99F;
		float f22 = 0.03F;
		if (this.isInWater()) {
			for (int l = 0; l < 4; ++l) {
				float f23 = 0.25F;
				this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double) f23,
						this.posY - this.motionY * (double) f23, this.posZ - this.motionZ * (double) f23, this.motionX,
						this.motionY, this.motionZ);
			}

			f19 = 0.8F;
		}

		this.motionX *= (double) f19;
		this.motionY *= (double) f19;
		this.motionZ *= (double) f19;
		this.motionY -= (double) f22;
		this.setPosition(this.posX, this.posY, this.posZ);
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

	public void onCollideWithPlayer(EntityPlayer entityplayer) {
		if (this.inGround && this.field_20083_aj == entityplayer && this.shake <= 0
				&& entityplayer.inventory.addItemStackToInventory(new ItemStack(Item.arrow, 1))) {
			this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F,
					((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			entityplayer.onItemPickup(this, 1);
			this.setEntityDead();
		}

	}
}
