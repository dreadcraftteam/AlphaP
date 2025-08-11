package net.minecraft_server.src;

public class EntityMob extends EntityCreature implements IMob {
	protected int attackStrength = 2;

	public EntityMob(World world) {
		super(world);
		this.health = 20;
	}

	public void onLivingUpdate() {
		float f = this.getEntityBrightness(1.0F);
		if (f > 0.5F) {
			this.age += 2;
		}

		super.onLivingUpdate();
	}

	public void onUpdate() {
		super.onUpdate();
		if (!this.worldObj.multiplayerWorld && this.worldObj.difficultySetting == 0) {
			this.setEntityDead();
		}

	}

	protected Entity findPlayerToAttack() {
		EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 16.0D);
		return entityplayer != null && this.canEntityBeSeen(entityplayer) ? entityplayer : null;
	}

	public boolean attackEntityFrom(Entity entity, int i) {
		if (super.attackEntityFrom(entity, i)) {
			if (this.riddenByEntity != entity && this.ridingEntity != entity) {
				if (entity != this) {
					this.playerToAttack = entity;
				}

				return true;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	protected void attackEntity(Entity entity, float f) {
		if (this.attackTime <= 0 && f < 2.0F && entity.boundingBox.maxY > this.boundingBox.minY
				&& entity.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			entity.attackEntityFrom(this, this.attackStrength);
		}

	}

	protected float getBlockPathWeight(int i, int j, int k) {
		return 0.5F - this.worldObj.getLightBrightness(i, j, k);
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
	}

	public boolean getCanSpawnHere() {
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.boundingBox.minY);
		int k = MathHelper.floor_double(this.posZ);
		if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > this.rand.nextInt(32)) {
			return false;
		} else {
			int l = this.worldObj.getBlockLightValue(i, j, k);
			if (this.worldObj.func_27067_u()) {
				int i1 = this.worldObj.skylightSubtracted;
				this.worldObj.skylightSubtracted = 10;
				l = this.worldObj.getBlockLightValue(i, j, k);
				this.worldObj.skylightSubtracted = i1;
			}

			return l <= this.rand.nextInt(8) && super.getCanSpawnHere();
		}
	}
}
