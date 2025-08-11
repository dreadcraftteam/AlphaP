package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBTBase {
	private String key = null;

	abstract void writeTagContents(DataOutput dataOutput1) throws IOException;

	abstract void readTagContents(DataInput dataInput1) throws IOException;

	public abstract byte getType();

	public String getKey() {
		return this.key == null ? "" : this.key;
	}

	public NBTBase setKey(String s) {
		this.key = s;
		return this;
	}

	public static NBTBase readTag(DataInput datainput) throws IOException {
		byte byte0 = datainput.readByte();
		if(byte0 == 0) {
			return new NBTTagEnd();
		} else {
			NBTBase nbtbase = createTagOfType(byte0);
			nbtbase.key = datainput.readUTF();
			nbtbase.readTagContents(datainput);
			return nbtbase;
		}
	}

	public static void writeTag(NBTBase nbtbase, DataOutput dataoutput) throws IOException {
		dataoutput.writeByte(nbtbase.getType());
		if(nbtbase.getType() != 0) {
			dataoutput.writeUTF(nbtbase.getKey());
			nbtbase.writeTagContents(dataoutput);
		}
	}

	public static NBTBase createTagOfType(byte byte0) {
		switch(byte0) {
		case 0:
			return new NBTTagEnd();
		case 1:
			return new NBTTagByte();
		case 2:
			return new NBTTagShort();
		case 3:
			return new NBTTagInt();
		case 4:
			return new NBTTagLong();
		case 5:
			return new NBTTagFloat();
		case 6:
			return new NBTTagDouble();
		case 7:
			return new NBTTagByteArray();
		case 8:
			return new NBTTagString();
		case 9:
			return new NBTTagList();
		case 10:
			return new NBTTagCompound();
		case 11:
			return new NBTTagIntArray();
		case 12:
			return new NBTTagShortArray();
		default:
			return null;
		}
	}

	public static String getTagName(byte byte0) {
		switch(byte0) {
		case 0:
			return "TAG_End";
		case 1:
			return "TAG_Byte";
		case 2:
			return "TAG_Short";
		case 3:
			return "TAG_Int";
		case 4:
			return "TAG_Long";
		case 5:
			return "TAG_Float";
		case 6:
			return "TAG_Double";
		case 7:
			return "TAG_Byte_Array";
		case 8:
			return "TAG_String";
		case 9:
			return "TAG_List";
		case 10:
			return "TAG_Compound";
		case 11:
			return "TAG_Int_Array";
		case 12:
			return "TAG_Short_Array";
		default:
			return "UNKNOWN";
		}
	}
}
