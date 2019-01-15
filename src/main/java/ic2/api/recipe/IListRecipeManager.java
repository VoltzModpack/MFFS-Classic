package ic2.api.recipe;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IListRecipeManager extends Iterable {

	void add(ItemStack var1);

	boolean contains(ItemStack var1);

	List getStacks();
}
