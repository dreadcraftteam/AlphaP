package net.minecraft_server.src;

public class TileEntityMobSpawner extends TileEntity {
	public int delay = -1;
	private String mobID = "Pig";
	public double yaw;
	public double yaw2 = 0.0D;

	public TileEntityMobSpawner() {
		this.delay = 20;
	}

	public void setMobID(String s) {
		this.mobID = s;
	}

	public boolean anyPlayerInRange() {
		return this.worldObj.getClosestPlayer((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D,
				(double) this.zCoord + 0.5D, 16.0D) != null;
	}

	public void updateEntity() {
		this.yaw2 = this.yaw;
		if (this.anyPlayerInRange()) {
			double d = (double) ((float) this.xCoord + this.worldObj.rand.nextFloat());
			double d2 = (double) ((float) this.yCoord + this.worldObj.rand.nextFloat());
			double d4 = (double) ((float) this.zCoord + this.worldObj.rand.nextFloat());
			this.worldObj.spawnParticle("smoke", d, d2, d4, 0.0D, 0.0D, 0.0D);
			this.worldObj.spawnParticle("flame", d, d2, d4, 0.0D, 0.0D, 0.0D);

			for (this.yaw += (double) (1000.0F
					/ ((float) this.delay + 200.0F)); this.yaw > 360.0D; this.yaw2 -= 360.0D) {
				this.yaw -= 360.0D;
			}

			if (!this.worldObj.multiplayerWorld) {
				if (this.delay == -1) {
					this.updateDelay();
				}

				if (this.delay > 0) {
					--this.delay;
					return;
				}

				byte byte0 = 4;

				for (int i = 0; i < byte0; ++i) {
					EntityLiving entityliving = (EntityLiving) EntityList.createEntityInWorld(this.mobID,
							this.worldObj);
					if (entityliving == null) {
						return;
					}

					int j = this.worldObj.getEntitiesWithinAABB(entityliving.getClass(), AxisAlignedBB
							.getBoundingBoxFromPool((double) this.xCoord, (double) this.yCoord, (double) this.zCoord,
									(double) (this.xCoord + 1), (double) (this.yCoord + 1), (double) (this.zCoord + 1))
							.expand(8.0D, 4.0D, 8.0D)).size();
					if (j >= 6) {
						this.updateDelay();
						return;
					}

					if (entityliving != null) {
						double d6 = (double) this.xCoord
								+ (this.worldObj.rand.nextDouble() - this.worldObj.rand.nextDouble()) * 4.0D;
						double d7 = (double) (this.yCoord + this.worldObj.rand.nextInt(3) - 1);
						double d8 = (double) this.zCoord
								+ (this.worldObj.rand.nextDouble() - this.worldObj.rand.nextDouble()) * 4.0D;
						entityliving.setLocationAndAngles(d6, d7, d8, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
						if (entityliving.getCanSpawnHere()) {
							this.worldObj.entityJoinedWorld(entityliving);

							for (int k = 0; k < 20; ++k) {
								double d1 = (double) this.xCoord + 0.5D
										+ ((double) this.worldObj.rand.nextFloat() - 0.5D) * 2.0D;
								double d3 = (double) this.yCoord + 0.5D
										+ ((double) this.worldObj.rand.nextFloat() - 0.5D) * 2.0D;
								double d5 = (double) this.zCoord + 0.5D
										+ ((double) this.worldObj.rand.nextFloat() - 0.5D) * 2.0D;
								this.worldObj.spawnParticle("smoke", d1, d3, d5, 0.0D, 0.0D, 0.0D);
								this.worldObj.spawnParticle("flame", d1, d3, d5, 0.0D, 0.0D, 0.0D);
							}

							entityliving.spawnExplosionParticle();
							this.updateDelay();
						}
					}
				}
			}

			super.updateEntity();
		}
	}

	private void updateDelay() {
		this.delay = 200 + this.worldObj.rand.nextInt(600);
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.mobID = nbttagcompound.getString("EntityId");
		this.delay = nbttagcompound.getShort("Delay");
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setString("EntityId", this.mobID);
		nbttagcompound.setShort("Delay", (short) this.delay);
	}
}
