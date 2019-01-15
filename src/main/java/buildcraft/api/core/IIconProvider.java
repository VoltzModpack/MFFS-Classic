package buildcraft.api.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public interface IIconProvider {

	@SideOnly(Side.CLIENT)
	Icon getIcon(int var1);

	@SideOnly(Side.CLIENT)
	void registerIcons(IconRegister var1);
}
