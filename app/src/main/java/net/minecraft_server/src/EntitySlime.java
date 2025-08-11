package net.minecraft_server.src;

public class EntitySlime extends EntityLiving implements IMob {
	public float field_401_a;
	public float field_400_b;
	private int ticksTillJump = 0;

	public EntitySlime(World world) {
		super(world);
		this.texture = "/mob/slime.png";
		int i = 1 << this.rand.nextInt(3);
		this.yOffset = 0.0F;
		this.ticksTillJump = this.rand.nextInt(20) + 10;
		this.setSlimeSize(i);
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, new Byte((byte) 1));
	}

	public void setSlimeSize(int i) {
		this.dataWatcher.updateObject(16, new Byte((byte) i));
		this.setSize(0.6F * (float) i, 0.6F * (float) i);
		this.health = i * i;
		this.setPosition(this.posX, this.posY, this.posZ);
	}

	public int func_25027_m() {
		return this.dataWatcher.getWatchableObjectByte(16);
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("Size", this.func_25027_m() - 1);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.setSlimeSize(nbttagcompound.getInteger("Size") + 1);
	}

	public void onUpdate() {
		this.field_400_b = this.field_401_a;
		boolean flag = this.onGround;
		super.onUpdate();
		if (this.onGround && !flag) {
			int i = this.func_25027_m();

			for (int j = 0; j < i * 8; ++j) {
				float f = this.rand.nextFloat() * 3.141593F * 2.0F;
				float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
				float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
				float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;
				this.worldObj.spawnParticle("slime", this.posX + (double) f2, this.boundingBox.minY,
						this.posZ + (double) f3, 0.0D, 0.0D, 0.0D);
			}

			if (i > 2) {
				this.worldObj.playSoundAtEntity(this, "mob.slime", this.getSoundVolume(),
						((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			}

			this.field_401_a = -0.5F;
		}

		this.field_401_a *= 0.6F;
	}

	protected void updatePlayerActionState() {
		this.func_27013_Q();
		EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 16.0D);
		if (entityplayer != null) {
			this.faceEntity(entityplayer, 10.0F, 20.0F);
		}

		if (this.onGround && this.ticksTillJump-- <= 0) {
			this.ticksTillJump = this.rand.nextInt(20) + 10;
			if (entityplayer != null) {
				this.ticksTillJump /= 3;
			}

			this.isJumping = true;
			if (this.func_25027_m() > 1) {
				this.worldObj.playSoundAtEntity(this, "mob.slime", this.getSoundVolume(),
						((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
			}

			this.field_401_a = 1.0F;
			this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
			this.moveForward = (float) (1 * this.func_25027_m());
		} else {
			this.isJumping = false;
			if (this.onGround) {
				this.moveStrafing = this.moveForward = 0.0F;
			}
		}

	}

	public void setEntityDead() {
		int i = this.func_25027_m();
		if (!this.worldObj.multiplayerWorld && i > 1 && this.health == 0) {
			for (int j = 0; j < 4; ++j) {
				float f = ((float) (j % 2) - 0.5F) * (float) i / 4.0F;
				float f1 = ((float) (j / 2) - 0.5F) * (float) i / 4.0F;
				EntitySlime entityslime = new EntitySlime(this.worldObj);
				entityslime.setSlimeSize(i / 2);
				entityslime.setLocationAndAngles(this.posX + (double) f, this.posY + 0.5D, this.posZ + (double) f1,
						this.rand.nextFloat() * 360.0F, 0.0F);
				this.worldObj.entityJoinedWorld(entityslime);
			}
		}

		super.setEntityDead();
	}

	public void onCollideWithPlayer(EntityPlayer entityplayer) {
		int i = this.func_25027_m();
		if (i > 1 && this.canEntityBeSeen(entityplayer)
				&& (double) this.getDistanceToEntity(entityplayer) < 0.6D * (double) i
				&& entityplayer.attackEntityFrom(this, i)) {
			this.worldObj.playSoundAtEntity(this, "mob.slimeattack", 1.0F,
					(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
		}

	}

	protected String getHurtSound() {
		return "mob.slime";
	}

	protected String getDeathSound() {
		return "mob.slime";
	}

	protected int getDropItemId() {
		return this.func_25027_m() == 1 ? Item.slimeBall.shiftedIndex : 0;
	}

	public boolean getCanSpawnHere() {
		Chunk chunk = this.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(this.posX),
				MathHelper.floor_double(this.posZ));
		return (this.func_25027_m() == 1 || this.worldObj.difficultySetting > 0) && this.rand.nextInt(10) == 0
				&& chunk.func_334_a(987234911L).nextInt(10) == 0 && this.posY < 16.0D;
	}

	protected float getSoundVolume() {
		return 0.6F;
	}
}
