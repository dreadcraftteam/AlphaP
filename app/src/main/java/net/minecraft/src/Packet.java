package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Packet {
	private static Map packetIdToClassMap = new HashMap();
	private static Map packetClassToIdMap = new HashMap();
	private static Set clientPacketIdList = new HashSet();
	private static Set serverPacketIdList = new HashSet();
	public final long creationTimeMillis = System.currentTimeMillis();
	public boolean isChunkDataPacket = false;
	private static HashMap packetStats = new HashMap();
	private static int totalPacketsCount = 0;

	static void addIdClassMapping(int i, boolean flag, boolean flag1, Class class1) {
		if(packetIdToClassMap.containsKey(i)) {
			throw new IllegalArgumentException("Duplicate packet id:" + i);
		} else if(packetClassToIdMap.containsKey(class1)) {
			throw new IllegalArgumentException("Duplicate packet class:" + class1);
		} else {
			packetIdToClassMap.put(i, class1);
			packetClassToIdMap.put(class1, i);
			if(flag) {
				clientPacketIdList.add(i);
			}

			if(flag1) {
				serverPacketIdList.add(i);
			}

		}
	}

	public static Packet getNewPacket(int i) {
		try {
			Class exception = (Class)packetIdToClassMap.get(i);
			return exception == null ? null : (Packet)exception.newInstance();
		} catch (Exception exception2) {
			exception2.printStackTrace();
			System.out.println("Skipping packet with id " + i);
			return null;
		}
	}

	public final int getPacketId() {
		return ((Integer)packetClassToIdMap.get(this.getClass())).intValue();
	}

	public static Packet readPacket(DataInputStream datainputstream, boolean flag) throws IOException {
		boolean i = false;
		Packet packet = null;

		int i6;
		try {
			i6 = datainputstream.read();
			if(i6 == -1) {
				return null;
			}

			if(flag && !serverPacketIdList.contains(i6) || !flag && !clientPacketIdList.contains(i6)) {
				throw new IOException("Bad packet id " + i6);
			}

			packet = getNewPacket(i6);
			if(packet == null) {
				throw new IOException("Bad packet id " + i6);
			}

			packet.readPacketData(datainputstream);
		} catch (EOFException eOFException5) {
			System.out.println("Reached end of stream");
			return null;
		}

		PacketCounter packetcounter = (PacketCounter)packetStats.get(i6);
		if(packetcounter == null) {
			packetcounter = new PacketCounter((Empty1)null);
			packetStats.put(i6, packetcounter);
		}

		packetcounter.addPacket(packet.getPacketSize());
		++totalPacketsCount;
		if(totalPacketsCount % 1000 != 0) {
			;
		}

		return packet;
	}

	public static void writePacket(Packet packet, DataOutputStream dataoutputstream) throws IOException {
		dataoutputstream.write(packet.getPacketId());
		packet.writePacketData(dataoutputstream);
	}

	public static void writeString(String s, DataOutputStream dataoutputstream) throws IOException {
		if(s.length() > 32767) {
			throw new IOException("String too big");
		} else {
			dataoutputstream.writeShort(s.length());
			dataoutputstream.writeChars(s);
		}
	}

	public static String readString(DataInputStream datainputstream, int i) throws IOException {
		short word0 = datainputstream.readShort();
		if(word0 > i) {
			throw new IOException("Received string length longer than maximum allowed (" + word0 + " > " + i + ")");
		} else if(word0 < 0) {
			throw new IOException("Received string length is less than zero! Weird string!");
		} else {
			StringBuilder stringbuilder = new StringBuilder();

			for(int j = 0; j < word0; ++j) {
				stringbuilder.append(datainputstream.readChar());
			}

			return stringbuilder.toString();
		}
	}

	public abstract void readPacketData(DataInputStream dataInputStream1) throws IOException;

	public abstract void writePacketData(DataOutputStream dataOutputStream1) throws IOException;

	public abstract void processPacket(NetHandler netHandler1);

	public abstract int getPacketSize();

	static Class _mthclass$(String s) {
		try {
			return Class.forName(s);
		} catch (ClassNotFoundException classNotFoundException2) {
			throw new NoClassDefFoundError(classNotFoundException2.getMessage());
		}
	}

	static {
		addIdClassMapping(0, true, true, Packet0KeepAlive.class);
		addIdClassMapping(1, true, true, Packet1Login.class);
		addIdClassMapping(2, true, true, Packet2Handshake.class);
		addIdClassMapping(3, true, true, Packet3Chat.class);
		addIdClassMapping(4, true, false, Packet4UpdateTime.class);
		addIdClassMapping(5, true, false, Packet5PlayerInventory.class);
		addIdClassMapping(6, true, false, Packet6SpawnPosition.class);
		addIdClassMapping(7, false, true, Packet7UseEntity.class);
		addIdClassMapping(8, true, false, Packet8UpdateHealth.class);
		addIdClassMapping(9, true, true, Packet9Respawn.class);
		addIdClassMapping(10, true, true, Packet10Flying.class);
		addIdClassMapping(11, true, true, Packet11PlayerPosition.class);
		addIdClassMapping(12, true, true, Packet12PlayerLook.class);
		addIdClassMapping(13, true, true, Packet13PlayerLookMove.class);
		addIdClassMapping(14, false, true, Packet14BlockDig.class);
		addIdClassMapping(15, false, true, Packet15Place.class);
		addIdClassMapping(16, false, true, Packet16BlockItemSwitch.class);
		addIdClassMapping(17, true, false, Packet17Sleep.class);
		addIdClassMapping(18, true, true, Packet18Animation.class);
		addIdClassMapping(19, false, true, Packet19EntityAction.class);
		addIdClassMapping(20, true, false, Packet20NamedEntitySpawn.class);
		addIdClassMapping(21, true, false, Packet21PickupSpawn.class);
		addIdClassMapping(22, true, false, Packet22Collect.class);
		addIdClassMapping(23, true, false, Packet23VehicleSpawn.class);
		addIdClassMapping(24, true, false, Packet24MobSpawn.class);
		addIdClassMapping(25, true, false, Packet25EntityPainting.class);
		addIdClassMapping(27, false, true, Packet27Position.class);
		addIdClassMapping(28, true, false, Packet28EntityVelocity.class);
		addIdClassMapping(29, true, false, Packet29DestroyEntity.class);
		addIdClassMapping(30, true, false, Packet30Entity.class);
		addIdClassMapping(31, true, false, Packet31RelEntityMove.class);
		addIdClassMapping(32, true, false, Packet32EntityLook.class);
		addIdClassMapping(33, true, false, Packet33RelEntityMoveLook.class);
		addIdClassMapping(34, true, false, Packet34EntityTeleport.class);
		addIdClassMapping(38, true, false, Packet38EntityStatus.class);
		addIdClassMapping(39, true, false, Packet39AttachEntity.class);
		addIdClassMapping(40, true, false, Packet40EntityMetadata.class);
		addIdClassMapping(50, true, false, Packet50PreChunk.class);
		addIdClassMapping(51, true, false, Packet51MapChunk.class);
		addIdClassMapping(52, true, false, Packet52MultiBlockChange.class);
		addIdClassMapping(53, true, false, Packet53BlockChange.class);
		addIdClassMapping(54, true, false, Packet54PlayNoteBlock.class);
		addIdClassMapping(59, false, true, Packet59RequestCube.class);
		addIdClassMapping(60, true, false, Packet60Explosion.class);
		addIdClassMapping(61, true, false, Packet61DoorChange.class);
		addIdClassMapping(70, true, false, Packet70Bed.class);
		addIdClassMapping(71, true, false, Packet71Weather.class);
		addIdClassMapping(100, true, false, Packet100OpenWindow.class);
		addIdClassMapping(101, true, true, Packet101CloseWindow.class);
		addIdClassMapping(102, false, true, Packet102WindowClick.class);
		addIdClassMapping(103, true, false, Packet103SetSlot.class);
		addIdClassMapping(104, true, false, Packet104WindowItems.class);
		addIdClassMapping(105, true, false, Packet105UpdateProgressbar.class);
		addIdClassMapping(106, true, true, Packet106Transaction.class);
		addIdClassMapping(130, true, true, Packet130UpdateSign.class);
		addIdClassMapping(131, true, false, Packet131MapData.class);
		addIdClassMapping(255, true, true, Packet255KickDisconnect.class);
	}
}
