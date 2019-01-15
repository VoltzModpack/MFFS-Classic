package universalelectricity.prefab.tile;

import net.minecraft.entity.Entity;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityNetworkHelper;
import universalelectricity.core.electricity.ElectricityPack;

import java.util.EnumSet;

public abstract class TileEntityElectricityRunnable extends TileEntityElectrical {

	public double prevWatts;
	public double wattsReceived = 0.0D;

	public void updateEntity() {
		super.updateEntity();
		this.prevWatts = this.wattsReceived;
		if (!super.worldObj.isRemote) {
			if (!this.isDisabled()) {
				ElectricityPack electricityPack = ElectricityNetworkHelper.consumeFromMultipleSides(this, this.getConsumingSides(), this.getRequest());
				this.onReceive(electricityPack);
			} else {
				ElectricityNetworkHelper.consumeFromMultipleSides(this, new ElectricityPack());
			}
		}

	}

	protected EnumSet getConsumingSides() {
		return ElectricityNetworkHelper.getDirections(this);
	}

	public ElectricityPack getRequest() {
		return new ElectricityPack();
	}

	public void onReceive(ElectricityPack electricityPack) {
		if (UniversalElectricity.isVoltageSensitive && electricityPack.voltage > this.getVoltage()) {
			super.worldObj.createExplosion((Entity) null, (double) super.xCoord, (double) super.yCoord, (double) super.zCoord, 1.5F, true);
		} else {
			this.wattsReceived = Math.min(this.wattsReceived + electricityPack.getWatts(), this.getWattBuffer());
		}
	}

	public double getWattBuffer() {
		return this.getRequest().getWatts() * 2.0D;
	}
}
