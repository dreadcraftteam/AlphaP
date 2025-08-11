package net.minecraft_server.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShortArray extends NBTBase {
	public short[] data;

	public NBTTagShortArray() {
	}

	public NBTTagShortArray(short[] ai) {
		this.data = ai;
	}

	void writeTagContents(DataOutput dataoutput) throws IOException {
		dataoutput.writeInt(this.data.length);

		for (int i = 0; i < this.data.length; ++i) {
			dataoutput.writeShort(this.data[i]);
		}

	}

	void readTagContents(DataInput datainput) throws IOException {
		int len = datainput.readInt();
		this.data = new short[len];

		for (int n = 0; n < this.data.length; ++n) {
			this.data[n] = datainput.readShort();
		}

	}

	public byte getType() {
		return (byte) 12;
	}

	public String toString() {
		return "[" + this.data.length + " ints]";
	}
}
