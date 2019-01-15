package ic2.api.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IWrenchable {

	boolean wrenchCanSetFacing(EntityPlayer var1, int var2);

	short getFacing();

	void setFacing(short var1);

	boolean wrenchCanRemove(EntityPlayer var1);

	float getWrenchDropRate();

	ItemStack getWrenchDrop(EntityPlayer var1);
}
