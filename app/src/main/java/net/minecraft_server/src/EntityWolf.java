package net.minecraft_server.src;

import java.util.Iterator;
import java.util.List;

public class EntityWolf extends EntityAnimal {
	private boolean field_25039_a = false;
	private float field_25038_b;
	private float field_25044_c;
	private boolean isWet;
	private boolean field_25042_g;
	private float field_25041_h;
	private float field_25040_i;

	public EntityWolf(World world) {
		super(world);
		this.texture = "/mob/wolf.png";
		this.setSize(0.8F, 0.8F);
		this.moveSpeed = 1.1F;
		this.health = 8;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, (byte) 0);
		this.dataWatcher.addObject(17, "");
		this.dataWatcher.addObject(18, new Integer(this.health));
	}

	protected boolean func_25017_l() {
		return false;
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Angry", this.getIsAngry());
		nbttagcompound.setBoolean("Sitting", this.getIsSitting());
		if (this.getOwner() == null) {
			nbttagcompound.setString("Owner", "");
		} else {
			nbttagcompound.setString("Owner", this.getOwner());
		}

	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.setIsAngry(nbttagcompound.getBoolean("Angry"));
		this.setIsSitting(nbttagcompound.getBoolean("Sitting"));
		String s = nbttagcompound.getString("Owner");
		if (s.length() > 0) {
			this.setOwner(s);
			this.setIsTamed(true);
		}

	}

	protected boolean func_25020_s() {
		return !this.func_25030_y();
	}

	protected String getLivingSound() {
		return this.getIsAngry() ? "mob.wolf.growl"
				: (this.rand.nextInt(3) == 0
						? (this.func_25030_y() && this.dataWatcher.getWatchableObjectInteger(18) < 10 ? "mob.wolf.whine"
								: "mob.wolf.panting")
						: "mob.wolf.bark");
	}

	protected String getHurtSound() {
		return "mob.wolf.hurt";
	}

	protected String getDeathSound() {
		return "mob.wolf.death";
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	protected int getDropItemId() {
		return -1;
	}

	protected void updatePlayerActionState() {
		super.updatePlayerActionState();
		if (!this.hasAttacked && !this.getGotPath() && this.func_25030_y() && this.ridingEntity == null) {
			EntityPlayer list1 = this.worldObj.getPlayerEntityByName(this.getOwner());
			if (list1 != null) {
				float f = list1.getDistanceToEntity(this);
				if (f > 5.0F) {
					this.setPathEntity(list1, f);
				}
			} else if (!this.isInWater()) {
				this.setIsSitting(true);
			}
		} else if (this.playerToAttack == null && !this.getGotPath() && !this.func_25030_y()
				&& this.worldObj.rand.nextInt(100) == 0) {
			List list = this.worldObj.getEntitiesWithinAABB(EntitySheep.class,
					AxisAlignedBB.getBoundingBoxFromPool(this.posX, this.posY, this.posZ, this.posX + 1.0D,
							this.posY + 1.0D, this.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
			if (!list.isEmpty()) {
				this.setEntityToAttack((Entity) list.get(this.worldObj.rand.nextInt(list.size())));
			}
		}

		if (this.isInWater()) {
			this.setIsSitting(false);
		}

		if (!this.worldObj.multiplayerWorld) {
			this.dataWatcher.updateObject(18, this.health);
		}

	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.field_25039_a = false;
		if (this.func_25021_O() && !this.getGotPath() && !this.getIsAngry()) {
			Entity entity = this.getCurrentTarget();
			if (entity instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entity;
				ItemStack itemstack = entityplayer.inventory.getCurrentItem();
				if (itemstack != null) {
					if (!this.func_25030_y() && itemstack.itemID == Item.bone.shiftedIndex) {
						this.field_25039_a = true;
					} else if (this.func_25030_y() && Item.itemsList[itemstack.itemID] instanceof ItemFood) {
						this.field_25039_a = ((ItemFood) Item.itemsList[itemstack.itemID]).func_25010_k();
					}
				}
			}
		}

		if (!this.isMultiplayerEntity && this.isWet && !this.field_25042_g && !this.getGotPath() && this.onGround) {
			this.field_25042_g = true;
			this.field_25041_h = 0.0F;
			this.field_25040_i = 0.0F;
			this.worldObj.sendTrackedEntityStatusUpdatePacket(this, (byte) 8);
		}

	}

	public void onUpdate() {
		super.onUpdate();
		this.field_25044_c = this.field_25038_b;
		if (this.field_25039_a) {
			this.field_25038_b += (1.0F - this.field_25038_b) * 0.4F;
		} else {
			this.field_25038_b += (0.0F - this.field_25038_b) * 0.4F;
		}

		if (this.field_25039_a) {
			this.numTicksToChaseTarget = 10;
		}

		if (this.func_27008_Y()) {
			this.isWet = true;
			this.field_25042_g = false;
			this.field_25041_h = 0.0F;
			this.field_25040_i = 0.0F;
		} else if ((this.isWet || this.field_25042_g) && this.field_25042_g) {
			if (this.field_25041_h == 0.0F) {
				this.worldObj.playSoundAtEntity(this, "mob.wolf.shake", this.getSoundVolume(),
						(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}

			this.field_25040_i = this.field_25041_h;
			this.field_25041_h += 0.05F;
			if (this.field_25040_i >= 2.0F) {
				this.isWet = false;
				this.field_25042_g = false;
				this.field_25040_i = 0.0F;
				this.field_25041_h = 0.0F;
			}

			if (this.field_25041_h > 0.4F) {
				float f = (float) this.boundingBox.minY;
				int i = (int) (MathHelper.sin((this.field_25041_h - 0.4F) * 3.141593F) * 7.0F);

				for (int j = 0; j < i; ++j) {
					float f1 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					this.worldObj.spawnParticle("splash", this.posX + (double) f1, (double) (f + 0.8F),
							this.posZ + (double) f2, this.motionX, this.motionY, this.motionZ);
				}
			}
		}

	}

	public float getEyeHeight() {
		return this.height * 0.8F;
	}

	protected int func_25018_n_() {
		return this.getIsSitting() ? 20 : super.func_25018_n_();
	}

	private void setPathEntity(Entity entity, float f) {
		PathEntity pathentity = this.worldObj.getPathToEntity(this, entity, 16.0F);
		if (pathentity == null && f > 12.0F) {
			int i = MathHelper.floor_double(entity.posX) - 2;
			int j = MathHelper.floor_double(entity.posZ) - 2;
			int k = MathHelper.floor_double(entity.boundingBox.minY);

			for (int l = 0; l <= 4; ++l) {
				for (int i1 = 0; i1 <= 4; ++i1) {
					if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.worldObj.isBlockNormalCube(i + l, k - 1, j + i1)
							&& !this.worldObj.isBlockNormalCube(i + l, k, j + i1)
							&& !this.worldObj.isBlockNormalCube(i + l, k + 1, j + i1)) {
						this.setLocationAndAngles((double) ((float) (i + l) + 0.5F), (double) k,
								(double) ((float) (j + i1) + 0.5F), this.rotationYaw, this.rotationPitch);
						return;
					}
				}
			}
		} else {
			this.setPathToEntity(pathentity);
		}

	}

	protected boolean func_25026_u() {
		return this.getIsSitting() || this.field_25042_g;
	}

	public boolean attackEntityFrom(Entity entity, int i) {
		this.setIsSitting(false);
		if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow)) {
			i = (i + 1) / 2;
		}

		if (!super.attackEntityFrom((Entity) entity, i)) {
			return false;
		} else {
			if (!this.func_25030_y() && !this.getIsAngry()) {
				if (entity instanceof EntityPlayer) {
					this.setIsAngry(true);
					this.playerToAttack = (Entity) entity;
				}

				if (entity instanceof EntityArrow && ((EntityArrow) entity).owner != null) {
					entity = ((EntityArrow) entity).owner;
				}

				if (entity instanceof EntityLiving) {
					List list = this.worldObj
							.getEntitiesWithinAABB(EntityWolf.class,
									AxisAlignedBB.getBoundingBoxFromPool(this.posX, this.posY, this.posZ,
											this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)
											.expand(16.0D, 4.0D, 16.0D));
					Iterator iterator = list.iterator();

					while (iterator.hasNext()) {
						Entity entity1 = (Entity) iterator.next();
						EntityWolf entitywolf = (EntityWolf) entity1;
						if (!entitywolf.func_25030_y() && entitywolf.playerToAttack == null) {
							entitywolf.playerToAttack = (Entity) entity;
							if (entity instanceof EntityPlayer) {
								entitywolf.setIsAngry(true);
							}
						}
					}
				}
			} else if (entity != this && entity != null) {
				if (this.func_25030_y() && entity instanceof EntityPlayer
						&& ((EntityPlayer) entity).username.equalsIgnoreCase(this.getOwner())) {
					return true;
				}

				this.playerToAttack = (Entity) entity;
			}

			return true;
		}
	}

	protected Entity findPlayerToAttack() {
		return this.getIsAngry() ? this.worldObj.getClosestPlayerToEntity(this, 16.0D) : null;
	}

	protected void attackEntity(Entity entity, float f) {
		if (f > 2.0F && f < 6.0F && this.rand.nextInt(10) == 0) {
			if (this.onGround) {
				double byte01 = entity.posX - this.posX;
				double d1 = entity.posZ - this.posZ;
				float f1 = MathHelper.sqrt_double(byte01 * byte01 + d1 * d1);
				this.motionX = byte01 / (double) f1 * 0.5D * (double) 0.8F + this.motionX * (double) 0.2F;
				this.motionZ = d1 / (double) f1 * 0.5D * (double) 0.8F + this.motionZ * (double) 0.2F;
				this.motionY = (double) 0.4F;
			}
		} else if ((double) f < 1.5D && entity.boundingBox.maxY > this.boundingBox.minY
				&& entity.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			byte byte0 = 2;
			if (this.func_25030_y()) {
				byte0 = 4;
			}

			entity.attackEntityFrom(this, byte0);
		}

	}

	public boolean interact(EntityPlayer entityplayer) {
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		if (!this.func_25030_y()) {
			if (itemstack != null && itemstack.itemID == Item.bone.shiftedIndex && !this.getIsAngry()) {
				--itemstack.stackSize;
				if (itemstack.stackSize <= 0) {
					entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem,
							(ItemStack) null);
				}

				if (!this.worldObj.multiplayerWorld) {
					if (this.rand.nextInt(3) == 0) {
						this.setIsTamed(true);
						this.setPathToEntity((PathEntity) null);
						this.setIsSitting(true);
						this.health = 20;
						this.setOwner(entityplayer.username);
						this.isNowTamed(true);
						this.worldObj.sendTrackedEntityStatusUpdatePacket(this, (byte) 7);
					} else {
						this.isNowTamed(false);
						this.worldObj.sendTrackedEntityStatusUpdatePacket(this, (byte) 6);
					}
				}

				return true;
			}
		} else {
			if (itemstack != null && Item.itemsList[itemstack.itemID] instanceof ItemFood) {
				ItemFood itemfood = (ItemFood) Item.itemsList[itemstack.itemID];
				if (itemfood.func_25010_k() && this.dataWatcher.getWatchableObjectInteger(18) < 20) {
					--itemstack.stackSize;
					if (itemstack.stackSize <= 0) {
						entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem,
								(ItemStack) null);
					}

					this.heal(((ItemFood) Item.porkRaw).getHealAmount());
					return true;
				}
			}

			if (entityplayer.username.equalsIgnoreCase(this.getOwner())) {
				if (!this.worldObj.multiplayerWorld) {
					this.setIsSitting(!this.getIsSitting());
					this.isJumping = false;
					this.setPathToEntity((PathEntity) null);
				}

				return true;
			}
		}

		return false;
	}

	void isNowTamed(boolean flag) {
		String s = "heart";
		if (!flag) {
			s = "smoke";
		}

		for (int i = 0; i < 7; ++i) {
			double d = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			this.worldObj.spawnParticle(s,
					this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
					this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height),
					this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d, d1, d2);
		}

	}

	public int getMaxSpawnedInChunk() {
		return 8;
	}

	public String getOwner() {
		return this.dataWatcher.getWatchableObjectString(17);
	}

	public void setOwner(String s) {
		this.dataWatcher.updateObject(17, s);
	}

	public boolean getIsSitting() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	public void setIsSitting(boolean flag) {
		byte byte0 = this.dataWatcher.getWatchableObjectByte(16);
		if (flag) {
			this.dataWatcher.updateObject(16, (byte) (byte0 | 1));
		} else {
			this.dataWatcher.updateObject(16, (byte) (byte0 & -2));
		}

	}

	public boolean getIsAngry() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
	}

	public void setIsAngry(boolean flag) {
		byte byte0 = this.dataWatcher.getWatchableObjectByte(16);
		if (flag) {
			this.dataWatcher.updateObject(16, (byte) (byte0 | 2));
		} else {
			this.dataWatcher.updateObject(16, (byte) (byte0 & -3));
		}

	}

	public boolean func_25030_y() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
	}

	public void setIsTamed(boolean flag) {
		byte byte0 = this.dataWatcher.getWatchableObjectByte(16);
		if (flag) {
			this.dataWatcher.updateObject(16, (byte) (byte0 | 4));
		} else {
			this.dataWatcher.updateObject(16, (byte) (byte0 & -5));
		}

	}
}
