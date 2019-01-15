package mffs.api.card;

import mffs.api.security.Permission;
import net.minecraft.item.ItemStack;

public interface ICardIdentification extends ICard {

	boolean hasPermission(ItemStack var1, Permission var2);

	boolean addPermission(ItemStack var1, Permission var2);

	boolean removePermission(ItemStack var1, Permission var2);

	String getUsername(ItemStack var1);

	void setUsername(ItemStack var1, String var2);
}
