package net.minecraft_server.src;

public class EntityCreeper extends EntityMob {
	int timeSinceIgnited;
	int lastActiveTime;

	public EntityCreeper(World world) {
		super(world);
		this.texture = "/mob/creeper.png";
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, (byte) -1);
		this.dataWatcher.addObject(17, (byte) 0);
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		if (this.dataWatcher.getWatchableObjectByte(17) == 1) {
			nbttagcompound.setBoolean("powered", true);
		}

	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.dataWatcher.updateObject(17, (byte) (nbttagcompound.getBoolean("powered") ? 1 : 0));
	}

	protected void func_28013_b(Entity entity, float f) {
		if (!this.worldObj.multiplayerWorld) {
			if (this.timeSinceIgnited > 0) {
				this.setCreeperState(-1);
				--this.timeSinceIgnited;
				if (this.timeSinceIgnited < 0) {
					this.timeSinceIgnited = 0;
				}
			}

		}
	}

	public void onUpdate() {
		this.lastActiveTime = this.timeSinceIgnited;
		if (this.worldObj.multiplayerWorld) {
			int i = this.getCreeperState();
			if (i > 0 && this.timeSinceIgnited == 0) {
				this.worldObj.playSoundAtEntity(this, "random.fuse", 1.0F, 0.5F);
			}

			this.timeSinceIgnited += i;
			if (this.timeSinceIgnited < 0) {
				this.timeSinceIgnited = 0;
			}

			if (this.timeSinceIgnited >= 30) {
				this.timeSinceIgnited = 30;
			}
		}

		super.onUpdate();
		if (this.playerToAttack == null && this.timeSinceIgnited > 0) {
			this.setCreeperState(-1);
			--this.timeSinceIgnited;
			if (this.timeSinceIgnited < 0) {
				this.timeSinceIgnited = 0;
			}
		}

	}

	protected String getHurtSound() {
		return "mob.creeper";
	}

	protected String getDeathSound() {
		return "mob.creeperdeath";
	}

	public void onDeath(Entity entity) {
		super.onDeath(entity);
		if (entity instanceof EntitySkeleton) {
			this.dropItem(Item.record13.shiftedIndex + this.rand.nextInt(2), 1);
		}

	}

	protected void attackEntity(Entity entity, float f) {
		if (!this.worldObj.multiplayerWorld) {
			int i = this.getCreeperState();
			if (i <= 0 && f < 3.0F || i > 0 && f < 7.0F) {
				if (this.timeSinceIgnited == 0) {
					this.worldObj.playSoundAtEntity(this, "random.fuse", 1.0F, 0.5F);
				}

				this.setCreeperState(1);
				++this.timeSinceIgnited;
				if (this.timeSinceIgnited >= 30) {
					if (this.getPowered()) {
						this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 6.0F);
					} else {
						this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 3.0F);
					}

					this.setEntityDead();
				}

				this.hasAttacked = true;
			} else {
				this.setCreeperState(-1);
				--this.timeSinceIgnited;
				if (this.timeSinceIgnited < 0) {
					this.timeSinceIgnited = 0;
				}
			}

		}
	}

	public boolean getPowered() {
		return this.dataWatcher.getWatchableObjectByte(17) == 1;
	}

	protected int getDropItemId() {
		return Item.gunpowder.shiftedIndex;
	}

	private int getCreeperState() {
		return this.dataWatcher.getWatchableObjectByte(16);
	}

	private void setCreeperState(int i) {
		this.dataWatcher.updateObject(16, (byte) i);
	}

	public void onStruckByLightning(EntityLightningBolt entitylightningbolt) {
		super.onStruckByLightning(entitylightningbolt);
		this.dataWatcher.updateObject(17, (byte) 1);
	}
}
