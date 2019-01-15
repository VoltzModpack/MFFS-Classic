package mffs.api.modules;

import mffs.api.IFieldInteraction;
import mffs.api.IProjector;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import java.util.Set;

public interface IModule {

	float getFortronCost(float var1);

	boolean onProject(IProjector var1, Set var2);

	int onProject(IProjector var1, Vector3 var2);

	boolean onCollideWithForceField(World var1, int var2, int var3, int var4, Entity var5, ItemStack var6);

	void onCalculate(IFieldInteraction var1, Set var2);
}
