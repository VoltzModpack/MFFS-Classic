package mffs.slot;

import icbm.api.IBlockFrequency;
import icbm.api.IItemFrequency;
import mffs.base.TileEntityFrequency;
import net.minecraft.item.ItemStack;

public class SlotCard extends SlotBase {

	public SlotCard(TileEntityFrequency tileEntity, int id, int par4, int par5) {
		super(tileEntity, id, par4, par5);
	}

	public void onSlotChanged() {
		super.onSlotChanged();
		ItemStack itemStack = this.getStack();
		if (itemStack != null && itemStack.getItem() instanceof IItemFrequency) {
			((IItemFrequency) itemStack.getItem()).setFrequency(((IBlockFrequency) super.tileEntity).getFrequency(), itemStack);
		}

	}
}
