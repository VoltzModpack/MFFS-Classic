package mffs.item.module.interdiction;

import mffs.api.modules.IInterdictionMatrixModule;
import mffs.api.security.IInterdictionMatrix;
import mffs.item.module.ItemModule;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemModuleInterdictionMatrix extends ItemModule implements IInterdictionMatrixModule {

	public ItemModuleInterdictionMatrix(int id, String name) {
		super(id, name);
	}

	public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
		info.add("§4Interdiction Matrix");
		super.addInformation(itemStack, player, info, b);
	}

	public boolean onDefend(IInterdictionMatrix interdictionMatrix, EntityLiving entityLiving) {
		return false;
	}
}
