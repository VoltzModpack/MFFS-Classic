package mffs.slot;

import mffs.base.TileEntityInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotBase extends Slot {

	protected TileEntityInventory tileEntity;

	public SlotBase(TileEntityInventory tileEntity, int id, int par4, int par5) {
		super(tileEntity, id, par4, par5);
		this.tileEntity = tileEntity;
	}

	public boolean isItemValid(ItemStack itemStack) {
		return this.tileEntity.isStackValidForSlot(super.slotNumber, itemStack);
	}

	public int getSlotStackLimit() {
		ItemStack itemStack = this.tileEntity.getStackInSlot(super.slotNumber);
		return itemStack != null ? itemStack.getMaxStackSize() : this.tileEntity.getInventoryStackLimit();
	}
}
