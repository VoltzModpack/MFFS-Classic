package mffs.slot;

import mffs.base.TileEntityInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class SlotActive extends SlotBase {

	public SlotActive(TileEntityInventory tileEntity, int id, int par4, int par5) {
		super(tileEntity, id, par4, par5);
	}

	public boolean isItemValid(ItemStack itemStack) {
		return super.isItemValid(itemStack) && !super.tileEntity.isActive();
	}

	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return !super.tileEntity.isActive();
	}
}
