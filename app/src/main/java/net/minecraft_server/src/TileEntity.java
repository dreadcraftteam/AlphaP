package net.minecraft_server.src;

import java.util.HashMap;
import java.util.Map;

public class TileEntity {
	private static Map nameToClassMap = new HashMap();
	private static Map classToNameMap = new HashMap();
	public World worldObj;
	public int xCoord;
	public int yCoord;
	public int zCoord;
	protected boolean tileEntityInvalid;

	private static void addMapping(Class class1, String s) {
		if (classToNameMap.containsKey(s)) {
			throw new IllegalArgumentException("Duplicate id: " + s);
		} else {
			nameToClassMap.put(s, class1);
			classToNameMap.put(class1, s);
		}
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		this.xCoord = nbttagcompound.getInteger("x");
		this.yCoord = nbttagcompound.getInteger("y");
		this.zCoord = nbttagcompound.getInteger("z");
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		String s = (String) classToNameMap.get(this.getClass());
		if (s == null) {
			throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
		} else {
			nbttagcompound.setString("id", s);
			nbttagcompound.setInteger("x", this.xCoord);
			nbttagcompound.setInteger("y", this.yCoord);
			nbttagcompound.setInteger("z", this.zCoord);
		}
	}

	public void updateEntity() {
	}

	public static TileEntity createAndLoadEntity(NBTTagCompound nbttagcompound) {
		TileEntity tileentity = null;

		try {
			Class exception = (Class) nameToClassMap.get(nbttagcompound.getString("id"));
			if (exception != null) {
				tileentity = (TileEntity) exception.newInstance();
			}
		} catch (Exception exception3) {
			exception3.printStackTrace();
		}

		if (tileentity != null) {
			tileentity.readFromNBT(nbttagcompound);
		} else {
			System.out.println("Skipping TileEntity with id " + nbttagcompound.getString("id"));
		}

		return tileentity;
	}

	public int func_31005_e() {
		return this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
	}

	public void onInventoryChanged() {
		if (this.worldObj != null) {
			this.worldObj.updateTileEntityChunkAndDoNothing(this.xCoord, this.yCoord, this.zCoord, this);
		}

	}

	public Packet getDescriptionPacket() {
		return null;
	}

	public boolean isInvalid() {
		return this.tileEntityInvalid;
	}

	public void invalidate() {
		this.tileEntityInvalid = true;
	}

	public void validate() {
		this.tileEntityInvalid = false;
	}

	static Class _mthclass$(String s) {
		try {
			return Class.forName(s);
		} catch (ClassNotFoundException classNotFoundException2) {
			throw new NoClassDefFoundError(classNotFoundException2.getMessage());
		}
	}

	static {
		addMapping(TileEntityFurnace.class, "Furnace");
		addMapping(TileEntityChest.class, "Chest");
		addMapping(TileEntityRecordPlayer.class, "RecordPlayer");
		addMapping(TileEntityDispenser.class, "Trap");
		addMapping(TileEntitySign.class, "Sign");
		addMapping(TileEntityMobSpawner.class, "MobSpawner");
		addMapping(TileEntityNote.class, "Music");
		addMapping(TileEntityPiston.class, "Piston");
	}
}
