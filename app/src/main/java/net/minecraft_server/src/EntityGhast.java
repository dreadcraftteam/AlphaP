package net.minecraft_server.src;

public class EntityGhast extends EntityFlying implements IMob {
	public int courseChangeCooldown = 0;
	public double waypointX;
	public double waypointY;
	public double waypointZ;
	private Entity targetedEntity = null;
	private int aggroCooldown = 0;
	public int prevAttackCounter = 0;
	public int attackCounter = 0;

	public EntityGhast(World world) {
		super(world);
		this.texture = "/mob/ghast.png";
		this.setSize(4.0F, 4.0F);
		this.isImmuneToFire = true;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, (byte) 0);
	}

	public void onUpdate() {
		super.onUpdate();
		byte byte0 = this.dataWatcher.getWatchableObjectByte(16);
		this.texture = byte0 != 1 ? "/mob/ghast.png" : "/mob/ghast_fire.png";
	}

	protected void updatePlayerActionState() {
		if (!this.worldObj.multiplayerWorld && this.worldObj.difficultySetting == 0) {
			this.setEntityDead();
		}

		this.func_27013_Q();
		this.prevAttackCounter = this.attackCounter;
		double d = this.waypointX - this.posX;
		double d1 = this.waypointY - this.posY;
		double d2 = this.waypointZ - this.posZ;
		double d3 = (double) MathHelper.sqrt_double(d * d + d1 * d1 + d2 * d2);
		if (d3 < 1.0D || d3 > 60.0D) {
			this.waypointX = this.posX + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
			this.waypointY = this.posY + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
			this.waypointZ = this.posZ + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
		}

		if (this.courseChangeCooldown-- <= 0) {
			this.courseChangeCooldown += this.rand.nextInt(5) + 2;
			if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, d3)) {
				this.motionX += d / d3 * 0.1D;
				this.motionY += d1 / d3 * 0.1D;
				this.motionZ += d2 / d3 * 0.1D;
			} else {
				this.waypointX = this.posX;
				this.waypointY = this.posY;
				this.waypointZ = this.posZ;
			}
		}

		if (this.targetedEntity != null && this.targetedEntity.isDead) {
			this.targetedEntity = null;
		}

		if (this.targetedEntity == null || this.aggroCooldown-- <= 0) {
			this.targetedEntity = this.worldObj.getClosestPlayerToEntity(this, 100.0D);
			if (this.targetedEntity != null) {
				this.aggroCooldown = 20;
			}
		}

		double d4 = 64.0D;
		if (this.targetedEntity != null && this.targetedEntity.getDistanceSqToEntity(this) < d4 * d4) {
			double byte0 = this.targetedEntity.posX - this.posX;
			double d6 = this.targetedEntity.boundingBox.minY + (double) (this.targetedEntity.height / 2.0F)
					- (this.posY + (double) (this.height / 2.0F));
			double d7 = this.targetedEntity.posZ - this.posZ;
			this.renderYawOffset = this.rotationYaw = -((float) Math.atan2(byte0, d7)) * 180.0F / 3.141593F;
			if (this.canEntityBeSeen(this.targetedEntity)) {
				if (this.attackCounter == 10) {
					this.worldObj.playSoundAtEntity(this, "mob.ghast.charge", this.getSoundVolume(),
							(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
				}

				++this.attackCounter;
				if (this.attackCounter == 20) {
					this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", this.getSoundVolume(),
							(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
					EntityFireball entityfireball = new EntityFireball(this.worldObj, this, byte0, d6, d7);
					double d8 = 4.0D;
					Vec3D vec3d = this.getLook(1.0F);
					entityfireball.posX = this.posX + vec3d.xCoord * d8;
					entityfireball.posY = this.posY + (double) (this.height / 2.0F) + 0.5D;
					entityfireball.posZ = this.posZ + vec3d.zCoord * d8;
					this.worldObj.entityJoinedWorld(entityfireball);
					this.attackCounter = -40;
				}
			} else if (this.attackCounter > 0) {
				--this.attackCounter;
			}
		} else {
			this.renderYawOffset = this.rotationYaw = -((float) Math.atan2(this.motionX, this.motionZ)) * 180.0F
					/ 3.141593F;
			if (this.attackCounter > 0) {
				--this.attackCounter;
			}
		}

		if (!this.worldObj.multiplayerWorld) {
			byte b21 = this.dataWatcher.getWatchableObjectByte(16);
			byte byte1 = (byte) (this.attackCounter <= 10 ? 0 : 1);
			if (b21 != byte1) {
				this.dataWatcher.updateObject(16, byte1);
			}
		}

	}

	private boolean isCourseTraversable(double d, double d1, double d2, double d3) {
		double d4 = (this.waypointX - this.posX) / d3;
		double d5 = (this.waypointY - this.posY) / d3;
		double d6 = (this.waypointZ - this.posZ) / d3;
		AxisAlignedBB axisalignedbb = this.boundingBox.copy();

		for (int i = 1; (double) i < d3; ++i) {
			axisalignedbb.offset(d4, d5, d6);
			if (this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb).size() > 0) {
				return false;
			}
		}

		return true;
	}

	protected String getLivingSound() {
		return "mob.ghast.moan";
	}

	protected String getHurtSound() {
		return "mob.ghast.scream";
	}

	protected String getDeathSound() {
		return "mob.ghast.death";
	}

	protected int getDropItemId() {
		return Item.gunpowder.shiftedIndex;
	}

	protected float getSoundVolume() {
		return 10.0F;
	}

	public boolean getCanSpawnHere() {
		return this.rand.nextInt(20) == 0 && super.getCanSpawnHere() && this.worldObj.difficultySetting > 0;
	}

	public int getMaxSpawnedInChunk() {
		return 1;
	}
}
