package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet15Place extends Packet {
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int direction;
	public ItemStack itemStack;

	public Packet15Place() {
	}

	public Packet15Place(int i, int j, int k, int l, ItemStack itemstack) {
		this.xPosition = i;
		this.yPosition = j;
		this.zPosition = k;
		this.direction = l;
		this.itemStack = itemstack;
	}

	public void readPacketData(DataInputStream datainputstream) throws IOException {
		this.xPosition = datainputstream.readInt();
		this.yPosition = datainputstream.readInt();
		this.zPosition = datainputstream.readInt();
		this.direction = datainputstream.read();
		short word0 = datainputstream.readShort();
		if(word0 >= 0) {
			byte byte0 = datainputstream.readByte();
			short word1 = datainputstream.readShort();
			this.itemStack = new ItemStack(word0, byte0, word1);
		} else {
			this.itemStack = null;
		}

	}

	public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
		dataoutputstream.writeInt(this.xPosition);
		dataoutputstream.writeInt(this.yPosition);
		dataoutputstream.writeInt(this.zPosition);
		dataoutputstream.write(this.direction);
		if(this.itemStack == null) {
			dataoutputstream.writeShort(-1);
		} else {
			dataoutputstream.writeShort(this.itemStack.itemID);
			dataoutputstream.writeByte(this.itemStack.stackSize);
			dataoutputstream.writeShort(this.itemStack.getItemDamage());
		}

	}

	public void processPacket(NetHandler nethandler) {
		nethandler.handlePlace(this);
	}

	public int getPacketSize() {
		return 18;
	}
}
