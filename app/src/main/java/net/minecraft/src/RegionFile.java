package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public abstract class RegionFile {
	private static final int VERSION_GZIP = 1;
	private static final int VERSION_DEFLATE = 2;
	private static final int SECTOR_INTS = 1024;
	private static final int SECTOR_INDEX_BYTES = 8192;
	private static final int TIMESTAMP_OFFSET_BYTES = 4096;
	static final int CHUNK_HEADER_SIZE = 5;
	private final File fileName;
	private RandomAccessFile file;
	private final int[] offsets = new int[1024];
	private final int[] chunkTimestamps = new int[1024];
	private ArrayList sectorFree;
	private int sizeDelta;
	private long lastModified = 0L;

	protected abstract byte[] getEmptySector();

	public RegionFile(File path) {
		this.fileName = path;
		this.debugln("REGION LOAD " + this.fileName);
		this.sizeDelta = 0;

		try {
			if(path.exists()) {
				this.lastModified = path.lastModified();
			}

			this.file = new RandomAccessFile(path, "rw");
			int e;
			if(this.file.length() < 8192L) {
				for(e = 0; e < 1024; ++e) {
					this.file.writeInt(0);
				}

				for(e = 0; e < 1024; ++e) {
					this.file.writeInt(0);
				}

				this.sizeDelta += 8192;
			}

			int i;
			if(this.file.length() % (long)this.SectorBytes() != 0L) {
				long j8 = (long)this.SectorBytes() - this.file.length() % (long)this.SectorBytes();

				for(i = 0; (long)i < j8; ++i) {
					this.file.write(0);
				}
			}

			e = (int)this.file.length() / this.SectorBytes();
			this.sectorFree = new ArrayList(e);

			int temp;
			for(temp = 0; temp < e; ++temp) {
				this.sectorFree.add(true);
			}

			temp = 8192 / this.SectorBytes();

			for(i = 0; i < temp; ++i) {
				this.sectorFree.set(i, false);
			}

			this.file.seek(0L);

			int lastModValue;
			for(i = 0; i < 1024; ++i) {
				lastModValue = this.file.readInt();
				this.offsets[i] = lastModValue;
				if(lastModValue != 0 && (lastModValue >> 8) + (lastModValue & 255) <= this.sectorFree.size()) {
					for(int sectorNum = 0; sectorNum < (lastModValue & 255); ++sectorNum) {
						this.sectorFree.set((lastModValue >> 8) + sectorNum, false);
					}
				}
			}

			for(i = 0; i < 1024; ++i) {
				lastModValue = this.file.readInt();
				this.chunkTimestamps[i] = lastModValue;
			}
		} catch (IOException iOException7) {
			iOException7.printStackTrace();
		}

	}

	public long lastModified() {
		return this.lastModified;
	}

	public synchronized int getSizeDelta() {
		int ret = this.sizeDelta;
		this.sizeDelta = 0;
		return ret;
	}

	private void debug(String in) {
	}

	private void debugln(String in) {
		this.debug(in + "\n");
	}

	private void debug(String mode, int x, int z, String in) {
		this.debug("REGION " + mode + " " + this.fileName.getName() + "[" + x + "," + z + "] = " + in);
	}

	private void debug(String mode, int x, int z, int count, String in) {
		this.debug("REGION " + mode + " " + this.fileName.getName() + "[" + x + "," + z + "] " + count + "B = " + in);
	}

	private void debugln(String mode, int x, int z, String in) {
		this.debug(mode, x, z, in + "\n");
	}

	public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
		if(this.outOfBounds(x, z)) {
			this.debugln("READ", x, z, "out of bounds");
			return null;
		} else {
			try {
				int e = this.getOffset(x, z);
				if(e == 0) {
					return null;
				} else {
					int sectorNumber = e >> 8;
					int numSectors = e & 255;
					if(sectorNumber + numSectors > this.sectorFree.size()) {
						this.debugln("READ", x, z, "invalid sector");
						return null;
					} else {
						this.file.seek((long)(sectorNumber * this.SectorBytes()));
						int length = this.file.readInt();
						if(length > this.SectorBytes() * numSectors) {
							this.debugln("READ", x, z, "invalid length: " + length + " > " + this.SectorBytes() + " * " + numSectors);
							return null;
						} else {
							byte version = this.file.readByte();
							byte[] data;
							DataInputStream ret;
							if(version == 1) {
								data = new byte[length - 1];
								this.file.read(data);
								ret = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data)));
								return ret;
							} else if(version == 2) {
								data = new byte[length - 1];
								this.file.read(data);
								ret = new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data)));
								return ret;
							} else {
								this.debugln("READ", x, z, "unknown version " + version);
								return null;
							}
						}
					}
				}
			} catch (IOException iOException10) {
				this.debugln("READ", x, z, "exception");
				return null;
			}
		}
	}

	public DataOutputStream getChunkDataOutputStream(int x, int z) {
		return this.outOfBounds(x, z) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFileChunkBuffer(this, x, z)));
	}

	public synchronized void write(int x, int z, byte[] data, int length) {
		try {
			int e = this.getOffset(x, z);
			int sectorNumber = e >> 8;
			int sectorsAllocated = e & 255;
			int sectorsNeeded = (length + 5) / this.SectorBytes() + 1;
			if(sectorsNeeded >= 256) {
				return;
			}

			if(sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
				this.debug("SAVE", x, z, length, "rewrite");
				this.write(sectorNumber, data, length);
			} else {
				int runStart;
				if(sectorNumber < this.sectorFree.size()) {
					for(runStart = 0; runStart < sectorsAllocated; ++runStart) {
						this.sectorFree.set(sectorNumber + runStart, true);
					}
				}

				runStart = this.sectorFree.indexOf(true);
				int runLength = 0;
				int i;
				if(runStart != -1) {
					for(i = runStart; i < this.sectorFree.size(); ++i) {
						if(runLength != 0) {
							if(((Boolean)this.sectorFree.get(i)).booleanValue()) {
								++runLength;
							} else {
								runLength = 0;
							}
						} else if(((Boolean)this.sectorFree.get(i)).booleanValue()) {
							runStart = i;
							runLength = 1;
						}

						if(runLength >= sectorsNeeded) {
							break;
						}
					}
				}

				if(runLength >= sectorsNeeded) {
					this.debug("SAVE", x, z, length, "reuse");
					sectorNumber = runStart;
					this.setOffset(x, z, runStart << 8 | sectorsNeeded);

					for(i = 0; i < sectorsNeeded; ++i) {
						this.sectorFree.set(sectorNumber + i, false);
					}

					this.write(sectorNumber, data, length);
				} else {
					this.debug("SAVE", x, z, length, "grow");
					this.file.seek(this.file.length());
					sectorNumber = this.sectorFree.size();

					for(i = 0; i < sectorsNeeded; ++i) {
						this.file.write(this.getEmptySector());
						this.sectorFree.add(false);
					}

					this.sizeDelta += this.SectorBytes() * sectorsNeeded;
					this.write(sectorNumber, data, length);
					this.setOffset(x, z, sectorNumber << 8 | sectorsNeeded);
				}
			}

			this.setTimestamp(x, z, (int)(System.currentTimeMillis() / 1000L));
		} catch (IOException iOException12) {
			iOException12.printStackTrace();
		}

	}

	private void write(int sectorNumber, byte[] data, int length) throws IOException {
		this.debugln(" " + sectorNumber);
		this.file.seek((long)(sectorNumber * this.SectorBytes()));
		this.file.writeInt(length + 1);
		this.file.writeByte(2);
		this.file.write(data, 0, length);
	}

	private boolean outOfBounds(int x, int z) {
		return x < 0 || x >= 32 || z < 0 || z >= 32;
	}

	private int getOffset(int x, int z) {
		return this.offsets[x + z * 32];
	}

	public boolean func_22202_c(int i, int j) {
		return this.getOffset(i, j) != 0;
	}

	public boolean hasChunk(int x, int z) {
		return this.getOffset(x, z) != 0;
	}

	private void setOffset(int x, int z, int offset) throws IOException {
		int index = x + z * 32;
		this.offsets[index] = offset;
		this.file.seek((long)(index * 4));
		this.file.writeInt(offset);
	}

	private void setTimestamp(int x, int z, int value) throws IOException {
		int index = x + z * 32;
		this.chunkTimestamps[index] = value;
		this.file.seek((long)(4096 + index * 4));
		this.file.writeInt(value);
	}

	public void close() throws IOException {
		this.file.close();
	}

	public abstract int SectorBytes();
}
