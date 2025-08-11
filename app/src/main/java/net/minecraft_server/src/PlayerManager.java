package net.minecraft_server.src;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.minecraft_server.server.MinecraftServer;

public class PlayerManager {
	public List players = new ArrayList();
	private PlayerHash playerInstances = new PlayerHash();
	private List playerInstancesToUpdate = new ArrayList();
	private MinecraftServer mcServer;
	private int field_28110_e;
	private int playerViewRadius;
	private final int[][] field_22089_e = new int[][] { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

	public PlayerManager(MinecraftServer minecraftserver, int i, int j) {
		if (j > 15) {
			throw new IllegalArgumentException("Too big view radius!");
		} else if (j < 3) {
			throw new IllegalArgumentException("Too small view radius!");
		} else {
			this.playerViewRadius = j;
			this.mcServer = minecraftserver;
			this.field_28110_e = i;
		}
	}

	public WorldServer getMinecraftServer() {
		return this.mcServer.getWorldManager(this.field_28110_e);
	}

	public void updatePlayerInstances() {
		for (int i = 0; i < this.playerInstancesToUpdate.size(); ++i) {
			((PlayerInstance) this.playerInstancesToUpdate.get(i)).onUpdate();
		}

		this.playerInstancesToUpdate.clear();
	}

	private PlayerInstance getPlayerInstance(int x, int y, int z, boolean flag) {
		long l = (long) x + 2147483647L | (long) z + 2147483647L << 32;
		Hashtable instances = (Hashtable) this.playerInstances.getValueByKey(l);
		if (instances == null) {
			if (!flag) {
				return null;
			}

			instances = new Hashtable();
			this.playerInstances.add(l, instances);
		}

		PlayerInstance instance = (PlayerInstance) instances.get(y);
		if (instance == null && flag) {
			instance = new PlayerInstance(this, x, y, z);
			instances.put(y, instance);
		}

		return instance;
	}

	public void markBlockNeedsUpdate(int x, int y, int z) {
		int xCh = x >> 4;
		int yCh = y >> 4;
		int zCh = z >> 4;
		PlayerInstance playerinstance = this.getPlayerInstance(xCh, yCh, zCh, false);
		if (playerinstance != null) {
			playerinstance.markBlockNeedsUpdate(x & 15, y, z & 15);
		}

	}

	public void addPlayer(EntityPlayerMP entityplayermp) {
		int xCh = (int) entityplayermp.posX >> 4;
		int yCh = (int) entityplayermp.posY >> 4;
		int zCh = (int) entityplayermp.posZ >> 4;
		entityplayermp.lastUpdatedX = entityplayermp.posX;
		entityplayermp.lastUpdatedY = entityplayermp.posY;
		entityplayermp.lastUpdatedZ = entityplayermp.posZ;
		int viewRadius = this.playerViewRadius;
		int count = 0;
		int x1 = 0;
		int z1 = 0;

		int l1;
		for (l1 = -viewRadius; l1 <= viewRadius; ++l1) {
			this.getPlayerInstance(xCh, yCh + l1, zCh, true).addPlayer(entityplayermp);
		}

		int y1;
		for (l1 = 1; l1 <= viewRadius * 2; ++l1) {
			for (y1 = 0; y1 < 2; ++y1) {
				int[] ai = this.field_22089_e[count++ % 4];

				for (int m = 0; m < l1; ++m) {
					x1 += ai[0];
					z1 += ai[1];

					for (int y11 = -viewRadius; y11 <= viewRadius; ++y11) {
						this.getPlayerInstance(xCh + x1, yCh + y11, zCh + z1, true).addPlayer(entityplayermp);
					}
				}
			}
		}

		count %= 4;

		for (l1 = 0; l1 < viewRadius * 2; ++l1) {
			x1 += this.field_22089_e[count][0];
			z1 += this.field_22089_e[count][1];

			for (y1 = -viewRadius; y1 < viewRadius; ++y1) {
				this.getPlayerInstance(xCh + x1, yCh + y1, zCh + z1, true).addPlayer(entityplayermp);
			}
		}

		this.players.add(entityplayermp);
	}

	public void removePlayer(EntityPlayerMP entityplayermp) {
		int x = (int) entityplayermp.lastUpdatedX >> 4;
		int y = (int) entityplayermp.lastUpdatedY >> 4;
		int z = (int) entityplayermp.lastUpdatedZ >> 4;

		for (int x1 = x - this.playerViewRadius; x1 <= x + this.playerViewRadius; ++x1) {
			for (int y1 = y - this.playerViewRadius; y1 <= y + this.playerViewRadius; ++y1) {
				for (int z1 = z - this.playerViewRadius; z1 <= z + this.playerViewRadius; ++z1) {
					PlayerInstance player = this.getPlayerInstance(x1, y1, z1, false);
					if (player != null) {
						player.removePlayer(entityplayermp);
					}
				}
			}
		}

		this.players.remove(entityplayermp);
	}

	private boolean func_544_a(int x1, int y1, int z1, int x2, int y2, int z2) {
		int x3 = x1 - x2;
		int y3 = y1 - y2;
		int z3 = z1 - z2;
		return x3 >= -this.playerViewRadius && x3 <= this.playerViewRadius
				? (y3 >= -this.playerViewRadius && y3 <= this.playerViewRadius
						? z3 >= -this.playerViewRadius && z3 <= this.playerViewRadius
						: false)
				: false;
	}

	public void func_543_c(EntityPlayerMP entityplayermp) {
		int xCh = (int) entityplayermp.posX >> 4;
		int yCh = (int) entityplayermp.posY >> 4;
		int zCh = (int) entityplayermp.posZ >> 4;
		double distX = entityplayermp.lastUpdatedX - entityplayermp.posX;
		double distZ = entityplayermp.lastUpdatedZ - entityplayermp.posZ;
		double distSq = distX * distX + distZ * distZ;
		if (distSq >= 64.0D) {
			int lastUpdatedXCh = (int) entityplayermp.lastUpdatedX >> 4;
			int lastUpdatedYCh = (int) entityplayermp.lastUpdatedY >> 4;
			int lastUpdatedZCh = (int) entityplayermp.lastUpdatedZ >> 4;
			int movedXCh = xCh - lastUpdatedXCh;
			int movedYCh = yCh - lastUpdatedYCh;
			int movedZCh = zCh - lastUpdatedZCh;
			if (movedXCh != 0 || movedYCh != 0 || movedZCh != 0) {
				for (int x4 = xCh - this.playerViewRadius; x4 <= xCh + this.playerViewRadius; ++x4) {
					for (int y4 = yCh - this.playerViewRadius; y4 <= yCh + this.playerViewRadius; ++y4) {
						for (int z4 = zCh - this.playerViewRadius; z4 <= zCh + this.playerViewRadius; ++z4) {
							if (!this.func_544_a(x4, y4, z4, lastUpdatedXCh, lastUpdatedYCh, lastUpdatedZCh)) {
								this.getPlayerInstance(x4, y4, z4, true).addPlayer(entityplayermp);
							}

							if (!this.func_544_a(x4 - movedXCh, y4 - movedYCh, z4 - movedZCh, xCh, yCh, zCh)) {
								PlayerInstance player = this.getPlayerInstance(x4 - movedXCh, y4 - movedYCh,
										z4 - movedZCh, false);
								if (player != null) {
									player.removePlayer(entityplayermp);
								}
							}
						}
					}
				}

				entityplayermp.lastUpdatedX = entityplayermp.posX;
				entityplayermp.lastUpdatedY = entityplayermp.posY;
				entityplayermp.lastUpdatedZ = entityplayermp.posZ;
			}
		}
	}

	public int getMaxTrackingDistance() {
		return this.playerViewRadius * 16 - 16;
	}

	static PlayerHash getPlayerInstances(PlayerManager playermanager) {
		return playermanager.playerInstances;
	}

	static List getPlayerInstancesToUpdate(PlayerManager playermanager) {
		return playermanager.playerInstancesToUpdate;
	}
}
