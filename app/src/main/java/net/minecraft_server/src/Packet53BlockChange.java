package net.minecraft_server.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet53BlockChange extends Packet {
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int type;
	public int metadata;

	public Packet53BlockChange() {
		this.isChunkDataPacket = true;
	}

	public Packet53BlockChange(int i, int j, int k, World world) {
		this.isChunkDataPacket = true;
		this.xPosition = i;
		this.yPosition = j;
		this.zPosition = k;
		this.type = world.getBlockId(i, j, k);
		this.metadata = world.getBlockMetadata(i, j, k);
	}

	public void readPacketData(DataInputStream datainputstream) throws IOException {
		this.xPosition = datainputstream.readInt();
		this.yPosition = datainputstream.readInt();
		this.zPosition = datainputstream.readInt();
		this.type = datainputstream.read();
		this.metadata = datainputstream.read();
	}

	public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
		dataoutputstream.writeInt(this.xPosition);
		dataoutputstream.writeInt(this.yPosition);
		dataoutputstream.writeInt(this.zPosition);
		dataoutputstream.write(this.type);
		dataoutputstream.write(this.metadata);
	}

	public void processPacket(NetHandler nethandler) {
		nethandler.handleBlockChange(this);
	}

	public int getPacketSize() {
		return 14;
	}
}
