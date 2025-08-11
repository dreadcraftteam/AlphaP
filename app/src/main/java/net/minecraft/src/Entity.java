package net.minecraft.src;

import java.util.List;
import java.util.Random;

public abstract class Entity {
	private static int nextEntityID = 0;
	public int entityId = nextEntityID++;
	public double renderDistanceWeight = 1.0D;
	public boolean preventEntitySpawning = false;
	public Entity riddenByEntity;
	public Entity ridingEntity;
	public World worldObj;
	public double prevPosX;
	public double prevPosY;
	public double prevPosZ;
	public double posX;
	public double posY;
	public double posZ;
	public double motionX;
	public double motionY;
	public double motionZ;
	public float rotationYaw;
	public float rotationPitch;
	public float prevRotationYaw;
	public float prevRotationPitch;
	public final AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
	public boolean onGround = false;
	public boolean isCollidedHorizontally;
	public boolean isCollidedVertically;
	public boolean isCollided = false;
	public boolean beenAttacked = false;
	public boolean isInWeb;
	public boolean field_9293_aM = true;
	public boolean isDead = false;
	public float yOffset = 0.0F;
	public float width = 0.6F;
	public float height = 1.8F;
	public float prevDistanceWalkedModified = 0.0F;
	public float distanceWalkedModified = 0.0F;
	protected float fallDistance = 0.0F;
	private int nextStepDistance = 1;
	public double lastTickPosX;
	public double lastTickPosY;
	public double lastTickPosZ;
	public float ySize = 0.0F;
	public float stepHeight = 0.0F;
	public boolean noClip = false;
	public float entityCollisionReduction = 0.0F;
	protected Random rand = new Random();
	public int ticksExisted = 0;
	public int fireResistance = 1;
	public int fire = 0;
	protected int maxAir = 300;
	protected boolean inWater = false;
	public int heartsLife = 0;
	public int air = 300;
	private boolean isFirstUpdate = true;
	public String skinUrl;
	public String cloakUrl;
	protected boolean isImmuneToFire = false;
	protected DataWatcher dataWatcher = new DataWatcher();
	public float entityBrightness = 0.0F;
	private double entityRiderPitchDelta;
	private double entityRiderYawDelta;
	public boolean addedToChunk = false;
	public int chunkCoordX;
	public int chunkCoordY;
	public int chunkCoordZ;
	public int serverPosX;
	public int serverPosY;
	public int serverPosZ;
	public boolean ignoreFrustumCheck;
	public static final int SpaceLevel = 100000;

	public Entity(World world) {
		this.worldObj = world;
		this.setPosition(0.0D, 0.0D, 0.0D);
		this.dataWatcher.addObject(0, (byte)0);
		this.entityInit();
	}

	protected abstract void entityInit();

	public DataWatcher getDataWatcher() {
		return this.dataWatcher;
	}

	public boolean equals(Object obj) {
		return obj instanceof Entity ? ((Entity)obj).entityId == this.entityId : false;
	}

	public int hashCode() {
		return this.entityId;
	}

	protected void preparePlayerToSpawn() {
		if(this.worldObj != null) {
			while(this.posY > 0.0D) {
				this.setPosition(this.posX, this.posY, this.posZ);
				if(this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0) {
					break;
				}

				++this.posY;
			}

			this.motionX = this.motionY = this.motionZ = 0.0D;
			this.rotationPitch = 0.0F;
		}
	}

	public void setEntityDead() {
		this.isDead = true;
	}

	protected void setSize(float f, float f1) {
		this.width = f;
		this.height = f1;
	}

	protected void setRotation(float f, float f1) {
		this.rotationYaw = f % 360.0F;
		this.rotationPitch = f1 % 360.0F;
	}

	public void setPosition(double d, double d1, double d2) {
		this.posX = d;
		this.posY = d1;
		this.posZ = d2;
		float f = this.width / 2.0F;
		float f1 = this.height;
		this.boundingBox.setBounds(d - (double)f, d1 - (double)this.yOffset + (double)this.ySize, d2 - (double)f, d + (double)f, d1 - (double)this.yOffset + (double)this.ySize + (double)f1, d2 + (double)f);
	}

	public void func_346_d(float f, float f1) {
		float f2 = this.rotationPitch;
		float f3 = this.rotationYaw;
		this.rotationYaw = (float)((double)this.rotationYaw + (double)f * 0.15D);
		this.rotationPitch = (float)((double)this.rotationPitch - (double)f1 * 0.15D);
		if(this.rotationPitch < -90.0F) {
			this.rotationPitch = -90.0F;
		}

		if(this.rotationPitch > 90.0F) {
			this.rotationPitch = 90.0F;
		}

		this.prevRotationPitch += this.rotationPitch - f2;
		this.prevRotationYaw += this.rotationYaw - f3;
	}

