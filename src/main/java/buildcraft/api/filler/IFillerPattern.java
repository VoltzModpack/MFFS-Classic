package buildcraft.api.filler;

import buildcraft.api.core.IBox;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;

public interface IFillerPattern {

	int getId();

	void setId(int var1);

	boolean iteratePattern(TileEntity var1, IBox var2, ItemStack var3);

	@SideOnly(Side.CLIENT)
	Icon getTexture();

	String getName();
}
