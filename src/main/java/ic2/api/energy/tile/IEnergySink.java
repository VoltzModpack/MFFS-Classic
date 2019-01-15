package ic2.api.energy.tile;

import ic2.api.Direction;

public interface IEnergySink extends IEnergyAcceptor {

	int demandsEnergy();

	int injectEnergy(Direction var1, int var2);

	int getMaxSafeInput();
}
