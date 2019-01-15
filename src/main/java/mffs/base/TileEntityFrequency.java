package mffs.base;

import com.google.common.io.ByteArrayDataInput;
import icbm.api.IBlockFrequency;
import mffs.api.IBiometricIdentifierLink;
import mffs.api.card.ICardLink;
import mffs.api.security.IBiometricIdentifier;
import mffs.fortron.FrequencyGrid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import universalelectricity.core.vector.Vector3;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class TileEntityFrequency extends TileEntityInventory implements IBlockFrequency, IBiometricIdentifierLink {

	private int frequency;

	public void initiate() {
		FrequencyGrid.instance().register(this);
		super.initiate();
	}

	public void invalidate() {
		FrequencyGrid.instance().unregister(this);
		super.invalidate();
	}

	public void onReceivePacket(int packetID, ByteArrayDataInput dataStream) throws IOException {
		super.onReceivePacket(packetID, dataStream);
		if (packetID == TileEntityBase.TilePacketType.FREQUENCY.ordinal()) {
			this.setFrequency(dataStream.readInt());
		}

	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.setFrequency(nbt.getInteger("frequency"));
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("frequency", this.getFrequency());
	}

	public int getFrequency() {
		return this.frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public IBiometricIdentifier getBiometricIdentifier() {
		return this.getBiometricIdentifiers().size() > 0 ? (IBiometricIdentifier) this.getBiometricIdentifiers().toArray()[0] : null;
	}

	public Set getBiometricIdentifiers() {
		Set list = new HashSet();
		Iterator i$ = this.getCards().iterator();

		while (i$.hasNext()) {
			ItemStack itemStack = (ItemStack) i$.next();
			if (itemStack != null && itemStack.getItem() instanceof ICardLink) {
				Vector3 linkedPosition = ((ICardLink) itemStack.getItem()).getLink(itemStack);
				TileEntity tileEntity = linkedPosition.getTileEntity(super.worldObj);
				if (linkedPosition != null && tileEntity instanceof IBiometricIdentifier) {
					list.add((IBiometricIdentifier) tileEntity);
				}
			}
		}

		i$ = FrequencyGrid.instance().get(this.getFrequency()).iterator();

		while (i$.hasNext()) {
			IBlockFrequency tileEntity = (IBlockFrequency) i$.next();
			if (tileEntity instanceof IBiometricIdentifier) {
				list.add((IBiometricIdentifier) tileEntity);
			}
		}

		return list;
	}
}
