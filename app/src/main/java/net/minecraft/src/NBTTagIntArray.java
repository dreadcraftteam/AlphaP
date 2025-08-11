package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagIntArray extends NBTBase {
	public int[] regularArray;
	public short[] castArray;
	public boolean usesAlternate = false;

	public NBTTagIntArray() {
	}

	public NBTTagIntArray(int[] ai) {
		this.regularArray = ai;
	}

	public NBTTagIntArray(short[] ai) {
		this.castArray = ai;
		this.usesAlternate = true;
	}

	void writeTagContents(DataOutput dataoutput) throws IOException {
		int i;
		if(this.usesAlternate) {
			dataoutput.writeInt(this.castArray.length);

			for(i = 0; i < this.castArray.length; ++i) {
				dataoutput.writeInt(this.castArray[i]);
			}
		} else {
			dataoutput.writeInt(this.regularArray.length);

			for(i = 0; i < this.regularArray.length; ++i) {
				dataoutput.writeInt(this.regularArray[i]);
			}
		}

	}

	void readTagContents(DataInput datainput) throws IOException {
		int len = datainput.readInt();
		this.castArray = new short[len];
		this.regularArray = new int[len];

		for(int n = 0; n < this.castArray.length; ++n) {
			int val = datainput.readInt();
			this.regularArray[n] = val;
			this.castArray[n] = (short)val;
		}

	}

	public byte getType() {
		return (byte)11;
	}

	public String toString() {
		return "[" + this.regularArray.length + " ints]";
	}
}
