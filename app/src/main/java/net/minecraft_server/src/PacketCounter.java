package net.minecraft_server.src;

class PacketCounter {
	private int totalPackets;
	private long totalBytes;

	private PacketCounter() {
	}

	public void addPacket(int i1) {
		++this.totalPackets;
		this.totalBytes += (long) i1;
	}

	PacketCounter(Empty1 empty11) {
		this();
	}
}
