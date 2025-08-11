package net.minecraft_server.src;

import java.util.Random;

public class MapGenBase {
	protected int field_947_a = 8;
	protected Random rand = new Random();

	public void func_667_a(IChunkProvider ichunkprovider, World world, int i, int j, byte[] abyte0) {
		int k = this.field_947_a;
		this.rand.setSeed(world.getRandomSeed());
		long l = this.rand.nextLong() / 2L * 2L + 1L;
		long l1 = this.rand.nextLong() / 2L * 2L + 1L;

		for (int i1 = i - k; i1 <= i + k; ++i1) {
			for (int j1 = j - k; j1 <= j + k; ++j1) {
				this.rand.setSeed((long) i1 * l + (long) j1 * l1 ^ world.getRandomSeed());
				this.func_666_a(world, i1, j1, i, j, abyte0);
			}
		}

	}

	protected void func_666_a(World world, int i, int j, int k, int l, byte[] abyte0) {
	}

	public void generate(IChunkProvider ichunkprovider, World world, int xCh, int yCh, int zCh, byte[] data) {
		int range = this.field_947_a;
		this.rand.setSeed(world.getRandomSeed());
		long r1 = this.rand.nextLong() / 2L * 2L + 1L;
		long r2 = this.rand.nextLong() / 2L * 2L + 1L;
		long r3 = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed((long) xCh * r1 + (long) zCh * r2 ^ world.getRandomSeed() + (long) yCh * r3
				^ world.getRandomSeed() * world.getRandomSeed());

		for (int x1 = xCh - range; x1 <= xCh + range; ++x1) {
			for (int z1 = zCh - range; z1 <= zCh + range; ++z1) {
				for (int y1 = zCh - range; y1 <= zCh + range; ++y1) {
					this.rand.setSeed((long) x1 * r1 + (long) z1 * r2 ^ world.getRandomSeed() + (long) y1 * r3
							^ world.getRandomSeed() * world.getRandomSeed());
					this.subGenerate(world, x1, y1, z1, xCh, yCh, zCh, data);
				}
			}
		}

	}

	protected void subGenerate(World world, int x1, int y1, int z1, int x, int y, int z, byte[] data) {
	}
}
