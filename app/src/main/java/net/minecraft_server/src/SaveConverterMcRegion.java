package net.minecraft_server.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

public class SaveConverterMcRegion extends SaveFormatOld {
	public SaveConverterMcRegion(File file) {
		super(file);
	}

	public ISaveHandler func_22105_a(String s, boolean flag) {
		return new SaveOldDir(this.field_22106_a, s, flag);
	}

	public boolean isOldSaveType(String s) {
		WorldInfo worldinfo = this.getWorldInfo(s);
		return worldinfo != null && worldinfo.getVersion() == 0;
	}

	public boolean converMapToMCRegion(String s, IProgressUpdate iprogressupdate) {
		iprogressupdate.setLoadingProgress(0);
		ArrayList arraylist = new ArrayList();
		ArrayList arraylist1 = new ArrayList();
		ArrayList arraylist2 = new ArrayList();
		ArrayList arraylist3 = new ArrayList();
		File file = new File(this.field_22106_a, s);
		File file1 = new File(file, "DIM-1");
		System.out.println("Scanning folders...");
		this.func_22108_a(file, arraylist, arraylist1);
		if (file1.exists()) {
			this.func_22108_a(file1, arraylist2, arraylist3);
		}

		int i = arraylist.size() + arraylist2.size() + arraylist1.size() + arraylist3.size();
		System.out.println("Total conversion count is " + i);
		this.func_22107_a(file, arraylist, 0, i, iprogressupdate);
		this.func_22107_a(file1, arraylist2, arraylist.size(), i, iprogressupdate);
		WorldInfo worldinfo = this.getWorldInfo(s);
		worldinfo.setVersion(19132);
		ISaveHandler isavehandler = this.func_22105_a(s, false);
		isavehandler.func_22094_a(worldinfo);
		this.func_22109_a(arraylist1, arraylist.size() + arraylist2.size(), i, iprogressupdate);
		if (file1.exists()) {
			this.func_22109_a(arraylist3, arraylist.size() + arraylist2.size() + arraylist1.size(), i, iprogressupdate);
		}

		return true;
	}

	private void func_22108_a(File file, ArrayList arraylist, ArrayList arraylist1) {
		ChunkFolderPattern chunkfolderpattern = new ChunkFolderPattern((Empty2) null);
		ChunkFilePattern chunkfilepattern = new ChunkFilePattern((Empty2) null);
		File[] afile = file.listFiles(chunkfolderpattern);
		File[] afile1 = afile;
		int i = afile.length;

		for (int j = 0; j < i; ++j) {
			File file1 = afile1[j];
			arraylist1.add(file1);
			File[] afile2 = file1.listFiles(chunkfolderpattern);
			File[] afile3 = afile2;
			int k = afile2.length;

			for (int l = 0; l < k; ++l) {
				File file2 = afile3[l];
				File[] afile4 = file2.listFiles(chunkfilepattern);
				File[] afile5 = afile4;
				int i1 = afile4.length;

				for (int j1 = 0; j1 < i1; ++j1) {
					File file3 = afile5[j1];
					arraylist.add(new ChunkFile(file3));
				}
			}
		}

	}

	private void func_22107_a(File file, ArrayList arraylist, int i, int j, IProgressUpdate iprogressupdate) {
		Collections.sort(arraylist);
		byte[] abyte0 = new byte[4096];
		Iterator iterator = arraylist.iterator();

		while (iterator.hasNext()) {
			ChunkFile chunkfile = (ChunkFile) iterator.next();
			int k = chunkfile.func_22205_b();
			int l = chunkfile.func_22204_c();
			RegionFile regionfile = RegionFileCache.createOrLoadRegionFile(file, k, l);
			if (!regionfile.hasChunk(k & 31, l & 31)) {
				try {
					DataInputStream ioexception = new DataInputStream(
							new GZIPInputStream(new FileInputStream(chunkfile.func_22207_a())));
					DataOutputStream dataoutputstream = regionfile.getChunkDataOutputStream(k & 31, l & 31);
					boolean j1 = false;

					int i17;
					while ((i17 = ioexception.read(abyte0)) != -1) {
						dataoutputstream.write(abyte0, 0, i17);
					}

					dataoutputstream.close();
					ioexception.close();
				} catch (IOException iOException16) {
					iOException16.printStackTrace();
				}
			}

			++i;
			int i1 = (int) Math.round(100.0D * (double) i / (double) j);
			iprogressupdate.setLoadingProgress(i1);
		}

		RegionFileCache.dumpChunkMapCache();
	}

	private void func_22109_a(ArrayList arraylist, int i, int j, IProgressUpdate iprogressupdate) {
		Iterator iterator = arraylist.iterator();

		while (iterator.hasNext()) {
			File file = (File) iterator.next();
			File[] afile = file.listFiles();
			func_22104_a(afile);
			file.delete();
			++i;
			int k = (int) Math.round(100.0D * (double) i / (double) j);
			iprogressupdate.setLoadingProgress(k);
		}

	}
}
