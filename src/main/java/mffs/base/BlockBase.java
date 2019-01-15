package mffs.base;

import mffs.MFFSCreativeTab;
import mffs.Settings;
import net.minecraft.block.material.Material;
import universalelectricity.prefab.block.BlockAdvanced;

public class BlockBase extends BlockAdvanced {

	public BlockBase(int id, String name, Material material) {
		super(Settings.CONFIGURATION.getBlock(name, id).getInt(id), material);
		this.setUnlocalizedName("mffs:" + name);
		this.setCreativeTab(MFFSCreativeTab.INSTANCE);
	}
}
