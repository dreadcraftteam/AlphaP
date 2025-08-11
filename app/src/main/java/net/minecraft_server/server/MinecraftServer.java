package net.minecraft_server.server;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft_server.src.AxisAlignedBB;
import net.minecraft_server.src.ChunkCoordinates;
import net.minecraft_server.src.ConsoleCommandHandler;
import net.minecraft_server.src.ConsoleLogManager;
import net.minecraft_server.src.ConvertProgressUpdater;
import net.minecraft_server.src.EntityTracker;
import net.minecraft_server.src.ICommandListener;
import net.minecraft_server.src.IProgressUpdate;
import net.minecraft_server.src.ISaveFormat;
import net.minecraft_server.src.IUpdatePlayerListBox;
import net.minecraft_server.src.NetworkListenThread;
import net.minecraft_server.src.Packet4UpdateTime;
import net.minecraft_server.src.PropertyManager;
import net.minecraft_server.src.SaveConverterMcRegion;
import net.minecraft_server.src.SaveOldDir;
import net.minecraft_server.src.ServerCommand;
import net.minecraft_server.src.ServerConfigurationManager;
import net.minecraft_server.src.ServerGUI;
import net.minecraft_server.src.ThreadCommandReader;
import net.minecraft_server.src.ThreadServerApplication;
import net.minecraft_server.src.ThreadSleepForever;
import net.minecraft_server.src.Vec3D;
import net.minecraft_server.src.WorldManager;
import net.minecraft_server.src.WorldServer;
import net.minecraft_server.src.WorldServerMulti;

public class MinecraftServer implements Runnable, ICommandListener {
	public static Logger logger = Logger.getLogger("Minecraft");
	public static HashMap field_6037_b = new HashMap();
	public NetworkListenThread networkServer;
	public PropertyManager propertyManagerObj;
	public WorldServer[] worldMngr;
	public ServerConfigurationManager configManager;
	private ConsoleCommandHandler commandHandler;
	private boolean serverRunning = true;
	public boolean serverStopped = false;
	int deathTime = 0;
	public String currentTask;
	public int percentDone;
	private List field_9010_p = new ArrayList();
	private List commands = Collections.synchronizedList(new ArrayList());
	public EntityTracker[] entityTracker = new EntityTracker[2];
	public boolean onlineMode;
	public boolean spawnPeacefulMobs;
	public boolean pvpOn;
	public boolean allowFlight;

	public MinecraftServer() {
		new ThreadSleepForever(this);
	}

