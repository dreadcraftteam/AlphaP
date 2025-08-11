package net.minecraft_server.src;

public class ContainerWorkbench extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public IInventory craftResult = new InventoryCraftResult();
	private World field_20150_c;
	private int field_20149_h;
	private int field_20148_i;
	private int field_20147_j;

	public ContainerWorkbench(InventoryPlayer inventoryplayer, World world, int i, int j, int k) {
		this.field_20150_c = world;
		this.field_20149_h = i;
		this.field_20148_i = j;
		this.field_20147_j = k;
		this.addSlot(new SlotCrafting(inventoryplayer.player, this.craftMatrix, this.craftResult, 0, 124, 35));

		int j1;
		int l1;
		for (j1 = 0; j1 < 3; ++j1) {
			for (l1 = 0; l1 < 3; ++l1) {
				this.addSlot(new Slot(this.craftMatrix, l1 + j1 * 3, 30 + l1 * 18, 17 + j1 * 18));
			}
		}

		for (j1 = 0; j1 < 3; ++j1) {
			for (l1 = 0; l1 < 9; ++l1) {
				this.addSlot(new Slot(inventoryplayer, l1 + j1 * 9 + 9, 8 + l1 * 18, 84 + j1 * 18));
			}
		}

		for (j1 = 0; j1 < 9; ++j1) {
			this.addSlot(new Slot(inventoryplayer, j1, 8 + j1 * 18, 142));
		}

		this.onCraftMatrixChanged(this.craftMatrix);
	}

	public void onCraftMatrixChanged(IInventory iinventory) {
		this.craftResult.setInventorySlotContents(0,
				CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix));
	}

	public void onCraftGuiClosed(EntityPlayer entityplayer) {
		super.onCraftGuiClosed(entityplayer);
		if (!this.field_20150_c.multiplayerWorld) {
			for (int i = 0; i < 9; ++i) {
				ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
				if (itemstack != null) {
					entityplayer.dropPlayerItem(itemstack);
				}
			}

		}
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return this.field_20150_c.getBlockId(this.field_20149_h, this.field_20148_i,
				this.field_20147_j) != Block.workbench.blockID ? false
						: entityplayer.getDistanceSq((double) this.field_20149_h + 0.5D,
								(double) this.field_20148_i + 0.5D, (double) this.field_20147_j + 0.5D) <= 64.0D;
	}

	public ItemStack func_27086_a(int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(i);
		if (slot != null && slot.func_27006_b()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i == 0) {
				this.func_28126_a(itemstack1, 10, 46, true);
			} else if (i >= 10 && i < 37) {
				this.func_28126_a(itemstack1, 37, 46, false);
			} else if (i >= 37 && i < 46) {
				this.func_28126_a(itemstack1, 10, 37, false);
			} else {
				this.func_28126_a(itemstack1, 10, 46, false);
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(itemstack1);
		}

		return itemstack;
	}
}
