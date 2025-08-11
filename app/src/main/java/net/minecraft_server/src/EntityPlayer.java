package net.minecraft_server.src;

import java.util.Iterator;
import java.util.List;

public abstract class EntityPlayer extends EntityLiving {
	public InventoryPlayer inventory = new InventoryPlayer(this);
	public Container personalCraftingInventory;
	public Container currentCraftingInventory;
	public byte field_9152_am = 0;
	public int score = 0;
	public float prevCameraYaw;
	public float cameraYaw;
	public boolean isSwinging = false;
	public int swingProgressInt = 0;
	public String username;
	public int dimension;
	public double field_20047_ay;
	public double field_20046_az;
	public double field_20051_aA;
	public double field_20050_aB;
	public double field_20049_aC;
	public double field_20048_aD;
	protected boolean sleeping;
	public ChunkCoordinates playerLocation;
	private int sleepTimer;
	public float field_22066_z;
	public float field_22067_A;
	private ChunkCoordinates spawnChunk;
	private ChunkCoordinates field_27995_d;
	public int timeUntilPortal = 20;
	protected boolean inPortal = false;
	public float timeInPortal;
	private int damageRemainder = 0;
	public EntityFish fishEntity = null;

	public EntityPlayer(World world) {
		super(world);
		this.personalCraftingInventory = new ContainerPlayer(this.inventory, !world.multiplayerWorld);
		this.currentCraftingInventory = this.personalCraftingInventory;
		this.yOffset = 1.62F;
		ChunkCoordinates chunkcoordinates = world.getSpawnPoint();
		this.setLocationAndAngles((double) chunkcoordinates.posX + 0.5D, (double) (chunkcoordinates.posY + 1),
				(double) chunkcoordinates.posZ + 0.5D, 0.0F, 0.0F);
		this.health = 20;
		this.entityType = "humanoid";
		this.field_9117_aI = 180.0F;
		this.fireResistance = 20;
		this.texture = "/mob/char.png";
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, (byte) 0);
	}

	public void onUpdate() {
		if (this.func_22057_E()) {
			++this.sleepTimer;
			if (this.sleepTimer > 100) {
				this.sleepTimer = 100;
			}

			if (!this.worldObj.multiplayerWorld) {
				if (!this.isInBed()) {
					this.wakeUpPlayer(true, true, false);
				} else if (this.worldObj.isDaytime()) {
					this.wakeUpPlayer(false, true, true);
				}
			}
		} else if (this.sleepTimer > 0) {
			++this.sleepTimer;
			if (this.sleepTimer >= 110) {
				this.sleepTimer = 0;
			}
		}

		super.onUpdate();
		if (!this.worldObj.multiplayerWorld && this.currentCraftingInventory != null
				&& !this.currentCraftingInventory.canInteractWith(this)) {
			this.usePersonalCraftingInventory();
			this.currentCraftingInventory = this.personalCraftingInventory;
		}

		this.field_20047_ay = this.field_20050_aB;
		this.field_20046_az = this.field_20049_aC;
		this.field_20051_aA = this.field_20048_aD;
		double d = this.posX - this.field_20050_aB;
		double d1 = this.posY - this.field_20049_aC;
		double d2 = this.posZ - this.field_20048_aD;
		double d3 = 10.0D;
		if (d > d3) {
			this.field_20047_ay = this.field_20050_aB = this.posX;
		}

		if (d2 > d3) {
			this.field_20051_aA = this.field_20048_aD = this.posZ;
		}

		if (d1 > d3) {
			this.field_20046_az = this.field_20049_aC = this.posY;
		}

		if (d < -d3) {
			this.field_20047_ay = this.field_20050_aB = this.posX;
		}

		if (d2 < -d3) {
			this.field_20051_aA = this.field_20048_aD = this.posZ;
		}

		if (d1 < -d3) {
			this.field_20046_az = this.field_20049_aC = this.posY;
		}

		this.field_20050_aB += d * 0.25D;
		this.field_20048_aD += d2 * 0.25D;
		this.field_20049_aC += d1 * 0.25D;
		if (this.ridingEntity == null) {
			this.field_27995_d = null;
		}

	}

	protected boolean isMovementBlocked() {
		return this.health <= 0 || this.func_22057_E();
	}

	protected void usePersonalCraftingInventory() {
		this.currentCraftingInventory = this.personalCraftingInventory;
	}

	public void updateRidden() {
		double d = this.posX;
		double d1 = this.posY;
		double d2 = this.posZ;
		super.updateRidden();
		this.prevCameraYaw = this.cameraYaw;
		this.cameraYaw = 0.0F;
		this.func_27015_h(this.posX - d, this.posY - d1, this.posZ - d2);
	}

	protected void updatePlayerActionState() {
		if (this.isSwinging) {
			++this.swingProgressInt;
			if (this.swingProgressInt >= 8) {
				this.swingProgressInt = 0;
				this.isSwinging = false;
			}
		} else {
			this.swingProgressInt = 0;
		}

		this.swingProgress = (float) this.swingProgressInt / 8.0F;
	}

	public void onLivingUpdate() {
		if (this.worldObj.difficultySetting == 0 && this.health < 20 && this.ticksExisted % 20 * 12 == 0) {
			this.heal(1);
		}

		this.inventory.decrementAnimations();
		this.prevCameraYaw = this.cameraYaw;
		super.onLivingUpdate();
		float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		float f1 = (float) Math.atan(-this.motionY * (double) 0.2F) * 15.0F;
		if (f > 0.1F) {
			f = 0.1F;
		}

		if (!this.onGround || this.health <= 0) {
			f = 0.0F;
		}

		if (this.onGround || this.health <= 0) {
			f1 = 0.0F;
		}

		this.cameraYaw += (f - this.cameraYaw) * 0.4F;
		this.cameraPitch += (f1 - this.cameraPitch) * 0.8F;
		if (this.health > 0) {
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
					this.boundingBox.expand(1.0D, 0.0D, 1.0D));
			if (list != null) {
				for (int i = 0; i < list.size(); ++i) {
					Entity entity = (Entity) list.get(i);
					if (!entity.isDead) {
						this.func_171_h(entity);
					}
				}
			}
		}

	}

	private void func_171_h(Entity entity) {
		entity.onCollideWithPlayer(this);
	}

	public void onDeath(Entity entity) {
		super.onDeath(entity);
		this.setSize(0.2F, 0.2F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.motionY = (double) 0.1F;
		if (this.username.equals("Notch")) {
			this.dropPlayerItemWithRandomChoice(new ItemStack(Item.appleRed, 1), true);
		}

		this.inventory.dropAllItems();
		if (entity != null) {
			this.motionX = (double) (-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * 3.141593F / 180.0F)
					* 0.1F);
			this.motionZ = (double) (-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * 3.141593F / 180.0F)
					* 0.1F);
		} else {
			this.motionX = this.motionZ = 0.0D;
		}

		this.yOffset = 0.1F;
	}

	public void addToPlayerScore(Entity entity, int i) {
		this.score += i;
	}

	public void dropCurrentItem() {
		this.dropPlayerItemWithRandomChoice(this.inventory.decrStackSize(this.inventory.currentItem, 1), false);
	}

	public void dropPlayerItem(ItemStack itemstack) {
		this.dropPlayerItemWithRandomChoice(itemstack, false);
	}

	public void dropPlayerItemWithRandomChoice(ItemStack itemstack, boolean flag) {
		if (itemstack != null) {
			EntityItem entityitem = new EntityItem(this.worldObj, this.posX,
					this.posY - (double) 0.3F + (double) this.getEyeHeight(), this.posZ, itemstack);
			entityitem.delayBeforeCanPickup = 40;
			float f = 0.1F;
			float f1;
			float f3;
			if (flag) {
				f1 = this.rand.nextFloat() * 0.5F;
				f3 = this.rand.nextFloat() * 3.141593F * 2.0F;
				entityitem.motionX = (double) (-MathHelper.sin(f3) * f1);
				entityitem.motionZ = (double) (MathHelper.cos(f3) * f1);
				entityitem.motionY = (double) 0.2F;
			} else {
				f1 = 0.3F;
				entityitem.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * 3.141593F)
						* MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F) * f1);
				entityitem.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * 3.141593F)
						* MathHelper.cos(this.rotationPitch / 180.0F * 3.141593F) * f1);
				entityitem.motionY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F * 3.141593F) * f1 + 0.1F);
				f1 = 0.02F;
				f3 = this.rand.nextFloat() * 3.141593F * 2.0F;
				f1 *= this.rand.nextFloat();
				entityitem.motionX += Math.cos((double) f3) * (double) f1;
				entityitem.motionY += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
				entityitem.motionZ += Math.sin((double) f3) * (double) f1;
			}

			this.joinEntityItemWithWorld(entityitem);
		}
	}

	protected void joinEntityItemWithWorld(EntityItem entityitem) {
		this.worldObj.entityJoinedWorld(entityitem);
	}

	public float getCurrentPlayerStrVsBlock(Block block) {
		float f = this.inventory.getStrVsBlock(block);
		if (this.isInsideOfMaterial(Material.water)) {
			f /= 5.0F;
		}

		if (!this.onGround) {
			f /= 5.0F;
		}

		return f;
	}

	public boolean canHarvestBlock(Block block) {
		return this.inventory.canHarvestBlock(block);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getTagList("Inventory");
		this.inventory.readFromNBT(nbttaglist);
		this.dimension = nbttagcompound.getInteger("Dimension");
		this.sleeping = nbttagcompound.getBoolean("Sleeping");
		this.sleepTimer = nbttagcompound.getShort("SleepTimer");
		if (this.sleeping) {
			this.playerLocation = new ChunkCoordinates(MathHelper.floor_double(this.posX),
					MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
			this.wakeUpPlayer(true, true, false);
		}

		if (nbttagcompound.hasKey("SpawnX") && nbttagcompound.hasKey("SpawnY") && nbttagcompound.hasKey("SpawnZ")) {
			this.spawnChunk = new ChunkCoordinates(nbttagcompound.getInteger("SpawnX"),
					nbttagcompound.getInteger("SpawnY"), nbttagcompound.getInteger("SpawnZ"));
		}

	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
		nbttagcompound.setInteger("Dimension", this.dimension);
		nbttagcompound.setBoolean("Sleeping", this.sleeping);
		nbttagcompound.setShort("SleepTimer", (short) this.sleepTimer);
		if (this.spawnChunk != null) {
			nbttagcompound.setInteger("SpawnX", this.spawnChunk.posX);
			nbttagcompound.setInteger("SpawnY", this.spawnChunk.posY);
			nbttagcompound.setInteger("SpawnZ", this.spawnChunk.posZ);
		}

	}

	public void displayGUIChest(IInventory iinventory) {
	}

	public void displayWorkbenchGUI(int i, int j, int k) {
	}

	public void onItemPickup(Entity entity, int i) {
	}

	public float getEyeHeight() {
		return 0.12F;
	}

	protected void resetHeight() {
		this.yOffset = 1.62F;
	}

	public boolean attackEntityFrom(Entity entity, int i) {
		this.age = 0;
		if (this.health <= 0) {
			return false;
		} else {
			if (this.func_22057_E() && !this.worldObj.multiplayerWorld) {
				this.wakeUpPlayer(true, true, false);
			}

			if (entity instanceof EntityMob || entity instanceof EntityArrow) {
				if (this.worldObj.difficultySetting == 0) {
					i = 0;
				}

				if (this.worldObj.difficultySetting == 1) {
					i = i / 3 + 1;
				}

				if (this.worldObj.difficultySetting == 3) {
					i = i * 3 / 2;
				}
			}

			if (i == 0) {
				return false;
			} else {
				Object obj = entity;
				if (entity instanceof EntityArrow && ((EntityArrow) entity).owner != null) {
					obj = ((EntityArrow) entity).owner;
				}

				if (obj instanceof EntityLiving) {
					this.func_25047_a((EntityLiving) obj, false);
				}

				return super.attackEntityFrom(entity, i);
			}
		}
	}

	protected boolean isPVPEnabled() {
		return false;
	}

	protected void func_25047_a(EntityLiving entityliving, boolean flag) {
		if (!(entityliving instanceof EntityCreeper) && !(entityliving instanceof EntityGhast)) {
			if (entityliving instanceof EntityWolf) {
				EntityWolf list = (EntityWolf) entityliving;
				if (list.func_25030_y() && this.username.equals(list.getOwner())) {
					return;
				}
			}

			if (!(entityliving instanceof EntityPlayer) || this.isPVPEnabled()) {
				List list1 = this.worldObj.getEntitiesWithinAABB(EntityWolf.class,
						AxisAlignedBB.getBoundingBoxFromPool(this.posX, this.posY, this.posZ, this.posX + 1.0D,
								this.posY + 1.0D, this.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
				Iterator iterator = list1.iterator();

				while (true) {
					EntityWolf entitywolf1;
					do {
						do {
							do {
								do {
									if (!iterator.hasNext()) {
										return;
									}

									Entity entity = (Entity) iterator.next();
									entitywolf1 = (EntityWolf) entity;
								} while (!entitywolf1.func_25030_y());
							} while (entitywolf1.getEntityToAttack() != null);
						} while (!this.username.equals(entitywolf1.getOwner()));
					} while (flag && entitywolf1.getIsSitting());

					entitywolf1.setIsSitting(false);
					entitywolf1.setEntityToAttack(entityliving);
				}
			}
		}
	}

	protected void damageEntity(int i) {
		int j = 25 - this.inventory.getTotalArmorValue();
		int k = i * j + this.damageRemainder;
		this.inventory.damageArmor(i);
		i = k / 25;
		this.damageRemainder = k % 25;
		super.damageEntity(i);
	}

	public void displayGUIFurnace(TileEntityFurnace tileentityfurnace) {
	}

	public void displayGUIDispenser(TileEntityDispenser tileentitydispenser) {
	}

	public void displayGUIEditSign(TileEntitySign tileentitysign) {
	}

	public void useCurrentItemOnEntity(Entity entity) {
		if (!entity.interact(this)) {
			ItemStack itemstack = this.getCurrentEquippedItem();
			if (itemstack != null && entity instanceof EntityLiving) {
				itemstack.useItemOnEntity((EntityLiving) entity);
				if (itemstack.stackSize <= 0) {
					itemstack.onItemDestroyedByUse(this);
					this.destroyCurrentEquippedItem();
				}
			}

		}
	}

	public ItemStack getCurrentEquippedItem() {
		return this.inventory.getCurrentItem();
	}

	public void destroyCurrentEquippedItem() {
		this.inventory.setInventorySlotContents(this.inventory.currentItem, (ItemStack) null);
	}

	public double getYOffset() {
		return (double) (this.yOffset - 0.5F);
	}

	public void swingItem() {
		this.swingProgressInt = -1;
		this.isSwinging = true;
	}

	public void attackTargetEntityWithCurrentItem(Entity entity) {
		int i = this.inventory.getDamageVsEntity(entity);
		if (i > 0) {
			if (this.motionY < 0.0D) {
				++i;
			}

			entity.attackEntityFrom(this, i);
			ItemStack itemstack = this.getCurrentEquippedItem();
			if (itemstack != null && entity instanceof EntityLiving) {
				itemstack.hitEntity((EntityLiving) entity, this);
				if (itemstack.stackSize <= 0) {
					itemstack.onItemDestroyedByUse(this);
					this.destroyCurrentEquippedItem();
				}
			}

			if (entity instanceof EntityLiving) {
				if (entity.isEntityAlive()) {
					this.func_25047_a((EntityLiving) entity, true);
				}

			}
		}

	}

	public void onItemStackChanged(ItemStack itemstack) {
	}

	public void setEntityDead() {
		super.setEntityDead();
		this.personalCraftingInventory.onCraftGuiClosed(this);
		if (this.currentCraftingInventory != null) {
			this.currentCraftingInventory.onCraftGuiClosed(this);
		}

	}

	public boolean isEntityInsideOpaqueBlock() {
		return !this.sleeping && super.isEntityInsideOpaqueBlock();
	}

	public EnumStatus goToSleep(int i, int j, int k) {
		if (!this.worldObj.multiplayerWorld) {
			label53: {
				if (!this.func_22057_E() && this.isEntityAlive()) {
					if (this.worldObj.worldProvider.field_6167_c) {
						return EnumStatus.NOT_POSSIBLE_HERE;
					}

					if (this.worldObj.isDaytime()) {
						return EnumStatus.NOT_POSSIBLE_NOW;
					}

					if (Math.abs(this.posX - (double) i) <= 3.0D && Math.abs(this.posY - (double) j) <= 2.0D
							&& Math.abs(this.posZ - (double) k) <= 3.0D) {
						break label53;
					}

					return EnumStatus.TOO_FAR_AWAY;
				}

				return EnumStatus.OTHER_PROBLEM;
			}
		}

		this.setSize(0.2F, 0.2F);
		this.yOffset = 0.2F;
		if (this.worldObj.blockExists(i, j, k)) {
			int l = this.worldObj.getBlockMetadata(i, j, k);
			int i1 = BlockBed.func_22019_c(l);
			float f = 0.5F;
			float f1 = 0.5F;
			switch (i1) {
				case 0:
					f1 = 0.9F;
					break;
				case 1:
					f = 0.1F;
					break;
				case 2:
					f1 = 0.1F;
					break;
				case 3:
					f = 0.9F;
			}

			this.func_22059_e(i1);
			this.setPosition((double) ((float) i + f), (double) ((float) j + 0.9375F), (double) ((float) k + f1));
		} else {
			this.setPosition((double) ((float) i + 0.5F), (double) ((float) j + 0.9375F), (double) ((float) k + 0.5F));
		}

		this.sleeping = true;
		this.sleepTimer = 0;
		this.playerLocation = new ChunkCoordinates(i, j, k);
		this.motionX = this.motionZ = this.motionY = 0.0D;
		if (!this.worldObj.multiplayerWorld) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}

		return EnumStatus.OK;
	}

	private void func_22059_e(int i) {
		this.field_22066_z = 0.0F;
		this.field_22067_A = 0.0F;
		switch (i) {
			case 0:
				this.field_22067_A = -1.8F;
				break;
			case 1:
				this.field_22066_z = 1.8F;
				break;
			case 2:
				this.field_22067_A = 1.8F;
				break;
			case 3:
				this.field_22066_z = -1.8F;
		}

	}

	public void wakeUpPlayer(boolean flag, boolean flag1, boolean flag2) {
		this.setSize(0.6F, 1.8F);
		this.resetHeight();
		ChunkCoordinates chunkcoordinates = this.playerLocation;
		ChunkCoordinates chunkcoordinates1 = this.playerLocation;
		if (chunkcoordinates != null && this.worldObj.getBlockId(chunkcoordinates.posX, chunkcoordinates.posY,
				chunkcoordinates.posZ) == Block.bed.blockID) {
			BlockBed.func_22022_a(this.worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ,
					false);
			ChunkCoordinates chunkcoordinates2 = BlockBed.func_22021_g(this.worldObj, chunkcoordinates.posX,
					chunkcoordinates.posY, chunkcoordinates.posZ, 0);
			if (chunkcoordinates2 == null) {
				chunkcoordinates2 = new ChunkCoordinates(chunkcoordinates.posX, chunkcoordinates.posY + 1,
						chunkcoordinates.posZ);
			}

			this.setPosition((double) ((float) chunkcoordinates2.posX + 0.5F),
					(double) ((float) chunkcoordinates2.posY + this.yOffset + 0.1F),
					(double) ((float) chunkcoordinates2.posZ + 0.5F));
		}

		this.sleeping = false;
		if (!this.worldObj.multiplayerWorld && flag1) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}

		if (flag) {
			this.sleepTimer = 0;
		} else {
			this.sleepTimer = 100;
		}

		if (flag2) {
			this.setSpawnChunk(this.playerLocation);
		}

	}

	private boolean isInBed() {
		return this.worldObj.getBlockId(this.playerLocation.posX, this.playerLocation.posY,
				this.playerLocation.posZ) == Block.bed.blockID;
	}

	public static ChunkCoordinates func_25051_a(World world, ChunkCoordinates chunkcoordinates) {
		IChunkProvider ichunkprovider = world.o();

		for (int chunkcoordinates1 = -3; chunkcoordinates1 <= 3; chunkcoordinates1 += 6) {
			for (int z = -3; z <= 3; z += 6) {
				for (int y = -3; y <= 3; y += 6) {
					ichunkprovider.loadCube(chunkcoordinates.posX + chunkcoordinates1 >> 4,
							chunkcoordinates.posY + y >> 4, chunkcoordinates.posZ + z >> 4);
				}
			}
		}

		if (world.getBlockId(chunkcoordinates.posX, chunkcoordinates.posY,
				chunkcoordinates.posZ) != Block.bed.blockID) {
			return null;
		} else {
			ChunkCoordinates chunkcoordinates11 = BlockBed.func_22021_g(world, chunkcoordinates.posX,
					chunkcoordinates.posY, chunkcoordinates.posZ, 0);
			return chunkcoordinates11;
		}
	}

	public boolean func_22057_E() {
		return this.sleeping;
	}

	public boolean isPlayerFullyAsleep() {
		return this.sleeping && this.sleepTimer >= 100;
	}

	public void func_22061_a(String s) {
	}

	public ChunkCoordinates getSpawnChunk() {
		return this.spawnChunk;
	}

	public void setSpawnChunk(ChunkCoordinates chunkcoordinates) {
		if (chunkcoordinates != null) {
			this.spawnChunk = new ChunkCoordinates(chunkcoordinates);
		} else {
			this.spawnChunk = null;
		}

	}

	protected void jump() {
		super.jump();
	}

	public void moveEntityWithHeading(float f, float f1) {
		double d = this.posX;
		double d1 = this.posY;
		double d2 = this.posZ;
		super.moveEntityWithHeading(f, f1);
		this.func_25045_g(this.posX - d, this.posY - d1, this.posZ - d2);
	}

	private void func_25045_g(double d, double d1, double d2) {
		if (this.ridingEntity == null) {
			int l;
			if (this.isInsideOfMaterial(Material.water)) {
				l = Math.round(MathHelper.sqrt_double(d * d + d1 * d1 + d2 * d2) * 100.0F);
			} else if (this.isInWater()) {
				l = Math.round(MathHelper.sqrt_double(d * d + d2 * d2) * 100.0F);
				
			} else if (this.onGround) {
				l = Math.round(MathHelper.sqrt_double(d * d + d2 * d2) * 100.0F);
			} else {
				l = Math.round(MathHelper.sqrt_double(d * d + d2 * d2) * 100.0F);
			}

		}
	}

	private void func_27015_h(double d, double d1, double d2) {
		if (this.ridingEntity != null) {
			int i = Math.round(MathHelper.sqrt_double(d * d + d1 * d1 + d2 * d2) * 100.0F);
			if (i > 0) {
				if (this.ridingEntity instanceof EntityMinecart) {
					if (this.field_27995_d == null) {
						this.field_27995_d = new ChunkCoordinates(MathHelper.floor_double(this.posX),
								MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
					} else if (this.field_27995_d.getSqDistanceTo(MathHelper.floor_double(this.posX),
							MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) >= 1000.0D) {
					}
				}
			}
		}

	}

	protected void fall(float f) {
		super.fall(f);
	}

	protected void fly() {
		this.motionY = 1.0D;
	}

	public void func_27010_a(EntityLiving entityliving) {
	}

	public void setInPortal() {
		if (this.timeUntilPortal > 0) {
			this.timeUntilPortal = 10;
		} else {
			this.inPortal = true;
		}
	}
}
