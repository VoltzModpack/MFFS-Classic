package mffs.api.modules;

import mffs.api.security.IInterdictionMatrix;
import net.minecraft.entity.EntityLiving;

public interface IInterdictionMatrixModule extends IModule {

	boolean onDefend(IInterdictionMatrix var1, EntityLiving var2);
}
