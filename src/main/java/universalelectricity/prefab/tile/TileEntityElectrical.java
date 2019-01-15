package universalelectricity.prefab.tile;

import universalelectricity.core.block.IConnector;
import universalelectricity.core.block.IVoltage;
import universalelectricity.core.electricity.ElectricityNetworkHelper;

public abstract class TileEntityElectrical extends TileEntityDisableable implements IConnector, IVoltage {

	public void updateEntity() {
		super.updateEntity();
	}

	public double getVoltage() {
		return 120.0D;
	}

	public void invalidate() {
		ElectricityNetworkHelper.invalidate(this);
		super.invalidate();
	}
}
