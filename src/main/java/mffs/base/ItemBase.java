package mffs.base;

import mffs.MFFSCreativeTab;
import mffs.MFFSHelper;
import mffs.Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import universalelectricity.prefab.TranslationHelper;

import java.util.List;

public class ItemBase extends Item {

	public ItemBase(int id, String name) {
		super(Settings.CONFIGURATION.getItem(name, id).getInt(id));
		this.setUnlocalizedName("mffs:" + name);
		this.setCreativeTab(MFFSCreativeTab.INSTANCE);
		this.setNoRepair();
	}

	public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
		String tooltip = TranslationHelper.getLocal(this.getUnlocalizedName() + ".tooltip");
		if (tooltip != null && tooltip.length() > 0) {
			info.addAll(MFFSHelper.splitStringPerWord(tooltip, 5));
		}

	}
}
