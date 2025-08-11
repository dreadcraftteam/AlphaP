package net.minecraft.src;

import java.io.File;

public class RegionFileSmall extends RegionFile {
	private static final int SECTOR_BYTES = 256;
	private static final byte[] emptySector = new byte[256];

	public RegionFileSmall(File path) {
		super(path);
	}

	protected byte[] getEmptySector() {
		return emptySector;
	}

	public int SectorBytes() {
		return 256;
	}
}
