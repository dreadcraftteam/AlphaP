package net.minecraft_server.src;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

class PlayerInstance {
	private List players;
	private int chunkX;
	private int chunkY;
	private int chunkZ;
	private ChunkCubeCoord e;
	private short[] blocksToUpdate;
	private int numBlocksToUpdate;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int minZ;
	private int maxZ;
	final PlayerManager playerManager;

	public PlayerInstance(PlayerManager playermanager, int x, int y, int z) {
		this.playerManager = playermanager;
		this.players = new ArrayList();
		this.blocksToUpdate = new short[10];
		this.numBlocksToUpdate = 0;
		this.chunkX = x;
		this.chunkY = y;
		this.chunkZ = z;
		this.e = new ChunkCubeCoord(x, y, z);
		playermanager.getMinecraftServer().chunkProviderServer.loadCube(x, y, z);
	}

	public void addPlayer(EntityPlayerMP entityplayermp) {
		if (this.players.contains(entityplayermp)) {
			throw new IllegalStateException("Failed to add player. " + entityplayermp + " already is in chunk: "
					+ this.chunkX + ", " + this.chunkY + ", " + this.chunkZ + ". Player\'s ChunkCoords: "
					+ entityplayermp.chunkCoordX + ", " + entityplayermp.chunkCoordY + ", "
					+ entityplayermp.chunkCoordZ);
		} else {
			entityplayermp.field_420_ah.add(this.e);
			entityplayermp.playerNetServerHandler.sendPacket(new Packet50PreChunk(this.e.chunkXPos, this.e.chunkYPos,
					this.e.chunkZPos, true, this.getIsAir(this.e.chunkXPos, this.e.chunkYPos, this.e.chunkZPos)));
			this.players.add(entityplayermp);
			entityplayermp.chunksToLoad.add(this.e);
		}
	}

	public void removePlayer(EntityPlayerMP entityplayermp) {
		if (this.players.contains(entityplayermp)) {
			this.players.remove(entityplayermp);
			if (this.players.size() == 0) {
				long l = (long) this.chunkX + 2147483647L | (long) this.chunkZ + 2147483647L << 32;
				PlayerHash playerInstances = PlayerManager.getPlayerInstances(this.playerManager);
				Hashtable instances = (Hashtable) playerInstances.getValueByKey(l);
				instances.remove(this.chunkY);
				if (instances.isEmpty()) {
					playerInstances.remove(l);
				}

				if (this.numBlocksToUpdate > 0) {
					PlayerManager.getPlayerInstancesToUpdate(this.playerManager).remove(this);
				}

				this.playerManager.getMinecraftServer().chunkProviderServer.func_374_c(this.chunkX, this.chunkZ);
			}

			entityplayermp.chunksToLoad.remove(this.e);
			if (entityplayermp.field_420_ah.contains(this.e)) {
				entityplayermp.playerNetServerHandler
						.sendPacket(new Packet50PreChunk(this.e.chunkXPos, this.e.chunkYPos, this.e.chunkZPos, true,
								this.getIsAir(this.e.chunkXPos, this.e.chunkYPos, this.e.chunkZPos)));
			}

		}
	}

	public void markBlockNeedsUpdate(int i, int j, int k) {
		if (this.numBlocksToUpdate == 0) {
			PlayerManager.getPlayerInstancesToUpdate(this.playerManager).add(this);
			this.minX = this.maxX = i;
			this.minY = this.maxY = j;
			this.minZ = this.maxZ = k;
		}

		if (this.minX > i) {
			this.minX = i;
		}

		if (this.maxX < i) {
			this.maxX = i;
		}

		if (this.minY > j) {
			this.minY = j;
		}

		if (this.maxY < j) {
			this.maxY = j;
		}

		if (this.minZ > k) {
			this.minZ = k;
		}

		if (this.maxZ < k) {
			this.maxZ = k;
		}

		if (this.numBlocksToUpdate < 10) {
			short word0 = (short) (i << 12 | k << 8 | j);

			for (int l = 0; l < this.numBlocksToUpdate; ++l) {
				if (this.blocksToUpdate[l] == word0) {
					return;
				}
			}

			this.blocksToUpdate[this.numBlocksToUpdate++] = word0;
		}

	}

	public void sendPacketToPlayersInInstance(Packet packet) {
		for (int i = 0; i < this.players.size(); ++i) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) this.players.get(i);
			if (entityplayermp.field_420_ah.contains(this.e)) {
				entityplayermp.playerNetServerHandler.sendPacket(packet);
			}
		}

	}

	public void onUpdate() {
		WorldServer worldserver = this.playerManager.getMinecraftServer();
		if (this.numBlocksToUpdate != 0) {
			int k;
			int j1;
			int i2;
			if (this.numBlocksToUpdate == 1) {
				k = this.chunkX * 16 + this.minX;
				j1 = this.chunkY * 16 + this.minY;
				i2 = this.chunkZ * 16 + this.minZ;
				this.sendPacketToPlayersInInstance(new Packet53BlockChange(k, j1, i2, worldserver));
				if (Block.isBlockContainer[worldserver.getBlockId(k, j1, i2)]) {
					this.updateTileEntity(worldserver.getBlockTileEntity(k, j1, i2));
				}
			} else {
				int k2;
				if (this.numBlocksToUpdate == 10) {
					this.minY = this.minY / 2 * 2;
					this.maxY = (this.maxY / 2 + 1) * 2;
					k = this.minX + this.chunkX * 16;
					j1 = this.minY + this.chunkY * 16;
					i2 = this.minZ + this.chunkZ * 16;
					k2 = this.maxX - this.minX + 1;
					int l2 = this.maxY - this.minY + 2;
					int i3 = this.maxZ - this.minZ + 1;
					this.sendPacketToPlayersInInstance(new Packet51MapChunk(k, j1, i2, k2, l2, i3, worldserver));
					List list = worldserver.getTileEntityList(k, j1, i2, k + k2, j1 + l2, i2 + i3);

					for (int j3 = 0; j3 < list.size(); ++j3) {
						this.updateTileEntity((TileEntity) list.get(j3));
					}
				} else {
					this.sendPacketToPlayersInInstance(new Packet52MultiBlockChange(this.chunkX, this.chunkZ,
							this.blocksToUpdate, this.numBlocksToUpdate, worldserver));

					for (k = 0; k < this.numBlocksToUpdate; ++k) {
						j1 = this.chunkX * 16 + (this.numBlocksToUpdate >> 12 & 15);
						i2 = this.chunkY * 16 + (this.numBlocksToUpdate & 255);
						k2 = this.chunkZ * 16 + (this.numBlocksToUpdate >> 8 & 15);
						if (Block.isBlockContainer[worldserver.getBlockId(j1, i2, k2)]) {
							System.out.println("Sending!");
							this.updateTileEntity(worldserver.getBlockTileEntity(j1, i2, k2));
						}
					}
				}
			}

			this.numBlocksToUpdate = 0;
		}
	}

	private void updateTileEntity(TileEntity tileentity) {
		if (tileentity != null) {
			Packet packet = tileentity.getDescriptionPacket();
			if (packet != null) {
				this.sendPacketToPlayersInInstance(packet);
			}
		}

	}

	private boolean getIsAir(int x, int y, int z) {
		WorldServer server = this.playerManager.getMinecraftServer();
		boolean isAir = false;
		if (server.cubeExists(this.e.chunkXPos, this.e.chunkYPos, this.e.chunkZPos)) {
			isAir = server.getChunkFromChunkCoords(this.e.chunkXPos, this.e.chunkYPos, this.e.chunkZPos).isAir;
		}

		return isAir;
	}
}
