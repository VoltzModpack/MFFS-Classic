package buildcraft.api.recipes;

import net.minecraft.item.ItemStack;

import java.util.LinkedList;

public class AssemblyRecipe {

	public static LinkedList assemblyRecipes = new LinkedList();
	public final ItemStack[] input;
	public final ItemStack output;
	public final float energy;

	public AssemblyRecipe(ItemStack[] input, int energy, ItemStack output) {
		this.input = input;
		this.output = output;
		this.energy = (float) energy;
	}

	public boolean canBeDone(ItemStack[] items) {
		ItemStack[] arr$ = this.input;
		int len$ = arr$.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			ItemStack in = arr$[i$];
			if (in != null) {
				int found = 0;
				ItemStack[] arr$ = items;
				int len$ = items.length;

				for (int i$ = 0; i$ < len$; ++i$) {
					ItemStack item = arr$[i$];
					if (item != null && item.isItemEqual(in)) {
						found += item.stackSize;
					}
				}

				if (found < in.stackSize) {
					return false;
				}
			}
		}

		return true;
	}
}