	public void onUpdate() {
		this.onEntityUpdate();
	}

	public void onEntityUpdate() {
		if(this.ridingEntity != null && this.ridingEntity.isDead) {
			this.ridingEntity = null;
		}

		++this.ticksExisted;
		this.prevDistanceWalkedModified = this.distanceWalkedModified;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;
		if(this.handleWaterMovement()) {
			if(!this.inWater && !this.isFirstUpdate) {
				float f = MathHelper.sqrt_double(this.motionX * this.motionX * (double)0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * (double)0.2F) * 0.2F;
				if(f > 1.0F) {
					f = 1.0F;
				}

				this.worldObj.playSoundAtEntity(this, "random.splash", f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
				float f1 = (float)MathHelper.floor_double(this.boundingBox.minY);

				int j;
				float f3;
				float f5;
				for(j = 0; (float)j < 1.0F + this.width * 20.0F; ++j) {
					f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
					f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
					this.worldObj.spawnParticle("bubble", this.posX + (double)f3, (double)(f1 + 1.0F), this.posZ + (double)f5, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ);
				}

				for(j = 0; (float)j < 1.0F + this.width * 20.0F; ++j) {
					f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
					f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
					this.worldObj.spawnParticle("splash", this.posX + (double)f3, (double)(f1 + 1.0F), this.posZ + (double)f5, this.motionX, this.motionY, this.motionZ);
				}
			}

			this.fallDistance = 0.0F;
			this.inWater = true;
			this.fire = 0;
		} else {
			this.inWater = false;
		}

		if(this.worldObj.multiplayerWorld) {
			this.fire = 0;
		} else if(this.fire > 0) {
			if(this.isImmuneToFire) {
				this.fire -= 4;
				if(this.fire < 0) {
					this.fire = 0;
				}
			} else {
				if(this.fire % 20 == 0) {
					this.attackEntityFrom((Entity)null, 1);
				}

				--this.fire;
			}
		}

		if(this.handleLavaMovement()) {
			this.setOnFireFromLava();
		}

		if(!this.worldObj.multiplayerWorld) {
			this.setEntityFlag(0, this.fire > 0);
			this.setEntityFlag(2, this.ridingEntity != null);
		}

		this.isFirstUpdate = false;
	}

	protected void setOnFireFromLava() {
		if(!this.isImmuneToFire) {
			this.attackEntityFrom((Entity)null, 4);
			this.fire = 600;
		}

	}

	protected void kill() {
		this.setEntityDead();
	}

	public boolean isOffsetPositionInLiquid(double d, double d1, double d2) {
		AxisAlignedBB axisalignedbb = this.boundingBox.getOffsetBoundingBox(d, d1, d2);
		List list = this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb);
		return list.size() > 0 ? false : !this.worldObj.getIsAnyLiquid(axisalignedbb);
	}

	public void moveEntity(double d, double d1, double d2) {
		if(this.noClip) {
			this.boundingBox.offset(d, d1, d2);
			this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
			this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
			this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
		} else {
			this.ySize *= 0.4F;
			double d3 = this.posX;
			double d4 = this.posZ;
			if(this.isInWeb) {
				this.isInWeb = false;
				d *= 0.25D;
				d1 *= (double)0.05F;
				d2 *= 0.25D;
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}

			double d5 = d;
			double d6 = d1;
			double d7 = d2;
			AxisAlignedBB axisalignedbb = this.boundingBox.copy();
			boolean flag = this.onGround && this.isSneaking();
			if(flag) {
				double list;
				for(list = 0.05D; d != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(d, -1.0D, 0.0D)).size() == 0; d5 = d) {
					if(d < list && d >= -list) {
						d = 0.0D;
					} else if(d > 0.0D) {
						d -= list;
					} else {
						d += list;
					}
				}

				for(; d2 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(0.0D, -1.0D, d2)).size() == 0; d7 = d2) {
					if(d2 < list && d2 >= -list) {
						d2 = 0.0D;
					} else if(d2 > 0.0D) {
						d2 -= list;
					} else {
						d2 += list;
					}
				}
			}

