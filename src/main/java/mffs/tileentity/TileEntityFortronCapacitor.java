package mffs.tileentity;

import com.google.common.io.ByteArrayDataInput;
import icbm.api.IBlockFrequency;
import mffs.MFFSHelper;
import mffs.ModularForceFieldSystem;
import mffs.TransferMode;
import mffs.api.card.ICard;
import mffs.api.card.ICardInfinite;
import mffs.api.card.ICardLink;
import mffs.api.fortron.IFortronCapacitor;
import mffs.api.fortron.IFortronFrequency;
import mffs.api.fortron.IFortronStorage;
import mffs.api.modules.IModule;
import mffs.base.TileEntityBase;
import mffs.base.TileEntityModuleAcceptor;
import mffs.fortron.FrequencyGrid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector3;

import java.io.IOException;
import java.util.*;

public class TileEntityFortronCapacitor extends TileEntityModuleAcceptor implements IFortronStorage, IFortronCapacitor {

	private TransferMode transferMode;

	public TileEntityFortronCapacitor() {
		this.transferMode = TransferMode.EQUALIZE;
		super.capacityBase = 700;
		super.capacityBoost = 10;
		super.startModuleIndex = 2;
	}

	public void updateEntity() {
		super.updateEntity();
		this.consumeCost();
		if (!this.isDisabled() && this.isActive() && super.ticks % 10L == 0L) {
			Set machines = new HashSet();
			Iterator i$ = this.getCards().iterator();

			while (i$.hasNext()) {
				ItemStack itemStack = (ItemStack) i$.next();
				if (itemStack != null) {
					if (itemStack.getItem() instanceof ICardInfinite) {
						this.setFortronEnergy(this.getFortronCapacity());
					} else if (itemStack.getItem() instanceof ICardLink) {
						Vector3 linkPosition = ((ICardLink) itemStack.getItem()).getLink(itemStack);
						if (linkPosition != null && linkPosition.getTileEntity(super.worldObj) instanceof IFortronFrequency) {
							((Set) machines).add(this);
							((Set) machines).add((IFortronFrequency) linkPosition.getTileEntity(super.worldObj));
						}
					}
				}
			}

			if (((Set) machines).size() < 1) {
				machines = this.getLinkedDevices();
			}

			MFFSHelper.transferFortron(this, (Set) machines, this.transferMode, this.getTransmissionRate());
		}

	}

	public float getAmplifier() {
		return 0.001F;
	}

	public List getPacketUpdate() {
		List objects = new LinkedList();
		objects.addAll(super.getPacketUpdate());
		objects.add(this.transferMode.ordinal());
		return objects;
	}

	public void onReceivePacket(int packetID, ByteArrayDataInput dataStream) throws IOException {
		super.onReceivePacket(packetID, dataStream);
		if (packetID == TileEntityBase.TilePacketType.DESCRIPTION.ordinal()) {
			this.transferMode = TransferMode.values()[dataStream.readInt()];
		} else if (packetID == TileEntityBase.TilePacketType.TOGGLE_MODE.ordinal()) {
			this.transferMode = this.transferMode.toggle();
		}

	}

	public int getSizeInventory() {
		return 5;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.transferMode = TransferMode.values()[nbt.getInteger("transferMode")];
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("transferMode", this.transferMode.ordinal());
	}

	public Set getLinkedDevices() {
		Set fortronBlocks = new HashSet();
		Set frequencyBlocks = FrequencyGrid.instance().get(super.worldObj, new Vector3(this), this.getTransmissionRange(), this.getFrequency());
		Iterator i$ = frequencyBlocks.iterator();

		while (i$.hasNext()) {
			IBlockFrequency frequencyBlock = (IBlockFrequency) i$.next();
			if (frequencyBlock instanceof IFortronFrequency) {
				fortronBlocks.add((IFortronFrequency) frequencyBlock);
			}
		}

		return fortronBlocks;
	}

	public boolean isStackValidForSlot(int slotID, ItemStack itemStack) {
		return slotID != 0 && slotID != 1 ? itemStack.getItem() instanceof IModule : itemStack.getItem() instanceof ICard;
	}

	public Set getCards() {
		Set cards = new HashSet();
		cards.add(super.getCard());
		cards.add(this.getStackInSlot(1));
		return cards;
	}

	public TransferMode getTransferMode() {
		return this.transferMode;
	}

	public int getTransmissionRange() {
		return 15 + this.getModuleCount(ModularForceFieldSystem.itemModuleScale, new int[0]);
	}

	public int getTransmissionRate() {
		return 250 + 50 * this.getModuleCount(ModularForceFieldSystem.itemModuleSpeed, new int[0]);
	}
}
