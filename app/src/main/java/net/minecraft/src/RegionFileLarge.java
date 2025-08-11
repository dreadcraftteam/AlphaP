package net.minecraft.src;

import java.io.File;

public class RegionFileLarge extends RegionFile {
	private static final int SECTOR_BYTES = 4096;
	private static final byte[] emptySector = new byte[4096];

	public RegionFileLarge(File path) {
		super(path);
	}

	protected byte[] getEmptySector() {
		return emptySector;
	}

	public int SectorBytes() {
		return 4096;
	}
}