	private boolean startServer() throws UnknownHostException {
		this.commandHandler = new ConsoleCommandHandler(this);
		ThreadCommandReader threadcommandreader = new ThreadCommandReader(this);
		threadcommandreader.setDaemon(true);
		threadcommandreader.start();
		ConsoleLogManager.init();
		logger.info("Starting minecraft server version Beta 1.7.3 - Cubic Chunks mod");
		if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
			logger.warning("**** NOT ENOUGH RAM!");
			logger.warning(
					"To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
		}

		logger.info("Loading properties");
		this.propertyManagerObj = new PropertyManager(new File("server.properties"));
		String s = this.propertyManagerObj.getStringProperty("server-ip", "");
		this.onlineMode = this.propertyManagerObj.getBooleanProperty("online-mode", true);
		this.spawnPeacefulMobs = this.propertyManagerObj.getBooleanProperty("spawn-animals", true);
		this.pvpOn = this.propertyManagerObj.getBooleanProperty("pvp", true);
		this.allowFlight = this.propertyManagerObj.getBooleanProperty("allow-flight", false);
		InetAddress inetaddress = null;
		if (s.length() > 0) {
			inetaddress = InetAddress.getByName(s);
		}

		int i = this.propertyManagerObj.getIntProperty("server-port", 25565);
		logger.info("Starting Minecraft server on " + (s.length() != 0 ? s : "*") + ":" + i);

		try {
			this.networkServer = new NetworkListenThread(this, inetaddress, i);
		} catch (IOException iOException13) {
			logger.warning("**** FAILED TO BIND TO PORT!");
			logger.log(Level.WARNING, "The exception was: " + iOException13.toString());
			logger.warning("Perhaps a server is already running on that port?");
			return false;
		}

		if (!this.onlineMode) {
			logger.warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
			logger.warning("The server will make no attempt to authenticate usernames. Beware.");
			logger.warning(
					"While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
			logger.warning("To change this, set \"online-mode\" to \"true\" in the server.settings file.");
		}

		this.configManager = new ServerConfigurationManager(this);
		this.entityTracker[0] = new EntityTracker(this, 0);
		this.entityTracker[1] = new EntityTracker(this, -1);
		long l = System.nanoTime();
		String s1 = this.propertyManagerObj.getStringProperty("level-name", "world");
		String s2 = this.propertyManagerObj.getStringProperty("level-seed", "");
		long l1 = (new Random()).nextLong();
		if (s2.length() > 0) {
			try {
				l1 = Long.parseLong(s2);
			} catch (NumberFormatException numberFormatException12) {
				l1 = (long) s2.hashCode();
			}
		}

		logger.info("Preparing level \"" + s1 + "\"");
		this.initWorld(new SaveConverterMcRegion(new File(".")), s1, l1);
		logger.info("Done (" + (System.nanoTime() - l) + "ns)! For help, type \"help\" or \"?\"");
		return true;
	}

	private void initWorld(ISaveFormat isaveformat, String s, long l) {
		if (isaveformat.isOldSaveType(s)) {
			logger.info("Converting map!");
			isaveformat.converMapToMCRegion(s, new ConvertProgressUpdater(this));
		}

		this.worldMngr = new WorldServer[2];
		SaveOldDir saveolddir = new SaveOldDir(new File("."), s, true);

		for (int range = 0; range < this.worldMngr.length; ++range) {
			if (range == 0) {
				this.worldMngr[range] = new WorldServer(this, saveolddir, s, range != 0 ? -1 : 0, l);
			} else {
				this.worldMngr[range] = new WorldServerMulti(this, saveolddir, s, range != 0 ? -1 : 0, l,
						this.worldMngr[0]);
			}

			this.worldMngr[range].addWorldAccess(new WorldManager(this, this.worldMngr[range]));
			this.worldMngr[range].difficultySetting = this.propertyManagerObj.getBooleanProperty("spawn-monsters", true)
					? 1
					: 0;
			this.worldMngr[range].setAllowedSpawnTypes(
					this.propertyManagerObj.getBooleanProperty("spawn-monsters", true), this.spawnPeacefulMobs);
			this.configManager.setPlayerManager(this.worldMngr);
		}

		short s19 = 196;
		int yRange = s19 >> 1;
		long l1 = System.currentTimeMillis();

		for (int j = 0; j < this.worldMngr.length; ++j) {
			logger.info("Preparing start region for level " + j);
			if (j == 0 || this.propertyManagerObj.getBooleanProperty("allow-nether", true)) {
				WorldServer worldserver = this.worldMngr[j];
				ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();

				for (int x1 = -s19; x1 <= s19 && this.serverRunning; x1 += 16) {
					for (int z1 = -s19; z1 <= s19 && this.serverRunning; z1 += 16) {
						long l2 = System.currentTimeMillis();
						if (l2 < l1) {
							l1 = l2;
						}

						int y1;
						if (l2 > l1 + 2000L) {
							y1 = (s19 * 2 + 1) * (s19 * 2 + 1);
							int k1 = (x1 + s19) * (s19 * 2 + 1) + z1 + 1;
							this.outputPercentRemaining("Preparing spawn area", k1 * 100 / y1);
							l1 = l2;
						}

						for (y1 = -yRange; y1 <= yRange; y1 += 16) {
							worldserver.chunkProviderServer.loadCube(chunkcoordinates.posX + x1 >> 4,
									chunkcoordinates.posY + y1 >> 4, chunkcoordinates.posZ + z1 >> 4);
						}

						while (worldserver.func_6156_d() && this.serverRunning) {
						}
					}
				}
			}
		}

		this.clearCurrentTask();
	}

	private void outputPercentRemaining(String s, int i) {
		this.currentTask = s;
		this.percentDone = i;
		logger.info(s + ": " + i + "%");
	}

	private void clearCurrentTask() {
		this.currentTask = null;
		this.percentDone = 0;
	}

	private void saveServerWorld() {
		logger.info("Saving chunks");

		for (int i = 0; i < this.worldMngr.length; ++i) {
			WorldServer worldserver = this.worldMngr[i];
			worldserver.saveWorld(true, (IProgressUpdate) null);
			worldserver.func_30006_w();
		}

	}

	private void stopServer() {
		logger.info("Stopping server");
		if (this.configManager != null) {
			this.configManager.savePlayerStates();
		}

		for (int i = 0; i < this.worldMngr.length; ++i) {
			WorldServer worldserver = this.worldMngr[i];
			if (worldserver != null) {
				this.saveServerWorld();
			}
		}

	}

	public void initiateShutdown() {
		this.serverRunning = false;
	}

	public void run() {
		try {
			if (this.startServer()) {
				long throwable2 = System.currentTimeMillis();

				for (long l1 = 0L; this.serverRunning; Thread.sleep(1L)) {
					long l2 = System.currentTimeMillis();
					long l3 = l2 - throwable2;
					if (l3 > 2000L) {
						logger.warning("Can\'t keep up! Did the system time change, or is the server overloaded?");
						l3 = 2000L;
					}

					if (l3 < 0L) {
						logger.warning("Time ran backwards! Did the system time change?");
						l3 = 0L;
					}

					l1 += l3;
					throwable2 = l2;
					if (this.worldMngr[0].isAllPlayersFullyAsleep()) {
						this.doTick();
						l1 = 0L;
					} else {
						while (l1 > 50L) {
							l1 -= 50L;
							this.doTick();
						}
					}
				}
			} else {
				while (this.serverRunning) {
					this.commandLineParser();

					try {
						Thread.sleep(10L);
					} catch (InterruptedException interruptedException57) {
						interruptedException57.printStackTrace();
					}
				}
			}
		} catch (Throwable throwable58) {
			throwable58.printStackTrace();
			logger.log(Level.SEVERE, "Unexpected exception", throwable58);

			while (this.serverRunning) {
				this.commandLineParser();

				try {
					Thread.sleep(10L);
				} catch (InterruptedException interruptedException56) {
					interruptedException56.printStackTrace();
				}
			}
		} finally {
			try {
				this.stopServer();
				this.serverStopped = true;
			} catch (Throwable throwable54) {
				throwable54.printStackTrace();
			} finally {
				System.exit(0);
			}

		}

	}

	private void doTick() {
		ArrayList arraylist = new ArrayList();
		Iterator exception = field_6037_b.keySet().iterator();

		while (exception.hasNext()) {
			String worldserver = (String) exception.next();
			int i1 = ((Integer) field_6037_b.get(worldserver)).intValue();
			if (i1 > 0) {
				field_6037_b.put(worldserver, i1 - 1);
			} else {
				arraylist.add(worldserver);
			}
		}

		int i6;
		for (i6 = 0; i6 < arraylist.size(); ++i6) {
			field_6037_b.remove(arraylist.get(i6));
		}

		AxisAlignedBB.clearBoundingBoxPool();
		Vec3D.initialize();
		++this.deathTime;

		for (i6 = 0; i6 < this.worldMngr.length; ++i6) {
			if (i6 == 0 || this.propertyManagerObj.getBooleanProperty("allow-nether", true)) {
				WorldServer worldServer7 = this.worldMngr[i6];
				if (this.deathTime % 20 == 0) {
					this.configManager.sendPacketToAllPlayersInDimension(
							new Packet4UpdateTime(worldServer7.getWorldTime()), worldServer7.worldProvider.worldType);
				}

				worldServer7.tick();

				while (worldServer7.func_6156_d()) {
				}

				worldServer7.updateEntities();
			}
		}

		this.networkServer.handleNetworkListenThread();
		this.configManager.onTick();

		for (i6 = 0; i6 < this.entityTracker.length; ++i6) {
			this.entityTracker[i6].updateTrackedEntities();
		}

		for (i6 = 0; i6 < this.field_9010_p.size(); ++i6) {
			((IUpdatePlayerListBox) this.field_9010_p.get(i6)).update();
		}

		try {
			this.commandLineParser();
		} catch (Exception exception5) {
			logger.log(Level.WARNING, "Unexpected exception while parsing console command", exception5);
		}

	}

	public void addCommand(String s, ICommandListener icommandlistener) {
		this.commands.add(new ServerCommand(s, icommandlistener));
	}

	public void commandLineParser() {
		while (this.commands.size() > 0) {
			ServerCommand servercommand = (ServerCommand) this.commands.remove(0);
			this.commandHandler.handleCommand(servercommand);
		}

	}

	public void func_6022_a(IUpdatePlayerListBox iupdateplayerlistbox) {
		this.field_9010_p.add(iupdateplayerlistbox);
	}

	public static void main(String[] args) {
		try {
			MinecraftServer exception = new MinecraftServer();
			if (!GraphicsEnvironment.isHeadless() && (args.length <= 0 || !args[0].equals("nogui"))) {
				ServerGUI.initGui(exception);
			}

			(new ThreadServerApplication("Server thread", exception)).start();
		} catch (Exception exception2) {
			logger.log(Level.SEVERE, "Failed to start the AlphaPlus server", exception2);
		}

	}

	public File getFile(String s) {
		return new File(s);
	}

	public void log(String s) {
		logger.info(s);
	}

	public void logWarning(String s) {
		logger.warning(s);
	}

	public String getUsername() {
		return "CONSOLE";
	}

	public WorldServer getWorldManager(int i) {
		return i == -1 ? this.worldMngr[1] : this.worldMngr[0];
	}

	public EntityTracker getEntityTracker(int i) {
		return i == -1 ? this.entityTracker[1] : this.entityTracker[0];
	}

	public static boolean isServerRunning(MinecraftServer minecraftserver) {
		return minecraftserver.serverRunning;
	}
}
