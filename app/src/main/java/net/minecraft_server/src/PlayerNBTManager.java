package net.minecraft_server.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class PlayerNBTManager implements IPlayerFileData, ISaveHandler {
	private static final Logger logger = Logger.getLogger("Minecraft");
	private final File worldDir;
	private final File worldFile;
	private final File field_28112_d;
	private final long field_22100_d = System.currentTimeMillis();

	public PlayerNBTManager(File file, String s, boolean flag) {
		this.worldDir = new File(file, s);
		this.worldDir.mkdirs();
		this.worldFile = new File(this.worldDir, "players");
		this.field_28112_d = new File(this.worldDir, "data");
		this.field_28112_d.mkdirs();
		if (flag) {
			this.worldFile.mkdirs();
		}

		this.func_22098_f();
	}

	private void func_22098_f() {
		try {
			File ioexception = new File(this.worldDir, "session.lock");
			DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(ioexception));

			try {
				dataoutputstream.writeLong(this.field_22100_d);
			} finally {
				dataoutputstream.close();
			}

		} catch (IOException iOException7) {
			iOException7.printStackTrace();
			throw new RuntimeException("Failed to check session lock, aborting");
		}
	}

	protected File getWorldDir() {
		return this.worldDir;
	}

	public void func_22091_b() {
		try {
			File ioexception = new File(this.worldDir, "session.lock");
			DataInputStream datainputstream = new DataInputStream(new FileInputStream(ioexception));

			try {
				if (datainputstream.readLong() != this.field_22100_d) {
					throw new MinecraftException("The save is being accessed from another location, aborting");
				}
			} finally {
				datainputstream.close();
			}

		} catch (IOException iOException7) {
			throw new MinecraftException("Failed to check session lock, aborting");
		}
	}

	public IChunkLoader func_22092_a(WorldProvider worldprovider) {
		if (worldprovider instanceof WorldProviderHell) {
			File file = new File(this.worldDir, "DIM-1");
			file.mkdirs();
			return new ChunkLoader(file, true);
		} else {
			return new ChunkLoader(this.worldDir, true);
		}
	}

	public WorldInfo func_22096_c() {
		File file = new File(this.worldDir, "level.dat");
		NBTTagCompound exception1;
		NBTTagCompound nbttagcompound3;
		if (file.exists()) {
			try {
				exception1 = CompressedStreamTools.func_770_a(new FileInputStream(file));
				nbttagcompound3 = exception1.getCompoundTag("Data");
				return new WorldInfo(nbttagcompound3);
			} catch (Exception exception5) {
				exception5.printStackTrace();
			}
		}

		file = new File(this.worldDir, "level.dat_old");
		if (file.exists()) {
			try {
				exception1 = CompressedStreamTools.func_770_a(new FileInputStream(file));
				nbttagcompound3 = exception1.getCompoundTag("Data");
				return new WorldInfo(nbttagcompound3);
			} catch (Exception exception4) {
				exception4.printStackTrace();
			}
		}

		return null;
	}

	public void saveWorldInfoAndPlayer(WorldInfo worldinfo, List list) {
		NBTTagCompound nbttagcompound = worldinfo.func_22183_a(list);
		NBTTagCompound nbttagcompound1 = new NBTTagCompound();
		nbttagcompound1.setTag("Data", nbttagcompound);

		try {
			File exception = new File(this.worldDir, "level.dat_new");
			File file1 = new File(this.worldDir, "level.dat_old");
			File file2 = new File(this.worldDir, "level.dat");
			CompressedStreamTools.writeGzippedCompoundToOutputStream(nbttagcompound1, new FileOutputStream(exception));
			if (file1.exists()) {
				file1.delete();
			}

			file2.renameTo(file1);
			if (file2.exists()) {
				file2.delete();
			}

			exception.renameTo(file2);
			if (exception.exists()) {
				exception.delete();
			}
		} catch (Exception exception8) {
			exception8.printStackTrace();
		}

	}

	public void func_22094_a(WorldInfo worldinfo) {
		NBTTagCompound nbttagcompound = worldinfo.func_22185_a();
		NBTTagCompound nbttagcompound1 = new NBTTagCompound();
		nbttagcompound1.setTag("Data", nbttagcompound);

		try {
			File exception = new File(this.worldDir, "level.dat_new");
			File file1 = new File(this.worldDir, "level.dat_old");
			File file2 = new File(this.worldDir, "level.dat");
			CompressedStreamTools.writeGzippedCompoundToOutputStream(nbttagcompound1, new FileOutputStream(exception));
			if (file1.exists()) {
				file1.delete();
			}

			file2.renameTo(file1);
			if (file2.exists()) {
				file2.delete();
			}

			exception.renameTo(file2);
			if (exception.exists()) {
				exception.delete();
			}
		} catch (Exception exception7) {
			exception7.printStackTrace();
		}

	}

	public void writePlayerData(EntityPlayer entityplayer) {
		try {
			NBTTagCompound exception = new NBTTagCompound();
			entityplayer.writeToNBT(exception);
			File file = new File(this.worldFile, "_tmp_.dat");
			File file1 = new File(this.worldFile, entityplayer.username + ".dat");
			CompressedStreamTools.writeGzippedCompoundToOutputStream(exception, new FileOutputStream(file));
			if (file1.exists()) {
				file1.delete();
			}

			file.renameTo(file1);
		} catch (Exception exception5) {
			logger.warning("Failed to save player data for " + entityplayer.username);
		}

	}

	public void readPlayerData(EntityPlayer entityplayer) {
		NBTTagCompound nbttagcompound = this.getPlayerData(entityplayer.username);
		if (nbttagcompound != null) {
			entityplayer.readFromNBT(nbttagcompound);
		}

	}

	public NBTTagCompound getPlayerData(String s) {
		try {
			File exception = new File(this.worldFile, s + ".dat");
			if (exception.exists()) {
				return CompressedStreamTools.func_770_a(new FileInputStream(exception));
			}
		} catch (Exception exception3) {
			logger.warning("Failed to load player data for " + s);
		}

		return null;
	}

	public IPlayerFileData func_22090_d() {
		return this;
	}

	public void func_22093_e() {
	}

	public File func_28111_b(String s) {
		return new File(this.field_28112_d, s + ".dat");
	}
}
