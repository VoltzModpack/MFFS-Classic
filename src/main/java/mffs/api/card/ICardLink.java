package mffs.api.card;

import net.minecraft.item.ItemStack;
import universalelectricity.core.vector.Vector3;

public interface ICardLink {

	void setLink(ItemStack var1, Vector3 var2);

	Vector3 getLink(ItemStack var1);
}
