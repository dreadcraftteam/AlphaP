package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet14BlockDig extends Packet {
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int face;
	public int status;

	public Packet14BlockDig() {
	}

	public Packet14BlockDig(int i, int j, int k, int l, int i1) {
		this.status = i;
		this.xPosition = j;
		this.yPosition = k;
		this.zPosition = l;
		this.face = i1;
	}

	public void readPacketData(DataInputStream datainputstream) throws IOException {
		this.status = datainputstream.read();
		this.xPosition = datainputstream.readInt();
		this.yPosition = datainputstream.readInt();
		this.zPosition = datainputstream.readInt();
		this.face = datainputstream.read();
	}

	public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
		dataoutputstream.write(this.status);
		dataoutputstream.writeInt(this.xPosition);
		dataoutputstream.writeInt(this.yPosition);
		dataoutputstream.writeInt(this.zPosition);
		dataoutputstream.write(this.face);
	}

	public void processPacket(NetHandler nethandler) {
		nethandler.handleBlockDig(this);
	}

	public int getPacketSize() {
		return 14;
	}
}
