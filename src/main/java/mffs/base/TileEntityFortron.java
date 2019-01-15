package mffs.base;

import mffs.MFFSHelper;
import mffs.Settings;
import mffs.TransferMode;
import mffs.api.ISpecialForceManipulation;
import mffs.api.card.ICard;
import mffs.api.fortron.IFortronFrequency;
import mffs.fortron.FortronHelper;
import mffs.fortron.FrequencyGrid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidTank;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;

public abstract class TileEntityFortron extends TileEntityFrequency implements IFluidContainerItem, IFortronFrequency, ISpecialForceManipulation {

	protected FluidTank fortronTank;
	private boolean markSendFortron;

	public TileEntityFortron() {
		this.fortronTank = new FluidTank(FortronHelper.LIQUID_FORTRON.copy(), 1000, this);
		this.markSendFortron = true;
	}

	public void updateEntity() {
		super.updateEntity();
		if (!Settings.CONSERVE_PACKETS && super.ticks % 60L == 0L) {
			PacketManager.sendPacketToClients(this.getDescriptionPacket(), super.worldObj, new Vector3(this), 30.0D);
		}

	}

	public void invalidate() {
		if (this.markSendFortron) {
			MFFSHelper.transferFortron(this, FrequencyGrid.instance().getFortronTiles(super.worldObj, new Vector3(this), 100, this.getFrequency()), TransferMode.DRAIN, Integer.MAX_VALUE);
		}

		super.invalidate();
	}

	public boolean preMove(int x, int y, int z) {
		return true;
	}

	public void move(int x, int y, int z) {
		this.markSendFortron = false;
	}

	public void postMove() {
	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.fortronTank.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fortron")));
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (this.fortronTank.getFluid() != null) {
			NBTTagCompound fortronCompound = new NBTTagCompound();
			this.fortronTank.getFluid().writeToNBT(fortronCompound);
			nbt.setTag("fortron", fortronCompound);
		}

	}

	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return resource.isFluidEqual(FortronHelper.LIQUID_FORTRON) ? this.fortronTank.fill(resource, doFill) : 0;
	}

	public int fill(int tankIndex, FluidStack resource, boolean doFill) {
		return this.fill(ForgeDirection.getOrientation(tankIndex), resource, doFill);
	}

	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return this.fortronTank.drain(maxDrain, doDrain);
	}

	public FluidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return this.drain(ForgeDirection.getOrientation(tankIndex), maxDrain, doDrain);
	}

	public IFluidTank[] getTanks(ForgeDirection direction) {
		return new IFluidTank[][]{this.fortronTank};
	}

	public IFluidTank getTank(ForgeDirection direction, FluidStack type) {
		return type.isFluidEqual(FortronHelper.LIQUID_FORTRON) ? this.fortronTank : null;
	}

	public void setFortronEnergy(int joules) {
		this.fortronTank.setFluid(FortronHelper.getFortron(joules));
	}

	public int getFortronEnergy() {
		return FortronHelper.getAmount(this.fortronTank);
	}

	public int getFortronCapacity() {
		return this.fortronTank.getCapacity();
	}

	public int requestFortron(int joules, boolean doUse) {
		return FortronHelper.getAmount(this.fortronTank.drain(joules, doUse));
	}

	public int provideFortron(int joules, boolean doUse) {
		return this.fortronTank.fill(FortronHelper.getFortron(joules), doUse);
	}

	public ItemStack getCard() {
		ItemStack itemStack = this.getStackInSlot(0);
		return itemStack != null && itemStack.getItem() instanceof ICard ? itemStack : null;
	}
}
