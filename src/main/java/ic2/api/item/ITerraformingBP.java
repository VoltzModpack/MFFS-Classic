package ic2.api.item;

import net.minecraft.world.World;

public interface ITerraformingBP {

	int getConsume();

	int getRange();

	boolean terraform(World var1, int var2, int var3, int var4);
}
