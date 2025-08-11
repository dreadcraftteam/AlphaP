package net.minecraft_server.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.minecraft_server.server.MinecraftServer;

public class ServerConfigurationManager {
	public static Logger logger = Logger.getLogger("Minecraft");
	public List playerEntities = new ArrayList();
	private MinecraftServer mcServer;
	private PlayerManager[] playerManagerObj = new PlayerManager[2];
	private int maxPlayers;
	private Set bannedPlayers = new HashSet();
	private Set bannedIPs = new HashSet();
	private Set ops = new HashSet();
	private Set whiteListedIPs = new HashSet();
	private File bannedPlayersFile;
	private File ipBanFile;
	private File opFile;
	private File whitelistPlayersFile;
	private IPlayerFileData playerNBTManagerObj;
	private boolean whiteListEnforced;

	public ServerConfigurationManager(MinecraftServer minecraftserver) {
		this.mcServer = minecraftserver;
		this.bannedPlayersFile = minecraftserver.getFile("banned-players.txt");
		this.ipBanFile = minecraftserver.getFile("banned-ips.txt");
		this.opFile = minecraftserver.getFile("ops.txt");
		this.whitelistPlayersFile = minecraftserver.getFile("white-list.txt");
		int i = minecraftserver.propertyManagerObj.getIntProperty("view-distance", 10);
		this.playerManagerObj[0] = new PlayerManager(minecraftserver, 0, i);
		this.playerManagerObj[1] = new PlayerManager(minecraftserver, -1, i);
		this.maxPlayers = minecraftserver.propertyManagerObj.getIntProperty("max-players", 20);
		this.whiteListEnforced = minecraftserver.propertyManagerObj.getBooleanProperty("white-list", false);
		this.readBannedPlayers();
		this.loadBannedList();
		this.loadOps();
		this.loadWhiteList();
		this.writeBannedPlayers();
		this.saveBannedList();
		this.saveOps();
		this.saveWhiteList();
	}

	public void setPlayerManager(WorldServer[] aworldserver) {
		this.playerNBTManagerObj = aworldserver[0].getWorldFile().func_22090_d();
	}

	public void func_28172_a(EntityPlayerMP entityplayermp) {
		this.playerManagerObj[0].removePlayer(entityplayermp);
		this.playerManagerObj[1].removePlayer(entityplayermp);
		this.getPlayerManager(entityplayermp.dimension).addPlayer(entityplayermp);
		WorldServer worldserver = this.mcServer.getWorldManager(entityplayermp.dimension);
		worldserver.chunkProviderServer.loadChunk((int) entityplayermp.posX >> 4, (int) entityplayermp.posZ >> 4);
	}

	public int getMaxTrackingDistance() {
		return this.playerManagerObj[0].getMaxTrackingDistance();
	}

	private PlayerManager getPlayerManager(int i) {
		return i != -1 ? this.playerManagerObj[0] : this.playerManagerObj[1];
	}

	public void readPlayerDataFromFile(EntityPlayerMP entityplayermp) {
		this.playerNBTManagerObj.readPlayerData(entityplayermp);
	}

	public void playerLoggedIn(EntityPlayerMP entityplayermp) {
		this.playerEntities.add(entityplayermp);
		WorldServer worldserver = this.mcServer.getWorldManager(entityplayermp.dimension);
		worldserver.chunkProviderServer.loadChunk((int) entityplayermp.posX >> 4, (int) entityplayermp.posZ >> 4);

		while (worldserver.getCollidingBoundingBoxes(entityplayermp, entityplayermp.boundingBox).size() != 0) {
			entityplayermp.setPosition(entityplayermp.posX, entityplayermp.posY + 1.0D, entityplayermp.posZ);
		}

		worldserver.entityJoinedWorld(entityplayermp);
		this.getPlayerManager(entityplayermp.dimension).addPlayer(entityplayermp);
	}

	public void func_613_b(EntityPlayerMP entityplayermp) {
		this.getPlayerManager(entityplayermp.dimension).func_543_c(entityplayermp);
	}

