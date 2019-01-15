package universalelectricity.core.electricity;

import universalelectricity.core.block.IConductor;

import java.util.List;

public interface IConductorRegistry {

	void register(IConductor var1);

	void cleanConductors();

	void resetAllConnections();

	List getConductors();
}
