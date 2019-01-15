package calclavia.lib;

import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.PowerFramework;
import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.tile.TileEntityElectricityRunnable;

public abstract class TileEntityUniversalRunnable extends TileEntityElectricityRunnable implements IUniversalEnergyTile {

	private IPowerProvider powerProvider;

	public TileEntityUniversalRunnable() {
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
		if (this.powerProvider != null && !super.worldObj.isRemote) {
			this.powerProvider.configure(0, 0, Integer.MAX_VALUE, 0, (int) Math.ceil(this.getWattBuffer() * UniversalElectricity.TO_BC_RATIO));
			float requiredEnergy = (float) (this.getRequest().getWatts() * UniversalElectricity.TO_BC_RATIO);
			float energyReceived = this.powerProvider.useEnergy(0.0F, requiredEnergy, true);
			this.onReceive(ElectricityPack.getFromWatts(UniversalElectricity.BC3_RATIO * (double) energyReceived, this.getVoltage()));
		}

	}

	public boolean canConnect(ForgeDirection direction) {
		if (this instanceof IRotatable) {
			return direction == ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite();
		} else {
			return true;
		}
	}

	public ForgeDirection getDirection(IBlockAccess world, int x, int y, int z) {
		return ForgeDirection.getOrientation(this.getBlockMetadata());
	}

	public void setDirection(World world, int x, int y, int z, ForgeDirection facingDirection) {
		super.worldObj.setBlockMetadataWithNotify(super.xCoord, super.yCoord, super.zCoord, facingDirection.ordinal(), 2);
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
}