	public void playerLoggedOut(EntityPlayerMP entityplayermp) {
		this.playerNBTManagerObj.writePlayerData(entityplayermp);
		this.mcServer.getWorldManager(entityplayermp.dimension).removePlayerForLogoff(entityplayermp);
		this.playerEntities.remove(entityplayermp);
		this.getPlayerManager(entityplayermp.dimension).removePlayer(entityplayermp);
	}

	public EntityPlayerMP login(NetLoginHandler netloginhandler, String s) {
		if (this.bannedPlayers.contains(s.trim().toLowerCase())) {
			netloginhandler.kickUser("You are banned from this server!");
			return null;
		} else if (!this.isAllowedToLogin(s)) {
			netloginhandler.kickUser("You are not white-listed on this server!");
			return null;
		} else {
			String s1 = netloginhandler.netManager.getRemoteAddress().toString();
			s1 = s1.substring(s1.indexOf("/") + 1);
			s1 = s1.substring(0, s1.indexOf(":"));
			if (this.bannedIPs.contains(s1)) {
				netloginhandler.kickUser("Your IP address is banned from this server!");
				return null;
			} else if (this.playerEntities.size() >= this.maxPlayers) {
				netloginhandler.kickUser("The server is full!");
				return null;
			} else {
				for (int i = 0; i < this.playerEntities.size(); ++i) {
					EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntities.get(i);
					if (entityplayermp.username.equalsIgnoreCase(s)) {
						entityplayermp.playerNetServerHandler.kickPlayer("You logged in from another location");
					}
				}

				return new EntityPlayerMP(this.mcServer, this.mcServer.getWorldManager(0), s,
						new ItemInWorldManager(this.mcServer.getWorldManager(0)));
			}
		}
	}

