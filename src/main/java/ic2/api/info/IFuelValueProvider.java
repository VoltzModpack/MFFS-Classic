package ic2.api.info;

import net.minecraft.item.ItemStack;

public interface IFuelValueProvider {

	int getFuelValue(ItemStack var1, boolean var2);
}