			List list35 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(d, d1, d2));

			for(int flag1 = 0; flag1 < list35.size(); ++flag1) {
				d1 = ((AxisAlignedBB)list35.get(flag1)).calculateYOffset(this.boundingBox, d1);
			}

			this.boundingBox.offset(0.0D, d1, 0.0D);
			if(!this.field_9293_aM && d6 != d1) {
				d2 = 0.0D;
				d1 = 0.0D;
				d = 0.0D;
			}

			boolean z36 = this.onGround || d6 != d1 && d6 < 0.0D;

			int d10;
			for(d10 = 0; d10 < list35.size(); ++d10) {
				d = ((AxisAlignedBB)list35.get(d10)).calculateXOffset(this.boundingBox, d);
			}

			this.boundingBox.offset(d, 0.0D, 0.0D);
			if(!this.field_9293_aM && d5 != d) {
				d2 = 0.0D;
				d1 = 0.0D;
				d = 0.0D;
			}

			for(d10 = 0; d10 < list35.size(); ++d10) {
				d2 = ((AxisAlignedBB)list35.get(d10)).calculateZOffset(this.boundingBox, d2);
			}

			this.boundingBox.offset(0.0D, 0.0D, d2);
			if(!this.field_9293_aM && d7 != d2) {
				d2 = 0.0D;
				d1 = 0.0D;
				d = 0.0D;
			}

			double d12;
			int l3;
			double d37;
			if(this.stepHeight > 0.0F && z36 && (flag || this.ySize < 0.05F) && (d5 != d || d7 != d2)) {
				d37 = d;
				d12 = d1;
				double i1 = d2;
				d = d5;
				d1 = (double)this.stepHeight;
				d2 = d7;
				AxisAlignedBB i2 = this.boundingBox.copy();
				this.boundingBox.setBB(axisalignedbb);
				List k3 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(d5, d1, d7));

				for(l3 = 0; l3 < k3.size(); ++l3) {
					d1 = ((AxisAlignedBB)k3.get(l3)).calculateYOffset(this.boundingBox, d1);
				}

				this.boundingBox.offset(0.0D, d1, 0.0D);
				if(!this.field_9293_aM && d6 != d1) {
					d2 = 0.0D;
					d1 = 0.0D;
					d = 0.0D;
				}

				for(l3 = 0; l3 < k3.size(); ++l3) {
					d = ((AxisAlignedBB)k3.get(l3)).calculateXOffset(this.boundingBox, d);
				}

				this.boundingBox.offset(d, 0.0D, 0.0D);
				if(!this.field_9293_aM && d5 != d) {
					d2 = 0.0D;
					d1 = 0.0D;
					d = 0.0D;
				}

				for(l3 = 0; l3 < k3.size(); ++l3) {
					d2 = ((AxisAlignedBB)k3.get(l3)).calculateZOffset(this.boundingBox, d2);
				}

				this.boundingBox.offset(0.0D, 0.0D, d2);
				if(!this.field_9293_aM && d7 != d2) {
					d2 = 0.0D;
					d1 = 0.0D;
					d = 0.0D;
				}

				if(!this.field_9293_aM && d6 != d1) {
					d2 = 0.0D;
					d1 = 0.0D;
					d = 0.0D;
				} else {
					d1 = (double)(-this.stepHeight);

					for(l3 = 0; l3 < k3.size(); ++l3) {
						d1 = ((AxisAlignedBB)k3.get(l3)).calculateYOffset(this.boundingBox, d1);
					}

					this.boundingBox.offset(0.0D, d1, 0.0D);
				}

				if(d37 * d37 + i1 * i1 >= d * d + d2 * d2) {
					d = d37;
					d1 = d12;
					d2 = i1;
					this.boundingBox.setBB(i2);
				} else {
					double d42 = this.boundingBox.minY - (double)((int)this.boundingBox.minY);
					if(d42 > 0.0D) {
						this.ySize = (float)((double)this.ySize + d42 + 0.01D);
					}
				}
			}

			this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
			this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
			this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
			this.isCollidedHorizontally = d5 != d || d7 != d2;
			this.isCollidedVertically = d6 != d1;
			this.onGround = d6 != d1 && d6 < 0.0D;
			this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
			this.updateFallState(d1, this.onGround);
			if(d5 != d) {
				this.motionX = 0.0D;
			}

			if(d6 != d1) {
				this.motionY = 0.0D;
			}

			if(d7 != d2) {
				this.motionZ = 0.0D;
			}

			d37 = this.posX - d3;
			d12 = this.posZ - d4;
			int k1;
			int i38;
			int i39;
			int i40;
			if(this.canTriggerWalking() && !flag && this.ridingEntity == null) {
				this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt_double(d37 * d37 + d12 * d12) * 0.6D);
				i38 = MathHelper.floor_double(this.posX);
				k1 = MathHelper.floor_double(this.posY - (double)0.2F - (double)this.yOffset);
				i39 = MathHelper.floor_double(this.posZ);
				i40 = this.worldObj.getBlockId(i38, k1, i39);
				if(this.worldObj.getBlockId(i38, k1 - 1, i39) == Block.fence.blockID) {
					i40 = this.worldObj.getBlockId(i38, k1 - 1, i39);
				}

				if(this.distanceWalkedModified > (float)this.nextStepDistance && i40 > 0) {
					++this.nextStepDistance;
					StepSound stepSound43 = Block.blocksList[i40].stepSound;
					if(this.worldObj.getBlockId(i38, k1 + 1, i39) == Block.snow.blockID) {
						stepSound43 = Block.snow.stepSound;
						this.worldObj.playSoundAtEntity(this, stepSound43.func_1145_d(), stepSound43.getVolume() * 0.15F, stepSound43.getPitch());
					} else if(!Block.blocksList[i40].blockMaterial.getIsLiquid()) {
						this.worldObj.playSoundAtEntity(this, stepSound43.func_1145_d(), stepSound43.getVolume() * 0.15F, stepSound43.getPitch());
					}

					Block.blocksList[i40].onEntityWalking(this.worldObj, i38, k1, i39, this);
				}
			}

			i38 = MathHelper.floor_double(this.boundingBox.minX + 0.001D);
			k1 = MathHelper.floor_double(this.boundingBox.minY + 0.001D);
			i39 = MathHelper.floor_double(this.boundingBox.minZ + 0.001D);
			i40 = MathHelper.floor_double(this.boundingBox.maxX - 0.001D);
			l3 = MathHelper.floor_double(this.boundingBox.maxY - 0.001D);
			int i4 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001D);
			if(this.worldObj.checkChunksExist(i38, k1, i39, i40, l3, i4)) {
				for(int flag2 = i38; flag2 <= i40; ++flag2) {
					for(int k4 = k1; k4 <= l3; ++k4) {
						for(int l4 = i39; l4 <= i4; ++l4) {
							int i5 = this.worldObj.getBlockId(flag2, k4, l4);
							if(i5 > 0) {
								Block.blocksList[i5].onEntityCollidedWithBlock(this.worldObj, flag2, k4, l4, this);
							}
						}
					}
				}
			}

			boolean z41 = this.isWet();
			if(this.worldObj.isBoundingBoxBurning(this.boundingBox.func_28195_e(0.001D, 0.001D, 0.001D))) {
				this.dealFireDamage(1);
				if(!z41) {
					++this.fire;
					if(this.fire == 0) {
						this.fire = 300;
					}
				}
			} else if(this.fire <= 0) {
				this.fire = -this.fireResistance;
			}

			if(z41 && this.fire > 0) {
				this.worldObj.playSoundAtEntity(this, "random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
				this.fire = -this.fireResistance;
			}

		}
	}

	protected boolean canTriggerWalking() {
		return true;
	}

	protected void updateFallState(double d, boolean flag) {
		if(flag) {
			if(this.fallDistance > 0.0F) {
				this.fall(this.fallDistance);
				this.fallDistance = 0.0F;
			}
		} else if(d < 0.0D) {
			if(this.posY < 100000.0D) {
				this.fallDistance = (float)((double)this.fallDistance - d);
			} else {
				this.fallDistance = (float)((double)this.fallDistance - d * 0.03D);
			}
		}

	}

	public AxisAlignedBB getBoundingBox() {
		return null;
	}

	protected void dealFireDamage(int i) {
		if(!this.isImmuneToFire) {
			this.attackEntityFrom((Entity)null, i);
		}

	}

	protected void fall(float f) {
		if(this.riddenByEntity != null) {
			this.riddenByEntity.fall(f);
		}

	}

	public boolean isWet() {
		return this.inWater || this.worldObj.canBlockBeRainedOn(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
	}

	public boolean isInWater() {
		return this.inWater;
	}

	public boolean handleWaterMovement() {
		return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.4000000059604645D, 0.0D).func_28195_e(0.001D, 0.001D, 0.001D), Material.water, this);
	}

	public boolean isInsideOfMaterial(Material material) {
		double d = this.posY + (double)this.getEyeHeight();
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_float((float)MathHelper.floor_double(d));
		int k = MathHelper.floor_double(this.posZ);
		int l = this.worldObj.getBlockId(i, j, k);
		if(l != 0 && Block.blocksList[l].blockMaterial == material) {
			float f = BlockFluid.getPercentAir(this.worldObj.getBlockMetadata(i, j, k)) - 0.1111111F;
			float f1 = (float)(j + 1) - f;
			return d < (double)f1;
		} else {
			return false;
		}
	}

	public float getEyeHeight() {
		return 0.0F;
	}

	public boolean handleLavaMovement() {
		return this.worldObj.isMaterialInBB(this.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.lava);
	}

	public void moveFlying(float f, float f1, float f2) {
		float f3 = MathHelper.sqrt_float(f * f + f1 * f1);
		if(f3 >= 0.01F) {
			if(f3 < 1.0F) {
				f3 = 1.0F;
			}

			f3 = f2 / f3;
			f *= f3;
			f1 *= f3;
			float f4 = MathHelper.sin(this.rotationYaw * 3.141593F / 180.0F);
			float f5 = MathHelper.cos(this.rotationYaw * 3.141593F / 180.0F);
			this.motionX += (double)(f * f5 - f1 * f4);
			this.motionZ += (double)(f1 * f5 + f * f4);
		}
	}

	public float getEntityBrightness(float f) {
		int i = MathHelper.floor_double(this.posX);
		double d = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
		int j = MathHelper.floor_double(this.posY - (double)this.yOffset + d);
		int k = MathHelper.floor_double(this.posZ);
		if(this.worldObj.checkChunksExist(MathHelper.floor_double(this.boundingBox.minX), MathHelper.floor_double(this.boundingBox.minY), MathHelper.floor_double(this.boundingBox.minZ), MathHelper.floor_double(this.boundingBox.maxX), MathHelper.floor_double(this.boundingBox.maxY), MathHelper.floor_double(this.boundingBox.maxZ))) {
			float f1 = this.worldObj.getLightBrightness(i, j, k);
			if(f1 < this.entityBrightness) {
				f1 = this.entityBrightness;
			}

			return f1;
		} else {
			return this.entityBrightness;
		}
	}

	public void setWorld(World world) {
		this.worldObj = world;
	}

	public void setPositionAndRotation(double d, double d1, double d2, float f, float f1) {
		this.prevPosX = this.posX = d;
		this.prevPosY = this.posY = d1;
		this.prevPosZ = this.posZ = d2;
		this.prevRotationYaw = this.rotationYaw = f;
		this.prevRotationPitch = this.rotationPitch = f1;
		this.ySize = 0.0F;
		double d3 = (double)(this.prevRotationYaw - f);
		if(d3 < -180.0D) {
			this.prevRotationYaw += 360.0F;
		}

		if(d3 >= 180.0D) {
			this.prevRotationYaw -= 360.0F;
		}

		this.setPosition(this.posX, this.posY, this.posZ);
		this.setRotation(f, f1);
	}

	public void setLocationAndAngles(double d, double d1, double d2, float f, float f1) {
		this.lastTickPosX = this.prevPosX = this.posX = d;
		this.lastTickPosY = this.prevPosY = this.posY = d1 + (double)this.yOffset;
		this.lastTickPosZ = this.prevPosZ = this.posZ = d2;
		this.rotationYaw = f;
		this.rotationPitch = f1;
		this.setPosition(this.posX, this.posY, this.posZ);
	}

	public float getDistanceToEntity(Entity entity) {
		float f = (float)(this.posX - entity.posX);
		float f1 = (float)(this.posY - entity.posY);
		float f2 = (float)(this.posZ - entity.posZ);
		return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
	}

	public double getDistanceSq(double d, double d1, double d2) {
		double d3 = this.posX - d;
		double d4 = this.posY - d1;
		double d5 = this.posZ - d2;
		return d3 * d3 + d4 * d4 + d5 * d5;
	}

	public double getDistance(double d, double d1, double d2) {
		double d3 = this.posX - d;
		double d4 = this.posY - d1;
		double d5 = this.posZ - d2;
		return (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
	}

	public double getDistanceSqToEntity(Entity entity) {
		double d = this.posX - entity.posX;
		double d1 = this.posY - entity.posY;
		double d2 = this.posZ - entity.posZ;
		return d * d + d1 * d1 + d2 * d2;
	}

	public void onCollideWithPlayer(EntityPlayer entityplayer) {
	}

	public void applyEntityCollision(Entity entity) {
		if(entity.riddenByEntity != this && entity.ridingEntity != this) {
			double d = entity.posX - this.posX;
			double d1 = entity.posZ - this.posZ;
			double d2 = MathHelper.abs_max(d, d1);
			if(d2 >= (double)0.01F) {
				d2 = (double)MathHelper.sqrt_double(d2);
				d /= d2;
				d1 /= d2;
				double d3 = 1.0D / d2;
				if(d3 > 1.0D) {
					d3 = 1.0D;
				}

				d *= d3;
				d1 *= d3;
				d *= (double)0.05F;
				d1 *= (double)0.05F;
				d *= (double)(1.0F - this.entityCollisionReduction);
				d1 *= (double)(1.0F - this.entityCollisionReduction);
				this.addVelocity(-d, 0.0D, -d1);
				entity.addVelocity(d, 0.0D, d1);
			}

		}
	}

	public void addVelocity(double d, double d1, double d2) {
		this.motionX += d;
		this.motionY += d1;
		this.motionZ += d2;
	}

	protected void setBeenAttacked() {
		this.beenAttacked = true;
	}

	public boolean attackEntityFrom(Entity entity, int i) {
		this.setBeenAttacked();
		return false;
	}

	public boolean canBeCollidedWith() {
		return false;
	}

	public boolean canBePushed() {
		return false;
	}

	public void addToPlayerScore(Entity entity, int i) {
	}

	public boolean isInRangeToRenderVec3D(Vec3D vec3d) {
		double d = this.posX - vec3d.xCoord;
		double d1 = this.posY - vec3d.yCoord;
		double d2 = this.posZ - vec3d.zCoord;
		double d3 = d * d + d1 * d1 + d2 * d2;
		return this.isInRangeToRenderDist(d3);
	}

	public boolean isInRangeToRenderDist(double d) {
		double d1 = this.boundingBox.getAverageEdgeLength();
		d1 *= 64.0D * this.renderDistanceWeight;
		return d < d1 * d1;
	}

	public String getEntityTexture() {
		return null;
	}

	public boolean addEntityID(NBTTagCompound nbttagcompound) {
		String s = this.getEntityString();
		if(!this.isDead && s != null) {
			nbttagcompound.setString("id", s);
			this.writeToNBT(nbttagcompound);
			return true;
		} else {
			return false;
		}
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setTag("Pos", this.newDoubleNBTList(new double[]{this.posX, this.posY + (double)this.ySize, this.posZ}));
		nbttagcompound.setTag("Motion", this.newDoubleNBTList(new double[]{this.motionX, this.motionY, this.motionZ}));
		nbttagcompound.setTag("Rotation", this.newFloatNBTList(new float[]{this.rotationYaw, this.rotationPitch}));
		nbttagcompound.setFloat("FallDistance", this.fallDistance);
		nbttagcompound.setShort("Fire", (short)this.fire);
		nbttagcompound.setShort("Air", (short)this.air);
		nbttagcompound.setBoolean("OnGround", this.onGround);
		this.writeEntityToNBT(nbttagcompound);
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		NBTTagList nbttaglist = nbttagcompound.getTagList("Pos");
		NBTTagList nbttaglist1 = nbttagcompound.getTagList("Motion");
		NBTTagList nbttaglist2 = nbttagcompound.getTagList("Rotation");
		this.motionX = ((NBTTagDouble)nbttaglist1.tagAt(0)).doubleValue;
		this.motionY = ((NBTTagDouble)nbttaglist1.tagAt(1)).doubleValue;
		this.motionZ = ((NBTTagDouble)nbttaglist1.tagAt(2)).doubleValue;
		if(Math.abs(this.motionX) > 10.0D) {
			this.motionX = 0.0D;
		}

		if(Math.abs(this.motionY) > 10.0D) {
			this.motionY = 0.0D;
		}

		if(Math.abs(this.motionZ) > 10.0D) {
			this.motionZ = 0.0D;
		}

		this.prevPosX = this.lastTickPosX = this.posX = ((NBTTagDouble)nbttaglist.tagAt(0)).doubleValue;
		this.prevPosY = this.lastTickPosY = this.posY = ((NBTTagDouble)nbttaglist.tagAt(1)).doubleValue;
		this.prevPosZ = this.lastTickPosZ = this.posZ = ((NBTTagDouble)nbttaglist.tagAt(2)).doubleValue;
		this.prevRotationYaw = this.rotationYaw = ((NBTTagFloat)nbttaglist2.tagAt(0)).floatValue;
		this.prevRotationPitch = this.rotationPitch = ((NBTTagFloat)nbttaglist2.tagAt(1)).floatValue;
		this.fallDistance = nbttagcompound.getFloat("FallDistance");
		this.fire = nbttagcompound.getShort("Fire");
		this.air = nbttagcompound.getShort("Air");
		this.onGround = nbttagcompound.getBoolean("OnGround");
		this.setPosition(this.posX, this.posY, this.posZ);
		this.setRotation(this.rotationYaw, this.rotationPitch);
		this.readEntityFromNBT(nbttagcompound);
	}

	protected final String getEntityString() {
		return EntityList.getEntityString(this);
	}

	protected abstract void readEntityFromNBT(NBTTagCompound nBTTagCompound1);

	protected abstract void writeEntityToNBT(NBTTagCompound nBTTagCompound1);

	protected NBTTagList newDoubleNBTList(double[] ad) {
		NBTTagList nbttaglist = new NBTTagList();
		double[] ad1 = ad;
		int i = ad.length;

		for(int j = 0; j < i; ++j) {
			double d = ad1[j];
			nbttaglist.setTag(new NBTTagDouble(d));
		}

		return nbttaglist;
	}

	protected NBTTagList newFloatNBTList(float[] af) {
		NBTTagList nbttaglist = new NBTTagList();
		float[] af1 = af;
		int i = af.length;

		for(int j = 0; j < i; ++j) {
			float f = af1[j];
			nbttaglist.setTag(new NBTTagFloat(f));
		}

		return nbttaglist;
	}

	public float getShadowSize() {
		return this.height / 2.0F;
	}

	public EntityItem dropItem(int i, int j) {
		return this.dropItemWithOffset(i, j, 0.0F);
	}

	public EntityItem dropItemWithOffset(int i, int j, float f) {
		return this.entityDropItem(new ItemStack(i, j, 0), f);
	}

	public EntityItem entityDropItem(ItemStack itemstack, float f) {
		EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY + (double)f, this.posZ, itemstack);
		entityitem.delayBeforeCanPickup = 10;
		this.worldObj.entityJoinedWorld(entityitem);
		return entityitem;
	}

	public boolean isEntityAlive() {
		return !this.isDead;
	}

	public boolean isEntityInsideOpaqueBlock() {
		for(int i = 0; i < 8; ++i) {
			float f = ((float)((i >> 0) % 2) - 0.5F) * this.width * 0.9F;
			float f1 = ((float)((i >> 1) % 2) - 0.5F) * 0.1F;
			float f2 = ((float)((i >> 2) % 2) - 0.5F) * this.width * 0.9F;
			int j = MathHelper.floor_double(this.posX + (double)f);
			int k = MathHelper.floor_double(this.posY + (double)this.getEyeHeight() + (double)f1);
			int l = MathHelper.floor_double(this.posZ + (double)f2);
			if(this.worldObj.isBlockNormalCube(j, k, l)) {
				return true;
			}
		}

		return false;
	}

	public boolean interact(EntityPlayer entityplayer) {
		return false;
	}

	public AxisAlignedBB getCollisionBox(Entity entity) {
		return null;
	}

	public void updateRidden() {
		if(this.ridingEntity.isDead) {
			this.ridingEntity = null;
		} else {
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
			this.onUpdate();
			if(this.ridingEntity != null) {
				this.ridingEntity.updateRiderPosition();
				this.entityRiderYawDelta += (double)(this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);

				for(this.entityRiderPitchDelta += (double)(this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch); this.entityRiderYawDelta >= 180.0D; this.entityRiderYawDelta -= 360.0D) {
				}

				while(this.entityRiderYawDelta < -180.0D) {
					this.entityRiderYawDelta += 360.0D;
				}

				while(this.entityRiderPitchDelta >= 180.0D) {
					this.entityRiderPitchDelta -= 360.0D;
				}

				while(this.entityRiderPitchDelta < -180.0D) {
					this.entityRiderPitchDelta += 360.0D;
				}

				double d = this.entityRiderYawDelta * 0.5D;
				double d1 = this.entityRiderPitchDelta * 0.5D;
				float f = 10.0F;
				if(d > (double)f) {
					d = (double)f;
				}

				if(d < (double)(-f)) {
					d = (double)(-f);
				}

				if(d1 > (double)f) {
					d1 = (double)f;
				}

				if(d1 < (double)(-f)) {
					d1 = (double)(-f);
				}

				this.entityRiderYawDelta -= d;
				this.entityRiderPitchDelta -= d1;
				this.rotationYaw = (float)((double)this.rotationYaw + d);
				this.rotationPitch = (float)((double)this.rotationPitch + d1);
			}
		}
	}

	public void updateRiderPosition() {
		this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
	}

	public double getYOffset() {
		return (double)this.yOffset;
	}

	public double getMountedYOffset() {
		return (double)this.height * 0.75D;
	}

	public void mountEntity(Entity entity) {
		this.entityRiderPitchDelta = 0.0D;
		this.entityRiderYawDelta = 0.0D;
		if(entity == null) {
			if(this.ridingEntity != null) {
				this.setLocationAndAngles(this.ridingEntity.posX, this.ridingEntity.boundingBox.minY + (double)this.ridingEntity.height, this.ridingEntity.posZ, this.rotationYaw, this.rotationPitch);
				this.ridingEntity.riddenByEntity = null;
			}

			this.ridingEntity = null;
		} else if(this.ridingEntity == entity) {
			this.ridingEntity.riddenByEntity = null;
			this.ridingEntity = null;
			this.setLocationAndAngles(entity.posX, entity.boundingBox.minY + (double)entity.height, entity.posZ, this.rotationYaw, this.rotationPitch);
		} else {
			if(this.ridingEntity != null) {
				this.ridingEntity.riddenByEntity = null;
			}

			if(entity.riddenByEntity != null) {
				entity.riddenByEntity.ridingEntity = null;
			}

			this.ridingEntity = entity;
			entity.riddenByEntity = this;
		}
	}

	public void setPositionAndRotation2(double d, double d1, double d2, float f, float f1, int i) {
		this.setPosition(d, d1, d2);
		this.setRotation(f, f1);
		List list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.func_28195_e(8.0D / 256D, 0.0D, 8.0D / 256D));
		if(list.size() > 0) {
			double d3 = 0.0D;

			for(int j = 0; j < list.size(); ++j) {
				AxisAlignedBB axisalignedbb = (AxisAlignedBB)list.get(j);
				if(axisalignedbb.maxY > d3) {
					d3 = axisalignedbb.maxY;
				}
			}

			d1 += d3 - this.boundingBox.minY;
			this.setPosition(d, d1, d2);
		}

	}

	public float getCollisionBorderSize() {
		return 0.1F;
	}

	public Vec3D getLookVec() {
		return null;
	}

	public void setInPortal() {
	}

	public void setVelocity(double d, double d1, double d2) {
		this.motionX = d;
		this.motionY = d1;
		this.motionZ = d2;
	}

	public void handleHealthUpdate(byte byte0) {
	}

	public void performHurtAnimation() {
	}

	public void updateCloak() {
	}

	public void outfitWithItem(int i, int j, int k) {
	}

	public boolean isBurning() {
		return this.fire > 0 || this.getEntityFlag(0);
	}

	public boolean isRiding() {
		return this.ridingEntity != null || this.getEntityFlag(2);
	}

	public boolean isSneaking() {
		return this.getEntityFlag(1);
	}

	protected boolean getEntityFlag(int i) {
		return (this.dataWatcher.getWatchableObjectByte(0) & 1 << i) != 0;
	}

	protected void setEntityFlag(int i, boolean flag) {
		byte byte0 = this.dataWatcher.getWatchableObjectByte(0);
		if(flag) {
			this.dataWatcher.updateObject(0, (byte)(byte0 | 1 << i));
		} else {
			this.dataWatcher.updateObject(0, (byte)(byte0 & ~(1 << i)));
		}

	}

	public void onStruckByLightning(EntityLightningBolt entitylightningbolt) {
		this.dealFireDamage(5);
		++this.fire;
		if(this.fire == 0) {
			this.fire = 300;
		}

	}

	public void onKillEntity(EntityLiving entityliving) {
	}

	protected boolean pushOutOfBlocks(double d, double d1, double d2) {
		int i = MathHelper.floor_double(d);
		int j = MathHelper.floor_double(d1);
		int k = MathHelper.floor_double(d2);
		double d3 = d - (double)i;
		double d4 = d1 - (double)j;
		double d5 = d2 - (double)k;
		if(this.worldObj.isBlockNormalCube(i, j, k)) {
			boolean flag = !this.worldObj.isBlockNormalCube(i - 1, j, k);
			boolean flag1 = !this.worldObj.isBlockNormalCube(i + 1, j, k);
			boolean flag2 = !this.worldObj.isBlockNormalCube(i, j - 1, k);
			boolean flag3 = !this.worldObj.isBlockNormalCube(i, j + 1, k);
			boolean flag4 = !this.worldObj.isBlockNormalCube(i, j, k - 1);
			boolean flag5 = !this.worldObj.isBlockNormalCube(i, j, k + 1);
			byte byte0 = -1;
			double d6 = 9999.0D;
			if(flag && d3 < d6) {
				d6 = d3;
				byte0 = 0;
			}

			if(flag1 && 1.0D - d3 < d6) {
				d6 = 1.0D - d3;
				byte0 = 1;
			}

			if(flag2 && d4 < d6) {
				d6 = d4;
				byte0 = 2;
			}

			if(flag3 && 1.0D - d4 < d6) {
				d6 = 1.0D - d4;
				byte0 = 3;
			}

			if(flag4 && d5 < d6) {
				d6 = d5;
				byte0 = 4;
			}

			if(flag5 && 1.0D - d5 < d6) {
				double f = 1.0D - d5;
				byte0 = 5;
			}

			float f1 = this.rand.nextFloat() * 0.2F + 0.1F;
			if(byte0 == 0) {
				this.motionX = (double)(-f1);
			}

			if(byte0 == 1) {
				this.motionX = (double)f1;
			}

			if(byte0 == 2) {
				this.motionY = (double)(-f1);
			}

			if(byte0 == 3) {
				this.motionY = (double)f1;
			}

			if(byte0 == 4) {
				this.motionZ = (double)(-f1);
			}

			if(byte0 == 5) {
				this.motionZ = (double)f1;
			}
		}

		return false;
	}
}
