package ic2.api.item;

import net.minecraft.item.ItemStack;

/**
 * @deprecated
 */
@Deprecated
public interface ICustomElectricItem extends IElectricItem {

	int charge(ItemStack var1, int var2, int var3, boolean var4, boolean var5);

	int discharge(ItemStack var1, int var2, int var3, boolean var4, boolean var5);

	boolean canUse(ItemStack var1, int var2);

	boolean canShowChargeToolTip(ItemStack var1);
}
