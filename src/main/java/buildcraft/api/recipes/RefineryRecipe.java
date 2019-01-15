package buildcraft.api.recipes;

import net.minecraftforge.liquids.LiquidStack;

import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class RefineryRecipe implements Comparable {

	private static SortedSet recipes = new TreeSet();
	public final LiquidStack ingredient1;
	public final LiquidStack ingredient2;
	public final LiquidStack result;
	public final int energy;
	public final int delay;

	public static void registerRefineryRecipe(RefineryRecipe recipe) {
		if (!recipes.contains(recipe)) {
			recipes.add(recipe);
		}

	}

	public static SortedSet getRecipes() {
		return Collections.unmodifiableSortedSet(recipes);
	}

	public static RefineryRecipe findRefineryRecipe(LiquidStack liquid1, LiquidStack liquid2) {
		Iterator i$ = recipes.iterator();

		RefineryRecipe recipe;
		do {
			if (!i$.hasNext()) {
				return null;
			}

			recipe = (RefineryRecipe) i$.next();
		} while (!recipe.matches(liquid1, liquid2));

		return recipe;
	}

	public RefineryRecipe(int ingredientId1, int ingredientQty1, int ingredientId2, int ingredientQty2, int resultId, int resultQty, int energy, int delay) {
		this(new LiquidStack(ingredientId1, ingredientQty1, 0), new LiquidStack(ingredientId2, ingredientQty2, 0), new LiquidStack(resultId, resultQty, 0), energy, delay);
	}

	public RefineryRecipe(LiquidStack ingredient1, LiquidStack ingredient2, LiquidStack result, int energy, int delay) {
		if (ingredient1 != null && ingredient2 != null) {
			if (ingredient1.itemID <= ingredient2.itemID && (ingredient1.itemID != ingredient2.itemID || ingredient1.itemMeta <= ingredient2.itemMeta)) {
				this.ingredient1 = ingredient1;
				this.ingredient2 = ingredient2;
			} else {
				this.ingredient1 = ingredient2;
				this.ingredient2 = ingredient1;
			}
		} else if (ingredient2 != null) {
			this.ingredient1 = ingredient2;
			this.ingredient2 = ingredient1;
		} else {
			this.ingredient1 = ingredient1;
			this.ingredient2 = ingredient2;
		}

		this.result = result;
		this.energy = energy;
		this.delay = delay;
	}

	public boolean matches(LiquidStack liquid1, LiquidStack liquid2) {
		if (liquid1 == null && liquid2 == null) {
			return false;
		} else if (this.ingredient1 == null || this.ingredient2 == null || liquid1 != null && liquid2 != null) {
			if (this.ingredient1 != null) {
				if (this.ingredient2 == null) {
					return this.ingredient1.isLiquidEqual(liquid1) || this.ingredient1.isLiquidEqual(liquid2);
				} else {
					return this.ingredient1.isLiquidEqual(liquid1) && this.ingredient2.isLiquidEqual(liquid2) || this.ingredient2.isLiquidEqual(liquid1) && this.ingredient1.isLiquidEqual(liquid2);
				}
			} else if (this.ingredient2 == null) {
				return false;
			} else {
				return this.ingredient2.isLiquidEqual(liquid1) || this.ingredient2.isLiquidEqual(liquid2);
			}
		} else {
			return false;
		}
	}

	public int compareTo(RefineryRecipe other) {
		if (other == null) {
			return -1;
		} else if (this.ingredient1 == null) {
			return other.ingredient1 == null ? 0 : 1;
		} else if (other.ingredient1 == null) {
			return -1;
		} else if (this.ingredient1.itemID != other.ingredient1.itemID) {
			return this.ingredient1.itemID - other.ingredient1.itemID;
		} else if (this.ingredient1.itemMeta != other.ingredient1.itemMeta) {
			return this.ingredient1.itemMeta - other.ingredient1.itemMeta;
		} else if (this.ingredient2 == null) {
			return other.ingredient2 == null ? 0 : 1;
		} else if (other.ingredient2 == null) {
			return -1;
		} else if (this.ingredient2.itemID != other.ingredient2.itemID) {
			return this.ingredient2.itemID - other.ingredient2.itemID;
		} else {
			return this.ingredient2.itemMeta != other.ingredient2.itemMeta ? this.ingredient2.itemMeta - other.ingredient2.itemMeta : 0;
		}
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof RefineryRecipe) {
			return this.compareTo((RefineryRecipe) obj) == 0;
		} else {
			return false;
		}
	}

	public int hashCode() {
		if (this.ingredient1 == null) {
			return 0;
		} else {
			return this.ingredient2 == null ? this.ingredient1.itemID ^ this.ingredient1.itemMeta : this.ingredient1.itemID ^ this.ingredient1.itemMeta ^ this.ingredient2.itemID ^ this.ingredient2.itemMeta;
		}
	}
}
