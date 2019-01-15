package mffs.api;

import mffs.api.modules.IModule;
import mffs.api.modules.IModuleAcceptor;
import mffs.api.modules.IProjectorMode;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRotatable;

import java.util.Set;

public interface IFieldInteraction extends IModuleAcceptor, IRotatable, IActivatable {

	IProjectorMode getMode();

	ItemStack getModeStack();

	int[] getSlotsBasedOnDirection(ForgeDirection var1);

	int[] getModuleSlots();

	int getSidedModuleCount(IModule var1, ForgeDirection... var2);

	Vector3 getTranslation();

	Vector3 getPositiveScale();

	Vector3 getNegativeScale();

	int getRotationYaw();

	int getRotationPitch();

	Set getCalculatedField();

	Set getInteriorPoints();

	void setCalculating(boolean var1);

	void setCalculated(boolean var1);
}
