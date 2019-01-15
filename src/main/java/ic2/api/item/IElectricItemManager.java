package ic2.api.item;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

public interface IElectricItemManager {

	int charge(ItemStack var1, int var2, int var3, boolean var4, boolean var5);

	int discharge(ItemStack var1, int var2, int var3, boolean var4, boolean var5);

	int getCharge(ItemStack var1);

	boolean canUse(ItemStack var1, int var2);

	boolean use(ItemStack var1, int var2, EntityLiving var3);

	void chargeFromArmor(ItemStack var1, EntityLiving var2);

	String getToolTip(ItemStack var1);
}
