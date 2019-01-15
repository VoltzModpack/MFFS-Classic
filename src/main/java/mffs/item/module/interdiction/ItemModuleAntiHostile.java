package mffs.item.module.interdiction;

import mffs.ModularForceFieldSystem;
import mffs.api.security.IInterdictionMatrix;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;

public class ItemModuleAntiHostile extends ItemModuleInterdictionMatrix {

	public ItemModuleAntiHostile(int i) {
		super(i, "moduleAntiHostile");
	}

	public boolean onDefend(IInterdictionMatrix interdictionMatrix, EntityLiving entityLiving) {
		if (entityLiving instanceof IMob && !(entityLiving instanceof INpc)) {
			entityLiving.attackEntityFrom(ModularForceFieldSystem.damagefieldShock, 20);
		}

		return false;
	}
}
