package net.minecraft_server.src;

import java.util.ArrayList;
import java.util.List;

public class EntityPainting extends Entity {
	private int field_452_ad;
	public int direction;
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public EnumArt art;

	public EntityPainting(World world) {
		super(world);
		this.field_452_ad = 0;
		this.direction = 0;
		this.yOffset = 0.0F;
		this.setSize(0.5F, 0.5F);
	}

	public EntityPainting(World world, int i, int j, int k, int l) {
		this(world);
		this.xPosition = i;
		this.yPosition = j;
		this.zPosition = k;
		ArrayList arraylist = new ArrayList();
		EnumArt[] aenumart = EnumArt.values();
		int i1 = aenumart.length;

		for (int j1 = 0; j1 < i1; ++j1) {
			EnumArt enumart = aenumart[j1];
			this.art = enumart;
			this.func_179_a(l);
			if (this.onValidSurface()) {
				arraylist.add(enumart);
			}
		}

		if (arraylist.size() > 0) {
			this.art = (EnumArt) arraylist.get(this.rand.nextInt(arraylist.size()));
		}

		this.func_179_a(l);
	}

	protected void entityInit() {
	}

	public void func_179_a(int i) {
		this.direction = i;
		this.prevRotationYaw = this.rotationYaw = (float) (i * 90);
		float f = (float) this.art.sizeX;
		float f1 = (float) this.art.sizeY;
		float f2 = (float) this.art.sizeX;
		if (i != 0 && i != 2) {
			f = 0.5F;
		} else {
			f2 = 0.5F;
		}

		f /= 32.0F;
		f1 /= 32.0F;
		f2 /= 32.0F;
		float f3 = (float) this.xPosition + 0.5F;
		float f4 = (float) this.yPosition + 0.5F;
		float f5 = (float) this.zPosition + 0.5F;
		float f6 = 0.5625F;
		if (i == 0) {
			f5 -= f6;
		}

		if (i == 1) {
			f3 -= f6;
		}

		if (i == 2) {
			f5 += f6;
		}

		if (i == 3) {
			f3 += f6;
		}

		if (i == 0) {
			f3 -= this.func_180_c(this.art.sizeX);
		}

		if (i == 1) {
			f5 += this.func_180_c(this.art.sizeX);
		}

		if (i == 2) {
			f3 += this.func_180_c(this.art.sizeX);
		}

		if (i == 3) {
			f5 -= this.func_180_c(this.art.sizeX);
		}

		f4 += this.func_180_c(this.art.sizeY);
		this.setPosition((double) f3, (double) f4, (double) f5);
		float f7 = -0.00625F;
		this.boundingBox.setBounds((double) (f3 - f - f7), (double) (f4 - f1 - f7), (double) (f5 - f2 - f7),
				(double) (f3 + f + f7), (double) (f4 + f1 + f7), (double) (f5 + f2 + f7));
	}

	private float func_180_c(int i) {
		return i == 32 ? 0.5F : (i != 64 ? 0.0F : 0.5F);
	}

	public void onUpdate() {
		if (this.field_452_ad++ == 100 && !this.worldObj.multiplayerWorld) {
			this.field_452_ad = 0;
			if (!this.onValidSurface()) {
				this.setEntityDead();
				this.worldObj.entityJoinedWorld(
						new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
			}
		}

	}

	public boolean onValidSurface() {
		if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() > 0) {
			return false;
		} else {
			int i = this.art.sizeX / 16;
			int j = this.art.sizeY / 16;
			int k = this.xPosition;
			int l = this.yPosition;
			int i1 = this.zPosition;
			if (this.direction == 0) {
				k = MathHelper.floor_double(this.posX - (double) ((float) this.art.sizeX / 32.0F));
			}

			if (this.direction == 1) {
				i1 = MathHelper.floor_double(this.posZ - (double) ((float) this.art.sizeX / 32.0F));
			}

			if (this.direction == 2) {
				k = MathHelper.floor_double(this.posX - (double) ((float) this.art.sizeX / 32.0F));
			}

			if (this.direction == 3) {
				i1 = MathHelper.floor_double(this.posZ - (double) ((float) this.art.sizeX / 32.0F));
			}

			l = MathHelper.floor_double(this.posY - (double) ((float) this.art.sizeY / 32.0F));

			int l1;
			for (int list = 0; list < i; ++list) {
				for (l1 = 0; l1 < j; ++l1) {
					Material material;
					if (this.direction != 0 && this.direction != 2) {
						material = this.worldObj.getBlockMaterial(this.xPosition, l + l1, i1 + list);
					} else {
						material = this.worldObj.getBlockMaterial(k + list, l + l1, this.zPosition);
					}

					if (!material.isSolid()) {
						return false;
					}
				}
			}

			List list9 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);

			for (l1 = 0; l1 < list9.size(); ++l1) {
				if (list9.get(l1) instanceof EntityPainting) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean canBeCollidedWith() {
		return true;
	}

	public boolean attackEntityFrom(Entity entity, int i) {
		if (!this.isDead && !this.worldObj.multiplayerWorld) {
			this.setEntityDead();
			this.setBeenAttacked();
			this.worldObj.entityJoinedWorld(
					new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
		}

		return true;
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setByte("Dir", (byte) this.direction);
		nbttagcompound.setString("Motive", this.art.title);
		nbttagcompound.setInteger("TileX", this.xPosition);
		nbttagcompound.setInteger("TileY", this.yPosition);
		nbttagcompound.setInteger("TileZ", this.zPosition);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.direction = nbttagcompound.getByte("Dir");
		this.xPosition = nbttagcompound.getInteger("TileX");
		this.yPosition = nbttagcompound.getInteger("TileY");
		this.zPosition = nbttagcompound.getInteger("TileZ");
		String s = nbttagcompound.getString("Motive");
		EnumArt[] aenumart = EnumArt.values();
		int i = aenumart.length;

		for (int j = 0; j < i; ++j) {
			EnumArt enumart = aenumart[j];
			if (enumart.title.equals(s)) {
				this.art = enumart;
			}
		}

		if (this.art == null) {
			this.art = EnumArt.Kebab;
		}

		this.func_179_a(this.direction);
	}

	public void moveEntity(double d, double d1, double d2) {
		if (!this.worldObj.multiplayerWorld && d * d + d1 * d1 + d2 * d2 > 0.0D) {
			this.setEntityDead();
			this.worldObj.entityJoinedWorld(
					new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
		}

	}

	public void addVelocity(double d, double d1, double d2) {
		if (!this.worldObj.multiplayerWorld && d * d + d1 * d1 + d2 * d2 > 0.0D) {
			this.setEntityDead();
			this.worldObj.entityJoinedWorld(
					new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
		}

	}
}
