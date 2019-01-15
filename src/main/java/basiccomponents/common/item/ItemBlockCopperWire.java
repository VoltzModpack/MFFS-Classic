package basiccomponents.common.item;

import basiccomponents.common.tileentity.TileEntityCopperWire;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import universalelectricity.core.electricity.ElectricityDisplay;

import java.util.List;

public class ItemBlockCopperWire extends ItemBlock {

	public ItemBlockCopperWire(int id) {
		super(id);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("Resistance: " + ElectricityDisplay.getDisplay(TileEntityCopperWire.RESISTANCE, ElectricityDisplay.ElectricUnit.RESISTANCE));
		par3List.add("Max Amps: " + ElectricityDisplay.getDisplay(TileEntityCopperWire.MAX_AMPS, ElectricityDisplay.ElectricUnit.AMPERE));
	}
}
