package net.minecraft_server.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet59RequestCube extends Packet {
	public int xPosition;
	public int yPosition;
	public int zPosition;

	public Packet59RequestCube() {
		this.isChunkDataPacket = false;
	}

	public Packet59RequestCube(int x, int y, int z) {
		this.isChunkDataPacket = false;
		this.xPosition = x;
		this.yPosition = y;
		this.zPosition = z;
	}

	public void readPacketData(DataInputStream datainputstream) throws IOException {
		this.xPosition = datainputstream.readInt();
		this.yPosition = datainputstream.readInt();
		this.zPosition = datainputstream.readInt();
	}

	public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
		dataoutputstream.writeInt(this.xPosition);
		dataoutputstream.writeInt(this.yPosition);
		dataoutputstream.writeInt(this.zPosition);
	}

	public void processPacket(NetHandler nethandler) {
		nethandler.handleCubeRequest(this);
	}

	public int getPacketSize() {
		return 12;
	}
}
