package net.minecraft_server.src;

import java.util.Random;

public class MapGenCaves extends MapGenBase {
	protected void func_669_a(int i, int j, byte[] abyte0, double d, double d1, double d2) {
		this.genCave(i, j, abyte0, d, d1, d2, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
	}

	protected void genCave(int i, int j, byte[] abyte0, double d, double d1, double d2, float f, float f1, float f2,
			int k, int l, double d3) {
		double d4 = (double) (i * 16 + 8);
		double d5 = (double) (j * 16 + 8);
		float f3 = 0.0F;
		float f4 = 0.0F;
		Random random = new Random(this.rand.nextLong());
		if (l <= 0) {
			int flag = this.field_947_a * 16 - 16;
			l = flag - random.nextInt(flag / 4);
		}

		boolean z60 = false;
		if (k == -1) {
			k = l / 2;
			z60 = true;
		}

		int j1 = random.nextInt(l / 2) + l / 4;

		for (boolean flag1 = random.nextInt(6) == 0; k < l; ++k) {
			double d6 = 1.5D + (double) (MathHelper.sin((float) k * 3.141593F / (float) l) * f * 1.0F);
			double d7 = d6 * d3;
			float f5 = MathHelper.cos(f2);
			float f6 = MathHelper.sin(f2);
			d += (double) (MathHelper.cos(f1) * f5);
			d1 += (double) f6;
			d2 += (double) (MathHelper.sin(f1) * f5);
			if (flag1) {
				f2 *= 0.92F;
			} else {
				f2 *= 0.7F;
			}

			f2 += f4 * 0.1F;
			f1 += f3 * 0.1F;
			f4 *= 0.9F;
			f3 *= 0.75F;
			f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (!z60 && k == j1 && f > 1.0F) {
				this.genCave(i, j, abyte0, d, d1, d2, random.nextFloat() * 0.5F + 0.5F, f1 - 1.570796F, f2 / 3.0F, k, l,
						1.0D);
				this.genCave(i, j, abyte0, d, d1, d2, random.nextFloat() * 0.5F + 0.5F, f1 + 1.570796F, f2 / 3.0F, k, l,
						1.0D);
				return;
			}

			if (z60 || random.nextInt(4) != 0) {
				double d8a = d - d4;
				double d9a = d2 - d5;
				double d10a = (double) (l - k);
				double d11 = (double) (f + 2.0F + 16.0F);
				if (d8a * d8a + d9a * d9a - d10a * d10a > d11 * d11) {
					return;
				}

				if (d >= d4 - 16.0D - d6 * 2.0D && d2 >= d5 - 16.0D - d6 * 2.0D && d <= d4 + 16.0D + d6 * 2.0D
						&& d2 <= d5 + 16.0D + d6 * 2.0D) {
					int d8 = MathHelper.floor_double(d - d6) - i * 16 - 1;
					int k1 = MathHelper.floor_double(d + d6) - i * 16 + 1;
					int d9 = MathHelper.floor_double(d1 - d7) - 1;
					int l1 = MathHelper.floor_double(d1 + d7) + 1;
					int d10 = MathHelper.floor_double(d2 - d6) - j * 16 - 1;
					int i2 = MathHelper.floor_double(d2 + d6) - j * 16 + 1;
					if (d8 < 0) {
						d8 = 0;
					}

					if (k1 > 16) {
						k1 = 16;
					}

					if (d9 < 1) {
						d9 = 1;
					}

					if (l1 > 120) {
						l1 = 120;
					}

					if (d10 < 0) {
						d10 = 0;
					}

					if (i2 > 16) {
						i2 = 16;
					}

					boolean flag2 = false;

					int k2;
					int k3;
					for (k2 = d8; !flag2 && k2 < k1; ++k2) {
						for (int d12 = d10; !flag2 && d12 < i2; ++d12) {
							for (int i3 = l1 + 1; !flag2 && i3 >= d9 - 1; --i3) {
								k3 = (k2 * 16 + d12) * 128 + i3;
								if (i3 >= 0 && i3 < 128) {
									if (abyte0[k3] == Block.waterMoving.blockID
											|| abyte0[k3] == Block.waterStill.blockID) {
										flag2 = true;
									}

									if (i3 != d9 - 1 && k2 != d8 && k2 != k1 - 1 && d12 != d10 && d12 != i2 - 1) {
										i3 = d9;
									}
								}
							}
						}
					}

					if (!flag2) {
						for (k2 = d8; k2 < k1; ++k2) {
							double d61 = ((double) (k2 + i * 16) + 0.5D - d) / d6;

							for (k3 = d10; k3 < i2; ++k3) {
								double d13 = ((double) (k3 + j * 16) + 0.5D - d2) / d6;
								int l3 = (k2 * 16 + k3) * 128 + l1;
								boolean flag3 = false;
								if (d61 * d61 + d13 * d13 < 1.0D) {
									for (int i4 = l1 - 1; i4 >= d9; --i4) {
										double d14 = ((double) i4 + 0.5D - d1) / d7;
										if (d14 > -0.7D && d61 * d61 + d14 * d14 + d13 * d13 < 1.0D) {
											byte byte0 = abyte0[l3];
											if (byte0 == Block.grass.blockID) {
												flag3 = true;
											}

											if (byte0 == Block.stone.blockID || byte0 == Block.dirt.blockID
													|| byte0 == Block.grass.blockID) {
												if (i4 < 10) {
													abyte0[l3] = (byte) Block.lavaMoving.blockID;
												} else {
													abyte0[l3] = 0;
													if (flag3 && abyte0[l3 - 1] == Block.dirt.blockID) {
														abyte0[l3 - 1] = (byte) Block.grass.blockID;
													}
												}
											}
										}

										--l3;
									}
								}
							}
						}

						if (z60) {
							break;
						}
					}
				}
			}
		}

	}

	protected void func_666_a(World world, int i, int j, int k, int l, byte[] abyte0) {
		int i1 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
		if (this.rand.nextInt(15) != 0) {
			i1 = 0;
		}

		for (int j1 = 0; j1 < i1; ++j1) {
			double d = (double) (i * 16 + this.rand.nextInt(16));
			double d1 = (double) this.rand.nextInt(this.rand.nextInt(120) + 8);
			double d2 = (double) (j * 16 + this.rand.nextInt(16));
			int k1 = 1;
			if (this.rand.nextInt(4) == 0) {
				this.func_669_a(k, l, abyte0, d, d1, d2);
				k1 += this.rand.nextInt(4);
			}

			for (int l1 = 0; l1 < k1; ++l1) {
				float f = this.rand.nextFloat() * 3.141593F * 2.0F;
				float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float f2 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
				this.genCave(k, l, abyte0, d, d1, d2, f2, f, f1, 0, 0, 1.0D);
			}
		}

	}

	protected void subGenerate(World world, int x1, int y1, int z1, int xCh, int yCh, int zCh, byte[] data) {
		int r1 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
		if (this.rand.nextInt(15) == 0) {
			for (int n = 0; n < r1; ++n) {
				double x2 = (double) (x1 * 16 + this.rand.nextInt(16));
				double y2 = (double) this.rand.nextInt(this.rand.nextInt(120) + 8);
				double z2 = (double) (z1 * 16 + this.rand.nextInt(16));
				if (y1 < 0) {
					if (this.rand.nextInt(5) != 0) {
						continue;
					}

					y2 = (double) (y1 * 16 + this.rand.nextInt(16));
				} else {
					if (y1 >= 8) {
						continue;
					}

					double k1 = y2 - (double) y1;
					if (k1 > 16.0D || k1 < -16.0D) {
						continue;
					}
				}

				int i22 = 1;
				if (this.rand.nextInt(4) == 0) {
					this.func_870_a(xCh, yCh, zCh, data, x2, y2, z2);
					i22 += this.rand.nextInt(4);
				}

				for (int n2 = 0; n2 < i22; ++n2) {
					float f = this.rand.nextFloat() * 3.141593F * 2.0F;
					float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
					float f2 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
					this.genCave(xCh, yCh, zCh, data, x2, y2, z2, f2, f, f1, 0, 0, 1.0D);
				}
			}

		}
	}

	protected void func_870_a(int i, int y, int j, byte[] abyte0, double d, double d1, double d2) {
		this.genCave(i, y, j, abyte0, d, d1, d2, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
	}

	protected void genCave(int xCh, int yCh, int zCh, byte[] data, double x1, double y1, double z1, float f, float f1,
			float f2, int k, int l, double d3) {
		double xBl = (double) (xCh * 16 + 8);
		double yBl = (double) (yCh * 16 + 8);
		double zBl = (double) (zCh * 16 + 8);
		float f3 = 0.0F;
		float f4 = 0.0F;
		Random random = new Random(this.rand.nextLong());
		if (l <= 0) {
			int flag = this.field_947_a * 16 - 16;
			l = flag - random.nextInt(flag / 4);
		}

		boolean z63 = false;
		if (k == -1) {
			k = l / 2;
			z63 = true;
		}

		int j1 = random.nextInt(l / 2) + l / 4;

		for (boolean flag1 = random.nextInt(6) == 0; k < l; ++k) {
			double d6 = 1.5D + (double) (MathHelper.sin((float) k * 3.141593F / (float) l) * f * 1.0F);
			double d7 = d6 * d3;
			float f5 = MathHelper.cos(f2);
			float f6 = MathHelper.sin(f2);
			x1 += (double) (MathHelper.cos(f1) * f5);
			y1 += (double) f6;
			z1 += (double) (MathHelper.sin(f1) * f5);
			if (flag1) {
				f2 *= 0.92F;
			} else {
				f2 *= 0.7F;
			}

			f2 += f4 * 0.1F;
			f1 += f3 * 0.1F;
			f4 *= 0.9F;
			f3 *= 0.75F;
			f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (!z63 && k == j1 && f > 1.0F) {
				this.genCave(xCh, yCh, zCh, data, x1, y1, z1, random.nextFloat() * 0.5F + 0.5F, f1 - 1.570796F,
						f2 / 3.0F, k, l, 1.0D);
				this.genCave(xCh, yCh, zCh, data, x1, y1, z1, random.nextFloat() * 0.5F + 0.5F, f1 + 1.570796F,
						f2 / 3.0F, k, l, 1.0D);
				return;
			}

			if (z63 || random.nextInt(4) != 0) {
				double d8a = x1 - xBl;
				double d9a = z1 - zBl;
				double d10a = (double) (l - k);
				double d11 = (double) (f + 2.0F + 16.0F);
				if (d8a * d8a + d9a * d9a - d10a * d10a > d11 * d11) {
					return;
				}

				if (x1 >= xBl - 16.0D - d6 * 2.0D && z1 >= zBl - 16.0D - d6 * 2.0D && x1 <= xBl + 16.0D + d6 * 2.0D
						&& z1 <= zBl + 16.0D + d6 * 2.0D) {
					int d8 = MathHelper.floor_double(x1 - d6) - xCh * 16 - 1;
					int k1 = MathHelper.floor_double(x1 + d6) - xCh * 16 + 1;
					int d9 = MathHelper.floor_double(y1 - d7) - 1;
					int l1 = MathHelper.floor_double(y1 + d7) + 1;
					int d10 = MathHelper.floor_double(z1 - d6) - zCh * 16 - 1;
					int i2 = MathHelper.floor_double(z1 + d6) - zCh * 16 + 1;
					if (d8 < 0) {
						d8 = 0;
					}

					if (k1 > 16) {
						k1 = 16;
					}

					if (d10 < 0) {
						d10 = 0;
					}

					if (i2 > 16) {
						i2 = 16;
					}

					boolean flag2 = false;

					int k2;
					int k3;
					for (k2 = d8; !flag2 && k2 < k1; ++k2) {
						for (int d12 = d10; !flag2 && d12 < i2; ++d12) {
							for (int i3 = l1 + 1; !flag2 && i3 >= d9 - 1; --i3) {
								k3 = (k2 * 16 + d12) * 16 + i3 % 16;
								if (k3 >= 0 && k3 <= 4096) {
									if (data[k3] == Block.waterMoving.blockID || data[k3] == Block.waterStill.blockID) {
										flag2 = true;
									}

									if (i3 != d9 - 1 && k2 != d8 && k2 != k1 - 1 && d12 != d10 && d12 != i2 - 1) {
										i3 = d9;
									}
								}
							}
						}
					}

					if (!flag2) {
						for (k2 = d8; k2 < k1; ++k2) {
							double d64 = ((double) (k2 + xCh * 16) + 0.5D - x1) / d6;

							for (k3 = d10; k3 < i2; ++k3) {
								double d13 = ((double) (k3 + zCh * 16) + 0.5D - z1) / d6;
								int index = (k2 * 16 + k3) * 16 + (l1 & 15);
								boolean flag3 = false;
								if (d64 * d64 + d13 * d13 < 1.0D) {
									for (int i4 = l1 - 1; i4 >= d9 && index >= 0; --i4) {
										double d14 = ((double) i4 + 0.5D - y1) / d7;
										if (d14 > -0.7D && d64 * d64 + d14 * d14 + d13 * d13 < 1.0D) {
											byte byte0 = data[index];
											if (byte0 == Block.grass.blockID) {
												flag3 = true;
											}

											if (byte0 == Block.stone.blockID || byte0 == Block.dirt.blockID
													|| byte0 == Block.grass.blockID) {
												if (i4 < 10) {
													data[index] = (byte) Block.lavaMoving.blockID;
												} else {
													data[index] = 0;
													if (flag3 && data[index - 1] == Block.dirt.blockID) {
														data[index - 1] = (byte) Block.grass.blockID;
													}
												}
											}
										}

										--index;
									}
								}
							}
						}

						if (z63) {
							break;
						}
					}
				}
			}
		}

	}
}
