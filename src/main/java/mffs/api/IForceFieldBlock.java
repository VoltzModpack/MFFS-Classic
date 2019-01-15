package mffs.api;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IForceFieldBlock {

	IProjector getProjector(IBlockAccess var1, int var2, int var3, int var4);

	void weakenForceField(World var1, int var2, int var3, int var4, int var5);
}
