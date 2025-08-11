package net.minecraft_server.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Packet51MapChunk extends Packet {
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int xSize;
	public int ySize;
	public int zSize;
	public byte[] chunk;
	private int chunkSize;
	public boolean isAir = false;

	public Packet51MapChunk() {
		this.isChunkDataPacket = true;
	}

	public Packet51MapChunk(int i, int j, int k, int l, int i1, int j1, World world) {
		this.isChunkDataPacket = true;
		this.xPosition = i;
		this.yPosition = j;
		this.zPosition = k;
		this.xSize = l;
		this.ySize = i1;
		this.zSize = j1;
		byte[] abyte0 = world.getChunkData(i, j, k, l, i1, j1);
		Deflater deflater = new Deflater(-1);

		try {
			deflater.setInput(abyte0);
			deflater.finish();
			this.chunk = new byte[l * i1 * j1 * 5 / 2];
			this.chunkSize = deflater.deflate(this.chunk);
		} finally {
			deflater.end();
		}

	}

	public void readPacketData(DataInputStream datainputstream) throws IOException {
		this.xPosition = datainputstream.readInt();
		this.yPosition = datainputstream.readShort();
		this.zPosition = datainputstream.readInt();
		this.xSize = datainputstream.read() + 1;
		this.ySize = datainputstream.read() + 1;
		this.zSize = datainputstream.read() + 1;
		this.chunkSize = datainputstream.readInt();
		if (this.chunkSize == 0) {
			this.isAir = true;
		} else {
			byte[] abyte0 = new byte[this.chunkSize];
			datainputstream.readFully(abyte0);
			this.chunk = new byte[this.xSize * this.ySize * this.zSize * 5 / 2];
			Inflater inflater = new Inflater();
			inflater.setInput(abyte0);

			try {
				inflater.inflate(this.chunk);
			} catch (DataFormatException dataFormatException8) {
				throw new IOException("Bad compressed data format");
			} finally {
				inflater.end();
			}

		}
	}

	public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
		dataoutputstream.writeInt(this.xPosition);
		dataoutputstream.writeShort(this.yPosition);
		dataoutputstream.writeInt(this.zPosition);
		dataoutputstream.write(this.xSize - 1);
		dataoutputstream.write(this.ySize - 1);
		dataoutputstream.write(this.zSize - 1);
		if (this.isAir) {
			dataoutputstream.writeInt(0);
		} else {
			dataoutputstream.writeInt(this.chunkSize);
			dataoutputstream.write(this.chunk, 0, this.chunkSize);
		}

	}

	public void processPacket(NetHandler nethandler) {
		nethandler.handleMapChunk(this);
	}

	public int getPacketSize() {
		return 17 + this.chunkSize;
	}

	public Packet51MapChunk setIsAir(boolean IsAir) {
		this.isAir = IsAir;
		return this;
	}
}
