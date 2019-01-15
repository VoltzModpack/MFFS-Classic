package buildcraft.api.transport;

import net.minecraft.world.World;

public interface IExtractionHandler {

	boolean canExtractItems(Object var1, World var2, int var3, int var4, int var5);

	boolean canExtractLiquids(Object var1, World var2, int var3, int var4, int var5);
}
