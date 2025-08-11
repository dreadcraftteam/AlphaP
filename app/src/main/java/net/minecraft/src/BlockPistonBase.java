package net.minecraft.src;

import java.util.ArrayList;

public class BlockPistonBase extends Block {
	private boolean isSticky;
	private boolean field_31048_b;

	public BlockPistonBase(int i, int j, boolean flag) {
		super(i, j, Material.field_31067_B);
		this.isSticky = flag;
		this.setStepSound(soundStoneFootstep);
		this.setHardness(0.5F);
	}

	public int func_31040_i() {
		return !this.isSticky ? 107 : 106;
	}

	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		int k = func_31044_d(j);
		return k > 5 ? this.blockIndexInTexture : (i == k ? (!isPowered(j) && this.minX <= 0.0D && this.minY <= 0.0D && this.minZ <= 0.0D && this.maxX >= 1.0D && this.maxY >= 1.0D && this.maxZ >= 1.0D ? this.blockIndexInTexture : 110) : (i != PistonBlockTextures.field_31057_a[k] ? 108 : 109));
	}

	public int getRenderType() {
		return 16;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		return false;
	}

	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
		int l = func_31039_c(world, i, j, k, (EntityPlayer)entityliving);
		world.setBlockMetadataWithNotify(i, j, k, l);
		if(!world.multiplayerWorld) {
			this.func_31043_h(world, i, j, k);
		}

	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		if(!world.multiplayerWorld && !this.field_31048_b) {
			this.func_31043_h(world, i, j, k);
		}

	}

	public void onBlockAdded(World world, int i, int j, int k) {
		if(!world.multiplayerWorld && world.getBlockTileEntity(i, j, k) == null) {
			this.func_31043_h(world, i, j, k);
		}

	}

	private void func_31043_h(World world, int i, int j, int k) {
		int l = world.getBlockMetadata(i, j, k);
		int i1 = func_31044_d(l);
		boolean flag = this.func_31041_f(world, i, j, k, i1);
		if(l != 7) {
			if(flag && !isPowered(l)) {
				if(func_31045_h(world, i, j, k, i1)) {
					world.setBlockMetadata(i, j, k, i1 | 8);
					world.playNoteAt(i, j, k, 0, i1);
				}
			} else if(!flag && isPowered(l)) {
				world.setBlockMetadata(i, j, k, i1);
				world.playNoteAt(i, j, k, 1, i1);
			}

		}
	}

	private boolean func_31041_f(World world, int i, int j, int k, int l) {
		return l != 0 && world.isBlockIndirectlyProvidingPowerTo(i, j - 1, k, 0) ? true : (l != 1 && world.isBlockIndirectlyProvidingPowerTo(i, j + 1, k, 1) ? true : (l != 2 && world.isBlockIndirectlyProvidingPowerTo(i, j, k - 1, 2) ? true : (l != 3 && world.isBlockIndirectlyProvidingPowerTo(i, j, k + 1, 3) ? true : (l != 5 && world.isBlockIndirectlyProvidingPowerTo(i + 1, j, k, 5) ? true : (l != 4 && world.isBlockIndirectlyProvidingPowerTo(i - 1, j, k, 4) ? true : (world.isBlockIndirectlyProvidingPowerTo(i, j, k, 0) ? true : (world.isBlockIndirectlyProvidingPowerTo(i, j + 2, k, 1) ? true : (world.isBlockIndirectlyProvidingPowerTo(i, j + 1, k - 1, 2) ? true : (world.isBlockIndirectlyProvidingPowerTo(i, j + 1, k + 1, 3) ? true : (world.isBlockIndirectlyProvidingPowerTo(i - 1, j + 1, k, 4) ? true : world.isBlockIndirectlyProvidingPowerTo(i + 1, j + 1, k, 5)))))))))));
	}

	public void playBlock(World world, int i, int j, int k, int l, int i1) {
		this.field_31048_b = true;
		if(l == 0) {
			if(this.func_31047_i(world, i, j, k, i1)) {
				world.setBlockMetadataWithNotify(i, j, k, i1 | 8);
				world.playSoundEffect((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
			}
		} else if(l == 1) {
			TileEntity tileentity = world.getBlockTileEntity(i + PistonBlockTextures.field_31056_b[i1], j + PistonBlockTextures.field_31059_c[i1], k + PistonBlockTextures.field_31058_d[i1]);
			if(tileentity != null && tileentity instanceof TileEntityPiston) {
				((TileEntityPiston)tileentity).func_31011_l();
			}

			world.setBlockAndMetadata(i, j, k, Block.pistonMoving.blockID, i1);
			world.setBlockTileEntity(i, j, k, BlockPistonMoving.func_31036_a(this.blockID, i1, i1, false, true));
			if(this.isSticky) {
				int k1 = i + PistonBlockTextures.field_31056_b[i1] * 2;
				int l1 = j + PistonBlockTextures.field_31059_c[i1] * 2;
				int i2 = k + PistonBlockTextures.field_31058_d[i1] * 2;
				int j2 = world.getBlockId(k1, l1, i2);
				int k2 = world.getBlockMetadata(k1, l1, i2);
				boolean flag = false;
				if(j2 == Block.pistonMoving.blockID) {
					TileEntity tileentity1 = world.getBlockTileEntity(k1, l1, i2);
					if(tileentity1 != null && tileentity1 instanceof TileEntityPiston) {
						TileEntityPiston tileentitypiston = (TileEntityPiston)tileentity1;
						if(tileentitypiston.func_31009_d() == i1 && tileentitypiston.func_31015_b()) {
							tileentitypiston.func_31011_l();
							j2 = tileentitypiston.getStoredBlockID();
							k2 = tileentitypiston.getBlockMetadata();
							flag = true;
						}
					}
				}

				if(flag || j2 <= 0 || !canPushBlock(j2, world, k1, l1, i2, false) || Block.blocksList[j2].getMobilityFlag() != 0 && j2 != Block.pistonBase.blockID && j2 != Block.pistonStickyBase.blockID) {
					if(!flag) {
						this.field_31048_b = false;
						world.setBlockWithNotify(i + PistonBlockTextures.field_31056_b[i1], j + PistonBlockTextures.field_31059_c[i1], k + PistonBlockTextures.field_31058_d[i1], 0);
						this.field_31048_b = true;
					}
				} else {
					this.field_31048_b = false;
					world.setBlockWithNotify(k1, l1, i2, 0);
					this.field_31048_b = true;
					i += PistonBlockTextures.field_31056_b[i1];
					j += PistonBlockTextures.field_31059_c[i1];
					k += PistonBlockTextures.field_31058_d[i1];
					world.setBlockAndMetadata(i, j, k, Block.pistonMoving.blockID, k2);
					world.setBlockTileEntity(i, j, k, BlockPistonMoving.func_31036_a(j2, k2, i1, false, false));
				}
			} else {
				this.field_31048_b = false;
				world.setBlockWithNotify(i + PistonBlockTextures.field_31056_b[i1], j + PistonBlockTextures.field_31059_c[i1], k + PistonBlockTextures.field_31058_d[i1], 0);
				this.field_31048_b = true;
			}

			world.playSoundEffect((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
		}

		this.field_31048_b = false;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		int l = iblockaccess.getBlockMetadata(i, j, k);
		if(isPowered(l)) {
			switch(func_31044_d(l)) {
			case 0:
				this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
				break;
			case 1:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
				break;
			case 2:
				this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
				break;
			case 3:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
				break;
			case 4:
				this.setBlockBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				break;
			case 5:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
			}
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}

	}

	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public void getCollidingBoundingBoxes(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, ArrayList arraylist) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public static int func_31044_d(int i) {
		return i & 7;
	}

	public static boolean isPowered(int i) {
		return (i & 8) != 0;
	}

	private static int func_31039_c(World world, int i, int j, int k, EntityPlayer entityplayer) {
		if(MathHelper.abs((float)entityplayer.posX - (float)i) < 2.0F && MathHelper.abs((float)entityplayer.posZ - (float)k) < 2.0F) {
			double l = entityplayer.posY + 1.82D - (double)entityplayer.yOffset;
			if(l - (double)j > 2.0D) {
				return 1;
			}

			if((double)j - l > 0.0D) {
				return 0;
			}
		}

		int l1 = MathHelper.floor_double((double)(entityplayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		return l1 == 0 ? 2 : (l1 == 1 ? 5 : (l1 == 2 ? 3 : (l1 != 3 ? 0 : 4)));
	}

	private static boolean canPushBlock(int i, World world, int j, int k, int l, boolean flag) {
		if(i == Block.obsidian.blockID) {
			return false;
		} else {
			if(i != Block.pistonBase.blockID && i != Block.pistonStickyBase.blockID) {
				if(Block.blocksList[i].getHardness() == -1.0F) {
					return false;
				}

				if(Block.blocksList[i].getMobilityFlag() == 2) {
					return false;
				}

				if(!flag && Block.blocksList[i].getMobilityFlag() == 1) {
					return false;
				}
			} else if(isPowered(world.getBlockMetadata(j, k, l))) {
				return false;
			}

			TileEntity tileentity = world.getBlockTileEntity(j, k, l);
			return tileentity == null;
		}
	}

	private static boolean func_31045_h(World world, int i, int j, int k, int l) {
		int i1 = i + PistonBlockTextures.field_31056_b[l];
		int j1 = j + PistonBlockTextures.field_31059_c[l];
		int k1 = k + PistonBlockTextures.field_31058_d[l];

		for(int l1 = 0; l1 < 13; ++l1) {
			int i2 = world.getBlockId(i1, j1, k1);
			if(i2 == 0) {
				break;
			}

			if(!canPushBlock(i2, world, i1, j1, k1, true)) {
				return false;
			}

			if(Block.blocksList[i2].getMobilityFlag() == 1) {
				break;
			}

			if(l1 == 12) {
				return false;
			}

			i1 += PistonBlockTextures.field_31056_b[l];
			j1 += PistonBlockTextures.field_31059_c[l];
			k1 += PistonBlockTextures.field_31058_d[l];
		}

		return true;
	}

	private boolean func_31047_i(World world, int i, int j, int k, int l) {
		int i1 = i + PistonBlockTextures.field_31056_b[l];
		int j1 = j + PistonBlockTextures.field_31059_c[l];
		int k1 = k + PistonBlockTextures.field_31058_d[l];

		int l2;
		for(int l1 = 0; l1 < 13; ++l1) {
			l2 = world.getBlockId(i1, j1, k1);
			if(l2 == 0) {
				break;
			}

			if(!canPushBlock(l2, world, i1, j1, k1, true)) {
				return false;
			}

			if(Block.blocksList[l2].getMobilityFlag() == 1) {
				Block.blocksList[l2].dropBlockAsItem(world, i1, j1, k1, world.getBlockMetadata(i1, j1, k1));
				world.setBlockWithNotify(i1, j1, k1, 0);
				break;
			}

			if(l1 == 12) {
				return false;
			}

			i1 += PistonBlockTextures.field_31056_b[l];
			j1 += PistonBlockTextures.field_31059_c[l];
			k1 += PistonBlockTextures.field_31058_d[l];
		}

		while(i1 != i || j1 != j || k1 != k) {
			int i2 = i1 - PistonBlockTextures.field_31056_b[l];
			int k2 = j1 - PistonBlockTextures.field_31059_c[l];
			l2 = k1 - PistonBlockTextures.field_31058_d[l];
			int i3 = world.getBlockId(i2, k2, l2);
			int j3 = world.getBlockMetadata(i2, k2, l2);
			if(i3 == this.blockID && i2 == i && k2 == j && l2 == k) {
				world.setBlockAndMetadata(i1, j1, k1, Block.pistonMoving.blockID, l | (this.isSticky ? 8 : 0));
				world.setBlockTileEntity(i1, j1, k1, BlockPistonMoving.func_31036_a(Block.pistonExtension.blockID, l | (this.isSticky ? 8 : 0), l, true, false));
			} else {
				world.setBlockAndMetadata(i1, j1, k1, Block.pistonMoving.blockID, j3);
				world.setBlockTileEntity(i1, j1, k1, BlockPistonMoving.func_31036_a(i3, j3, l, true, false));
			}

			i1 = i2;
			j1 = k2;
			k1 = l2;
		}

		return true;
	}
}
