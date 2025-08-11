package net.minecraft_server.src;

public class EntityItem extends Entity {
	public ItemStack item;
	private int field_9170_e;
	public int age = 0;
	public int delayBeforeCanPickup;
	private int health = 5;
	public float field_432_ae = (float) (Math.random() * Math.PI * 2.0D);

	public EntityItem(World world, double d, double d1, double d2, ItemStack itemstack) {
		super(world);
		this.setSize(0.25F, 0.25F);
		this.yOffset = this.height / 2.0F;
		this.setPosition(d, d1, d2);
		this.item = itemstack;
		this.rotationYaw = (float) (Math.random() * 360.0D);
		this.motionX = (double) ((float) (Math.random() * (double) 0.2F - (double) 0.1F));
		this.motionY = (double) 0.2F;
		this.motionZ = (double) ((float) (Math.random() * (double) 0.2F - (double) 0.1F));
	}

	protected boolean func_25017_l() {
		return false;
	}

	public EntityItem(World world) {
		super(world);
		this.setSize(0.25F, 0.25F);
		this.yOffset = this.height / 2.0F;
	}

	protected void entityInit() {
	}

	public void onUpdate() {
		super.onUpdate();
		if (this.delayBeforeCanPickup > 0) {
			--this.delayBeforeCanPickup;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= (double) 0.04F;
		if (this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY),
				MathHelper.floor_double(this.posZ)) == Material.lava) {
			this.motionY = (double) 0.2F;
			this.motionX = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			this.motionZ = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			this.worldObj.playSoundAtEntity(this, "random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
		}

		this.func_28005_g(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		float f = 0.98F;
		if (this.onGround) {
			f = 0.5880001F;
			int i = this.worldObj.getBlockId(MathHelper.floor_double(this.posX),
					MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
			if (i > 0) {
				f = Block.blocksList[i].slipperiness * 0.98F;
			}
		}

		this.motionX *= (double) f;
		this.motionY *= (double) 0.98F;
		this.motionZ *= (double) f;
		if (this.onGround) {
			this.motionY *= -0.5D;
		}

		++this.field_9170_e;
		++this.age;
		if (this.age >= 6000) {
			this.setEntityDead();
		}

	}

	public boolean handleWaterMovement() {
		return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
	}

	protected void dealFireDamage(int i) {
		this.attackEntityFrom((Entity) null, i);
	}

	public boolean attackEntityFrom(Entity entity, int i) {
		this.setBeenAttacked();
		this.health -= i;
		if (this.health <= 0) {
			this.setEntityDead();
		}

		return false;
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("Health", (short) ((byte) this.health));
		nbttagcompound.setShort("Age", (short) this.age);
		nbttagcompound.setCompoundTag("Item", this.item.writeToNBT(new NBTTagCompound()));
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.health = nbttagcompound.getShort("Health") & 255;
		this.age = nbttagcompound.getShort("Age");
		NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Item");
		this.item = new ItemStack(nbttagcompound1);
	}

	public void onCollideWithPlayer(EntityPlayer entityplayer) {
		if (!this.worldObj.multiplayerWorld) {
			int i = this.item.stackSize;
			if (this.delayBeforeCanPickup == 0 && entityplayer.inventory.addItemStackToInventory(this.item)) {
				this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F,
						((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				entityplayer.onItemPickup(this, i);
				if (this.item.stackSize <= 0) {
					this.setEntityDead();
				}
			}

		}
	}
}
