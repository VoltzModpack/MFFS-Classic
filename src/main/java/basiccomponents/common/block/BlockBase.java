package basiccomponents.common.block;

import basiccomponents.common.BasicComponents;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockBase extends Block {

	public BlockBase(String name, int id) {
		super(BasicComponents.CONFIGURATION.getItem(name, id).getInt(id), Material.rock);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setUnlocalizedName("basiccomponents:" + name);
		this.setHardness(2.0F);
	}
}
