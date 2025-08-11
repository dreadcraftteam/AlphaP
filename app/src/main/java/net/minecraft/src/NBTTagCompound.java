package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NBTTagCompound extends NBTBase {
	private Map tagMap = new HashMap();

	void writeTagContents(DataOutput dataoutput) throws IOException {
		Iterator iterator = this.tagMap.values().iterator();

		while(iterator.hasNext()) {
			NBTBase nbtbase = (NBTBase)iterator.next();
			NBTBase.writeTag(nbtbase, dataoutput);
		}

		dataoutput.writeByte(0);
	}

	void readTagContents(DataInput datainput) throws IOException {
		this.tagMap.clear();

		NBTBase nbtbase;
		while((nbtbase = NBTBase.readTag(datainput)).getType() != 0) {
			this.tagMap.put(nbtbase.getKey(), nbtbase);
		}

	}

	public Collection func_28110_c() {
		return this.tagMap.values();
	}

	public byte getType() {
		return (byte)10;
	}

	public void setTag(String s, NBTBase nbtbase) {
		this.tagMap.put(s, nbtbase.setKey(s));
	}

	public void setByte(String s, byte byte0) {
		this.tagMap.put(s, (new NBTTagByte(byte0)).setKey(s));
	}

	public void setShort(String s, short word0) {
		this.tagMap.put(s, (new NBTTagShort(word0)).setKey(s));
	}

	public void setInteger(String s, int i) {
		this.tagMap.put(s, (new NBTTagInt(i)).setKey(s));
	}

	public void setLong(String s, long l) {
		this.tagMap.put(s, (new NBTTagLong(l)).setKey(s));
	}

	public void setFloat(String s, float f) {
		this.tagMap.put(s, (new NBTTagFloat(f)).setKey(s));
	}

	public void setDouble(String s, double d) {
		this.tagMap.put(s, (new NBTTagDouble(d)).setKey(s));
	}

	public void setString(String s, String s1) {
		this.tagMap.put(s, (new NBTTagString(s1)).setKey(s));
	}

	public void setByteArray(String s, byte[] abyte0) {
		this.tagMap.put(s, (new NBTTagByteArray(abyte0)).setKey(s));
	}

	public void setShortArray(String s, short[] ar) {
		this.tagMap.put(s, (new NBTTagShortArray(ar)).setKey(s));
	}

	public void setIntArray(String s, int[] ar) {
		this.tagMap.put(s, (new NBTTagIntArray(ar)).setKey(s));
	}

	public void setCompoundTag(String s, NBTTagCompound nbttagcompound) {
		this.tagMap.put(s, nbttagcompound.setKey(s));
	}

	public void setBoolean(String s, boolean flag) {
		this.setByte(s, (byte)(flag ? 1 : 0));
	}

	public boolean hasKey(String s) {
		return this.tagMap.containsKey(s);
	}

	public byte getByte(String s) {
		return !this.tagMap.containsKey(s) ? 0 : ((NBTTagByte)this.tagMap.get(s)).byteValue;
	}

	public short getShort(String s) {
		return !this.tagMap.containsKey(s) ? 0 : ((NBTTagShort)this.tagMap.get(s)).shortValue;
	}

	public int getInteger(String s) {
		return !this.tagMap.containsKey(s) ? 0 : ((NBTTagInt)this.tagMap.get(s)).intValue;
	}

	public long getLong(String s) {
		return !this.tagMap.containsKey(s) ? 0L : ((NBTTagLong)this.tagMap.get(s)).longValue;
	}

	public float getFloat(String s) {
		return !this.tagMap.containsKey(s) ? 0.0F : ((NBTTagFloat)this.tagMap.get(s)).floatValue;
	}

	public double getDouble(String s) {
		return !this.tagMap.containsKey(s) ? 0.0D : ((NBTTagDouble)this.tagMap.get(s)).doubleValue;
	}

	public String getString(String s) {
		return !this.tagMap.containsKey(s) ? "" : ((NBTTagString)this.tagMap.get(s)).stringValue;
	}

	public byte[] getByteArray(String s) {
		return !this.tagMap.containsKey(s) ? new byte[0] : ((NBTTagByteArray)this.tagMap.get(s)).byteArray;
	}

	public short[] getShortArray(String s) {
		return !this.tagMap.containsKey(s) ? new short[0] : ((NBTTagShortArray)this.tagMap.get(s)).data;
	}

	public short[] getCastedIntArray(String s) {
		return !this.tagMap.containsKey(s) ? new short[0] : ((NBTTagIntArray)this.tagMap.get(s)).castArray;
	}

	public int[] getIntArray(String s) {
		return !this.tagMap.containsKey(s) ? new int[0] : ((NBTTagIntArray)this.tagMap.get(s)).regularArray;
	}

	public NBTTagCompound getCompoundTag(String s) {
		return !this.tagMap.containsKey(s) ? new NBTTagCompound() : (NBTTagCompound)this.tagMap.get(s);
	}

	public NBTTagList getTagList(String s) {
		return !this.tagMap.containsKey(s) ? new NBTTagList() : (NBTTagList)this.tagMap.get(s);
	}

	public boolean getBoolean(String s) {
		return this.getByte(s) != 0;
	}

	public String toString() {
		return "" + this.tagMap.size() + " entries";
	}

	public boolean getIsType(String str, Class cl) {
		return !this.tagMap.containsKey(str) ? false : cl.isInstance(this.tagMap.get(str));
	}
}
