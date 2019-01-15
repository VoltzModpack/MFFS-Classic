package mffs.item.module.projector;

import mffs.ModularForceFieldSystem;
import mffs.item.module.ItemModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemModuleShock extends ItemModule {

	public ItemModuleShock(int i) {
		super(i, "moduleShock");
	}

	public boolean onCollideWithForceField(World world, int x, int y, int z, Entity entity, ItemStack moduleStack) {
		if (entity instanceof EntityLiving) {
			entity.attackEntityFrom(ModularForceFieldSystem.damagefieldShock, moduleStack.stackSize);
		}

		return false;
	}
}
