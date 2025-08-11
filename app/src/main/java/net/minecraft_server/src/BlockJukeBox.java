package net.minecraft_server.src;

public class BlockJukeBox extends BlockContainer {
	protected BlockJukeBox(int i, int j) {
		super(i, j, Material.wood);
	}

	public int getBlockTextureFromSide(int i) {
		return this.blockIndexInTexture + (i != 1 ? 0 : 1);
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		if (world.getBlockMetadata(i, j, k) == 0) {
			return false;
		} else {
			this.func_28035_b_(world, i, j, k);
			return true;
		}
	}

	public void ejectRecord(World world, int i, int j, int k, int l) {
		if (!world.multiplayerWorld) {
			TileEntityRecordPlayer tileentityrecordplayer = (TileEntityRecordPlayer) world.getBlockTileEntity(i, j, k);
			tileentityrecordplayer.field_28009_a = l;
			tileentityrecordplayer.onInventoryChanged();
			world.setBlockMetadataWithNotify(i, j, k, 1);
		}
	}

	public void func_28035_b_(World world, int i, int j, int k) {
		if (!world.multiplayerWorld) {
			TileEntityRecordPlayer tileentityrecordplayer = (TileEntityRecordPlayer) world.getBlockTileEntity(i, j, k);
			int l = tileentityrecordplayer.field_28009_a;
			if (l != 0) {
				world.func_28097_e(1005, i, j, k, 0);
				world.playRecord((String) null, i, j, k);
				tileentityrecordplayer.field_28009_a = 0;
				tileentityrecordplayer.onInventoryChanged();
				world.setBlockMetadataWithNotify(i, j, k, 0);
				float f = 0.7F;
				double d = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
				double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.2D + 0.6D;
				double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
				EntityItem entityitem = new EntityItem(world, (double) i + d, (double) j + d1, (double) k + d2,
						new ItemStack(l, 1, 0));
				entityitem.delayBeforeCanPickup = 10;
				world.entityJoinedWorld(entityitem);
			}
		}
	}

	public void onBlockRemoval(World world, int i, int j, int k) {
		this.func_28035_b_(world, i, j, k);
		super.onBlockRemoval(world, i, j, k);
	}

	public void dropBlockAsItemWithChance(World world, int i, int j, int k, int l, float f) {
		if (!world.multiplayerWorld) {
			super.dropBlockAsItemWithChance(world, i, j, k, l, f);
		}
	}

	protected TileEntity getBlockEntity() {
		return new TileEntityRecordPlayer();
	}
}