	public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP entityplayermp, int i) {
		this.mcServer.getEntityTracker(entityplayermp.dimension).removeTrackedPlayerSymmetric(entityplayermp);
		this.mcServer.getEntityTracker(entityplayermp.dimension).untrackEntity(entityplayermp);
		this.getPlayerManager(entityplayermp.dimension).removePlayer(entityplayermp);
		this.playerEntities.remove(entityplayermp);
		this.mcServer.getWorldManager(entityplayermp.dimension).removePlayer(entityplayermp);
		ChunkCoordinates chunkcoordinates = entityplayermp.getSpawnChunk();
		entityplayermp.dimension = i;
		EntityPlayerMP entityplayermp1 = new EntityPlayerMP(this.mcServer,
				this.mcServer.getWorldManager(entityplayermp.dimension), entityplayermp.username,
				new ItemInWorldManager(this.mcServer.getWorldManager(entityplayermp.dimension)));
		entityplayermp1.entityId = entityplayermp.entityId;
		entityplayermp1.playerNetServerHandler = entityplayermp.playerNetServerHandler;
		WorldServer worldserver = this.mcServer.getWorldManager(entityplayermp.dimension);
		if (chunkcoordinates != null) {
			ChunkCoordinates chunkcoordinates1 = EntityPlayer
					.func_25051_a(this.mcServer.getWorldManager(entityplayermp.dimension), chunkcoordinates);
			if (chunkcoordinates1 != null) {
				entityplayermp1.setLocationAndAngles((double) ((float) chunkcoordinates1.posX + 0.5F),
						(double) ((float) chunkcoordinates1.posY + 0.1F),
						(double) ((float) chunkcoordinates1.posZ + 0.5F), 0.0F, 0.0F);
				entityplayermp1.setSpawnChunk(chunkcoordinates);
			} else {
				entityplayermp1.playerNetServerHandler.sendPacket(new Packet70Bed(0));
			}
		}

		worldserver.chunkProviderServer.loadCube((int) entityplayermp1.posX >> 4, (int) entityplayermp1.posY >> 4,
				(int) entityplayermp1.posZ >> 4);

		while (worldserver.getCollidingBoundingBoxes(entityplayermp1, entityplayermp1.boundingBox).size() != 0) {
			entityplayermp1.setPosition(entityplayermp1.posX, entityplayermp1.posY + 1.0D, entityplayermp1.posZ);
		}

		entityplayermp1.playerNetServerHandler.sendPacket(new Packet9Respawn((byte) entityplayermp1.dimension));
		entityplayermp1.playerNetServerHandler.teleportTo(entityplayermp1.posX, entityplayermp1.posY,
				entityplayermp1.posZ, entityplayermp1.rotationYaw, entityplayermp1.rotationPitch);
		this.func_28170_a(entityplayermp1, worldserver);
		this.getPlayerManager(entityplayermp1.dimension).addPlayer(entityplayermp1);
		worldserver.entityJoinedWorld(entityplayermp1);
		this.playerEntities.add(entityplayermp1);
		entityplayermp1.func_20057_k();
		entityplayermp1.func_22068_s();
		return entityplayermp1;
	}

	public void sendPlayerToOtherDimension(EntityPlayerMP entityplayermp) {
		WorldServer worldserver = this.mcServer.getWorldManager(entityplayermp.dimension);
		boolean i = false;
		byte i1;
		if (entityplayermp.dimension == -1) {
			i1 = 0;
		} else {
			i1 = -1;
		}

		entityplayermp.dimension = i1;
		WorldServer worldserver1 = this.mcServer.getWorldManager(entityplayermp.dimension);
		entityplayermp.playerNetServerHandler.sendPacket(new Packet9Respawn((byte) entityplayermp.dimension));
		worldserver.removePlayer(entityplayermp);
		entityplayermp.isDead = false;
		double d = entityplayermp.posX;
		double d1 = entityplayermp.posZ;
		double d2 = 8.0D;
		if (entityplayermp.dimension == -1) {
			d /= d2;
			d1 /= d2;
			entityplayermp.setLocationAndAngles(d, entityplayermp.posY, d1, entityplayermp.rotationYaw,
					entityplayermp.rotationPitch);
			if (entityplayermp.isEntityAlive()) {
				worldserver.updateEntityWithOptionalForce(entityplayermp, false);
			}
		} else {
			d *= d2;
			d1 *= d2;
			entityplayermp.setLocationAndAngles(d, entityplayermp.posY, d1, entityplayermp.rotationYaw,
					entityplayermp.rotationPitch);
			if (entityplayermp.isEntityAlive()) {
				worldserver.updateEntityWithOptionalForce(entityplayermp, false);
			}
		}

		if (entityplayermp.isEntityAlive()) {
			worldserver1.entityJoinedWorld(entityplayermp);
			entityplayermp.setLocationAndAngles(d, entityplayermp.posY, d1, entityplayermp.rotationYaw,
					entityplayermp.rotationPitch);
			worldserver1.updateEntityWithOptionalForce(entityplayermp, false);
			worldserver1.chunkProviderServer.chunkLoadOverride = true;
			(new Teleporter()).setExitLocation(worldserver1, entityplayermp);
			worldserver1.chunkProviderServer.chunkLoadOverride = false;
		}

		this.func_28172_a(entityplayermp);
		entityplayermp.playerNetServerHandler.teleportTo(entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ,
				entityplayermp.rotationYaw, entityplayermp.rotationPitch);
		entityplayermp.setWorldHandler(worldserver1);
		this.func_28170_a(entityplayermp, worldserver1);
		this.func_30008_g(entityplayermp);
	}

	public void onTick() {
		for (int i = 0; i < this.playerManagerObj.length; ++i) {
			this.playerManagerObj[i].updatePlayerInstances();
		}

	}

	public void markBlockNeedsUpdate(int i, int j, int k, int l) {
		this.getPlayerManager(l).markBlockNeedsUpdate(i, j, k);
	}

	public void sendPacketToAllPlayers(Packet packet) {
		for (int i = 0; i < this.playerEntities.size(); ++i) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntities.get(i);
			entityplayermp.playerNetServerHandler.sendPacket(packet);
		}

	}

	public void sendPacketToAllPlayersInDimension(Packet packet, int i) {
		for (int j = 0; j < this.playerEntities.size(); ++j) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntities.get(j);
			if (entityplayermp.dimension == i) {
				entityplayermp.playerNetServerHandler.sendPacket(packet);
			}
		}

	}

	public String getPlayerList() {
		String s = "";

		for (int i = 0; i < this.playerEntities.size(); ++i) {
			if (i > 0) {
				s = s + ", ";
			}

			s = s + ((EntityPlayerMP) this.playerEntities.get(i)).username;
		}

		return s;
	}

	public void banPlayer(String s) {
		this.bannedPlayers.add(s.toLowerCase());
		this.writeBannedPlayers();
	}

	public void pardonPlayer(String s) {
		this.bannedPlayers.remove(s.toLowerCase());
		this.writeBannedPlayers();
	}

	private void readBannedPlayers() {
		try {
			this.bannedPlayers.clear();
			BufferedReader exception = new BufferedReader(new FileReader(this.bannedPlayersFile));
			String s = "";

			while ((s = exception.readLine()) != null) {
				this.bannedPlayers.add(s.trim().toLowerCase());
			}

			exception.close();
		} catch (Exception exception3) {
			logger.warning("Failed to load ban list: " + exception3);
		}

	}

	private void writeBannedPlayers() {
		try {
			PrintWriter exception = new PrintWriter(new FileWriter(this.bannedPlayersFile, false));
			Iterator iterator = this.bannedPlayers.iterator();

			while (iterator.hasNext()) {
				String s = (String) iterator.next();
				exception.println(s);
			}

			exception.close();
		} catch (Exception exception4) {
			logger.warning("Failed to save ban list: " + exception4);
		}

	}

	public void banIP(String s) {
		this.bannedIPs.add(s.toLowerCase());
		this.saveBannedList();
	}

	public void pardonIP(String s) {
		this.bannedIPs.remove(s.toLowerCase());
		this.saveBannedList();
	}

	private void loadBannedList() {
		try {
			this.bannedIPs.clear();
			BufferedReader exception = new BufferedReader(new FileReader(this.ipBanFile));
			String s = "";

			while ((s = exception.readLine()) != null) {
				this.bannedIPs.add(s.trim().toLowerCase());
			}

			exception.close();
		} catch (Exception exception3) {
			logger.warning("Failed to load ip ban list: " + exception3);
		}

	}

	private void saveBannedList() {
		try {
			PrintWriter exception = new PrintWriter(new FileWriter(this.ipBanFile, false));
			Iterator iterator = this.bannedIPs.iterator();

			while (iterator.hasNext()) {
				String s = (String) iterator.next();
				exception.println(s);
			}

			exception.close();
		} catch (Exception exception4) {
			logger.warning("Failed to save ip ban list: " + exception4);
		}

	}

	public void opPlayer(String s) {
		this.ops.add(s.toLowerCase());
		this.saveOps();
	}

	public void deopPlayer(String s) {
		this.ops.remove(s.toLowerCase());
		this.saveOps();
	}

	private void loadOps() {
		try {
			this.ops.clear();
			BufferedReader exception = new BufferedReader(new FileReader(this.opFile));
			String s = "";

			while ((s = exception.readLine()) != null) {
				this.ops.add(s.trim().toLowerCase());
			}

			exception.close();
		} catch (Exception exception3) {
			logger.warning("Failed to load ip ban list: " + exception3);
		}

	}

	private void saveOps() {
		try {
			PrintWriter exception = new PrintWriter(new FileWriter(this.opFile, false));
			Iterator iterator = this.ops.iterator();

			while (iterator.hasNext()) {
				String s = (String) iterator.next();
				exception.println(s);
			}

			exception.close();
		} catch (Exception exception4) {
			logger.warning("Failed to save ip ban list: " + exception4);
		}

	}

	private void loadWhiteList() {
		try {
			this.whiteListedIPs.clear();
			BufferedReader exception = new BufferedReader(new FileReader(this.whitelistPlayersFile));
			String s = "";

			while ((s = exception.readLine()) != null) {
				this.whiteListedIPs.add(s.trim().toLowerCase());
			}

			exception.close();
		} catch (Exception exception3) {
			logger.warning("Failed to load white-list: " + exception3);
		}

	}

	private void saveWhiteList() {
		try {
			PrintWriter exception = new PrintWriter(new FileWriter(this.whitelistPlayersFile, false));
			Iterator iterator = this.whiteListedIPs.iterator();

			while (iterator.hasNext()) {
				String s = (String) iterator.next();
				exception.println(s);
			}

			exception.close();
		} catch (Exception exception4) {
			logger.warning("Failed to save white-list: " + exception4);
		}

	}

	public boolean isAllowedToLogin(String s) {
		s = s.trim().toLowerCase();
		return !this.whiteListEnforced || this.ops.contains(s) || this.whiteListedIPs.contains(s);
	}

	public boolean isOp(String s) {
		return this.ops.contains(s.trim().toLowerCase());
	}

	public EntityPlayerMP getPlayerEntity(String s) {
		for (int i = 0; i < this.playerEntities.size(); ++i) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntities.get(i);
			if (entityplayermp.username.equalsIgnoreCase(s)) {
				return entityplayermp;
			}
		}

		return null;
	}

	public void sendChatMessageToPlayer(String s, String s1) {
		EntityPlayerMP entityplayermp = this.getPlayerEntity(s);
		if (entityplayermp != null) {
			entityplayermp.playerNetServerHandler.sendPacket(new Packet3Chat(s1));
		}

	}

	public void sendPacketToPlayersAroundPoint(double d, double d1, double d2, double d3, int i, Packet packet) {
		this.func_28171_a((EntityPlayer) null, d, d1, d2, d3, i, packet);
	}

	public void func_28171_a(EntityPlayer entityplayer, double d, double d1, double d2, double d3, int i,
			Packet packet) {
		for (int j = 0; j < this.playerEntities.size(); ++j) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntities.get(j);
			if (entityplayermp != entityplayer && entityplayermp.dimension == i) {
				double d4 = d - entityplayermp.posX;
				double d5 = d1 - entityplayermp.posY;
				double d6 = d2 - entityplayermp.posZ;
				if (d4 * d4 + d5 * d5 + d6 * d6 < d3 * d3) {
					entityplayermp.playerNetServerHandler.sendPacket(packet);
				}
			}
		}

	}

	public void sendChatMessageToAllOps(String s) {
		Packet3Chat packet3chat = new Packet3Chat(s);

		for (int i = 0; i < this.playerEntities.size(); ++i) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntities.get(i);
			if (this.isOp(entityplayermp.username)) {
				entityplayermp.playerNetServerHandler.sendPacket(packet3chat);
			}
		}

	}

	public boolean sendPacketToPlayer(String s, Packet packet) {
		EntityPlayerMP entityplayermp = this.getPlayerEntity(s);
		if (entityplayermp != null) {
			entityplayermp.playerNetServerHandler.sendPacket(packet);
			return true;
		} else {
			return false;
		}
	}

	public void savePlayerStates() {
		for (int i = 0; i < this.playerEntities.size(); ++i) {
			this.playerNBTManagerObj.writePlayerData((EntityPlayer) this.playerEntities.get(i));
		}

	}

	public void sentTileEntityToPlayer(int i, int j, int k, TileEntity tileentity) {
	}

	public void addToWhiteList(String s) {
		this.whiteListedIPs.add(s);
		this.saveWhiteList();
	}

	public void removeFromWhiteList(String s) {
		this.whiteListedIPs.remove(s);
		this.saveWhiteList();
	}

	public Set getWhiteListedIPs() {
		return this.whiteListedIPs;
	}

	public void reloadWhiteList() {
		this.loadWhiteList();
	}

	public void func_28170_a(EntityPlayerMP entityplayermp, WorldServer worldserver) {
		entityplayermp.playerNetServerHandler.sendPacket(new Packet4UpdateTime(worldserver.getWorldTime()));
		if (worldserver.func_27068_v()) {
			entityplayermp.playerNetServerHandler.sendPacket(new Packet70Bed(1));
		}

	}

	public void func_30008_g(EntityPlayerMP entityplayermp) {
		entityplayermp.func_28017_a(entityplayermp.personalCraftingInventory);
		entityplayermp.func_30001_B();
	}
}
