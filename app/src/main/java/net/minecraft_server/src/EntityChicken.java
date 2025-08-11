package net.minecraft_server.src;

public class EntityChicken extends EntityAnimal {
	public boolean field_392_a = false;
	public float field_391_b = 0.0F;
	public float field_395_ad = 0.0F;
	public float field_394_ae;
	public float field_393_af;
	public float field_390_ai = 1.0F;
	public int timeUntilNextEgg;

	public EntityChicken(World world) {
		super(world);
		this.texture = "/mob/chicken.png";
		this.setSize(0.3F, 0.4F);
		this.health = 4;
		this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.field_393_af = this.field_391_b;
		this.field_394_ae = this.field_395_ad;
		this.field_395_ad = (float) ((double) this.field_395_ad + (double) (this.onGround ? -1 : 4) * 0.3D);
		if (this.field_395_ad < 0.0F) {
			this.field_395_ad = 0.0F;
		}

		if (this.field_395_ad > 1.0F) {
			this.field_395_ad = 1.0F;
		}

		if (!this.onGround && this.field_390_ai < 1.0F) {
			this.field_390_ai = 1.0F;
		}

		this.field_390_ai = (float) ((double) this.field_390_ai * 0.9D);
		if (!this.onGround && this.motionY < 0.0D) {
			this.motionY *= 0.6D;
		}

		this.field_391_b += this.field_390_ai * 2.0F;
		if (!this.worldObj.multiplayerWorld && --this.timeUntilNextEgg <= 0) {
			this.worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F,
					(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			this.dropItem(Item.egg.shiftedIndex, 1);
			this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
		}

	}

	protected void fall(float f) {
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
	}

	protected String getLivingSound() {
		return "mob.chicken";
	}

	protected String getHurtSound() {
		return "mob.chickenhurt";
	}

	protected String getDeathSound() {
		return "mob.chickenhurt";
	}

	protected int getDropItemId() {
		return Item.feather.shiftedIndex;
	}
}
