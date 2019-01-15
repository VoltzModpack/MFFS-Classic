package universalelectricity.core.block;

public interface IConductor extends INetworkProvider, IConnectionProvider {

	double getResistance();

	double getCurrentCapcity();
}
