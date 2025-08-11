package net.minecraft_server.src;

public class EntityPig extends EntityAnimal {
	public EntityPig(World world) {
		super(world);
		this.texture = "/mob/pig.png";
		this.setSize(0.9F, 0.9F);
	}

	protected void entityInit() {
		this.dataWatcher.addObject(16, (byte) 0);
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Saddle", this.getSaddled());
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.setSaddled(nbttagcompound.getBoolean("Saddle"));
	}

	protected String getLivingSound() {
		return "mob.pig";
	}

	protected String getHurtSound() {
		return "mob.pig";
	}

	protected String getDeathSound() {
		return "mob.pigdeath";
	}

	public boolean interact(EntityPlayer entityplayer) {
		if (!this.getSaddled() || this.worldObj.multiplayerWorld
				|| this.riddenByEntity != null && this.riddenByEntity != entityplayer) {
			return false;
		} else {
			entityplayer.mountEntity(this);
			return true;
		}
	}

	protected int getDropItemId() {
		return this.fire > 0 ? Item.porkCooked.shiftedIndex : Item.porkRaw.shiftedIndex;
	}

	public boolean getSaddled() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	public void setSaddled(boolean flag) {
		if (flag) {
			this.dataWatcher.updateObject(16, (byte) 1);
		} else {
			this.dataWatcher.updateObject(16, (byte) 0);
		}

	}

	public void onStruckByLightning(EntityLightningBolt entitylightningbolt) {
		if (!this.worldObj.multiplayerWorld) {
			EntityPigZombie entitypigzombie = new EntityPigZombie(this.worldObj);
			entitypigzombie.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.worldObj.entityJoinedWorld(entitypigzombie);
			this.setEntityDead();
		}
	}

	protected void fall(float f) {
		super.fall(f);
	}
}
