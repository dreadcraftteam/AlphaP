package net.minecraft_server.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileEntityPiston extends TileEntity {
	private int storedBlockID;
	private int storedMetadata;
	private int storedOrientation;
	private boolean isExtending;
	private boolean field_31018_j;
	private float progress;
	private float lastProgress;
	private static List field_31013_m = new ArrayList();

	public TileEntityPiston() {
	}

	public TileEntityPiston(int i, int j, int k, boolean flag, boolean flag1) {
		this.storedBlockID = i;
		this.storedMetadata = j;
		this.storedOrientation = k;
		this.isExtending = flag;
		this.field_31018_j = flag1;
	}

	public int getStoredBlockID() {
		return this.storedBlockID;
	}

	public int func_31005_e() {
		return this.storedMetadata;
	}

	public boolean func_31010_c() {
		return this.isExtending;
	}

	public int func_31008_d() {
		return this.storedOrientation;
	}

	public float func_31007_a(float f) {
		if (f > 1.0F) {
			f = 1.0F;
		}

		return this.lastProgress + (this.progress - this.lastProgress) * f;
	}

	private void func_31009_a(float f, float f1) {
		if (!this.isExtending) {
			--f;
		} else {
			f = 1.0F - f;
		}

		AxisAlignedBB axisalignedbb = Block.pistonMoving.func_31032_a(this.worldObj, this.xCoord, this.yCoord,
				this.zCoord, this.storedBlockID, f, this.storedOrientation);
		if (axisalignedbb != null) {
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity) null, axisalignedbb);
			if (!list.isEmpty()) {
				field_31013_m.addAll(list);
				Iterator iterator = field_31013_m.iterator();

				while (iterator.hasNext()) {
					Entity entity = (Entity) iterator.next();
					entity.moveEntity(
							(double) (f1 * (float) PistonBlockTextures.offsetsXForSide[this.storedOrientation]),
							(double) (f1 * (float) PistonBlockTextures.offsetsYForSide[this.storedOrientation]),
							(double) (f1 * (float) PistonBlockTextures.offsetsZForSide[this.storedOrientation]));
				}

				field_31013_m.clear();
			}
		}

	}

	public void clearPistonTileEntity() {
		if (this.lastProgress < 1.0F) {
			this.lastProgress = this.progress = 1.0F;
			this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
			this.invalidate();
			if (this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) == Block.pistonMoving.blockID) {
				this.worldObj.setBlockAndMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID,
						this.storedMetadata);
			}
		}

	}

	public void updateEntity() {
		this.lastProgress = this.progress;
		if (this.lastProgress >= 1.0F) {
			this.func_31009_a(1.0F, 0.25F);
			this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
			this.invalidate();
			if (this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) == Block.pistonMoving.blockID) {
				this.worldObj.setBlockAndMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID,
						this.storedMetadata);
			}

		} else {
			this.progress += 0.5F;
			if (this.progress >= 1.0F) {
				this.progress = 1.0F;
			}

			if (this.isExtending) {
				this.func_31009_a(this.progress, this.progress - this.lastProgress + 0.0625F);
			}

		}
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.storedBlockID = nbttagcompound.getInteger("blockId");
		this.storedMetadata = nbttagcompound.getInteger("blockData");
		this.storedOrientation = nbttagcompound.getInteger("facing");
		this.lastProgress = this.progress = nbttagcompound.getFloat("progress");
		this.isExtending = nbttagcompound.getBoolean("extending");
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("blockId", this.storedBlockID);
		nbttagcompound.setInteger("blockData", this.storedMetadata);
		nbttagcompound.setInteger("facing", this.storedOrientation);
		nbttagcompound.setFloat("progress", this.lastProgress);
		nbttagcompound.setBoolean("extending", this.isExtending);
	}
}
