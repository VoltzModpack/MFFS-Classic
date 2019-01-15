package basiccomponents.common.item;

import basiccomponents.common.BasicComponents;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import universalelectricity.core.item.ItemElectric;

public class ItemBattery extends ItemElectric {

	public ItemBattery(String name, int id) {
		super(BasicComponents.CONFIGURATION.getItem(name, id).getInt(id));
		this.setUnlocalizedName("basiccomponents:" + name);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		super.itemIcon = iconRegister.registerIcon(this.getUnlocalizedName().replace("item.", ""));
	}

	public double getMaxJoules(ItemStack itemStack) {
		return 1000000.0D;
	}

	public double getVoltage(ItemStack itemStack) {
		return 25.0D;
	}
}
