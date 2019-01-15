package ic2.api.recipe;

import net.minecraft.item.ItemStack;

public interface ICraftingRecipeManager {

	void addRecipe(ItemStack var1, Object... var2);

	void addShapelessRecipe(ItemStack var1, Object... var2);
}
