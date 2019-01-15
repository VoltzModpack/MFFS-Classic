package universalelectricity.core.item;

import net.minecraft.item.ItemStack;
import universalelectricity.core.electricity.ElectricityPack;

public interface IItemElectric extends IItemElectricityStorage, IItemVoltage {

	ElectricityPack onReceive(ElectricityPack var1, ItemStack var2);

	ElectricityPack onProvide(ElectricityPack var1, ItemStack var2);

	ElectricityPack getReceiveRequest(ItemStack var1);

	ElectricityPack getProvideRequest(ItemStack var1);
}
