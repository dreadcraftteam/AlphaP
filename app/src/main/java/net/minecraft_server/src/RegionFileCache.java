package net.minecraft_server.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegionFileCache {
	private static final Map cache = new HashMap();

	public static synchronized RegionFile createOrLoadRegionFile(File file, int x, int y, int z) {
		return getFileHelper(file, (x >> 5) + "." + y + "." + (z >> 5) + ".mcr");
	}

	public static synchronized RegionFile createOrLoadRegionFile(File file, int i, int j) {
		return getFileHelper(file, (i >> 5) + "." + (j >> 5) + ".mcr");
	}

	private static synchronized RegionFile getFileHelper(File rootDir, String file2PartName) {
		String file2Name = "r2." + file2PartName;
		File regionDir = new File(rootDir, "region");
		File regionFile = new File(regionDir, file2Name);
		Reference reference = (Reference) cache.get(regionFile);
		if (reference != null) {
			RegionFile newFile = (RegionFile) reference.get();
			if (newFile != null) {
				return newFile;
			}
		}

		if (!regionDir.exists()) {
			regionDir.mkdirs();
		}

		if (cache.size() >= 256) {
			dumpChunkMapCache();
		}

		RegionFileSmall regionFileSmall28 = new RegionFileSmall(regionFile);
		cache.put(regionFile, new SoftReference(regionFileSmall28));
		String file2OldName = "r." + file2PartName;
		File file2Old = new File(regionDir, file2OldName);
		if (file2Old.exists()) {
			RegionFileLarge oldFile = new RegionFileLarge(file2Old);

			for (int e = 0; e < 32; ++e) {
				for (int z = 0; z < 32; ++z) {
					if (oldFile.hasChunk(e, z)) {
						DataInputStream inStream = oldFile.getChunkDataInputStream(e, z);
						if (inStream != null) {
							DataOutputStream outStream = regionFileSmall28.getChunkDataOutputStream(e, z);

							try {
								NBTTagCompound e1 = CompressedStreamTools.func_774_a(inStream);
								NBTTagCompound level = e1.getCompoundTag("Level");
								if (level.hasKey("HeightMap") && level.hasKey("yPos")) {
									level.setShortArray("HeightMap", new short[0]);
								}

								CompressedStreamTools.func_771_a(e1, outStream);
							} catch (Exception exception26) {
								exception26.printStackTrace();
							} finally {
								try {
									outStream.close();
									inStream.close();
								} catch (Exception exception24) {
									exception24.printStackTrace();
								}

							}
						}
					}
				}
			}

			try {
				oldFile.close();
			} catch (IOException iOException25) {
				iOException25.printStackTrace();
			}

			file2Old.delete();
		}

		return regionFileSmall28;
	}

	public static synchronized void dumpChunkMapCache() {
		Iterator iterator = cache.values().iterator();

		while (iterator.hasNext()) {
			Reference reference = (Reference) iterator.next();

			try {
				RegionFile ioexception = (RegionFile) reference.get();
				if (ioexception != null) {
					ioexception.close();
				}
			} catch (IOException iOException3) {
				iOException3.printStackTrace();
			}
		}

		cache.clear();
	}

	public static int getSizeDelta(File file, int i, int j) {
		RegionFile regionfile = createOrLoadRegionFile(file, i, j);
		return regionfile.getSizeDelta();
	}

	public static int getSizeDelta(File file, int x, int y, int z) {
		RegionFile regionfile = createOrLoadRegionFile(file, x, y, z);
		return regionfile.getSizeDelta();
	}

	public static DataInputStream getChunkInputStream(File file, int i, int j) {
		RegionFile regionfile = createOrLoadRegionFile(file, i, j);
		return regionfile.getChunkDataInputStream(i & 31, j & 31);
	}

	public static DataOutputStream getChunkOutputStream(File file, int i, int j) {
		RegionFile regionfile = createOrLoadRegionFile(file, i, j);
		return regionfile.getChunkDataOutputStream(i & 31, j & 31);
	}

	public static DataInputStream getCubeInputStream(File file, int x, int y, int z) {
		RegionFile regionfile = createOrLoadRegionFile(file, x, y, z);
		return regionfile.getChunkDataInputStream(x & 31, z & 31);
	}

	public static DataOutputStream getCubeOutputStream(File file, int x, int y, int z) {
		RegionFile regionfile = createOrLoadRegionFile(file, x, y, z);
		return regionfile.getChunkDataOutputStream(x & 31, z & 31);
	}
}
