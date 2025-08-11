package net.minecraft_server.src;

public class EntityTNTPrimed extends Entity {
	public int fuse;

	public EntityTNTPrimed(World world) {
		super(world);
		this.fuse = 0;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.yOffset = this.height / 2.0F;
	}

	public EntityTNTPrimed(World world, double d, double d1, double d2) {
		this(world);
		this.setPosition(d, d1, d2);
		float f = (float) (Math.random() * (double) (float) Math.PI * 2.0D);
		this.motionX = (double) (-MathHelper.sin(f * 3.141593F / 180.0F) * 0.02F);
		this.motionY = (double) 0.2F;
		this.motionZ = (double) (-MathHelper.cos(f * 3.141593F / 180.0F) * 0.02F);
		this.fuse = 80;
		this.prevPosX = d;
		this.prevPosY = d1;
		this.prevPosZ = d2;
	}

	protected void entityInit() {
	}

	protected boolean func_25017_l() {
		return false;
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= (double) 0.04F;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double) 0.98F;
		this.motionY *= (double) 0.98F;
		this.motionZ *= (double) 0.98F;
		if (this.onGround) {
			this.motionX *= (double) 0.7F;
			this.motionZ *= (double) 0.7F;
			this.motionY *= -0.5D;
		}

		if (this.fuse-- <= 0) {
			if (!this.worldObj.multiplayerWorld) {
				this.setEntityDead();
				this.explode();
			} else {
				this.setEntityDead();
			}
		} else {
			this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
		}

	}

	private void explode() {
		float f = 4.0F;
		this.worldObj.createExplosion((Entity) null, this.posX, this.posY, this.posZ, f);
	}

	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setByte("Fuse", (byte) this.fuse);
	}

	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.fuse = nbttagcompound.getByte("Fuse");
	}
}
