package net.minecraft_server.src;

import java.util.List;

public class EntityFireball extends Entity {
	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private int inTile = 0;
	private boolean inGround = false;
	public int shake = 0;
	public EntityLiving owner;
	private int field_9190_an;
	private int ticksInAir = 0;
	public double field_9199_b;
	public double field_9198_c;
	public double field_9196_d;

	public EntityFireball(World world) {
		super(world);
		this.setSize(1.0F, 1.0F);
	}

	protected void entityInit() {
	}

	public EntityFireball(World world, EntityLiving entityliving, double d, double d1, double d2) {
		super(world);
		this.owner = entityliving;
		this.setSize(1.0F, 1.0F);
		this.setLocationAndAngles(entityliving.posX, entityliving.posY, entityliving.posZ, entityliving.rotationYaw,
				entityliving.rotationPitch);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		d += this.rand.nextGaussian() * 0.4D;
		d1 += this.rand.nextGaussian() * 0.4D;
		d2 += this.rand.nextGaussian() * 0.4D;
		double d3 = (double) MathHelper.sqrt_double(d * d + d1 * d1 + d2 * d2);
		this.field_9199_b = d / d3 * 0.1D;
		this.field_9198_c = d1 / d3 * 0.1D;
		this.field_9196_d = d2 / d3 * 0.1D;
	}

	public void onUpdate() {
		super.onUpdate();
		this.fire = 10;
		if (this.shake > 0) {
			--this.shake;
		}

		if (this.inGround) {
			int vec3d = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
			if (vec3d == this.inTile) {
				++this.field_9190_an;
				if (this.field_9190_an == 1200) {
					this.setEntityDead();
				}

				return;
			}

			this.inGround = false;
			this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
			this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
			this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
			this.field_9190_an = 0;
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

		Entity entity = null;
		List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
				this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
		double d = 0.0D;

		for (int f = 0; f < list.size(); ++f) {
			Entity f1 = (Entity) list.get(f);
			if (f1.canBeCollidedWith() && (f1 != this.owner || this.ticksInAir >= 25)) {
				float k = 0.3F;
				AxisAlignedBB f3 = f1.boundingBox.expand((double) k, (double) k, (double) k);
				MovingObjectPosition movingobjectposition1 = f3.func_706_a(vec3D15, vec3d1);
				if (movingobjectposition1 != null) {
					double d1 = vec3D15.distanceTo(movingobjectposition1.hitVec);
					if (d1 < d || d == 0.0D) {
						entity = f1;
						d = d1;
					}
				}
			}
		}

		if (entity != null) {
			movingobjectposition = new MovingObjectPosition(entity);
		}

		if (movingobjectposition != null) {
			if (!this.worldObj.multiplayerWorld) {
				if (movingobjectposition.entityHit != null
						&& !movingobjectposition.entityHit.attackEntityFrom(this.owner, 0)) {
					;
				}

				this.worldObj.newExplosion((Entity) null, this.posX, this.posY, this.posZ, 1.0F, true);
			}

			this.setEntityDead();
		}

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float f16 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / (double) (float) Math.PI);

		for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f16) * 180.0D
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
		float f17 = 0.95F;
		if (this.isInWater()) {
			for (int i18 = 0; i18 < 4; ++i18) {
				float f19 = 0.25F;
				this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double) f19,
						this.posY - this.motionY * (double) f19, this.posZ - this.motionZ * (double) f19, this.motionX,
						this.motionY, this.motionZ);
			}

			f17 = 0.8F;
		}

		this.motionX += this.field_9199_b;
		this.motionY += this.field_9198_c;
		this.motionZ += this.field_9196_d;
		this.motionX *= (double) f17;
		this.motionY *= (double) f17;
		this.motionZ *= (double) f17;
		this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
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

	public boolean canBeCollidedWith() {
		return true;
	}

	public boolean attackEntityFrom(Entity entity, int i) {
		this.setBeenAttacked();
		if (entity != null) {
			Vec3D vec3d = entity.getLookVec();
			if (vec3d != null) {
				this.motionX = vec3d.xCoord;
				this.motionY = vec3d.yCoord;
				this.motionZ = vec3d.zCoord;
				this.field_9199_b = this.motionX * 0.1D;
				this.field_9198_c = this.motionY * 0.1D;
				this.field_9196_d = this.motionZ * 0.1D;
			}

			return true;
		} else {
			return false;
		}
	}
}
