package calclavia.lib;

import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import cpw.mods.fml.common.Loader;
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
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.tile.TileEntityElectrical;

import java.util.EnumSet;
import java.util.Iterator;

public class TileEntityUniversalProducer extends TileEntityElectrical implements IEnergySource, IPowerReceptor {

	private IPowerProvider powerProvider;

	public TileEntityUniversalProducer() {
		if (PowerFramework.currentFramework != null && this.powerProvider == null) {
			this.powerProvider = PowerFramework.currentFramework.createPowerProvider();
			this.powerProvider.configure(0, 0, 0, 0, Integer.MAX_VALUE);
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

		if (Loader.isModLoaded("IC2") && remaining.getWatts() > 0.0D) {
			EnergyTileSourceEvent evt = new EnergyTileSourceEvent(this, (int) (remaining.getWatts() * UniversalElectricity.TO_IC2_RATIO));
			MinecraftForge.EVENT_BUS.post(evt);
			remaining = new ElectricityPack((double) evt.amount * UniversalElectricity.IC2_RATIO / remaining.voltage, remaining.voltage);
		}

		return remaining;
	}

	public boolean canConnect(ForgeDirection direction) {
		if (this instanceof IRotatable) {
			return direction.ordinal() == this.getBlockMetadata();
		} else {
			return true;
		}
	}

	public IPowerProvider getPowerProvider(TileEntity tileEntity) {
		return tileEntity instanceof IPowerReceptor ? ((IPowerReceptor) tileEntity).getPowerProvider() : null;
	}

	public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
		return this.canConnect(direction.toForgeDirection());
	}

	public boolean isAddedToEnergyNet() {
		return super.ticks > 0L;
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
		return 0;
	}
}
