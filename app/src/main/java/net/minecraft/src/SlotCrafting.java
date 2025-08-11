package net.minecraft.src;

public class SlotCrafting extends Slot {
	private final IInventory craftMatrix;
	private EntityPlayer thePlayer;

	public SlotCrafting(EntityPlayer paramgs, IInventory paramlw1, IInventory paramlw2, int paramInt1, int paramInt2, int paramInt3) {
		super(paramlw2, paramInt1, paramInt2, paramInt3);
		this.thePlayer = paramgs;
		this.craftMatrix = paramlw1;
	}

	public boolean isItemValid(ItemStack paramiz) {
		return false;
	}

	public void onPickupFromSlot(ItemStack paramiz) {
		paramiz.onCrafting(this.thePlayer.worldObj, this.thePlayer);

		ModLoader.TakenFromCrafting(this.thePlayer, paramiz);

		for(int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
			ItemStack localiz = this.craftMatrix.getStackInSlot(i);
			if(localiz != null) {
				this.craftMatrix.decrStackSize(i, 1);
				if(localiz.getItem().hasContainerItem()) {
					this.craftMatrix.setInventorySlotContents(i, new ItemStack(localiz.getItem().getContainerItem()));
				}
			}
		}

	}
}
