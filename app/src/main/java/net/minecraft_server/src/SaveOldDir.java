package net.minecraft_server.src;

import java.io.File;
import java.util.List;

public class SaveOldDir extends PlayerNBTManager {
	public SaveOldDir(File file, String s, boolean flag) {
		super(file, s, flag);
	}

	public IChunkLoader func_22092_a(WorldProvider worldprovider) {
		File file = this.getWorldDir();
		if (worldprovider instanceof WorldProviderHell) {
			File file1 = new File(file, "DIM-1");
			file1.mkdirs();
			return new McRegionChunkLoader(file1);
		} else {
			return new McRegionChunkLoader(file);
		}
	}

	public void saveWorldInfoAndPlayer(WorldInfo worldinfo, List list) {
		worldinfo.setVersion(19132);
		super.saveWorldInfoAndPlayer(worldinfo, list);
	}

	public void func_22093_e() {
		RegionFileCache.dumpChunkMapCache();
	}
}
