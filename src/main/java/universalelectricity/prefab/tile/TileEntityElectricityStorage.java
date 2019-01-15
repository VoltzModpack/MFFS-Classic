package universalelectricity.prefab.tile;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.block.IElectricityStorage;
import universalelectricity.core.electricity.ElectricityNetworkHelper;
import universalelectricity.core.electricity.ElectricityPack;

import java.util.EnumSet;

public abstract class TileEntityElectricityStorage extends TileEntityElectrical implements IElectricityStorage {

	private double joules = 0.0D;
	public double prevJoules = 0.0D;

	public void updateEntity() {
		super.updateEntity();
		this.prevJoules = this.joules;
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
		return new ElectricityPack((this.getMaxJoules() - this.getJoules()) / this.getVoltage(), this.getVoltage());
	}

	public void onReceive(ElectricityPack electricityPack) {
		if (UniversalElectricity.isVoltageSensitive && electricityPack.voltage > this.getVoltage()) {
			super.worldObj.createExplosion((Entity) null, (double) super.xCoord, (double) super.yCoord, (double) super.zCoord, 1.5F, true);
		} else {
			this.setJoules(this.getJoules() + electricityPack.getWatts());
		}
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.joules = par1NBTTagCompound.getDouble("joules");
	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setDouble("joules", this.joules);
	}

	public double getJoules() {
		return this.joules;
	}

	public void setJoules(double joules) {
		this.joules = Math.max(Math.min(joules, this.getMaxJoules()), 0.0D);
	}
}
