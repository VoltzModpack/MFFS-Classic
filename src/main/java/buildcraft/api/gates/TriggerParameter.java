package buildcraft.api.gates;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TriggerParameter implements ITriggerParameter {

	protected ItemStack stack;

	public ItemStack getItemStack() {
		return this.stack;
	}

	public void set(ItemStack stack) {
		if (stack != null) {
			this.stack = stack.copy();
			this.stack.stackSize = 1;
		}

	}

	public void writeToNBT(NBTTagCompound compound) {
		if (this.stack != null) {
			compound.setInteger("itemID", this.stack.itemID);
			compound.setInteger("itemDMG", this.stack.getItemDamage());
		}

	}

	public void readFromNBT(NBTTagCompound compound) {
		int itemID = compound.getInteger("itemID");
		if (itemID != 0) {
			this.stack = new ItemStack(itemID, 1, compound.getInteger("itemDMG"));
		}

	}

	public ItemStack getItem() {
		return this.stack;
	}
}
