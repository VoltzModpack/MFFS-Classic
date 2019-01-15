package ic2.api.item;

import net.minecraft.item.ItemStack;

public interface IElectricItem {

	boolean canProvideEnergy(ItemStack var1);

	int getChargedItemId(ItemStack var1);

	int getEmptyItemId(ItemStack var1);

	int getMaxCharge(ItemStack var1);

	int getTier(ItemStack var1);

	int getTransferLimit(ItemStack var1);
}
