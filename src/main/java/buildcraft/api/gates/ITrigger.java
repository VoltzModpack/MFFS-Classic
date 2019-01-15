package buildcraft.api.gates;

import buildcraft.api.core.IIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public interface ITrigger {

	int getId();

	int getIconIndex();

	@SideOnly(Side.CLIENT)
	IIconProvider getIconProvider();

	boolean hasParameter();

	String getDescription();

	boolean isTriggerActive(ForgeDirection var1, TileEntity var2, ITriggerParameter var3);

	ITriggerParameter createParameter();
}
