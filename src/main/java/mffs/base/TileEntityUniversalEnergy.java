package mffs.base;

import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import calclavia.lib.IUniversalEnergyTile;
import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityNetworkHelper;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

import java.util.EnumSet;
import java.util.Iterator;

public abstract class TileEntityUniversalEnergy extends TileEntityModuleAcceptor implements IEnergySource, IUniversalEnergyTile {

	public double prevWatts;
	public double wattsReceived = 0.0D;
	private IPowerProvider powerProvider;

	public TileEntityUniversalEnergy() {
		if (PowerFramework.currentFramework != null && this.powerProvider == null) {
			this.powerProvider = PowerFramework.currentFramework.createPowerProvider();
			this.powerProvider.configure(0, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
		}

	}

	public void initiate() {
		super.initiate();
		MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
	}

	public void invalidate() {
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
		super.invalidate();
	}

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

		if (this.powerProvider != null) {
			this.powerProvider.configure(0, 0, Integer.MAX_VALUE, 0, (int) Math.ceil(this.getWattBuffer() * UniversalElectricity.TO_BC_RATIO));
			float requiredEnergy = (float) (this.getRequest().getWatts() * UniversalElectricity.TO_BC_RATIO);
			float energyReceived = this.powerProvider.useEnergy(0.0F, requiredEnergy, true);
			this.onReceive(ElectricityPack.getFromWatts(UniversalElectricity.BC3_RATIO * (double) energyReceived, this.getVoltage()));
		}

	}

	public ElectricityPack produce(double watts) {
		ElectricityPack pack = new ElectricityPack(watts / this.getVoltage(), this.getVoltage());
		ElectricityPack remaining = ElectricityNetworkHelper.produceFromMultipleSides(this, pack);
		if (remaining.getWatts() > 0.0D) {
			EnumSet approachingDirections = ElectricityNetworkHelper.getDirections(this);
			Iterator i$ = approachingDirections.iterator();

			while (i$.hasNext()) {
				ForgeDirection direction = (ForgeDirection) i$.next();
				TileEntity tileEntity = VectorHelper.getTileEntityFromSide(super.worldObj, new Vector3(this), direction);
				if (this.getPowerProvider(tileEntity) != null) {
					this.getPowerProvider(tileEntity).receiveEnergy((float) (remaining.getWatts() * UniversalElectricity.TO_BC_RATIO), direction.getOpposite());
				}
			}
		}

		if (remaining.getWatts() > 0.0D) {
			EnergyTileSourceEvent evt = new EnergyTileSourceEvent(this, (int) (remaining.getWatts() * UniversalElectricity.TO_IC2_RATIO));
			MinecraftForge.EVENT_BUS.post(evt);
			remaining = new ElectricityPack((double) evt.amount * UniversalElectricity.IC2_RATIO / remaining.voltage, remaining.voltage);
		}

		return remaining;
	}

	protected EnumSet getConsumingSides() {
		return ElectricityNetworkHelper.getDirections(this);
	}

	public ElectricityPack getRequest() {
		return new ElectricityPack();
	}

	public void onReceive(ElectricityPack electricityPack) {
		if (!UniversalElectricity.isVoltageSensitive || electricityPack.voltage <= this.getVoltage()) {
			this.wattsReceived = Math.min(this.wattsReceived + electricityPack.getWatts(), this.getWattBuffer());
		}
	}

	public double getWattBuffer() {
		return this.getRequest().getWatts() * 2.0D;
	}

	public double getVoltage() {
		return 120.0D;
	}

	public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
		return this.getConsumingSides() != null ? this.getConsumingSides().contains(direction.toForgeDirection()) : true;
	}

	public boolean isAddedToEnergyNet() {
		return super.ticks > 0L;
	}

	public int demandsEnergy() {
		return (int) Math.ceil(this.getRequest().getWatts() * UniversalElectricity.TO_IC2_RATIO);
	}

	public int injectEnergy(Direction direction, int i) {
		double givenElectricity = (double) i * UniversalElectricity.IC2_RATIO;
		double rejects = 0.0D;
		if (givenElectricity > this.getWattBuffer()) {
			rejects = givenElectricity - this.getRequest().getWatts();
		}

		this.onReceive(new ElectricityPack(givenElectricity / this.getVoltage(), this.getVoltage()));
		return (int) (rejects * UniversalElectricity.TO_IC2_RATIO);
	}

	public int getMaxSafeInput() {
		return 2048;
	}

	public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
		return this.canConnect(direction.toForgeDirection());
	}

	public int getMaxEnergyOutput() {
		return 2048;
	}

	public void setPowerProvider(IPowerProvider provider) {
		this.powerProvider = provider;
	}

	public IPowerProvider getPowerProvider() {
		return this.powerProvider;
	}

	public void doWork() {
	}

	public int powerRequest(ForgeDirection from) {
		return this.canConnect(from) ? (int) Math.ceil(this.getRequest().getWatts() * UniversalElectricity.TO_BC_RATIO) : 0;
	}

	public IPowerProvider getPowerProvider(TileEntity tileEntity) {
		return tileEntity instanceof IPowerReceptor ? ((IPowerReceptor) tileEntity).getPowerProvider() : null;
	}
}
