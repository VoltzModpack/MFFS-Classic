package mffs.tileentity;

import com.google.common.io.ByteArrayDataInput;
import mffs.ModularForceFieldSystem;
import mffs.Settings;
import mffs.api.modules.IModule;
import mffs.base.TileEntityBase;
import mffs.base.TileEntityUniversalEnergy;
import mffs.fortron.FortronHelper;
import mffs.item.card.ItemCardFrequency;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.item.ElectricItemHelper;
import universalelectricity.core.item.IItemElectric;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TileEntityCoercionDeriver extends TileEntityUniversalEnergy {

	public static final int WATTAGE = 1000;
	public static final int REQUIRED_TIME = 200;
	private static final int INITIAL_PRODUCTION = 40;
	public static final int MULTIPLE_PRODUCTION = 4;
	public static final float FORTRON_UE_RATIO = 6.0F;
	public static final int SLOT_FREQUENCY = 0;
	public static final int SLOT_BATTERY = 1;
	public static final int SLOT_FUEL = 2;
	public int processTime = 0;
	public boolean isInversed = false;

	public TileEntityCoercionDeriver() {
		super.capacityBase = 30;
		super.startModuleIndex = 3;
	}

	public void updateEntity() {
		super.updateEntity();
		if (!super.worldObj.isRemote) {
			if (!this.isDisabled() && this.isActive()) {
				if (this.isInversed && Settings.ENABLE_ELECTRICITY) {
					double watts = (double) Math.min((float) this.getFortronEnergy() * 6.0F, 1000.0F);
					ElectricityPack remainder = this.produce(watts);
					double electricItemGiven = 0.0D;
					if (remainder.getWatts() > 0.0D) {
						electricItemGiven = ElectricItemHelper.chargeItem(this.getStackInSlot(1), remainder.getWatts(), this.getVoltage());
					}

					this.requestFortron((int) ((watts - (remainder.getWatts() - electricItemGiven)) / 6.0D), true);
				} else {
					super.wattsReceived += ElectricItemHelper.dechargeItem(this.getStackInSlot(1), 1000.0D, this.getVoltage());
					if (super.wattsReceived >= 1000.0D || !Settings.ENABLE_ELECTRICITY && this.isStackValidForSlot(2, this.getStackInSlot(2))) {
						int production = this.getProductionRate();
						super.fortronTank.fill(FortronHelper.getFortron(production + super.worldObj.rand.nextInt(production)), true);
						if (this.processTime == 0 && this.isStackValidForSlot(2, this.getStackInSlot(2))) {
							this.decrStackSize(2, 1);
							this.processTime = 200 * Math.max(this.getModuleCount(ModularForceFieldSystem.itemModuleSpeed, new int[0]) / 20, 1);
						}

						if (this.processTime > 0) {
							--this.processTime;
							if (this.processTime < 1) {
								this.processTime = 0;
							}
						} else {
							this.processTime = 0;
						}

						super.wattsReceived -= 1000.0D;
					}
				}
			}
		} else if (this.isActive()) {
			++super.animation;
		}

	}

	public int getProductionRate() {
		if (!this.isDisabled() && this.isActive() && !this.isInversed) {
			int production = 40;
			if (this.processTime > 0) {
				production *= 4;
			}

			return production;
		} else {
			return 0;
		}
	}

	public int getSizeInventory() {
		return 6;
	}

	public ElectricityPack getRequest() {
		return this.canConsume() ? new ElectricityPack(1000.0D / this.getVoltage(), this.getVoltage()) : super.getRequest();
	}

	public boolean canConsume() {
		if (this.isActive() && !this.isInversed) {
			return FortronHelper.getAmount(super.fortronTank) < super.fortronTank.getCapacity();
		} else {
			return false;
		}
	}

	public List getPacketUpdate() {
		List objects = new LinkedList();
		objects.addAll(super.getPacketUpdate());
		objects.add(this.isInversed);
		objects.add(super.wattsReceived);
		return objects;
	}

	public void onReceivePacket(int packetID, ByteArrayDataInput dataStream) throws IOException {
		super.onReceivePacket(packetID, dataStream);
		if (packetID == TileEntityBase.TilePacketType.DESCRIPTION.ordinal()) {
			this.isInversed = dataStream.readBoolean();
			super.wattsReceived = dataStream.readDouble();
		} else if (packetID == TileEntityBase.TilePacketType.TOGGLE_MODE.ordinal()) {
			this.isInversed = !this.isInversed;
		}

	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.processTime = nbt.getInteger("processTime");
		this.isInversed = nbt.getBoolean("isInversed");
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("processTime", this.processTime);
		nbt.setBoolean("isInversed", this.isInversed);
	}

	public boolean isStackValidForSlot(int slotID, ItemStack itemStack) {
		if (itemStack != null) {
			if (slotID >= super.startModuleIndex) {
				return itemStack.getItem() instanceof IModule;
			}

			switch (slotID) {
				case 0:
					return itemStack.getItem() instanceof ItemCardFrequency;
				case 1:
					return itemStack.getItem() instanceof IItemElectric;
				case 2:
					return itemStack.isItemEqual(new ItemStack(Item.dyePowder, 1, 4)) || itemStack.isItemEqual(new ItemStack(Item.netherQuartz));
			}
		}

		return false;
	}

	public boolean canConnect(ForgeDirection direction) {
		return true;
	}
}
