package net.minecraft_server.src;

public class EntityFallingSand extends Entity {
	public int blockID;
	public int fallTime = 0;

	public EntityFallingSand(World world) {
		super(world);
	}

	public EntityFallingSand(World world, double d, double d1, double d2, int i) {
		super(world);
		this.blockID = i;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.yOffset = this.height / 2.0F;
		this.setPosition(d, d1, d2);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = d;
		this.prevPosY = d1;
		this.prevPosZ = d2;
	}

	protected boolean func_25017_l() {
		return false;
	}

	protected void entityInit() {
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void onUpdate() {
		if (this.blockID == 0) {
			this.setEntityDead();
		} else {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			++this.fallTime;
			if (this.posY > 100000.0D) {
				this.motionY -= 0.004D;
			} else {
				this.motionY -= 0.04D;
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double) 0.98F;
			this.motionY *= (double) 0.98F;
			this.motionZ *= (double) 0.98F;
			int i = MathHelper.floor_double(this.posX);
			int j = MathHelper.floor_double(this.posY);
			int k = MathHelper.floor_double(this.posZ);
			if (this.worldObj.getBlockId(i, j, k) == this.blockID) {
				this.worldObj.setBlockWithNotify(i, j, k, 0);
			}

			if (this.onGround) {
				this.motionX *= (double) 0.7F;
				this.motionZ *= (double) 0.7F;
				this.motionY *= -0.5D;
				this.setEntityDead();
				if ((!this.worldObj.canBlockBePlacedAt(this.blockID, i, j, k, true, 1)
						|| BlockSand.canFallBelow(this.worldObj, i, j - 1, k)
						|| !this.worldObj.setBlockWithNotify(i, j, k, this.blockID))
						&& !this.worldObj.multiplayerWorld) {
					this.dropItem(this.blockID, 1);
				}
			} else if (this.fallTime > 1000 && !this.worldObj.multiplayerWorld) {
				this.dropItem(this.blockID, 1);
				this.setEntityDead();
			}

		}
	}

	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setByte("Tile", (byte) this.blockID);
	}

	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.blockID = nbttagcompound.getByte("Tile") & 255;
	}
}
