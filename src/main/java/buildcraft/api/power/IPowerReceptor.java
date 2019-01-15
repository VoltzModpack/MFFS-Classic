package buildcraft.api.power;

import net.minecraftforge.common.ForgeDirection;

public interface IPowerReceptor {

	void setPowerProvider(IPowerProvider var1);

	IPowerProvider getPowerProvider();

	void doWork();

	int powerRequest(ForgeDirection var1);
}
