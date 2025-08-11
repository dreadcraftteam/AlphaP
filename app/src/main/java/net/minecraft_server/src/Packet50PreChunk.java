package net.minecraft_server.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet50PreChunk extends Packet {
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public boolean mode;
	public boolean isAir;

	public Packet50PreChunk() {
		this.isChunkDataPacket = false;
	}

	public Packet50PreChunk(int x, int y, int z, boolean Mode, boolean IsAir) {
		this.isChunkDataPacket = false;
		this.xPosition = x;
		this.yPosition = y;
		this.zPosition = z;
		this.mode = Mode;
		this.isAir = IsAir;
	}

	public void readPacketData(DataInputStream datainputstream) throws IOException {
		this.xPosition = datainputstream.readInt();
		this.yPosition = datainputstream.readInt();
		this.zPosition = datainputstream.readInt();
		byte temp = datainputstream.readByte();
		this.mode = temp % 2 != 0;
		this.isAir = temp > 1;
	}

	public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
		dataoutputstream.writeInt(this.xPosition);
		dataoutputstream.writeInt(this.yPosition);
		dataoutputstream.writeInt(this.zPosition);
		int temp = 0;
		if (this.mode) {
			++temp;
		}

		if (this.isAir) {
			temp += 2;
		}

		dataoutputstream.write(temp);
	}

	public void processPacket(NetHandler nethandler) {
		nethandler.handlePreChunk(this);
	}

	public int getPacketSize() {
		return 13;
	}
}
