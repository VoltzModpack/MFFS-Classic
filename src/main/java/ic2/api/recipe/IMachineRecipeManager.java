package ic2.api.recipe;

import net.minecraft.item.ItemStack;

import java.util.Map;

public interface IMachineRecipeManager {

	void addRecipe(ItemStack var1, Object var2);

	Object getOutputFor(ItemStack var1, boolean var2);

	Map getRecipes();
}
